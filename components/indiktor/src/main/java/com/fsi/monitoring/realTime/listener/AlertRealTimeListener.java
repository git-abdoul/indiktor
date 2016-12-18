package com.fsi.monitoring.realTime.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.apps.connection.RemoteServiceConnector;
import com.fsi.monitoring.RealTimeValue;
import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.bean.AlertBean;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.computeServer.services.ComputeServerService;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.user.UserPM;


public class AlertRealTimeListener extends RealtimeValueListener {	
	private static final Logger logger = Logger.getLogger(AlertRealTimeListener.class);			

	private Map<Long, AlertBean> alertBeanMap;
	private List<AlertBean> alertBeans;
	
	private UserPM userPM;
	private BeanPM beanPM;
	
	private RemoteServiceConnector computeServerConnector;
	
	public AlertRealTimeListener() {
		alertBeanMap = new HashMap<Long, AlertBean>();
		alertBeans = new ArrayList<AlertBean>();
	}	
	
	public void setComputeServiceConnector(RemoteServiceConnector computeServerConnector) {
		this.computeServerConnector = computeServerConnector;
	}
	
	public AlertBean getAlertBean(long alertDefinitionId) {
		AlertBean res = alertBeanMap.get(alertDefinitionId);
		if (res == null) {
			res = new AlertBean(alertDefinitionId, beanPM , userPM);
			alertBeanMap.put(alertDefinitionId, res);
			alertBeans.add(res);
		}		
		return res;
	}
	
	public void updateAlertBean(long alertDefinitionId, Alert alert, boolean onDeletion, AlertEvent event) {
		AlertBean alertBean = getAlertBean(alertDefinitionId);
		alertBean.updateAlert(alert, onDeletion, event);
	}
	
	public List<AlertBean> getAlertBeans() {
		return alertBeans;
	}

	@Override
	protected void notifRealTimeSubscribers(Collection<? extends RealTimeValue> realtimeValues) {
		for (RealTimeValue rtValue : realtimeValues) { 
			Alert alert = (Alert)rtValue;
			long alertDefinitionId = alert.getValueDefinitionId();
			AlertBean alertBean = getAlertBean(alertDefinitionId);
			alertBean.updateAlert(alert, false, null);
		}
	}

	@Override
	protected void initRealTimeValues() {
		try {
			ComputeServerService csService = (ComputeServerService)computeServerConnector.getRemoteService();
			
			if (csService!=null) {
				Collection<Alert> alerts = csService.getAlerts();
				
				if (alerts != null) {
					notifRealTimeSubscribers(alerts);
				}
			}
		} catch(Exception exc) {
			logger.error("Error while initializing alerts RT", exc);
		}
	}

	public void setUserPM(UserPM userPM) {
		this.userPM = userPM;
	}

	public void setBeanPM(BeanPM beanPM) {
		this.beanPM = beanPM;
	}	
}
