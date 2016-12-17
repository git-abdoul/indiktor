package com.fsi.monitoring.cache;

import java.util.Properties;

import net.sf.ehcache.event.CacheEventListenerFactory;

public class StaticDataCacheEventListenerFactory 
extends CacheEventListenerFactory {

	
	@Override
	public StaticDataCacheEventListener createCacheEventListener(Properties arg0) {
		StaticDataCacheEventListener tmp = StaticDataCacheEventListener.getInstance();
		return tmp;
	} 

}
