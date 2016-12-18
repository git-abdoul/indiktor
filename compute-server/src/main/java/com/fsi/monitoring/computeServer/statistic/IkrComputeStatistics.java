package com.fsi.monitoring.computeServer.statistic;

import com.fsi.monitoring.kpi.metrics.IkrValue;

public interface IkrComputeStatistics {

	long getIkrDefinition();
	void addValue(IkrValue ikrValue);
	String getComputedValue();
	
}
