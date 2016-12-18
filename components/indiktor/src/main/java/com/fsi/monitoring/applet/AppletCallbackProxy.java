package com.fsi.monitoring.applet;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;

public class AppletCallbackProxy extends UnicastRemoteObject implements	IAppletCallbackProxy {
	private static final Logger logger = Logger.getLogger(AppletCallbackProxy.class);
	private static final long serialVersionUID = 9028392633686552990L;
	
	private static final int MAX_CAPACITY = 120;
	
	ConcurrentHashMap<String, List<IAppletCallback>> ikrCategoryCallbacks;
	ConcurrentHashMap<Long, List<IAppletCallback>> ikrDefinitionCallbacks;
	
	
	ConcurrentHashMap<Long, List<IkrValueBean>> ikrDefinitionValues;
	ConcurrentHashMap<Long, List<IkrValueBean>> ikrCategoryValues;
	
	protected AppletCallbackProxy() throws RemoteException {
		super();		
		ikrCategoryCallbacks = new ConcurrentHashMap<String, List<IAppletCallback>>();
		ikrDefinitionCallbacks = new ConcurrentHashMap<Long, List<IAppletCallback>>();
		ikrDefinitionValues = new ConcurrentHashMap<Long, List<IkrValueBean>>();
		ikrCategoryValues = new ConcurrentHashMap<Long, List<IkrValueBean>>();
	}
	
	public void send(IkrValueBean ikrValueBean) {
		// send ikrDef
		sendIkrDefinitionValue(ikrValueBean);
		
		// send ikrCat
		sendIkrCategoryValue(ikrValueBean);
	}
	
	private void sendIkrDefinitionValue(IkrValueBean ikrValueBean) {
		long ikrDef = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getId();
		synchronized (ikrDefinitionValues) {
			List<IkrValueBean> values = ikrDefinitionValues.get(ikrDef);
			if(values == null) {
				values = new ArrayList<IkrValueBean>();
				ikrDefinitionValues.put(ikrDef, values);
			}			
			if (values.size() == MAX_CAPACITY)
				values.remove(0);
			values.add(ikrValueBean);
			System.out.println("AppletCallbackProxy::sendIkrDefinitionValue >> " + ikrDef + " added");
		}
		
		synchronized (ikrDefinitionCallbacks) {			
			List<IAppletCallback> callbacks = ikrDefinitionCallbacks.get(ikrDef);
			if(callbacks != null && callbacks.size()>0) {
				for(IAppletCallback callback : callbacks) {
					try {
						callback.onNewDataReceived(ikrValueBean);
					} catch (ConnectException e) {
						logger.error(e.getMessage(), e);
						try {
							unsubscribeToIkrDefinition(callback);
						} catch (RemoteException e1) {
							logger.error(e1.getMessage(), e1);
						}
					} catch (RemoteException e) {
						logger.error(e.getMessage(), e);
					} catch (SecurityException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}
	
	private void sendIkrCategoryValue(IkrValueBean ikrValueBean) {
		long id = ikrValueBean.getIkrDefinitionBean().getIkrCategory().getId();	
		synchronized (ikrCategoryValues) {
			List<IkrValueBean> values = ikrCategoryValues.get(id);
			if(values == null) {
				values = new ArrayList<IkrValueBean>();
				ikrCategoryValues.put(id, values);
			}
			if (values.size() == MAX_CAPACITY)
				values.remove(0);
			values.add(ikrValueBean);
			System.out.println("AppletCallbackProxy::sendIkrCategoryValue >> " + id + " added");
		}
		
		synchronized (ikrCategoryCallbacks) {
			// send ikrCategory
			String env = null;//ikrValueBean.getIkrDefinitionBean().getMonitorConfig().getEnv();
			String context = null;//ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getIkrContext();
			long ikrCategory = ikrValueBean.getIkrDefinitionBean().getIkrCategory().getId();			
			String key = env+context+ikrCategory;
			List<IAppletCallback> callbacks = ikrCategoryCallbacks.get(key);
			if(callbacks != null && callbacks.size()>0) {
				for(IAppletCallback callback : callbacks) {
					try {
						callback.onNewDataReceived(ikrValueBean);
					} catch (ConnectException e) {
						logger.error(e.getMessage(), e);
						try {
							unsubscribeToIkrCategory(callback);
						} catch (RemoteException e1) {
							logger.error(e1.getMessage(), e1);
						}
					} catch (RemoteException e) {
						logger.error(e.getMessage(), e);
					} catch (SecurityException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}
	
	public List<IkrValueBean> getIkrDefinitionValues(long id) throws RemoteException {
		List<IkrValueBean> values = ikrDefinitionValues.get(id);
		return (values!=null)?values:new ArrayList<IkrValueBean>();
	}
	
	public List<IkrValueBean> getIkrCategoryValues(long id) throws RemoteException{
		List<IkrValueBean> values = ikrCategoryValues.get(id);
		return (values!=null)?values:new ArrayList<IkrValueBean>();
	}
	
	public void subscribeToIkrCategory(Collection<String> ids, IAppletCallback callback)
			throws RemoteException {
		synchronized (ikrCategoryCallbacks) {
			for(String id : ids) {
				List<IAppletCallback> subs = ikrCategoryCallbacks.get(id);
				if (subs == null) {
					subs = new ArrayList<IAppletCallback>();
					ikrCategoryCallbacks.put(id, subs);
				}
				subs.add(callback);
			}
		}
	}
	
	public void subscribeToIkrDefinition(Collection<Long> ids, IAppletCallback callback)
			throws RemoteException {
		synchronized (ikrDefinitionCallbacks) {
			for(long id : ids) {
				List<IAppletCallback> subs = ikrDefinitionCallbacks.get(id);
				if (subs == null) {
					subs = new ArrayList<IAppletCallback>();
					ikrDefinitionCallbacks.put(id, subs);
				}
				subs.add(callback);
			}
		}
	}

	public void unsubscribeToIkrCategory(IAppletCallback callback) throws RemoteException {
		synchronized (ikrCategoryCallbacks) {
			for(String id : ikrCategoryCallbacks.keySet()) {
				List<IAppletCallback> subs = ikrCategoryCallbacks.get(id);
				if (subs!= null && subs.size()>0) {
					subs.remove(callback);
					if (subs.size() == 0)
						ikrCategoryCallbacks.remove(id);
				}
			}
		}
	}
	
	public void unsubscribeToIkrDefinition(IAppletCallback callback) throws RemoteException {
		synchronized (ikrDefinitionCallbacks) {
			for(long id : ikrDefinitionCallbacks.keySet()) {
				List<IAppletCallback> subs = ikrDefinitionCallbacks.get(id);
				if (subs!= null && subs.size()>0) {
					subs.remove(callback);
					if (subs.size() == 0)
						ikrDefinitionCallbacks.remove(id);
				}
			}
		}
	}
}
