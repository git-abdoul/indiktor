package com.fsi.monitoring.admin.console.bean;

public class IkrAdminStatsBean {
	private String attribute;
	private String value;
	
	public IkrAdminStatsBean(String attribute, String value) {
		super();
		this.attribute = attribute;
		this.value = value;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getValue() {
		return value;
	}	
}
