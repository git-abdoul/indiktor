package com.fsi.monitoring.kpi.monitor.calypso;

import java.util.Date;

public class CalypsoException {
	private Date date;
	private String applicationName;
	private String content;
	
	public CalypsoException(Date date, String applicationName, String content) {
		super();
		this.date = date;
		this.applicationName = applicationName;
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getContent() {
		return content;
	}
}
