package com.fsi.monitoring.kpi.monitor.calypso;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.calypso.tk.bo.BOMessage;
import com.calypso.tk.bo.BOTransfer;
import com.calypso.tk.core.Trade;
import com.calypso.tk.core.Util;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.EngineConfig;
import com.calypso.tk.event.PSEventAccounting;
import com.calypso.tk.event.PSEventCre;
import com.calypso.tk.event.PSEventException;
import com.calypso.tk.event.PSEventLog;
import com.calypso.tk.event.PSEventMessage;
import com.calypso.tk.event.PSEventMonitor;
import com.calypso.tk.event.PSEventStats;
import com.calypso.tk.event.PSEventSubscription;
import com.calypso.tk.event.PSEventTask;
import com.calypso.tk.event.PSEventTrade;
import com.calypso.tk.event.PSEventTransfer;
import com.calypso.tk.mo.TradeFilter;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.service.RemoteAccess;
import com.calypso.tk.service.RemoteBackOffice;
import com.calypso.tk.service.RemoteReferenceData;
import com.calypso.tk.service.RemoteTrade;
import com.calypso.tk.util.ScheduledTask;
import com.calypso.tk.util.ScheduledTaskExec;
import com.calypso.tk.util.TradeArray;
import com.calypso.tk.util.TransferArray;
import com.fsi.monitoring.connector.calypso.IkrCalypsoEventListener;

public class CalypsoMonitorService extends Observable implements Observer {
	private static final Logger LOG = Logger.getLogger(CalypsoMonitorService.class);
	//Default maximum number of active connections 
	public static final int DEFAULT_CONNECTION_POOL_SIZE = 30;
	//Default time to wait when pool exhausted
	public static final int DEFAULT_TIME_WAIT_BLOCKED = -1;
	
	private CalypsoConnectionConfig config;
	private DSConnection dsConnection = null;
	private IkrCalypsoEventListener evtListener;
//	private Map<String, PSEventMonitor> engineStats = new HashMap<String, PSEventMonitor>();

//	private Hashtable<String,CacheMetrics> metrics = new Hashtable<String, CacheMetrics>();
//	private Hashtable<String,CacheLimit> limits = new Hashtable<String, CacheLimit>();
	private Vector<String> clients = null;
	private Vector<String> vclients = null;
	private Map<String, Boolean> processStates = new HashMap<String, Boolean>();
	
//	private boolean isOracle = false;
//	private boolean isSybase = false;
//	private DataSource dsSource;
	
	protected CalypsoMonitorService(CalypsoConnectionConfig config) {
		super();
		this.config = config;
	}

	private void initAll() throws Exception{	
//		Properties p = Defaults.getProperties(config.getCalypsoEnv());
//		Defaults.setProperties(p);
//		Defaults.setEnvName(config.getCalypsoEnv());
//		String driver = Defaults.getProperty(Defaults.DRIVER);
//		if (driver.indexOf("Oracle") >= 0)
//			isOracle = true;		
//		else if (driver.indexOf("Syb") >= 0)
//			isSybase = true;
//		
//		try {
//			Class.forName(driver);
//		} catch (ClassNotFoundException exc) {
//			LOG.fatal("SGBD Driver not found : " + exc);
//		}
//		PooledDataSourceFactory dataSourceFactory = new PooledDataSourceFactory();		
//		dsSource = dataSourceFactory.createDataSource(DEFAULT_TIME_WAIT_BLOCKED, 
//													  DEFAULT_CONNECTION_POOL_SIZE, 
//													  Defaults.getProperty(Defaults.DBURL), 
//													  Defaults.getProperty(Defaults.DBUSER), 
//													  Defaults.getProperty(Defaults.DBPASSWORD));
//		
//		if (dsSource == null)
//			throw new Exception("Impossible to create The Calypso <"+ config.getCalypsoEnv() +">  Database Datasource");
		
//		try {
//			ioSQL.connect(Defaults.getProperty(Defaults.DRIVER), 
//				      Defaults.getProperty(Defaults.DBURL),
//				      Defaults.getProperty(Defaults.DBUSER),
//				      Defaults.getProperty(Defaults.DBPASSWORD));
//		} catch (SQLException e) {
//			System.out.println("CalypsoMonitor : " + e.getMessage());
//			LOG.error(e.getMessage(), e);
//		}
//		if (dsConnection != null) 
//			initEventListener();
	}
	
	protected void stop() {
		evtListener.deleteObserver(this);
		evtListener.stop();
		evtListener = null;
		
//		dsConnection.disconnect();
//		dsConnection = null;		
	}
	
	private void initEventListener() {
//		evtListener = new CalypsoEventListener(dsConnection, config.getApplicationName());
//		System.out.println("Event Listener Instanciate : " + evtListener);
//		evtListener.start();
//		evtListener.addObserver(this);
	}	
	
//	public boolean isOracle() {
//		return isOracle;
//	}
//	
//	public boolean isSybase() {
//		return isSybase;
//	}

//	public DSConnection getDsConnection() {
//		return dsConnection;
//	}
	
//	public Hashtable<String,CacheMetrics> getDsCacheMetrics(){	
//		if (dsConnection != null)
//			try {
//				RemoteAccess remote = getRemoteAccess();
//				metrics = (remote != null) ? remote.getCacheMetrics() : metrics;
//			} catch (RemoteException e) {			
//				LOG.error(e.getMessage(), e);
//			}		
//		return metrics;
//	}
	
//	public Hashtable<String, CacheLimit> getDsCacheLimits() {
//		if (dsConnection != null)
//			try {
//				RemoteAccess remote = getRemoteAccess();
//				limits = (remote != null) ? remote.getCacheLimits(CacheUtil.getDataServerName()) : metrics;
//			} catch (RemoteException e) {			
//				LOG.error(e.getMessage(), e);
//			}		
//		return limits;
//	}
	
	public int getNumberOfClientConnected() {
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				clients = (remote != null) ? remote.getConnectedClients() : clients;
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return (clients != null) ? clients.size() : 0;
	}
	
	public int getNumberOfVerifiedClientConnected() {
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				vclients = (remote != null) ? remote.getVerifiedConnectedClients() : vclients;
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return (vclients != null) ? vclients.size() : 0;
	}
	
	public boolean isDataServerConnected2EventServer() {
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				return (remote != null)  ? remote.isDataServerConnected2EventServer() : false;
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return false;
	}
	
	public int[] getNumberOfDbConnection(){
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				return (remote != null) ? remote.getDBConnectionsCount() : new int[0];
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return new int[0];
	}
	
	public int getDsEventBufferLimitSize(){
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				return (remote != null) ? remote.getEventBufferMaxSize() : 0;
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return 0;
	}
	
	public int getDsEventBufferCurrentMaxSize(){
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				return (remote != null) ? remote.getEventBufferCurrentMax() : 0;
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return 0;
	}
	
	public int getDsEventBufferCurrentSize(){
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				return (remote != null) ? remote.getEventBufferSize() :0;
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return 0;
	}
	
	public Vector<ScheduledTaskExec> getScheduledTaskExecs(){
		String whereClause = "exec_time > " + ioSQL.date2String(new Date());
		if (dsConnection != null)
			try {
				RemoteBackOffice remote = getRemoteBackOffice();
				return (remote != null) ? remote.getScheduledTaskTime(null, whereClause) : new Vector<ScheduledTaskExec>();
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return new Vector<ScheduledTaskExec>();
	}
	
	public Vector<ScheduledTask> getAllScheduledTasks(){
		if (dsConnection != null)
			try {
				RemoteBackOffice remote = getRemoteBackOffice();
				return (remote != null) ? remote.getScheduledTasks() : new Vector<ScheduledTask>();
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return new Vector<ScheduledTask>();
	}
	
//	public EventStats getEventStats() {
//		if (dsConnection != null)
//			try {
//				RemoteAccess remote = getRemoteAccess();
//				return (remote != null) ? remote.getEventStats() : null;
//			} catch (RemoteException e) {
//				LOG.error(e.getMessage(), e);
//			}
//		return null;
//	}
	
//	public EngineConfig getEngineConfig() {
//		if (dsConnection != null)
//			try {
//				RemoteReferenceData remote = getRemoteReferenceData();
//				return (remote != null) ? remote.getEngineConfig() : null;
//			} catch (RemoteException e) {
//				LOG.error(e.getMessage(), e);
//			}
//		return null;
//	}
	
//	public boolean isApplicationRunning(String appName){
//		if (dsConnection != null)
//			try {
//				RemoteAccess remote = getRemoteAccess();
//				return (remote != null) ? remote.isApplicationRunning(appName) : false;
//			} catch (RemoteException e) {
//				LOG.error(e.getMessage(), e);
//			}
//		return false;
//	}
	
	public Hashtable<String,Integer> getNumberOfPendingEventProcessing(String engineName){
		if (dsConnection != null)
			try {
				RemoteAccess remote = getRemoteAccess();
				return (remote != null) ? remote.getPendingProcessingCount(engineName) : new Hashtable<String, Integer>();
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return new Hashtable<String, Integer>();
	}
	
//	public int getNumberOfConsumedEvent(String eventClass, String engineName) {
//		String query = "select count(*) from ps_event_consumed where event_class='"
//			+ eventClass + "' and engine_id=" + getEngineConfig().getId(engineName);	
//		Connection con = null;
//		Statement smt = null;
//		ResultSet rs = null;
//		int consumed = 0;
//		try {
//			con = dsSource.getConnection();
//			smt = con.createStatement();
//			rs = smt.executeQuery(query.toLowerCase());
//			if (rs.next())
//				consumed = rs.getInt(1);
//		} catch (SQLException e) {
//			LOG.error(e.getMessage(), e);
//		} catch (Exception e) {
//			LOG.error(e.getMessage(), e);
//		} finally {
//			try {
//				if (rs != null)
//					rs.close();
//				if (smt != null)
//					smt.close();
//				if (con != null)
//					con.close();
//			} catch (SQLException e) {
//				LOG.error(e.getMessage(), e);
//			}
//			
//		}
//		return consumed;
//	}

//	public PSEventMonitor requestEngineStats(String engineName) {
//		evtListener.requestEngineStats(engineName);
//		synchronized (engineStats) {
//			try {
//				engineStats.wait();
//			} catch (InterruptedException e) {
//				Log.error(this, e);
//			}
//		}	
//		PSEventMonitor monitor = engineStats.get(engineName);
//		return monitor;
//	}
	
//	protected void requestEngineMemory(String engineName) {
//		evtListener.requestEngineMemory(engineName);
//    }

	public long[] getEventServerStats() {
		return evtListener.getEventServerStats();
	}

	public int getEventServerMaxQueueSize() {
		return evtListener.getEventServerMaxQueueSize();
	}
	
	public RemoteReferenceData getRemoteReferenceData() {
		if (dsConnection == null)
			return null;
		RemoteReferenceData ref = dsConnection.getRemoteReferenceData();
		if (ref == null && dsConnection.isClosed()) {
			dsConnection = null;
			this.setChanged();
			this.notifyObservers(this);
		}
		return ref;
	}
	
	private RemoteAccess getRemoteAccess() {
		if (dsConnection == null)
			return null;
		RemoteAccess ref = dsConnection.getRemoteAccess();
		if (ref == null && dsConnection.isClosed()) {
			dsConnection = null;
			this.setChanged();
			this.notifyObservers(this);
		}
		return ref;
	}
	
	private RemoteBackOffice getRemoteBackOffice() {
		if (dsConnection == null)
			return null;
		RemoteBackOffice ref = dsConnection.getRemoteBO();
		if (ref == null && dsConnection.isClosed()) {
			dsConnection = null;
			this.setChanged();
			this.notifyObservers(this);
		}
		return ref;
	}
	
	private RemoteTrade getRemoteTrade() {
		if (dsConnection == null)
			return null;
		RemoteTrade ref = dsConnection.getRemoteTrade();
		if (ref == null && dsConnection.isClosed()) {
			dsConnection = null;
			this.setChanged();
			this.notifyObservers(this);
		}
		return ref;
	}
	
	public Map<String, Boolean> getProcessesState() {		
        EngineConfig ec;        
        if (dsConnection != null) {
			try {
				RemoteReferenceData ref = getRemoteReferenceData();
				if (ref != null) {			
					ec = ref.getEngineConfig();
					Vector<String> engines = Util.sort(ec.getNames());
			        for(int i = 0; i < engines.size(); i++) {
			            String name = (String) engines.elementAt(i);
			            processStates.put(name, false);
			        }	        
			        
			        List<String> apps = getActiveEngines();
			        for(int i = 0; i < apps.size(); i++) {
			            String app = apps.get(i);
			            if(processStates.get(app) != null) 
			            	processStates.put(app, true);
			        }
				}
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			} 
        }
		return processStates;
	}

	public List<String> getActiveEngines() {
		return evtListener.getActiveEngines();
	}
	
	public TradeFilter getTradeFilter(String name) {
		TradeFilter filter = null;
		RemoteReferenceData remote = getRemoteReferenceData();
		if (remote != null)
			try {
				filter = remote.getTradeFilter(name);
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		return filter;
	}
	
	public List<Trade> getTrades(TradeFilter filter) {
		return getTrades(filter, null);
	}
	
	public List<Trade> getTrades(String whereClause) {
		return getTrades(null, whereClause);
	}
	
	public List<BOTransfer> getTranfers(TradeFilter filter) {
		return getTranfers(filter, null);
	}
	
	public List<BOTransfer> getTranfers(String whereClause) {
		return getTranfers(null,whereClause);
	}
	
	private List<Trade> getTrades(TradeFilter filter, String whereClause) {
		List<Trade> trades = new ArrayList<Trade>();
		RemoteTrade remote = getRemoteTrade();
		if (remote != null){
			try {
				TradeArray array = null;
				if (filter != null) 
					array =  remote.getTrades(filter, null);
				else 
					array = remote.getTrades(null, whereClause, null);
				if (array != null && array.size() > 0)
					trades = Arrays.asList(array.getTrades());
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		}		
		return trades;
	}
	
	private List<BOTransfer> getTranfers(TradeFilter filter, String whereClause) {
		List<BOTransfer> transfers = new ArrayList<BOTransfer>();
		RemoteBackOffice remote = getRemoteBackOffice();
		if (remote != null){
			try {
				TransferArray array = null;
				if (filter != null)
					array = remote.getTransfers(whereClause, filter);
				else 
					array = remote.getTransfers(null, whereClause);
				if (array != null && array.size() > 0) 
					transfers = Arrays.asList(array.getTransfers());
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		}		
		return transfers;
	}
	
	public List<BOMessage> getMessages(String whereClause) {
		List<BOMessage> messages = new ArrayList<BOMessage>();
		RemoteBackOffice remote = getRemoteBackOffice();
		if (remote != null){
			try {
				remote.getMessages(null, whereClause);
			
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			}
		}		
		return messages;
	}

	public void update(Observable o, Object arg) {		
		if (arg instanceof PSEventMonitor) {
//			PSEventMonitor monitor = (PSEventMonitor) arg;
//			engineStats.put(monitor.getSource(), monitor);
//			synchronized (engineStats) {				
//				engineStats.notify();
//			}
		} else if (arg instanceof PSEventException) {
			processException((PSEventException) arg);
		} else if (arg instanceof PSEventTask) {
			this.setChanged();
			this.notifyObservers(arg);
		} else if (arg instanceof PSEventStats) {
			PSEventStats stats = (PSEventStats)arg;
		} else if (arg instanceof PSEventSubscription) {			
		} else if (arg instanceof PSEventTrade) {
			this.setChanged();
			this.notifyObservers(arg);
		} else if (arg instanceof PSEventTransfer) {
			this.setChanged();
			this.notifyObservers(arg);
		} else if (arg instanceof PSEventMessage) {
			this.setChanged();
			this.notifyObservers(arg);
		} else if (arg instanceof PSEventCre) {
			this.setChanged();
			this.notifyObservers(arg);
		} else if (arg instanceof PSEventAccounting) {
			this.setChanged();
			this.notifyObservers(arg);
		} else if (arg instanceof PSEventLog) {
			PSEventLog evt = (PSEventLog) arg;
			String className = evt.getClassName();
			StringBuffer content = evt.getContents();
			String source = evt.getSource();
			System.out.println();
		}
	}

	protected void processException(PSEventException event) {
		String exceptionstr = event.getException();
		String[] tmp = StringUtils.splitPreserveAllTokens(exceptionstr, '\n');
		String appName = tmp[0];
		String content = tmp[2]+ '\n' + getExceptionContent(tmp);
		this.setChanged();
		this.notifyObservers(new CalypsoException(new Date(System.currentTimeMillis()), appName, content));
	}
	
	private String getExceptionContent(String[] exceptions) {
		int i = 0;
		while(exceptions[i].indexOf("END") < 0) 
			i++;
		return exceptions[++i] + '\n' + exceptions[++i];
	}	
	
	public CalypsoConnectionConfig getConfig() {
		return config;
	}

	public void initConnection(DSConnection dsConnection) throws Exception {
		this.dsConnection = dsConnection;
		initAll();
	}
	
	public String getId() {
		return config.getKey();
	}
	
	public String getDsLog() {
		RemoteAccess remote = getRemoteAccess();
		if (remote != null) {
			try {
				byte[] contents = remote.getServerLog();
				return unzipLog(contents).toString();
			} catch (RemoteException e) {
				LOG.error(e.getMessage(), e);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	private StringBuffer unzipLog(byte bytes[]) throws IOException,InterruptedException {
		try {
		    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		    GZIPInputStream gzin= new GZIPInputStream(is);
		    InputStreamReader reader= new  InputStreamReader(gzin);
		    BufferedReader buf=new BufferedReader(reader);
		    String line=null;
		    StringBuffer log = new StringBuffer(1024);
		    while((line=buf.readLine()) != null) {
		    	log.append(line).append("\n");
		    }
		    return log;
		} catch (Exception e) {
			LOG.debug(e.getMessage(), e);
		    return null;
		}
    }
}
