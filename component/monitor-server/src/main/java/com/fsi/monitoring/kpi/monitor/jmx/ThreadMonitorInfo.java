package com.fsi.monitoring.kpi.monitor.jmx;

public class ThreadMonitorInfo {
	public double runnable;
	public double suspended;
	public double blocked;
	public double waiting;
	public double timeWaited;
	public double terminated;
	public double newThread;
	public double deadlock;
	
	public double getRunnable() {
		return runnable;
	}
	public double getSuspended() {
		return suspended;
	}
	public double getBlocked() {
		return blocked;
	}
	public double getWaiting() {
		return waiting;
	}
	public double getTimeWaited() {
		return timeWaited;
	}
	public double getTerminated() {
		return terminated;
	}
	public double getNewThread() {
		return newThread;
	}
	public double getDeadlock() {
		return deadlock;
	}	
}
