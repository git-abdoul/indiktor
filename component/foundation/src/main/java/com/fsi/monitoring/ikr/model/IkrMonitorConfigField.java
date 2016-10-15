package com.fsi.monitoring.ikr.model;

public class IkrMonitorConfigField {
	private int id;
	private int ikrStaticDomainId;
	private String name;
	private String label;
	private boolean enable;
	
	public IkrMonitorConfigField(int id,
								 int ikrStaticDomainId, 
								 String name, 
								 String label,
								 boolean enable) {
		super();
		this.id = id;
		this.ikrStaticDomainId = ikrStaticDomainId;
		this.name = name;
		this.label = label;
		this.enable = enable;
	}
	
	public IkrMonitorConfigField() {
		super();
		this.enable = false;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
