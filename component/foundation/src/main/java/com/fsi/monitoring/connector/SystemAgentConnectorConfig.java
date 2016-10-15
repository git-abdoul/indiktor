package com.fsi.monitoring.connector;

import java.io.Serializable;


public class SystemAgentConnectorConfig
extends RmiConnectorConfig implements Serializable{
	private static final long serialVersionUID = -9077139654679486378L;
	public static final String TYPE = "SYSTEM_AGENT";

	public SystemAgentConnectorConfig(int id, 
							   String name, 
							   String description,
							   int maxAttempt,
							   int attemptDelay) {
		super(id, name, description, maxAttempt, attemptDelay);
		
		setServiceName("com.fsi.monitoring.system.server.SystemMonitoringServer");
	}
	
	public String getType() {
		return TYPE;
	}
	
}
