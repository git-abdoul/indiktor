package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;

import com.fsi.monitoring.ikr.LogicalEnv;

public class LogicalEnvBean implements Serializable {
	private static final long serialVersionUID = 647158057598745781L;
	
	private boolean selected;
	private LogicalEnv logicalEnv;
	
	public LogicalEnvBean(LogicalEnv logicalEnv) {
		super();
		this.logicalEnv = logicalEnv;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}	
}
