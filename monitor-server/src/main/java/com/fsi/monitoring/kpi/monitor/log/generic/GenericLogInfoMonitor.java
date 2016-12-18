package com.fsi.monitoring.kpi.monitor.log.generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.log.LogSystemAgentMonitor;
import com.fsi.monitoring.kpi.monitor.log.generic.resourceData.GenericLogInfoResourceData;
import com.fsi.monitoring.system.dto.logAnalysis.LogInfo;

public class GenericLogInfoMonitor extends LogSystemAgentMonitor {
	private static final long serialVersionUID = 3546042629826272964L;
	
	public static final String ALL_PROCESS_WILDCARD ="*";
	
	protected String[] defaultFilter;
	protected String[] logContentFilters;
	protected String[] logFilenameFilters;
	
	@Override
	protected void preStart() {
		defaultFilter = new String[1];
		defaultFilter[0] = ALL_PROCESS_WILDCARD;
		logContentFilters = defaultFilter;	
		logFilenameFilters = defaultFilter;	
		super.preStart();
	}
	
	@Override
	protected void subscribeToInfo() {
		systemAgentConnector.register("LOG_INFO", this);
	}	
	
	public GenericLogInfoResourceData fetchLOG_CONTENT()
	throws ConnectorException {	
		List<LogInfo> logs = new ArrayList<LogInfo>();
		for (LogInfo info : infos) {
			if (accepts(info.getLog(), logContentFilters) && accepts(info.getFilename(), logFilenameFilters))
				logs.add(info);
		}
		return new GenericLogInfoResourceData(logs, new Date());
	}	
	
	public void init() throws Exception {
		super.init();
		
		String contentFilters = monitorConfig.getAttributes().get("LOG_CONTENT_FILTERS");
		logContentFilters = (contentFilters!=null&&contentFilters.length()>0)?contentFilters.split(":"):defaultFilter;
		
		String filenameFilters = monitorConfig.getAttributes().get("LOG_FILENAME_FILTERS");
		logFilenameFilters = (filenameFilters!=null&&filenameFilters.length()>0)?filenameFilters.split(":"):defaultFilter;
	}
}
