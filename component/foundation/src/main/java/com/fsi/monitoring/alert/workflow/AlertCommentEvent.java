package com.fsi.monitoring.alert.workflow;

import java.util.Date;

public class AlertCommentEvent 
extends AlertEvent {

	private static final long serialVersionUID = -2163848731728128310L;

	private String comment;
	private long userId;
	
	public AlertCommentEvent(Date eventDate,
			   				 AlertWorkflow oldStatus,
			   				 AlertWorkflow newStatus,
			   				 String comment,
			   				 long userId) {
		super(eventDate,oldStatus,newStatus);
		
		this.comment = comment;
		this.userId = userId;
	}	
	
	public String getComment() {
		return comment;
	}
	
	public long getUserId() {
		return userId;
	}
}
