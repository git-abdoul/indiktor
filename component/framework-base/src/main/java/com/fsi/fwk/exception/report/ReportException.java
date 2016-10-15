package com.fsi.fwk.exception.report;

import com.fsi.fwk.exception.BaseException;

public class ReportException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1882579528087755562L;

	public ReportException(String message, String type) {
		super(message, type);
	}

	public ReportException(String message, Throwable cause, String type) {
		super(message, cause, type);
	}

}
