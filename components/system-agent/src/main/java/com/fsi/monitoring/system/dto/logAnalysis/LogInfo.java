package com.fsi.monitoring.system.dto.logAnalysis;

import java.util.Date;

import com.fsi.monitoring.system.dto.SystemInfo;

public class LogInfo extends SystemInfo {
	private static final long serialVersionUID = -1963638032806502807L;
	
	private String filename;
	private Date captureTime;	
	private String log;	

	public LogInfo(String filename, String log, Date captureTime) {
		super("LogInfo");
		setCategory("LOG_INFO");
		this.filename = filename;
		this.log = log;
		this.captureTime = captureTime;
	}

	public String getFilename() {
		return filename;
	}

	public Date getCaptureTime() {
		return captureTime;
	}
	
	public String getLog() {
		return log;
	}
	
}
