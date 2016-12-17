package com.fsi.fwk.apps.connection;


import org.apache.log4j.Logger;


/**
 * Class used to hold service configuration data.
 * @author aurelien.gonnay
 *
 */
public abstract class AbstractService {

	private static final Logger LOG = Logger.getLogger(AbstractService.class);
	
	protected String hostName;
	protected String serviceName;
	protected int port;
	
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostname) {
		this.hostName = hostname;
	}
//	public String getClassName() {
//		return classname;
//	}

	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
//	public int getPort() {
//		return port;
//	}
	public void setPort(int port) {
		this.port = port;
	}
}
