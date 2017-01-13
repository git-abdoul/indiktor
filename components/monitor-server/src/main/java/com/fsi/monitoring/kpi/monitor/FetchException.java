package com.fsi.monitoring.kpi.monitor;

public class FetchException 
extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2707139161896669443L;

	public FetchException(String message) {
		super(message);
	}

	public FetchException(Exception exc) {
		super(exc);
	}	
	
}
