package com.fsi.monitoring.system.dto;

import java.io.Serializable;

public abstract class SystemInfo implements Serializable {
	private static final long serialVersionUID = 5276332180986306422L;
	
	private String type;
	private String category;

	public SystemInfo(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}
