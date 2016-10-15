package com.fsi.monitoring.indiktor.dao.impl;

public class MetricDomainConfigFieldModel {
	private String name;
	private String label;
	private boolean enable;
	private String fieldType;
	private String fieldTypeValues;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldTypeValues() {
		return fieldTypeValues;
	}
	public void setFieldTypeValues(String fieldTypeValues) {
		this.fieldTypeValues = fieldTypeValues;
	}	
	
}
