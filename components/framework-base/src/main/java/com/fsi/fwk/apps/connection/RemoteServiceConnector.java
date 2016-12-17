package com.fsi.fwk.apps.connection;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;


public class RemoteServiceConnector
extends AbstractService {

	private static final Logger LOG = Logger.getLogger(RemoteServiceConnector.class);	
	
	private Remote remoteService;	
	
	public void initConnection() {
		try {
			Registry registry = LocateRegistry.getRegistry(hostName, port);
			remoteService = (Remote) registry.lookup(serviceName);			
			String message = "RemoteService binded: " + serviceName;
			LOG.info(message);
			System.out.println(message);
		} catch(Exception exc) {
			String error = "Impossible to bind remoteService: " + serviceName;
			LOG.fatal(error);
			LOG.fatal(exc);
			LOG.fatal("Make sure the Compute Server is started. Otherwise, stop this server and start the Compute Server before lauching this server");
			System.out.println(error);
			System.out.println(exc.getMessage());
		} 
	}
	
	public Remote getRemoteService() {
		return remoteService;
	}	

}
