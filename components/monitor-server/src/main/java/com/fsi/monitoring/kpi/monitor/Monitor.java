package com.fsi.monitoring.kpi.monitor;


import java.util.Date;
import java.util.Map;
import java.util.Observer;

import com.fsi.monitoring.admin.adminCtrl;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;


public interface Monitor extends adminCtrl{	
	long getId();
	String getName();
	boolean isAutoStart();
	void setMonitorConfig(MonitorConfig monitorConfig);
	MonitorConfig getMonitorConfig();
	void initMonitor() throws Exception;
	void addObserver(Observer o);
	long getCaptureDelay();
	String getType();
	Date getStartTime();
	String getLogicalEnv();
	Map<String, String> getBusinessFilterSet();	
}
