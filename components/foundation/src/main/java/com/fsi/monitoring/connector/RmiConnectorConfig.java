package com.fsi.monitoring.connector;


public class RmiConnectorConfig
extends AbstractConnectorConfig {
	private static final long serialVersionUID = 6473407697232997451L;

	public static final String TYPE = "RMI";
	
	private String hostname;
	private int port;
	
	private String serviceName;

	public RmiConnectorConfig(int id, 
							   String name, 
							   String description,
							   int maxAttempt,
							   int attemptDelay) {
		super(id, name, description, maxAttempt, attemptDelay);
	}
	
	public void setConnectorContext(String hostname) {
		this.hostname = hostname;
	}
	
	public String getConnectorContext() {
		return hostname;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServiceName() {
		return serviceName;
	}	
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getType() {
		return TYPE;
	}	
}
