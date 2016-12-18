package com.fsi.monitoring.server.callback;


import java.util.Collection;

import com.fsi.monitoring.kpi.metrics.IkrValue;

public interface RTCallback {
	public void onMessage(Collection<IkrValue> ikrValues);
}
