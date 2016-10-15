package com.fsi.monitoring.connector.http;

import java.util.List;

import org.apache.http.NameValuePair;

import com.fsi.monitoring.connector.Connector;
import com.fsi.monitoring.connector.ConnectorException;

public interface HttpConnector 
extends Connector {
	
	String getBodyResponse(List<NameValuePair> qparams, String service) 
	throws ConnectorException;
	
}
