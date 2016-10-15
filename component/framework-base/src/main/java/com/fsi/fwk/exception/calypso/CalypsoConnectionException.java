package com.fsi.fwk.exception.calypso;

import com.fsi.fwk.exception.BaseException;

public class CalypsoConnectionException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7733272705680966743L;

	public CalypsoConnectionException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

	public CalypsoConnectionException(String message, String type) {
		super(message, type);
	}

}
