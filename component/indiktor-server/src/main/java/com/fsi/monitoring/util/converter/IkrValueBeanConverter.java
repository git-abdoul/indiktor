package com.fsi.monitoring.util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrMetricConverter;


public class IkrValueBeanConverter
implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return null;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		IkrValueBean ikrValueBean = (IkrValueBean)arg2;

		String value = ikrValueBean.getIkrValue().getValue();		
		IkrCategory ikrCategory = ikrValueBean.getIkrDefinitionBean().getIkrCategory();
	
		String[] display = IkrMetricConverter.convert(value, ikrCategory);
		
		return display[0] + display[1];
	}

}
