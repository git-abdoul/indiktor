package com.fsi.monitoring.computeServer.alert;

import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;

public class AlertConditionResolverStrategyBoolean 
implements AlertConditionResolverStrategy {

	public ComputeStatus getConditionStatus(String value,
											AlertConditionOperator operator,
											String stringConditionValue) {
		ComputeStatus res = ComputeStatus.DOWN;
		
		if (Boolean.parseBoolean(value) == Boolean.parseBoolean(stringConditionValue)) {
			res = ComputeStatus.UP;
		}
		
		return res;
	}
}
