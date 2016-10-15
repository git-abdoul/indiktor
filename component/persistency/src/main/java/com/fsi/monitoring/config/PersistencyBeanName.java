package com.fsi.monitoring.config;

import com.fsi.fwk.apps.config.BeanName;

public enum PersistencyBeanName 
implements BeanName {

	dataModelPM,monitoringPM,alertPM,userPM,reportPM,histoPM,ikrValueIdGenerator,alertDefinitionIdGenerator,domainConfigIdGenerator;
	
	public String getBeanName() {
		return this.name();
	}	
}
