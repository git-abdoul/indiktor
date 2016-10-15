package com.fsi.monitoring.cache;


import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import com.fsi.monitoring.msd.StaticData;

public class StaticDataCacheEventListener
implements CacheEventListener  {
	
	private List<CacheEventListener> observers = new ArrayList<CacheEventListener>();

	private static StaticDataCacheEventListener instance = new StaticDataCacheEventListener();
	
	public static StaticDataCacheEventListener getInstance() {
		return instance;
	}
	
	public synchronized void addObserver(CacheEventListener obs) {
		observers.add(obs);
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
		if (element.getValue() instanceof StaticData) {		
			for(CacheEventListener obs : observers) {
				if (obs != null) {
					obs.notifyElementPut(arg0, arg1);
				}
			}	
		}
	}

	public synchronized void notifyElementRemoved(Ehcache arg0, Element arg1)
	throws CacheException {
		Element element = (Element)arg1;
		if (element.getValue() instanceof StaticData) {		
			for(CacheEventListener obs : observers) {
				if (obs != null) {
					obs.notifyElementRemoved(arg0, arg1);
				}
			}	
		}
	}

	public synchronized void notifyElementUpdated(Ehcache arg0, Element arg1)
	throws CacheException {
		Element element = (Element)arg1;
		if (element.getValue() instanceof StaticData) {		
			for(CacheEventListener obs : observers) {
				if (obs != null) {
					obs.notifyElementUpdated(arg0, arg1);
				}
			}	
		}
	}

	public void notifyRemoveAll(Ehcache arg0) {
		// TODO Auto-generated method stub
		
	}

	public java.lang.Object clone() throws java.lang.CloneNotSupportedException {
		return null;
	}
	
}
