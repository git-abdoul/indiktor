package com.fsi.monitoring.config;

import com.fsi.fwk.apps.config.BeanName;

public enum RealTimeBackBeanName 
implements BeanName {

	computeServerConnector, connectorManager, dataSynchronizationMgr;
	
	public String getBeanName() {
		return this.name();
	}		
	
}
