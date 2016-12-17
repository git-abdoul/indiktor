package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostTcp;

public class SystemNetworkTcpResourceData extends IkrResourceData {
	
	private HostTcp currentInfo = null;
	private HostTcp previousInfo = null;
	
	public SystemNetworkTcpResourceData(HostTcp currentInfo,
		  							HostTcp previousInfo,
			   				  		Date captureTime) {
		super(captureTime);
		this.currentInfo = currentInfo;
		this.previousInfo = previousInfo;
	}
	
	public Map<String, String> getTcpOutRsts() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null) {
			long current = currentInfo.getRetransSegs();
			long previous = previousInfo == null? 0 : previousInfo.getRetransSegs();
			values.put("nettcp", String.valueOf(current - previous));
		}
		return values;
	}
	
	public Map<String, String> getTcpOutSegments() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null) {
			long current = currentInfo.getOutSegs();
			long previous = previousInfo == null? 0 : previousInfo.getOutSegs();
			values.put("nettcp", String.valueOf(current - previous));
		}
		return values;
	}
	
	public Map<String, String> getTcpInErrors() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null) {
			long current = currentInfo.getInErrs();
			long previous = previousInfo == null? 0 : previousInfo.getInErrs();
			values.put("nettcp", String.valueOf(current - previous));
		}
		return values;
	}
	
	public Map<String, String> getTcpInSegments() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null) {
			long current = currentInfo.getInSegs();
			long previous = previousInfo == null? 0 : previousInfo.getInSegs();
			values.put("nettcp", String.valueOf(current - previous));
		}
		return values;
	}
	
	public Map<String, String> getTcpOpenedCnx() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null)
			values.put("nettcp", String.valueOf(currentInfo.getPassiveOpens()));
		return values;
	}
	
	public Map<String, String> getTcpCnxEstablished() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null)
			values.put("nettcp", String.valueOf(currentInfo.getCurrEstab()));
		return values;
	}
	
	public Map<String, String> getTcpCnxFailed() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null)
			values.put("nettcp", String.valueOf(currentInfo.getAttemptFails()));
		return values;
	}
	
	public Map<String, String> getTcpActiveCnx() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfo!= null)
			values.put("nettcp", String.valueOf(currentInfo.getActiveOpens()));
		return values;
	}
}
