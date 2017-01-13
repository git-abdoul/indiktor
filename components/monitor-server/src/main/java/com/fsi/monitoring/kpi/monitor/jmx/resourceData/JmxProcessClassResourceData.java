package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.lang.management.ClassLoadingMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class JmxProcessClassResourceData extends IkrResourceData {
	
	private ClassLoadingMXBean classLoadingMXBean;
	private String processName;	
	
	public JmxProcessClassResourceData(ClassLoadingMXBean classLoadingMXBean,
									String processName,
			   				  		Date captureTime) {
		super(captureTime);
		this.classLoadingMXBean = classLoadingMXBean;
		this.processName = processName;
	}
	
	public Map<String, String> getCurrentClassesLoaded() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(classLoadingMXBean.getLoadedClassCount()));
		return values;
	}
	
	public Map<String, String> getTotalClassesLoaded() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(classLoadingMXBean.getTotalLoadedClassCount()));
		return values;
	}
	
	public Map<String, String> getTotalClasseUnloaded() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(classLoadingMXBean.getUnloadedClassCount()));
		return values;
	}
}
