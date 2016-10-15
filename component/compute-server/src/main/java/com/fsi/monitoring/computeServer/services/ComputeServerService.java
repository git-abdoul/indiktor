package com.fsi.monitoring.computeServer.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public interface ComputeServerService extends Remote {
	
	public void processAndSend(Collection<IkrValue> values) throws RemoteException;

//	public void addAlertComment(long alertDefinitionId, AlertCommentEvent event)
//	throws RemoteException;
	
	public Collection<Alert> getAlerts() throws RemoteException;
	
}
