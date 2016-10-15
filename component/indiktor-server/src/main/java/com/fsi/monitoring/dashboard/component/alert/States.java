package com.fsi.monitoring.dashboard.component.alert;


public class States {
	private String state;
	private boolean selected;
	
	public States(String state, boolean selected) {
		this.state = state;
		this.selected = selected;
	}

	public String getState() {
		return state;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
