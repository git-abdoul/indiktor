package com.fsi.fwk.scheduling.iterators;

import com.fsi.fwk.scheduling.ScheduleIterator;

import java.util.Calendar;
import java.util.Date;

/**
 * A <code>DailyIterator</code> returns a sequence of dates on subsequent days
 * representing the same time each day.
 */
public class MonthlyIterator implements ScheduleIterator {
	private final Calendar calendar = Calendar.getInstance();

	public MonthlyIterator(int dayOfMonth, int hourOfDay, int minute) {
		this(new Date(), dayOfMonth, hourOfDay, minute);
	}
	
	public MonthlyIterator(Date fromDate, int dayOfMonth, int hourOfDay, int minute) {
		calendar.setTime(fromDate);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (!calendar.getTime().before(fromDate)) {
			calendar.add(Calendar.MONTH, -1);
		}
	}

	public Date next() {
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}

	public Date current() {
		return calendar.getTime();
	}

}
