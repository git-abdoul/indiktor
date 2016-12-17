package com.fsi.monitoring.kpi.monitor.log.generic.resourceData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.log.AbstractGenericLogInfoResourceData;
import com.fsi.monitoring.system.dto.logAnalysis.LogInfo;

public class GenericLogInfoResourceData extends AbstractGenericLogInfoResourceData {

	public GenericLogInfoResourceData(List<LogInfo> infos, Date captureTime) {
		super(infos, captureTime);
	}
	
	public Map<String, List<String>> getContent() {
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		for (LogInfo info : infos) {
			List<String> logContents =  values.get(info.getFilename());
			if (logContents == null) {
				logContents = new ArrayList<String>();
				values.put(info.getFilename(), logContents);
			}
			logContents.add(info.getLog());
		}
		return values;
	}
}
