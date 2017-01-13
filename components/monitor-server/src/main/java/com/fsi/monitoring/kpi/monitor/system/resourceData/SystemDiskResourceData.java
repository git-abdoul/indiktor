package com.fsi.monitoring.kpi.monitor.system.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostFileSystemUsage;

public class SystemDiskResourceData extends IkrResourceData {
	
	private Map<String, HostFileSystemUsage> currentInfos = null;
	private Map<String, HostFileSystemUsage> previousInfos = null;	
	
	public SystemDiskResourceData(Map<String, HostFileSystemUsage> currentInfos,
								Map<String, HostFileSystemUsage> previousInfos,
			   				  	Date captureTime) {
		super(captureTime);
		this.currentInfos = currentInfos;
		this.previousInfos = previousInfos;
	}
	
	public Map<String, String> getTotal() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null) {
			for (String name : currentInfos.keySet()) {
				double size = currentInfos.get(name).getTotal()*1000;
				values.put(name, String.valueOf(size));
			}
		}
		return values;
	}
	
	public Map<String, String> getUsedpct() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null) {
			for (String name : currentInfos.keySet()) {
				double percent = currentInfos.get(name).getUsePercent();
				values.put(name, String.valueOf(percent));
			}
		}
		return values;
	}
	
	public Map<String, String> getReads() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getDiskReads();
				HostFileSystemUsage previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getDiskReads();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getReadBytes() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getDiskReadBytes();
				HostFileSystemUsage previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getDiskReadBytes();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getWrites() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getDiskWrites();
				HostFileSystemUsage previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getDiskWrites();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
	
	public Map<String, String> getWriteBytes() {
		Map<String, String> values = new HashMap<String, String>();
		if (currentInfos!=null && previousInfos!=null) {
			for (String name : currentInfos.keySet()) {
				long current = currentInfos.get(name).getDiskWriteBytes();
				HostFileSystemUsage previousInfo = previousInfos.get(name);
				long previous = previousInfo == null? 0 : previousInfo.getDiskWriteBytes();
				values.put(name, String.valueOf(current - previous));
			}
		}
		return values;
	}
}
