package com.fsi.monitoring.applet;

import java.rmi.Remote;

public interface IAppletChartOnDemandProxy extends Remote {
	public void subscribe(String id, IAppletChartOnDemand chartOnDemand) throws java.rmi.RemoteException;
	public void unsubscribe(String id) throws java.rmi.RemoteException;
}
