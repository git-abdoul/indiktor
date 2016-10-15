package com.fsi.monitoring.realTime.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.fsi.monitoring.RealTimeValue;
import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.IkrEventLog;
import com.fsi.monitoring.admin.console.bean.IkrEventLogBean;


public class AdminEventListener extends RealtimeValueListener {	
	private static final Logger logger = Logger.getLogger(AdminEventListener.class);	
	public final ReentrantLock lock = new ReentrantLock();
	
	private Map<Long, IkrEventLogBean> connectorEventLogBeans;
	private Map<Long, IkrEventLogBean> collectorEventLogBeans;
	private Map<Long, IkrEventLogBean> JobSchedulerEventLogBeans;
	
	public AdminEventListener() {
		connectorEventLogBeans = new HashMap<Long, IkrEventLogBean>();
		collectorEventLogBeans = new HashMap<Long, IkrEventLogBean>();
		JobSchedulerEventLogBeans = new HashMap<Long, IkrEventLogBean>();
	}
	
	public void removeComponentLog(long componentId, AdminComponent componentType) {
		lock.lock();
		try {
			switch (componentType) {
				case CONNECTOR:
					connectorEventLogBeans.remove(componentId);
					break;
					
				case COLLECTOR:
					collectorEventLogBeans.remove(componentId);
					break;
					
				case JOB_TASK:
					JobSchedulerEventLogBeans.remove(componentId);
					break;
		
				default:
					break;
			}			
		} finally {
			lock.unlock();
		}		
		
	}

	@Override
	protected void notifRealTimeSubscribers(Collection<? extends RealTimeValue> realtimeValues) {
		logger.debug("Received new Event Log ....");
		lock.lock();
		try {
			for (RealTimeValue rtValue : realtimeValues) { 
				IkrEventLog eventLog = (IkrEventLog)rtValue;
				switch (eventLog.getComponentType()) {
					case CONNECTOR:
						IkrEventLogBean connectorBean = connectorEventLogBeans.get(eventLog.getValueDefinitionId());
						if (connectorBean==null)
							connectorBean =  new IkrEventLogBean();
						connectorBean.setEventLog(eventLog);
						connectorEventLogBeans.put(eventLog.getValueDefinitionId(), connectorBean);
						break;
						
					case COLLECTOR:
						IkrEventLogBean collectorBean = collectorEventLogBeans.get(eventLog.getValueDefinitionId());
						if (collectorBean==null)
							collectorBean =  new IkrEventLogBean();
						collectorBean.setEventLog(eventLog);
						collectorEventLogBeans.put(eventLog.getValueDefinitionId(), collectorBean);
						break;
						
					case JOB_TASK:
						IkrEventLogBean jobTaskbean = JobSchedulerEventLogBeans.get(eventLog.getValueDefinitionId());
						if (jobTaskbean==null)
							jobTaskbean =  new IkrEventLogBean();
						jobTaskbean.setEventLog(eventLog);
						JobSchedulerEventLogBeans.put(eventLog.getValueDefinitionId(), jobTaskbean);
						break;
			
					default:
						break;
				}			
				
			}
		} finally {
			lock.unlock();
		}	
	}

	@Override
	protected void initRealTimeValues() {}

	public Collection<IkrEventLogBean> getEventLogBeans(AdminComponent componentType) {
		Collection<IkrEventLogBean> eventLogBeans = new ArrayList<IkrEventLogBean>();
		switch (componentType) {
			case CONNECTOR:
				eventLogBeans = connectorEventLogBeans.values();
				break;
				
			case COLLECTOR:
				eventLogBeans = collectorEventLogBeans.values();
				break;
				
			case JOB_TASK:
				eventLogBeans = JobSchedulerEventLogBeans.values();
				break;
	
			default:
				break;
		}			
		return eventLogBeans;
	}
	
	public void resetEventLogBeans(AdminComponent componentType) {
		switch (componentType) {
		case CONNECTOR:
			connectorEventLogBeans.clear();
			break;
			
		case COLLECTOR:
			collectorEventLogBeans.clear();
			break;
			
		case JOB_TASK:
			JobSchedulerEventLogBeans.clear();
			break;

		default:
			break;
	}			
	}
}
