package com.fsi.monitoring.admin.console.bean;

import java.text.SimpleDateFormat;

import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;

public class IkrAdminLoggingBean {
	private IkrAdminLogging log;
	private String componentType;
	private String componentName;

	public IkrAdminLoggingBean(IkrAdminLogging log, String componentType, String componentName) {
		super();
		this.log = log;
		this.componentType = componentType;
		this.componentName = componentName;
	}
	
	public String getLogTime() {
		return (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(log.getLogDatetime());
	}
	
	public String getContent() {
		return log.getContent();
	}
	
	public String getCategory() {
		return log.getCategory().getLabel();
	}
	
	public String getStyle() {
		String style = "text-align: left;";
		if(log.getCategory()==IkrAdminLoggingCategory.WARNING)
			style =  style + " background-color: yellow";
		else if (log.getCategory()==IkrAdminLoggingCategory.ERROR)
			style = style + " background-color: #F06161;";
	   return style;
	 }

	public IkrAdminLogging getLog() {
		return log;
	}

	public String getComponentType() {
		return componentType;
	}

	public String getComponentName() {
		return componentName;
	}	
}
