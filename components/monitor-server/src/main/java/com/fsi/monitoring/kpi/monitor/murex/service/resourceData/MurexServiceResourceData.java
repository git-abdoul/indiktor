package com.fsi.monitoring.kpi.monitor.murex.service.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexInfoResourceData;
import com.fsi.monitoring.system.dto.murex.MurexInfo;

public class MurexServiceResourceData extends MurexInfoResourceData {

	public MurexServiceResourceData(List<MurexInfo> infos, Date captureTime) {
		super(infos, captureTime);
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String host = info.getValue("HOST");
			String installCode = info.getValue("INSTALL_CODE");
			String type = info.getValue("TYPE");
			String name = "SERVICE["+host+"/"+ type+"/"+installCode +"]";
			values.put(name, info.getValue("STATUS"));
		}
		return values;
	}
	
	public Map<String, String> getStartTime() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String host = info.getValue("HOST");
			String installCode = info.getValue("INSTALL_CODE");
			String type = info.getValue("TYPE");
			String name = "SERVICE["+host+"/"+ type+"/"+installCode +"]";
			values.put(name, info.getValue("START_TIME"));
		}
		return values;
	}

}
