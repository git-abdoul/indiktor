package com.fsi.monitoring.system.dto.murex;

import java.util.Date;
import java.util.Map;

import com.fsi.monitoring.system.dto.SystemInfo;

public class MurexInfo extends SystemInfo {
	private static final long serialVersionUID = -1963638032806502807L;
	
	private Date captureTime;
	
	private Map<String, String> values;	

	public MurexInfo(Map<String, String> values, String category, Date captureTime) {
		super("MurexInfo");
		setCategory(category);
		this.values = values;
		this.captureTime = captureTime;
	}

	public Date getCaptureTime() {
		return captureTime;
	}
	
	public String getValue(String tag) {
		return this.values.get(tag);
	}
	
}
