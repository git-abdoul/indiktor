package com.fsi.fwk.exception.persistence;

public class CacheMonitorPersistenceException extends PersistenceException {

	private static final long serialVersionUID = 6328345752158993825L;

	public CacheMonitorPersistenceException(String message, String type) {
		super(message, type);
	}

	public CacheMonitorPersistenceException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
