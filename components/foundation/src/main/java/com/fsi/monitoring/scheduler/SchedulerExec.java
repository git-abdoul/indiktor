package com.fsi.monitoring.scheduler;

import java.util.Date;

public class SchedulerExec {
	private int taskId;
	private Date startTime;
	private Date endTime;
	
	public SchedulerExec(int taskId) {
		super();
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}	
}
