package com.fsi.monitoring.scheduler.config;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.fsi.fwk.scheduling.ScheduleIterator;
import com.fsi.fwk.scheduling.iterators.CustomIterator;
import com.fsi.fwk.scheduling.iterators.DailyIterator;
import com.fsi.fwk.scheduling.iterators.MonthlyIterator;
import com.fsi.fwk.scheduling.iterators.RestrictedDailyIterator;
import com.fsi.fwk.scheduling.iterators.WeeklyIterator;

public class ScheduledTaskConfig extends SchedulerConfig implements Serializable{
	private static final long serialVersionUID = 340528474702730336L;
	
	private String name;
	private int taskStaticDomainId;
	private String description;
	private Map<String, String> attributes;
	
	private int logicalEnvId;
	
	private String action;	
	private boolean active;
	
	private String modeType = null;
	private int modeValue;
	
	public ScheduledTaskConfig(int id, String name, int logicalEnvId, int taskStaticDomainId, String mode, String action, Calendar startTime, Calendar endTime, String description, boolean active) {
		super(id, mode, startTime, endTime);
		this.name = name;
		this.logicalEnvId = logicalEnvId;
		this.taskStaticDomainId = taskStaticDomainId;
		this.action = action;
		this.description = description;
		this.active = active;
		attributes = new HashMap<String, String>();
	}
	
	public ScheduledTaskConfig() {
		super();
		attributes = new HashMap<String, String>();
	}	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	

	public String getName() {
		return name;
	}	

	public void setName(String name) {
		this.name = name;
	}

	public int getTaskStaticDomainId() {
		return taskStaticDomainId;
	}	

	public void setTaskStaticDomainId(int taskStaticDomainId) {
		this.taskStaticDomainId = taskStaticDomainId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}	
	
	public int getLogicalEnvId() {
		return logicalEnvId;
	}

	public void setLogicalEnvId(int logicalEnvId) {
		this.logicalEnvId = logicalEnvId;
	}	
	
	public String getModeType() {
		return modeType;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}	

	public int getModeValue() {
		return modeValue;
	}

	public void setModeValue(int modeValue) {
		this.modeValue = modeValue;
	}

	public ScheduleIterator getStartIterator() {
		ScheduleIterator iterator = super.getStartIterator();;
		if (iterator == null) {
			if (NONE.equals(mode))
				iterator = new CustomIterator(modeValue, modeType);				
		}
		return iterator;
	}

	public ScheduleIterator getEndIterator() {
		ScheduleIterator iterator = null;
		Calendar endDate = Calendar.getInstance();
		if (endTime != null) {
			endDate.setTime(endTime.getTime());			
			if (DAILY.equals(mode))
				iterator = new DailyIterator(endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
			else if (RESTRICTED_DAILY.equals(mode)) 
				iterator = new RestrictedDailyIterator(endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE), selectedDays);
			else if (WEEKLY.equals(mode)) 
				iterator = new WeeklyIterator(endDate.get(Calendar.DAY_OF_WEEK), endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
			else if (MONTHLY.equals(mode)) 
				iterator = new MonthlyIterator(endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
		}
		
		return iterator;
	}
	
}
