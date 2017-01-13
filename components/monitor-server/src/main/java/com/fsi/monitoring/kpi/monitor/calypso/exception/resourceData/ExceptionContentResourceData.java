package com.fsi.monitoring.kpi.monitor.calypso.exception.resourceData;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;

public class ExceptionContentResourceData extends IkrResourceData {
	private Map<String, List<String>> contents;
	
	public ExceptionContentResourceData(Map<String, List<String>> contents, Date captureTime) {
		super(captureTime);
		this.contents = contents;
	}
	
	public  Map<String, List<String>> getContent() {
		return contents;
	}
	
}
