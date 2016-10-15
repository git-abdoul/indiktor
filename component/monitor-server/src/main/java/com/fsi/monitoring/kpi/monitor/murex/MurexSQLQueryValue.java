package com.fsi.monitoring.kpi.monitor.murex;

import java.util.Map;

public class MurexSQLQueryValue {
	private Map<String, String> values;

	public MurexSQLQueryValue(Map<String, String> values) {
		super();
		this.values = values;
	}

	public String getValue(String name) {
		return values.get(name);
	}	
	
	public Map<String, String> getValues() {
		return values;
	}
}
