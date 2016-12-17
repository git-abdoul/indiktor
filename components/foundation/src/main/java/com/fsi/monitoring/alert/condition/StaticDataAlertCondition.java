package com.fsi.monitoring.alert.condition;

import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;

public class StaticDataAlertCondition 
extends AlertCondition {


	private static final long serialVersionUID = -232508557330179573L;

	private long staticDataId = 0;
	
	public StaticDataAlertCondition(int id, 
									boolean enable,
								    long ikrDefinitionId,
								    long staticDataId) {
		super(id, enable, ikrDefinitionId);
		this.staticDataId = staticDataId;
	}
	
	public long getStaticDataId() {
		return staticDataId;
	}
	
	public ComputeStatus resolveCondition(AlertConditionResolver resolver) {
		return null;
	}	
	
}
