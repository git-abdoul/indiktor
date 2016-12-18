package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class JmxThreadTypeStatsResourceData extends IkrResourceData {

	private ThreadMXBean threadMXBean;
	private String processName;
	
	public JmxThreadTypeStatsResourceData(ThreadMXBean threadMXBean,
										String processName, 
										Date captureTime){
		super(captureTime);
		this.threadMXBean = threadMXBean;
		this.processName = processName;
	}
	
	public Map<String, String> getLiveThreads() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(threadMXBean.getThreadCount()));
		return values;
	}
	
	public Map<String, String> getPeakThreads() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(threadMXBean.getPeakThreadCount()));
		return values;
	}
	
	public Map<String, String> getThreadStarted() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(threadMXBean.getTotalStartedThreadCount()));
		return values;
	}
	
	public Map<String, String> getDaemonThread() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(threadMXBean.getDaemonThreadCount()));
		return values;
	}
	
	public Map<String, String> getDeadlock() {
		Map<String, String> values = new HashMap<String, String>();
		long[] deadlocks = threadMXBean.findMonitorDeadlockedThreads();
		values.put(processName, String.valueOf((deadlocks != null) ? deadlocks.length : 0));
		return values;
	}
}
