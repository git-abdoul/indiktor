package com.fsi.toolkits.defaultAlerts;

import java.io.Serializable;

public class AlertComputeModel implements Serializable {
	private static final long serialVersionUID = -8980842829630547706L;
	
	private int severity;
	private String cause;
	private boolean active;
	
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
