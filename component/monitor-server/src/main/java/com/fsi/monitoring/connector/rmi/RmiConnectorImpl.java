package com.fsi.monitoring.connector.rmi;

import java.rmi.Naming;
import java.rmi.Remote;

import com.fsi.monitoring.connector.AbstractConnector;
import com.fsi.monitoring.connector.RmiConnectorConfig;

public class RmiConnectorImpl
extends AbstractConnector 
implements RmiConnector {

	private RmiConnectorConfig connectorConfig;
	
	private Remote remoteService;
	
	public RmiConnectorImpl(RmiConnectorConfig connectorConfig) {
		super(connectorConfig);
		this.connectorConfig = connectorConfig;
	}

	@Override
	protected void closeConnection() throws Exception {
		// TODO Auto-generated method stub
	}
	
	public String getConnectorContext() {
		return connectorConfig.getConnectorContext();
	}

	@Override
	protected void openConnection() throws Exception {
		String key = connectorConfig.getConnectorContext() + ":" + connectorConfig.getPort();
		remoteService = Naming.lookup("//" + key + "/" + connectorConfig.getServiceName());
		if (remoteService==null)
			throw new Exception("An Error occured while trying to register to the registry : " + "//" + key + "/" + connectorConfig.getServiceName());
	}
	
	protected Remote getRemoteService() {
		return remoteService;
	}
}
