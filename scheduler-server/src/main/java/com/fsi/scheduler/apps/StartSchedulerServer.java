package com.fsi.scheduler.apps;

import com.fsi.scheduler.config.SchedulerServerContext;

public class StartSchedulerServer {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Starting Scheduler Server ...");
			System.out.println("Loading application context ...");
			SchedulerServerContext.getContext().init("applicationContext-scheduler-server.xml", "scheduler-server");	
			JobSchedulerManager jobSchedulerManager = (JobSchedulerManager)SchedulerServerContext.getBean("jobSchedulerManager");
			jobSchedulerManager.startManager(args[0]);
		} catch (Exception exc) {
			System.err.println("Impossible to start Scheduler Server");
			exc.printStackTrace();
			System.exit(0);
		}
	}
}
