package com.fsi.monitoring.datamodel.jobScheduler;

import java.util.HashSet;
import java.util.Set;

import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;

public class JobSchedulerConfigBean {
	private IkrJobSchedulerConfig jobSchedulerConfig;
	private IkrJobSchedulerStaticDomain jobSchedulerStaticDomain;
	
	private LogicalEnv logicalEnv;
	
	private boolean selected;
	
	private Set<String> searchIndexes;
	
	public JobSchedulerConfigBean(LogicalEnv logicalEnv, IkrJobSchedulerConfig jobSchedulerConfig, IkrJobSchedulerStaticDomain jobSchedulerStaticDomain) {
		super();
		this.logicalEnv = logicalEnv;
		this.jobSchedulerConfig = jobSchedulerConfig;
		this.jobSchedulerStaticDomain = jobSchedulerStaticDomain;
		
		this.initSearchIndexes();
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();
		searchIndexes.add(jobSchedulerConfig.getName().toLowerCase());
		searchIndexes.add(logicalEnv.getName().toLowerCase());
		searchIndexes.add(jobSchedulerStaticDomain.getJobSchedulerType().toLowerCase());
		searchIndexes.add(jobSchedulerConfig.getMode().toLowerCase());
		if (jobSchedulerStaticDomain.getDescription()!=null && jobSchedulerStaticDomain.getDescription().length()>0)
			searchIndexes.add(jobSchedulerStaticDomain.getDescription().toLowerCase());
		searchIndexes.add(jobSchedulerStaticDomain.getName().toLowerCase());
	}
	
	public IkrJobSchedulerConfig getJobSchedulerConfig() {
		return jobSchedulerConfig;
	}
	
	public IkrJobSchedulerStaticDomain getJobSchedulerStaticDomain() {
		return jobSchedulerStaticDomain;
	}
	
	public String getName() {
		return jobSchedulerConfig.getName();
	}
	
	public String getMode() {
		return jobSchedulerConfig.getMode();
	}
	
	public String getJobType() {
		return jobSchedulerStaticDomain.getJobSchedulerType();
	}
	
	public int getId() {
		return jobSchedulerConfig.getId();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}

	public void setLogicalEnv(LogicalEnv logicalEnv) {
		this.logicalEnv = logicalEnv;
	}

	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}		
}
