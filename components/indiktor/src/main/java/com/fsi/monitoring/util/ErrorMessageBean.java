package com.fsi.monitoring.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

public class ErrorMessageBean {
	public static final String WARNING = "Warning";
	public static final String ERROR = "Error";
	
	private boolean rendered;
	private boolean modal = true;
	
	private String type = "";
	private String message;
	private List<String> messages = new ArrayList<String>();
	
	public void init() {
		messages = new ArrayList<String>();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getMessage() {
		message = "";
		for (String msg : messages) {
			message = message + msg + "\n";
		}
		return message;
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}

	public boolean isRendered() {
		return rendered;
	}	
	
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
	}

	public void toggleMessage(ActionEvent event) {
		rendered = false;
		messages = new ArrayList<String>();
		type = "";
	}
}
