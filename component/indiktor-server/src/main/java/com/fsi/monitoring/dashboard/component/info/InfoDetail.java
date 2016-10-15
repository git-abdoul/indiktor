package com.fsi.monitoring.dashboard.component.info;


import java.io.Serializable;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;

public class InfoDetail implements Serializable{
	private static final long serialVersionUID = 8282168509272810071L;
	
	private String label;
	private IkrValueBean ikrValueBean;
	
	public InfoDetail(String label) {
		this.label = label;
	}
	
	public void setIkrValueBean(IkrValueBean ikrValueBean) {
		this.ikrValueBean = ikrValueBean;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getValue() {
		String value = "";
		if (ikrValueBean != null)
				value = ikrValueBean.getFormattedValue().getValue() + " " +ikrValueBean.getFormattedValue().getIkrUnit().getSymbol();
		return value;
	}
}
