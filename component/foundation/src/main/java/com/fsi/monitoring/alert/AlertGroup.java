package com.fsi.monitoring.alert;

public class AlertGroup {
	private int id;
	private String value;
	
	public AlertGroup(int id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public String getValue() {
		return value;
	}
}
