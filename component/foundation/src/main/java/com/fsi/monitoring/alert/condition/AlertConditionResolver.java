package com.fsi.monitoring.alert.condition;

import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public interface AlertConditionResolver {

	void setIkrValue(IkrValue ikrValue);
	
	ComputeStatus resolveCondition(ValueAlertCondition alertCondition) throws Exception;
//	ComputeStatus resolveCondition(StaticDataAlertCondition alertCondition);
}
