package com.fsi.monitoring.config;


import com.fsi.fwk.apps.config.AbstractApplicationContext;


public class RealTimeBackContext extends AbstractApplicationContext{
	
	public static RealTimeBackContext getContext() {
		if (singleton == null) {
			singleton = new RealTimeBackContext();
		}	
		return (RealTimeBackContext)singleton;
	}
}
