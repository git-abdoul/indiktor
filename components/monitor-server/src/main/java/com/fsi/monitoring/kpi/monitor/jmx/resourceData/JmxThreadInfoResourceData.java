package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class JmxThreadInfoResourceData extends IkrResourceData {

	private List<ThreadInfo> infos;
	private  String processName;

	public JmxThreadInfoResourceData(List<ThreadInfo> infos, String processName, Date captureTime){
		super(captureTime);
		this.infos = infos;
		this.processName = processName;
	}
	
	
	
	public Map<String, List<String>> getStacktrace() {
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		if (infos!=null&&infos.size()>0) {
			List<String> traces = new ArrayList<String>();
			values.put(processName, traces);
			for (ThreadInfo info : infos) {
				traces.add(getStackTrace(info));
			}
		}
		return values;	
	}
	
	private String getStackTrace(ThreadInfo info) {
		String stacktrace = "[" + info.getThreadName() +"]";
		for (StackTraceElement stack : info.getStackTrace()) {
			int line = stack.getLineNumber();
			String filename = (line > 0) ? (stack.getFileName() + ":" + line)
					: "Native Method";
			String trace = stack.getClassName() + ""
					+ stack.getMethodName() + "(" + filename + ")";
			stacktrace = stacktrace
					+ (stacktrace.length() != 0 ? "\n" : "") + trace;
		}
		return stacktrace;
	}
}
