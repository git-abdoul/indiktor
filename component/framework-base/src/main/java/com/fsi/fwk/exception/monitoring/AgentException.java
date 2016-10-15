package com.fsi.fwk.exception.monitoring;

import com.fsi.fwk.exception.BaseException;

public class AgentException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2750039944796121567L;

	public AgentException(String message, String type) {
		super(message, type);
	}

	public AgentException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
