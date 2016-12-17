package com.fsi.monitoring;


public enum AlertConditionOperator {
	
	EQUAL_TO("is equal to"),
	NOT_EQUAL_TO("is not equal to"),
	GREATER_THAN("is greater than"),
	GREATER_THAN_OR_EQUAL_TO("is greater than or equal to"),
	LESS_THAN("is less than"),
	LESS_THAN_OR_EQUAL_TO("is less than or equal to"),
	CONTAINS("contains"),
	NOT_CONTAINS("not contains");

	private String display;
	
	AlertConditionOperator(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}
}
