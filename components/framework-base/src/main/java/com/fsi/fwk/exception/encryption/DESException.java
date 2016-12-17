package com.fsi.fwk.exception.encryption;
import com.fsi.fwk.exception.BaseException;

public class DESException extends BaseException {

	private static final long serialVersionUID = -2710766463109323191L;

	/**
	 * 
	 */
	public DESException(String message, String type) {
		super(message, type);
	}

	public DESException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
