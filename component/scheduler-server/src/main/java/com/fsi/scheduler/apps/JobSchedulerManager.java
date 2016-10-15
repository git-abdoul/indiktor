package com.fsi.scheduler.apps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminEvent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsAdminConsole;
import com.fsi.scheduler.config.SchedulerServerContext;
import com.fsi.scheduler.jobs.IkrJobScheduler;
import com.fsi.scheduler.jobs.config.JobSchedulerFactory;

public class JobSchedulerManager extends JmsAdminConsole implements Observer {
	private static final Logger logger = Logger.getLogger(JobSchedulerManager.class);
	
	private DataModelPM dataModelPM;
	
	private LogicalEnv logicalEnv = null;
	private Map<Long,JobTaskRunner> jobSchedulers;
	
	public void init() {
		initJms();
	}
	
	public boolean isAlive() {
		return true;
	}

	public AdminComponent getComponentType() {
		return AdminComponent.JOB_TASK;
	}
	
	public void newMsgReceived(Collection<IkrJmsMessage> messages) {
		for (IkrJmsMessage msg : messages) {		
			AdminRequest request = (AdminRequest)msg;		
			if (request.getComponentType()== AdminComponent.JOB_TASK) {				
				switch (request.getCommand()) {
					case START:
						startJobScheduler(request.getComponentId(), true);
						break;
						
					case STOP:
						break;
						
					case ADD:
						JobSchedulerFactory factory = (JobSchedulerFactory)SchedulerServerContext.getBean("jobSchedulerFactory");
						try {
							IkrJobScheduler jobScheduler = factory.createJob((int)request.getComponentId());
							createJobTaskRunner(jobScheduler);
						} catch (Exception e) {
							logger.error("Error while trying to create the Job Scheduler " + request.getComponentId() + " " + e.getMessage(), e);
						}
						break;
						
					case REMOVE:
						JobTaskRunner runner = jobSchedulers.get(request.getComponentId());
						if (runner==null)
							continue;						
						if (runner.getJobScheduler().isRunning())
							runner.getJobScheduler().cancel();
						while (runner.getJobScheduler().isRunning()) {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								logger.error(e.getMessage(), e);
							}
						}						
						runner.getJobScheduler().updateStatus(ComponentStatus.REMOVE);
						runner.getJobScheduler().addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "REMOVED"));
						jobSchedulers.remove(request.getComponentId());
						sendEvent(runner.getJobScheduler().getLog());
						break;
						
					case UPDATE:
						runner = jobSchedulers.get(request.getComponentId());
						if (runner==null)
							continue;
						if (runner.getJobScheduler().isRunning())
							runner.getJobScheduler().cancel();
						while (runner.getJobScheduler().isRunning()) {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								logger.error(e.getMessage(), e);
							}
						}
						jobSchedulers.remove(request.getComponentId());
						factory = (JobSchedulerFactory)SchedulerServerContext.getBean("jobSchedulerFactory");	
						try {
							IkrJobScheduler jobScheduler = factory.createJob((int)request.getComponentId());
							createJobTaskRunner(jobScheduler);
						} catch (Exception e) {
							logger.error("Error while trying to create the Job Scheduler " + request.getComponentId() + " " + e.getMessage(), e);
						}
						break;
						
					case HEARTBEAT:
						// TODO
						break;
						
					case GLOBAL_STATUS:
						runner = jobSchedulers.get(request.getComponentId());
						if (runner!=null)
							runner.getJobScheduler().notifyCurrentState();
						break;
			
					default:
						break;
				}
			}
		}
		
	}
	
	public void startManager(String env) throws Exception {		
		try {
			logicalEnv = dataModelPM.getLogicalEnv(env);			
			if (logicalEnv == null) {
				System.out.println("Ikr Monitor Environment " +  env + " unknown. Please set a correct Monitor Environment");
				System.exit(1);
			}
			
			jobSchedulers = new HashMap<Long, JobSchedulerManager.JobTaskRunner>();
			
			logger.info("Initializing Scheduler Server's Objects ...");
			System.out.println("Initializing Scheduler Server's Objects ...");
			
			JobSchedulerFactory factory = (JobSchedulerFactory)SchedulerServerContext.getBean("jobSchedulerFactory");	
			Map<Integer,IkrJobScheduler> jobInstances = factory.createJobs(logicalEnv.getId());		
			for(IkrJobScheduler jobScheduler : jobInstances.values()) {
				createJobTaskRunner(jobScheduler);		
			}
		} catch(Exception exc) {
			throw new Exception("An Exception occured while initializing the Scheduler Server", exc);
		}
	}
	
	private void createJobTaskRunner(IkrJobScheduler jobScheduler) {
		jobScheduler.addObserver(this);
		jobSchedulers.put((long)jobScheduler.getId(), new JobTaskRunner(jobScheduler));
		String msg = "Job Scheduler created: <" + jobScheduler.getId() + " : " + jobScheduler.getName() + ">";
		logger.info(msg);
		if (jobScheduler.isActive()) {
			logger.info(msg + " | Activated | It will start automatically");
			startJobScheduler(jobScheduler.getId(), false);		
		}
		else {
			logger.info(msg + " | DEACTIVATED | It WON'T START");
		}		
	}
	
	private void startJobScheduler(long jobId, boolean runOnce) {	
		JobTaskRunner runner = jobSchedulers.get(jobId);		
		if (runner!=null) {
			String jobName = "<" + runner.getJobScheduler().getId() + " : " + runner.getJobScheduler().getName() + "> ";
			if (!runner.getJobScheduler().isRunning()) {	
				runner.setRunOnce(runOnce);
				System.out.println("Starting Job Scheduler " + jobName);
				logger.info("Starting Job Scheduler " + jobName);
				runner.start();
			}
			else {
				logger.info("Job Scheduler " + jobName + " is already started");
			}
		}
		else {
			logger.info("Job Scheduler ID=" + jobId + " can't be found");
		}
	}
	
	public void update(Observable o, Object arg) {
		if (o instanceof IkrJobScheduler) {
			if (arg != null) {
				if (arg instanceof AdminEvent) {
					sendEvent((AdminEvent)arg);
				}
			}
		}
	}
	
	private void sendEvent(AdminEvent event) {
		try {
			Collection<IkrJmsMessage> events = new ArrayList<IkrJmsMessage>();
			events.add(event);
			eventLogProducer.publish(events);
		} catch (Exception e) {
			logger.error("Error occured while publishing admin request : " + e.getMessage(), e);
		}
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}

	class JobTaskRunner extends Thread {
		private IkrJobScheduler jobScheduler;

		public JobTaskRunner(IkrJobScheduler jobScheduler) {
			super();
			this.jobScheduler = jobScheduler;
		}

		public void run() {
			String msg = "<" + jobScheduler.getId() + " : " + jobScheduler.getName() + "> ";
			try {
				jobScheduler.start();				
				System.out.println("Job Task " + msg + " STARTED");
				logger.info("Job Task " + msg + " STARTED");
			} catch (Exception exc) {	
				String exMsg = "Impossible to start Job Task " + msg;
				System.out.println(exMsg);
				logger.error(exMsg, exc);
			}			
		}

		public IkrJobScheduler getJobScheduler() {
			return jobScheduler;
		}

		public void setRunOnce(boolean runOnce) {
			this.jobScheduler.setStartOnce(runOnce);
		}		
	}
}
