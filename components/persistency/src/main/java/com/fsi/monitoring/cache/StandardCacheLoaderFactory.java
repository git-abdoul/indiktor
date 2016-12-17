package com.fsi.monitoring.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.loader.CacheLoaderFactory;

public class StandardCacheLoaderFactory 
extends CacheLoaderFactory {

	public static Map<String,CacheLoader> cacheLoaders = new HashMap<String,CacheLoader>();
	
	@Override
	public CacheLoader createCacheLoader(Map arg0) {
		return null;
	}

	@Override
	public CacheLoader createCacheLoader(Properties arg0) {
		String name = arg0.getProperty("name");
		
		CacheLoader cacheLoader = cacheLoaders.get(name);

		return cacheLoader;
	}
	
	public void setCacheLoaders(Map<String,CacheLoader> initCacheLoaders) {
		cacheLoaders.putAll(initCacheLoaders);
	}
}
