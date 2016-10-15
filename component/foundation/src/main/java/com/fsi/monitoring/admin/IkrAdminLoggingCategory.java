package com.fsi.monitoring.admin;

public enum IkrAdminLoggingCategory {
	INFO("Info", 1),
	WARNING("Warning", 2),
	ERROR("Error", 3);
	
	private String label;
	private int level;
	
	private IkrAdminLoggingCategory(String label, int level) {
		this.label = label;
		this.level = level;
	}

	public String getLabel() {
		return label;
	}

	public int getLevel() {
		return level;
	}
}
