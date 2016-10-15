package com.fsi.monitoring.scheduler.model;

public class TaskAttributeConfig {
	private int id;
	private int taskStaticDomainId;
	private String name;
	private String label;
	private boolean enabled;
	
	public TaskAttributeConfig(int id, int taskStaticDomainId, String name,
			String label, boolean enabled) {
		super();
		this.id = id;
		this.taskStaticDomainId = taskStaticDomainId;
		this.name = name;
		this.label = label;
		this.enabled = enabled;
	}

	public int getId() {
		return id;
	}

	public int getTaskStaticDomainId() {
		return taskStaticDomainId;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public boolean isEnabled() {
		return enabled;
	}	
}
