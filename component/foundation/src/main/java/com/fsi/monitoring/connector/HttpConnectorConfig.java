package com.fsi.monitoring.connector;


public class HttpConnectorConfig
extends AbstractConnectorConfig {
	private static final long serialVersionUID = 8170583433183112493L;

	public static final String TYPE = "HTTP";
	
	private String hostname;
	private int port;

	public HttpConnectorConfig(int id, 
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
