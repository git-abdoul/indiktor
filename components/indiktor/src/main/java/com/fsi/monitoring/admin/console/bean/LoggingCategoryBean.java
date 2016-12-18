package com.fsi.monitoring.admin.console.bean;

import java.io.Serializable;

import com.fsi.monitoring.admin.IkrAdminLoggingCategory;

public class LoggingCategoryBean implements Serializable {
	private static final long serialVersionUID = -5752765712340308443L;
	
	private IkrAdminLoggingCategory category;
	private boolean selected;
	
	public LoggingCategoryBean(IkrAdminLoggingCategory category) {
		super();
		this.category = category;
	}

	public IkrAdminLoggingCategory getCategory() {
		return category;
	}

	public void setCategory(IkrAdminLoggingCategory category) {
		this.category = category;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
