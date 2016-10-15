package com.fsi.monitoring.ikr.model;

import java.io.Serializable;

public class IkrStaticDomain implements Serializable{
	private static final long serialVersionUID = 2363802548528204353L;
	
	private int id;
	private int parentDomainId;
	private String domainValue;
	private String label;
	private String description;
	
	public IkrStaticDomain(int parentDomainId) {
		this.parentDomainId = parentDomainId;
	}	
	
	public IkrStaticDomain(int id, 
						   int parentDomainId, 
						   String domainValue, 
						   String label, 
						   String description) {
		super();
		this.id = id;
		this.parentDomainId = parentDomainId;
		this.domainValue = domainValue;
		this.label = label;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentDomainId() {
		return parentDomainId;
	}

	public void setParentDomainId(int parentDomainId) {
		this.parentDomainId = parentDomainId;
	}

	public String getDomainValue() {
		return domainValue;
	}

	public void setDomainValue(String domainValue) {
		this.domainValue = domainValue;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
