package com.fsi.monitoring.kpi.monitor.murex.paymentSettlement.resourceData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryResourceData;
import com.fsi.monitoring.kpi.monitor.murex.MurexSQLQueryValue;

public class MurexPaymentSettlementActivityResourceData extends MurexSQLQueryResourceData {

	public MurexPaymentSettlementActivityResourceData(Map<String, List<MurexSQLQueryValue>> queryValues, Date captureTime) {
		super(queryValues, captureTime);
	}
	
	public Map<String, String> getSize() {
		return getMetricValues("count");
	}

}
