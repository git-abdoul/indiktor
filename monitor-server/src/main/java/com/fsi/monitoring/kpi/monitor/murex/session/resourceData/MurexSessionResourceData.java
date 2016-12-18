package com.fsi.monitoring.kpi.monitor.murex.session.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexInfoResourceData;
import com.fsi.monitoring.system.dto.murex.MurexInfo;

public class MurexSessionResourceData extends MurexInfoResourceData {

	public MurexSessionResourceData(List<MurexInfo> infos, Date captureTime) {
		super(infos, captureTime);
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String user = info.getValue("USER");
			String npid = info.getValue("NPID");
			String name = "SESSION["+user+"/"+ npid+"]";
			values.put(name, info.getValue("STATUS"));
		}
		return values;
	}
	
	public Map<String, String> getStartTime() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String user = info.getValue("USER");
			String npid = info.getValue("NPID");
			String name = "SESSION["+user+"/"+ npid+"]";
			values.put(name, info.getValue("START_TIME"));
		}
		return values;
	}
	
	public Map<String, String> getMemory() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String user = info.getValue("USER");
			String npid = info.getValue("NPID");
			String name = "SESSION["+user+"/"+ npid+"]";
			values.put(name, info.getValue("MEM_SIZE"));
		}
		return values;
	}
	
	public Map<String, String> getCpu() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String user = info.getValue("USER");
			String npid = info.getValue("NPID");
			String name = "SESSION["+user+"/"+ npid+"]";
			values.put(name, info.getValue("CPU_SIZE"));
		}
		return values;
	}

}
