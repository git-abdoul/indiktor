package com.fsi.monitoring.alert;

public class AlertSubDomain {
	private int id;
	private int domainId;
	private String value;
	
	public AlertSubDomain(int id, int domainId, String value) {
		super();
		this.id = id;
		this.domainId = domainId;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getDomainId() {
		return domainId;
	}

	public String getValue() {
		return value;
	}	
}
