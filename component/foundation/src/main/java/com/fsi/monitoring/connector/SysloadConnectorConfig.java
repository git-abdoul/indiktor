package com.fsi.monitoring.connector;


public class SysloadConnectorConfig
extends HttpConnectorConfig {
  
	public static final String TYPE = "SYSLOAD";
	
	private String agent;
	private String userName;
	private String password;	
	
	public SysloadConnectorConfig(int id,							
							      String name,
							      String description,
							      int maxAttempt,
							      int attemptDelay) {
		super(id, name, description, maxAttempt, attemptDelay);
	}
 
	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	public String getAgent() {
		return agent;
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
}
