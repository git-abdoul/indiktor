package com.fsi.monitoring.scheduler;

import java.util.Date;

public class IkrJobSchedulerExec {
	private int jobSchedulerId;
	private Date startTime;
	private Date endTime;
	
	public IkrJobSchedulerExec(int jobSchedulerId) {
		super();
		this.jobSchedulerId = jobSchedulerId;
	}

	public int getJobSchedulerId() {
		return jobSchedulerId;
	}

	public void setJobSchedulerId(int jobSchedulerId) {
		this.jobSchedulerId = jobSchedulerId;
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
