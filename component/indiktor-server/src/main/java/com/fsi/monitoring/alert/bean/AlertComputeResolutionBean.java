package com.fsi.monitoring.alert.bean;

import java.io.Serializable;
import java.util.Collection;

import com.fsi.monitoring.alert.AlertComputeResolution;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;

public class AlertComputeResolutionBean 
implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2893277691317537049L;

	private AlertComputeResolution alertComputeResolution;
	
	private Collection<IkrValueBean> ikrValueBeans;
	
	public AlertComputeResolutionBean(AlertComputeResolution alertComputeResolution,
									  Collection<IkrValueBean> ikrValueBeans) {
		this.alertComputeResolution = alertComputeResolution;
		this.ikrValueBeans = ikrValueBeans;
	}
	
	public AlertComputeResolution getAlertComputeResolution() {
		return alertComputeResolution;
	}
	
	public Collection<IkrValueBean> getIkrValueBeans() {
		return ikrValueBeans;
	}
	
}
