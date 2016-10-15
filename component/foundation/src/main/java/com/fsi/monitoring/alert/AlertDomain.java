package com.fsi.monitoring.alert;

public class AlertDomain {
	private int id;
	private int groupId;
	private String value;
	
	public AlertDomain(int id, int groupId, String value) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getGroupId() {
		return groupId;
	}

	public String getValue() {
		return value;
	}	
}
