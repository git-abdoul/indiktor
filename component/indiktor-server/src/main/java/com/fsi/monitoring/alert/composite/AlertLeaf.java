package com.fsi.monitoring.alert.composite;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.bean.AlertBean;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.alert.workflow.AlertWorkflow.Status;


public class AlertLeaf
extends AlertItem {
	
	private static final long serialVersionUID = -3380794891892203621L;
	
	private static final Logger logger = Logger.getLogger(AlertLeaf.class);
	
	private List<AlertBean> alertBeans = null;
	private List<AlertBean> displayedAlertBeans = null;
	
	private static AlertBeanComparator comparator;
	
	static {
		comparator = new AlertBeanComparator();
	}
	
	public AlertLeaf(String title,
					 String type,
					 List<AlertBean> alertBeans) {
		super(title, type);
		this.alertBeans = alertBeans;		
		resetLevel();
	}
	
	private List<AlertBean> checkAlert( List<AlertBean> alertBeans) {
		List<AlertBean> alerts = new ArrayList<AlertBean>();
		for(AlertBean bean:alertBeans) {
			if(bean.getAlert().getAlertEvent() != null)
				alerts.add(bean);
		}
		return alerts;
	}
	
	private void resetLevel() {
		if (alertBeans.size() > 0) {
			level = ALERT_IDLE_DEFINITION;
		} else {
			level = NO_ALERT_DEFINITION;
		}		
	}
	
	protected int update() {
		resetLevel();
		
		if (alertBeans != null) {
			for(AlertBean alertBean : alertBeans) {
				if (alertBean.getAlert() != null) {
					level = Math.max(level, alertBean.getAlert().getState().getSeverity());
				}
			}
			
			Collections.sort(alertBeans,comparator);
			filterActiveAlertBeans();
		}
		return level;
	}
	
	public int getNbAlert() {
		return alertBeans.size();
	}
	
	public int getNbActiveAlert() {
		return displayedAlertBeans.size();
	}
	
	public Collection<AlertBean> getAlertBeans() {
		sortAlertTable();
		return displayedAlertBeans;
	}
	
	public Collection<AlertBean> getAllAlertBeans() {
		sortAlertTable();
		return alertBeans;
	}
	
	private void sortAlertTable() {
		if (alertBeans != null && alertBeans.size()>0) {
			Collections.sort(alertBeans, new Comparator<AlertBean>() {
				public int compare(AlertBean o1, AlertBean o2) {
					return (new Integer(o2.getAlertState().getSeverity())).compareTo(new Integer(o1.getAlertState().getSeverity()));
				}
			});				
		}
		if (displayedAlertBeans != null && displayedAlertBeans.size()>0) {
			Collections.sort(displayedAlertBeans, new Comparator<AlertBean>() {
				public int compare(AlertBean o1, AlertBean o2) {
					return (new Integer(o2.getAlertState().getSeverity())).compareTo(new Integer(o1.getAlertState().getSeverity()));
				}
			});				
		}
	}
	
	private void filterActiveAlertBeans() {
		displayedAlertBeans = new ArrayList<AlertBean>();
		for(AlertBean alertBean : alertBeans) {
			AlertWorkflow workflow = alertBean.getAlertState();
			if (workflow.getStatus() == Status.AUTO_UP) {
				displayedAlertBeans.add(alertBean);
			}
		}
	}
	
	static class AlertBeanComparator implements Comparator<AlertBean> {
	    public int compare(AlertBean o1, AlertBean o2) {
	    	Alert ao1 = o1.getAlert();
	    	Alert ao2 = o2.getAlert();
	    	
	    	if (ao1 == null && ao2 == null) {
	    		return 0;
	    	} else if (ao1 == null) {
	    		return -1;
	    	} else if (ao2 == null) {
	    		return +1;
	    	}
	    	
	    	AlertEvent event1 = ao1.getAlertEvent();
	    	AlertEvent event2 = ao2.getAlertEvent();
	    	
	    	if (event1 != null && event2 != null) {
	    		return event1.getEventDate().compareTo(event2.getEventDate());
	    	}
	    	
	    	return 1;
	    }
	}
}
