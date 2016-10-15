package com.fsi.monitoring.applet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class AppletChartOnDemandProxy extends UnicastRemoteObject implements
		IAppletChartOnDemandProxy {
	private static final long serialVersionUID = -3497901130661271397L;
	
	private ConcurrentHashMap<String, IAppletChartOnDemand> subscribers;

	protected AppletChartOnDemandProxy() throws RemoteException {
		super();		
		subscribers = new ConcurrentHashMap<String, IAppletChartOnDemand>();
	}	

	public void subscribe(String id, IAppletChartOnDemand chartOnDemand)
			throws RemoteException {
		synchronized (subscribers) {
			if (!subscribers.containsKey(id)) {
				subscribers.put(id, chartOnDemand);
			}
		}
	}

	public void unsubscribe(String id) throws RemoteException {
		synchronized (subscribers) {
			if (subscribers.containsKey(id)) {
				subscribers.remove(id);
			}
		}		
	}
	
	public IAppletChartOnDemand getApplet(String id) {
		return subscribers.get(id);
	}

}
