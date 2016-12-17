package com.fsi.fwk.apps.connection;

import java.rmi.Naming;
import java.rmi.Remote;

import org.apache.log4j.Logger;

public class ServicePublisher 
extends AbstractService {
	
	private static final Logger LOG = Logger.getLogger(ServicePublisher.class);		
	
	private Remote service = null;
	
	public void setService(Remote service) {
		this.service = service;
	}
	
	public void publishService() 
	throws Exception {
		try {			
			String objectURL = "rmi://"+hostName+":"+port+"/"+serviceName;
			Naming.rebind(objectURL, service);	
			
			String message = "Service published: " + serviceName;
			LOG.info(message);
			System.out.println(message);
		} catch (Exception exc) {
			String error = "Impossible to publish service:" + serviceName;
			System.out.println(error);
			LOG.fatal(error,exc);
			throw exc;
		}
	}

}
