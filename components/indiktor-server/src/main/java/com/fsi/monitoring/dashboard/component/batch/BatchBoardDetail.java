package com.fsi.monitoring.dashboard.component.batch;

import java.io.Serializable;

public class BatchBoardDetail implements Serializable {
	private static final long serialVersionUID = -1371468422815529195L;

	private String label;
	private String scheduledTime;
	private String startTime;
	private String endTime;
	private String uptime;
	private String uptimeUnit;
	private String status;
	private String delay;
	private String output;
	private String color;
	
	
	public BatchBoardDetail(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getScheduledTime() {
		return scheduledTime;
	}


	public void setScheduledTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() { 
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public String getUptime() {
		return uptime;
	}


	public void setUptime(String uptime) {
		this.uptime = uptime;
	}	

	public String getUptimeUnit() {
		return uptimeUnit;
	}

	public void setUptimeUnit(String uptimeUnit) {
		this.uptimeUnit = uptimeUnit;
	}

	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}