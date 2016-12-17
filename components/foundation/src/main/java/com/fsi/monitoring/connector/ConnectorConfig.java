package com.fsi.monitoring.connector;

public interface ConnectorConfig {

	int getId();
	
	String getName();
	String getType();
	String getDescription();
	String getConnectorContext();	
	
	int getMaxAttempt();
	int getAttemptDelay();
	
}
