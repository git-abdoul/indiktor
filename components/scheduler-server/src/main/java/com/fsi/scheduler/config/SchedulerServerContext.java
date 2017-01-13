package com.fsi.scheduler.config;

import com.fsi.fwk.apps.config.AbstractApplicationContext;

public class SchedulerServerContext extends AbstractApplicationContext {
	
	public static SchedulerServerContext getContext() {
		if (singleton == null) {
			singleton = new SchedulerServerContext();
		}	
		return (SchedulerServerContext)singleton;
	}

}
