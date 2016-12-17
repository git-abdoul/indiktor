package com.fsi.monitoring.report;

import java.io.Serializable;

public class ReportConfig 
implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private String description;
	
	public ReportConfig(long id,
					  String name,
					  String description) {
		this.id = id;
		this.name = name;
		this.description = description;	
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
}
