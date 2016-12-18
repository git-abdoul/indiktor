package com.fsi.monitoring.datamodel.bean;

public class MonitorBusinessFilterBean {
	private boolean selected;
	private String type;
	private String value;	
	
	public MonitorBusinessFilterBean(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}	
	
	public MonitorBusinessFilterBean() {
		super();
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {		
		return type+"="+value;
	}	
}
