package com.fsi.monitoring.alert.workflow;

import java.io.Serializable;
import java.util.Date;

public class AlertEvent implements Serializable {
	private static final long serialVersionUID = -8043649265490175553L;
	
	private long id;
	
	private AlertWorkflow oldState;
	private AlertWorkflow newState;
	
	private Date eventDate;
	
	public AlertEvent(Date eventDate,
					  AlertWorkflow oldState,
					  AlertWorkflow newState) {
		this.eventDate = eventDate;
		this.oldState= oldState;
		this.newState = newState;
	}
	
	public AlertWorkflow getNewState() {
		return newState;
	}
	
	public AlertWorkflow getOldState() {
		return oldState;
	}
	
	public Date getEventDate() {
		return eventDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return (int)id;
	}

	@Override
	public boolean equals(Object obj) {
		AlertEvent evt = (AlertEvent)obj;
		return getId()== evt.getId();
	}	
	
	
}
