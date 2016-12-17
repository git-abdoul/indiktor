package com.fsi.monitoring.ikr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetricDomainConfigField implements Serializable{
	private static final long serialVersionUID = -187764874596566508L;
	
	private int id;
	private int metricDomainConfigId;
	private String name;
	private String label;
	private boolean enable;
	
	private String fieldType;
	private List<String> fieldTypeValues;
	
	public MetricDomainConfigField(int id,
								 int metricDomainConfigId, 
								 String name, 
								 String label,
								 boolean enable,
								 String fieldType,
								 List<String> fieldTypeValues) {
		super();
		this.id = id;
		this.metricDomainConfigId = metricDomainConfigId;
		this.name = name;
		this.label = label;
		this.enable = enable;
		this.fieldType = fieldType;
		this.fieldTypeValues = fieldTypeValues;
	}
	
	public MetricDomainConfigField() {
		super();
		this.enable = false;
		this.fieldType = MetricDomainConfigFieldType.inputText.name();
		this.fieldTypeValues = new ArrayList<String>();
	}	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	

	public int getMetricDomainConfigId() {
		return metricDomainConfigId;
	}

	public void setMetricDomainConfigId(int metricDomainConfigId) {
		this.metricDomainConfigId = metricDomainConfigId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public List<String> getFieldTypeValues() {
		return fieldTypeValues;
	}
	
	public String getFieldTypeValuesStr() {
		 int i = 0;
	    int sz = fieldTypeValues.size();
	    String fieldTypeValuesStr = "";
	    for (String value : fieldTypeValues) {
	    	fieldTypeValuesStr = fieldTypeValuesStr + value;
	    	if (i < sz -1) {
	    		fieldTypeValuesStr = fieldTypeValuesStr + ",";
	    	}
	    	i++;
	    }
		return fieldTypeValuesStr;
	}

	public void setFieldTypeValues(List<String> fieldTypeValues) {
		this.fieldTypeValues = fieldTypeValues;
	}
}
