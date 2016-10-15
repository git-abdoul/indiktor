package com.fsi.monitoring.alert;

import java.io.Serializable;

import com.fsi.monitoring.alert.workflow.AlertWorkflow;

public class AlertCompute 
implements Serializable, Comparable<AlertCompute> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8776112095335610807L;
	
	private AlertWorkflow workflow;
	private String label;
	private String cause;
	private boolean enable;

	public AlertCompute(AlertWorkflow workflow,
						String label,
						String cause,
						boolean enable) {
		this.workflow = workflow;
		this.label = label;
		this.cause = cause;
		this.enable = enable;
	}
	
	public AlertCompute(AlertWorkflow workflow) {
		this.workflow = workflow;
		enable = false;
	}	
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public AlertWorkflow getWorkflow() {
		return workflow;
	}
	
	public String getCause() {
		return cause;
	}
	
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public int hashCode() {
		return workflow.getSeverity();
	}
	
	public boolean equals(Object obj) {
		AlertCompute other = (AlertCompute)obj;
		
		if (other.cause.equals(cause) &&
			other.workflow == workflow) {
			return true;
		}
		
		return false;
	}

	public int compareTo(AlertCompute o) {
		return this.getWorkflow().getSeverity() - o.getWorkflow().getSeverity();
	}
	
}
