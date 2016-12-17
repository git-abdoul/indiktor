package com.fsi.monitoring.alert.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fsi.monitoring.alert.AlertComputeResolution;

public class AlertComputeEvent extends AlertEvent {	
	private static final long serialVersionUID = 4319599310750616841L;
	
	private List<AlertComputeResolution> alertComputeResolutions;
	
	public AlertComputeEvent(Date eventDate,
			  				 AlertWorkflow oldState,
			  				 AlertWorkflow newState) {
		super(eventDate,oldState,newState);
		alertComputeResolutions = new ArrayList<AlertComputeResolution>();
	}
	
	public List<AlertComputeResolution> getAlertComputeResolutions() {
		return alertComputeResolutions;
	}
	
	public void addAlertComputeResolution(AlertComputeResolution alertComputeResolution) {
		alertComputeResolutions.add(alertComputeResolution);
	}
}
