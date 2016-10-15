package com.fsi.monitoring.system.server;

import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;

import com.fsi.monitoring.system.dto.SystemInfo;

public class SystemMonitoringServer extends UnicastRemoteObject implements SystemMonitoringRemote {
	private static final Logger LOG = Logger.getLogger(SystemMonitoringServer.class);
	private static final long serialVersionUID = 4275859583171023280L;
	
	/**
	 * @uml.property  name="monitor"
	 * @uml.associationEnd  
	 */
	private MonitoringProcess monitor = null;
	/**
	 * @uml.property  name="callbacks"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="com.fsi.monitoring.system.server.ISystemAgentCallback"
	 */
	private List<ISystemAgentCallback> callbacks;

	public SystemMonitoringServer() throws RemoteException {
		super();	
		callbacks = Collections.synchronizedList(new ArrayList<ISystemAgentCallback>());
	}
	
	public synchronized void bind(Registry registry) throws AccessException, RemoteException, AlreadyBoundException {
		registry.bind(SystemMonitoringServer.class.getName(), this);		
	}

	public void startClient(){
		monitor = new MonitoringProcess();
		MonitoredHost jstatdClient = null;
		try {
			jstatdClient = monitor.getJstatdClient();
		}catch (UnknownHostException e) {
			String msg = "Error while connecting to JStatd Daemon : " + e;
			System.out.println(msg);
			System.out.println("Some Java Process Information will be skip. See System Agent documentation for more information");
			LOG.info(msg);
			LOG.info("Some Java Process Information will be skip. See System Agent documentation for more information");
		} catch (MonitorException e) {
			String msg = "Cannot connect to JStatd Daemon : " + e;
			System.out.println(msg);
			System.out.println("Some Java Process Information will be skip. See System Agent documentation for more information");
			LOG.info(msg);
			LOG.info("Some Java Process Information will be skip. See System Agent documentation for more information");
		} 
		
		monitor.getClient(jstatdClient);
	}

	public SystemInfo monitor(String monitorType, String subType) throws RemoteException {
		try {
			return monitor.monitor(monitorType, subType);
		} catch (Throwable e) {
			LOG.error("Error when loading creating the System Agent RMI Server : " + e.getMessage());
			String message = e.getMessage();
			throw new RemoteException("Could not retrieve the Server Stats : " +
					(message == null ? "unknown reason" : message));
		}
	}	

	public void subscribe(ISystemAgentCallback callback) throws RemoteException{
		LOG.debug("Receive subscription from " + callback);		
		synchronized (callbacks) {
			if (!callbacks.contains(callback)) {
				callbacks.add(callback);
			}
		}
	}
	
	public void notifyInfo(SystemInfo info) {
		synchronized (callbacks) {
			for (ISystemAgentCallback callback : callbacks) {
				try {
					callback.onMessage(info);
				} catch (RemoteException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}			
	}
	
	public boolean isAlive() throws RemoteException {
		return true;
	}		
	
}
