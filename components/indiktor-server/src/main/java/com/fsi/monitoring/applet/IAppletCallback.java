package com.fsi.monitoring.applet;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;

public interface IAppletCallback extends Remote {
	public void onNewDataReceived(IkrValueBean ikrValueBean) throws RemoteException;
}
