package com.fsi.monitoring.datamodel.bean;

import java.io.Serializable;

import com.fsi.monitoring.ikr.model.IkrStaticDomain;

public class IkrStaticDomainBean implements Serializable {
	private static final long serialVersionUID = -1970245585922206780L;
	
	private boolean selected;
	private IkrStaticDomain ikrStaticDomain;
	
	public IkrStaticDomainBean(IkrStaticDomain ikrStaticDomain) {
		super();
		this.ikrStaticDomain = ikrStaticDomain;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public IkrStaticDomain getIkrStaticDomain() {
		return ikrStaticDomain;
	}
}
