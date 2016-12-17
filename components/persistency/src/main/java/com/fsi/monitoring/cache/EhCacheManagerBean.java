package com.fsi.monitoring.cache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

public class EhCacheManagerBean implements FactoryBean, InitializingBean, DisposableBean {
	private static final Logger LOG = Logger.getLogger(EhCacheManagerBean.class);
	
	private CacheManager cacheManager;
	
	private URL ehcacheConfigLocation;
	
	private String propertiesFile; 
	private String componentName;
	private String cacheManagerName;
	
	public void afterPropertiesSet() throws IOException, CacheException {
		String initInfo = "Initializing EHCache CacheManager";
		if (this.cacheManagerName!=null && this.cacheManagerName.length()>0)
			initInfo = initInfo + " <"+ this.cacheManagerName +">";
		
		LOG.info(initInfo);
		
		Configuration configuration = ConfigurationFactory.parseConfiguration(ehcacheConfigLocation);
        configuration.setSource("classpath");
        
        String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(propertiesFile);
		URL url = ResourceUtils.getURL(resolvedLocation);
		
		Properties properties = new Properties();
		String path = url.getFile();
		properties.load(new FileInputStream(path));
		
		String peerListenerHostname = "hostName="+properties.getProperty(componentName + ".cache.peerListener.host");
        String peerListenerProperties = configuration.getCacheManagerPeerListenerFactoryConfiguration().getProperties();
		if (peerListenerProperties == null || peerListenerProperties.length()==0)
			peerListenerProperties = peerListenerHostname;
		else
			peerListenerProperties = peerListenerProperties + ", " + peerListenerHostname;
		configuration.getCacheManagerPeerListenerFactoryConfiguration().setProperties(peerListenerProperties);
		
		String peerProviderHostname = "hostName="+properties.getProperty(componentName + ".cache.peerProvider.host");
        String peerProviderProperties = configuration.getCacheManagerPeerProviderFactoryConfiguration().getProperties();
		if (peerProviderProperties == null || peerProviderProperties.length()==0)
			peerProviderProperties = peerProviderHostname;
		else
			peerProviderProperties = peerProviderProperties + ", " + peerProviderHostname;
		configuration.getCacheManagerPeerProviderFactoryConfiguration().setProperties(peerProviderProperties);
		
		if (Boolean.parseBoolean(properties.getProperty(componentName + ".cache.activate"))) {
			Map<String, CacheConfiguration> cachesConfigurations = configuration.getCacheConfigurations();
			for (CacheName cacheName : CacheName.values()) {
				CacheConfiguration cacheConfig = cachesConfigurations.get(cacheName.name());
				if (cacheConfig != null)
					cacheConfig.setMaxElementsInMemory(Integer.parseInt(properties.getProperty(componentName+".cache."+cacheName.name()+".maxSize")));
			}
		}
		
		this.cacheManager = new CacheManager(configuration);
		if (this.cacheManagerName!=null && this.cacheManagerName.length()>0)
			this.cacheManager.setName(this.cacheManagerName);
	}	

	public void setEhcacheConfigLocation(String ehcacheConfigLocation) throws FileNotFoundException {
		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(ehcacheConfigLocation);
		this.ehcacheConfigLocation = ResourceUtils.getURL(resolvedLocation);
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public void setCacheManagerName(String cacheManagerName) {
		this.cacheManagerName = cacheManagerName;
	}

	public void destroy() throws Exception {
		LOG.info("Shutting down EHCache CacheManager");
		this.cacheManager.shutdown();		
	}

	public Object getObject() throws Exception {
		return this.cacheManager;
	}

	public Class getObjectType() {
		return (this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class);
	}

	public boolean isSingleton() {
		return true;
	}

}
