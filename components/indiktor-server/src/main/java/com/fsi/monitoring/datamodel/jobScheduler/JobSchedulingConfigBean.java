package com.fsi.monitoring.datamodel.jobScheduler;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.util.DateIntervalBean;

public class JobSchedulingConfigBean
implements Serializable {

	private static final long serialVersionUID = 4743074674367768379L;
	
	private SelectItem[] schedulerModeItems;
	private SelectItem[] schedulerModeTypeItems;	
	private SelectItem[] daysOfWeekItems;
	private SelectItem[] daysOfMonthItems;
	private SelectItem[] hourItems;
	private SelectItem[] minItems;
	
	private DateIntervalBean startDate;
//	private DateIntervalBean endDate;
	
	private boolean activeSchedulerModeType = false;
	
	private String modeTypeLabel;
	
	private JobSchedulerCustomAttribute customAttributeBean;
	private IkrJobSchedulerConfig schedulerConfig = null;
	
	public JobSchedulingConfigBean(IkrJobSchedulerConfig schedulerConfig) {
		this.schedulerConfig = schedulerConfig;	
	}
	
	public void onChangeSchedulerMode(ValueChangeEvent e) {
		String newMode = (String)e.getNewValue();
		changeSchedulerMode(newMode);
	}
	
	public void onChangeSchedulerModeType(ValueChangeEvent e) {
		String newModeType = (String)e.getNewValue();
		changeSchedulerModeType(newModeType);
	}
	
	public void onChangeStartDateWeekly(ValueChangeEvent e) {
		int day = (Integer)e.getNewValue();
		startDate.setDay(day);
	}
	
	public void onChangeDaysOfMonth(ValueChangeEvent e) {
		int day = (Integer)e.getNewValue();
		startDate.setDay(day);
	}
	
	public void onChangeHour(ValueChangeEvent e) {
		int hour = (Integer)e.getNewValue();
		startDate.setHour(hour);
	}
	
	public void onChangeMin(ValueChangeEvent e) {
		int min = (Integer)e.getNewValue();
		startDate.setMin(min);
	}
	
//	public void onChangeEndDateWeekly(ValueChangeEvent e) {
//		int day = (Integer)e.getNewValue();
//		endDate.setDay(day);
//	}
	
	public void startDateValueChanged(ValueChangeEvent e) {
		boolean newValue = (Boolean)e.getNewValue();
		startDate.setActive(newValue);
	}
	
//	public void endDateValueChanged(ValueChangeEvent e) {
//		boolean newValue = (Boolean)e.getNewValue();
//		endDate.setActive(newValue);
//	}
	
	public void changeSchedulerMode (String mode) {		
		schedulerConfig.setMode(mode);
		if (IkrJobSchedulerConfig.NONE.equals(mode)) {
			activeSchedulerModeType = true;
			String modeType = schedulerConfig.getModeType();
			if (modeType==null || modeType.length()==0)
				modeType = IkrJobSchedulerConfig.DAY_CT;
			changeSchedulerModeType(modeType);
		}
		else {
			activeSchedulerModeType = false;
			// StartDate		
			startDate = changeDate(mode, "Start", schedulerConfig.getStartTime());
			
		if (customAttributeBean != null)
			customAttributeBean.schedulingModeChanged(mode);
			// EndDate
//			endDate = changeDate(mode, "End", schedulerConfig.getEndTime());
		}
	}
	
	private DateIntervalBean changeDate(String mode, String typeDateLabel, Calendar date) {
		DateIntervalBean dateBean = new DateIntervalBean("", mode);
		dateBean.setActive(true);
		String label = typeDateLabel + " Time";
		if (date != null) {
			int day = 0;
			if (IkrJobSchedulerConfig.WEEKLY.equals(mode)) {
				day = date.get(Calendar.DAY_OF_WEEK);
				label = typeDateLabel + " Date";
			}
			else if (IkrJobSchedulerConfig.MONTHLY.equals(mode)) {
				day = date.get(Calendar.DAY_OF_MONTH);
				label = typeDateLabel + " Date";
			}
			
			dateBean.setLabel(label);			
			dateBean.setDay(day);
			dateBean.setHour(date.get(Calendar.HOUR_OF_DAY));
			dateBean.setMin(date.get(Calendar.MINUTE));
		}
		
		return dateBean;
	}
	
	public void changeSchedulerModeType (String modeType) {
		schedulerConfig.setModeType(modeType);
		if (IkrJobSchedulerConfig.DAY_CT.equals(modeType))
			modeTypeLabel = "Days Count";
		else if (IkrJobSchedulerConfig.HOUR_CT.equals(modeType))
			modeTypeLabel = "Hours Count";
		else if (IkrJobSchedulerConfig.MIN_CT.equals(modeType))
			modeTypeLabel = "Minutes Count";
	}
	
	public void initJobSchedulingConfig() {		
		initDaysOfWeek();
		initSchedulerModeItems();	
		initSchedulerModeTypeItems();
		initDayOfMonth();
		initHour();
		initMin();
		
		changeSchedulerMode(schedulerConfig.getMode());
	}
	
	private void initSchedulerModeItems() {
		schedulerModeItems = new SelectItem[4];
		schedulerModeItems[0] =  new SelectItem(IkrJobSchedulerConfig.NONE, IkrJobSchedulerConfig.NONE);
		schedulerModeItems[1] =  new SelectItem(IkrJobSchedulerConfig.DAILY, IkrJobSchedulerConfig.DAILY);
		schedulerModeItems[2] =  new SelectItem(IkrJobSchedulerConfig.WEEKLY, IkrJobSchedulerConfig.WEEKLY);
		schedulerModeItems[3] =  new SelectItem(IkrJobSchedulerConfig.MONTHLY, IkrJobSchedulerConfig.MONTHLY);
	}
	
	private void initSchedulerModeTypeItems() {
		schedulerModeTypeItems = new SelectItem[3];
		schedulerModeTypeItems[0] =  new SelectItem(IkrJobSchedulerConfig.DAY_CT, IkrJobSchedulerConfig.DAY_CT);
		schedulerModeTypeItems[1] =  new SelectItem(IkrJobSchedulerConfig.HOUR_CT, IkrJobSchedulerConfig.HOUR_CT);
		schedulerModeTypeItems[2] =  new SelectItem(IkrJobSchedulerConfig.MIN_CT, IkrJobSchedulerConfig.MIN_CT);
	}
	
	private void initDaysOfWeek() {
		daysOfWeekItems = new SelectItem[7];
		daysOfWeekItems[0] =  new SelectItem(Calendar.MONDAY, "Monday");
		daysOfWeekItems[1] =  new SelectItem(Calendar.TUESDAY, "Tuesday");
		daysOfWeekItems[2] =  new SelectItem(Calendar.WEDNESDAY, "Wednesday");
		daysOfWeekItems[3] =  new SelectItem(Calendar.THURSDAY, "Thursday");
		daysOfWeekItems[4] =  new SelectItem(Calendar.FRIDAY, "Friday");
		daysOfWeekItems[5] =  new SelectItem(Calendar.SATURDAY, "Saturday");
		daysOfWeekItems[6] =  new SelectItem(Calendar.SUNDAY, "Sunday");
	}
	
	private void initDayOfMonth() {
		daysOfMonthItems = new SelectItem[31];
		for(int i=0;i<31;i++) {
			daysOfMonthItems[i] =  new SelectItem(i+1, String.valueOf(i+1));
		}
	}
	
	private void initHour() {
		hourItems = new SelectItem[24];
		for(int i=0;i<24;i++) {
			hourItems[i] =  new SelectItem(i, String.valueOf(i));
		}
	}
	
	private void initMin() {
		minItems = new SelectItem[60];
		for(int i=0;i<10;i++) {
			minItems[i] =  new SelectItem(i, "0" + String.valueOf(i));
		}
		for(int i=10;i<60;i++) {
			minItems[i] =  new SelectItem(i, String.valueOf(i));
		}
	}
	
	public void update() {
		// Reset Date 
		schedulerConfig.setStartTime(null);
		schedulerConfig.setEndTime(null);
		
		if (!IkrJobSchedulerConfig.NONE.equals(schedulerConfig.getMode())) {
			if (isDateValid(startDate))
				schedulerConfig.setStartTime(startDate.getCalendar());
			else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				schedulerConfig.setStartTime(cal);
			}
		}
	}
	
	public DateIntervalBean getStartDate() {
		return startDate;
	}

	public void setStartDate(DateIntervalBean startDate) {
		this.startDate = startDate;
	}

//	public DateIntervalBean getEndDate() {
//		return endDate;
//	}
//
//	public void setEndDate(DateIntervalBean endDate) {
//		this.endDate = endDate;
//	}
	
	public SelectItem[] getSchedulerModeItems() {
		return schedulerModeItems;
	}
	
	public void setSchedulerModeItems(SelectItem[] schedulerModeItems) {
		this.schedulerModeItems = schedulerModeItems;
	}

	public SelectItem[] getSchedulerModeTypeItems() {
		return schedulerModeTypeItems;
	}
	
//	public void setSchedulerModeTypeItems(SelectItem[] schedulerModeTypeItems) {
//		this.schedulerModeTypeItems = schedulerModeTypeItems;
//	}

	public SelectItem[] getDaysOfWeekItems() {
		return daysOfWeekItems;
	}
	
	public SelectItem[] getDaysOfMonthItems() {
		return daysOfMonthItems;
	}

	public SelectItem[] getHourItems() {
		return hourItems;
	}

	public SelectItem[] getMinItems() {
		return minItems;
	}

	private boolean isDateValid(DateIntervalBean date) {
		boolean ret = false;
		if (date != null && date.isActive()){			
			ret = true;
//		if (date != null && date.isActive()){			
//			if (date.getDay()==0 && date.getHour()==0 && date.getMin()==0)
//				ret = false;
//			else
//				ret = true;
		}		
		return ret;
	}

	public boolean isActiveSchedulerModeType() {
		return activeSchedulerModeType;
	}

	public String getModeTypeLabel() {
		return modeTypeLabel;
	}

	public void setCustomAttributeBean(JobSchedulerCustomAttribute customAttributeBean) {
		this.customAttributeBean = customAttributeBean;
	}	
}
