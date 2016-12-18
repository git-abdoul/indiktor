package com.fsi.monitoring.kpi.monitor.murex.marketData.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexInfoResourceData;
import com.fsi.monitoring.system.dto.murex.MurexInfo;

public class MurexMarketDataResourceData extends MurexInfoResourceData {

	public MurexMarketDataResourceData(List<MurexInfo> infos, Date captureTime) {
		super(infos, captureTime);
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String name = "MD[" + info.getValue("MD_TYPE") + "]";
			values.put(name, info.getValue("STATUS"));
		}
		return values;
	}
	
	public Map<String, String> getMdUpdated() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String name = "MD[" + info.getValue("MD_TYPE") + "]";
			values.put(name, info.getValue("MD_UPDATED"));
		}
		return values;
	}

}
