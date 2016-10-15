package com.fsi.monitoring.scheduler.model;

public class IkrJobSchedulerStaticDomain {
	private int id;
	private String jobSchedulerType;
	private String name;
	private String description;
	
	public IkrJobSchedulerStaticDomain(int id, String jobSchedulerType, String name,String description) {
		super();
		this.id = id;
		this.jobSchedulerType = jobSchedulerType;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getJobSchedulerType() {
		return jobSchedulerType;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}	
}
