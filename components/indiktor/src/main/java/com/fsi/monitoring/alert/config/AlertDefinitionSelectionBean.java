package com.fsi.monitoring.alert.config;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;

import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;

public class AlertDefinitionSelectionBean 
implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1064159304387602601L;

	private AlertDefinitionBean alertDefinitionBean;
	private boolean selected;
	
	private String color;
	
	private Set<String> searchIndexes;
	
	public AlertDefinitionSelectionBean(AlertDefinitionBean alertDefinitionBean) {
		this.alertDefinitionBean = alertDefinitionBean;
		this.initSearchIndexes();
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();
		searchIndexes.add(alertDefinitionBean.getAlertDefinition().getName().toLowerCase());
		if (alertDefinitionBean.getAlertDefinition().getDescription()!=null)
			searchIndexes.add(alertDefinitionBean.getAlertDefinition().getDescription().toLowerCase());
		searchIndexes.add(alertDefinitionBean.getDomain().toLowerCase());
		searchIndexes.add(alertDefinitionBean.getGroup().toLowerCase());
		searchIndexes.add(alertDefinitionBean.getSubDomain().toLowerCase());
		searchIndexes.add(alertDefinitionBean.getLogicalEnv().getName().toLowerCase());
		searchIndexes.add(Boolean.toString(alertDefinitionBean.getAlertDefinition().isEnable()).toLowerCase());
	}
	
	public AlertDefinitionSelectionBean clone() {
		return new AlertDefinitionSelectionBean(alertDefinitionBean);
	}
	
	public AlertDefinitionBean getAlertDefinitionBean() {
		return alertDefinitionBean;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void updateSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void onChangeSelected(ValueChangeEvent evnt) {	
		this.selected = (Boolean)evnt.getNewValue();
	}
	
	public boolean isSelected() {
		return selected;
	}		
	
	public int hashCode() {
		return (int)alertDefinitionBean.getAlertDefinition().getId();
	}	
	
	public boolean equals(Object other) {
		AlertDefinitionBean oth = ((AlertDefinitionSelectionBean)other).getAlertDefinitionBean();
		
		return alertDefinitionBean.getAlertDefinition().getId() == oth.getAlertDefinition().getId();
	}
	
	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}	
	
	public long getId() {
		return alertDefinitionBean.getAlertDefinition().getId();
	}
	
	public String getStyle() {
		if(alertDefinitionBean.getAlertDefinition().isEnable())
			return "text-align: left;";
		else
			return "text-align: left; background-color: #F06161;";
	}	
}
