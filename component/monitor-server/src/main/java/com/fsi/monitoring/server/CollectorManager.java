package com.fsi.monitoring.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminEvent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.config.RealTimeBackContext;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsAdminConsole;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.monitor.Monitor;
import com.fsi.monitoring.kpi.monitor.config.MonitorFactory;
import com.fsi.monitoring.server.listener.RTListener;

public class CollectorManager extends JmsAdminConsole implements Observer {	
	private static final Logger logger = Logger.getLogger(CollectorManager.class);
	
	private DataModelPM dataModelPM;
	private RTListener listener = null;
	
	private LogicalEnv logicalEnv = null;
	private Map<Long,MonitorRunner> monitors;
	
	public void init() {
		initJms();
	}
	
	public boolean isAlive() {
		return true;
	}

	public AdminComponent getComponentType() {
		return AdminComponent.COLLECTOR;
	}
		
	public void newMsgReceived(Collection<IkrJmsMessage> messages) {
		for (IkrJmsMessage msg : messages) {		
			AdminRequest request = (AdminRequest)msg;		
			if (request.getComponentType()== AdminComponent.COLLECTOR) {				
				switch (request.getCommand()) {
					case START:
						startMonitor(request.getComponentId());
						break;
						
					case STOP:
						stopMonitor(request.getComponentId());
						break;
						
					case ADD:
						MonitorFactory factory = (MonitorFactory)RealTimeBackContext.getBean("monitorFactory");	
						try {
							Monitor monitor = factory.createMonitor(request.getComponentId());
							createMonitorRunner(monitor);
						} catch (Exception e) {
							logger.error("Error while trying to create the monitor " + request.getComponentId() + " " + e.getMessage(), e);
						}
						break;
						
					case REMOVE:
						stopMonitor(request.getComponentId());
						MonitorRunner runner = monitors.get(request.getComponentId());
						if (runner==null)
							continue;
						while (runner.isRunning()) {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								logger.error(e.getMessage(), e);
							}
						}
						runner.getMonitor().updateStatus(ComponentStatus.REMOVE);
						runner.getMonitor().addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "REMOVED"));
						monitors.remove(request.getComponentId());
						sendEvent(runner.getMonitor().getLog());
						break;
						
					case UPDATE:
						stopMonitor(request.getComponentId());
						runner = monitors.get(request.getComponentId());
						if (runner==null)
							continue;
						while (runner.isRunning()) {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								logger.error(e.getMessage(), e);
							}
						}
						monitors.remove(request.getComponentId());
						factory = (MonitorFactory)RealTimeBackContext.getBean("monitorFactory");	
						try {
							Monitor monitor = factory.createMonitor(request.getComponentId());
							createMonitorRunner(monitor);
						} catch (Exception e) {
							logger.error("Error while trying to create the monitor " + request.getComponentId() + " " + e.getMessage(), e);
						}
						break;
						
					case HEARTBEAT:
						// TODO
						break;
						
					case GLOBAL_STATUS:
						runner = monitors.get(request.getComponentId());
						if (runner!=null)
							runner.getMonitor().notifyCurrentState();
						break;
			
					default:
						break;
				}
			}
		}		
	}

	public void startManager(String env) throws Exception {
		try {
			logicalEnv = dataModelPM.getLogicalEnv(env);			
			if (logicalEnv == null) {
				System.out.println("Ikr Monitor Environment " +  env + " unknown. Please set a correct Monitor Environment");
				System.exit(1);
			}
			
			listener = new RTListener();			
			monitors = new HashMap<Long,MonitorRunner>();			
			
			logger.info("Initializing Collector's Objects ...");
			System.out.println("Initializing Collector's Objects ...");
			
			MonitorFactory factory = (MonitorFactory)RealTimeBackContext.getBean("monitorFactory");		
			Map<Long, Monitor> monitorInstances = factory.createMonitors(logicalEnv.getId());		
			for(Monitor monitor : monitorInstances.values()) {
				createMonitorRunner(monitor);		
			}
		} catch(Exception exc) {
			throw new Exception("An Exception occured while initializing the Collector Server", exc);
		}
	}
	
	private void createMonitorRunner(Monitor monitor) {
		monitor.addObserver(this);
		monitors.put(monitor.getId(), new MonitorRunner(monitor));
		MonitorConfig monitorConfig = monitor.getMonitorConfig();
		String msg = "Collector created: <" + monitorConfig.getId() + ": " + monitor.getName() + "> ";
		if (monitorConfig.isAutoStart()) {
			logger.info(msg + " | Auto Start is activated | Collector will start automatically");
			startMonitor(monitor.getId());		
		}
		else {
			logger.info(msg + " | Auto Start is DEACTIVATED | Collector WON'T START");
		}		
	}
	
	private void startMonitor(long id) {
		MonitorRunner runner = monitors.get(id);
		if (runner!=null) {
			String collectorName = "<" + runner.getMonitor().getId() + " : " + runner.getMonitor().getName() + "> ";
			if (!runner.isRunning()) {				
				System.out.println("Starting Collector " + collectorName);
				logger.info("Starting Collector " + collectorName);
				runner.start();
			}
			else {
				logger.info("Collector " + collectorName + " is already started");
			}
		}
		else {
			logger.info("Collector ID=" + id + " can't be found");
		}
	}
	
	private void stopMonitor(long id) {
		MonitorRunner runner = monitors.get(id);
		if (runner!=null) {
			String collectorName = "<" + runner.getMonitor().getId() + " : " + runner.getMonitor().getName() + "> ";
			if (runner.isRunning()) {				
				System.out.println("Stopping Collector " + collectorName);
				logger.info("Stopping Collector " + collectorName);
				runner.stopMe();
			}
			else {
				logger.info("Collector " + collectorName + " is already stopped");
			}
			Monitor monitor = runner.getMonitor();
			monitors.put(monitor.getId(), new MonitorRunner(monitor));
		}
		else {
			logger.info("Collector ID=" + id + " can't be found");
		}
	}
	
	public void update(Observable o, Object arg) {
		if (o instanceof Monitor) {
			if (arg instanceof Collection) {
				Collection<IkrValue> metrics = (Collection<IkrValue>)arg;	
				listener.newEvent(metrics);
			} 
			else if (arg instanceof AdminEvent) {
				sendEvent((AdminEvent)arg);
			}
			else {
				logger.error("Type of message not supported: " + arg.getClass());
			}	
		}
	}
	
	private void sendEvent(AdminEvent event) {
		try {
			Collection<IkrJmsMessage> events = new ArrayList<IkrJmsMessage>();
			events.add(event);
			eventLogProducer.publish(events);
		} catch (Exception e) {
			logger.error("Error occured while publishing admin request : " + e.getMessage(), e);
		}
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}	
	
	class MonitorRunner extends Thread {
		private boolean running;
		private Monitor monitor;

		public MonitorRunner(Monitor monitor) {
			super();
			this.monitor = monitor;
		}

		public void run() {
			String msg = "<" + monitor.getId() + " : " + monitor.getName() + "> ";
			try {
				running = true;
				monitor.start();				
				System.out.println("Collector " + msg + " STARTED");
				logger.info("Collector " + msg + " STARTED");
				monitor.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "STARTED"));
			} catch (Exception exc) {	
				running = false;
				monitor.updateStatus(ComponentStatus.NOT_RUNNING);				
				monitor.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Impossible to Start : " + exc.getMessage()));
				String exMsg = "Impossible to start Collector " + msg;
				System.out.println(exMsg);
				logger.error(exMsg, exc);
			}	
			sendEvent(monitor.getLog());
		}

		public Monitor getMonitor() {
			return monitor;
		}
		
		public void stopMe() {
			monitor.stop();
			running = false;
		}
		
		public boolean isRunning() {
			return running;
		}
	}
}
