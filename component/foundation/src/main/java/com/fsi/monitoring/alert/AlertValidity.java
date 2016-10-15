package com.fsi.monitoring.alert;

import java.io.Serializable;
import java.text.NumberFormat;

import com.fsi.monitoring.utils.IkrInputCalendar;

public class AlertValidity implements Serializable {
	private static final long serialVersionUID = -7382317204314890566L;
	
	private static NumberFormat format = NumberFormat.getInstance();
	
	private IkrInputCalendar start;
	private IkrInputCalendar end;
	
	public AlertValidity(IkrInputCalendar start, IkrInputCalendar end) {
		super();
		this.start = start;
		this.end = end;
		format.setMinimumIntegerDigits(2);
	}
	
	public AlertValidity() {
		super();
		format.setMinimumIntegerDigits(2);
	}

	public IkrInputCalendar getStart() {
		return start;
	}

	public IkrInputCalendar getEnd() {
		return end;
	}	
	
	public void setStart(IkrInputCalendar start) {
		this.start = start;
	}

	public void setEnd(IkrInputCalendar end) {
		this.end = end;
	}

	public String getStartStr() {
		return format.format(start.getHour())+":" + format.format(start.getMinute());
	}
	
	public String getEndStr() {
		return (end != null) ? format.format(end.getHour())+":" + format.format(end.getMinute()) : "23:59";
	}

}
