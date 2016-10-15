package com.fsi.monitoring.datamodel.bean;

import java.io.Serializable;

public class SelectedItemBean implements Serializable{
	private static final long serialVersionUID = 7235774137329049272L;
	
	private boolean selected;
	private Object value;	
	
	public SelectedItemBean(Object value) {
		super();
		this.value = value;
	}	
	
	public SelectedItemBean() {
		super();
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

//	@Override
//	public boolean equals(Object obj) {
//		boolean ret = false;
//		if (obj instanceof SelectedItemBean) {
//			SelectedItemBean tmp = (SelectedItemBean)obj;
//			ret = value.equals(tmp.getValue());
//		}
//		return ret;
//	}
	
	
	
}
