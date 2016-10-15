package com.fsi.toolkits.dataCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;
import com.fsi.toolkits.config.ToolkitContext;


public class DefaultDataCollectionConfig {
	
	private String logicalEnvName;	
	
	private Map<String, Integer> connectorIds;
	
	private static Map<String, String> metricDomainImpl = new HashMap<String, String>();
	static {
		metricDomainImpl.put("INDIKTOR_TRADE_ACTIVITY", "com.fsi.monitoring.kpi.monitor.calypso.transaction.CalypsoTradeActivityMonitor");
		metricDomainImpl.put("INDIKTOR_MESSAGE_ACTIVITY", "com.fsi.monitoring.kpi.monitor.calypso.confirmation.CalypsoMessageActivityMonitor");
		metricDomainImpl.put("INDIKTOR_DOCUMENT_ACTIVITY", "com.fsi.monitoring.kpi.monitor.calypso.confirmation.CalypsoDocumentActivityMonitor");
		metricDomainImpl.put("INDIKTOR_TRANSFER_PAYMENT_ACTIVITY", "com.fsi.monitoring.kpi.monitor.calypso.paymentSettlement.CalypsoTransferPaymentActivityMonitor");
		metricDomainImpl.put("INDIKTOR_ACCOUTING_ENTRY_POSTING", "com.fsi.monitoring.kpi.monitor.calypso.accounting.CalypsoAccountingEntryMonitor");
		metricDomainImpl.put("INDIKTOR_ACCOUTING_ENTRY_CRE", "com.fsi.monitoring.kpi.monitor.calypso.accounting.CalypsoAccountingEntryMonitor");
		metricDomainImpl.put("INDIKTOR_TASK_ACTIVITY", "com.fsi.monitoring.kpi.monitor.calypso.stp.CalypsoTaskActivityMonitor");
		metricDomainImpl.put("INDIKTOR_BATCH_PROCESS", "com.fsi.monitoring.kpi.monitor.calypso.stp.CalypsoScheduledTaskMonitor");
		metricDomainImpl.put("INDIKTOR_OPERATIONAL_ANOMALY", "com.fsi.monitoring.kpi.monitor.calypso.exception.CalypsoExceptionMonitor");
		metricDomainImpl.put("INDIKTOR_SYSTEM_ANOMALY", "com.fsi.monitoring.kpi.monitor.calypso.exception.CalypsoExceptionMonitor");
		metricDomainImpl.put("INDIKTOR_HARDWARE_ACTIVITY", "com.fsi.monitoring.kpi.monitor.system.SystemMonitor");
		metricDomainImpl.put("SYSLOAD_HARDWARE_ACTIVITY", "com.fsi.monitoring.kpi.monitor.sysload.SystemMonitor");
		metricDomainImpl.put("INDIKTOR_ORACLE", "com.fsi.monitoring.kpi.monitor.dbms.oracle.OracleMonitor");
		metricDomainImpl.put("INDIKTOR_SYBASE", "com.fsi.monitoring.kpi.monitor.dbms.sybase.SybaseMonitor");
		metricDomainImpl.put("INDIKTOR_CALYPSO_DS_CACHE", "com.fsi.monitoring.kpi.monitor.calypso.cache.CalypsoDsCacheMonitor");
		metricDomainImpl.put("INDIKTOR_CALYPSO_PSEVENT", "com.fsi.monitoring.kpi.monitor.calypso.event.CalypsoEventMonitor");
		metricDomainImpl.put("INDIKTOR_CALYPSO_DATASERVER", "com.fsi.monitoring.kpi.monitor.calypso.ds.CalypsoDataServerMonitor");
		metricDomainImpl.put("INDIKTOR_CALYPSO_ENGINE", "com.fsi.monitoring.kpi.monitor.calypso.engine.CalypsoEngineMonitor");
		metricDomainImpl.put("INDIKTOR_CALYPSO_EVENTSERVER", "com.fsi.monitoring.kpi.monitor.calypso.es.CalypsoEventServerMonitor");
		metricDomainImpl.put("INDIKTOR_JVM_ACTIVITY", "com.fsi.monitoring.kpi.monitor.jmx.JmxMonitor");
		metricDomainImpl.put("INDIKTOR_THREAD_ACTIVITY", "com.fsi.monitoring.kpi.monitor.jmx.JmxThreadMonitor");
		metricDomainImpl.put("INDIKTOR_PROCESS_ACTIVITY", "com.fsi.monitoring.kpi.monitor.process.ProcessMonitor");
		metricDomainImpl.put("SYSLOAD_PROCESS_ACTIVITY", "com.fsi.monitoring.kpi.monitor.sysload.ProcessMonitor");
	}
	
	public DefaultDataCollectionConfig(String logicalEnvName) {		
		this.logicalEnvName = logicalEnvName;				
		connectorIds = new HashMap<String, Integer>();
	}
	
	private void feedConnectors(DataModelPM dataModelPM) throws PersistenceException {
		XlsConnectorConfigParser parser = new XlsConnectorConfigParser(logicalEnvName);
		parser.parse();		
		
		Map<String, ConnectorConfig> connectors = parser.getConnectors();
		for (ConnectorConfig config : connectors.values()){
			long id = dataModelPM.saveConnector(config);
			connectorIds.put(config.getName(), (int)id);
		}
		
	}
	
	private void feedCollectors(DataModelPM dataModelPM) throws PersistenceException {
		XlsCollectorConfigParser parser = new XlsCollectorConfigParser(logicalEnvName);
		parser.parse();
		
		LogicalEnv env =  dataModelPM.getLogicalEnv(logicalEnvName);
		
		List<CollectorConfigXlsModel> collectors = parser.getCollectorList();
		for (CollectorConfigXlsModel configModel : collectors) {
			IkrStaticDomain domain = dataModelPM.getIkrStaticDomainByValue(configModel.getMetricDomain());
			
			List<MetricDomainResource> resourceList = dataModelPM.getMetricDomainResources(domain.getId());
			Map<String, MetricDomainResource> resources = new HashMap<String, MetricDomainResource>();
			for(MetricDomainResource resource : resourceList) {
				resources.put(resource.getResourceName(), resource);
			}
			
			List<MetricDomainConfig> metricDomainConfigs = dataModelPM.getMetricDomainConfigs(domain.getId());
			MetricDomainConfig metricDomainConfig = null;
			for (MetricDomainConfig domainConfig : metricDomainConfigs) {
				String classname = metricDomainImpl.get(configModel.getMetricDomainImpl());
				if (classname.equals(domainConfig.getClassName()))
					metricDomainConfig = domainConfig;
			}
			
			List<Integer> connectorConfigIds = new ArrayList<Integer>();
			connectorConfigIds.add(connectorIds.get(configModel.getConnector()));
			
			MonitorConfig config = new MonitorConfig((long)0, env.getId(), configModel.getContext(), metricDomainConfig, new IkrMonitorSchedulerConfig(), configModel.isAutoStart());
			config.setConnectorConfigIds(connectorConfigIds);
			config.setAttributes(configModel.getAttributes());
			
			dataModelPM.createMonitor(config);
		}
	}	
	
	public void process() {
		DataModelPM dataModelPM = (DataModelPM) ToolkitContext.getBean(PersistencyBeanName.dataModelPM.name());	
		
		try {
			System.out.println("Feeding connectors");
			feedConnectors(dataModelPM);
			System.out.println("Connectors feeding finished");
			System.out.println("Feeding collectors");
			feedCollectors(dataModelPM);
			System.out.println("Collectors feeding finished");
		} catch (PersistenceException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1) {
			System.out.println("java DefaultDataCollectionConfig <logicalEnv>");
			System.exit(0);
		}
		
		try {
			System.out.println("Start : Default Data Collection feeding");
			ToolkitContext.getContext().init("applicationContext-toolkits.xml", "toolkits");
			String logicalEnvName = args[0];
			DefaultDataCollectionConfig feeder = new DefaultDataCollectionConfig(logicalEnvName);
			feeder.process();
			System.out.println("End : Default Data Collection feeding");
		} catch(Exception exc) {
			exc.printStackTrace();
		}
		
		System.exit(0);
	}

}
