package com.fsi.monitoring.datamodel.bean;

import com.fsi.monitoring.ikr.model.MetricDomainConfigField;

public class MonitorConfigFieldItem {	
	private MetricDomainConfigField configField;
	private boolean selected;
	
	public MonitorConfigFieldItem() {
		configField = new MetricDomainConfigField();
		selected = false;
	}
	
	public MonitorConfigFieldItem(MetricDomainConfigField configField) {
		this.configField = configField;
		selected = false;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public int getId() {
		return configField.getId();
	}

	public MetricDomainConfigField getConfigField() {
		return configField;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

//	public String getType() {
//		return configField.getType();
//	}
//
//	public void setType(String type) {
//		configField.setType(type);
//	}

	public String getFieldName() {
		return configField.getName();
	}

	public void setFieldName(String fieldName) {
		configField.setName(fieldName);
	}

	public String getFieldLabel() {
		return configField.getLabel();
	}

	public void setFieldLabel(String fieldLabel) {
		configField.setLabel(fieldLabel);
	}	
	
	public boolean isEnabled() {
		return configField.isEnable();
	}

	public void setEnabled(boolean enabled) {
		configField.setEnable(enabled);
	}	
}
