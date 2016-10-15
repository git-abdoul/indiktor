package com.fsi.monitoring.server.callback;

import java.rmi.RemoteException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.fsi.fwk.apps.connection.RemoteServiceConnector;
import com.fsi.monitoring.computeServer.services.ComputeServerService;
import com.fsi.monitoring.config.RealTimeBackBeanName;
import com.fsi.monitoring.config.RealTimeBackContext;
import com.fsi.monitoring.kpi.metrics.IkrValue;


public class AgentRTCallback  
implements RTCallback {
	
	private static final Logger LOG = Logger.getLogger(AgentRTCallback.class);	
	
	private ComputeServerService csService = null;
	
	public AgentRTCallback() throws Exception {
		try {
			RemoteServiceConnector computeServerConnector = (RemoteServiceConnector)RealTimeBackContext.getBean(RealTimeBackBeanName.computeServerConnector);
			csService = (ComputeServerService)computeServerConnector.getRemoteService();			
			if (csService == null)
				throw new Exception ("Compute Server Service initialization failed. Check if the Compute Server has been correctly started");
		} catch(Exception e){
			LOG.error("Inititialization failed while getting remote connection settings", e);		
			throw new Exception("Inititialization failed while getting remote connection settings", e);
		}
	}
	
	public void onMessage(Collection<IkrValue> ikrValues) {		
		try {	
			csService.processAndSend(ikrValues);			
		} catch (RemoteException e) {
			LOG.error("The invocation of Compute Server failed",e);
		} 
	}
	
}
