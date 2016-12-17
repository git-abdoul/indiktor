package com.fsi.monitoring.scheduler.model;

public class TaskStaticDomain {
	private int id;
	private String taskType;
	private String name;
	private String description;
	
	public TaskStaticDomain(int id, String taskType, String name,
			String description) {
		super();
		this.id = id;
		this.taskType = taskType;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getTaskType() {
		return taskType;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}	
}
