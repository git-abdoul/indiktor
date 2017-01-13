package com.fsi.monitoring.connector;

import java.util.Collection;

import com.fsi.monitoring.admin.adminCtrl;

public interface Connector extends adminCtrl{

	long getId();
	String getName();
	String getDescription();
	String getType();
	String getConnectorContext();
	
	void addListener(ConnectorListener listener);
	void removeListener(ConnectorListener listener);
	void removeAllListener();
	Collection<ConnectorListener> getListeners();
	
	void reportFailure(String message) throws ConnectorException;
	void reportFailure(Exception exception) throws ConnectorException;
	void reportFailure(String message, Exception exception) throws ConnectorException ;
}
