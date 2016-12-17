package com.fsi.fwk.scheduling.iterators;

import com.fsi.fwk.scheduling.ScheduleIterator;

import java.util.Calendar;
import java.util.Date;

/**
 * A <code>DailyIterator</code> returns a sequence of dates on subsequent days
 * representing the same time each day.
 */
public class CustomIterator implements ScheduleIterator {
	private String type;
	private int value;
	private final Calendar calendar = Calendar.getInstance();

	public CustomIterator(int value, String type) {
		this(new Date(), value, type);
	}
	
	public CustomIterator(Date fromDate, int value, String type) {
		this.value = value;
		this.type = type;
		calendar.setTime(fromDate);
		if (!calendar.getTime().before(fromDate)) {
			if ("DAY_CT".equals(type))
				calendar.add(Calendar.DATE, -1*value);
			else if ("HOUR_CT".equals(type))
				calendar.add(Calendar.HOUR_OF_DAY, -1*value);
			else  if ("MIN_CT".equals(type))
				calendar.add(Calendar.MINUTE, -1*value);
		}		
	}

	public Date next() {
		if ("DAY_CT".equals(type))
			calendar.add(Calendar.DATE, value);
		else if ("HOUR_CT".equals(type))
			calendar.add(Calendar.HOUR_OF_DAY, value);
		else  if ("MIN_CT".equals(type))
			calendar.add(Calendar.MINUTE, value);		
		
		return calendar.getTime();
	}

	public Date current() {
		return calendar.getTime();
	}

}
