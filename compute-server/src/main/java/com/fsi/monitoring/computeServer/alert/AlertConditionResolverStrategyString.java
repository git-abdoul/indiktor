package com.fsi.monitoring.computeServer.alert;

import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;

public class AlertConditionResolverStrategyString 
implements AlertConditionResolverStrategy {

	public ComputeStatus getConditionStatus(String value,
											AlertConditionOperator operator,
											String stringConditionValue) {
		ComputeStatus res = ComputeStatus.DOWN;
		
		if (value == null)
			value = "";
		if (stringConditionValue == null)
			stringConditionValue = "";
		
		if (value != null) {	
			switch (operator) {
				case EQUAL_TO :
					if (value.equalsIgnoreCase(stringConditionValue)) {
						res = ComputeStatus.UP;
					}
				break;
				
				case NOT_EQUAL_TO :
					if (!value.equalsIgnoreCase(stringConditionValue)) {
						res = ComputeStatus.UP;
					}
				break;
				
				case CONTAINS :
					if ((value!=null && stringConditionValue!=null) && value.toLowerCase().contains(stringConditionValue.toLowerCase())) {
						res = ComputeStatus.UP;
					}
				break;
			
				case NOT_CONTAINS :
					if ((value!=null && stringConditionValue!=null) && !value.toLowerCase().contains(stringConditionValue.toLowerCase())) {
						res = ComputeStatus.UP;
					}
				break;
			}
		}
		return res;
	}
}
