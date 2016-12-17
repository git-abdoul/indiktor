package com.fsi.fwk.scheduling;

import java.util.Date;

/**
 * Implementations of <code>ScheduleIterator</code> specify a schedule as a series of <code>java.util.Date</code> objects.
 */

public interface ScheduleIterator {
	
    public Date current();
/**
 * Returns the next time that the related {@link SchedulerTask} should be run.
 * @return the next time of execution
 */
	public Date next();
}
