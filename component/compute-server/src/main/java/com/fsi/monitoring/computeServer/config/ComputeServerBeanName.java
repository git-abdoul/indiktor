package com.fsi.monitoring.computeServer.config;

import com.fsi.fwk.apps.config.BeanName;

public enum ComputeServerBeanName 
implements BeanName {

	realTimeFrontConnector, computeServerPublisher;
	
	public String getBeanName() {
		return this.name();
	}		
	
}
