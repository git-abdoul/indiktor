package com.fsi.fwk.exception;

public class SystemException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6150914579719212988L;

	public SystemException(String message, String type) {
		super(message, type);
	}

	public SystemException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
