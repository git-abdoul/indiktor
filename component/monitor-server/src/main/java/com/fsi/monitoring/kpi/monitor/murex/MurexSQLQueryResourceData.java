package com.fsi.monitoring.kpi.monitor.murex;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class MurexSQLQueryResourceData extends IkrResourceData {
	
	protected Map<String, List<MurexSQLQueryValue>> queryValues;

	public MurexSQLQueryResourceData(Map<String, List<MurexSQLQueryValue>> queryValues, Date captureTime) {
		super(captureTime);
		
		this.queryValues = queryValues;
	}
	
	protected Map<String, String> getMetricValues(String metricName) {
		Map<String, String> values = new HashMap<String, String>();
		if (queryValues!=null) {
			for (String instance : queryValues.keySet()) {
				List<MurexSQLQueryValue> instanceValues = queryValues.get(instance);
				String value = "";
				int numericValue = 0;
				boolean isNumeric = true;
				for (MurexSQLQueryValue instValue : instanceValues) {
					String tmp = instValue.getValue(metricName);
					if (tmp == null)
						continue;
					try {
						numericValue = Integer.parseInt(tmp) + numericValue;
					}
					catch (Exception e) {
						value = tmp;
						isNumeric = false;
						break;
					}
					
				}
				
				if (isNumeric)
					value = String.valueOf(numericValue);
				
				values.put(instance, value);
			}
		}
		return values;
	}

}
