package com.fsi.scheduler.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.scheduling.ScheduleIterator;
import com.fsi.fwk.scheduling.Scheduler;
import com.fsi.fwk.scheduling.SchedulerTask;
import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.admin.IkrEventLog;
import com.fsi.monitoring.admin.adminCtrl;
import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;

public abstract class IkrJobScheduler extends SchedulerTask implements adminCtrl {
	private static final Logger logger = Logger.getLogger(IkrJobScheduler.class);	
	private static final int LOG_MAX_CAPACITY = 10;
	
	protected IkrJobSchedulerConfig config; 
	private boolean isInitDone = false;

	protected Scheduler startJobScheduler;
	
	private IkrJobSchedulerStaticDomain jobSchedulerStaticDomain;
	private boolean startOnce;
	private boolean running;
	
	private Date startTime; 
	protected List<IkrAdminLogging> eventLogs;
	protected Map<String, String> eventStats;
	protected ComponentStatus status;
	
	private String logicalEnv;
	
	public IkrJobScheduler() {
		startOnce = false;
		eventLogs = new ArrayList<IkrAdminLogging>();
		eventStats = new HashMap<String, String>();
		status = ComponentStatus.NOT_RUNNING;
	}
	
	public void addLog(IkrAdminLogging log) {
		if (eventLogs.size()>=LOG_MAX_CAPACITY) {
			eventLogs.remove(0);
		}
		eventLogs.add(log);
	}
	
	public void addStat(String attr, String value) {
		eventStats.put(value, value);
	}
	
	public void updateStatus (ComponentStatus status) {
		this.status = status;
	}
	
	@Override
	public void run() {
		running = true;
		startTime = new Date(); 
		status = ComponentStatus.NOTHING_TO_REPORT;
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Job Scheduler In Progress ..."));
		notifyEventLog();
		try {
			process();
		} catch (Exception e) {
			running = false;
			logger.error(e.getMessage(), e);
			status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
		}
		running = false;
		status = ComponentStatus.NOT_RUNNING;
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Job Scheduler Finished"));
		notifyEventLog();
	}	
	
	public abstract void process() throws Exception;
	public abstract void init() throws Exception;
	
	public void stop() {}
	
	public void start() throws Exception {
		if (!isInitDone) {
			try {
				init();
				isInitDone = true;
			} catch (Exception e) {
				logger.error("Error while initializing the Job Scheduler <" + getName() + ":" + jobSchedulerStaticDomain.getJobSchedulerType() + ">", e);
				status = ComponentStatus.ERROR_OCCURED;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
				notifyEventLog();
				throw new Exception("Error while initializing the Job Scheduler <" + getName() + ":" + jobSchedulerStaticDomain.getJobSchedulerType() + ">", e);
			}
		}	
		
		if (startOnce) {
			run();
		}
		else {
			ScheduleIterator startIterator = config.getStartIterator();
			startJobScheduler = new Scheduler();
			startJobScheduler.schedule(this, startIterator);
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO,"Initialized and Scheduled Datetime is " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(startIterator.next())));
			notifyEventLog();
		}		
	}
	
//	private void initConfig() throws Exception {
//		try {
//			jobSchedulerStaticDomain = PMFactory.getDataModelPM().getJobSchedulerStaticDomain(config.getJobStaticDomainId());
//			init();
//		} catch (PersistenceException e) {
//			throw new Exception(e);
//		}
//	}
	
	public int getId() {
		return config.getId();
	}
	
	public String getName() {
		return config.getName();
	}
	
	public boolean isActive() {
		return config.isActive();
	}

	public IkrJobSchedulerConfig getConfig() {
		return config;
	}

	public void setConfig(IkrJobSchedulerConfig config) {
		this.config = config;
		try {
			LogicalEnv env = PMFactory.getDataModelPM().getLogicalEnv(config.getLogicalEnvId());
			if (env!=null) {
				logicalEnv = env.getName();					
			}
			else {
				status = ComponentStatus.NOT_RUNNING;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "The Task is not linked to a logical Environment"));
			}
			
			jobSchedulerStaticDomain = PMFactory.getDataModelPM().getJobSchedulerStaticDomain(config.getJobStaticDomainId());
			if (jobSchedulerStaticDomain==null){
				status = ComponentStatus.NOT_RUNNING;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "The Task static data not found"));				
			}
			
		} catch(Exception exc) {
			status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, exc.getMessage()));
			logger.error("Impossible to configure Job Scheduler", exc);
			notifyEventLog();
		}
		notifyEventLog();
	}	
	
	public void setStartOnce(boolean startOnce) {
		this.startOnce = startOnce;
	}
	
	public boolean isRunning() {
		return running;
	}	

	public String getLogicalEnv() {
		return logicalEnv;
	}

	protected void notifyEventLog() {
		IkrEventLog log = getLog();
		if (log !=null) {
			this.setChanged();
			
			this.notifyObservers(log);
		}
	}
	
	public void notifyCurrentState() {
		notifyEventLog();
	}
	
	public IkrEventLog getLog() {
		return new IkrEventLog(getId(), (jobSchedulerStaticDomain!=null)?jobSchedulerStaticDomain.getName():"",startTime, AdminComponent.JOB_TASK, status, getName(), getLogicalEnv(), eventLogs, eventStats);
	}
}
