package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostNetworkStat;

public class SystemNetworkStatResourceData extends IkrResourceData {
	
	private HostNetworkStat info = null;
	
	public SystemNetworkStatResourceData(HostNetworkStat info,
			   				  		Date captureTime) {
		super(captureTime);
		this.info = info;
	}
	
	public Map<String, String> getTcpListen() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getTcpListen()));
		return values;
	}
	
	public Map<String, String> getTcpIdle() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getTcpIdle()));
		return values;
	}
	
	public Map<String, String> getTcpEstablished() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getTcpEstablished()));
		return values;
	}
	
	public Map<String, String> getTcpBound() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getTcpBound()));
		return values;
	}
	
	public Map<String, String> getTcpOutbound() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getTcpOutboundTotal()));
		return values;
	}
	
	public Map<String, String> getTcpInbound() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getTcpInboundTotal()));
		return values;
	}
	
	public Map<String, String> getAllOutbound() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getAllOutboundTotal()));
		return values;
	}
	
	public Map<String, String> getAllInbound() {
		Map<String, String> values = new HashMap<String, String>();
		if (info!= null)
			values.put("netstat", String.valueOf(info.getAllInboundTotal()));
		return values;
	}
}
