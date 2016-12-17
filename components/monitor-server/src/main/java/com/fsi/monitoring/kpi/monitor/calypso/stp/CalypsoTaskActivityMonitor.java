package com.fsi.monitoring.kpi.monitor.calypso.stp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.calypso.tk.bo.Task;
import com.calypso.tk.core.Book;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventTask;
import com.calypso.tk.util.TaskArray;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.calypso.flow.AbstractCalypsoDataFlowMonitor;
import com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData.DataFlowResourceData;

public class CalypsoTaskActivityMonitor extends AbstractCalypsoDataFlowMonitor implements CalypsoListener{
	private static final Logger LOG = Logger.getLogger(CalypsoTaskActivityMonitor.class);
	
	private List<PSEventTask> eventBuffer;	
	private List<PSEventTask> eventsToUse;
	
	@Override
	protected String flowName() {
		return "Task";
	}	
	
	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		Map<String, Long> stats = new HashMap<String, Long>();
		String filter = "task_datetime > " + ioSQL.datetime2String(synchronizeDate);			
		try {
			TaskArray array = calypsoConnector.getRemoteBackOffice().getTasks(filter);
			Task[] tasks = (array!=null)?array.getTasks():null;
			if (tasks==null) return;
			for (Task task : tasks) {
				if (task==null) continue;
				if (!acceptBusinessComponent(task))
					continue;
				String instance = getInstance(task);
				updateCurrentStat(instance, stats, null);
				
				Date newObjectDate = new Date(task.getDatetime().getTime());
				updateLastEvtDate(newObjectDate);
			}		
		} catch (RemoteException e) {
			LOG.error(e);
		} catch (ConnectorException e) {
			LOG.error(e);
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
	
	public DataFlowResourceData fetchTASKS()
	throws ConnectorException {			
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(PSEventTask.class.getName()));
		Map<String, Long> stats = new HashMap<String, Long>();
		Iterator<PSEventTask> iter = eventsToUse.iterator();		
		while (iter.hasNext()) {	
			PSEventTask taskEvent = iter.next();
			Task task =  taskEvent.getTask();
			
			if (!acceptBusinessComponent(task))
				continue;
			
//			if (task.getDatetime().before(lastEvtDate))
//				continue;	
			
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
			
			String instance = getInstance(task);	
			updateCurrentStat(instance, stats, null);
		}		
		
		return new DataFlowResourceData(stats, new Date());
	}	
	
	@Override
	protected String getBusinessComponentValue(String componentType, Object businessObject) {
		String result = "";
		try {
			Object resObj = null;
			Task task = (Task)businessObject;
			if ("Status".equalsIgnoreCase(componentType)) {				
				resObj = task.getStatusAsString();
			} else if ("Priority".equalsIgnoreCase(componentType)) {
				resObj = task.getPriorityAsString();
			}  else if ("Class".equalsIgnoreCase(componentType)) {
				String evtClass = task.getEventClass();
				if (evtClass.contains("PSEvent")) {
					evtClass = evtClass.substring("PSEvent".length());
				}
				resObj = evtClass;
			}  else if ("Type".equalsIgnoreCase(componentType)) {
				resObj = task.getEventType();
			} else if ("CounterParty".equalsIgnoreCase(componentType)) {
				int id = task.getLegalEntityId();
				resObj = calypsoConnector.getEntityName(id);
			} else if ("ProcessingOrg".equalsIgnoreCase(componentType)) {
				int id = task.getBookId();
				Book book = calypsoConnector.getBook(id);
				if (book != null) {
					resObj = book.getLegalEntity().getName();
				}				
			} else {
				String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
				Method method = Task.class.getMethod(methName, null);
				resObj = method.invoke(task, null); 
			}	
			if (resObj != null) {
				if (resObj instanceof JDate || resObj instanceof JDatetime)
					result = dateToString(resObj);
				else
					result = resObj.toString();
			}
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOG.error(e.getMessage(), e);
		} catch (ConnectorException e) {
			LOG.error(e.getMessage(), e);
		}		
		
		return result;
	}
}
