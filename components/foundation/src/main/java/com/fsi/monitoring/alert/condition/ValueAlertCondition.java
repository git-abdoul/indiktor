package com.fsi.monitoring.alert.condition;

import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;

public class ValueAlertCondition 
extends AlertCondition {

	private static final long serialVersionUID = 3500772551208621275L;

	private String conditionValue;
	private IkrUnitType conditionUnitType;
	private IkrUnit conditionUnit;
	
	public ValueAlertCondition(int id, 
							   boolean enable, 
							   long ikrDefinitionId,
							   String conditionValue,
							   IkrUnitType conditionUnitType,
							   IkrUnit conditionUnit) {
		super(id, enable, ikrDefinitionId);
		this.conditionValue = conditionValue;
		this.conditionUnit = conditionUnit;
		this.conditionUnitType = conditionUnitType;
	}	
	
	public void setValue(String value) {
		conditionValue = value;
	}
	
	public String getValue() {
		return conditionValue;
	}
	
	
	public IkrUnit getUnit() {
		return conditionUnit;
	}

	public void setUnit(IkrUnit unit) {
		this.conditionUnit = unit;
	}
	
	public IkrUnitType getUnitType() {
		return conditionUnitType;
	}

	public void setUnitType(IkrUnitType unitType) {
		this.conditionUnitType = unitType;
	}

	public ComputeStatus resolveCondition(AlertConditionResolver resolver) throws Exception {
		return resolver.resolveCondition(this);
	}	
}
