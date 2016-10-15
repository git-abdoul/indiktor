package com.fsi.monitoring.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.log4j.Logger;

import com.fsi.monitoring.cache.MonitorCacheEventListener;
import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.config.RealTimeBackContext;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.monitor.Monitor;
import com.fsi.monitoring.kpi.monitor.config.MonitorFactory;
import com.fsi.monitoring.server.listener.RTListener;

public class RTMonitorServer
implements Observer, CacheEventListener {
	
	private static final Logger LOG = Logger.getLogger(RTMonitorServer.class);
	
	private LogicalEnv logicalEnv = null;
	
	private Map<Long,Monitor> monitors;
	private Map<Long,MonitorRunner> scheduledFutures;
	private Map<Long,MonitorRunner> failedFutures;
	private RTListener listener = null;	

	public RTMonitorServer(String env) throws Exception {
		try {
			DataModelPM dataModelPM = PMFactory.getDataModelPM();
			
			logicalEnv = dataModelPM.getLogicalEnv(env);
			
			if (logicalEnv == null) {
				System.out.println("Ikr Monitor Environment " +  env + " unknown. Please set a correct Monitor Environment");
				System.exit(1);
			}
			
			scheduledFutures = new HashMap<Long,MonitorRunner>();
			failedFutures = new HashMap<Long, RTMonitorServer.MonitorRunner>();
			listener = new RTListener();
			
			LOG.info("Initializing Collector's Objects ...");
			System.out.println("Initializing Collector's Objects ...");
			createMonitors();
			LOG.info("Collector's Objects Initialization is finished.");
			System.out.println("Collector's Objects Initialization is finished.");
			
			MonitorCacheEventListener cacheEventListener = MonitorCacheEventListener.getInstance();
			cacheEventListener.addObserver(logicalEnv.getId(), this);			
			
		} catch(Exception exc) {
			throw new Exception("An Exception occured while initializing the Collector Server", exc);
		}		
	}
	
	public void start(){
		System.out.println("Starting Collectors ... ");
		LOG.info("Starting Collectors ... ");
		for(Monitor monitor : monitors.values()) {	
			startMonitor(monitor);			
		}
		System.out.println(scheduledFutures.size() + " Collectors has been started");
		LOG.info(scheduledFutures.size() + " Collectors has been started");
	}
	
	private void startMonitor(Monitor monitor) {
		if (monitor != null && monitor.isAutoStart()) {
			(new MonitorRunner(monitor)).start();
		}
	}
	
	private void stopMonitor(long monitorId) {
		MonitorRunner runner = failedFutures.remove(monitorId);
		if (runner==null)
			runner = scheduledFutures.get(monitorId);
		if (runner!=null) {
			runner.stopMe();
			runner = null;			
			scheduledFutures.remove(monitorId);
		}
	}
	
	private void createMonitors() throws Exception {
		MonitorFactory factory = (MonitorFactory)RealTimeBackContext.getBean("monitorFactory");
		
		// Env is used to filter monitors by env for this server (One env by server)
		monitors = factory.createMonitors(logicalEnv.getId());
		
		for(Monitor monitor: monitors.values()) {
			MonitorConfig monitorConfig = monitor.getMonitorConfig();
			String msg = "Collector created: " + monitorConfig.getId() + ": <" + monitor.getName() + "> ";
			if (monitorConfig.isAutoStart()) {
				msg = msg + " | Auto Start is activated | Collector will start automatically";
			}
			else {
				msg = msg + " | Auto Start is DEACTIVATED | Collector WON'T START";
			}
//			System.out.println(msg);
			LOG.info(msg);
			
			monitors.put(monitor.getId(), monitor);		
			monitor.addObserver(this);
		}
	}	
	
	public void update(Observable o, Object arg) {
		if (arg instanceof Collection) {
			Collection<IkrValue> metrics = (Collection<IkrValue>)arg;	
			listener.newEvent(metrics);
		} else {
			LOG.error("Type of message not supported: " + arg.getClass());
		}		
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

	public void notifyElementPut(Ehcache arg0, Element arg1) throws CacheException {
//		System.out.println("--- MONITOR CREATED ---");
		try {
				Element element = (Element)arg1;
				MonitorConfig monitorConfig = (MonitorConfig)element.getValue();			
				if (monitorConfig.getLogicalEnvId() == logicalEnv.getId()) {				
					MonitorFactory factory = (MonitorFactory)RealTimeBackContext.getBean("monitorFactory");
					Monitor monitor = factory.createMonitor(monitorConfig.getId());					
					if (monitor != null) {
						monitors.put(monitor.getId(), monitor);		
						monitor.addObserver(this);
						startMonitor(monitor);
					}
				}
		} catch (Exception exc) {
			LOG.error("Impossible to update Agent on a monitor creation", exc);
		}
	}

	public void notifyElementRemoved(Ehcache arg0, Element arg1) throws CacheException {
		try {
				Element element = (Element)arg1;
				MonitorConfig monitorConfig = (MonitorConfig)element.getValue();
				long monitorId = monitorConfig.getId();				
				stopMonitor(monitorId);
				monitors.remove(monitorId);	
		} catch (Exception exc) {
			LOG.error("Impossible to update Agent on a monitor removal", exc);
		}
	}

	public void notifyElementUpdated(Ehcache arg0, Element arg1)
	throws CacheException {
//		System.out.println("--- MONITOR UPDATED ---");
		try {
				Element element = (Element)arg1;
				
				MonitorConfig newMonitorConfig = (MonitorConfig)element.getValue();
				long monitorId = newMonitorConfig.getId();				
				stopMonitor(monitorId);				
				if (newMonitorConfig.getLogicalEnvId() == logicalEnv.getId()) {	
					MonitorFactory factory = (MonitorFactory)RealTimeBackContext.getBean("monitorFactory");
					Monitor monitor = factory.createMonitor(monitorId);					
					if (monitor != null) {
						monitors.put(monitor.getId(), monitor);		
						monitor.addObserver(this);
						startMonitor(monitor);
					}
				}
		} catch (Exception exc) {
			LOG.error("Impossible to update Agent on a monitor update", exc);
		}
	}

	public void notifyRemoveAll(Ehcache arg0) {
		// TODO Auto-generated method stub		
	}

	public Object clone() throws java.lang.CloneNotSupportedException {
		return null;
	}	
	
	class MonitorRunner extends Thread {
		private Monitor monitor;

		public MonitorRunner(Monitor monitor) {
			super();
			this.monitor = monitor;
		}

		public void run() {
			startMonitor(monitor);			
		}
		
		private void startMonitor(Monitor monitor) {
			if (monitor != null && monitor.isAutoStart()) {
				failedFutures.put(monitor.getId(), this);
				try {
					monitor.start();
					scheduledFutures.put(monitor.getId(), this);
					failedFutures.remove(monitor.getId());
				} catch (Exception exc) {					
					String msg = "Impossible to schedule monitor:" + monitor.getId();
					System.out.println(msg);
					LOG.error(msg, exc);
				}
			}
		}

		public Monitor getMonitor() {
			return monitor;
		}
		
		public void stopMe() {
			monitor.stop();
		}
	}
}
