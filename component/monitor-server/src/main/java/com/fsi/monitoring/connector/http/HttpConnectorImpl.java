package com.fsi.monitoring.connector.http;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.AbstractConnector;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.HttpConnectorConfig;


public class HttpConnectorImpl
extends AbstractConnector
implements HttpConnector {

	private static final Logger logger = Logger.getLogger(HttpConnectorImpl.class);

	private HttpClient httpclient;
	
	private HttpConnectorConfig httpConnectorConfig;
	
	public HttpConnectorImpl(HttpConnectorConfig httpConnectorConfig) {
		super(httpConnectorConfig);
		this.httpConnectorConfig = httpConnectorConfig;
	}

	public String getBodyResponse(List<NameValuePair> qparams, String service) 
	throws ConnectorException {
		checkStatus();
		
		if (httpclient == null) {
			reportFailure("HTTP Client not initialized");
		}
		
		String responseBody = null;
		
		String hostname = httpConnectorConfig.getConnectorContext();
		int port = httpConnectorConfig.getPort();
		
		try {
	        URI uri = URIUtils.createURI("http", 
	        							 hostname, 
	        							 port, 
	        							 service,
	        							 URLEncodedUtils.format(qparams, "UTF-8"),
	        							 null);        
	        logger.debug("URI=" + uri);
	        
	        HttpGet httpget = new HttpGet(uri);
	        logger.debug(httpget.getMethod());
	        logger.debug(httpget.getParams());
	        logger.debug(httpget.getRequestLine());
	
	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
	        responseBody = httpclient.execute(httpget, responseHandler); 
		} catch (Exception exc) {
			String message = "Error while executing http service: " + service + " host=" + hostname + " port=" + port;
			Exception exception = new Exception(message, exc);
			reportFailure(exception);
		}
		
		if (responseBody == null) {
			String message = "Http service, responseBody null: " + service + " host=" + hostname + " port=" + port;
			reportFailure(message);
		}
		
		return responseBody;
	}
	
	public void closeConnection() {
		httpclient.getConnectionManager().shutdown();
	}

	public void openConnection()
	throws Exception {
        httpclient = new DefaultHttpClient();
	}

	public String getConnectorContext() {
		return httpConnectorConfig.getConnectorContext();
	}
}
