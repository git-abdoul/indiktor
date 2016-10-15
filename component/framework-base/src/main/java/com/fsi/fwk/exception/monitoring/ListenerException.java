package com.fsi.fwk.exception.monitoring;

import com.fsi.fwk.exception.BaseException;

public class ListenerException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4750726385534329634L;

	public ListenerException(String message, String type) {
		super(message, type);
	}

	public ListenerException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
