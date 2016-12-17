package com.fsi.monitoring.dashboard.component.framework;

import java.io.Serializable;

import com.fsi.monitoring.dashboard.config.DashboardComponentTypeLibrary;


public abstract class DashBoardComponent 
implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String componentId;
	protected String style;
	private String title;
	private String componentType;
	
	private boolean rendered;
	
	private boolean selected = false;
	
	public DashBoardComponent(String componentId,
							  String title,
							  String style,
							  String componentType,
							  boolean rendered) {
		this.componentId = componentId;
		this.title = title;
		this.style = style;
		this.componentType = componentType;
		this.rendered = rendered;
	}
	
	public abstract void synchronize();
	
	public String getComponentId() {
		return componentId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getStyle() {
		String shadow = "-moz-box-shadow: 0px 5px 10px rgba(0,0,0,0.8); -webkit-box-shadow: -webkit-box-shadow: 0px 5px 10px rgba(0,0,0,0.8); box-shadow: 0px 5px 10px rgba(0,0,0,0.8);";
		String corner = "-moz-border-radius: 5px; -webkit-border-radius: 5px; border-radius: 5px;";
		return style + " " + shadow + " " + corner;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public boolean isRendered() {
		return rendered;
	}

	public String getComponentType() {
		return componentType;
	}
	
	public String getComponentTypeLabel() {
		return DashboardComponentTypeLibrary.getComponentTypeLabel(componentType);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}	
}
