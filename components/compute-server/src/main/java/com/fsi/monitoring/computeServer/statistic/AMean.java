package com.fsi.monitoring.computeServer.statistic;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import com.fsi.monitoring.kpi.metrics.IkrValue;

public class AMean 
extends AbstractIkrComputeStatistics
implements IkrComputeStatistics {

	private DescriptiveStatistics stats;
	
	public AMean(long ikrDefinitionId,
				 int windowSize) {
		super(ikrDefinitionId);
		
		stats = new DescriptiveStatistics();
		stats.setWindowSize(windowSize);
	}
	
	public void addValue(IkrValue ikrValue) {
		double value = Double.parseDouble(ikrValue.getValue());
		stats.addValue(value);
	}
	
	public String getComputedValue() {
		double mean = stats.getMean();
		return String.valueOf(mean);
	}	
}
