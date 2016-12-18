package com.fsi.monitoring.util.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.util.MessageBundleLoader;

public class DisplayConverter
implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return null;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String res = null;
		
		if (arg2 instanceof AlertWorkflow) {
			AlertWorkflow alertState = (AlertWorkflow)arg2;
			res = MessageBundleLoader.getMessage("alert.state." + alertState);
		}
		
		return res;
	}

}
