package com.fsi.fwk.scheduling.iterators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fsi.fwk.scheduling.ScheduleIterator;

/**
 * A <code>CompositeIterator</code> combines a number of {@link ScheduleIterator}s
 * into a single {@link ScheduleIterator}. Duplicate dates are removed.
 */
public class CompositeIterator implements ScheduleIterator {
	
	private List<Date> currentTimes = new ArrayList<Date>();
	private List<ScheduleIterator> currentIterators = new ArrayList<ScheduleIterator>();
	
	private List<Date> orderedTimes = new ArrayList<Date>();
	private List<ScheduleIterator> orderedIterators = new ArrayList<ScheduleIterator>();

	public CompositeIterator(ScheduleIterator[] scheduleIterators) {
		for (int i = 0; i < scheduleIterators.length; i++) {
			insert(scheduleIterators[i]);
		}
	}

	private void insert(ScheduleIterator scheduleIterator) {
		Date nextTime = scheduleIterator.next();
		if (nextTime == null) {
			return;
		}
		int index = Collections.binarySearch(orderedTimes, nextTime);
		if (index < 0) {
			index = -index - 1;
		}
		orderedTimes.add(index, nextTime);
		orderedIterators.add(index, scheduleIterator);
		
		Date time = scheduleIterator.current();
		if (time == null) {
			return;
		}
		index = Collections.binarySearch(orderedTimes, time);
		if (index < 0) {
			index = -index - 1;
		}
		currentTimes.add(index, time);
		currentIterators.add(index, scheduleIterator);
	}

	public synchronized Date next() {
		Date next = null;
		while (!orderedTimes.isEmpty() &&
				(next == null || next.equals((Date) orderedTimes.get(0)))) {
			next = (Date) orderedTimes.remove(0);
			insert((ScheduleIterator) orderedIterators.remove(0));
		}
		return next;
	}

	public Date current() {
		Date current = null;
		while (!currentTimes.isEmpty() &&
				(current == null || current.equals((Date) currentTimes.get(0)))) {
			current = (Date) currentTimes.remove(0);
			insert((ScheduleIterator) currentIterators.remove(0));
		}
		return current;
	}

}
