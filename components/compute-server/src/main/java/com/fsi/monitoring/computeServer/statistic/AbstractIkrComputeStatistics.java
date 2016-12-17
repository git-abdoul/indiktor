package com.fsi.monitoring.computeServer.statistic;

import com.fsi.monitoring.kpi.compute.MetricCompute;

public abstract class AbstractIkrComputeStatistics {

	private long ikrDefinitionId;
	
	protected AbstractIkrComputeStatistics(long ikrDefinitionId) {
		this.ikrDefinitionId = ikrDefinitionId;
	}
	
	public long getIkrDefinition() {
		return ikrDefinitionId;
	}	
	
	public static IkrComputeStatistics createIkrComputeStatistics(long ikrDefinitionId,
															      MetricCompute metricCompute) {
			IkrComputeStatistics res = null;
			switch(metricCompute) {
				case MM100 :
					res = new AMean(ikrDefinitionId, 100);
				break;
				case MM50 :
					res = new AMean(ikrDefinitionId, 50);
				break;	
				case MM20 :
					res = new AMean(ikrDefinitionId, 20);
				break;				
			}
		
		return res;
	}
	
}
