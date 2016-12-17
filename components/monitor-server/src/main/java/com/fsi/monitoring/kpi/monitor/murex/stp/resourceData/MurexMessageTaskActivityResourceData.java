package com.fsi.monitoring.kpi.monitor.murex.stp.resourceData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryResourceData;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryValue;

public class MurexMessageTaskActivityResourceData extends MurexSQLQueryResourceData {

	public MurexMessageTaskActivityResourceData(Map<String, List<MurexSQLQueryValue>> queryValues, Date captureTime) {
		super(queryValues, captureTime);
	}
	
	public Map<String, String> getTaskMessages() {
		return getMetricValues("count");
	}

}
