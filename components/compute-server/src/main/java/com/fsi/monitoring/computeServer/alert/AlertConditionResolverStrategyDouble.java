package com.fsi.monitoring.computeServer.alert;

import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;

public class AlertConditionResolverStrategyDouble 
implements AlertConditionResolverStrategy {

	public ComputeStatus getConditionStatus(String value,
											AlertConditionOperator operator,
											String stringConditionValue) {
		ComputeStatus res = ComputeStatus.DOWN;
		
		double conditionValue = Double.parseDouble(stringConditionValue);
		
		if (value != null) {
			double valueD = Double.parseDouble(value);
			if (value != null) {	
				switch (operator) {
					case EQUAL_TO :
						if (valueD == conditionValue) {
							res = ComputeStatus.UP;
						}
					break;
					
					case GREATER_THAN :
						if (valueD > conditionValue) {
							res = ComputeStatus.UP;
						}
					break;
					
					case GREATER_THAN_OR_EQUAL_TO :
						if (valueD >= conditionValue) {
							res = ComputeStatus.UP;
						}
					break;
					
					case LESS_THAN :
						if (valueD < conditionValue) {
							res = ComputeStatus.UP;
						}
					break;
					
					case LESS_THAN_OR_EQUAL_TO :
						if (valueD <= conditionValue) {
							res = ComputeStatus.UP;
						}
					break;		
					
					case NOT_EQUAL_TO :
						if (valueD != conditionValue) {
							res = ComputeStatus.UP;
						}
					break;
				}
			}
		}
		return res;
	}
}
