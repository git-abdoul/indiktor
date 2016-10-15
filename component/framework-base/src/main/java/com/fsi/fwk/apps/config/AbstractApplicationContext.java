package com.fsi.fwk.apps.config;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fsi.fwk.log4j.IkrLog4jConfigurer;

public abstract class AbstractApplicationContext {

	private static final Logger LOG = Logger.getLogger(AbstractApplicationContext.class);		
	
	protected String homeDir = null;
	
	protected static AbstractApplicationContext singleton;

	private ApplicationContext applicationContext = null;
	
	public void init(String applicationContextFile, String ikrComponentName) throws IOException {		
		String ikrPropertiesEnvFile = System.getProperty("file.propEnv");
		IkrLog4jConfigurer.init(ikrPropertiesEnvFile, ikrComponentName);		
		applicationContext = new ClassPathXmlApplicationContext(applicationContextFile);
	}
	
	public static Object getBean(BeanName beanName) {
		if (singleton == null) {
			LOG.fatal("AbstractApplicationContext has not been initialized");
		}
		
		return singleton.applicationContext.getBean(beanName.getBeanName());
	}
	
	public static Object getBean(String beanName) {
		if (singleton == null) {
			LOG.fatal("AbstractApplicationContext has not been initialized");
		}
		
		return singleton.applicationContext.getBean(beanName);
	}
	
	public static AbstractApplicationContext getContext() {
		if (singleton == null) {
			LOG.fatal("ApplicationContext incorrectly initialized");
			System.exit(1);
		}
		return singleton;
	}
}
