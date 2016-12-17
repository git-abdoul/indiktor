package com.fsi.monitoring.connector;


public class RdbmsConnectorConfig
extends AbstractConnectorConfig {
	private static final long serialVersionUID = -2737124929645749668L;

	public static final String TYPE = "RDBMS";
	
	private String driver;
	private String uri;
	private String userName;
	private String password;
	
	private String instance;
	
	public RdbmsConnectorConfig(int id,							
							    String name,
							    String description,
							    int maxAttempt,
							    int attemptDelay) {
		super(id, name, description, maxAttempt, attemptDelay);
	}
 
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	public String getDriver() {
		return driver;
	}	
	
	public void setUri(String uri) {
		this.uri = uri;

		String[] tmp = uri.split(":");
		instance = tmp[tmp.length-1];		
	}
	
	public String getUri() {
		return uri;
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
	
	public String getType() {
		return TYPE;
	}

	public String getConnectorContext() {
		return instance;
	}
}
