package com.fsi.monitoring.utils;

import java.io.Serializable;

public class IkrInputCalendar implements Serializable {
	private static final long serialVersionUID = -2732179737692481632L;
	
	private int hour;
	private int minute;
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	
}
