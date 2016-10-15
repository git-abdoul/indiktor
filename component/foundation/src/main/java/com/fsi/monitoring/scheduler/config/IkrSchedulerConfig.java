package com.fsi.monitoring.scheduler.config;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.fsi.fwk.scheduling.ScheduleIterator;
import com.fsi.fwk.scheduling.iterators.DailyIterator;
import com.fsi.fwk.scheduling.iterators.MonthlyIterator;
import com.fsi.fwk.scheduling.iterators.RestrictedDailyIterator;
import com.fsi.fwk.scheduling.iterators.WeeklyIterator;

public abstract class IkrSchedulerConfig implements Serializable{
	private static final long serialVersionUID = 340528474702730336L;

	public static final String NONE = "None";
	public static final String DAILY = "Daily";
	public static final String RESTRICTED_DAILY = "Restricted Daily";
	public static final String WEEKLY = "Weekly";
	public static final String MONTHLY = "Monthly";
	
	public static final String START_TIME = "START_TIME";
	public static final String END_TIME = "END_TIME";
	public static final String DAY_CT = "DAY_CT";
	public static final String HOUR_CT = "HOUR_CT";
	public static final String MIN_CT = "MIN_CT";
	
	protected int id;
	protected String mode;
	
	protected Calendar startTime;
	protected Calendar endTime;
	
	protected int[] selectedDays;
	
	public IkrSchedulerConfig(int id, String mode, Calendar startTime, Calendar endTime) {
		this.id = id;
		this.mode = mode;
		this.startTime = startTime;
		this.endTime = endTime;
		
		if (DAILY.equals(mode)) {
			Date now = new Date(); 
			if (endTime!=null && now.before(endTime.getTime()) && now.after(startTime.getTime())) {
				this.startTime.add(Calendar.DATE, -1);
			}
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public IkrSchedulerConfig() {
		this.mode = DAILY;
		this.startTime = null;
		this.endTime = null;
	}

	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public Calendar getEndTime() {		
		return endTime;
	}	
	
	public int[] getSelectedDays() {
		return selectedDays;
	}

	public void setSelectedDays(int[] selectedDays) {
		this.selectedDays = selectedDays;
	}

	public ScheduleIterator getStartIterator() {
		ScheduleIterator iterator = null;
		if (startTime != null) {
			if (DAILY.equals(mode)) {
				iterator = new DailyIterator(startTime.getTime(), startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
			}
			else if (RESTRICTED_DAILY.equals(mode)) {
				iterator = new RestrictedDailyIterator(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), selectedDays);
			}
			else if (WEEKLY.equals(mode)) 
				iterator = new WeeklyIterator(startTime.get(Calendar.DAY_OF_WEEK), startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
			else if (MONTHLY.equals(mode)) 
				iterator = new MonthlyIterator(startTime.get(Calendar.DAY_OF_MONTH), startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
		}
		return iterator;
	}
}
