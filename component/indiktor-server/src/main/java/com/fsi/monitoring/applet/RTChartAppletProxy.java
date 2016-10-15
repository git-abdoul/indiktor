package com.fsi.monitoring.applet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;

public class RTChartAppletProxy {
	private static final Logger logger = Logger.getLogger(RTChartAppletProxy.class);
	
	public int port;	
	public static AppletCallbackProxy callbackProxy;
	public static AppletChartOnDemandProxy chartOnDemand;
	
	public RTChartAppletProxy(){}	
	
	public void init() {
		try {
			callbackProxy = new AppletCallbackProxy();
			chartOnDemand = new AppletChartOnDemandProxy();
			Registry reg = LocateRegistry.createRegistry(port);
			reg.bind(IAppletCallbackProxy.class.getName(), callbackProxy);
			reg.bind(IAppletChartOnDemandProxy.class.getName(), chartOnDemand);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (AlreadyBoundException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void update(IkrValueBean ikrValueBean) {
		if (callbackProxy != null)
			callbackProxy.send(ikrValueBean);
	}

	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getHostname() {
		String hostname = "localhost";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return hostname;
	}
	
	public AppletChartOnDemandProxy getChartOnDemandProxy() {
		return chartOnDemand;
	}
}
