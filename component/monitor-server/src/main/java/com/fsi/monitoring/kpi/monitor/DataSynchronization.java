package com.fsi.monitoring.kpi.monitor;

import java.util.Date;
import java.util.Map;

public class DataSynchronization {
	private String id;
	private Date lastEvtDate;
	private  Map<String, Long> stats;
	
	public DataSynchronization(String id, Date lastEvtDate, Map<String, Long> stats) {
		super();
		this.id = id;
		this.lastEvtDate = lastEvtDate;
		this.stats = stats;
	}

	public String getId() {
		return id;
	}

	public Date getLastEvtDate() {
		return lastEvtDate;
	}

	public Map<String, Long> getStats() {
		return stats;
	}	
}
