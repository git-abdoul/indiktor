package com.fsi.fwk.exception.monitoring;

import com.fsi.fwk.exception.BaseException;

public class CallbackException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8024499792229604580L;

	public CallbackException(String message, String type) {
		super(message, type);
	}

	public CallbackException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
