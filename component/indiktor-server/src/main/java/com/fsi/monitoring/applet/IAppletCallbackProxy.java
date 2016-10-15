package com.fsi.monitoring.applet;

import java.rmi.Remote;
import java.util.Collection;
import java.util.List;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;

public interface IAppletCallbackProxy extends Remote {
	public void subscribeToIkrCategory(Collection<String> ids, IAppletCallback callback) throws java.rmi.RemoteException;
	public void subscribeToIkrDefinition(Collection<Long> ids, IAppletCallback callback) throws java.rmi.RemoteException;
	public void unsubscribeToIkrCategory(IAppletCallback callback) throws java.rmi.RemoteException;
	public void unsubscribeToIkrDefinition(IAppletCallback callback) throws java.rmi.RemoteException;
	public List<IkrValueBean> getIkrDefinitionValues(long id) throws java.rmi.RemoteException;
	public List<IkrValueBean> getIkrCategoryValues(long id) throws java.rmi.RemoteException;
}
