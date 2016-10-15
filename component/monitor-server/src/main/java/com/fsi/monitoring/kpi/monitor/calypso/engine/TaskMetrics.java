package com.fsi.monitoring.kpi.monitor.calypso.engine;

public class TaskMetrics {
	private String task;
	private int completed;
	private int created;
	private int newKickOff;
	private int underProcessing;
	private int passedOver;
	
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public int getCompleted() {
		return completed;
	}
	public void setCompleted(int completed) {
		this.completed = completed;
	}
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
	public int getNewKickOff() {
		return newKickOff;
	}
	public void setNewKickOff(int newKickOff) {
		this.newKickOff = newKickOff;
	}
	public int getUnderProcessing() {
		return underProcessing;
	}
	public void setUnderProcessing(int underProcessing) {
		this.underProcessing = underProcessing;
	}
	public int getPassedOver() {
		return passedOver;
	}
	public void setPassedOver(int passedOver) {
		this.passedOver = passedOver;
	}	
}
