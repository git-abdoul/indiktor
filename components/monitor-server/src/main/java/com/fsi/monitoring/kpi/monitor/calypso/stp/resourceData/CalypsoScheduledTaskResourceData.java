package com.fsi.monitoring.kpi.monitor.calypso.stp.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoScheduledTaskResourceData extends IkrResourceData {
	private List<ScheduledTaskInfo> tasks;
	
	public CalypsoScheduledTaskResourceData(List<ScheduledTaskInfo> tasks, Date captureTime) {
		super(captureTime);
		this.tasks = tasks;
	}
	
	public Map<String, String> getStartTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (tasks!=null) {
			for (ScheduledTaskInfo info : tasks) {
				values.put(info.getName(), String.valueOf(info.getStartTime()));
			}
		}
		return values;
	}

	public Map<String, String> getEndTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (tasks!=null) {
			for (ScheduledTaskInfo info : tasks) {
				values.put(info.getName(), String.valueOf(info.getEndTime()));
			}
		}
		return values;
	}

	public Map<String, String> getScheduledTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (tasks!=null) {
			for (ScheduledTaskInfo info : tasks) {
				values.put(info.getName(), String.valueOf(info.getScheduledTime()));
			}
		}
		return values;
	}

	public Map<String, String> getUptime() {
		Map<String, String> values = new HashMap<String, String>();
		if (tasks!=null) {
			for (ScheduledTaskInfo info : tasks) {
				values.put(info.getName(), String.valueOf(info.getUptime()));
			}
		}
		return values;
	}

	public Map<String, String> getDelay() {
		Map<String, String> values = new HashMap<String, String>();
		if (tasks!=null) {
			for (ScheduledTaskInfo info : tasks) {
				values.put(info.getName(), String.valueOf(info.getDelay()));
			}
		}
		return values;
	}

	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		if (tasks!=null) {
			for (ScheduledTaskInfo info : tasks) {
				values.put(info.getName(), String.valueOf(info.getStatus()));
			}
		}
		return values;
	}
}
