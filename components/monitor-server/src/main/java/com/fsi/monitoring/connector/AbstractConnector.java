package com.fsi.monitoring.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.admin.IkrEventLog;

public abstract class AbstractConnector
extends Observable
implements Connector {
	private static final Logger logger = Logger.getLogger(AbstractConnector.class);	
	private static final int LOG_MAX_CAPACITY = 10;
	
	protected AbstractConnectorConfig connectorConfig;
	
	private Map<Long, ConnectorListener> listeners;
	
	private Date startTime; 
	
	private boolean isUpAndRunning;
	
	protected List<IkrAdminLogging> eventLogs;
	protected Map<String, String> eventStats;
	protected ComponentStatus status;

	public AbstractConnector(AbstractConnectorConfig connectorConfig) {
		this.connectorConfig = connectorConfig;
		isUpAndRunning = false;
		
		eventLogs = new ArrayList<IkrAdminLogging>();
		eventStats = new HashMap<String, String>();
		status = ComponentStatus.NOT_RUNNING;
		
		listeners = new HashMap<Long, ConnectorListener>();
	}
	
	public Collection<ConnectorListener> getListeners(){
		return listeners.values();
	}
	
	public void addListener(ConnectorListener listener) {
		listeners.put(listener.getListenerId(), listener);
	}
	
	public void removeListener(ConnectorListener listener) {
		listeners.remove(listener.getListenerId());
	}
	
	public void removeAllListener() {
		listeners.clear();
	}
	
	public void addLog(IkrAdminLogging log) {
		if (eventLogs.size()>=LOG_MAX_CAPACITY) {
			eventLogs.remove(0);
		}
		eventLogs.add(log);
	}
	
	public void addStat(String attr, String value) {
		eventStats.put(value, value);
	}
	
	public void updateStatus (ComponentStatus status) {
		this.status = status;
	}
	
	public long getId() {
		return (long)connectorConfig.getId();
	}
	
	public String getName() {
		return connectorConfig.getName();
	}
	
	public String getDescription() {
		return connectorConfig.getDescription();
	}	
	
	public void reportFailure(String message) throws ConnectorException {
		status = ComponentStatus.ERROR_OCCURED;
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, message));
		notifyEventLog();
		reportFailure();
		throw new ConnectorException(message);
	}
	
	public void reportFailure(Exception exception)
	throws ConnectorException {
		status = ComponentStatus.ERROR_OCCURED;
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, exception.getMessage()));
		notifyEventLog();
		reportFailure();
		throw new ConnectorException(exception);
	}	
	
	public void reportFailure(String message, Exception exception)
	throws ConnectorException {
		status = ComponentStatus.ERROR_OCCURED;
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, message + " : " + exception.getMessage()));
		notifyEventLog();
		reportFailure();
		throw new ConnectorException(message, exception);
	}		
	
	private synchronized void reportFailure() {
		if (isUpAndRunning) {
				stop();
				this.setChanged();
				notifyObservers(getId());
		}
	}
	
	protected abstract void openConnection() throws Exception;
	protected abstract void closeConnection() throws Exception;
	
	public synchronized void start() throws Exception {
		if (!isUpAndRunning) {
			try {
				openConnection();
				startTime = new Date();
				isUpAndRunning = true;
				status = ComponentStatus.NOTHING_TO_REPORT;
				notifyEventLog();
			}
			catch (Exception e) {
				isUpAndRunning = false;
				throw new Exception("Error occured when starting the connector : " + e.getMessage(), e);
			}
		}		
	}
	
	public synchronized void stop() {
		try {
			isUpAndRunning = false;
			closeConnection();	
			status = ComponentStatus.NOT_RUNNING;
		} catch(Exception exc) {
			status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Error while stopping connection : " + exc.getMessage()));			
			logger.error("Error while stopping connection", exc);
		}
		notifyEventLog();
	}
	
	public synchronized void checkStatus()
	throws ConnectorException {
		if (!isUpAndRunning) {
			status = ComponentStatus.NOT_RUNNING;
			notifyEventLog();
			throw new ConnectorException("Connector down: " + getType() + "--" +  getName());
		}
	}
	
	public boolean isUpAndRunning() {
		return isUpAndRunning;
	}

	public String getType() {
		return connectorConfig.getType();
	}
	
	protected void notifyEventLog() {
		IkrEventLog log = getLog();
		if (log !=null) {
			this.setChanged();			
			this.notifyObservers(log);
		}
	}
	
	public void notifyCurrentState() {
		notifyEventLog();
	}
	
	public IkrEventLog getLog() {
		return new IkrEventLog(getId(), getType(), startTime, AdminComponent.CONNECTOR, status, getName(), null, eventLogs, eventStats);
	}
}
