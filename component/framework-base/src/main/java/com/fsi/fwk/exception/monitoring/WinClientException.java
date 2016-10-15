package com.fsi.fwk.exception.monitoring;

import com.fsi.fwk.exception.BaseException;

public class WinClientException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WinClientException(String message, String type) {
		super(message, type);
	}

	public WinClientException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
