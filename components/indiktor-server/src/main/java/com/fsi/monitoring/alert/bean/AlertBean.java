package com.fsi.monitoring.alert.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.alert.workflow.AlertCommentEvent;
import com.fsi.monitoring.alert.workflow.AlertComputeEvent;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.datamodel.bean.RealTimeBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.user.UserPM;
import com.fsi.monitoring.util.MessageBundleLoader;


public class AlertBean implements RealTimeBean {
	private static final long serialVersionUID = 2868316287684385764L;
	
	private Alert alert;
	
	private Collection<AlertCommentBean> 		alertCommentBeans;
	private Collection<AlertComputeEventBean>   alertEventBeans;

	private UserPM userPM;
	private BeanPM beanPM;
	
	private long alertDefinitionId;
	
	public AlertBean(long alertDefinitionId, BeanPM beanPM, UserPM userPM) {
		this.alertDefinitionId = alertDefinitionId;
		this.beanPM = beanPM;
		this.userPM = userPM;
		
		alertCommentBeans = new ArrayList<AlertCommentBean>();
		alertEventBeans = new ArrayList<AlertComputeEventBean>();
	}	
	
	public AlertDefinitionBean getAlertDefinitionBean() {
		return beanPM.getAlertDefinitionBean(alertDefinitionId);
	}
	
	public long getAlertDefinitionId() {
		return alertDefinitionId;
	}
	
	public Alert getAlert() {
		return alert;
	}
	
	public String getUrl() {
		AlertWorkflow alertState = getAlertState();
		
		return "/" + MessageBundleLoader.getMessage("alert.img." + alertState.getSeverity());
	}	

	public String getColorStr() {
		AlertWorkflow alertState = getAlertState();

		return MessageBundleLoader.getMessage("alert.color." + alertState.getSeverity());
	}	
	
	public AlertWorkflow getAlertState() {
		AlertWorkflow alertState = null;
		if (alert == null) {
			alertState = AlertWorkflow.DOWN;
		} else {
			alertState = alert.getState();
		}
		return alertState;
	}
	
	public Collection<AlertCommentBean> getAlertCommentBeans() {
		List<AlertCommentBean> events = new ArrayList<AlertCommentBean>(alertCommentBeans);
		Collections.sort(events, new Comparator<AlertCommentBean>() {
			public int compare(AlertCommentBean o1, AlertCommentBean o2) {
				return o2.getEvent().getEventDate().compareTo(o1.getEvent().getEventDate());
			}
		});		
		return events;
	}
	
	public Collection<AlertComputeEventBean> getAlertEventBeans() {
		List<AlertComputeEventBean> events = new ArrayList<AlertComputeEventBean>(alertEventBeans);
		Collections.sort(events, new Comparator<AlertComputeEventBean>() {
			public int compare(AlertComputeEventBean o1, AlertComputeEventBean o2) {
				return o2.getEvent().getEventDate().compareTo(o1.getEvent().getEventDate());
			}
		});		
		return events;
	}
	
	public synchronized void updateAlert(Alert alert, boolean OnDeletion, AlertEvent event) {
		this.alert = alert;
		
		if(!OnDeletion) {
			for (AlertComputeEvent computeEvent : alert.getComputeEvents()) {
				AlertComputeEventBean bean = new AlertComputeEventBean(computeEvent);
				if (!alertEventBeans.contains(bean))
					alertEventBeans.add(bean);
			}
			
			for (AlertCommentEvent commentEvent : alert.getCommentEvents()) {
				AlertCommentBean bean = new AlertCommentBean(commentEvent,userPM);
				if (!alertCommentBeans.contains(bean))
					alertCommentBeans.add(bean);
			}
		}
		else if(OnDeletion) {
			if (event instanceof AlertCommentEvent) {
				AlertCommentBean bean = new AlertCommentBean((AlertCommentEvent)event,userPM);
				if (alertCommentBeans.contains(bean)) {
					alertCommentBeans.remove(bean);
				}
			}
			else {
				AlertComputeEventBean bean = new AlertComputeEventBean((AlertComputeEvent)event);
				if (alertEventBeans.contains(bean)) {
					alertEventBeans.remove(bean);
				}
			}
		}
	}
}
