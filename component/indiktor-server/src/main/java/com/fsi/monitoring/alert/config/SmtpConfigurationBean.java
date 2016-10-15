package com.fsi.monitoring.alert.config;

import javax.faces.event.ActionEvent;

import com.fsi.monitoring.util.AccessControlBean;

public class SmtpConfigurationBean extends AccessControlBean {
	
	public void init(ActionEvent action) {
		if (isAuthorized(110,"smtpConfig")) {	
		}
	}
}
