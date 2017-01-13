package com.fsi.monitoring.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.fsi.monitoring.connector.calypso.CalypsoConnectorImpl;
import com.fsi.monitoring.connector.http.HttpConnectorImpl;
import com.fsi.monitoring.connector.jmx.JmxConnectorImpl;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorImpl;
import com.fsi.monitoring.connector.sysload.SysloadConnectorImpl;
import com.fsi.monitoring.connector.systemAgent.SystemAgentConnectorImpl;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsAdminConsole;

public class ConnectorManager extends JmsAdminConsole implements Observer {
	private static final Logger logger = Logger.getLogger(ConnectorManager.class);	
	
	private Map<Long, List<ConnectorListener>> connectorUsers;
	private Map<Long,ConnectorRecovery> connectors;
	
	private DataModelPM dataModelPM;
	
	private int defaultMaxAttempt;
	private int defaultAttemptDelay;
	
	private int scenarioDelay;	

	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}
	
	public void setDefaultMaxAttempt(int defaultMaxAttempt) {
		this.defaultMaxAttempt = defaultMaxAttempt;
	}
	
	public void setDefaultAttemptDelay(int defaultAttemptDelay) {
		this.defaultAttemptDelay = defaultAttemptDelay;
	}	
	
	public void setScenarioDelay(int scenarioDelay) {
		this.scenarioDelay = scenarioDelay;
	}
	
	public boolean isAlive() {
		return true;
	}

	public AdminComponent getComponentType() {
		return AdminComponent.CONNECTOR;
	}

	public void newMsgReceived(Collection<IkrJmsMessage> messages) {
		for (IkrJmsMessage msg : messages) {		
			AdminRequest request = (AdminRequest)msg;
			if (request.getComponentType()== AdminComponent.CONNECTOR) {				
				switch (request.getCommand()) {
					case START:
						startConnector(request.getComponentId());
						break;
						
					case STOP:
						stopConnector(request.getComponentId());
						break;
						
					case ADD:
						try {
							ConnectorConfig connectorConfig = dataModelPM.getConnectorConfig((int)request.getComponentId());
							if (connectorConfig!=null)
								createConnectorRecovery(connectorConfig);
							else
								logger.error("Unable to find the connector with id = " + request.getComponentId());
						} catch (Exception e) {
							logger.error("Error while trying to create the monitor " + request.getComponentId() + " " + e.getMessage(), e);
						}
						break;
						
					case REMOVE:
						stopConnector(request.getComponentId());
						ConnectorRecovery recovery = connectors.get(request.getComponentId());
						if (recovery==null)
							continue;
						while (recovery.isInProgess()) {
							try {
								Thread.sleep(30000);
							} catch (InterruptedException e) {
								logger.error(e.getMessage(), e);
							}
						}
						recovery.getConnector().updateStatus(ComponentStatus.REMOVE);
						recovery.getConnector().addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "REMOVED"));
						sendEvent(recovery.getConnector().getLog());
						connectors.remove(request.getComponentId());
						break;
						
					case UPDATE:
						stopConnector(request.getComponentId());
						recovery = connectors.get(request.getComponentId());
						if (recovery==null)
							continue;
						while (recovery.isInProgess()) {
							try {
								Thread.sleep(30000);
							} catch (InterruptedException e) {
								logger.error(e.getMessage(), e);
							}
						}
						connectors.remove(request.getComponentId());
						try {
							ConnectorConfig connectorConfig = dataModelPM.getConnectorConfig((int)request.getComponentId());
							if (connectorConfig!=null)
								createConnectorRecovery(connectorConfig);
							else
								logger.error("Unable to find the connector with id = " + request.getComponentId());
						} catch (Exception e) {
							logger.error("Error while trying to create the monitor " + request.getComponentId() + " " + e.getMessage(), e);
						}
						break;
						
					case HEARTBEAT:
						// TODO
						break;
						
					case GLOBAL_STATUS:
						recovery = connectors.get(request.getComponentId());
						if (recovery!=null)
							recovery.getConnector().notifyCurrentState();
						break;
			
					default:
						break;
				}
			}
		}
		
	}
	
	public void init() {
		initJms();
		logger.info("Initializing Connector's Objects ... ");
		System.out.println("Initializing Connector's Objects ... ");
		connectors = new HashMap<Long,ConnectorRecovery>();
		connectorUsers = new HashMap<Long, List<ConnectorListener>>();
		
		Map<Integer, ConnectorConfig> connectorConfigs = null;
			
		try {
			connectorConfigs = dataModelPM.getConnectorConfigs();
		} catch (Exception exc) {
			System.err.println("Impossible to retreive connectorConfigs : " + exc.getMessage());
			logger.fatal("Impossible to retreive connectorConfigs", exc);
			logger.fatal("---- CONNECTORS INITIALIZATION FAILURE ----");
			return;
		}
				
		for (ConnectorConfig connectorConfig : connectorConfigs.values()) {
			createConnectorRecovery(connectorConfig);
		}
		System.out.println("Connector's Object initialization finished. " + connectors.size() + " connectors are ready to be used");
		logger.info("Connector's Object initialization finished. " + connectors.size() + " connectors are ready to be used");
	}
	
	private void createConnectorRecovery(ConnectorConfig connectorConfig) { 
		try {
			AbstractConnector connector = null;					
			String type = connectorConfig.getType();					
			if (type.equals(CalypsoConnectorConfig.TYPE)) {
				connector = new CalypsoConnectorImpl((CalypsoConnectorConfig)connectorConfig);
			} else if (type.equals(HttpConnectorConfig.TYPE)) {
				connector = new HttpConnectorImpl((HttpConnectorConfig)connectorConfig);
			} else if (type.equals(SysloadConnectorConfig.TYPE)) {
				connector = new SysloadConnectorImpl((SysloadConnectorConfig)connectorConfig);
			} else if (type.equals(RdbmsConnectorConfig.TYPE)) {
				connector = new RdbmsConnectorImpl((RdbmsConnectorConfig)connectorConfig);
			} else if (type.equals(JmxConnectorConfig.TYPE)) {
				connector = new JmxConnectorImpl((JmxConnectorConfig)connectorConfig);
			} else if (type.equals(SystemAgentConnectorConfig.TYPE)) {
				connector = new SystemAgentConnectorImpl((SystemAgentConnectorConfig)connectorConfig);
			}		
			
			if (connector.connectorConfig.getAttemptDelay() == 0) {
				connector.connectorConfig.setAttemptDelay(defaultAttemptDelay);
			}
							
			if (connector.connectorConfig.getMaxAttempt() == 0) {
				connector.connectorConfig.setMaxAttempt(defaultMaxAttempt);
			}
					
			connector.addObserver(this);
			connectors.put((long)connectorConfig.getId(), new ConnectorRecovery(connector));	
			logger.info("Connector <" + connector.connectorConfig.getType() + "--" + connector.getName() + "> is initialized and ready to be used.");
		} catch (Exception exc) {
			logger.fatal("!!! Error while creating connector: " + connectorConfig.getType() + "--" + connectorConfig.getName(), exc);
		}
	}
	
	public Connector getConnector(long connectorId) {
		Connector connector = null;
		ConnectorRecovery connectorRecovery = connectors.get(connectorId);
		if (connectorRecovery!=null)
			connector = connectorRecovery.getConnector();
		return connector;
	}
	
	public void addListener(long connectorId, ConnectorListener listener) {
		if (listener!=null) {
			Connector connector = getConnector(connectorId);
			if(connector!=null)
				connector.addListener(listener);
			List<ConnectorListener> users = connectorUsers.get(connectorId);
			if (users == null) {
				users = new ArrayList<ConnectorListener>();
				connectorUsers.put(connectorId, users);
			}
			users.add(listener);
		}
	}
	
	public void startConnector(long connectorId) {
		startConnector(connectorId, null);
	}
	
	public void stopConnector(long connectorId) {
		stopConnector(connectorId, null);
	}
	
	public void startConnector(long connectorId, ConnectorListener listener) {		
		ConnectorRecovery recovery = connectors.get(connectorId);
		if (recovery!=null) {
			if (listener==null){
				List<ConnectorListener> users = connectorUsers.get(connectorId);
				if (users != null) {
					for (ConnectorListener user : users) {
						recovery.getConnector().addListener(user);
					}
				}
			}
			else {
				recovery.getConnector().addListener(listener);
			}
			
			AbstractConnector absConnector = recovery.getConnector();
			if (!absConnector.isUpAndRunning()) {
				try {
					recovery.start();
				}
				catch (IllegalThreadStateException e) {
					System.out.println("Collector " + listener.getListenerId() + " just got an IllegalThreadStateException");
				}
			}
			else {
				if (listener!=null)
					listener.onStartedEvt(absConnector);
			}
		}
	}
	
	public void stopConnector(long connectorId, ConnectorListener listener) {
		ConnectorRecovery recovery = connectors.get(connectorId);
		if (recovery != null) {
			if (listener!=null) {
				recovery.getConnector().removeListener(listener);
				listener.onDisconnectEvt(recovery.getConnector());
			} else {
				recovery.getConnector().removeAllListener();	
			}	
			
			recovery.disconnect();
			AbstractConnector connector = recovery.getConnector();
			connectors.put(connectorId, new ConnectorRecovery(connector));
		}
	}

	public void update(Observable o, Object arg) {
		if (o instanceof Connector) {
			if (arg != null) {
				if (arg instanceof AdminEvent) {
					sendEvent((AdminEvent)arg);
				}
				else if (arg instanceof Long){		
					long connectorId = (Long)arg;	
					stopConnector(connectorId);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						logger.error(e.getMessage(), e);
					}
					startConnector(connectorId);
				}
			}	
		}
	}
	
	public synchronized void checkStatus(long connectorId) throws ConnectorException {
		ConnectorRecovery recovery = connectors.get(connectorId);
		if (recovery!=null)
			recovery.getConnector().checkStatus();
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
	
	private class ConnectorRecovery extends Thread {
		private AbstractConnector absConnector;
		private boolean inProgess = false;
		private boolean stop = false;
		
		public ConnectorRecovery(AbstractConnector absConnector) {
			this.absConnector = absConnector;
		}
		
		public void run() {
			connect();
		}
		
		protected void connect() {
			inProgess = true;
			while (!absConnector.isUpAndRunning() && !stop) {				
				int attempt = 0;
				while (!absConnector.isUpAndRunning() && attempt < absConnector.connectorConfig.getMaxAttempt() && !stop) {
					absConnector.updateStatus(ComponentStatus.STARTING);
					absConnector.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Trying to connect ..."));
					sendEvent(absConnector.getLog());
					try {
						absConnector.start();
					} catch (Exception exc) {	
						absConnector.updateStatus(ComponentStatus.STARTING);
						absConnector.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, exc.getMessage()));
						sendEvent(absConnector.getLog());
						String msg = "!!! Connection attempt(s) " + (attempt + 1) + " failure: " + absConnector.connectorConfig.getType() + "--" + absConnector.getName();
						logger.error(msg, exc);
						try {
							Thread.sleep(absConnector.connectorConfig.getAttemptDelay()*1000);
						} catch (InterruptedException exc2) {
							logger.error("Attempt sleep impossible", exc2);
						}
					}
					++attempt;
				}
				
				if (absConnector.isUpAndRunning()) {
					absConnector.updateStatus(ComponentStatus.NOTHING_TO_REPORT);
					absConnector.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "CONNECTED"));
					sendEvent(absConnector.getLog());
					Collection<ConnectorListener> users = absConnector.getListeners();
					if (users!=null) {
						System.out.println("Connector <" + absConnector.connectorConfig.getType() + "--" + absConnector.getName() + ">  NB Listeners = " + users.size());
						for (ConnectorListener listener : users) {
							listener.onStartedEvt(absConnector);
						}
					}
					String msg = "Connector <" + absConnector.connectorConfig.getType() + "--" + absConnector.getName() + "> is STARTED | Connection attempt = " + attempt;
					System.out.println(msg);
					logger.info(msg);
				} else {
					if (!stop) {
						absConnector.updateStatus(ComponentStatus.STARTING);
						absConnector.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "!!! Connection attempt " + attempt + " failure: " + absConnector.connectorConfig.getType() + "--" + absConnector.getName()));
						absConnector.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Waiting for " + scenarioDelay + " mins before new scenario connection attempt"));
						sendEvent(absConnector.getLog());
						logger.error("!!! Connection attempt " + attempt + " failure: " + absConnector.connectorConfig.getType() + "--" + absConnector.getName());
						logger.info("Waiting for " + scenarioDelay + " mins before new scenario connection attempt");
						try {
							Thread.sleep(scenarioDelay*60*1000);
						} catch (InterruptedException exc2) {
							logger.error("Attempt sleep impossible", exc2);
						}
					}
				}
			}
			
			inProgess = false;
			
			if (!absConnector.isUpAndRunning()) {
				absConnector.updateStatus(ComponentStatus.NOT_RUNNING);
				absConnector.addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "!!! Connection unsuccessfull"));				
			}
			
			sendEvent(absConnector.getLog());
		}
		
		protected void disconnect() {
			if (absConnector.getListeners().size()==0) {
				if (absConnector.isUpAndRunning()) {
					absConnector.stop();
				}
				else {
					if (inProgess) 
						stop = true;
				}
				
				List<ConnectorListener> users = connectorUsers.get(absConnector.getId());
				for (ConnectorListener listener : users) {
					listener.onDisconnectEvt(absConnector);
				}
			}
		}

		protected AbstractConnector getConnector() {
			return absConnector;
		}

		protected boolean isInProgess() {
			return inProgess;
		}		
	}	
}
