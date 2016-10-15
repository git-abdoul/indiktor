package com.fsi.monitoring.alert.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.monitoring.alert.AlertComputeResolution;
import com.fsi.monitoring.alert.workflow.AlertComputeEvent;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.bean.factory.BeanPMFactory;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.util.FacesUtils;

public class AlertComputeEventBean implements Serializable {
	private static final long serialVersionUID = -1538570026259814569L;

	private AlertComputeEvent event;
	private Collection<AlertComputeResolutionBean> alertComputeResolutionBeans;
	
	public AlertComputeEventBean(AlertComputeEvent event) {
		this.event = event;
	}
	
	public AlertComputeEvent getEvent() {
		return event;
	}
	
	public Collection<AlertComputeResolutionBean> getAlertComputeResolutionBeans() {
		if (alertComputeResolutionBeans == null) {
			initAlertComputeResolutionBeans();
		}
		return alertComputeResolutionBeans;
	}
	
	private synchronized void initAlertComputeResolutionBeans() {
		alertComputeResolutionBeans = new ArrayList<AlertComputeResolutionBean>();
		
		for (AlertComputeResolution alertComputeResolution :  event.getAlertComputeResolutions()) {
			Collection<IkrValueBean> beans = new ArrayList<IkrValueBean>();

			for (Long ikrValueId : alertComputeResolution.getIkrValueIds()) {
				BeanPM beanPM = (BeanPMFactory)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
				IkrValueBean ikrValueBean = beanPM.getIkrValueBean(ikrValueId);
				beans.add(ikrValueBean);
			}
			
			if (alertComputeResolution.getNonPersistantValues()!=null) {
				for (IkrValue ikrValue : alertComputeResolution.getNonPersistantValues()) {
					BeanPM beanPM = (BeanPMFactory)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
					IkrDefinitionBean ikrDefBean = beanPM.getIkrDefinitionBean(ikrValue.getValueDefinitionId());
					beans.add(new IkrValueBean(ikrDefBean, ikrValue));
				}
			}			
			
			AlertComputeResolutionBean acrBean = 
						new AlertComputeResolutionBean(alertComputeResolution,beans);
			
			alertComputeResolutionBeans.add(acrBean);
		}
	}
	
	public int hashCode() {
		return event.hashCode();
	}	
	
	public boolean equals(Object obj) { 
		AlertEvent other = ((AlertComputeEventBean)obj).getEvent();
		 return (event.getId() == other.getId());
	}
}
