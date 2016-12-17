package com.fsi.monitoring.util;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.fsi.fwk.log4j.IkrLog4jConfigurer;

public class SystemPropertiesHelper implements ServletContextListener {
	
	private ServletContext context = null;

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void contextInitialized(ServletContextEvent event) {
		context = event.getServletContext();
		Enumeration<String> params = context.getInitParameterNames();

		while (params.hasMoreElements()) {
			String param = (String) params.nextElement();
			String value = context.getInitParameter(param);
			if (param.startsWith("ikr.")) {
				System.setProperty(param, value);
			}
		}	
		
		try {
			String ikrPropertiesEnvFile = System.getProperty("ikr.appEnv.prop");
			IkrLog4jConfigurer.init(ikrPropertiesEnvFile, "indiktor-web-server");
		} catch (IOException e) {
			System.err.println("Log4j configuation failed");
			System.err.println(e);
		}
	}

}
