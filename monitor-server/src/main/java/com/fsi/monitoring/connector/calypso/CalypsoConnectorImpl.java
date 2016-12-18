package com.fsi.monitoring.connector.calypso;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.calypso.apps.startup.AppStarter;
import com.calypso.tk.bo.workflow.KickOffCutOffConfig;
import com.calypso.tk.core.Book;
import com.calypso.tk.core.CacheLimit;
import com.calypso.tk.core.CacheUtil;
import com.calypso.tk.core.Defaults;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.LegalEntity;
import com.calypso.tk.core.Util;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.EngineConfig;
import com.calypso.tk.event.EventStats;
import com.calypso.tk.mo.TradeFilter;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.service.RemoteAccess;
import com.calypso.tk.service.RemoteBackOffice;
import com.calypso.tk.service.RemoteReferenceData;
import com.calypso.tk.service.RemoteTrade;
import com.calypso.tk.util.ConnectionUtil;
import com.calypso.tk.util.ScheduledTask;
import com.calypso.tk.util.ScheduledTaskExec;
import com.calypso.tk.util.cache.CacheMetrics;
import com.fsi.monitoring.connector.AbstractConnector;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorImpl;
import com.fsi.monitoring.kpi.monitor.calypso.event.CalypsoEventObject;

public class CalypsoConnectorImpl
extends AbstractConnector
implements CalypsoConnector {
	
	private static final Logger logger = Logger.getLogger(CalypsoConnectorImpl.class);
	
	private CalypsoConnectorConfig calypsoConnectorConfig; 
	
	private DSConnection dsConnection;
	private IkrCalypsoEventListener evtListener;
	
	private Map<Integer, String> legalEntities;
	private Map<Integer, Book> books;
	
	private Hashtable<Integer, KickOffCutOffConfig> kickOffCutOffConfigs;
	
	private RdbmsConnector databaseConnector;
	
	public CalypsoConnectorImpl(CalypsoConnectorConfig calypsoConnectorConfig) {
		super(calypsoConnectorConfig);
		this.calypsoConnectorConfig = calypsoConnectorConfig;
		legalEntities = new HashMap<Integer, String>();
		kickOffCutOffConfigs = new Hashtable<Integer, KickOffCutOffConfig>();
		books = new HashMap<Integer, Book>();
	}
	
	@Override
	public void closeConnection() throws Exception {
		if (dsConnection != null) {
			dsConnection.disconnect();
			dsConnection = null;		
		}
	}

	@Override
	public void openConnection() throws Exception {		
		logger.info("AsOfDate Active : " + calypsoConnectorConfig.isAsofdateActive());
		if (calypsoConnectorConfig.isAsofdateActive()){
			String argsWithAsofdate[] = {"-env", calypsoConnectorConfig.getConnectorContext(), 
										 "-user", calypsoConnectorConfig.getUserName(),
										 "-password", calypsoConnectorConfig.getPassword(), 
										 "-asofdate", calypsoConnectorConfig.getAsofdate()};			
			String asofdate = AppStarter.getOption(argsWithAsofdate, "-asofdate");
			logger.info("AsOfDate Value: " + asofdate);
			if (asofdate != null) {
				JDate todayJDate = Util.stringToJDate(asofdate);
				logger.info("todayJDate Value: " + todayJDate.toString());
				JDate.setToday(todayJDate);
			}	
			logger.info("Connection Args : " + argsWithAsofdate);
			dsConnection = ConnectionUtil.connect(argsWithAsofdate,calypsoConnectorConfig.getApplicationName());
		}
		else {
			String args[] = {"-env", calypsoConnectorConfig.getConnectorContext(), 
					 "-user", calypsoConnectorConfig.getUserName(),
					 "-password", calypsoConnectorConfig.getPassword()};
			dsConnection = ConnectionUtil.connect(args,calypsoConnectorConfig.getApplicationName());
		}		
		
		if (dsConnection != null) {	
			evtListener = new IkrCalypsoEventListener(dsConnection, calypsoConnectorConfig.getApplicationName());
			logger.info("Calypso EventListener Created");
			evtListener.start();
			
			if(calypsoConnectorConfig.getDbUserName()!=null&&calypsoConnectorConfig.getDbUserName().length()>0&&
			   calypsoConnectorConfig.getDbPassword()!=null&&calypsoConnectorConfig.getDbPassword().length()>0) {
				RemoteAccess remote = dsConnection.getRemoteAccess();
				Properties envProp = remote.getEnvProperties();
				String driver = envProp.getProperty(Defaults.DRIVER);
				String uri = envProp.getProperty(Defaults.DBURL);			
				RdbmsConnectorConfig calypsoDbConnectionConfig = new RdbmsConnectorConfig(0, "Calypso Database Connector", "Calypso Database Connector", 5, 20);
				calypsoDbConnectionConfig.setUserName(calypsoConnectorConfig.getDbUserName());
				calypsoDbConnectionConfig.setPassword(calypsoConnectorConfig.getDbPassword());
				calypsoDbConnectionConfig.setDriver(driver);
				calypsoDbConnectionConfig.setUri(uri);
				databaseConnector = new RdbmsConnectorImpl(calypsoDbConnectionConfig);
				try {
					((RdbmsConnectorImpl)databaseConnector).openConnection();
				}
				catch (Exception e) {
//					evtListener.stop();
//					dsConnection.disconnect();
//					throw new Exception(e);
					logger.error(e.getMessage(), e);
				}
			}
		}
		else {
			throw new Exception("An Error occured while trying to connect to Calypso. The DSConnection is null");
		}
	}
	
	public Date getDsCurrentTime() {
		return dsConnection.getServerCurrentDatetime();
	}
	
	public IkrCalypsoEventListener getEventListener()
	throws ConnectorException {
		checkStatus();

		if (evtListener == null) {
			logger.error("CalypsoEventListener null: " + getType() + "--" +  getName());
			reportFailure("CalypsoEventListener is null");
		}
		
		if (!evtListener.checkStatus()) {
			reportFailure("CalypsoEventListener is having a problem");  
		}
		
		return evtListener;
	}
	
	public DSConnection getDSConnection() 
	throws ConnectorException {
		checkStatus();
		if (dsConnection == null || dsConnection.isClosed()) {
			String message = "The Calypso DSConnection is null or closed: " + getType() + "--" +  getName();
			reportFailure(message);
		}
		
		return dsConnection;
	}
	
	public RemoteAccess getRemoteAccess() 
	throws ConnectorException {
		DSConnection dsConnection = getDSConnection();
		
		RemoteAccess ref = null;
		if (dsConnection != null) {
			ref = dsConnection.getRemoteAccess();
		} else {
			logger.error("Calypso DSConnection used for RemoteAccess is null or closed: " + getType() + "--" +  getName());
			throw new ConnectorException();			
		}
		
		if (ref == null) {
			String message = "Calypso RemoteAccess null: " + getType() + "--" +  getName();
			reportFailure(message);
		}
		
		return ref;
	}	
	
	public RemoteTrade getRemoteTrade() 
	throws ConnectorException {
		DSConnection dsConnection = getDSConnection();
		
		RemoteTrade ref = null;
		if (dsConnection != null) {
			ref = dsConnection.getRemoteTrade();
		} else {
			logger.error("Calypso DSConnection used for RemoteTrade is null or closed: " + getType() + "--" +  getName());
			throw new ConnectorException();			
		}
		
		if (ref == null) {
			String message = "Calypso RemoteTrade is null: " + getType() + "--" +  getName();
			reportFailure(message);
		}
		
		return ref;
	}	
	
	public RemoteBackOffice getRemoteBackOffice() 
	throws ConnectorException {
		DSConnection dsConnection = getDSConnection();
		
		RemoteBackOffice ref = null;
		if (dsConnection != null) {
			ref = dsConnection.getRemoteBO();
		} else {
			logger.error("Calypso DSConnection used for RemoteBackOffice is null or closed: " + getType() + "--" +  getName());
			throw new ConnectorException();			
		}
		
		if (ref == null) {
			String message = "Calypso RemoteBackOffice is null: " + getType() + "--" +  getName();
			reportFailure(message);
		}
		
		return ref;
	}
	
	public Vector<ScheduledTask> getScheduledTasks()	
	throws ConnectorException {
		RemoteBackOffice remoteBackOffice = getRemoteBackOffice();
			
		Vector<ScheduledTask> res = null;

		try {
			res = remoteBackOffice.getScheduledTasks();
				
			if (res == null) {
				String message = "Calypso ScheduledTasks is null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}

		} catch (Exception remoteException) {
			String message = "Calypso ScheduledTasks remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
				
		return res;
	}		
		
	public Vector<ScheduledTaskExec> getScheduledTaskExecs()
	throws ConnectorException {
		RemoteBackOffice remoteBackOffice = getRemoteBackOffice();
		
		String whereClause = "exec_time > " + ioSQL.date2String(new Date());
		
		Vector<ScheduledTaskExec> res = null;
		
		try {
			res = remoteBackOffice.getScheduledTaskTime(null, whereClause);
				
			if (res == null) {
				String message = "Calypso ScheduledTaskExecs null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}

		} catch (Exception remoteException) {
			String message = "Calypso ScheduledTaskExecs remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
				
		return res;
	}		
		
		
		
	public RemoteReferenceData getRemoteReferenceData()
	throws ConnectorException {
		DSConnection dsConnection = getDSConnection();
		
		RemoteReferenceData ref = null;
		if (dsConnection != null) {
			ref = dsConnection.getRemoteReferenceData();
		} else {
			String message = "Calypso DSConnection is used for RemoteReferenceData null or closed: " + getType() + "--" +  getName();
			reportFailure(message);		
		}
		
		if (ref == null) {
			String message = "Calypso RemoteReferenceData is null: " + getType() + "--" +  getName();
			reportFailure(message);
		}
		
		return ref;
	}
	
	public EngineConfig getEngineConfig()
	throws ConnectorException {
		RemoteReferenceData remoteReferenceData = getRemoteReferenceData();
		
		EngineConfig engineConfig = null;
		try {
			if (remoteReferenceData != null) {
				engineConfig = remoteReferenceData.getEngineConfig();
			} else {
				String message = "Calypso RemoteReferenceData is used for engineConfig null: " + getType() + "--" +  getName();
				reportFailure(message);
			}
			
			if (engineConfig == null) {
				String message = "Calypso EngineConfig is null: " + getType() + "--" +  getName();
				reportFailure(message);
			}		
		} catch (Exception remoteException) {
			String message = "Calypso EngineConfig remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
		
		return engineConfig;
	}
	
	public TradeFilter getTradeFilter(String tradeFilterName) 
	throws ConnectorException {
		RemoteReferenceData remoteReferenceData = getRemoteReferenceData();
		
		TradeFilter tradeFilter = null;
		try {
			if (remoteReferenceData != null) {
				tradeFilter = remoteReferenceData.getTradeFilter(tradeFilterName);
			} else {
				String message = "Calypso RemoteReferenceData is used for engineConfig null: " + getType() + "--" +  getName();
				reportFailure(message);
			}
			
			if (tradeFilter == null) {
				String message = "Calypso TradeFilter is null: " + getType() + "--" +  getName();
				reportFailure(message);
			}		
		} catch(Exception remoteException) {
			String message = "Calypso TradeFilter error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
		
		return tradeFilter;
	}	
	
	public Hashtable<String,CacheLimit> getCacheLimits() 
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
		
		Hashtable<String,CacheLimit> limits = null;

		try {
			limits = remoteAccess.getCacheLimits(CacheUtil.getDataServerName());
			
			if (limits == null) {
				String message = "Calypso CacheLimits is null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}
			
		} catch (Exception remoteException) {
			String message = "Calypso CacheLimits remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
			
		return limits;
	}		
	
	public int[] getDBConnectionsCount()
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
		
		int[] values = null; 
		
		try {
			values = remoteAccess.getDBConnectionsCount();
			
			if (values == null) {
				String message = "Calypso DBConnectionsCount is null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}
			
		} catch (Exception remoteException) {
			String message = "Calypso DBConnectionsCount remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
			
		return values;		
	}
	
	public Vector getConnectedClients()
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
		
		Vector values = null; 
		
		try {
			values = remoteAccess.getConnectedClients();
			
			if (values == null) {
				String message = "Calypso ConnectedClients is null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}
			
		} catch (Exception remoteException) {
			String message = "Calypso ConnectedClients remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
			
		return values;		
	}
	
	public Vector getVerifiedConnectedClients()
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
		
		Vector values = null; 
		
		try {
			values = remoteAccess.getVerifiedConnectedClients();
			
			if (values == null) {
				String message = "Calypso Verified ConnectedClients is null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}
			
		} catch (Exception remoteException) {
			String message = "Calypso ConnectedClients remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
			
		return values;		
	}	
	
	public boolean isDataServerConnected2EventServer()
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();

		try {
			boolean value = remoteAccess.isDataServerConnected2EventServer();
			return value;
		} catch (Exception remoteException) {
			String message = "Calypso isDataServerConnected2EventServer remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}
		
		return false;
	}		
	
	
	
	public EventStats getEventStats() 
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
		
		EventStats eventStats = null;	
			
		try {
			eventStats = remoteAccess.getEventStats();

			if (eventStats == null) {
				String message = "Calypso EventStats is null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}
		} catch (Exception remoteException) {
			String message = "Calypso EventStats remote error: " + getType() + "--" +  getName();
			Exception exc = new Exception(message, remoteException);
			reportFailure(exc);	
		}		
			
		return eventStats;
	}
	
	public Hashtable<String,CacheMetrics> getCacheMetrics() 
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
				
		Hashtable<String,CacheMetrics> metrics = null;

		try {
			metrics = remoteAccess.getCacheMetrics();
		
			if (metrics == null) {
				String message = "Calypso CacheMetrics is null: " + getType() + "--" +  getName();
				reportFailure(message);			
			}
		} catch (Exception remoteException) {
			reportFailure(remoteException);
		}
		return metrics;
	}	

	public String getConnectorContext() {
		return calypsoConnectorConfig.getConnectorContext();
	}

	public int getEventBufferCurrentMax() throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();

		try {
			int res = remoteAccess.getEventBufferCurrentMax();
			return res;
		} catch (Exception remoteException) {
			reportFailure(remoteException);
		}
		
		return 0;
	}
	
	public Hashtable<String,Integer> getNumberOfPendingEventProcessing(String engineName)
	throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
		
		Hashtable<String,Integer> res = null;
		
		try {
			res = remoteAccess.getPendingProcessingCount(engineName);
		} catch (Exception remoteException) {
			reportFailure(remoteException);
		}
		
		return res;
	}	
	
	public List<CalypsoEventObject>  getPendingEventProcessing() throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();
		
		List<CalypsoEventObject> enginePendings = new ArrayList<CalypsoEventObject>();
		
		String query = "SELECT ps_event.EVENT_ID, ps_event.OBJECT_ID, ps_event.EVENT_CLASS, engine_config.ENGINE_NAME FROM ps_event, engine_config WHERE ps_event.engine_id = engine_config.engine_id";
		try {
			Vector pendings = remoteAccess.executeSelectSQL(query);
			for (int i=2; i<pendings.size(); i++) {
				Vector values = (Vector)pendings.get(i);
				String eventId = (String)values.get(0);
				String objectId = (String)values.get(1);
				String eventClass = (String)values.get(2);
				String engine = (String)values.get(3);
				enginePendings.add(new CalypsoEventObject(Integer.valueOf(eventId), Integer.valueOf(objectId), eventClass, engine));
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
		
		return enginePendings;
	}

	public int getEventBufferMaxSize() throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();

		try {
			int res = remoteAccess.getEventBufferMaxSize();
			return res;
		} catch (Exception remoteException) {
			reportFailure(remoteException);
		}
		
		return 0;
	}

	public int getEventBufferSize() throws ConnectorException {
		RemoteAccess remoteAccess = getRemoteAccess();

		try {
			int res = remoteAccess.getEventBufferSize();
			return res;
		} catch (Exception remoteException) {
			reportFailure(remoteException);
		}
		
		return 0;
	}
	
	public String getEngineParam(String engineName, String paramName) 
	throws ConnectorException {
		RemoteAccess remote = getRemoteAccess();
		
		String value = null;
		
		try {
			value = remote.getEngineParam(engineName, paramName);
		} catch (Exception remoteException) {
			reportFailure(remoteException);
		}
		
		return value;	
	}

	public String getEntityName(int entityId) throws ConnectorException {
		String name = legalEntities.get(entityId);
		if (name == null) {
			RemoteReferenceData remote = getRemoteReferenceData();
			try {
				LegalEntity entity = remote.getLegalEntity(entityId);
				name = entity.getName();
				legalEntities.put(entityId, name);
			} catch (RemoteException e) {
				reportFailure(e);
			}
		}		
		return name;
	}		
	
	public Book getBook(int bookId) throws ConnectorException {
		Book book = books.get(bookId);
		if (book == null) {
			RemoteReferenceData remote = getRemoteReferenceData();
			try {
				book = remote.getBook(bookId);
				books.put(bookId, book);
			} catch (RemoteException e) {
				reportFailure(e);
			}
		}		
		return book;
	}

	public RdbmsConnector getDatabaseConnector() {		
		return databaseConnector;
	}

	public KickOffCutOffConfig getKickOffCutOffConfig(int kickOffCutOffConfigId) throws ConnectorException {
		KickOffCutOffConfig config = kickOffCutOffConfigs.get(kickOffCutOffConfigId);
		if (config==null){
			try {
				config = getRemoteBackOffice().getKickOffCutOffConfig(kickOffCutOffConfigId);
				if (config!=null)
					kickOffCutOffConfigs.put(kickOffCutOffConfigId, config);
				
			} catch (RemoteException e) {
				reportFailure(e);
			} 
		}
		return config;
	}

	public void subscribeToCalypsoEvents(CalypsoListener listener, List<String> events) {
		if(evtListener!=null)
			evtListener.subscribeToEvents(listener, events);		
	}

	public void unsubscribeToCalypsoEvents(CalypsoListener listener) {
		if(evtListener!=null)
			evtListener.unsubscribeToEvents(listener);
	}

	public long[] getEventServerStats() {
		try {
			return getEventListener().getEventServerStats();
		} catch (ConnectorException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
