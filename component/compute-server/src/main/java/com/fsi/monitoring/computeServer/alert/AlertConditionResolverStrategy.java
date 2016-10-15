package com.fsi.monitoring.computeServer.alert;

import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;

public interface AlertConditionResolverStrategy {

	ComputeStatus getConditionStatus(String value,
									 AlertConditionOperator operator,
									 String stringConditionValue);
	
}
