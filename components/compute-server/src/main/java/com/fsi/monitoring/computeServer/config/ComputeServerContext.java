package com.fsi.monitoring.computeServer.config;

import com.fsi.fwk.apps.config.AbstractApplicationContext;

public class ComputeServerContext 
extends AbstractApplicationContext {

	public static ComputeServerContext getContext() {
		if (singleton == null) {
			singleton = new ComputeServerContext();
		}	
		return (ComputeServerContext)singleton;
	}
}