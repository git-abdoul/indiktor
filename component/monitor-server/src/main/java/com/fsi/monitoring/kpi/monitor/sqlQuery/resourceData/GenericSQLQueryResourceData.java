package com.fsi.monitoring.kpi.monitor.sqlQuery.resourceData;

import java.util.Date;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class GenericSQLQueryResourceData extends IkrResourceData {
	private Map<String, Map<String, Object>> values;

	public GenericSQLQueryResourceData(Date captureTime, Map<String, Map<String, Object>> values) {
		super(captureTime);
		this.values = values;
	}
	
	public Map<String, Map<String, Object>> getQueryResult() {
		return values;
	}

}
