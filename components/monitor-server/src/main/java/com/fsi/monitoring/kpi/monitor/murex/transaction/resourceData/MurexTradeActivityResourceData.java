package com.fsi.monitoring.kpi.monitor.murex.transaction.resourceData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryResourceData;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryValue;

public class MurexTradeActivityResourceData extends MurexSQLQueryResourceData {

	public MurexTradeActivityResourceData(Map<String, List<MurexSQLQueryValue>> queryValues, Date captureTime) {
		super(queryValues, captureTime);
	}
	
	public Map<String, String> getSize() {
		return null;
	}
	
	public Map<String, String> getLiveCount() {
		return getMetricValues("liveCount");
	}
	
	public Map<String, String> getDeadCount() {
		return getMetricValues("deadCount");
	}
	
	public Map<String, String> getPurgedCount() {
		return getMetricValues("purgedCount");
	}
}
