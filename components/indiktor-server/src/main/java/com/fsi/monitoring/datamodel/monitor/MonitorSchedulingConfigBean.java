package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;
import com.fsi.monitoring.util.DateIntervalBean;

public class MonitorSchedulingConfigBean
implements Serializable {

	private static final long serialVersionUID = 4743074674367768379L;
	
	private SelectItem[] schedulerTypeItems;
	private SelectItem[] schedulerModeItems;
	private SelectItem[] daysOfWeekItems;
	private SelectItem[] daysOfMonthItems;
	private SelectItem[] hourItems;
	private SelectItem[] minItems;
	
	private DateIntervalBean startDate;
	private DateIntervalBean endDate;
	
	private String startDateLabel;
	private String endDateLabel;
	
	private boolean disableSchedulerMode;
	private boolean disableCaptureDelay;	
	
	private IkrMonitorSchedulerConfig schedulerConfig = null;
	MetricDomainConfigAttributesBean monitorAttributeBean = null;
	
	public MonitorSchedulingConfigBean(MonitorConfig monitorConfig, MetricDomainConfigAttributesBean monitorAttributeBean) {
		this.schedulerConfig = monitorConfig.getSchedulerConfig();		
		this.monitorAttributeBean = monitorAttributeBean;
		initSchedulerConfig();
	}
	
	public DateIntervalBean getStartDate() {
		return startDate;
	}

	public void setStartDate(DateIntervalBean startDate) {
		this.startDate = startDate;
	}

	public DateIntervalBean getEndDate() {
		return endDate;
	}

	public void setEndDate(DateIntervalBean endDate) {
		this.endDate = endDate;
	}
	
	public IkrMonitorSchedulerConfig getSchedulerConfig() {
		return schedulerConfig;
	}

	public boolean isDisableSchedulerMode() {
		return disableSchedulerMode;
	}

	public boolean isDisableCaptureDelay() {
		return disableCaptureDelay;
	}
	
	public SelectItem[] getSchedulerTypeItems() {
		return schedulerTypeItems;
	}
	
	public SelectItem[] getSchedulerModeItems() {
		return schedulerModeItems;
	}
	
	public SelectItem[] getDaysOfWeekItems() {
		return daysOfWeekItems;
	}

	public void setDaysOfWeekItems(SelectItem[] daysOfWeekItems) {
		this.daysOfWeekItems = daysOfWeekItems;
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
	
	public void changeSchedulerMode(ValueChangeEvent e) {
		String newValue = (String)e.getNewValue();
		schedulerConfig.setMode(newValue);
		schedulerConfig.setEndTime(null);
		schedulerConfig.setStartTime(null);
		updateSchedulerTypeView();
		updateSchedulerModeView();
	}
	
	public void onChangeStartDateWeekly(ValueChangeEvent e) {
		int day = (Integer)e.getNewValue();
		startDate.setDay(day);
	}
	
	public void onChangeEndDateWeekly(ValueChangeEvent e) {
		int day = (Integer)e.getNewValue();
		endDate.setDay(day);
	}
	
	public void startDateValueChanged(ValueChangeEvent e) {
		boolean newValue = (Boolean)e.getNewValue();
		startDate.setActive(newValue);
		if(!schedulerConfig.getType().equalsIgnoreCase("one shot") && !schedulerConfig.getMode().equalsIgnoreCase("none"))
			if (e.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
				e.setPhaseId(PhaseId.INVOKE_APPLICATION);
				e.queue();
				return;
			}
			if(newValue)
				endDate.setActive(newValue);
	}
	
	public void endDateValueChanged(ValueChangeEvent e) {
		boolean newValue = (Boolean)e.getNewValue();
		endDate.setActive(newValue);
		if (e.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			e.setPhaseId(PhaseId.INVOKE_APPLICATION);
			e.queue();
			return;
		}
		if(newValue)
			startDate.setActive(newValue);
	}
	
	public void changeSchedulerType(ValueChangeEvent e) {
		String newValue = (String)e.getNewValue();
		schedulerConfig.setType(newValue);
		schedulerConfig.setEndTime(null);
		schedulerConfig.setStartTime(null);
		updateSchedulerTypeView();
		updateSchedulerModeView();
		monitorAttributeBean.init();
	}
	
	public void changeSchedulerMode(String newValue) {
		schedulerConfig.setMode(newValue);
		schedulerConfig.setEndTime(null);
		schedulerConfig.setStartTime(null);
		updateSchedulerTypeView();
		updateSchedulerModeView();
	}
	
	public void onChangeStartDateWeekly(int day) {
		startDate.setDay(day);
	}
	
	public void onChangeEndDateWeekly(int day) {
		endDate.setDay(day);
	}
	
	public void onChangeDaysOfMonthStart(ValueChangeEvent e) {
		int day = (Integer)e.getNewValue();
		startDate.setDay(day);
	}
	
	public void onChangeHourStart(ValueChangeEvent e) {
		int hour = (Integer)e.getNewValue();
		startDate.setHour(hour);
	}
	
	public void onChangeMinStart(ValueChangeEvent e) {
		int min = (Integer)e.getNewValue();
		startDate.setMin(min);
	}
	
	public void onChangeDaysOfMonthEnd(ValueChangeEvent e) {
		int day = (Integer)e.getNewValue();
		endDate.setDay(day);
	}
	
	public void onChangeHourEnd(ValueChangeEvent e) {
		int hour = (Integer)e.getNewValue();
		endDate.setHour(hour);
	}
	
	public void onChangeMinEnd(ValueChangeEvent e) {
		int min = (Integer)e.getNewValue();
		endDate.setMin(min);
	}
	
	public void startDateValueChanged(boolean newValue) {
		startDate.setActive(newValue);
	}
	
	public void endDateValueChanged(boolean newValue) {
		endDate.setActive(newValue);
	}
	
	public void changeSchedulerType(String newValue) {
		schedulerConfig.setType(newValue);
		schedulerConfig.setEndTime(null);
		schedulerConfig.setStartTime(null);
		updateSchedulerTypeView();
		updateSchedulerModeView();
	}
	
	private void updateSchedulerModeView () {
		String schedulerMode = schedulerConfig.getMode();
		if (IkrMonitorSchedulerConfig.NONE.equals(schedulerMode)) {
			startDate.setDisableSelection(true);
			startDate.setActive(false);
			endDate.setDisableSelection(true);
			endDate.setActive(false);
		}
		else {
			startDate.setType(schedulerMode);
			startDate.setActive(true);
			Calendar start = schedulerConfig.getStartTime();
			int day = 0;
			if (start != null) {			
				startDateLabel = "Start Time";			
				if (IkrMonitorSchedulerConfig.WEEKLY.equals(schedulerMode)) {
					startDateLabel = "Start Date";
					day = start.get(Calendar.DAY_OF_WEEK);
				}
				else if (IkrMonitorSchedulerConfig.MONTHLY.equals(schedulerMode)) {
					startDateLabel = "Start Date";
					day = start.get(Calendar.DAY_OF_MONTH);
				}
				startDate.setLabel(startDateLabel);			
				startDate.setDay(day);
				startDate.setHour(start.get(Calendar.HOUR_OF_DAY));
				startDate.setMin(start.get(Calendar.MINUTE));
//				startDate.setActive(true);
			}
//			else
//				startDate.setActive(false);
			
			endDate.setType(schedulerMode);
			endDate.setActive(true);
			Calendar end = schedulerConfig.getEndTime();
			if (end != null) {
				endDateLabel = "End Time";
				if (IkrMonitorSchedulerConfig.WEEKLY.equals(schedulerMode)) {
					endDateLabel = "End Date";
					day = end.get(Calendar.DAY_OF_WEEK);
				}
				else if (IkrMonitorSchedulerConfig.MONTHLY.equals(schedulerMode)) {
					endDateLabel = "End Date";
					day = end.get(Calendar.DAY_OF_MONTH);
				}
				endDate.setLabel(endDateLabel);			
				endDate.setDay(day);
				endDate.setHour(end.get(Calendar.HOUR_OF_DAY));
				endDate.setMin(end.get(Calendar.MINUTE));
//				endDate.setActive(true);
			}
//			else
//				endDate.setActive(false);
		}
	}
	
	private void updateSchedulerTypeView() {
		startDate = new DateIntervalBean("Start Time", "");		
		endDate = new DateIntervalBean("End Time", "");
		String SchedulerType = schedulerConfig.getType();
		if (IkrMonitorSchedulerConfig.ONE_SHOT.equals(SchedulerType)) {
			disableSchedulerMode = false;
			schedulerConfig.setMode(schedulerConfig.getMode());
			disableCaptureDelay = true;
			startDate.setDisableSelection(false);
			startDate.setActive(false);
			endDate.setDisableSelection(true);
		}
		else if (IkrMonitorSchedulerConfig.RECURRING.equals(SchedulerType)) {
			disableSchedulerMode = false;
			schedulerConfig.setMode(schedulerConfig.getMode());
			disableCaptureDelay = false;
			startDate.setDisableSelection(false);
			startDate.setActive(false);
			endDate.setDisableSelection(false);
			endDate.setActive(false);		
		}	
	}
	
	private void initSchedulerConfig() {
		initDaysOfWeek();
		initSchedulerTypeItems();
		initSchedulerModeItems();
		initDayOfMonth();
		initHour();
		initMin();
		
		updateSchedulerTypeView();
		updateSchedulerModeView();		
	}	
	
	private void initSchedulerTypeItems() {
		schedulerTypeItems = new SelectItem[2];
		schedulerTypeItems[0] =  new SelectItem(IkrMonitorSchedulerConfig.ONE_SHOT, IkrMonitorSchedulerConfig.ONE_SHOT);
		schedulerTypeItems[1] =  new SelectItem(IkrMonitorSchedulerConfig.RECURRING, IkrMonitorSchedulerConfig.RECURRING);
	}
	
	private void initSchedulerModeItems() {
		schedulerModeItems = new SelectItem[4];
		schedulerModeItems[0] =  new SelectItem(IkrMonitorSchedulerConfig.NONE, IkrMonitorSchedulerConfig.NONE);
		schedulerModeItems[1] =  new SelectItem(IkrMonitorSchedulerConfig.DAILY, IkrMonitorSchedulerConfig.DAILY);
		schedulerModeItems[2] =  new SelectItem(IkrMonitorSchedulerConfig.WEEKLY, IkrMonitorSchedulerConfig.WEEKLY);
		schedulerModeItems[3] =  new SelectItem(IkrMonitorSchedulerConfig.MONTHLY, IkrMonitorSchedulerConfig.MONTHLY);
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
		
		if (isDateValid(startDate))
			schedulerConfig.setStartTime(startDate.getCalendar());
		else {
			if (!IkrMonitorSchedulerConfig.NONE.equals(schedulerConfig.getMode())) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				schedulerConfig.setStartTime(cal);
			}
		}
		
		if (isDateValid(endDate))
			schedulerConfig.setEndTime(endDate.getCalendar());
	}
	
	private boolean isDateValid(DateIntervalBean date) {
		boolean ret = false;
		if (date!=null&&date.isActive()){
//			if (date.getDay()==0 && date.getHour()==0 && date.getMin()==0)
//				ret = false;
//			else
//				ret = true;
			ret = true;
		}		
		return ret;
	}
}
