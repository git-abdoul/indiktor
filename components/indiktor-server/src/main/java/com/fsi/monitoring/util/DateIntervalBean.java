package com.fsi.monitoring.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;

public class DateIntervalBean {
	private String label;	
	private String type;	
	private String dayLabel;	
	private boolean dayRendered;
	
	private boolean active;
	
	private boolean disableSelection;
	
	private int day;
	private int min;
	private int hour;
	
	private static Map<Integer, String> daysOfWeek = new HashMap<Integer, String>();
	
	public DateIntervalBean(String label, String type) {
		this.label = label;
		this.type = (type != null && type.length()>0)?type:IkrMonitorSchedulerConfig.NONE;
		process();		
	}
	
	private void process() {
		if (IkrMonitorSchedulerConfig.WEEKLY.equals(type)) {
			dayLabel = "Day Of Week";
			dayRendered = true;
		}
		else if (IkrMonitorSchedulerConfig.MONTHLY.equals(type)) {
			dayLabel = "Day Of Month";
			dayRendered = true;
		}
		else{
			dayRendered = false;
		}
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
		process();
	}
	
	public int getDay() {
		return day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public int getMin() {
		return min;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	
	public int getHour() {
		return hour;
	}
	
	public void setHour(int hour) {
		this.hour = hour;
	}

	public String getDayLabel() {
		return dayLabel;
	}

	public boolean isDayRendered() {
		return dayRendered;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (!disableSelection)
			this.active = active;
		else
			this.active = false;
	}

	public boolean isDisableComponent() {
		return !active;
	}

	public void setDayRendered(boolean dayRendered) {
		this.dayRendered = dayRendered;
	}

	public boolean isDisableSelection() {
		return disableSelection;
	}

	public void setDisableSelection(boolean disableSelection) {
		this.disableSelection = disableSelection;
	}
	
	public boolean isMonthlyRenderer() {
		if(IkrMonitorSchedulerConfig.MONTHLY.equals(type))
			return true;
		return false;
	}
	
	public boolean isWeeklyRenderer() {
		if(IkrMonitorSchedulerConfig.WEEKLY.equals(type))
			return true;
		return false;
	}
	
	public Calendar getCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if (IkrMonitorSchedulerConfig.MONTHLY.equals(type))
			cal.set(Calendar.DAY_OF_MONTH, day);
		else if (IkrMonitorSchedulerConfig.WEEKLY.equals(type))
			cal.set(Calendar.DAY_OF_WEEK, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		return cal;
	}
	
}
