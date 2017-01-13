package com.fsi.monitoring.system.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.fsi.monitoring.system.dto.SystemInfo;

public interface SystemMonitoringRemote extends Remote {
	public SystemInfo monitor(String monitorType, String subType) throws RemoteException;
	public void subscribe(ISystemAgentCallback callback) throws RemoteException;
	public boolean isAlive() throws RemoteException;
}
