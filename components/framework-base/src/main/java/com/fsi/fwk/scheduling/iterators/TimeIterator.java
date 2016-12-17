package com.fsi.fwk.scheduling.iterators;

import com.fsi.fwk.scheduling.ScheduleIterator;

import java.util.Calendar;
import java.util.Date;

/**
 * A <code>DailyIterator</code> returns a sequence of dates on subsequent days
 * representing the same time each day.
 */
public class TimeIterator implements ScheduleIterator {
	private long delay;
	private final Calendar calendar = Calendar.getInstance();

	public TimeIterator(int hourOfDay, int minute, long delay) {
		this(new Date(), hourOfDay, minute, delay);
	}
	
	public TimeIterator(Date fromDate, int hourOfDay, int minute, long delay) {
		this.delay = delay;
		calendar.setTime(fromDate);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (!calendar.getTime().before(fromDate)) {
			calendar.add(Calendar.SECOND, -1*(int)delay);
		}		
	}

	public Date next() {
		calendar.add(Calendar.SECOND, (int)delay);
		return calendar.getTime();
	}

	public Date current() {
		return calendar.getTime();
	}

}
