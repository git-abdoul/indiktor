package com.fsi.monitoring.connector.systemAgent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.fsi.monitoring.system.dto.SystemInfo;
import com.fsi.monitoring.system.server.ISystemAgentCallback;

public class SystemAgentListener extends UnicastRemoteObject implements ISystemAgentCallback {
	private static final long serialVersionUID = 4269816974990355997L;	
	
	private ConcurrentHashMap<String, List<SystemAgentCallback>> callbacks;
	
	public SystemAgentListener() throws RemoteException {
		super();
		callbacks = new ConcurrentHashMap<String, List<SystemAgentCallback>>();
	}

	public void onMessage(SystemInfo info) throws RemoteException {
		synchronized (callbacks) {
			List<SystemAgentCallback> subscribers = callbacks.get(info.getCategory());
			if (subscribers != null) {
				for (SystemAgentCallback callback : subscribers) {
					callback.onMessage(info);
				}
			}
		}			

	}

	public void register(String category , SystemAgentCallback callback) {		
		synchronized (callbacks) {
			List<SystemAgentCallback> subscribers = callbacks.get(category);
			if (subscribers == null) {
				subscribers = new ArrayList<SystemAgentCallback>();
				callbacks.put(category, subscribers);
			}
			subscribers.add(callback);
		}
	}

}
