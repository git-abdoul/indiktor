package com.fsi.toolkits.config;

import com.fsi.fwk.apps.config.AbstractApplicationContext;

public class ToolkitContext extends AbstractApplicationContext {
	
	public static ToolkitContext getContext() {
		if (singleton == null) {
			singleton = new ToolkitContext();
		}	
		return (ToolkitContext)singleton;
	}
}
