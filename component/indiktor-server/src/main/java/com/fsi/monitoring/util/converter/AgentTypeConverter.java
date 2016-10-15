package com.fsi.monitoring.util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class AgentTypeConverter 
implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String type) {
		return type;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String agentType = (String)arg2;
		return agentType;
	}

}
