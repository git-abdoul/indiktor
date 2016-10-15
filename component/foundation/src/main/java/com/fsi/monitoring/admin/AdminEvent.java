package com.fsi.monitoring.admin;

import java.io.Serializable;

import com.fsi.monitoring.RealTimeValue;
import com.fsi.monitoring.RealtimeValueType;

public class AdminEvent implements Serializable, RealTimeValue {
	private static final long serialVersionUID = -871794901782061788L;
	
	public static final String REMOVE_EVENT="REMOVE_EVENT";
	
	private long componentId;
	private AdminComponent componentType;
	
	public AdminComponent getComponentType() {
		return componentType;
	}	

	public AdminEvent(long componentId, AdminComponent componentType) {
		super();
		this.componentId = componentId;
		this.componentType = componentType;
	}

	public long getValueDefinitionId() {
		return componentId;
	}
	
	public String getType() {
		return RealtimeValueType.ADMIN_EVENT.name();
	}

}
