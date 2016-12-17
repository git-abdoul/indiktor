package com.fsi.toolkits.defaultAlerts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fsi.toolkits.VarDefModel;

public class AlertPropertiesModel implements Serializable{
	private static final long serialVersionUID = -2299122326179973678L;
	
	private String type;
	private List<VarDefModel> varDefinitions;

	public AlertPropertiesModel() {
		varDefinitions = new ArrayList<VarDefModel>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void addVarDefinition(VarDefModel model) {
		varDefinitions.add(model);
	}

	public List<VarDefModel> getVarDefinitions() {
		return varDefinitions;
	}	
}
