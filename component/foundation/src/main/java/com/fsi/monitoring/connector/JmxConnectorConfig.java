package com.fsi.monitoring.connector;


public class JmxConnectorConfig 
extends AbstractConnectorConfig {
	private static final long serialVersionUID = -1578571206834991169L;

	public static final String TYPE = "JMX";
	
	private String hostname;
	private int port;	
	
	private String processName;
	private String userName;
	private String password;	
	
	public JmxConnectorConfig(int id,
							  String name,
							  String description,
							  int maxAttempt,
							  int attemptDelay) {
		super(id, name, description, maxAttempt, attemptDelay);
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
	
	public void setConnectorContext(String hostname) {
		this.hostname = hostname;
	}
	
	public String getConnectorContext() {
		return hostname;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}	
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getProcessName() {
		return processName;
	}
	
	public String getType() {
		return TYPE;
	}
}
