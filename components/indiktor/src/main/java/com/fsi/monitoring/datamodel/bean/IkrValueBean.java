package com.fsi.monitoring.datamodel.bean;

import java.io.Serializable;

import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.kpi.units.IkrUnitType;

public class IkrValueBean 
implements RealTimeBean, Serializable {	
	private static final long serialVersionUID = 7250847047198702353L;	

	private IkrDefinitionBean ikrDefinitionBean;
	private IkrValue ikrValue;

	public IkrValueBean(IkrDefinitionBean ikrDefinitionBean,
						IkrValue ikrValue) {
		this.ikrDefinitionBean = ikrDefinitionBean;
		this.ikrValue = ikrValue;
	}
	
	public IkrDefinitionBean getIkrDefinitionBean() {
		return ikrDefinitionBean;
	}
	
	public IkrValue getIkrValue() {
		return ikrValue;
	}
	
	public FormattedValue getFormattedValue() {
		IkrUnitType ikrUnitType = ikrDefinitionBean.getIkrCategory().getIkrUnitType();
		FormattedValue formattedValue = ikrUnitType.format(ikrValue.getValue(), ikrDefinitionBean.getIkrCategory().getIkrUnit());
		return formattedValue;
	}
}
