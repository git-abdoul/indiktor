package com.fsi.monitoring.system.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.fsi.monitoring.system.dto.SystemInfo;

public interface ISystemAgentCallback extends Remote {
	public void onMessage(SystemInfo info) throws RemoteException;;
}
