package com.fsi.monitoring.extension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.kpi.monitor.MonitorTask;

public class MockMonitor extends MonitorTask {

	@Override
	protected void initConnection() throws Exception {}
	
	@Override
	protected void postFetchs() throws Exception {}
	
	public MockIkrResourceData fetchMOCK_GROUP() 
	throws ConnectorException {
		return new MockIkrResourceData(new Date());
	}
	
	
	public class MockIkrResourceData 
	extends IkrResourceData {

		public MockIkrResourceData(Date captureTime) {
			super(captureTime);
			// TODO Auto-generated constructor stub
		}
		
		public Map<String, String> getStringValue() {
			Map<String, String> values = new HashMap<String, String>();
			values.put("instance", "La vie est un ...");
			return values;
		}
		
		public Map<String, String> getBooleanValue() {
			Map<String, String> values = new HashMap<String, String>();
			values.put("instance", String.valueOf(true));
			return values;
		}
		
		public Map<String, String> getRateValue() {
			Map<String, String> values = new HashMap<String, String>();
			values.put("instance", String.valueOf(78854645));
			return values;
		}
		
		public Map<String, String> getStorageValue() {
			Map<String, String> values = new HashMap<String, String>();
			values.put("instance", String.valueOf(12846897));
			return values;
		}
		
		public Map<String, String> getDurationValue() {
			Map<String, String> values = new HashMap<String, String>();
			values.put("instance", String.valueOf(180));
			return values;
		}
	}

	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
