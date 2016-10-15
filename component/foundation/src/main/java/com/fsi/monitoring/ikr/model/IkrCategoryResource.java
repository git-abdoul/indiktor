package com.fsi.monitoring.ikr.model;

public class IkrCategoryResource {
	private int id;
	private int ikrStaticDomainId;
	private int metricDomainResourceId;
	private String name;
	private boolean activated;	
	
	public IkrCategoryResource(int id, int ikrStaticDomainId,
			int metricDomainResourceId, String name, boolean activated) {
		super();
		this.id = id;
		this.ikrStaticDomainId = ikrStaticDomainId;
		this.metricDomainResourceId = metricDomainResourceId;
		this.name = name;
		this.activated = activated;
	}	
	
	public int getMetricDomainResourceId() {
		return metricDomainResourceId;
	}

	public int getIkrStaticDomainId() {
		return ikrStaticDomainId;
	}	

	public void setName(String name) {
		this.name = name;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getName() {
		return name;
	}

	public boolean isActivated() {
		return activated;
	}

	public int getId() {
		return id;
	}

	public void setIkrStaticDomainId(int ikrStaticDomainId) {
		this.ikrStaticDomainId = ikrStaticDomainId;
	}

	public void setMetricDomainResourceId(int metricDomainResourceId) {
		this.metricDomainResourceId = metricDomainResourceId;
	}	
}	
