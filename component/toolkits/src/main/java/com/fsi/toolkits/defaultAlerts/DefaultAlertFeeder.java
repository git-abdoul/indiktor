package com.fsi.toolkits.defaultAlerts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.AlertConditionOperator;
import com.fsi.monitoring.alert.AlertCompute;
import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertDomain;
import com.fsi.monitoring.alert.AlertGroup;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.AlertSubDomain;
import com.fsi.monitoring.alert.condition.ValueAlertCondition;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.msd.StaticData;
import com.fsi.toolkits.VarDefModel;
import com.fsi.toolkits.config.ToolkitContext;

public class DefaultAlertFeeder {
	// key = groupName
	private Map<String, Integer> groups;	
	// key = groupName_domainName
	private Map<String, Integer> domains;
	// key = groupName_domainName_subDomainName
	private Map<String, Integer> subdomains;
	
	private LogicalEnv logicalEnv;
	private  Map<String, AlertPropertiesModel> alertDefProperties;
	
	private Map<String, MonitorConfig> monitorConfigs;
	
//	String resourcePath;
	private String alertDescriptionPath;	
	private String alertPropertiesPath;	
	
	protected String toolkitConfigHome;
	protected String toolkitResourcesHome;
	
	DataModelPM dataModelPM;
	MonitoringPM monitoringPM;
	AlertPM alertPM;
	

	public DefaultAlertFeeder() {
		toolkitConfigHome = System.getProperty("toolkit.conf");
		toolkitResourcesHome = System.getProperty("toolkit.resources");
		
		alertDescriptionPath = toolkitResourcesHome + File.separator + "alertDefinition.xml";		
		
		monitorConfigs = new HashMap<String, MonitorConfig>();
		
		dataModelPM = (DataModelPM) ToolkitContext.getBean(PersistencyBeanName.dataModelPM.name());
		monitoringPM = (MonitoringPM) ToolkitContext.getBean(PersistencyBeanName.monitoringPM.name());
		alertPM = (AlertPM)ToolkitContext.getBean(PersistencyBeanName.alertPM);
	}
	
	public void init(String logicalEnvName) throws PersistenceException {
		
		logicalEnv = dataModelPM.getLogicalEnv(logicalEnvName);	
		
		if (logicalEnv == null) {
			throw new PersistenceException("No logical Env is define for <" + logicalEnvName + ">", BaseException.ERROR);
		}
		
		Map<Long, MonitorConfig> monitors = dataModelPM.getMonitorConfigs(logicalEnv.getId());
		for (MonitorConfig config : monitors.values()) {
			monitorConfigs.put(config.getContext(), config);
		}
		
		alertPropertiesPath = toolkitConfigHome + File.separator + "alertProperties" + logicalEnvName + ".xml";
		
		initGroups();
		initDomains();
		initSubDomains();
	}
	
	private void initGroups() throws PersistenceException {
		groups = new HashMap<String, Integer>();
		Map<Integer, AlertGroup> tmp = alertPM.getAllAlertGroups();
		for(int id : tmp.keySet()){
			AlertGroup group = tmp.get(id);
			groups.put(group.getValue(), id);
		}
	}
	
	private void initDomains() throws PersistenceException {
		domains = new HashMap<String, Integer>();
		Map<Integer, AlertGroup> groupsTmp = alertPM.getAllAlertGroups();
		Map<Integer, AlertDomain> tmp = alertPM.getAllAlertDomains();
		for(int id : tmp.keySet()){
			AlertDomain domain = tmp.get(id);
			AlertGroup group = groupsTmp.get(domain.getGroupId());
			String key = group.getValue() + "_" + domain.getValue();
			domains.put(key, id);
		}
	}
	
	private void initSubDomains() throws PersistenceException {
		subdomains = new HashMap<String, Integer>();
		Map<Integer, AlertGroup> groupsTmp = alertPM.getAllAlertGroups();
		Map<Integer, AlertDomain> domainsTmp = alertPM.getAllAlertDomains();
		Map<Integer, AlertSubDomain> tmp = alertPM.getAllAlertSubDomains();
		for(int id : tmp.keySet()){
			AlertSubDomain subDomain = tmp.get(id);
			AlertDomain domain = domainsTmp.get(subDomain.getDomainId());
			AlertGroup group = groupsTmp.get(domain.getGroupId());
			String key = group.getValue() + "_" + domain.getValue() + "_" + subDomain.getValue();
			subdomains.put(key, id);
		}
	}
	
	private String getValue(String value, String template, String tempVal) {
		return value.replace(template, tempVal);
	}
	
	private String getValue(String value, Map<String, String> vars) {
		String res = value;
		for (String key : vars.keySet()) {
			res = getValue(res, "%"+ key +"%", vars.get(key));
		}
		return res;
	}
	
	public void reset(String whatToDo, String logicalEnvName) throws PersistenceException, SystemException {
		System.out.println("RESET ALERTS - Start");			
		System.out.println("RESET ALERTS - Building Alert Definitions ...");		
		
		XmlAlertPropertiesParser propParser = (new XmlAlertPropertiesParser()).parse(alertPropertiesPath);		
		alertDefProperties = propParser.getAlertProperties();
		
		XmlAlertParser parser = (new XmlAlertParser()).parse(alertDescriptionPath);		
		List<AlertDefinitionModel> alertDefModels = parser.getAlertDefinitions();
		
		List<AlertDefinition> alertDefs = new ArrayList<AlertDefinition>();		
		for (AlertDefinitionModel alertDefModel : alertDefModels) {
			alertDefs.addAll(buildAlertDefinitions(alertDefModel));			
		}	
		
		System.out.println("RESET ALERTS - " +  alertDefs.size() + " Alert Definitions found");
		System.out.println("RESET ALERTS - Processing Alert definitions ...");
		
		try {									
			alertPM.deleteAlertDefinitionsForEnv(logicalEnv.getId());
	
			if (whatToDo!= null && "RESET".equals(whatToDo.toUpperCase())) {					
				for (AlertDefinition alertDef : alertDefs) {
					alertPM.createAlertDefinition(alertDef);
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}
	
	public void update(String logicalEnvName) throws PersistenceException, SystemException {
		System.out.println("UPDATE ALERTS - Start");			
		System.out.println("UPDATE ALERTS - Building Alert Definitions ...");
		
		XmlAlertPropertiesParser propParser = (new XmlAlertPropertiesParser()).parse(alertPropertiesPath);		
		alertDefProperties = propParser.getAlertProperties();
		
		XmlAlertParser parser = (new XmlAlertParser()).parse(alertDescriptionPath);		
		List<AlertDefinitionModel> alertDefModels = parser.getAlertDefinitions();
		
		List<AlertDefinition> alertDefs = new ArrayList<AlertDefinition>();		
		for (AlertDefinitionModel alertDefModel : alertDefModels) {
			alertDefs.addAll(buildAlertDefinitions(alertDefModel));			
		}	
		
		System.out.println("UPDATE ALERTS - " +  alertDefs.size() + " Alert Definitions found");
		System.out.println("UPDATE ALERTS - Processing Alert definitions ...");
		
		Map<String, AlertDefinition> mylogivalEnvExistingDefinitions = new HashMap<String, AlertDefinition>();		
		Map<Long, AlertDefinition> existingAlertDefinitions = alertPM.getAlertDefinitions();
		for (AlertDefinition alertDef : existingAlertDefinitions.values()) {
			if (alertDef.getLogicalEnv() == logicalEnv.getId())
				mylogivalEnvExistingDefinitions.put(alertDef.getName(), alertDef);
		}
		
		try {									
			for (AlertDefinition alertDef : alertDefs) {
				if (!mylogivalEnvExistingDefinitions.containsKey(alertDef.getName())) {
					alertPM.createAlertDefinition(alertDef);
					System.out.println("Alert Definition <" +  alertDef.getName() + "> found and saved");
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}
	
	private List<AlertDefinition> buildAlertDefinitions(AlertDefinitionModel alertDefModel) {
		
		List<AlertDefinition> alertDefs = new ArrayList<AlertDefinition>();		
		AlertPropertiesModel propertiesModel = alertDefProperties.get(alertDefModel.getType());
		
		for (VarDefModel model : propertiesModel.getVarDefinitions()) {
			AlertDefinition alertDefinition;
			try {
				alertDefinition = buildAlertDefinition(alertDefModel, model.getVars());
				alertDefs.add(alertDefinition);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		
		return alertDefs;
	}
	
	private AlertDefinition buildAlertDefinition(AlertDefinitionModel alertDefModel,Map<String, String> variables) throws Exception{
		int groupId = groups.get(alertDefModel.getGroup());
		int domainId = domains.get(alertDefModel.getGroup()+"_"+alertDefModel.getDomain());
		int subDomainId = subdomains.get(alertDefModel.getGroup()+"_"+alertDefModel.getDomain()+"_"+alertDefModel.getSubDomain());
		
		String name = getValue(alertDefModel.getName(), variables);
		String desc = getValue(alertDefModel.getDescription(), variables);
		
		AlertDefinition alertDef = new AlertDefinition((long)0, name, groupId, domainId, subDomainId, logicalEnv.getId(), desc, "", alertDefModel.isActive(), new Date(), new Date(), (long)0);
		
		for(AlertConditionModel model : alertDefModel.getConditions()) {
			String ikrCategoryValue = model.getIkrCategoryValue();
			
			String ikrInstance = getValue(model.getIkrInstance(), variables);
			
			String context = getValue(model.getContext(), variables);			
			try {
				IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomainByValue(ikrCategoryValue);
				if (ikrCategory != null) {
					IkrUnitType ikrUnitType = null;	
	 	          	IkrUnit ikrUnit = null;
	 	           	try {	 	           		
	 	           		ikrUnitType = IkrUnitType.valueOf(model.getUnitType());	 	           	
		 	            if (ikrUnitType != null && model.getUnit() != null) {
		 	            	ikrUnit = ikrUnitType.getIkrUnit(model.getUnit());
		 	            }
	 	           	} catch (Exception exc) {
	 	           		System.err.println("Impossible to create category because of wrong IkrUnitType: " + model.getUnitType());
	 	           		continue;
	 	           	}			
					
					Map<Long, AbstractIkrDefinition> definitions = monitoringPM.getIkrDefinitions(logicalEnv.getId(),context,ikrCategory.getId());
					AbstractIkrDefinition ikrDefinition = null;						
					for (AbstractIkrDefinition definition : definitions.values()) {
						if (definition.getIkrInstance().equals(ikrInstance) && definition.getIkrCompute().name().equals(model.getCompute())) {
							ikrDefinition = definition;
							break;
						}
					}
					if (ikrDefinition != null) {				
		 	           	ValueAlertCondition alertCondition = new ValueAlertCondition(model.getId(),model.isActive(), ikrDefinition.getId(), model.getValue() ,ikrUnitType, ikrUnit);
		 	           	alertCondition.setOperator(AlertConditionOperator.valueOf(model.getOperator()));
						alertDef.addAlertCondition(alertCondition);
					} else {
						if (CrossComputeDefinition.CROSS_COMPUTE_CONTEXT.equals(context) || StaticData.STATIC_DATA_CONTEXT.equals(context)) {
							System.err.println("IkrDefinition name not recognized for: " + logicalEnv.getName() + " , " + context + " , " + ikrCategory.getDomainValue() + " , " + ikrInstance);
						}
						else {
							MonitorConfig conf = monitorConfigs.get(context);
							if (conf != null) {
								MetricCompute ikrCompute = MetricCompute.getCompute(model.getCompute());
								if (ikrCompute != null) {								
									long ikrDefId = monitoringPM.createIkrDefinition(new IkrDefinition(0, conf.getId(), ikrCategory.getId(), ikrInstance, ikrCompute, true));
									ValueAlertCondition alertCondition = new ValueAlertCondition(model.getId(),model.isActive(), ikrDefId, model.getValue() ,ikrUnitType, ikrUnit);
					 	           	alertCondition.setOperator(AlertConditionOperator.valueOf(model.getOperator()));
									alertDef.addAlertCondition(alertCondition);
								}
								else {
									System.err.println("IKR Compute : <" + model.getCompute() + "> Definition does not exist");
								}
							}
							else {
								System.err.println("No Monitor Config found for : <" + context + "> in logical Env <" + logicalEnv.getName() + ">");
							}
						}	
					}
				} else {
					throw new Exception("MetricCategory name not recognized: " + ikrCategoryValue);
				}
			} catch (PersistenceException exc) {
				throw new Exception(exc.getMessage());
			}
		}				
		
		for(AlertComputeModel model : alertDefModel.getComputes()) {
			AlertCompute alertCompute = new AlertCompute(AlertWorkflow.getStateBySeverity(model.getSeverity()),"",model.getCause(),model.isActive());
			alertDef.addAlertCompute(alertCompute);
		}
		
		return alertDef;
	}
	
	public static void main(String[] args) {
		if(args.length<2) {
			System.out.println("java DefaultAlertFeeder <RESET:CLEAN:UPDATE> <LOGICAL ENV>");
			System.exit(0);
		}
		
		try {
			ToolkitContext.getContext().init("applicationContext-toolkits.xml", "toolkits");
			
			String command = args[0];
			String logicalEnv = args[1];
			DefaultAlertFeeder feeder = new DefaultAlertFeeder();			
			feeder.init(logicalEnv);
			
			if("RESET".equalsIgnoreCase(command))
				feeder.reset("RESET", logicalEnv);
			else if ("CLEAN".equalsIgnoreCase(command))
				feeder.reset(null, logicalEnv);
			else if ("UPDATE".equalsIgnoreCase(command))
				feeder.update(logicalEnv);
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}
}
