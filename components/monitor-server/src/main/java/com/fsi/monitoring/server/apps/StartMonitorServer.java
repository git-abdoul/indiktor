package com.fsi.monitoring.server.apps;

import com.fsi.fwk.exception.server.StartupServerException;
import com.fsi.monitoring.config.RealTimeBackContext;
import com.fsi.monitoring.server.CollectorManager;

public class StartMonitorServer {
	public static void main(String[] args) throws StartupServerException {
		try {
			System.out.println("Starting Collector Server ...");
			System.out.println("Loading application context ...");
			RealTimeBackContext.getContext().init("applicationContext-monitor-server.xml", "monitor-server");
			CollectorManager collectorManager = (CollectorManager)RealTimeBackContext.getBean("collectorManager");
			collectorManager.startManager(args[0]);
		} catch (Exception exc) {
			System.err.println("Impossible to start Collector Server : " + exc.getMessage());
			exc.printStackTrace();
			System.exit(0);
		}
	}
}