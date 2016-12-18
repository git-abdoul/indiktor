package com.fsi.monitoring.kpi.monitor.murex.stp.resourceData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryResourceData;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryValue;

public class MurexFixingsActivityResourceData extends MurexSQLQueryResourceData {

	public MurexFixingsActivityResourceData(Map<String, List<MurexSQLQueryValue>> queryValues, Date captureTime) {
		super(queryValues, captureTime);
	}
	
	public Map<String, String> getStuckFixings() {
		return getMetricValues("stuckFixings");
	}
	
	public Map<String, String> getFixingsInError() {
		return getMetricValues("fixingsInError");
	}
	
	public Map<String, String> getAwaitingFixings() {
		return getMetricValues("awaitingFixings");
	}
	
	public Map<String, String> getTradesMissingFixings() {
		return getMetricValues("tradesMissingFixings");
	}

}
