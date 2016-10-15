package com.fsi.toolkits;

import java.io.Serializable;

public class VariableModel implements Serializable{
	private static final long serialVersionUID = 2017493179857262216L;
	
	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}	
}
