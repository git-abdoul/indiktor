package com.fsi.monitoring.alert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import com.fsi.fwk.histo.Historizable;
import com.fsi.monitoring.RealTimeValue;
import com.fsi.monitoring.RealtimeValueType;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;
import com.fsi.monitoring.alert.workflow.AlertCommentEvent;
import com.fsi.monitoring.alert.workflow.AlertComputeEvent;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.alert.workflow.AlertWorkflow.Status;


public class Alert
extends Historizable
implements Serializable,RealTimeValue {	
	private static final long serialVersionUID = -6974638982523934467L;	
	private static final Logger logger = Logger.getLogger(Alert.class);	
	
	private static final int MAX_EVENT_SIZE = 10;
	private static final int MAX_COMMENT_EVENT_SIZE = 10;
	
	private long alertDefinitionId;

	// Conditions that are up (key = conditionId)
	private Collection<Integer>	conditions;
	
	private List<AlertComputeEvent> computeEvents;
	private List<AlertCommentEvent>	commentEvents;
	
		
	// The last event that created an alertState change
	private AlertEvent  alertEvent;
	
	private long raisedTime;
	
	private Map<AlertCompute,ComputeStatus> computeStatus;
	
	public Alert(long alertDefinitionId) {
		conditions = new ArrayList<Integer>();
		computeEvents = new ArrayList<AlertComputeEvent>(MAX_EVENT_SIZE);
		commentEvents = new ArrayList<AlertCommentEvent>(MAX_COMMENT_EVENT_SIZE);
//		computeEvents = new ArrayList<AlertComputeEvent>();
//		commentEvents = new ArrayList<AlertCommentEvent>();
		this.alertDefinitionId = alertDefinitionId;
	}
	
	public long getValueDefinitionId() {
		return alertDefinitionId;
	}
	
	public AlertWorkflow getState() {
		AlertWorkflow res = null;

		if (alertEvent == null) {
			res =  AlertWorkflow.DOWN;
		} else {
			res = alertEvent.getNewState();
		}
		return res;
	}
	
	public Map<AlertCompute,ComputeStatus> getComputeStatus() {
		return computeStatus;
	}
	
	public void setComputeStatus(Map<AlertCompute,ComputeStatus> computeStatus) {
		this.computeStatus = computeStatus;
	}
	
	public boolean isAcknowledged() {
		if (alertEvent == null) {
			return false;
		} else {
			AlertWorkflow newState = alertEvent.getNewState();
			return newState == AlertWorkflow.ACK;
		}
	}
	
	public AlertEvent getAlertEvent() {
		return alertEvent;
	}
	
	public boolean isConditionUP(int conditionId) {
		return conditions.contains(conditionId);
	}
	
	public void removeCondition(int conditionId) {
		conditions.remove(conditionId);
	}
	
	public void addCondition(int conditionId) {
		conditions.add(conditionId);
	}
	
	public void addEvent(AlertEvent event, boolean updateMAinStatus) {
		if (event instanceof AlertComputeEvent) {
			if (event.getNewState().getStatus() == Status.AUTO_UP &&
				raisedTime == 0) {	
				raisedTime = event.getEventDate().getTime();
			} else {
				raisedTime = 0;
			}

			if (computeEvents.size() == MAX_EVENT_SIZE) {
				computeEvents.remove(MAX_EVENT_SIZE-1);
			}
			computeEvents.add(0,(AlertComputeEvent)event);
		} else if (event instanceof AlertCommentEvent) {
			if (commentEvents.size() == MAX_COMMENT_EVENT_SIZE) {
				commentEvents.remove(MAX_COMMENT_EVENT_SIZE-1);
			}
			commentEvents.add(0,(AlertCommentEvent)event);
		}
		
		if (event.getNewState() != event.getOldState() && updateMAinStatus) {
			this.alertEvent = event;
			logger.debug("Alert Event Changed, new State=" +  event.getNewState());
		}
	}
	
	public void deleteEvent(AlertEvent event) {
		if (event instanceof AlertComputeEvent)
			computeEvents.remove(event);
		else if (event instanceof AlertCommentEvent) {
			if (commentEvents.contains(event))
				commentEvents.remove(event);
		}
	}
	
	public List<AlertComputeEvent> getComputeEvents() {
		return computeEvents;
	}
	
	public List<AlertCommentEvent> getCommentEvents() {
		return commentEvents;
	}
	
	public long getRaisedTime() {
		return raisedTime;
	}

	public String getType() {
		return RealtimeValueType.ALERT.name();
	}
}
