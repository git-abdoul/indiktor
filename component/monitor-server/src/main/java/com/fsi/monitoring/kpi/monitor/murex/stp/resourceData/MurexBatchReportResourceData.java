package com.fsi.monitoring.kpi.monitor.murex.stp.resourceData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryResourceData;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryValue;

public class MurexBatchReportResourceData extends MurexSQLQueryResourceData {
	private static final Logger LOG = Logger.getLogger(MurexBatchReportResourceData.class);
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");	

	public MurexBatchReportResourceData(Map<String, List<MurexSQLQueryValue>> queryValues, Date captureTime) {
		super(queryValues, captureTime);
	}
	
	public Map<String, String> getStartTime() {
		Map<String, String> values = new HashMap<String, String>();
		Map<String, String> rawStartTimeValues = getMetricValues("startTime"); 
		Map<String, String> rawStartDateValues = getMetricValues("startDate"); 
		for (String instance : rawStartTimeValues.keySet()) {			
			try {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				Date startDate = cal.getTime();
				if (rawStartDateValues!=null) {
					String startDateStr = rawStartDateValues.get(instance);
					if (startDateStr!=null)
						startDate = (Date)format.parse(startDateStr);
				}
				long date = startDate.getTime();
				String startTimeStr = rawStartTimeValues.get(instance);
				long time = Long.parseLong(startTimeStr)*1000;
				long datetime = date + time;
				values.put(instance, String.valueOf(datetime));				
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}			
		}
		return values;
	}

	public Map<String, String> getEndTime() {
		Map<String, String> values = new HashMap<String, String>();
		Map<String, String> rawEndTimeValues = getMetricValues("endTime"); 
		Map<String, String> rawStartDateValues = getMetricValues("startDate"); 
		for (String instance : rawStartDateValues.keySet()) {
			String endTimeStr = rawEndTimeValues.get(instance);
			if (endTimeStr!=null && endTimeStr.length()>0) {
				try {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					Date startDate = cal.getTime();
					if (rawStartDateValues!=null) {
						String startDateStr = rawStartDateValues.get(instance);
						if (startDateStr!=null)
							startDate = (Date)format.parse(startDateStr);
					}
					long date = startDate.getTime();				
					long time = Long.parseLong(endTimeStr)*1000;
					long datetime = date + time;
					values.put(instance, String.valueOf(datetime));				
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}	
			}
		}
		return values;
	}

	public Map<String, String> getScheduledTime() {
		return null;
	}

	public Map<String, String> getUptime() {		
		Map<String, String> values = new HashMap<String, String>();
		Map<String, String> rawEndTimeValues = getMetricValues("endTime"); 
		Map<String, String> rawStartTimeValues = getMetricValues("startTime"); 
		Map<String, String> rawStartDateValues = getMetricValues("startDate"); 
		for (String instance : rawEndTimeValues.keySet()) {
			String startTimeStr = rawStartTimeValues.get(instance);
			String endTimeStr = rawEndTimeValues.get(instance);			
			if (endTimeStr!=null && endTimeStr.length()>0) {
				try {
					long endTime = Long.parseLong(endTimeStr);
					long startTime = Long.parseLong(startTimeStr);
					long uptime = (endTime - startTime)*1000;
					values.put(instance, String.valueOf(uptime));				
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}	
			}
			else {
				try {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					Date startDate = cal.getTime();
					if (rawStartDateValues!=null) {
						String startDateStr = rawStartDateValues.get(instance);
						if (startDateStr!=null)
							startDate = (Date)format.parse(startDateStr);
					}
					long date = startDate.getTime();				
					long time = Long.parseLong(startTimeStr)*1000;
					long startTime = date + time;					
					long now = (new Date()).getTime();
					long uptime = now - startTime;
					values.put(instance, String.valueOf(uptime));				
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}	
			}
		}
		return values;
	}

	public Map<String, String> getDelay() {
		return null;
	}

	public Map<String, String> getStatus() {
		return getMetricValues("status");
	}

}
