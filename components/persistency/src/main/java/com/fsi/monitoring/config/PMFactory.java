package com.fsi.monitoring.config;

import com.fsi.fwk.apps.config.AbstractApplicationContext;
import com.fsi.monitoring.histo.HistoPM;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;

public class PMFactory {

	public static DataModelPM getDataModelPM() {
		return (DataModelPM) AbstractApplicationContext.getBean(PersistencyBeanName.dataModelPM);
	}
	
	public static MonitoringPM getMonitoringPM() {
		return (MonitoringPM)AbstractApplicationContext.getBean(PersistencyBeanName.monitoringPM);
	}	
	
	public static HistoPM getHistoPM() {
		return (HistoPM)AbstractApplicationContext.getBean(PersistencyBeanName.histoPM);
	}		
}
