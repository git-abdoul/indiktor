package com.fsi.monitoring.alert.condition;

import java.io.Serializable;

import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;

public abstract class AlertCondition 
implements Serializable {
	
	private static final long serialVersionUID = -516966355096920007L;
	 
	private int id;
	private long ikrDefinitionId;
	
	protected AlertConditionOperator operator;
	
	private boolean enable;

	protected AlertCondition(int id,
						  	 boolean enable,
						  	 long ikrDefinitionId) {
		this.id = id;
		this.ikrDefinitionId = ikrDefinitionId;
		this.enable = enable;
	}
	
	public abstract ComputeStatus resolveCondition(AlertConditionResolver resolver) throws Exception;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public long getIkrDefinitionId() {
		return ikrDefinitionId;
	}
	public void setIkrDefinitionId(long ikrDefinitionId) {
		this.ikrDefinitionId = ikrDefinitionId;
	}
	
	public AlertConditionOperator getOperator() {
		return operator;
	}
	
	public void setOperator(AlertConditionOperator operator) {
		this.operator = operator;
	}
}
