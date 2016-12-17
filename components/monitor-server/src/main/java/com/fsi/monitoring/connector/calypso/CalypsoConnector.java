package com.fsi.monitoring.connector.calypso;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.calypso.tk.bo.workflow.KickOffCutOffConfig;
import com.calypso.tk.core.Book;
import com.calypso.tk.core.CacheLimit;
import com.calypso.tk.event.EngineConfig;
import com.calypso.tk.event.EventStats;
import com.calypso.tk.mo.TradeFilter;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.service.RemoteAccess;
import com.calypso.tk.service.RemoteBackOffice;
import com.calypso.tk.service.RemoteReferenceData;
import com.calypso.tk.service.RemoteTrade;
import com.calypso.tk.util.ScheduledTask;
import com.calypso.tk.util.ScheduledTaskExec;
import com.calypso.tk.util.cache.CacheMetrics;
import com.fsi.monitoring.connector.Connector;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.kpi.monitor.calypso.event.CalypsoEventObject;

public interface CalypsoConnector 
extends Connector {
	
	DSConnection getDSConnection() throws ConnectorException;
	
	// RemoteAccess
	RemoteAccess getRemoteAccess() throws ConnectorException;
	RemoteTrade getRemoteTrade() throws ConnectorException;
	EventStats getEventStats() throws ConnectorException;
	Hashtable<String,CacheMetrics> getCacheMetrics() throws ConnectorException;
	Hashtable<String,CacheLimit> getCacheLimits() throws ConnectorException;
	int[] getDBConnectionsCount() throws ConnectorException;
	Vector getConnectedClients() throws ConnectorException;
	Vector getVerifiedConnectedClients() throws ConnectorException;	
	boolean isDataServerConnected2EventServer() throws ConnectorException;
	int getEventBufferMaxSize() throws ConnectorException;
	int getEventBufferCurrentMax() throws ConnectorException;
	int getEventBufferSize() throws ConnectorException;
	String getEngineParam(String engineName, String paramName) throws ConnectorException;
	Hashtable<String,Integer> getNumberOfPendingEventProcessing(String engineName) throws ConnectorException;
	List<CalypsoEventObject> getPendingEventProcessing() throws ConnectorException;	
	void subscribeToCalypsoEvents(CalypsoListener listener, List<String> events);
	void unsubscribeToCalypsoEvents(CalypsoListener listener);
	long[] getEventServerStats();
	
	Date getDsCurrentTime();
	
	
	// RemoteReferenceData
	RemoteReferenceData getRemoteReferenceData() throws ConnectorException;
	EngineConfig getEngineConfig() throws ConnectorException;
	TradeFilter getTradeFilter(String tradeFilterName) throws ConnectorException;
	
	// RemoteBackOffice
	RemoteBackOffice getRemoteBackOffice() throws ConnectorException;
	Vector<ScheduledTask> getScheduledTasks() throws ConnectorException;
	Vector<ScheduledTaskExec> getScheduledTaskExecs() throws ConnectorException;
	
	// EventListener
	IkrCalypsoEventListener getEventListener() throws ConnectorException;
	
	//Legal Entity
	String getEntityName(int entityId) throws ConnectorException;
	
	//Book
	Book getBook(int bookId) throws ConnectorException;
	
	//KickOffCutOffConfig
	KickOffCutOffConfig getKickOffCutOffConfig(int kickOffCutOffConfigId) throws ConnectorException;
	
	// Database Connector
	RdbmsConnector getDatabaseConnector();
}
