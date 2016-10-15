package com.fsi.monitoring.util;


import com.fsi.monitoring.sec.SecurityBean;

public class AccessControlBean {
	
	//private SecurityBean securityBean;
	
	private String action;
	
	public AccessControlBean() {
	//	securityBean = (SecurityBean)FacesUtils.getManagedBean("securityBean");
	}
	
	public String action() {
		return action;
	}	
	
	public void setAction(String action) {
		this.action = action;
	}

	public boolean isAuthorized(long accessPermId, String action) {
		boolean authorized = getSecurityBean().isAuthorized(accessPermId);
		
	//	System.out.println(this + "--" + accessPermId + "-" +  action);
		
		if (!authorized) {
			this.action = "accessDenied";
		} else {
			this.action = action;
		}
		
		return authorized;
	}
	
	private SecurityBean getSecurityBean() {
		return (SecurityBean)FacesUtils.getManagedBean("securityBean");
	}
}
