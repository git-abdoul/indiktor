package com.fsi.monitoring.cache;


import java.util.HashMap;
import java.util.Map;


import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class MonitorCacheEventListener
implements CacheEventListener  {
	
	private Map<Integer,CacheEventListener> observers = new HashMap<Integer,CacheEventListener>();

	private static MonitorCacheEventListener instance = new MonitorCacheEventListener();
	
	public static MonitorCacheEventListener getInstance() {
		return instance;
	}
	
	public synchronized void addObserver(int logicalEnvId, CacheEventListener obs) {
		observers.put(logicalEnvId, obs);
	}
	
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void notifyElementEvicted(Ehcache arg0, Element arg1) {
		// TODO Auto-generated method stub
		
	}

	public void notifyElementExpired(Ehcache arg0, Element arg1) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void notifyElementPut(Ehcache arg0, Element arg1)
	throws CacheException {
		Element element = (Element)arg1;
		MonitorConfig monitorConfig = (MonitorConfig)element.getValue();
		
		CacheEventListener obs = observers.get(monitorConfig.getLogicalEnvId());
		if (obs != null) {
			obs.notifyElementPut(arg0, arg1);
		}
		
	}

	public synchronized void notifyElementRemoved(Ehcache arg0, Element arg1)
	throws CacheException {
		Element element = (Element)arg1;
		MonitorConfig monitorConfig = (MonitorConfig)element.getValue();
		
		CacheEventListener obs = observers.get(monitorConfig.getLogicalEnvId());
		if (obs != null) {
			obs.notifyElementRemoved(arg0, arg1);
		}
	}

	public synchronized void notifyElementUpdated(Ehcache arg0, Element arg1)
	throws CacheException {
//		System.out.println("---MONITOR UPDATED---");
		Element element = (Element)arg1;
		MonitorConfig monitorConfig = (MonitorConfig)element.getValue();
		
		CacheEventListener obs = observers.get(monitorConfig.getLogicalEnvId());
		if (obs != null) {
			obs.notifyElementUpdated(arg0, arg1);
		}		
	}

	public void notifyRemoveAll(Ehcache arg0) {
		// TODO Auto-generated method stub		
	}

	public java.lang.Object clone() throws java.lang.CloneNotSupportedException {
		return null;
	}
	
}
