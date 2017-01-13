package com.fsi.monitoring.kpi.monitor.calypso.stp.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class CalypsoKickOffCutOffResourceData extends IkrResourceData {
	private Map<String, List<String>> kickOffDts; 
	private Map<String, List<String>> cutOffDts; 
	private Map<String, String> ids;
	
	public CalypsoKickOffCutOffResourceData(Map<String, List<String>> kickOffDts, Map<String, List<String>> cutOffDts, Map<String, String> ids, Date captureTime) {
		super(captureTime);
		this.kickOffDts = kickOffDts;
		this.cutOffDts = cutOffDts;
		this.ids = ids;
	}
	
	public Map<String, List<String>> getKickOffDt() {
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		if (values!=null) {
			for (String instance : kickOffDts.keySet()) {								
				values.put(instance, kickOffDts.get(instance));
			}
		}
		return values;
	}

	public Map<String, List<String>> getCutOffDt() {
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		if (values!=null) {
			for (String instance : cutOffDts.keySet()) {								
				values.put(instance, cutOffDts.get(instance));
			}
		}
		return values;
	}

	public Map<String, String> getKickOffCutOffValues() {
		Map<String, String> values = new HashMap<String, String>();
		if (ids!=null) {
			for (String instance : ids.keySet()) {
				values.put(instance, ids.get(instance));
			}
		}
		return values;
	}
}
