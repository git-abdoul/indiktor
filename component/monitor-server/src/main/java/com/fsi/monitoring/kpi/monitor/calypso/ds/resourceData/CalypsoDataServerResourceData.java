package com.fsi.monitoring.kpi.monitor.calypso.ds.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoDataServerResourceData extends IkrResourceData {
	private static final String INSTANCE = "dataserver";
	
	private int[] dbConnectionCount;
	private int[] connectedClients;
	private Boolean isDsConnectedToEs;
	private DSEventBuffer eventBuffer;
	
	public CalypsoDataServerResourceData(Date captureTime) {
		super(captureTime);
	}
	
	public Map<String, String> getConnectionsOpened() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (dbConnectionCount!=null)?String.valueOf(dbConnectionCount[0]):null);
		return values;
	}
	
	public Map<String, String> getConnectionsUsed() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (dbConnectionCount!=null)?String.valueOf(dbConnectionCount[1]):null);
		return values;
	}
	
	public Map<String, String> getCurrentBuffer() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (eventBuffer!=null)?String.valueOf(eventBuffer.getCurrentBufferSize()):null);
		return values;
	}
	
	public Map<String, String> getBufferLimit() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (eventBuffer!=null)?String.valueOf(eventBuffer.getBufferLimitSize()):null);
		return values;
	}
	
	public Map<String, String> getMaxBufferReached() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (eventBuffer!=null)?String.valueOf(eventBuffer.getMaxBufferSize()):null);
		return values;
	}
	
	public Map<String, String> getClientConnected() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (connectedClients!=null)?String.valueOf(connectedClients[0]):null);
		return values;
	}
	
	public Map<String, String> getVerifiedClientConnected() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (connectedClients!=null)?String.valueOf(connectedClients[1]):null);
		return values;
	}
	
	public Map<String, String> getEsConnectionLost() {
		Map<String, String> values = new HashMap<String, String>();
		values.put(INSTANCE, (isDsConnectedToEs!=null)?String.valueOf((isDsConnectedToEs)?true:false):null);
		return values;
	}

	public void setDbConnectionCount(int[] dbConnectionCount) {
		this.dbConnectionCount = dbConnectionCount;
	}

	public void setConnectedClients(int[] connectedClients) {
		this.connectedClients = connectedClients;
	}

	public void setDsConnectedToEs(Boolean isDsConnectedToEs) {
		this.isDsConnectedToEs = isDsConnectedToEs;
	}

	public void setEventBuffer(DSEventBuffer eventBuffer) {
		this.eventBuffer = eventBuffer;
	}	
}
