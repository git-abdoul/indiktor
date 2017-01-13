package com.fsi.monitoring.connector;

public class ConnectorException 
extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6506502979257315692L;
	
	public ConnectorException() {}
	
	public ConnectorException(String message) {
		super(message);
	}
	
	public ConnectorException(Exception exc) {
		super(exc);
	}
	
	public ConnectorException(String message, Exception exc) {
		super(message, exc);
	}
}
