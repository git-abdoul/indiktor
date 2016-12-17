package com.fsi.fwk.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

public abstract class BaseException extends Exception implements IException {
	
	 /**
     * logging type for error messages.
     */
    public static final String ERROR = "error";
    
    /**
     * logging type for error messages.
     */
    public static final String EXCEPTION = "exception";

    /**
     * logging type for warning messages.
     */
    public static final String WARNING = "warning";

    /**
     * logging type for info messages.
     */
    public static final String INFO = "info";

    final private Throwable cause;
	final private String type;

	public BaseException(final String message, final String type) {	 
		this(message, null, type);
	}
	 
	public BaseException(final String message, final Throwable cause, final String type) { 
       super(message);
       this.cause = cause;
       this.type = type;
	}
	 
    final public boolean hasCause() { 
         return (cause != null);
    }
 
    final public Throwable getCause() { 
         return cause;
    }
    
    final public String getType(){
    	return type;
    }
    
    private String getDetailedMessage() {
        StringBuffer msg = new StringBuffer();
        if (getMessage() != null) {
            msg.append("Message : ");
            msg.append(getMessage());
            msg.append("\n");
        }

        msg.append("Exception Stack Trace\n");
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            printStackTrace(pw);
            msg.append(sw.toString());
            sw.close();
        } catch (Exception e) {
            msg.append(toString());
        }
        Throwable rootCause = getCause();
        if (rootCause != null) {
            msg.append("\n Root Exception Stack Trace : ");
            msg.append(rootCause.toString());
            msg.append("\n");
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                rootCause.printStackTrace(pw);
                msg.append(sw.toString());
                sw.close();
            } catch (Exception e) {
                msg.append(rootCause.toString());
            }
        }
        return msg.toString();
    }
    
//    private String getDetailedMessage(Throwable a) {
//    	StringBuffer msg = new StringBuffer();
//        msg.append("Message : ");
//        msg.append(a.getMessage());
//        msg.append("\n");
//        msg.append("Exception Stack Trace\n");
//        try {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            a.printStackTrace(pw);
//            msg.append(sw.toString());
//            sw.close();
//        } catch (Exception e) {
//            msg.append(a.toString());
//        }
//        String ret = msg.toString();
//        msg = null;
//        return ret;
//    }

    public String getExceptionLog() {
        String errorId = getClass().getName();
        String detailedMessage = null;
        detailedMessage = getDetailedMessage();
        StringBuffer lBuffer = new StringBuffer();
        String msg = null;
        lBuffer.append("ERRORID :");
        lBuffer.append(errorId);
        lBuffer.append("\n");
        lBuffer.append("EXCEPTION MESSAGE :");
        lBuffer.append(detailedMessage);
        msg = lBuffer.toString();
        return msg;
    }    
 
    final public void printStackTrace() {
    	super.printStackTrace();
    	if (hasCause())
    		cause.printStackTrace();
    }
    
    public void logException(String type, Logger logger, boolean isLogged) {
    	if (!isLogged)
            return;
    	String exceptionTrace = getExceptionLog();
        if (BaseException.ERROR.equals(type) || BaseException.EXCEPTION.equals(type))
            logger.error(exceptionTrace);
        else if (BaseException.INFO.equals(type))
            logger.info(exceptionTrace);
        else if (BaseException.WARNING.equals(type))
            logger.warn(exceptionTrace);
        else {
            logger.error(exceptionTrace);
        }
    }
}
		