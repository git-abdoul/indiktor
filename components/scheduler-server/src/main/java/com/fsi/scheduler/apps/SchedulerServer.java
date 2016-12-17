package com.fsi.scheduler.apps;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.log4j.Logger;

import com.fsi.monitoring.cache.ScheduledTaskCacheEventListener;
import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.scheduler.config.SchedulerServerContext;
import com.fsi.scheduler.jobs.IkrJobScheduler;
import com.fsi.scheduler.jobs.config.JobSchedulerFactory;

public class SchedulerServer implements CacheEventListener {
	private static final Logger LOG = Logger.getLogger(CacheEventListener.class);
	
	private Map<Integer, LogicalEnv> logicalEnvs = null;
	
	private Map<Integer,IkrJobScheduler> jobSchedulers;
	
	public SchedulerServer(String env) {		
		String[] envs = null;
		if (env!=null && env.length()>0)
			envs = env.split(",");
		
		try {
			
			if (envs== null || envs.length==0) {
				System.out.println("At least one Logical Environment needs to be define");
				System.exit(1);
			}
			
			ScheduledTaskCacheEventListener cacheEventListener = ScheduledTaskCacheEventListener.getInstance();
			
			jobSchedulers = new HashMap<Integer, IkrJobScheduler>();
			logicalEnvs = new HashMap<Integer, LogicalEnv>();
			
			DataModelPM dataModelPM = PMFactory.getDataModelPM();
			
			Map<Integer, LogicalEnv> allEnvs = dataModelPM.getLogicalEnvs();
			
			for(LogicalEnv logicalEnv : allEnvs.values()) {
				if (env.contains(logicalEnv.getName())) {
					logicalEnvs.put(logicalEnv.getId(), logicalEnv);
					cacheEventListener.addObserver(logicalEnv.getId(), this);
				}
			}				
			
			createJobSchedulers();						
			
		} catch(Exception exc) {
			System.out.println("Impossible to start Scheduler Server");
			LOG.fatal("Impossible to start Scheduler Server", exc);
			System.exit(0);
		}		
	}
	
	public void start(){
		for(IkrJobScheduler jobScheduler : jobSchedulers.values()) {		
			if (jobScheduler != null) {
				startJobScheduler(jobScheduler);
			}	
		}
	}
	
	private void startJobScheduler(IkrJobScheduler jobScheduler) {
		if (jobScheduler != null && jobScheduler.isActive()) {
			try {
//				jobScheduler.execute(false);
			} catch (Exception exc) {
				String msg = "Impossible to schedule Job:" + jobScheduler.getId();
				System.out.println(msg);
				LOG.error(msg, exc);
			}
		}
	}	
	
	private void createJobSchedulers() throws Exception {
		JobSchedulerFactory factory = (JobSchedulerFactory)SchedulerServerContext.getBean("jobSchedulerFactory");
		
		for (LogicalEnv logicalEnv : logicalEnvs.values()) {
			Map<Integer,IkrJobScheduler> jobs = factory.createJobs(logicalEnv.getId());
			jobSchedulers.putAll(jobs);
		}
		
		for(IkrJobScheduler jobScheduler: jobSchedulers.values()) {
			IkrJobSchedulerConfig taskConfig = jobScheduler.getConfig();
			int id = taskConfig.getId();
			String msg = "Job Scheduler created: " + id + ":<" + jobScheduler.getName() + "> [OK]";
			System.out.println(msg);
			LOG.info(msg);
			
			jobSchedulers.put(jobScheduler.getId(), jobScheduler);	
		}
	}	
	

	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void notifyElementEvicted(Ehcache arg0, Element arg1) {
		// TODO Auto-generated method stub
	}

	public void notifyElementExpired(Ehcache arg0, Element arg1) {
		// TODO Auto-generated method stub

	}

	public void notifyElementPut(Ehcache arg0, Element arg1)
			throws CacheException {
		System.out.println("--- JOB SCHEDULER CREATED ---");
		try {
			Element element = (Element)arg1;
			IkrJobSchedulerConfig jobSchedulerConfig = (IkrJobSchedulerConfig)element.getValue();
		
			if (logicalEnvs.containsKey(jobSchedulerConfig.getLogicalEnvId())) {				
				JobSchedulerFactory factory = (JobSchedulerFactory)SchedulerServerContext.getBean("jobSchedulerFactory");
				IkrJobScheduler jobScheduler = factory.createJob(jobSchedulerConfig.getId());
				
				if (jobScheduler != null) {
					jobSchedulers.put(jobScheduler.getId(), jobScheduler);	
					startJobScheduler(jobScheduler);
				}
			}
		} catch (Exception exc) {
			LOG.error("Impossible to update Job Scheduler creation", exc);
		}
	}

	public void notifyElementRemoved(Ehcache arg0, Element arg1)
			throws CacheException {
		try {
			Element element = (Element)arg1;
			IkrJobSchedulerConfig jobSchedulerConfig = (IkrJobSchedulerConfig)element.getValue();
			int jobSchedulerId = jobSchedulerConfig.getId();
			
			IkrJobScheduler jobScheduler = jobSchedulers.get(jobSchedulerId);
			if (jobScheduler != null) {
				jobScheduler.cancel();
			}
			jobSchedulers.remove(jobSchedulerId);	
		} catch (Exception exc) {
			LOG.error("Impossible to update Jon Scheduler removal", exc);
		}

	}

	public void notifyElementUpdated(Ehcache arg0, Element arg1)
			throws CacheException {
		System.out.println("--- JOB SCHEDULER UPDATED ---");
		try {
			Element element = (Element)arg1;			
			IkrJobSchedulerConfig jobSchedulerConfig = (IkrJobSchedulerConfig)element.getValue();
			int jobSchedulerId = jobSchedulerConfig.getId();
			
			IkrJobScheduler jobScheduler = jobSchedulers.get(jobSchedulerId);
			if (jobScheduler != null) {
				jobScheduler.cancel();
			}
			
			if (logicalEnvs.containsKey(jobSchedulerConfig.getLogicalEnvId())) {				
				JobSchedulerFactory factory = (JobSchedulerFactory)SchedulerServerContext.getBean("jobSchedulerFactory");
				IkrJobScheduler newJobScheduler = factory.createJob(jobSchedulerConfig.getId());
				
				if (newJobScheduler != null) {
					jobSchedulers.put(newJobScheduler.getId(), newJobScheduler);	
					startJobScheduler(newJobScheduler);
				}
			}
		} catch (Exception exc) {
			LOG.error("Impossible to update Job Scheduler", exc);
		}

	}

	public void notifyRemoveAll(Ehcache arg0) {
		// TODO Auto-generated method stub
	}

	public Object clone() throws java.lang.CloneNotSupportedException {
		return null;
	}	

}
