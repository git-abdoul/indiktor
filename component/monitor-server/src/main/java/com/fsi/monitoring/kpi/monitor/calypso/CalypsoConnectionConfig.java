package com.fsi.monitoring.kpi.monitor.calypso;

public class CalypsoConnectionConfig {
	private String user;
	private String password;
	private String calypsoEnv;
	private String applicationName;	
	
	public CalypsoConnectionConfig(String user, String password,
			String calypsoEnv, String applicationName) {
		super();
		this.user = user;
		this.password = password;
		this.calypsoEnv = calypsoEnv;
		this.applicationName = applicationName;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getCalypsoEnv() {
		return calypsoEnv;
	}	
	
	public String getApplicationName() {
		return applicationName;
	}

	public String getKey() {
		return calypsoEnv + ":" + user;
	}
}
