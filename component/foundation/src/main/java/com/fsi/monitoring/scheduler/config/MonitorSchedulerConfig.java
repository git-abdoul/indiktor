package com.fsi.monitoring.scheduler.config;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.fsi.fwk.scheduling.ScheduleIterator;
import com.fsi.fwk.scheduling.iterators.DailyIterator;
import com.fsi.fwk.scheduling.iterators.MonthlyIterator;
import com.fsi.fwk.scheduling.iterators.TimeIterator;
import com.fsi.fwk.scheduling.iterators.WeeklyIterator;

public class MonitorSchedulerConfig extends SchedulerConfig implements Serializable{
	private static final long serialVersionUID = 340528474702730336L;

	public static final String ONE_SHOT = "One Shot";
	public static final String RECURRING = "Recurring";
	
	private String type;
	
	private long delay;
	
	public MonitorSchedulerConfig(int id, String type, String mode, Calendar startTime, Calendar endTime , long delay) {
		super(id, mode, startTime, endTime);
		this.type = type;
		this.delay = delay;
	}
	
	public MonitorSchedulerConfig() {
		super();
		this.type = RECURRING;
		this.mode = NONE;
		this.delay = 30;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	
	
	public ScheduleIterator getEndIterator() {
		ScheduleIterator iterator = null;		
		if (RECURRING.equals(type) && !NONE.equals(mode)) {
			Calendar endDate = Calendar.getInstance();
			if (endTime == null) {
				endDate.setTime(startTime.getTime());
				if (DAILY.equals(mode))
					endDate.add(Calendar.DATE, 1);
				else if (WEEKLY.equals(mode)) 
					endDate.add(Calendar.WEEK_OF_YEAR, 1);
				else if (MONTHLY.equals(mode)) 
					endDate.add(Calendar.MONTH, 1);
			}
			else {
				endDate.setTime(endTime.getTime());
			}
			
			if (DAILY.equals(mode))
				iterator = new DailyIterator(endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
			else if (WEEKLY.equals(mode)) 
				iterator = new WeeklyIterator(endDate.get(Calendar.DAY_OF_WEEK), endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
			else if (MONTHLY.equals(mode)) 
				iterator = new MonthlyIterator(endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
		}		
		
		return iterator;
	}
	
	public ScheduleIterator getDelayIterator() {
		ScheduleIterator iterator = null;
		if (MonitorSchedulerConfig.RECURRING.equals(type)) {
			Calendar calendar = Calendar.getInstance();
			if (startTime == null) 
				calendar.setTime(new Date());
			else
				calendar.setTime(startTime.getTime()); 			
				
			iterator = new TimeIterator(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), delay);			
		}
		return iterator;
	}
}
