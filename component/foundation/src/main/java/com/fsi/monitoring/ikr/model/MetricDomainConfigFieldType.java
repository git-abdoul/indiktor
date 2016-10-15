package com.fsi.monitoring.ikr.model;

public enum MetricDomainConfigFieldType {
	inputText("Text"),
	selectBooleanCheckbox("Checkbox"),
	selectOneMenu("Menu");
	
	private String label;
	
	MetricDomainConfigFieldType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
