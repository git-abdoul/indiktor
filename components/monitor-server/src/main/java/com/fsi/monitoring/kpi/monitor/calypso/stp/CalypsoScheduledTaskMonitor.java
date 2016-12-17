package com.fsi.monitoring.kpi.monitor.calypso.stp;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.calypso.tk.core.DateRule;
import com.calypso.tk.core.Holiday;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.LegalEntity;
import com.calypso.tk.util.ScheduledTask;
import com.calypso.tk.util.ScheduledTaskExec;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoConnector;
import com.fsi.monitoring.kpi.monitor.BusinessMonitorTask;
import com.fsi.monitoring.kpi.monitor.calypso.stp.resourceData.CalypsoScheduledTaskResourceData;
import com.fsi.monitoring.kpi.monitor.calypso.stp.resourceData.ScheduledTaskInfo;


public class CalypsoScheduledTaskMonitor extends BusinessMonitorTask {
	private static final Logger LOG = Logger.getLogger(CalypsoScheduledTaskMonitor.class);
	
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_FAILED = "FAILED";
	public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
	public static final String STATUS_NOT_STARTED = "NOT_STARTED";
	public static final String STATUS_DELAYED = "DELAYED";
	
	public static final int TREND_UPWARD = 1;
	public static final int TREND_STABLE = 0;
	public static final int TREND_DOWNWARD = -1;
	
	private Map<String, ScheduledTaskInfo> taskInfos = new HashMap<String, ScheduledTaskInfo>();
	private Map<String, ScheduledTaskExec> taskExecs = new HashMap<String, ScheduledTaskExec>();
	
	private CalypsoConnector calypsoConnector;
	
	@Override
	protected void initConnector() {
		calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);
		
	}

	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		// NOTHING TO DO	
	}
	
	@Override
	protected Date getMonitoredEnvCurrentTime() {
		return calypsoConnector.getDsCurrentTime();
	}
	
	@Override
	protected void preFetchs() throws Exception {}
	
	public CalypsoScheduledTaskResourceData fetchBATCH()
	throws ConnectorException {		
		JDate today = JDate.getNow();		
		Date now = getMonitoredEnvCurrentTime();
		if (now==null)
			now = new Date();
		
		filterTasks(calypsoConnector.getScheduledTasks(), calypsoConnector);
		
		Vector<ScheduledTaskExec> execs = calypsoConnector.getScheduledTaskExecs();
		if (execs == null) 
			execs = new Vector<ScheduledTaskExec>();
		
		for(ScheduledTaskExec exec : execs) { 
			String poId = (exec.getProcessingOrg() != null) ? exec.getProcessingOrg().getLegalEntityId() + "" : "ALL";				
			String key = exec.getType() + ":" + poId + "@" + calypsoConnector.getConnectorContext();
			taskExecs.put(key, exec);
		}
		
		List<ScheduledTaskInfo> batchs = new ArrayList<ScheduledTaskInfo>();
		
		Iterator<String> iter = taskInfos.keySet().iterator();
		while(iter.hasNext()) {
			String todayKey = iter.next();
			String keys[] = todayKey.split("]");
			String taskKey = keys[0];
			JDate taskDate = JDate.valueOf(keys[1]);			
			if (!today.equals(taskDate)) {
				iter.remove();
				continue;
			}
			
			ScheduledTaskInfo taskInfo = taskInfos.get(todayKey);									
			ScheduledTaskExec exec = taskExecs.get(taskKey);
			JDatetime startTime = (exec != null) ? exec.getExecTime() : null;
			JDatetime endTime = (exec != null) ? exec.getEndTime() : null;			
			long uptime = 0;
			String status = STATUS_NOT_STARTED;
			if (startTime != null) {
				long dt = (endTime != null) ? endTime.getTime() : now.getTime();
				uptime = dt - startTime.getTime();
				 if ( exec.getStatus() == null)
					 status = STATUS_IN_PROGRESS;
				 else if (exec.getStatus())
					 status = STATUS_SUCCESS;
				 else
					 status = STATUS_FAILED;				
			} 
			
			long delay = 0;
			if(uptime == 0) {
				long dt = now.getTime() - taskInfo.getScheduledTime();
				delay = (dt > 0) ? dt : 0;
				if (dt > 0)
					status = STATUS_DELAYED;
			}
			
			if (endTime != null) {
				if (endTime.getTime() > taskInfo.getScheduledTime() || now.getTime() > taskInfo.getScheduledTime()) {
					Object[] schedInfo = getSchedulingInfo(taskInfo.getTask());
					taskInfo.setScheduledTime((JDatetime)schedInfo[1]);
				}
			}
			
			taskInfo.setStartTime(startTime);
			taskInfo.setEndTime(endTime);
			taskInfo.setUptime(uptime);
			taskInfo.setDelay(delay);
			taskInfo.setStatus(status);
			
			batchs.add(taskInfo);
		}
		return new CalypsoScheduledTaskResourceData(batchs, new Date());
	}
	
	@Override
	protected void postFetchs() throws Exception {}	
	
	private void filterTasks(Vector<ScheduledTask> tasks, CalypsoConnector calypsoConnector) {
		if (tasks == null)
			return;
		JDatetime now = new JDatetime();
		now = new JDatetime(now.getTime() + 1);
		JDate today = JDate.getNow();
		for(ScheduledTask task : tasks) {
			if (!acceptBusinessComponent(task))
				continue;
			
			String poId = (task.getProcessingOrg() != null) ? task.getProcessingOrg().getLegalEntityId() + "" : "ALL";				
			String key = task.getType() + ":" + poId + "@" + calypsoConnector.getConnectorContext()  + "]" + today;
			String name = getInstance(task);
			if (!taskInfos.containsKey(key)) {	
				Object[] schedInfo = getSchedulingInfo(task);				
				JDate nextDate = (JDate)schedInfo[0];		
				JDatetime nextDatetime = (JDatetime)schedInfo[1];
				JDate lastDate = (JDate)schedInfo[2];
				JDatetime lastDatetime = (JDatetime)schedInfo[3];				
				ScheduledTaskInfo taskInfo = null;
				if (nextDate.equals(today)) {
					taskInfo = new ScheduledTaskInfo();
					taskInfo.setScheduledTime(nextDatetime);
				} else if (nextDate.gte(today) && lastDate.equals(today)) {
					taskInfo = new ScheduledTaskInfo();
					taskInfo.setScheduledTime(lastDatetime);
				}
				
				if (taskInfo != null) {
					taskInfo.setName(name);
					taskInfo.setTask(task);
					taskInfos.put(key, taskInfo);
				}
			} 			
		}
	}
	
	private Object[] getSchedulingInfo(ScheduledTask task) {
		JDatetime now = new JDatetime();
		now = new JDatetime(now.getTime() + 1);
		JDate today = JDate.getNow();
		JDatetime nextDatetime = null;
		JDate nextDate = null;
		JDate td = today.addDays(-1); 
		
		DateRule dtRule = task.getDateRule();
		if (dtRule != null)
			td = dtRule.previous(today);
		
		JDatetime lastDatetime = task.calculateDatetime(td);	
		JDate lastDate = task.calculateDate(td);
		JDatetime nxtdt = lastDatetime;
		JDate nxtd = lastDate;
		int i = 0;
		while (nxtdt.lte(now) || isHoliday(task, nxtd)) {
			if (i > 0 && !isHoliday(task, nxtd)) {
				lastDatetime = nxtdt;
				lastDate = nxtd;
			}
			td = td.addDays(1);
			nxtdt = task.calculateDatetime(td);					
			nxtd = task.calculateDate(td);
			i++;
		}
		nextDatetime = nxtdt;	
		nextDate = nxtd;
		return new Object[] {nextDate, nextDatetime, lastDate, lastDatetime};
	}
	
	private boolean isHoliday(ScheduledTask task, JDate date) {
		if (task.getExecuteOnHolidays() == false && !Holiday.getCurrent().isBusinessDay(date, task.getHolidays())) {
			if (task.getDateRule() != null) {
				return false;			
			}
			return true;
		}
		return false;
	}	

	@Override
	protected String getBusinessComponentValue(String componentType, Object businessObject) {
		String result = "";
		try {
			Object resObj = null;
			ScheduledTask task = (ScheduledTask)businessObject;
			if ("Type".equalsIgnoreCase(componentType)) {
				resObj = task.getType();
			} else if ("ProcessingOrg".equalsIgnoreCase(componentType)) {
				LegalEntity po = task.getProcessingOrg();
				if (po != null) {
					resObj = po.getName();
				}				
			} else {
				String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
				Method method = ScheduledTask.class.getMethod(methName, null);
				resObj = method.invoke(task, null); 
			}	
			if (resObj != null) {				
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
		return "BATCH";
	}	
}
