package com.fsi.monitoring.dashboard.component.batch;


public class BatchBean {

	private String batchName;
	private boolean selected;
	
	public BatchBean(String batchName) {
		this.batchName = batchName;
	}

	public String getBatchNames() {
		return batchName;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
