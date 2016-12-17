package com.fsi.monitoring.dashboard.thread;

import java.io.Serializable;

public class ThreadDetailBean implements Serializable{	
	private static final long serialVersionUID = 2772732931590808528L;
	
	private String threadName;
	private String state;
	private String blockedTime;
	private String waitedTime;
	private String stackTrace;
	private boolean selected;
	
	public ThreadDetailBean(String name) {
		this.threadName = name;
	}
	
	public String getThreadName() {
		return threadName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBlockedTime() {
		return blockedTime;
	}
	public void setBlockedTime(String blockedTime) {
		this.blockedTime = blockedTime;
	}
	public String getWaitedTime() {
		return waitedTime;
	}
	public void setWaitedTime(String waitedTime) {
		this.waitedTime = waitedTime;
	}
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
