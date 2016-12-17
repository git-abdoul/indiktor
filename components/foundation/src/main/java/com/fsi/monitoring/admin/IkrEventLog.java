package com.fsi.monitoring.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class IkrEventLog extends AdminEvent {
	private static final long serialVersionUID = -5160760493385945646L;
	
	private Date startTime;
	private ComponentStatus status;
	private String name;
	private String logicalEnv;
	private String category;
	private List<IkrAdminLogging> logs;
	private Map<String, String> stats;
	
	public IkrEventLog(long componentId, String category, Date startTime, AdminComponent componentType, ComponentStatus status, String name, String logicalEnv, List<IkrAdminLogging> logs, Map<String, String> stats) {
		super(componentId, componentType);
		this.status = status;
		this.name = name;
		this.logicalEnv = logicalEnv;
		this.logs = logs;
		this.stats = stats;
		this.startTime = startTime;
		this.category = category;
	}

	public Date getStartTime() {
		return startTime;
	}

	public ComponentStatus getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}

	public String getLogicalEnv() {
		return logicalEnv;
	}

	public List<IkrAdminLogging> getLogs() {
		return logs;
	}

	public Map<String, String> getStats() {
		return stats;
	}

	public String getCategory() {
		return category;
	}
}
