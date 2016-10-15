package com.fsi.monitoring.indiktor.dao.impl;

public class MetricDomainConfigResourceModel {
	private String name;
	private boolean enable;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}	
}
