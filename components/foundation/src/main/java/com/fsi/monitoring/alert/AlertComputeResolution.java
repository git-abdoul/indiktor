package com.fsi.monitoring.alert;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fsi.monitoring.kpi.metrics.IkrValue;


public class AlertComputeResolution
implements Serializable {

	private static final long serialVersionUID = -4712847254803832173L;
	
	public static enum ComputeStatus {UP,DOWN};	
	
	private AlertCompute alertCompute;
	private ComputeStatus computeStatus;
	private Set<Long> ikrValueIds;
	
	private List<IkrValue> nonPersistantValues;
	
	public AlertComputeResolution(AlertCompute alertCompute,
								  ComputeStatus computeStatus,
								  Set<Long> ikrValueIds) {
		this.alertCompute = alertCompute;
		this.computeStatus = computeStatus;
		this.ikrValueIds = ikrValueIds;
	}
	
	public AlertCompute getAlertCompute() {
		return alertCompute;
	}
	
	public ComputeStatus getComputeStatus() {
		return computeStatus;
	}
	
	public Collection<Long> getIkrValueIds() {
		return ikrValueIds;
	}

	public List<IkrValue> getNonPersistantValues() {
		return nonPersistantValues;
	}

	public void setNonPersistantValues(List<IkrValue> nonPersistantValues) {
		this.nonPersistantValues = nonPersistantValues;
	}
}
