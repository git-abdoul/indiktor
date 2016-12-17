package com.fsi.toolkits.defaultAlerts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlertDefinitionModel implements Serializable{	
	private static final long serialVersionUID = -4725969256286948988L;
	
	private String type;
	private String name;
	private String group;
	private String domain;
	private String subDomain;
	private boolean active;
	private String description;
	
	private IkrInstanceVariableModel instanceVariable;
	
	private List<AlertConditionModel> conditions;
	private List<AlertComputeModel> computes;
	

	public AlertDefinitionModel() {
		conditions = new ArrayList<AlertConditionModel>();
		computes = new ArrayList<AlertComputeModel>();
	}

	public void addCondition(AlertConditionModel model) {
		conditions.add(model);
	}
	
	public void addCompute(AlertComputeModel model) {
		computes.add(model);
	}
	
	public void setInstanceVariable(IkrInstanceVariableModel model) {
		instanceVariable = model;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getSubDomain() {
		return subDomain;
	}

	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<AlertConditionModel> getConditions() {
		return conditions;
	}

	public List<AlertComputeModel> getComputes() {
		return computes;
	}

	public String getInstanceVariables() {
		return instanceVariable.getValue();
	}
}
