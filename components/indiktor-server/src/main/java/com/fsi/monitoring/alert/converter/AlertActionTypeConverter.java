package com.fsi.monitoring.alert.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.fsi.monitoring.alert.action.AlertAction.AlertActionType;

public class AlertActionTypeConverter implements Converter 
{
		public Object getAsObject(FacesContext context, UIComponent component,String value) {
	 		return AlertActionType.valueOf(value);
		}

		public String getAsString(FacesContext context, UIComponent component,Object value) {
			AlertActionType alertActionType = (AlertActionType)value;
			return alertActionType.name();
		}

}

