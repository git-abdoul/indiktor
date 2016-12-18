package com.fsi.monitoring.kpi.monitor.calypso.stp.resourceData;

import java.util.Date;

import com.calypso.tk.util.ScheduledTask;

public class ScheduledTaskInfo {
	private String name;
	private Date startTime, endTime, scheduledTime;
	private long uptime, delay;
	private String status;
	private int trend;
	private ScheduledTask task;		
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStartTime() {
		return (startTime!=null)?startTime.getTime():-1;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return (endTime!=null)?endTime.getTime():-1;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getScheduledTime() {
		return scheduledTime.getTime();
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public long getUptime() {
		return uptime;
	}

	public long getDelay() {
		return delay;
	}

	public String getStatus() {
		return status;
	}

	public int getTrend() {
		return trend;
	}

		public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTrend(int trend) {
		this.trend = trend;
	}

	public ScheduledTask getTask() {
		return task;
	}

	public void setTask(ScheduledTask task) {
		this.task = task;
	}	
}
