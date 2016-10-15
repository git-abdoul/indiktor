package com.fsi.monitoring.kpi.monitor.murex.server.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.murex.MurexInfoResourceData;
import com.fsi.monitoring.system.dto.murex.MurexInfo;

public class MurexServerResourceData extends MurexInfoResourceData {

	public MurexServerResourceData(List<MurexInfo> infos, Date captureTime) {
		super(infos, captureTime);
	}
	
	public Map<String, String> getStatus() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String host = (info.getValue("HOST")!= null)?info.getValue("HOST"):"";
			String npid = (info.getValue("NPID")!= null)?info.getValue("NPID"):"";
			String type = info.getValue("TYPE");
			String name = (host.length()>0 && npid.length()>0)?"SERVER["+host+"/"+ type+"/"+npid +"]":"SERVER["+type+"]";
			values.put(name, info.getValue("STATUS"));
		}
		return values;
	}
	
	public Map<String, String> getStartTime() {
		Map<String, String> values = new HashMap<String, String>();
		for (MurexInfo info : infos) {
			String host = (info.getValue("HOST")!= null)?info.getValue("HOST"):"";
			String npid = (info.getValue("NPID")!= null)?info.getValue("NPID"):"";
			String type = info.getValue("TYPE");
			String name = (host.length()>0 && npid.length()>0)?"SERVER["+host+"/"+ type+"/"+npid +"]":"SERVER["+type+"]";
			values.put(name, info.getValue("START_TIME"));
		}
		return values;
	}

}
