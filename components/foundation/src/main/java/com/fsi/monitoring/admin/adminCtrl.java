package com.fsi.monitoring.admin;


public interface adminCtrl {
	void stop();
	void start() throws Exception;
	IkrEventLog getLog();
	void notifyCurrentState();
	
	void addLog(IkrAdminLogging log);
	void addStat(String attr, String value);
	void updateStatus (ComponentStatus status);
}
