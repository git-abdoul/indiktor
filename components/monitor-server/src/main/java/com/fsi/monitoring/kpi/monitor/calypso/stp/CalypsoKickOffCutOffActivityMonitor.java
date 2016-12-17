package com.fsi.monitoring.kpi.monitor.calypso.stp;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.calypso.tk.bo.Task;
import com.calypso.tk.bo.TaskWorkflowConfig;
import com.calypso.tk.bo.workflow.KickOffCutOffConfig;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventTask;
import com.calypso.tk.util.TaskArray;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.calypso.flow.AbstractCalypsoDataFlowMonitor;
import com.fsi.monitoring.kpi.monitor.calypso.stp.resourceData.CalypsoKickOffCutOffResourceData;

public class CalypsoKickOffCutOffActivityMonitor extends AbstractCalypsoDataFlowMonitor implements CalypsoListener{
	private static final Logger logger = Logger.getLogger(CalypsoKickOffCutOffActivityMonitor.class);
	
	private String flowName;
	private Map<Integer, String> kickOffCutOffInstances;
	private String eventClass;
	private Map<Integer, Map<Long, Task>> kickOffCutOffTasks;	
	
	private List<PSEventTask> eventBuffer;	
	private List<PSEventTask> eventsToUse;

	@Override
	protected void synchronizeData(String id, Date synchronizeDate)
			throws Exception {
		String filter = "task_datetime > " + ioSQL.datetime2String(synchronizeDate);			
		try {
			TaskArray array = calypsoConnector.getRemoteBackOffice().getTasks(filter);
			Task[] tasks = (array!=null)?array.getTasks():null;
			if (tasks==null) return;
			for (Task task : tasks) {
				if (task==null) continue;
				if (!acceptBusinessComponent(task))
					continue;
				update(task);
				Date newObjectDate = new Date(task.getDatetime().getTime());
				updateLastEvtDate(newObjectDate);
			}		
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (ConnectorException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	protected boolean acceptBusinessComponent(Object businessObject) {
		boolean accept = true;
		Task task = (Task)businessObject;
//		if (task.getDatetime().before(lastEvtDate))	accept=false;	
		if (task.getKickOffCutOffId()==0) accept=false;
		if (!eventClass.equals(task.getObjectClassName())) accept=false;
		if(task.getStatus()==Task.COMPLETED)accept=false;
        if(task.getStatus()==Task.UNDER_PROCESSING) accept=false;
        KickOffCutOffConfig config;
		try {
			config = calypsoConnector.getKickOffCutOffConfig(task.getKickOffCutOffId());
			if (config==null) accept=false;
			if (task.getKickOffDatetime()==null || task.getCutOffDatetime()==null) accept=false;
		} catch (ConnectorException e) {
			accept=false;
			logger.error(e.getMessage(), e);
		}		
		return accept;
	}
	
	private void update(Task task) throws ConnectorException {
		KickOffCutOffConfig config = calypsoConnector.getKickOffCutOffConfig(task.getKickOffCutOffId());
		String kickOffCutOffIdStr = kickOffCutOffInstances.get(config.getId());
		if (kickOffCutOffIdStr==null) {
			String currency = config.getCurrency();
			String methodName = config.getMethod();
			int wfwId = config.getWorkflowId();
			int recId = config.getReceiverId();
			try {
				TaskWorkflowConfig wfwCfg = calypsoConnector.getRemoteBackOffice().getTaskWorkflowConfig(wfwId);
				if (wfwCfg!=null) {
					String wfw = wfwCfg.getStatus().getStatus()+"/"+wfwCfg.getResultingStatus().getStatus();
					String recName = calypsoConnector.getEntityName(recId);
					if (recName==null)
						 recName = "NONE";
					kickOffCutOffIdStr = wfw+","+recName+","+methodName+","+currency;
					kickOffCutOffInstances.put(config.getId(),kickOffCutOffIdStr);
				}
			} catch (RemoteException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		if (kickOffCutOffIdStr!=null) {
			Map<Long, Task> tasks = kickOffCutOffTasks.get(config.getId());
			if (tasks==null) {
				tasks = new HashMap<Long, Task>();
				kickOffCutOffTasks.put(config.getId(), tasks);
			}
			tasks.put(task.getObjectId(), task);
		}
	}
	
	public void onEventReceived(PSEvent event) {
		statsLock.lock();
		try {
			eventBuffer.add((PSEventTask)event);
		} finally {
			statsLock.unlock();
		}		
	}
	
	@Override
	protected void preStart() {
		kickOffCutOffTasks = new HashMap<Integer, Map<Long,Task>>();
		kickOffCutOffInstances = new HashMap<Integer, String>();
		eventBuffer = new ArrayList<PSEventTask>();
		super.preStart();
		List<String> events = new ArrayList<String>();
		events.add(PSEventTask.class.getName());				
		calypsoConnector.subscribeToCalypsoEvents(this, events);
	}

	@Override
	protected void preFetchs() throws Exception {
		eventsToUse = new ArrayList<PSEventTask>();
		statsLock.lock();
		try {			
			eventsToUse.addAll(eventBuffer);				
			eventBuffer.clear();
		} finally {
			statsLock.unlock();
		}
		super.preFetchs();
		
	}

	public CalypsoKickOffCutOffResourceData fetchKICKOFF_CUTTOFF() throws ConnectorException {
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(PSEventTask.class.getName()));		
		Iterator<PSEventTask> iter = eventsToUse.iterator();		
		while (iter.hasNext()) {	
			PSEventTask taskEvent = iter.next();
			Task task =  taskEvent.getTask();
			
			if (!acceptBusinessComponent(task)){
				Map<Long, Task> tasks = kickOffCutOffTasks.get(task.getKickOffCutOffId());
				if (tasks!=null) {
					if (tasks.containsKey(task.getObjectId()))
						tasks.remove(task.getObjectId());
				}
				continue;
			}
						
			Date newObjectDate = new Date(task.getDatetime().getTime());
			String newObjectId = String.valueOf(task.getId());
			if (lastObjectDate != null) {					
				if (newObjectDate.after(lastObjectDate)) {
					lastObjectDate = newObjectDate;
					lastObjectId = newObjectId;
				}
			}
			else {
				lastObjectDate = newObjectDate;
				lastObjectId = newObjectId;
			}
			
			update(task);
		}		
		
		long now = (new Date()).getTime();
		Map<String, List<String>> kickOffDts = new HashMap<String, List<String>>(); 
		Map<String, List<String>> cutOffDts = new HashMap<String, List<String>>(); 
		Map<String, String> valueIds = new HashMap<String, String>();
		
		for (int kickOffCutOffCfdId : kickOffCutOffTasks.keySet()) {
			String instance = flowName()+"["+kickOffCutOffInstances.get(kickOffCutOffCfdId)+"]";
			Map<Long, Task> tasks = kickOffCutOffTasks.get(kickOffCutOffCfdId);
			String ids = "";
			List<String> taskKickOffDts = new ArrayList<String>(); 
			List<String> taskCutOffDts = new ArrayList<String>();
			int i = 0;
			int sz = tasks.size();
			for (Task task : tasks.values()) {
				JDatetime taskKickOffDate = task.getKickOffDatetime();
				JDatetime taskCutOffDate = task.getCutOffDatetime();
				long kickOffDt = (taskKickOffDate.getTime() - now)/60000;
				long cutOffDt = (taskCutOffDate.getTime() - now)/60000;
				ids = ids + task.getObjectId();
				if (i<sz-1)
					ids = ids + ",";
				taskKickOffDts.add(String.valueOf(kickOffDt));
				taskCutOffDts.add(String.valueOf(cutOffDt));
			}
			kickOffDts.put(instance, taskKickOffDts);
			cutOffDts.put(instance, taskCutOffDts);
			valueIds.put(instance, ids);
		}
		
		return new CalypsoKickOffCutOffResourceData(kickOffDts, cutOffDts, valueIds, new Date());
	}

	@Override
	protected String getBusinessComponentValue(String componentType,
			Object businessObject) {
		return "";
	}

	@Override
	protected String flowName() {
		return flowName;
	}

	@Override
	protected void initConnection() throws Exception {
		super.initConnection();		
		flowName = monitorConfig.getAttribute("EVENT_CLASS");
		if ("Trade".equalsIgnoreCase(flowName)) {
			eventClass = "PSEventTrade";
		}
		else if ("Message".equalsIgnoreCase(flowName)) {
			eventClass = "PSEventMessage";
		}
		else if ("Transfer".equalsIgnoreCase(flowName)) {
			eventClass = "PSEventTransfer";
		}
	}

}
