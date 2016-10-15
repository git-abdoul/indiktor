package com.fsi.monitoring.kpi.monitor.murex.stp.resourceData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryResourceData;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryValue;

public class MurexTradeTaskActivityResourceData extends MurexSQLQueryResourceData {

	public MurexTradeTaskActivityResourceData(Map<String, List<MurexSQLQueryValue>> queryValues, Date captureTime) {
		super(queryValues, captureTime);
	}
	
	public Map<String, String> getTradesInError() {
		return getMetricValues("dealInError");
	}
	
	public Map<String, String> getDocDealTaskIncomingTrades() {
		return getMetricValues("docDealTaskIncomingTrades");
	}
	
	public Map<String, String> getOutgoingTradesSendOperations() {
		return getMetricValues("outgoingTradesSendOperations");
	}

}
