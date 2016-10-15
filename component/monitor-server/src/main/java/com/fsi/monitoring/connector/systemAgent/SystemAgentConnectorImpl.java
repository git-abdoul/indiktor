package com.fsi.monitoring.connector.systemAgent;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.monitoring.connector.rmi.RmiConnectorImpl;
import com.fsi.monitoring.system.dto.SystemInfo;
import com.fsi.monitoring.system.server.SystemMonitoringRemote;

public class SystemAgentConnectorImpl 
extends RmiConnectorImpl 
implements SystemAgentConnector, Serializable {
	private static final long serialVersionUID = 2007554444165588426L;

	private static final Logger logger = Logger.getLogger(SystemAgentConnector.class);		
	
	private SystemMonitoringRemote saRemoteService = null;	
	private SystemAgentConnectorConfig connectorConfig;
	private SystemAgentListener listener;
	
	public SystemAgentConnectorImpl(SystemAgentConnectorConfig connectorConfig) {
		super(connectorConfig);
		this.connectorConfig = connectorConfig;
		try {
			listener = new SystemAgentListener();
		} catch (RemoteException e) {
			logger.error("Error while initializing SystemAgentListener : " + e.getMessage(), e);
		}
	}

	public SystemInfo updateInfo(String monitorType, String subType) 
	throws ConnectorException {	
		checkStatus();
		
		if (saRemoteService == null) {
			reportFailure("SystemAgent RemoteService null");
		}
		
		SystemInfo res = null;
		
		try {
			res = saRemoteService.monitor(monitorType, subType);
	
			if (res == null) {
				logger.error("SystemInfo null for " + monitorType + "--" + subType);
			}
		} catch (RemoteException rexc) {
			reportFailure(rexc);
		}
		
		return res;
	}
	
	public void register(String category, SystemAgentCallback callback) {
		if (listener != null)
			listener.register(category, callback);
	}	
	
	@Override
	protected void openConnection() throws Exception {
		super.openConnection();	
		saRemoteService = (SystemMonitoringRemote)getRemoteService();
		
		// Connection actual test
		SystemInfo siTest = saRemoteService.monitor("SYSTEM_CPU_GLOBAL", null);
		if (siTest == null) {
			throw new Exception("SystemInfo Test null");
		}
		
		if (listener != null)
			saRemoteService.subscribe(listener);
	}

	public String getHostname() {
		return connectorConfig.getConnectorContext();
	}	
}
