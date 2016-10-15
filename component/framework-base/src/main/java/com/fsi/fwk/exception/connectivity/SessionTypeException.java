package com.fsi.fwk.exception.connectivity;

import com.fsi.fwk.exception.BaseException;

public class SessionTypeException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3083047181259641045L;

	public SessionTypeException(String message, String type) {
		super(message, type);
	}

	public SessionTypeException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
