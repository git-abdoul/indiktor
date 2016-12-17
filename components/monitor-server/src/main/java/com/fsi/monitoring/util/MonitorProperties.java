package com.fsi.monitoring.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MonitorProperties {
	private static final Logger LOG = Logger.getLogger(MonitorProperties.class); 
	
	public final static int DEFAULT_MONITOR_MAX_RECONNECTION = 5;
	public final static int DEFAULT_MONITOR_RECONNECTION_DELAY = 10000;
	
	public final  static String SYSTEM_MONITOR_MAX_RECONNECTION = "SYSTEM_MONITOR_MAX_RECONNECTION";
	public final  static String SYSTEM_MONITOR_RECONNECTION_DELAY = "SYSTEM_MONITOR_RECONNECTION_DELAY";
	public final  static String JMX_MONITOR_MAX_RECONNECTION = "JMX_MONITOR_MAX_RECONNECTION";
	public final  static String JMX_MONITOR_RECONNECTION_DELAY = "JMX_MONITOR_RECONNECTION_DELAY";
	public final  static String CALYPSO_MONITOR_MAX_RECONNECTION = "CALYPSO_MONITOR_MAX_RECONNECTION";
	public final  static String CALYPSO_MONITOR_RECONNECTION_DELAY = "CALYPSO_MONITOR_RECONNECTION_DELAY";
	public final  static String DBMS_MONITOR_MAX_RECONNECTION = "DBMS_MONITOR_MAX_RECONNECTION";
	public final  static String DBMS_MONITOR_RECONNECTION_DELAY = "DBMS_MONITOR_RECONNECTION_DELAY";
	
	private static Properties props = new Properties();
	
	public static void loadProperties() throws Exception{		
		File file = null;
        InputStream stream = null;
        
        file = new File(System.getProperty("monitor.env"));
        stream = new FileInputStream(file);
		 props.load(stream);
		
		if (stream != null) {
            stream.close();
        }
	}
	
	static public int getIntProperty(String property) {
        String s = props.getProperty(property);
        if (s == null)
            return 0;
        try {
            return Integer.parseInt(s);
       } catch (Exception e) {
    	   LOG.error(e.getMessage(), e);
            return 0;
        }
    }
	
	public static int getSystemMonitorMaxReconnection() {
		return getIntProperty(SYSTEM_MONITOR_MAX_RECONNECTION);
	}
	
	public static int getSystemMonitorReconnectionDelay() {
		return getIntProperty(SYSTEM_MONITOR_RECONNECTION_DELAY);
	}	
	
	public static int getJmxMonitorMaxReconnection() {
		return getIntProperty(JMX_MONITOR_MAX_RECONNECTION);
	}
	
	public static int getJmxMonitorReconnectionDelay() {
		return getIntProperty(JMX_MONITOR_RECONNECTION_DELAY);
	}	
	
	public static int getCalypsoMonitorMaxReconnection() {
		return getIntProperty(CALYPSO_MONITOR_MAX_RECONNECTION);
	}
	
	public static int getCalypsoMonitorReconnectionDelay() {
		return getIntProperty(CALYPSO_MONITOR_RECONNECTION_DELAY);
	}	
	
	public static int getDbmsMonitorMaxReconnection() {
		return getIntProperty(DBMS_MONITOR_MAX_RECONNECTION);
	}
	
	public static int getDbmsMonitorReconnectionDelay() {
		return getIntProperty(DBMS_MONITOR_RECONNECTION_DELAY);
	}	

}
