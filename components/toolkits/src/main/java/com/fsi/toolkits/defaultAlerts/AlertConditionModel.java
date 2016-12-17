package com.fsi.toolkits.defaultAlerts;

import java.io.Serializable;

public class AlertConditionModel implements Serializable {
	private static final long serialVersionUID = -6261897474999557117L;
	
	private int id;
	private String ikrCategoryValue;
	private String ikrInstance;
	private String context;
	private String compute;
	private String operator;
	private String value;
	private String unitType;
	private String unit;
	private boolean active;	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIkrCategoryValue() {
		return ikrCategoryValue;
	}
	public void setIkrCategoryValue(String ikrCategoryValue) {
		this.ikrCategoryValue = ikrCategoryValue;
	}
	public String getIkrInstance() {
		return ikrInstance;
	}
	public void setIkrInstance(String ikrInstance) {
		this.ikrInstance = ikrInstance;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}	
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getCompute() {
		return compute;
	}
	public void setCompute(String compute) {
		this.compute = compute;
	}
}
