package com.fsi.monitoring.kpi.monitor.calypso.exception;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.calypso.tk.bo.Task;
import com.calypso.tk.core.Book;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventException;
import com.calypso.tk.event.PSEventTask;
import com.calypso.tk.util.TaskArray;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.calypso.CalypsoException;
import com.fsi.monitoring.kpi.monitor.calypso.exception.resourceData.ExceptionContentResourceData;
import com.fsi.monitoring.kpi.monitor.calypso.flow.AbstractCalypsoDataFlowMonitor;
import com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData.DataFlowResourceData;


public class CalypsoExceptionMonitor extends AbstractCalypsoDataFlowMonitor implements CalypsoListener {
	private static final Logger LOG = Logger.getLogger(CalypsoExceptionMonitor.class);
	
	private String flowName="SystemException";	
	private Map<String, List<String>> exceptionContents;
	
	private List<PSEventTask> eventTasks;
	private List<CalypsoException> eventExceptions;
	
	private List<PSEventTask> eventTasksToUse;
	private List<CalypsoException> eventExceptionsToUse;	
	
	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		if ("OPERATIONAL_ANOMALY".equals(getType())) {
			Map<String, Long> stats = new HashMap<String, Long>();
			flowName="OperationException";
			String filter = "task_datetime > " + ioSQL.datetime2String(synchronizeDate);			
			try {
				TaskArray array = calypsoConnector.getRemoteBackOffice().getTasks(filter);
				Task[] tasks = (array!=null)?array.getTasks():null;
				if (tasks==null) return;
				for (Task task : tasks) {
					if (task==null) continue;
					if (!"Exception".equalsIgnoreCase(task.getEventClass()))
						continue;
					if (!acceptBusinessComponent(task))
						continue;
					String instance = getInstance(task);
					updateCurrentStat(instance, stats, null);
					
					Date newObjectDate = new Date(task.getDatetime().getTime());
					updateLastEvtDate(newObjectDate);
					
					List<String> contents = exceptionContents.get(instance);
					if (contents == null)
						contents = new ArrayList<String>();
					contents.add(task.getComment());
					exceptionContents.put(instance,contents);
				}		
			} catch (RemoteException e) {
				LOG.error(e);
			} catch (ConnectorException e) {
				LOG.error(e);
			}
		}
	}	
	
	public void onEventReceived(PSEvent event) {
		statsLock.lock();
		try {
			if (event instanceof PSEventTask) {
				eventTasks.add((PSEventTask)event);
			}
			else if (event instanceof PSEventException) {
				CalypsoException exception = processException((PSEventException)event);
				eventExceptions.add(exception);
			}
		} finally {
			statsLock.unlock();
		}
	}
	
	private CalypsoException processException(PSEventException event) {
		String exceptionstr = event.getException();
		String[] tmp = StringUtils.splitPreserveAllTokens(exceptionstr, '\n');
		String appName = tmp[0];
		String content = tmp[2]+ '\n' + getExceptionContent(tmp);
		return new CalypsoException(new Date(System.currentTimeMillis()), appName, content);
	}
	
	private String getExceptionContent(String[] exceptions) {
		int i = 0;
		while(exceptions[i].indexOf("END") < 0) 
			i++;
		return exceptions[++i] + '\n' + exceptions[++i];
	}	

	@Override
	protected void preFetchs() throws Exception {
		eventTasksToUse = new ArrayList<PSEventTask>();
		eventExceptionsToUse = new ArrayList<CalypsoException>();
		statsLock.lock();
		try {			
			eventTasksToUse.addAll(eventTasks);			
			eventExceptionsToUse.addAll(eventExceptions);
			
			eventTasks.clear();
			eventExceptions.clear();
		} finally {
			statsLock.unlock();
		}
		super.preFetchs();
	}

	@Override
	protected void preStart() {
		exceptionContents = new HashMap<String, List<String>>();
		eventTasks = new ArrayList<PSEventTask>();
		eventExceptions = new ArrayList<CalypsoException>();
		super.preStart();
		List<String> events = new ArrayList<String>();
		events.add(PSEventTask.class.getName());
		events.add(PSEventException.class.getName());
		calypsoConnector.subscribeToCalypsoEvents(this, events);
	}
	
	public DataFlowResourceData fetchOPERATION_EXCEPTION_COUNT() 
	throws ConnectorException, FetchException {
		flowName="OperationException";
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(PSEventTask.class.getName()));		
		Map<String, Long> stats = new HashMap<String, Long>();		
		Iterator<PSEventTask> iter = eventTasksToUse.iterator();		
		while (iter.hasNext()) {	
			PSEventTask taskEvent = iter.next();
			Task task =  taskEvent.getTask();
			
			if (!"Exception".equalsIgnoreCase(task.getEventClass()))
				continue;
			
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
	
	public ExceptionContentResourceData fetchOPERATION_EXCEPTION_CONTENT() 
	throws ConnectorException, FetchException {
		flowName="OperationException";
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(PSEventTask.class.getName()));
		Map<String, List<String>> contents = new HashMap<String, List<String>>();
		if (exceptionContents!=null && exceptionContents.size()>0)
			contents.putAll(exceptionContents);		
		Iterator<PSEventTask> iter = eventTasksToUse.iterator();		
		while (iter.hasNext()) {	
			PSEventTask taskEvent = iter.next();
			Task task =  taskEvent.getTask();
			
			if (!"Exception".equalsIgnoreCase(task.getEventClass()))
				continue;
			
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
			List<String> tmps = contents.get(instance);
			if (tmps == null)
				tmps = new ArrayList<String>();
			tmps.add(task.getComment());
			contents.put(instance,tmps);			
		}
		
		return new ExceptionContentResourceData(contents, new Date());
	}
	
	public DataFlowResourceData fetchEXCEPTION_COUNT() 
	throws ConnectorException, FetchException {		
		flowName="SystemException";
//		List<CalypsoException> exceptions = Collections.synchronizedList(calypsoConnector.getEventListener().getCalypsoExceptions());
		Map<String, Long> stats = new HashMap<String, Long>();		
		Iterator<CalypsoException> iter = eventExceptionsToUse.iterator();		
		while (iter.hasNext()) {	
			CalypsoException exception = iter.next();			
			if (!acceptBusinessComponent(exception))
				continue;
//			if (exception.getDate().before(lastEvtDate))
//				continue;			
			String instance = getInstance(exception);	
			Long size = stats.get(instance);
			if (size == null)
				size = new Long(1);
			else
				size = size + 1;
			stats.put(instance, size);
		}		
		
		return new DataFlowResourceData(stats, new Date());
	}	
	
	public ExceptionContentResourceData fetchEXCEPTION_CONTENT() 
	throws ConnectorException, FetchException {		
		flowName="SystemException";
//		List<CalypsoException> exceptions = Collections.synchronizedList(calypsoConnector.getEventListener().getCalypsoExceptions());
		Map<String, List<String>> contents = new HashMap<String, List<String>>();
		Iterator<CalypsoException> iter = eventExceptionsToUse.iterator();	
		while (iter.hasNext()) {	
			CalypsoException exception = iter.next();			
			if (!acceptBusinessComponent(exception))
				continue;
//			if (exception.getDate().before(lastEvtDate))
//				continue;			
			String instance = getInstance(exception);
			List<String> tmps = contents.get(instance);
			if (tmps == null)
				tmps = new ArrayList<String>();
			tmps.add(exception.getContent());
			contents.put(instance,tmps);
		}
		return new ExceptionContentResourceData(contents, new Date());
	}

	@Override
	protected String getBusinessComponentValue(String componentType, Object businessObject) {
		String result = "";		
		if ("OPERATIONAL_ANOMALY".equals(getType())) 
			result = getOpExceptionComponentValue(componentType, (Task)businessObject);
		else
			result = getSysExceptionComponentValue(componentType, (CalypsoException)businessObject);
		return result;
	}
	
	private String getOpExceptionComponentValue(String componentType, Task task) {
		String result = "";
		try {
			Object resObj = null;
			if ("Class".equalsIgnoreCase(componentType)) {
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
			}  else {
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
	
	private String getSysExceptionComponentValue(String componentType, CalypsoException exc) {
		String result = "";
		try {
			Object resObj = null;
			String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
			Method method = CalypsoException.class.getMethod(methName, null);
			resObj = method.invoke(exc, null); 
			
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
		} 	
		
		return result;
	}

	@Override
	protected String flowName() {
		return flowName;
	}
	
}
