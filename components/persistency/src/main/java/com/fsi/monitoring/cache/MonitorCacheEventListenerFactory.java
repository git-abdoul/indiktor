package com.fsi.monitoring.cache;

import java.util.Properties;

import net.sf.ehcache.event.CacheEventListenerFactory;

public class MonitorCacheEventListenerFactory 
extends CacheEventListenerFactory {

	
	@Override
	public MonitorCacheEventListener createCacheEventListener(Properties arg0) {
		MonitorCacheEventListener tmp = MonitorCacheEventListener.getInstance();
		return tmp;
	} 

}
