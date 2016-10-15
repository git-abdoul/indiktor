package com.fsi.monitoring.computeServer.apps;

import org.apache.log4j.Logger;

import com.fsi.monitoring.computeServer.config.ComputeServerContext;


public class StartComputeServer {
	private static final Logger LOG = Logger.getLogger(StartComputeServer.class);
	
	public static void main(String[] args) {		
		
		try {
			System.out.println("Starting ComputeServer ...");
			System.out.println("Loading application context ...");
			
			ComputeServerContext.getContext().init("applicationContext-compute-server.xml", "compute-server");
	
			System.out.println("Application context loaded");
			LOG.info("Application context loaded");
					
			LOG.info("ComputeServer ready");
			System.out.println("ComputeServer ready");
		} catch (Exception exc) {
			System.err.println("Impossible to start Compute Server : " + exc.getMessage());
			exc.printStackTrace();
			System.exit(0);
		}
	}	
}
