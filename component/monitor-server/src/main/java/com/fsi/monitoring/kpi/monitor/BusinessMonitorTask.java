package com.fsi.monitoring.kpi.monitor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.config.RealTimeBackBeanName;
import com.fsi.monitoring.config.RealTimeBackContext;
import com.fsi.monitoring.ikr.model.DataFrequency;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;


public abstract class BusinessMonitorTask extends MonitorTask implements Monitor {
	private static final Logger LOG = Logger.getLogger(BusinessMonitorTask.class);	
	
	protected Map<String, Long> globalStats;	
	
	private boolean synchronizeDataNeeded = false;
	private boolean realtimeData = false;
	private String dataFrequency = "NONE";
	
	private DataSynchronizationMgr dataSynchronizationMgr;
	
	protected Date lastEvtDate;
	
	private Date syncDate;
	private Date reInitStatDate;
	
	protected String lastObjectId;
	protected Date lastObjectDate;
	
	@Override
	protected void preStart() {		
		initConnector();
		updateStatCalendar();
		globalStats = new HashMap<String, Long>();	
		
		dataSynchronizationMgr = (DataSynchronizationMgr)RealTimeBackContext.getBean(RealTimeBackBeanName.dataSynchronizationMgr);	
		DataSynchronization dataSynchronization = dataSynchronizationMgr.getDataSynchronization(String.valueOf(getId()));
		if (dataSynchronization!=null) {
			globalStats = dataSynchronization.getStats();
		}
		
		if (realtimeData) {			
			Date synCDateUsed = null;
			if (dataSynchronization!=null) {
				lastEvtDate = dataSynchronization.getLastEvtDate();			
				synCDateUsed = lastEvtDate;
				if (syncDate!=null&&lastEvtDate.before(syncDate)) {
					lastEvtDate = syncDate;
					synCDateUsed = syncDate;
				}
			}
			else {
				lastEvtDate = getMonitoredEnvCurrentTime();
				if (lastEvtDate==null)
					lastEvtDate = getStartTime();
				synCDateUsed = lastEvtDate;
				if (syncDate!=null){
					lastEvtDate = syncDate;
					synCDateUsed = syncDate;
				}
			}
			
			if (synchronizeDataNeeded) {
				try {
					String syncId = null;
					if (dataSynchronization!=null)
						syncId = dataSynchronization.getId();
					synchronizeData(syncId, synCDateUsed);
				} catch (Exception e) {
					LOG.error("Error while synchronizing Data", e);
				}
			}
		}
		else {
			lastEvtDate = syncDate;
			if (synchronizeDataNeeded) {				
				try {
					synchronizeData(null, syncDate);
				} catch (Exception e) {
					LOG.error("Error while synchronizing Data", e);
				}
			}
		}
	}
	
	protected void updateLastEvtDate(Date newObjectDate) {
		if (lastEvtDate != null) {					
			if (newObjectDate.after(lastEvtDate)) {
				lastEvtDate = newObjectDate;
			}
		}
		else {
			lastEvtDate = newObjectDate;
		}					
	}
	
	protected Map<String, Long> updateCurrentStat(String instance, Map<String, Long> currentStats, Long valueToUseInstead) {
		Long size = null;
		if (valueToUseInstead!=null)
			size = valueToUseInstead;
		else {
			size = globalStats.get(instance);		
			if (size == null)
				size = new Long(1);
			else
				size = size + 1;
		}
		currentStats.put(instance, size);
		globalStats.put(instance, size);
		return currentStats;
	}
	
	private void updateStatCalendar() {		
		Calendar calSync = Calendar.getInstance();
		Calendar calReInitStat = Calendar.getInstance();
		if (DataFrequency.DAILY.name().equals(dataFrequency)) {
			calSync.set(Calendar.HOUR_OF_DAY, 0);
			calSync.set(Calendar.MINUTE, 0);
			calSync.set(Calendar.SECOND, 0);
			calSync.set(Calendar.MILLISECOND, 0);
			calReInitStat.setTime(calSync.getTime());
			calReInitStat.set(Calendar.HOUR_OF_DAY, 23);
			calReInitStat.set(Calendar.MINUTE, 59);
			calReInitStat.set(Calendar.SECOND, 59);
			calReInitStat.set(Calendar.MILLISECOND, 999);
		}
		else if (DataFrequency.WEEKLY.name().equals(dataFrequency)) {
			calSync.add(Calendar.WEEK_OF_YEAR, -1);
			calSync.set(Calendar.DAY_OF_WEEK, calSync.getFirstDayOfWeek());
			calSync.set(Calendar.HOUR_OF_DAY, 0);
			calSync.set(Calendar.MINUTE, 0);
			calSync.set(Calendar.SECOND, 0);
			calSync.set(Calendar.MILLISECOND, 0);
			calReInitStat.setTime(calSync.getTime());
			calReInitStat.add(Calendar.DATE, 6);
			calReInitStat.set(Calendar.HOUR_OF_DAY, 23);
			calReInitStat.set(Calendar.MINUTE, 59);
			calReInitStat.set(Calendar.SECOND, 59);
			calReInitStat.set(Calendar.MILLISECOND, 999);
		}
		else if (DataFrequency.MONTHLY.name().equals(dataFrequency)) {
			calSync.add(Calendar.MONTH, -1);
			calSync.set(Calendar.DAY_OF_MONTH, 1);
			calSync.set(Calendar.HOUR_OF_DAY, 0);
			calSync.set(Calendar.MINUTE, 0);
			calSync.set(Calendar.SECOND, 0);
			calSync.set(Calendar.MILLISECOND, 0);
			calReInitStat.setTime(calSync.getTime());
			calReInitStat.add(Calendar.MONTH, 1);
			calReInitStat.add(Calendar.DATE, -1);
			calReInitStat.set(Calendar.HOUR_OF_DAY, 23);
			calReInitStat.set(Calendar.MINUTE, 59);
			calReInitStat.set(Calendar.SECOND, 59);
			calReInitStat.set(Calendar.MILLISECOND, 999);
		}
		else {
			syncDate = null;
			reInitStatDate = null;
		}
		
		if (!DataFrequency.NONE.name().equals(dataFrequency)) {
			syncDate = calSync.getTime();
			reInitStatDate = calReInitStat.getTime();
		}
	}
	
	@Override
	protected void preFetchs() throws Exception {
		if (realtimeData) {
			Date now = new Date();
			if (reInitStatDate!=null && now.after(reInitStatDate)) {
				globalStats = new HashMap<String, Long>();
				updateStatCalendar();
			}
		}
	}

	@Override
	protected void postFetchs() throws Exception {
		if (lastObjectId!=null && lastObjectDate!=null) {
			lastEvtDate = lastObjectDate;
			dataSynchronizationMgr.writeDataSynchronization(String.valueOf(getId()), new DataSynchronization(lastObjectId, lastObjectDate,globalStats));
		}
	}

	protected boolean acceptBusinessComponent(Object businessObject) {
		boolean ret = false;		
		Map<String, String> filters = getBusinessFilterSet();
		if (filters.size() == 0)
			ret = true;
		else {
			for (String key : filters.keySet()) {
				String value = getBusinessComponentValue(key, businessObject);
				ret = accept(value);
			}
		}
		return ret;
	}	
	
	protected String getInstance(Object object) {
		String instance = flowName();
		boolean prb = false;
		if (views!=null && views.length>0) {
			int i = 1;
			instance = instance + "[";
			for (String view : views) {
				String val = getBusinessComponentValue(view, object);
				if (val!=null&&val.length()>0) {
					instance = instance + val;
					if (i<views.length)
						instance = instance + ",";
					i++;
				}
				else {
					instance = flowName();
					prb = true;
					break;
				}
			}	
			
			if (!prb)
				instance = instance + "]";
		}
		return instance;
	}
	
	protected abstract void initConnector();
	protected abstract void synchronizeData(String id, Date synchronizeDate) throws Exception;
	protected abstract String getBusinessComponentValue(String componentType, Object businessObject);
	protected abstract String flowName();
	protected abstract Date getMonitoredEnvCurrentTime();
	
	@Override
	protected void initConnection() throws Exception {
		String attr = monitorConfig.getAttribute("METRIC_VIEW");
		if (attr != null && attr.length()>0) {
			views = attr.split(":");
		}
		
		if (IkrMonitorSchedulerConfig.ONE_SHOT.equals(schedulerConfig.getType())) {
			realtimeData = false;
		}
		else {
			realtimeData = true;
		}
		
		
		String realtimeDataStr = monitorConfig.getAttribute("REALTIME_DATA");
		if (realtimeDataStr != null && realtimeDataStr.length()>0) {
			realtimeData = Boolean.parseBoolean(realtimeDataStr);
		}
		
		String synchonizationNeededStr = monitorConfig.getAttribute("DATA_SYNCHRONIZATION");
		if (synchonizationNeededStr != null && synchonizationNeededStr.length()>0) {
			synchronizeDataNeeded = Boolean.parseBoolean(synchonizationNeededStr);
		}
		
		dataFrequency = monitorConfig.getAttribute("STAT_FREQUENCY");
		if (dataFrequency==null)
			dataFrequency = "NONE";
	}
	
	protected void rollbackConnection(Connection con) throws Exception {		
		try {
			if (con != null) {
				con.rollback();
			}
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}	

	protected void closeResultSet(ResultSet rs) throws Exception {		
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}
	
	protected void closeStatement(Statement stmt) throws Exception  {
		try {
			if (stmt !=null) {
				stmt.close();
			}
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}	
	
	protected void closeConnection(Connection con) throws Exception  {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			throw new Exception(e);
		}
	}
	
}
