package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostNetworkInterfaceStat;

public class SystemNetworkInterfaceResourceData extends IkrResourceData {
	
	private Map<String, HostNetworkInterfaceStat> currentInfos = null;
	private Map<String, HostNetworkInterfaceStat> previousInfos = null;
	
	public SystemNetworkInterfaceResourceData(Map<String, HostNetworkInterfaceStat> currentInfos,
											Map<String, HostNetworkInterfaceStat> previousInfos,
											Date captureTime) {
		super(captureTime);
		this.currentInfos = currentInfos;
		this.previousInfos = previousInfos;
	}
	
	public Map<String, String> getSpeed() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null) {
			for (String name : currentInfos.keySet()) {
				values.put(name, String.valueOf(currentInfos.get(name).getSpeed()));
			}
		}
		return values;
	}
	
	public Map<String, String> getRxBytes() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getRxBytes();
				HostNetworkInterfaceStat previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getRxBytes();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getRxPackets() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getRxPackets();
				HostNetworkInterfaceStat previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getRxPackets();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getRxErrors() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getRxErrors();
				HostNetworkInterfaceStat previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getRxErrors();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getTxBytes() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getTxBytes();
				HostNetworkInterfaceStat previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getTxBytes();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getTxPackets() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getTxPackets();
				HostNetworkInterfaceStat previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getTxPackets();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getTxErrors() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getTxErrors();
				HostNetworkInterfaceStat previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getTxErrors();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getTxCollisions() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getTxCollisions();
				HostNetworkInterfaceStat previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getTxCollisions();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
}
