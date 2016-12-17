package com.fsi.fwk.exception;

public interface IException {
	public boolean hasCause();	 
	public Throwable getCause();	 
	public void printStackTrace();
}
