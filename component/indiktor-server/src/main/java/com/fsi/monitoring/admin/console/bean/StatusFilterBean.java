package com.fsi.monitoring.admin.console.bean;

import java.io.Serializable;

import com.fsi.monitoring.admin.ComponentStatus;

public class StatusFilterBean implements Serializable {
	private static final long serialVersionUID = 6587373167250774281L;
	
	private ComponentStatus status;
	private boolean selected;
	
	public StatusFilterBean(ComponentStatus status) {
		super();
		this.status = status;
	}

	public ComponentStatus getStatus() {
		return status;
	}

	public void setStatus(ComponentStatus status) {
		this.status = status;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}	
}
