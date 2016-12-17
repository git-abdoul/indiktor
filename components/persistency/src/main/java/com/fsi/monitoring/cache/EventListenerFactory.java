package com.fsi.monitoring.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

public class EventListenerFactory 
extends CacheEventListenerFactory {

	private static Map<String, CacheEventListener> listeners = new HashMap<String, CacheEventListener>();
	
	@Override
	public CacheEventListener createCacheEventListener(Properties properties) {
		return listeners.get(properties.getProperty("type"));
	} 
	
	public static void registerListener(CacheEventListener listenerToRegister, String type) {
		listeners.put(type, listenerToRegister);
	}
	
	public EventListenerFactory() {}
}
