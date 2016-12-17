package com.fsi.monitoring.connector;

public class CalypsoConnectorConfig 
extends AbstractConnectorConfig {
	private static final long serialVersionUID = 3265593042266477480L;

	public static final String TYPE = "CALYPSO";	
	
	private String calypsoEnv;
	private String userName;
	private String password;
	private String dbUserName;
	private String dbPassword;
	private String applicationName;
	private boolean asofdateActive;
	private String asofdate;
	
	public CalypsoConnectorConfig(int id, 
								  String name, 
								  String description,
								  int maxAttempt,
								  int attemptDelay) {
		super(id, name, description, maxAttempt, attemptDelay);
	}

	public void setConnectorContext(String calypsoEnv) {
		this.calypsoEnv = calypsoEnv;
	}
	
	public String getConnectorContext() {
		return calypsoEnv;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public String getApplicationName() {
		return applicationName;
	}	
	
	public boolean isAsofdateActive() {
		return asofdateActive;
	}

	public void setAsofdateActive(boolean asofdateActive) {
		this.asofdateActive = asofdateActive;
	}	

	public String getAsofdate() {
		return asofdate;
	}

	public void setAsofdate(String asofdate) {
		this.asofdate = asofdate;
	}

	public String getType() {
		return TYPE;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}	
}
