package com.fsi.monitoring.connector;

import java.io.Serializable;

public abstract class AbstractConnectorConfig 
implements ConnectorConfig, Serializable {
	private static final long serialVersionUID = 6604196678736092739L;
	
	private int id;
	private String name;
	private String description;
	
	private int maxAttempt;
	private int attemptDelay;
	
	public AbstractConnectorConfig(int id) {
		this.id = id;
	}

	public AbstractConnectorConfig(int id, 
							 	   String name, 
							 	   String description,
							 	   int maxAttempt,
							 	   int attemptDelay) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.maxAttempt = maxAttempt;
		this.attemptDelay = attemptDelay;
	}
	
	public abstract String getType();
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setMaxAttempt(int maxAttempt) {
		this.maxAttempt = maxAttempt;
	}
	
	public int getMaxAttempt() {
		return maxAttempt;
	}
	
	public void setAttemptDelay(int attemptDelay) {
		this.attemptDelay = attemptDelay;
	}
	
	public int getAttemptDelay() {
		return attemptDelay;
	}
}
