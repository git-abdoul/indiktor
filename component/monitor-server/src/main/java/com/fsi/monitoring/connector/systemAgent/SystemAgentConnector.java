package com.fsi.monitoring.connector.systemAgent;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rmi.RmiConnector;
import com.fsi.monitoring.system.dto.SystemInfo;

public interface SystemAgentConnector
extends RmiConnector {

	String getHostname();
	SystemInfo updateInfo(String monitorType, String subType) throws ConnectorException;
	void register(String category, SystemAgentCallback callback);
}
