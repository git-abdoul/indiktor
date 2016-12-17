package com.fsi.monitoring.util;

import java.util.HashMap;
import java.util.Map;

public class MonitorReconnectionAttempt {
	public final static int DEFAULT_MONITOR_MAX_RECONNECTION = 5;
	public final static int DEFAULT_MONITOR_RECONNECTION_DELAY = 300000;
	
	private Map<String,String> maxAttemptNumbers = new HashMap<String,String>();
	private Map<String,String> maxAttemptDelays = new HashMap<String,String>();
	
	public void setMaxAttemptNumbers(Map<String,String> maxAttemptNumbers) {
		this.maxAttemptNumbers = maxAttemptNumbers;
	}
	
	public int getMaxAttemptNumber(String type) {
		int nb = getIntValue(maxAttemptNumbers.get(type));
		return (nb==0)?DEFAULT_MONITOR_MAX_RECONNECTION:nb;
	}
	
	public void setMaxAttemptDelays(Map<String,String> maxAttemptDelays) {
		this.maxAttemptDelays = maxAttemptDelays;
	}
	
	public int getMaxAttemptDelay(String type) {
		int delay = getIntValue(maxAttemptDelays.get(type))*1000;
		return (delay==0)?DEFAULT_MONITOR_RECONNECTION_DELAY:delay;
	}
	
	private int getIntValue(String value) {
        if (value == null)
            return 0;
        try {
            return Integer.parseInt(value);
       } catch (Exception e) {
            return 0;
        }
    }
}
