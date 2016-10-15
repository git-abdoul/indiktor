package com.fsi.monitoring.datamodel.bean.factory;

import javax.faces.model.SelectItem;

import com.fsi.monitoring.ikr.model.MetricDomainConfigField;
import com.fsi.monitoring.ikr.model.MetricDomainConfigFieldType;

public class MonitorAttributeUIBean {
	private MetricDomainConfigField field = null;
	private String value = null;
	
	public MonitorAttributeUIBean(MetricDomainConfigField field) {
		super();
		this.field = field;
	}
	
	public String getLabel() {
		String label = "";
		if (field!=null)
			label = field.getLabel();
		return label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setBooleanValue(boolean value) {
		this.value = String.valueOf(value);
	}
	
	public boolean getBooleanValue() {
		return Boolean.parseBoolean(this.value);
	}
	
	public boolean isEnabled() {
		boolean enabled = false;
		if (field!=null)
			enabled = field.isEnable();
		return enabled;
	}

	public MetricDomainConfigField getField() {
		return field;
	}

	public void setField(MetricDomainConfigField field) {
		this.field = field;
	}
	
	public boolean isInputText() {
		boolean ret = false;
		if (field!=null)
			ret = MetricDomainConfigFieldType.inputText.name().equals(field.getFieldType());
		return ret;
	}
	
	public boolean isSelectBooleanCheckbox() {
		boolean ret = false;
		if (field!=null)
			ret = MetricDomainConfigFieldType.selectBooleanCheckbox.name().equals(field.getFieldType());
		return ret;
	}
	
	public boolean isSelectOneMenu() {
		boolean ret = false;
		if (field!=null)
			ret = MetricDomainConfigFieldType.selectOneMenu.name().equals(field.getFieldType());
		return ret;
	}
	
	public SelectItem[] getSelectionItems() {
		SelectItem[] items = null;
		if (field!=null) {
			items = new SelectItem[field.getFieldTypeValues().size()];
			int i = 0;
			for (String name : field.getFieldTypeValues()) {
				items[i] = new SelectItem(name,name);
				i++;
			}
		}
		else {
			items = new SelectItem[0];
		}
		return items;
	}
}
