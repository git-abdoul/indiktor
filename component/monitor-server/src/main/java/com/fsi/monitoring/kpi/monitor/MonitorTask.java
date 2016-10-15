package com.fsi.monitoring.kpi.monitor;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fsi.fwk.scheduling.ScheduleIterator;
import com.fsi.fwk.scheduling.Scheduler;
import com.fsi.fwk.scheduling.SchedulerTask;
import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.admin.IkrEventLog;
import com.fsi.monitoring.admin.IkrEventStatsAttribute;
import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.config.RealTimeBackBeanName;
import com.fsi.monitoring.config.RealTimeBackContext;
import com.fsi.monitoring.connector.Connector;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.ConnectorListener;
import com.fsi.monitoring.connector.ConnectorManager;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfigExtension;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.monitor.MonitorConfigAttributeKey;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.monitor.sqlQuery.resourceData.GenericSQLQueryResourceData;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;

public abstract class MonitorTask extends Observable implements Monitor, ConnectorListener {	
	private static final Logger LOG = Logger.getLogger(MonitorTask.class);	
	
	protected static final String ALL_WILDCARD ="*";
	private static final int LOG_MAX_CAPACITY = 10;
	
	protected Map<Integer, IkrResourceData> resourceDatas;	
	protected Map<Integer,List<IkrCategoryResource>> activities;
	
	private Map<String,Connector> connectors;
	private Set<Long> startedConnectorIds;
	
	protected String[] views;
	private Map<String, Method> monitorMethods;
	private Map<String, Method> extensionMethods;	
	private Map<String, Method> resourceDataMethods;
	
	private boolean initDone = false;
	protected MonitorConfig monitorConfig;
	private Date startTime;
	
	private String type;
	private String typeLabel;
	protected String logicalEnv;
	private String domainView;
	
	protected Map<Integer, MonitorExtension> monitorExtensions;
	
	private Scheduler startScheduler;
	private Scheduler endScheduler;	
	private CaptureTaskScheduler captureTaskScheduler;
	private CaptureTask oneShotCaptureTask;
	
	protected IkrMonitorSchedulerConfig schedulerConfig;
	
	protected List<IkrAdminLogging> eventLogs;
	protected Map<String, String> eventStats;
	protected ComponentStatus status;
	
	public MonitorTask(){
		monitorMethods = new HashMap<String, Method>();	
		resourceDataMethods = new HashMap<String, Method>();	
		resourceDatas = new HashMap<Integer, IkrResourceData>();
		connectors = new HashMap<String,Connector>();
		startedConnectorIds = new HashSet<Long>();
		
		eventLogs = new ArrayList<IkrAdminLogging>();
		eventStats = new HashMap<String, String>();
		status = ComponentStatus.NOT_RUNNING;
	}

	protected abstract void initConnection() throws Exception;	
	protected abstract void preStart();	
	protected abstract void preFetchs()throws Exception;
	protected abstract void postFetchs() throws Exception;	
	
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
	
	public Date getStartTime() {
		return startTime;
	}

	public void stop() {
		stopConnector();
		
		if (oneShotCaptureTask!=null)
			oneShotCaptureTask.stopMe();
		if (startScheduler != null)
			startScheduler.cancel();
		if (captureTaskScheduler != null) {
			captureTaskScheduler.stopMe();
			captureTaskScheduler.cancel();
		}
		if (endScheduler != null)
			endScheduler.cancel();
		
		status = ComponentStatus.NOT_RUNNING;
		notifyEventLog();
	}
	
	public void start()  throws Exception {	
		try {
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Collector <" + getName() + "> : Initializing"));
			initMonitor();
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Collector <" + getName() + "> : Connecting to Connector"));
			startConnector();
			scheduleMonitor();	
			startTime = new Date();	
			LOG.debug("Monitor <" + getName() + "> started at " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(startTime));
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO,"Started at " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(startTime)));
//			status = ComponentStatus.NOTHING_TO_REPORT;
			notifyEventLog();
		} catch (Exception e) {	
			status = ComponentStatus.NOT_RUNNING;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Error while initializing : " + e.getMessage()));
			notifyEventLog();
			stop();
			LOG.error("Error while initializing Collector " + getId() +":" + getName(), e);	
			throw new Exception(e);
		}
	}
	
	private void scheduleMonitor() {
		
		ScheduleIterator startIterator = schedulerConfig.getStartIterator();
		if (startIterator != null) {
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Due to start at " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(startIterator.next())));
			LOG.info("Collector <" + getName() + "> is due to start at " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(startIterator.next()));
			notifyEventLog();
		}
		
		if (IkrMonitorSchedulerConfig.ONE_SHOT.equals(schedulerConfig.getType())) {
			if (IkrMonitorSchedulerConfig.NONE.equals(schedulerConfig.getMode())) {
				oneShotCaptureTask = new CaptureTask();
				oneShotCaptureTask.run();
			} 
			else {
				startScheduler.schedule(new SchedulerTask() {
	                public void run() {
	                	oneShotCaptureTask = new CaptureTask();
	    				oneShotCaptureTask.run();
	                }            
	                
	            }, schedulerConfig.getStartIterator());		
			}
		}
		else if (IkrMonitorSchedulerConfig.RECURRING.equals(schedulerConfig.getType())) {			
			if (IkrMonitorSchedulerConfig.NONE.equals(schedulerConfig.getMode())) {
				if (captureTaskScheduler == null)
    				captureTaskScheduler = new CaptureTaskScheduler();		        
				captureTaskScheduler.run();
    		}
    		else {		
    			if (captureTaskScheduler == null)
    				captureTaskScheduler = new CaptureTaskScheduler();		        
				startScheduler.schedule(captureTaskScheduler, schedulerConfig.getStartIterator());		    		    	
		    	
		    	endScheduler.schedule(new SchedulerTask() {
		            public void run() {
		            	captureTaskScheduler.stopMe();
		            }
		        }, schedulerConfig.getEndIterator());
    		}   	
		}
	}
	
//	protected boolean startConnector() {
//		boolean isOk = true;
//		ConnectorManager connectorManager = (ConnectorManager)RealTimeBackContext.getBean(RealTimeBackBeanName.connectorManager);		
//		Collection<Integer> connectorIds = monitorConfig.getConnectorConfigIds();
//		connectors = new HashMap<String,Connector>(connectorIds.size());
//		
//		for (int connectorId : connectorIds) {
//			if (monitorConfig.isAutoStart()) {
//				isOk = isOk && connectorManager.startConnector(connectorId, monitorConfig.getId());
//			}
//			
//			if (isOk) {
//				AbstractConnector connector = (AbstractConnector)connectorManager.getConnector(connectorId);
//				connectors.put(connector.getType(), connector);
//			}			
//		}
//		
//		if (monitorExtensions.size()>0) {
//			for (MonitorExtension extension : monitorExtensions.values()) {
//				extension.setConnectors(connectors);
//			}
//		}
//		
//		return isOk;
//	}
	
	protected void startConnector() {
		ConnectorManager connectorManager = (ConnectorManager)RealTimeBackContext.getBean(RealTimeBackBeanName.connectorManager);		
		Collection<Integer> connectorIds = monitorConfig.getConnectorConfigIds();	
		
		for (int connectorId : connectorIds) {
			connectorManager.startConnector(connectorId, this);
		}
		
		if (monitorExtensions.size()>0) {
			for (MonitorExtension extension : monitorExtensions.values()) {
				extension.setConnectors(connectors);
			}
		}
	}	
	
	public void onStartedEvt(Connector connector) {
		connectors.put(connector.getType(), connector);
		startedConnectorIds.add(connector.getId());		
	}

	public void onDisconnectEvt(Connector connector) {
		connectors.remove(connector.getType());
		startedConnectorIds.remove(connector.getId());			
	}

	public long getListenerId() {
		return getId();
	}

	private void stopConnector() {
		ConnectorManager connectorManager = (ConnectorManager)RealTimeBackContext.getBean(RealTimeBackBeanName.connectorManager);		
		Collection<Integer> connectorIds = monitorConfig.getConnectorConfigIds();
		for (int connectorId : connectorIds) {
			connectorManager.stopConnector(connectorId, this);
		}
		if (monitorExtensions.size()>0) {
			for (MonitorExtension extension : monitorExtensions.values()) {
				extension.setConnectors(null);
			}
		}
	}
	
	protected void load() throws Exception, Throwable {
		preFetchs();
		
		if (monitorExtensions.size()>0) {
			for (MonitorExtension extension : monitorExtensions.values()) {
				extension.preFetchs();
			} 
		}
		
		for(MetricDomainConfigResource resource : monitorConfig.getMetricDomainConfig().getResources()){
			if(resource.isEnabled()) {
				String methodName = "fetch"+resource.getResource().getResourceName().toUpperCase();				
				try {
					IkrResourceData resourceData = null;	
					if (monitorExtensions.size()>0) {
						int priority = 1;
						for (int i=0; i<monitorExtensions.size(); i++) {
							MonitorExtension extension = monitorExtensions.get(priority);						
							Method method = getMethod(methodName, extension, extensionMethods);
							if (method != null) {
								resourceData = (IkrResourceData)method.invoke(extension, null);
								break;
							}
						}
					}
					else {
						Method method = getMethod(methodName, this, monitorMethods);
						if (method != null) {
							resourceData = (IkrResourceData)method.invoke(this, null);
						}
					}
					
					if (resourceData != null)
						resourceDatas.put(resource.getResource().getId(), resourceData);
					
				} catch (InvocationTargetException invocationTargetExc) {					
					throw invocationTargetExc.getTargetException();
				}
			}
		}		
	}
	
	protected void fetchs() throws Throwable {		
		for(int metricDomainResourceId : activities.keySet()) {
			List<IkrValue> ikrValues = new ArrayList<IkrValue>();
			List<IkrCategoryResource> categoryResources = activities.get(metricDomainResourceId);
			for (IkrCategoryResource resource : categoryResources) {
				if (resource.isActivated()) {
					IkrResourceData data = resourceDatas.get(metricDomainResourceId);
					if (data != null) {						
						Map<String, Object> values = null;
						if (data instanceof GenericSQLQueryResourceData) {
							GenericSQLQueryResourceData resourceData = (GenericSQLQueryResourceData)data;
							Map<String, Map<String, Object>> resourceValues = resourceData.getQueryResult();
							values = resourceValues.get(resource.getName());
						}
						else {
							String methodName = "get"+resource.getName().substring(0, 1).toUpperCase() + resource.getName().substring(1);
							Method method = getMethod(methodName, data, resourceDataMethods);
							
							if (method != null) {
								values = (Map<String, Object>)method.invoke(data, null);
							} 
						}						
						
						if (values!=null)
							data.getIkrValues(values, getId(), resource.getIkrStaticDomainId(), ikrValues);
					}
				}
			}
			
			try {
				notifyChange(ikrValues);	
				status = ComponentStatus.NOTHING_TO_REPORT;
			} catch (Exception e) {
				status = ComponentStatus.ERROR_OCCURED;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Error Saving ikrValues : " + e.getMessage()));
				LOG.error("Error when retrieving/saving ikrValues, monitor " + getId() +":" + getName(), e);
			}
			eventStats.put(IkrEventStatsAttribute.LAST_FETCH_DATE_TIME, (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(new Date()));
			notifyEventLog();
		}
	}
	
	private Method getMethod(String methodName, Object obj, Map<String, Method> methods) {
		String key = methodName + "_" + obj.getClass().getName();
		Method method = methods.get(key);		
		if (method == null) {		
			try {
				method = obj.getClass().getMethod(methodName, null);
				if (method != null) {
					methods.put(key, method);
				}
			} catch (SecurityException sec) {
				status = ComponentStatus.ERROR_OCCURED;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, sec.getMessage()));
				notifyEventLog();
				LOG.error(sec);
			} catch (NoSuchMethodException noSuchMeth) {
				status = ComponentStatus.ERROR_OCCURED;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, noSuchMeth.getMessage()));
				notifyEventLog();
				LOG.error(noSuchMeth);
			}		
		}		
		return method;
	}	
	
	public final void initMonitor() throws Exception {
		init();
		initConnection();		
	}
	
	protected void init() throws Exception {		
		// Load Activities
		if (monitorConfig.isOnTheFly()) {
			activities = new HashMap<Integer, List<IkrCategoryResource>>();
			try {
				MetricDomainConfigResource configResource = monitorConfig.getMetricDomainConfig().getResources().get(0);
				String ikrCategoryResourceIdsStr = getAttribute(MonitorConfigAttributeKey.CATEGORY_RESOURCE);
				if (ikrCategoryResourceIdsStr!=null&&ikrCategoryResourceIdsStr.length()>0) {
					List<Integer> ids = new ArrayList<Integer>();
					for (String str : ikrCategoryResourceIdsStr.split(",")) {
						ids.add(Integer.parseInt(str));
					}
					List<IkrCategoryResource> categoryResources = PMFactory.getDataModelPM().getIkrCategoryResources(ids);
					if (categoryResources.size()==0)
						throw new Exception("Error occured when getting Metric Domain Config Resource : No Category Resource found");
					activities.put(configResource.getResource().getId(), categoryResources);
				}
			}
			catch (Exception e) {
				throw new Exception("Error occured when getting Metric Domain Config Resource : " + e.getMessage());
			}
		}
		else {
			activities = PMFactory.getDataModelPM().getMonitorActivities(getId());	
		}
		
		// Set SchedulerConfig
		schedulerConfig = monitorConfig.getSchedulerConfig();
		if (schedulerConfig.getStartIterator() != null)
			startScheduler = new Scheduler();
		if (schedulerConfig.getEndIterator() != null)
			endScheduler = new Scheduler();				
	}
	
	public long getId() {
		return monitorConfig.getId();
	}
	
	public String getName() {
		return typeLabel + " " + domainView + " : " + monitorConfig.getContext();
	}
	
	public long getCaptureDelay() {
		long delay = 0;
		if (schedulerConfig != null)
			delay = schedulerConfig.getDelay();
		return delay;
	}
	
	public Map<String, String> getBusinessFilterSet() {
		Map<String, String>  filters = new HashMap<String, String>();
		String attributes = monitorConfig.getAttribute("BUSINESS_FILTERS");
		if (attributes != null && attributes.length()>0) {
			for (String filter : attributes.split(":")) {
				String[] tmp = filter.split("=");
				filters.put(tmp[0], tmp[1]);
			}
		}
		return filters;
	}

	public void setMonitorConfig(MonitorConfig monitorConfig) {
		initDone = true;
		this.monitorConfig = monitorConfig;
		
		monitorExtensions = new HashMap<Integer, MonitorExtension>();
		extensionMethods = new HashMap<String, Method>();
		
		List<MetricDomainConfigExtension> extensionConfigs = monitorConfig.getMetricDomainConfig().getExtensionConfigs();
		for (MetricDomainConfigExtension extensionConf : extensionConfigs) {
			String extensionClassName = extensionConf.getClassName();
			if(extensionClassName!= null && extensionClassName.length()>0) {
				try {
					Class<MonitorExtension> extensionClass = (Class<MonitorExtension>) Class.forName(extensionClassName);
					MonitorExtension monitorExtension = extensionClass.newInstance();					
					monitorExtensions.put(extensionConf.getPriority(), monitorExtension);
				} catch (ClassNotFoundException e) {
					status = ComponentStatus.NOT_RUNNING;
					addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
					LOG.error(e);
					notifyEventLog();
				} catch (InstantiationException e) {
					status = ComponentStatus.NOT_RUNNING;
					addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
					LOG.error(e);
					notifyEventLog();
				} catch (IllegalAccessException e) {
					status = ComponentStatus.NOT_RUNNING;
					addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
					LOG.error(e);
					notifyEventLog();
				}			
			}
			else {
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.WARNING, "No className has been set for " + extensionConf.getDescription()));
				LOG.error("No className has been set for " + extensionConf.getDescription());
				notifyEventLog();
			}
		}
		
		try {
			LogicalEnv env = PMFactory.getDataModelPM().getLogicalEnv(monitorConfig.getLogicalEnvId());
			if (env!=null)
				logicalEnv = env.getName();
			
			IkrStaticDomain domain = PMFactory.getDataModelPM().getIkrStaticDomain(monitorConfig.getMetricDomainConfig().getIkrStaticDomainId());
			type = (domain!=null)?domain.getDomainValue():"";
			typeLabel = (domain!=null)?domain.getLabel():"";
			
			String domainView = "";
			String views = monitorConfig.getAttribute("METRIC_VIEW");
			if (views!=null && views.length()>0) {
				domainView = "[";
				String[] items = views.split(":");
				int i = 0;
				for(String item : items) {
					domainView = domainView + item;
					if (i<items.length-1)
						domainView = domainView + ",";
					i++;
				}
				domainView = domainView + "]";				
			}
			this.domainView = domainView;
			
			ConnectorManager connectorManager = (ConnectorManager)RealTimeBackContext.getBean(RealTimeBackBeanName.connectorManager);
			Collection<Integer> connectorIds = monitorConfig.getConnectorConfigIds();
			for (int connectorId : connectorIds) {
				connectorManager.addListener(connectorId, this);
			}
			
			if (monitorExtensions.size()>0) {
				for (MonitorExtension extension : monitorExtensions.values()) {
					extension.setMonitorConfig(monitorConfig);
					extension.setType(domain);
					extension.setLogicalEnv(env);
				}
			}
			
		} catch(Exception exc) {
			initDone = false;
			status = ComponentStatus.INVALID;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Impossible to configure the collector : " + exc.getMessage()));
			LOG.error("Impossible to configure monitor", exc);
			notifyEventLog();
		}
	}
	
	public MonitorConfig getMonitorConfig() {
		return monitorConfig;
	}
	
	private boolean isConnectorAvailable() {
		boolean isOk = true;
		for (int id : monitorConfig.getConnectorConfigIds()) {
			isOk = isOk && startedConnectorIds.contains((long)id);			
		}
		return isOk;
	}
	
	class CaptureTaskScheduler extends SchedulerTask {
		CaptureTask captureTask;
		Scheduler captureScheduler;
		
        public void run() {
        	captureScheduler = new Scheduler();
        	captureTask = new CaptureTask();        	
        	captureScheduler.schedule(captureTask, schedulerConfig.getDelayIterator());
        }
        
        public void stopMe() {
        	if (captureScheduler!=null && captureTask!=null) {
        		captureTask.stopMe();
        		captureScheduler.cancel();
        		LOG.info("Collector <" + getName() + "> stop fetching at " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(new Date()));
        		System.out.println("Collector <" + getName() + "> stop fetching at " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(new Date()));
        		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Stopped fetching at " + (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(new Date())));
        		notifyEventLog();
        	}
        }		
	}
	
	class CaptureTask extends SchedulerTask {		
		private boolean preStartDone;
		private volatile boolean stop;
		
		public CaptureTask() {
			super();
			preStartDone = false;
//			preStart();
		}

		public void run() {
			if (!preStartDone) {
				while(!isConnectorAvailable()&&!stop) {
					status = ComponentStatus.ERROR_OCCURED;
					addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Connector Not Available"));
					notifyEventLog();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				if(!stop) {
					preStart();
					preStartDone = true;
				}
			}
			
			if (!stop) {
				String msg = "Collector <" + getId() + " : " + monitorConfig.getContext() + "> ";
				if (isConnectorAvailable()) {
					msg = msg + " | Connector Available - Ready To fetch";
					status = ComponentStatus.NOTHING_TO_REPORT;
					addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Connector Available - fetching"));
					System.out.println(msg);
					notifyEventLog();				
					doCapture();			
					addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "Connector Available - fetch done"));
					notifyEventLog();	
				}
				else {
					msg = msg + " | Connector Not Available - Can't fetch";
					status = ComponentStatus.ERROR_OCCURED;
					addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Connector Not Available - Can't fetch"));
					System.out.println(msg);
					notifyEventLog();
				}			
				LOG.debug(msg);		
			}
		}
		
		public void stopMe() {
			stop = true;
		}
	}	
	
	private void doCapture() {
		try {			
			ConnectorManager connectorManager = (ConnectorManager)RealTimeBackContext.getBean(RealTimeBackBeanName.connectorManager);		
			Collection<Integer> connectorIds = monitorConfig.getConnectorConfigIds();				
			for (int connectorId : connectorIds) {
				connectorManager.checkStatus((long)connectorId);
			}
			
			load();
			
			fetchs();
			
			postFetchs();
			if (monitorExtensions.size()>0) {
				for (MonitorExtension extension : monitorExtensions.values()) {
					extension.postFetchs();
				} 
			}
			status = ComponentStatus.NOTHING_TO_REPORT;
		} 
		catch(ConnectorException cexc) {
			status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Connector problem : " + cexc.getMessage()));
			LOG.error("Connector problem", cexc);
			notifyEventLog();
		} 
		catch (FetchException fexc) {
			status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Fetch problem : " + fexc.getMessage()));
			LOG.error("Fetch problem", fexc);
			notifyEventLog();
		}
		catch (Throwable  exc) {
			status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, "Error in the doCapture process : " + exc.getMessage()));
			LOG.error("Error in the doCapture process", exc);
			notifyEventLog();
		}
	}
	
	protected boolean accept(String value) {
		boolean accepted = false;
		if (value != null && value.length()>0){
			Map<String, String> filters = getBusinessFilterSet();
			if (filters.size() == 0)
				accepted = true;
			else {
				for (String key : filters.keySet()) {
					String filter = filters.get(key);
					if (value.contains(filter)) 
						accepted = true;
				}	
			}
		}
		return accepted;
	}
	
/* ============== Filter ==================== */
	
	/**
	 * @return true if the name contains one the component of the filter
	 */
	protected boolean accepts(String name, String[] filter){
		if(filter==null || filter.length==0)
			return false;
		if(name==null || name.length()==0)
			return false;
		String filterComponent;
		boolean accepted = false;
		int filterInd = 0;
		while(!accepted && filterInd<filter.length){
			filterComponent = filter[filterInd];
			accepted = name.contains(filterComponent) || filterComponent.equals(ALL_WILDCARD);
			filterInd++;
		}		
		return accepted;
	}
	
	/**
	 * @return true if the name contains one the component of the filter
	 */
	protected boolean accepts(String[] names, String[] filter){
		boolean ret = false;
		for(int i=0; i<names.length; i++) {
			ret = accepts(names[i], filter);
			if (ret == true)
				break;
		}
		return ret;
	}
	
	protected Collection<Connector> getConnectors() {
		return connectors.values();
	}
	
	protected Connector getConnector(String type) {
		return connectors.get(type);
	}

	public boolean isAutoStart() {
		return monitorConfig.isAutoStart();
	}
	
	private void notifyChange(List<IkrValue> ikrValues) {
		if (ikrValues !=null && !ikrValues.isEmpty()) {
			this.setChanged();
			this.notifyObservers(ikrValues);
		}
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

	public String getType() {
		return type;
	}

	public String getLogicalEnv() {
		return logicalEnv;
	}	
	
	public String getAttribute(String key) {
		return monitorConfig.getAttribute(key);
	}
	
	public Map<String, String> getAttributes() {
		return monitorConfig.getAttributes();
	}

	public IkrEventLog getLog() {
		return new IkrEventLog(getId(), typeLabel, startTime, AdminComponent.COLLECTOR, status, monitorConfig.getContext(), getLogicalEnv(), eventLogs, eventStats);
	}
}
