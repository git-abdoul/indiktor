package com.fsi.monitoring.util.menu.menuItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MenuItem {

	protected String value;
	protected String link;
	protected String action;
	protected String actionListener;
	protected Map<String,String> attributes;

	protected byte level;
	protected String id;
	
	public MenuItem() {
		attributes = new HashMap<String,String>();
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public void setActionListener(String actionListener) {
		this.actionListener = actionListener;
	}
	
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	public void setLevel(byte level) {
		this.level = level;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String edit() {
		StringBuffer edition = new StringBuffer();
		edition.append("<li>");
		edition.append(editLink());
		edition.append("</li>");
		
		return edition.toString();
	}
	
	protected String editLink() {
		StringBuffer edition = new StringBuffer();
		if (link == null) {
			edition.append("<h:commandLink ");
			if (actionListener != null) {
				edition.append("actionListener=\"");
				edition.append(actionListener);
				edition.append("\" ");
			}
			if (action != null) {
				edition.append("action=\"");
				edition.append(action);
				edition.append("\" ");
			}
			edition.append("value=\"");
			edition.append(value);
			edition.append("\"");
			if (attributes.isEmpty()) {
				edition.append("/>");
			} else {
				edition.append(">");
				for (Entry<String, String> entry : attributes.entrySet()) {
					edition.append("<f:attribute name=\"");
					edition.append(entry.getKey());
					edition.append("\" value=\"");
					edition.append(entry.getValue());
					edition.append("\"/>");
				}
				edition.append("</h:commandLink>");
			}
		} else {
			edition.append(value);
		}
		return edition.toString();
	}
}
