package com.fsi.monitoring.kpi.monitor.jmx.resourceData;

import java.util.Date;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;

public class JmxThreadDeadLockResourceData extends IkrInstanceData {
	private long[] tmpDeadlocks;
	
	public JmxThreadDeadLockResourceData(long[] tmpDeadlocks, String ikrInstance,	Date captureTime) {
		super(ikrInstance, captureTime);
		this.tmpDeadlocks = tmpDeadlocks;
	}

	public String getDeadlock() {
		long[] deadlocks = (tmpDeadlocks == null || tmpDeadlocks.length == 0) ? new long[0] : tmpDeadlocks;
		return String.valueOf(deadlocks.length);
	}
}
