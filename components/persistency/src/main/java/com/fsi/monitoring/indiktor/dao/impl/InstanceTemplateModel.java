package com.fsi.monitoring.indiktor.dao.impl;

import java.util.ArrayList;
import java.util.List;

public class InstanceTemplateModel {
	private String template;
	private List<String> vars = new ArrayList<String>();		
	
	public String getTemplate() {
		return (template!=null && template.length()>0)?template:"";
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public void setVar(String var) {
		vars.add(var);
	}
	
	public List<String> getVars() {
		return vars;
	}
}
