package com.fsi.fwk.exception.server;

import com.fsi.fwk.exception.BaseException;

public class StartupServerException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StartupServerException(String message, String type) {
		super(message, type);
	}

	public StartupServerException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
