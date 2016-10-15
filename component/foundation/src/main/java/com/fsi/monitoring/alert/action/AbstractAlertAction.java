package com.fsi.monitoring.alert.action;

import java.io.Serializable;
import java.util.Collection;


public abstract class AbstractAlertAction 
implements AlertAction,Serializable {

	private static final long serialVersionUID = 5142364014762651837L;

	private int id;
	private long alertDefinitionId;
	private Collection<AlertActionType> types;

	
	public AbstractAlertAction(int id, 
							   long alertDefinitionId,
							   Collection<AlertActionType> types) {
		this.id = id;
		this.alertDefinitionId = alertDefinitionId;
		this.types = types;
	}
	
	public int getId() {
		return id;
	}
	
	public long getAlertDefinitionId() {
		return alertDefinitionId;
	}
	
	public void setAlertDefinitionId(long alertDefinitionId) {
		this.alertDefinitionId = alertDefinitionId;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setTypes(Collection<AlertActionType> types) {
		this.types = types;
	}
	
	public Collection<AlertActionType> getTypes() {
		return types;
	}
	
	public void addType(AlertActionType type) {
		types.add(type);
	}

}
