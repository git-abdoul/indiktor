package com.fsi.monitoring.util.menu.menuItem;

import java.util.ArrayList;
import java.util.Collection;

public class MenuItems 
extends MenuItem {
	
	private Collection<MenuItem> children;
	
	public MenuItems() {
		children = new ArrayList<MenuItem>();
	}
	
	public void add(MenuItem child) {
		children.add(child);
		child.setLevel((byte)(this.level + 1));
		child.id = this.id + children.size();
	}
	
	
	@Override
	public String edit() {
		StringBuffer edition = new StringBuffer();
		edition.append("<li>");
		edition.append(editLink());
		edition.append("<ul id=\"");
		edition.append(id);
		edition.append("\" class=\"subMenuLvl");
		edition.append(level);
		edition.append("\">");
		
		for(MenuItem menuItem : children) {
			edition.append(menuItem.edit());
		}
		
		edition.append("</ul></li>");
		return edition.toString();
	}
	
}
