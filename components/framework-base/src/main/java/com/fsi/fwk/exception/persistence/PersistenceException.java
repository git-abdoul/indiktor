package com.fsi.fwk.exception.persistence;

import com.fsi.fwk.exception.BaseException;

public class PersistenceException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7213811241035359094L;

	public PersistenceException(String message, String type) {
		super(message, type);
	}

	public PersistenceException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
