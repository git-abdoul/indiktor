package com.fsi.monitoring.datamodel.jobScheduler;

import java.io.Serializable;

import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;

public abstract class JobSchedulerCustomAttribute implements Serializable {
	private static final long serialVersionUID = -5811105255517789418L;
	
	private String jspPageDirectory;

	public abstract void schedulingModeChanged(String newMode);
	public abstract String getJspPageName();	
	public abstract void updateAttributes();	
	public abstract void initSchedulerConfig(IkrJobSchedulerConfig schedulerConfig);
	
	protected void setJspPageDirectory (String jspPageDirectory) {
		this.jspPageDirectory = jspPageDirectory;
	}
	
	public String getJspPage() {
		return jspPageDirectory + "/" + getJspPageName();
	}
}
