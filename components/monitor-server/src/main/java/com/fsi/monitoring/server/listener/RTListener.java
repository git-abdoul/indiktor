package com.fsi.monitoring.server.listener;


import java.util.Collection;
import org.apache.log4j.Logger;

import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.server.callback.AgentRTCallback;
import com.fsi.monitoring.server.callback.RTCallback;

public class RTListener {
	private static final Logger LOG = Logger.getLogger(RTListener.class);
	
	private RTCallback callback = null;
	
	public RTListener() throws Exception {
		callback = new AgentRTCallback();
	}
	
	public void newEvent(Collection<IkrValue> irkValue) {
		try {
			callback.onMessage(irkValue);
		} catch (RuntimeException e) {
			LOG.error("Impossible to create a new message", e);
		}
	}
}
