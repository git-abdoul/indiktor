package com.fsi.scheduler.jobs.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;
import com.fsi.scheduler.jobs.IkrJobScheduler;


public class JobSchedulerFactory {
	protected final static Logger LOG = Logger.getLogger(JobSchedulerFactory.class);
	
	private DataModelPM dataModelPM;
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}
	
	public void initFactory() {}

	public Map<Integer,IkrJobScheduler> createJobs(int logicalEnvId) throws Exception {
		Map<Integer,IkrJobScheduler> tasks = new HashMap<Integer, IkrJobScheduler>();
		
		Map<Integer, IkrJobSchedulerConfig> jobConfigs = dataModelPM.getJobSchedulerConfigs(logicalEnvId);			
		for (IkrJobSchedulerConfig jobConfig: jobConfigs.values()) {
			Class<IkrJobScheduler> jobClass;
			IkrJobScheduler job = null;
			try {
				IkrJobSchedulerStaticDomain jobStaticDomain = dataModelPM.getJobSchedulerStaticDomain(jobConfig.getJobStaticDomainId());
				jobClass = (Class<IkrJobScheduler>)Class.forName("com.fsi.scheduler.jobs.IkrJobScheduler_"+jobStaticDomain.getJobSchedulerType());
				job = jobClass.newInstance();					
				job.setConfig(jobConfig);
				tasks.put(jobConfig.getId(),job);
			} catch (ClassNotFoundException e) {
				throw new Exception("Error occured while creating Job" + "<"+jobConfig.getName()+"> : " + e);
			} catch (InstantiationException e) {
				throw new Exception("Error occured while Job Task" + "<"+jobConfig.getName()+"> : " + e);
			} catch (IllegalAccessException e) {
				throw new Exception("Error occured while Job Task" + "<"+jobConfig.getName()+"> : " + e);
			}
		}
		
		return tasks;
	}
	
	public IkrJobScheduler createJob(int jobId) throws Exception {
		IkrJobSchedulerConfig jobConfig = dataModelPM.getJobSchedulerConfig(jobId);			
		Class<IkrJobScheduler> jobClass;
		IkrJobScheduler job = null;
		try {
			IkrJobSchedulerStaticDomain jobStaticDomain = dataModelPM.getJobSchedulerStaticDomain(jobConfig.getJobStaticDomainId());
			jobClass = (Class<IkrJobScheduler>)Class.forName("com.fsi.scheduler.jobs.IkrJobScheduler_"+jobStaticDomain.getJobSchedulerType());
			job = jobClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new Exception("Error occured while creating Task" + "<"+jobConfig.getName()+"> : " + e);
		} catch (InstantiationException e) {
			throw new Exception("Error occured while creating Task" + "<"+jobConfig.getName()+"> : " + e);
		} catch (IllegalAccessException e) {
			throw new Exception("Error occured while creating Task" + "<"+jobConfig.getName()+"> : " + e);
		}
		
		job.setConfig(jobConfig);		
		return job;
	}
}
