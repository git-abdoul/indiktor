package com.fsi.monitoring.jms;

import javax.jms.ConnectionFactory;

public class JmsConnectionConfig {
	private ConnectionFactory factory;
	private String destination;
	
	public void setJmsConnectionFactory(ConnectionFactory factory) {
		this.factory = factory;
	}
	
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public ConnectionFactory getJmsConnectionFactory() {
		return factory;
	}

	public String getDestination() {
		return destination;
	}	
}
