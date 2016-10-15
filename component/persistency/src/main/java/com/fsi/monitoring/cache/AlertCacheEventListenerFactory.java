package com.fsi.monitoring.cache;

import java.util.Properties;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

public class AlertCacheEventListenerFactory 
extends CacheEventListenerFactory {

	private static CacheEventListener listener;
	
	@Override
	public CacheEventListener createCacheEventListener(Properties arg0) {
		return listener;
	} 
	
	public static void registerListener(CacheEventListener listenerToRegister) {
		listener = listenerToRegister;
	}

}
