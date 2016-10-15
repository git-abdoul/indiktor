package com.fsi.fwk.scheduling.iterators;

import com.fsi.fwk.scheduling.ScheduleIterator;

import java.util.Calendar;
import java.util.Date;

/**
 * A <code>DailyIterator</code> returns a sequence of dates on subsequent days
 * representing the same time each day.
 */
public class DailyIterator implements ScheduleIterator {
	private final Calendar calendar = Calendar.getInstance();
	
	public DailyIterator(int hourOfDay, int minute) {		
		this(new Date(), hourOfDay, minute);
	}
	
	public DailyIterator(Date fromDate, int hourOfDay, int minute) {		
		calendar.setTime(fromDate);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
	
	public Date next() {
		Date now = new Date();
		if (calendar.getTime().before(now)) {
			calendar.add(Calendar.DATE, 1);
		}		
		return calendar.getTime();
	}

	public Date current() {
		return calendar.getTime();
	}

}
