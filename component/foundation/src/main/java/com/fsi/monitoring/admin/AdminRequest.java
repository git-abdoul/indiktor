package com.fsi.monitoring.admin;

import java.io.Serializable;

import com.fsi.monitoring.jms.IkrJmsMessage;

public class AdminRequest implements Serializable, IkrJmsMessage {
	private static final long serialVersionUID = 9209446733081207503L;	
	
	private AdminRequestCommand command;
	private long componentId;
	private AdminComponent componentType;
	
	public AdminRequest(AdminRequestCommand command, long componentId, AdminComponent componentType) {
		super();
		this.command = command;
		this.componentId = componentId;
		this.componentType = componentType;
	}

	public AdminRequestCommand getCommand() {
		return command;
	}

	public long getComponentId() {
		return componentId;
	}

	public AdminComponent getComponentType() {
		return componentType;
	}	
}
