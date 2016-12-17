package com.fsi.monitoring.ikr.model;

import java.io.Serializable;


public class MetricDomainResource 
implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8333735169931974085L;
	
	private int id;
	private int ikrStaticDomainId;
	private String resourceName;
	
	public MetricDomainResource() {
		resourceName = "";
	}

	public MetricDomainResource(int id, int ikrStaticDomainId, String resourceName) {
		super();
		this.id = id;
		this.ikrStaticDomainId = ikrStaticDomainId;
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIkrStaticDomainId() {
		return ikrStaticDomainId;
	}

	public void setIkrStaticDomainId(int ikrStaticDomainId) {
		this.ikrStaticDomainId = ikrStaticDomainId;
	}
}
