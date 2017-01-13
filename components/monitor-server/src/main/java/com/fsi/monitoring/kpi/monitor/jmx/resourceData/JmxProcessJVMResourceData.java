package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.lang.management.CompilationMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.sun.management.OperatingSystemMXBean;

public class JmxProcessJVMResourceData extends IkrResourceData {

	private RuntimeMXBean runtimeMXBean;
	private CompilationMXBean compilationMXBean;
	private OperatingSystemMXBean operatingSystemMXBean;
	private String processName;
	
	public JmxProcessJVMResourceData(RuntimeMXBean runtimeMXBean,
									CompilationMXBean compilationMXBean,
									OperatingSystemMXBean operatingSystemMXBean,
									String processName,
									Date captureTime) {
		super(captureTime);
		this.processName = processName;
		this.runtimeMXBean = runtimeMXBean;
		this.operatingSystemMXBean = operatingSystemMXBean;
		this.operatingSystemMXBean = operatingSystemMXBean;
	}

	public Map<String, String> getJvmStartTime() {		
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(runtimeMXBean.getStartTime()));
		return values;
	}
	
	public Map<String, String> getJvmCompileTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (compilationMXBean!=null)
			values.put(processName, String.valueOf(compilationMXBean.getTotalCompilationTime()));
		return values;
	}
	
	public Map<String, String> getJvmUptime() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(runtimeMXBean.getUptime()));
		return values;
	}
	
	public Map<String, String> getJvmCpuTime() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(processName, String.valueOf(operatingSystemMXBean.getProcessCpuTime()/1000000));
		return values;
	}
}
