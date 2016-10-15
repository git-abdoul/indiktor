package com.fsi.monitoring.datamodel.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.AlertCompute;
import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.config.AllAlertDefinitionBean;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.XlsAlertParser;
import com.fsi.monitoring.util.XlsAlertParser.AlertDefaultComputation;
import com.fsi.monitoring.util.XlsAlertParser.AlertDefaultConditions;
import com.fsi.monitoring.util.XlsAlertParser.AlertDefaultDefinition;
import com.icesoft.faces.component.ext.HtmlCommandLink;

public class DefaultAlertDefinitionBean extends AccessControlBean {
	private static final Logger logger = Logger.getLogger(DefaultAlertDefinitionBean.class);
	
	private static final String MONITOR_ENV_TEMPLATE = "%MONITOR_ENV%";
	private static final String CALYPSO_ENV_TEMPLATE = "%CALYPSOENV%";
	private static final String HOSTNAME_TEMPLATE = "%HOSTNAME%";
	private static final String ENGINE_TEMPLATE = "%ENGINE_NAME%";
	private static final String CACHE_NAME_TEMPLATE = "%CACHE_NAME%";
	
	private XlsAlertParser parser;

	private String monitorEnv;
	private String whatToDo = "";
	private boolean withConfirmation = false;
	
	private String calypsoEnv = "";
	private Map<String, MonitorConfig> processes;
	private List<String> hostsToMonitor;
	private Map<String, List<MonitorConfig>> monitorConfigs;
	
	private HtmlCommandLink linkUI = null;
	private boolean activateLink;

	public DefaultAlertDefinitionBean() {
		linkUI = new HtmlCommandLink();
		activateLink = false;
		linkUI.setDisabled(true);
	}

	public void init(ActionEvent action) {
		if (!isAuthorized(1032, "defaultAlertDefSetup")) {
			return;
		}
		
		processes = new HashMap<String, MonitorConfig>();
		hostsToMonitor = new ArrayList<String>();
		parser = new XlsAlertParser();
	}

	private List<AlertDefinition> buildDefaultAlerts() {
		List<AlertDefinition> res = new ArrayList<AlertDefinition>();
		DataModelPM dataModelPM = (DataModelPM) FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		try {
			// MUST use getMonitorConfigs(String monitorEnv) from dataModelPM
			// this method uses the cache returns monitors by Env.
			// The specific Map must be create here (business) not in the persistency layer.
			
			monitorConfigs = null ; // dataModelPM.getMonitorConfigsByTypes(monitorEnv);
			List<MonitorConfig> calypsoConfigs = monitorConfigs.get("CALYPSO_DS_CACHE_MONITOR");
			if (calypsoConfigs != null && calypsoConfigs.size()>0) {
				MonitorConfig conf = calypsoConfigs.get(0);
				calypsoEnv = conf.getAttributes().get("CALYPSO_ENV");
			}
			
			List<MonitorConfig> jmxConfigs = monitorConfigs.get("JMX_MONITOR");
			if (jmxConfigs != null && jmxConfigs.size()>0) {
				for (MonitorConfig tmp : jmxConfigs) {
					processes.put(tmp.getAttributes().get("PROCESS_NAME"), tmp);
				}
			}
			
			List<MonitorConfig> systemConfigs = monitorConfigs.get("SYSTEM_MONITOR");
			if (systemConfigs != null && systemConfigs.size()>0) {
				for (MonitorConfig tmp : systemConfigs) {
//					hostsToMonitor.add(tmp.getHostname());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		Map<String, List<AlertDefaultDefinition>> defaultAlertMap = parser.getAlertDefaultDefinitions();
		for(String type : defaultAlertMap.keySet()) {
			List<AlertDefaultDefinition> defaultAlerts = defaultAlertMap.get(type);
			for(AlertDefaultDefinition def : defaultAlerts) {
				res.addAll(getAlertDefinitionFinals(def, AlertType.valueOf(type)));
			}
		}
		return res;
	}
	
	private List<AlertDefinition> getAlertDefinitionFinals(AlertDefaultDefinition def, AlertType type) {
		List<AlertDefinition> res = new ArrayList<AlertDefinition>();
		if (AlertType.ENGINE == type) {
//			long alertDefId = def.getId();
			for(String processName : processes.keySet()){
				if(processName.contains("Engine")){
					MonitorConfig conf = processes.get(processName);
//					def.setId(alertDefId);					
//					res.add(getAlertDefUnit(def, "", conf.getHostname(), processName));
//					alertDefId = alertDefId + 10;
				}
			}
		}
		else if (AlertType.DS_CACHE == type) {
			if (calypsoEnv != null && calypsoEnv.length()>0) {
				MonitorConfig conf = monitorConfigs.get("CALYPSO_DS_CACHE_MONITOR").get(0);
				if(conf!=null) {
					MonitoringPM monitoringPM = (MonitoringPM) FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
					Collection<IkrDefinition> ikrDefs = null;
//					try {
//						ikrDefs = monitoringPM.getIkrDefinitions(conf.getId(), "CALYPSO_DS_OBJECT_CACHE");
//					} catch (PersistenceException e) {
//						e.printStackTrace();
//						logger.error(e.getMessage(), e);
//					}
					
					List<String> cacheNames = new ArrayList<String>();
					if (ikrDefs!=null && !ikrDefs.isEmpty()) {
	//					long alertDefId = def.getId();
						for (IkrDefinition ikr : ikrDefs) {
							if (!cacheNames.contains(ikr.getIkrInstance())) {
	//							def.setId(alertDefId);
								res.add(getAlertDefUnit(def, ikr.getIkrInstance(), "", ""));
	//							alertDefId = alertDefId + 1;
								cacheNames.add(ikr.getIkrInstance());
							}
						}
					}
				}
			}
		}
		else if (AlertType.SYSTEM == type) {
//			long alertDefId = def.getId();
			for(String hostname : hostsToMonitor) {
//				def.setId(alertDefId);
				res.add(getAlertDefUnit(def, "", hostname, ""));
//				alertDefId = alertDefId + 2;
			}
		}
		else if (AlertType.DATASERVER == type) { 
			MonitorConfig conf = processes.get("DataServer");
			if(conf!=null){
//				String hostname = conf.getHostname();
//				res.add(getAlertDefUnit(def, "", hostname, ""));
			}
		}
		else if (AlertType.DS_CACHE_GLOBAL == type) { 
			if (calypsoEnv != null && calypsoEnv.length()>0) {
				res.add(getAlertDefUnit(def, "", "", ""));
			}
		}
		else if (AlertType.EVENTSERVER == type) { 
//			String hostname = processes.get("EventServer").getHostname();
//			res.add(getAlertDefUnit(def, "", hostname, ""));
		}
		else if (AlertType.STUCK_EVENT == type) { 
			if (calypsoEnv != null && calypsoEnv.length()>0) {
				res.add(getAlertDefUnit(def, "", "", ""));
			}
		}
		else if (AlertType.CALCULATION_SERVER == type) { 
			MonitorConfig conf = processes.get("CalculationServer");
			if(conf != null){
//				String hostname = conf.getHostname();			
//				res.add(getAlertDefUnit(def, "", hostname, ""));
			}
		}
		else if (AlertType.PRESENTATION_SERVER == type) { 
			MonitorConfig conf = processes.get("PresentationServer");
			if(conf != null){
//				String hostname = conf.getHostname();			
//				res.add(getAlertDefUnit(def, "", hostname, ""));
			}
		}
		return res;
	}
	
	private AlertDefinition getAlertDefUnit(AlertDefaultDefinition def, String cacheName, String hostname, String engineName) {
		String name = getValue(def.getName(), CALYPSO_ENV_TEMPLATE, calypsoEnv);
		name = getValue(name, CACHE_NAME_TEMPLATE, cacheName);
		name = getValue(name, HOSTNAME_TEMPLATE, hostname);
		name = getValue(name, ENGINE_TEMPLATE, engineName);
//		AlertDefinition alertDef = new AlertDefinition(0, 
//												  name,
//												  def.getGroup(), 
//												  def.getDomain(), 
//												  def.getSubdomain(), 
//												  def.getEnv().replace(MONITOR_ENV_TEMPLATE, monitorEnv), 
//												  name, 
//												  "", 
//												  def.isActive(), 
//												  new Date(),	
//												  new Date(),
//												  0);
		AlertDefinition alertDef = null;
		for(AlertDefaultConditions defCond : def.getConditions()) {
			long ikrDefId = -1;
			String ikrInstance = getValue(defCond.getInstance(), CACHE_NAME_TEMPLATE, cacheName);
			ikrInstance = getValue(ikrInstance, ENGINE_TEMPLATE, engineName);
			String ikrEnv = getValue(defCond.getEnv(), CALYPSO_ENV_TEMPLATE, calypsoEnv);
			ikrEnv = getValue(ikrEnv, HOSTNAME_TEMPLATE, hostname);
			MonitoringPM monitoringPM = (MonitoringPM) FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
//			try {
//				IkrDefinition ikrDef = monitoringPM.getIkrDefinition(defCond.getCategory(), ikrInstance, ikrEnv);
//				if (ikrDef != null)
//					ikrDefId = ikrDef.getId();
//			} catch (PersistenceException e) {
//				e.printStackTrace();
//				logger.error(e.getMessage(), e);
//			}
//			alertDef.addAlertCondition(new AlertConditionDouble(defCond.getConditionId(), 
//						defCond.getValMin(), 
//						defCond.getValMax(), 
//						defCond.isActive(), 
//						ikrDefId));
		}
		
		List<Integer> levels = new ArrayList<Integer>();
		levels.add(new Integer(1));
		levels.add(new Integer(2));
		levels.add(new Integer(3));
		levels.add(new Integer(100));
		
		for(AlertDefaultComputation compute : def.getComputations()){
			alertDef.addAlertCompute(new AlertCompute(AlertWorkflow.getStateBySeverity(compute.getLevel()), 
							compute.getLabel(), 
							compute.getCause(), 
							compute.isEnable()));
			levels.remove(new Integer(compute.getLevel()));
		}
		
		for (int level : levels) {
			alertDef.addAlertCompute(new AlertCompute(AlertWorkflow.getStateBySeverity(level), 
													  "", 
													  "", 
													  true));
		}
		
		levels = null;
		return alertDef;
	}
	
	private String getValue(String value, String template, String tempVal) {
		return value.replace(template, tempVal);
	}

	public void resetAlerts(ActionEvent event) {
		if (!isAuthorized(1032, "defaultAlertDefSetup")) {
			return;
		}
		
		try {
			AlertPM alertPM = (AlertPM) FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			List<String> envs = alertPM.getAlertEnvs();
			
			// delete all monitors for this monitorEnv				
			if (envs.contains(monitorEnv))  {
				//alertPM.deleteAlertDefinitionsForEnv(monitorEnv);
				envs.remove(monitorEnv);
			}

			if ("RESET".equals(whatToDo)) {
				List<AlertDefinition> alertDefs = buildDefaultAlerts();
				for (AlertDefinition alertDef : alertDefs) {
//					long id = alertDef.getId();
//					alertDef.setId(id + 5000*envs.size());
					alertPM.createAlertDefinition(alertDef);
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		toggleModal(null);
		
		AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");		
		allAlertDefinitionBean.init(null);
	}

	public void updateAlertConditions(ActionEvent event) {
		if (!isAuthorized(1032, "defaultAlertDefSetup")) {
			return;
		}		
		
		try {
			List<AlertDefinition> alertDefs = buildDefaultAlerts();
			AlertPM alertPM = (AlertPM) FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			for (AlertDefinition alertDef : alertDefs) {
				alertPM.updateAlertDefinition(alertDef);
			}			
		} catch (PersistenceException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
	
	public void activeLinkListener(ValueChangeEvent event) {
		if (!isAuthorized(1032, "defaultAlertDefSetup")) {
			return;
		}
		activateLink = (Boolean)event.getNewValue();
		linkUI.setDisabled(!activateLink);
	}

	public void deleteToggleModal(ActionEvent event) {
		if (!isAuthorized(1032, "defaultAlertDefSetup")) {
			return;
		}
		whatToDo = "DELETE";
		withConfirmation = !withConfirmation;
	}

	public void createToggleModal(ActionEvent event) {
		if (!isAuthorized(1032, "defaultAlertDefSetup")) {
			return;
		}
		whatToDo = "RESET";
		withConfirmation = !withConfirmation;
	}

	public void toggleModal(ActionEvent event) {
		whatToDo = "";
		withConfirmation = !withConfirmation;
	}

	public String getMonitorEnv() {
		return monitorEnv;
	}

	public void setMonitorEnv(String monitorEnv) {
		this.monitorEnv = monitorEnv;
	}

	public boolean isWithConfirmation() {
		return withConfirmation;
	}

	public void setWithConfirmation(boolean withConfirmation) {
		this.withConfirmation = withConfirmation;
	}

	public String getWhatToDo() {
		return whatToDo;
	}
	
	public HtmlCommandLink getLinkUI() {
		return linkUI;
	}

	public void setLinkUI(HtmlCommandLink linkUI) {
		this.linkUI = linkUI;
	}

	public boolean isActivateLink() {
		return activateLink;
	}

	public void setActivateLink(boolean activateLink) {
		this.activateLink = activateLink;
	}
	
	private enum AlertType {
		DATASERVER,DS_CACHE_GLOBAL,DS_CACHE,EVENTSERVER,STUCK_EVENT,ENGINE,CALCULATION_SERVER,PRESENTATION_SERVER,SYSTEM;
	}
}
