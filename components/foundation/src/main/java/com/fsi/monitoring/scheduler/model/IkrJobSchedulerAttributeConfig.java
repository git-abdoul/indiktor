package com.fsi.monitoring.scheduler.model;

import java.util.List;

public class IkrJobSchedulerAttributeConfig {
	private int id;
	private int jobSchedulerStaticDomainId;
	private String name;
	private String label;
	private boolean enabled;
	
	private boolean selection;
	private List<String> selectionValues;
	
	public IkrJobSchedulerAttributeConfig(int id, int jobSchedulerStaticDomainId, String name,
			String label, boolean enabled, boolean selection, List<String> selectionValues) {
		super();
		this.id = id;
		this.jobSchedulerStaticDomainId = jobSchedulerStaticDomainId;
		this.name = name;
		this.label = label;
		this.enabled = enabled;
		
		this.selection = selection;
		this.selectionValues = selectionValues;
	}

	public int getId() {
		return id;
	}

	public int getJobSchedulerStaticDomainId() {
		return jobSchedulerStaticDomainId;
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

	public boolean isSelection() {
		return selection;
	}

	public List<String> getSelectionValues() {
		return selectionValues;
	}		
}
