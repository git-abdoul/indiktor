package com.fsi.monitoring.admin;

public enum ComponentStatus {
	NOTHING_TO_REPORT("OK", 2),
	ERROR_OCCURED("ERROR", 1),	
	STARTING("STARTING ...", 0),
	INVALID("INVALID", -2),
	NOT_RUNNING("DOWN", -5),	
	REMOVE("REMOVED", -10);
	
	private String label;
	private int statusLevel;
	
	private ComponentStatus(String label, int statusLevel) {
        this.label = label; 
        this.statusLevel = statusLevel; 
    }

	public String getLabel() {
		return label;
	}	

	public void setLabel(String label) {
		this.label = label;
	}

	public int getStatusLevel() {
		return statusLevel;
	} 
}
