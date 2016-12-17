package com.fsi.monitoring.system.dto;

import java.io.Serializable;
import java.util.Map;

public class IkrMessageValue implements Serializable {
	private static final long serialVersionUID = -5935075782464183608L;
	
	/**
	 * @uml.property  name="values"
	 * @uml.associationEnd  qualifier="tag:java.lang.String java.lang.String"
	 */
	private Map<String, String> values;

	public IkrMessageValue(Map<String, String> values) {
		super();
		this.values = values;
	}	
	
	public String getValue(String tag) {
		return this.values.get(tag);
	}

}
