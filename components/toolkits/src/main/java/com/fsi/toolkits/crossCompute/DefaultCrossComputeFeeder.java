package com.fsi.toolkits.crossCompute;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;
import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
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

public class DefaultCrossComputeFeeder {
	
	private LogicalEnv logicalEnv;
	private  Map<String, CrossComputePropertiesModel> crossComputeDefProperties;
	private List<CrossComputeDefinitionModel> crossComputeDefModels;
	
	private List<CrossComputeDefinition> crossComputeDefs;
	
	private Map<String, MonitorConfig> monitorConfigs;
	
	protected String toolkitConfigHome;
	protected String toolkitResourcesHome;
	private String crossComputeDescriptionPath;	
	private String crossComputePropertiesPath;	
	
	DataModelPM dataModelPM;
	MonitoringPM monitoringPM;

	public DefaultCrossComputeFeeder() {			
		toolkitConfigHome = System.getProperty("toolkit.conf");
		toolkitResourcesHome = System.getProperty("toolkit.resources");
		
		crossComputeDescriptionPath = toolkitResourcesHome + File.separator + "crossComputeDefinition.xml";	
		
		monitorConfigs = new HashMap<String, MonitorConfig>();
		
		dataModelPM = (DataModelPM) ToolkitContext.getBean(PersistencyBeanName.dataModelPM.name());
		monitoringPM = (MonitoringPM) ToolkitContext.getBean(PersistencyBeanName.monitoringPM.name());
	}
	
	public void init(String logicalEnvName) throws PersistenceException, SystemException {
		
		logicalEnv = dataModelPM.getLogicalEnv(logicalEnvName);	
		
		if (logicalEnv == null) {
			throw new PersistenceException("No logical Env is define for <" + logicalEnvName + ">", BaseException.ERROR);
		}
		
		Map<Long, MonitorConfig> monitors = dataModelPM.getMonitorConfigs(logicalEnv.getId());
		for (MonitorConfig config : monitors.values()) {
			monitorConfigs.put(config.getContext(), config);
		}
		
		crossComputePropertiesPath = toolkitConfigHome + File.separator + "crossComputeProperties" + logicalEnvName + ".xml";
		
		XmlCrossComputePropertiesParser propParser = (new XmlCrossComputePropertiesParser()).parse(crossComputePropertiesPath);		
		crossComputeDefProperties = propParser.getCrossComputeProperties();
		
		XmlCrossComputeParser parser = (new XmlCrossComputeParser()).parse(crossComputeDescriptionPath);		
		crossComputeDefModels = parser.getCrossComputeDefinitions();
		
		System.out.println("Building Cross Compute Definitions ...");			
		
		crossComputeDefs = new ArrayList<CrossComputeDefinition>();		
		for (CrossComputeDefinitionModel crossComputeDefModel : crossComputeDefModels) {
			crossComputeDefs.addAll(buildCrossComputeDefinitions(crossComputeDefModel));			
		}	
		
		System.out.println(crossComputeDefs.size() + " Cross Compute Definitions found");
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
		System.out.println("Processing Cross Compute ...");
		
		try {									
			monitoringPM.deleteCrossComputeDefinitions(logicalEnv.getId());
	
			if (whatToDo!= null && "RESET".equals(whatToDo.toUpperCase())) {					
				for (CrossComputeDefinition crossComputeDef : crossComputeDefs) {
					monitoringPM.createCrossComputeDefinition(crossComputeDef);
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}
	
	public void update(String logicalEnvName) throws PersistenceException, SystemException {
//		System.out.println("Processing Cross Compute ...");
//		
//		Map<String, AlertDefinition> mylogivalEnvExistingDefinitions = new HashMap<String, AlertDefinition>();		
//		Map<Long, AlertDefinition> existingAlertDefinitions = alertPM.getAlertDefinitions();
//		for (AlertDefinition alertDef : existingAlertDefinitions.values()) {
//			if (alertDef.getLogicalEnv() == logicalEnv.getId())
//				mylogivalEnvExistingDefinitions.put(alertDef.getName(), alertDef);
//		}
//		
//		try {									
//			for (AlertDefinition alertDef : alertDefs) {
//				if (!mylogivalEnvExistingDefinitions.containsKey(alertDef.getName())) {
//					alertPM.createAlertDefinition(alertDef);
//					System.out.println("Alert Definition <" +  alertDef.getName() + "> found and saved");
//				}
//			}
//		} catch (PersistenceException e) {
//			e.printStackTrace();
//		}
	}
	
	private List<CrossComputeDefinition> buildCrossComputeDefinitions(CrossComputeDefinitionModel crossComputeDefModel) {		
		List<CrossComputeDefinition> crossComputeDefs = new ArrayList<CrossComputeDefinition>();		
		CrossComputePropertiesModel propertiesModel = crossComputeDefProperties.get(crossComputeDefModel.getKey());
		
		for (VarDefModel model : propertiesModel.getVarDefinitions()) {
			CrossComputeDefinition crossComputeDefinition;
			try {
				crossComputeDefinition = buildCrossComputeDefinition(crossComputeDefModel, model.getVars());
				crossComputeDefs.add(crossComputeDefinition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return crossComputeDefs;
	}
	
	private CrossComputeDefinition buildCrossComputeDefinition(CrossComputeDefinitionModel crossComputeDefModel,Map<String, String> variables) throws Exception{
		String metricCategoryDomain = "COMPUTED - " + crossComputeDefModel.getMetricCategory();
		
		IkrCategory ikrCat = (IkrCategory)dataModelPM.getIkrStaticDomainByValue(metricCategoryDomain);
		int ikrCategoryId = 0;
		if (ikrCat == null) {
			IkrStaticDomain parent = dataModelPM.getIkrStaticDomainByValue(crossComputeDefModel.getMetricDomain());
			IkrUnitType ikrUnitType = null;	
          	IkrUnit ikrUnit = null;
           	try {	 	           		
           		ikrUnitType = IkrUnitType.valueOf(crossComputeDefModel.getUnitType());	 	           	
	            if (ikrUnitType != null && crossComputeDefModel.getUnit() != null) {
	            	ikrUnit = ikrUnitType.getIkrUnit(crossComputeDefModel.getUnit());
	            }
           	} catch (Exception exc) {
           		throw new Exception("Impossible to create category because of wrong IkrUnitType: " + crossComputeDefModel.getUnitType());
           	}							
			ikrCat = new IkrCategory(0, parent.getId(), metricCategoryDomain, crossComputeDefModel.getMetricCategory(), "", ikrUnitType, ikrUnit, 0, true, true, new ArrayList<String>());
			ikrCategoryId = dataModelPM.createIkrStaticDomain(ikrCat);
		}
		else
			ikrCategoryId = ikrCat.getId();
		
		Map<String, String> ikrDefVars = new HashMap<String, String>();
		
		String crossComputation = null;
		String ikrInstance = null;
		
		for(MetricModel model : crossComputeDefModel.getMetrics()) {
			String ikrCategoryValue = model.getIkrCategoryValue();
			
			String metricInstance = getValue(model.getIkrInstance(), variables);
			
			String context = getValue(model.getContext(), variables);			
			try {
				IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomainByValue(ikrCategoryValue);
				if (ikrCategory != null) {
					Map<Long, AbstractIkrDefinition> definitions = monitoringPM.getIkrDefinitions(logicalEnv.getId(),context,ikrCategory.getId());
					AbstractIkrDefinition ikrDefinition = null;						
					for (AbstractIkrDefinition definition : definitions.values()) {
						if (definition.getIkrInstance().equals(metricInstance) && definition.getIkrCompute().name().equals(model.getCompute())) {
							ikrDefinition = definition;
							break;
						}
					}
					if (ikrDefinition != null) {
						ikrDefVars.put("M"+model.getId(), "M"+ikrDefinition.getId());
					} else {
						if (CrossComputeDefinition.CROSS_COMPUTE_CONTEXT.equals(context) || StaticData.STATIC_DATA_CONTEXT.equals(context)) {
							System.err.println("IkrDefinition name not recognized: " + metricInstance);
						}
						else {
							MonitorConfig conf = monitorConfigs.get(context);
							if (conf != null) {
								MetricCompute ikrCompute = MetricCompute.getCompute(model.getCompute());
								if (ikrCompute != null) {								
									long ikrDefId = monitoringPM.createIkrDefinition(new IkrDefinition(0, conf.getId(), ikrCategory.getId(), metricInstance, ikrCompute, true));
									ikrDefVars.put("M"+model.getId(), "M"+ikrDefId);
								}
								else {
									System.err.println("IKR Compute : <" + model.getCompute() + ">  Definition does not exist");
								}
							}
							else {
								System.err.println("No Monitor Config found for : <" + context + "> in logical Env <" + logicalEnv.getName() + ">");
							}
						}					
					}
				} else {
					System.err.println("MetricCategory name not recognized: " + ikrCategoryValue);
				}
			} catch (PersistenceException exc) {
				throw new Exception(exc.getMessage());
			}
		}				
		
		ikrInstance = getValue(crossComputeDefModel.getComputeName(), variables);
		
		crossComputation = getValue(crossComputeDefModel.getComputation(), ikrDefVars);
		
		return new CrossComputeDefinition(0, logicalEnv.getId(), ikrCategoryId, ikrInstance, MetricCompute.RT, crossComputation, true);
	}
	
	public static void main(String[] args) {
		if(args.length<2) {
			System.out.println("java DefaultCrossComputeFeeder <RESET:CLEAN:UPDATE> <LOGICAL ENV>");
			System.exit(0);
		}		
		
		try {
			
			ToolkitContext.getContext().init("applicationContext-toolkits.xml", "toolkits");
			
			String command = args[0];
			String logicalEnv = args[1];
			DefaultCrossComputeFeeder feeder = new DefaultCrossComputeFeeder();
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
