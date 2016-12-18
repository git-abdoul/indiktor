package com.fsi.monitoring.dashboard.config;

import javax.faces.event.ActionEvent;

import com.fsi.monitoring.util.AccessControlBean;

public abstract class DashBoardConfigBean
extends AccessControlBean {

	protected String env;
	protected String type;
	protected String title;
	
	protected boolean error;
	protected String message;	
	
	public void init(String env,
			 String type,
			 String title) {
		this.env = env;
		this.type = type;
		this.title = title;
	}
	
	public boolean isError() {
		return error;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void ackError(ActionEvent act) {
		error = false;
	}	
	
}
