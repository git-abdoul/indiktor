package com.fsi.monitoring.util;


public class MenuItemBean{
	private String id;
	private String action;
	private String actionListener;
	private boolean isDisable;
	private String link;
	private String value;
	private String style;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getActionListener() {
		return actionListener;
	}
	public void setActionListener(String actionListener) {
		this.actionListener = actionListener;
	}
	public boolean isDisable() {
		return isDisable;
	}
	public void setDisable(boolean isDisable) {
		this.isDisable = isDisable;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}	
}
