package com.fsi.monitoring.computeServer.alert;

import java.util.Date;

import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;

public class AlertConditionResolverStrategyDatetime 
implements AlertConditionResolverStrategy {

	public ComputeStatus getConditionStatus(String value,
											AlertConditionOperator operator,
											String stringConditionValue) {
		ComputeStatus res = ComputeStatus.DOWN;
		
		Date conditionValue = new Date(Long.parseLong(stringConditionValue));
		
		if (value != null) {
			Date valueD = new Date(Long.parseLong(value));
			if (valueD != null) {	
				switch (operator) {
					case EQUAL_TO :
						if (valueD.compareTo(conditionValue)==0) {
							res = ComputeStatus.UP;
						}
					break;
					
					case GREATER_THAN :
						if (valueD.compareTo(conditionValue)>0) {
							res = ComputeStatus.UP;
						}
					break;
					
					case GREATER_THAN_OR_EQUAL_TO :
						if (valueD.compareTo(conditionValue)>=0) {
							res = ComputeStatus.UP;
						}
					break;
					
					case LESS_THAN :
						if (valueD.compareTo(conditionValue)<0) {
							res = ComputeStatus.UP;
						}
					break;
					
					case LESS_THAN_OR_EQUAL_TO :
						if (valueD.compareTo(conditionValue)<=0) {
							res = ComputeStatus.UP;
						}
					break;		
					
					case NOT_EQUAL_TO :
						if (valueD.compareTo(conditionValue)!=0) {
							res = ComputeStatus.UP;
						}
					break;
				}
			}
		}
		return res;
	}
}
