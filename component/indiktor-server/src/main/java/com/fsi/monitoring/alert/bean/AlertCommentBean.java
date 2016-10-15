package com.fsi.monitoring.alert.bean;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.workflow.AlertCommentEvent;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.UserPM;

public class AlertCommentBean
implements Serializable {
	
	private static final Logger logger = Logger.getLogger(AlertCommentBean.class);			

	
	private static final long serialVersionUID = 5649909540362514286L;
	private AlertCommentEvent event;
	private User user;
	
	private String textAreaStyle = "";
	
	public AlertCommentBean(AlertCommentEvent event,
							UserPM userPM) {
		this.event = event;
		try {
			user = userPM.getUser(event.getUserId());
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public User getUser() {
		return user;
	}
	
	public AlertCommentEvent getEvent() {
		return event;
	}
	
	public int hashCode() {
		return event.hashCode();
	}	
	
	public boolean equals(Object obj) { 
		AlertEvent other = ((AlertCommentBean)obj).getEvent();
		 return (event.getId() == other.getId());
	}

	public String getTextAreaStyle() {
		if (event.getComment().length() < 50)
			textAreaStyle = "width: 280px; height: 25px;";
		else if(event.getComment().length() >= 50 && event.getComment().length() < 150)
			textAreaStyle = "width: 280px; height: 50px;";
		else if((event.getComment().length() >= 150) && (event.getComment().length() < 300))
			textAreaStyle = "width: 280px; height: 100px;";
		else
			textAreaStyle = "width: 280px; height: 150px;";
		return textAreaStyle;
	}

	public boolean isDeleteButtonOn() {
		if(event.getComment().equalsIgnoreCase("ACK"))
			return false;
		else
			return true;
	}

	public boolean isEditButtonOn() {
		if(event.getComment().equalsIgnoreCase("ACK"))
			return false;
		else
			return true;
	}
}
