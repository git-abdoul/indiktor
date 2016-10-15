package com.fsi.monitoring.dashboard.thread;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;


public class ThreadStatsBean 
implements Serializable {
	
	private static final long serialVersionUID = 7232103509959751781L;
	
	private String status;
	private String value;
	private String percentage;
	private boolean selected;
	private Date captureTime;

	public ThreadStatsBean(String status) {
		super();
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Date getCaptureTime(){
		return captureTime;
	}

	public void setCaptureTime(Date captureTime) {
		this.captureTime = captureTime;
	}
	
	
}
