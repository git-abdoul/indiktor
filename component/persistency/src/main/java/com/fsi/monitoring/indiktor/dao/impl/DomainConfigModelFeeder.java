package com.fsi.monitoring.indiktor.dao.impl;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;

public class DomainConfigModelFeeder {	
	private static final Logger LOG = Logger.getLogger(DomainConfigModelFeeder.class);	
	
	private static final String DOMAIN_CONFIG_TAG	= "DOMAIN_CONFIG";
	private static final String DOMAIN_TYPE_TAG		= "DOMAIN_CONFIG/DOMAIN_TYPE";
	private static final String METRIC_DOMAIN_TAG	= "DOMAIN_CONFIG/DOMAIN_TYPE/METRIC_DOMAIN";
	private static final String CONFIG_TAG	= "DOMAIN_CONFIG/DOMAIN_TYPE/METRIC_DOMAIN/CONFIG";
	private static final String ATTRIBUTE_TAG	= "DOMAIN_CONFIG/DOMAIN_TYPE/METRIC_DOMAIN/CONFIG/ATTRIBUTES/ATTRIBUTE";
	private static final String FIELD_TAG	= "DOMAIN_CONFIG/DOMAIN_TYPE/METRIC_DOMAIN/CONFIG/ATTRIBUTE_FIELDS/FIELD";
	private static final String CONFIG_ITEMS_TAG	= "DOMAIN_CONFIG/DOMAIN_TYPE/METRIC_DOMAIN/CONFIG/CONFIG_ITEMS";
	private static final String RESOURCE_TAG	= "DOMAIN_CONFIG/DOMAIN_TYPE/METRIC_DOMAIN/CONFIG/RESOURCES/RESOURCE";
	
	private Map<String,List<MetricDomainConfigModel>> supportedMetricDomainconfigs;
	
	public Map<String,List<MetricDomainConfigModel>> getSupportedMetricDomainconfigs() {
		return supportedMetricDomainconfigs;
	}

	public DomainConfigModelFeeder() {
		supportedMetricDomainconfigs =  new HashMap<String,List<MetricDomainConfigModel>>();
	}
	
	public void addDomainTypeModel(DomainTypeModel domainTypeModel) {
		List<MetricDomainModel> metricDomainModels = domainTypeModel.getMetricDomainModels();
		for (MetricDomainModel model : metricDomainModels) {
			supportedMetricDomainconfigs.put(model.getType(), model.getConfigs());
		}
	}
	
	public DomainConfigModelFeeder parse(String filename) throws SystemException {		
		Digester digester = new Digester();
		digester.setValidating(false);

		digester.addObjectCreate(DOMAIN_CONFIG_TAG, DomainConfigModelFeeder.class);

		digester.addObjectCreate(DOMAIN_TYPE_TAG, DomainTypeModel.class);		
		digester.addSetProperties(DOMAIN_TYPE_TAG);
			digester.addObjectCreate(METRIC_DOMAIN_TAG, MetricDomainModel.class);		
			digester.addSetProperties(METRIC_DOMAIN_TAG);
				digester.addObjectCreate(CONFIG_TAG, MetricDomainConfigModel.class);
				digester.addSetProperties(CONFIG_TAG);
					digester.addSetProperties(CONFIG_ITEMS_TAG);
					digester.addObjectCreate(ATTRIBUTE_TAG, MetricDomainConfigAttributeModel.class);
					digester.addSetProperties(ATTRIBUTE_TAG);
						digester.addSetNext(ATTRIBUTE_TAG, "addAttribute");
					digester.addObjectCreate(FIELD_TAG, MetricDomainConfigFieldModel.class);
					digester.addSetProperties(FIELD_TAG);
						digester.addSetNext(FIELD_TAG, "addField");
					digester.addObjectCreate(RESOURCE_TAG, MetricDomainConfigResourceModel.class);
					digester.addSetProperties(RESOURCE_TAG);
						digester.addSetNext(RESOURCE_TAG, "addResource");
				digester.addSetNext(CONFIG_TAG, "addConfig");
			digester.addSetNext(METRIC_DOMAIN_TAG, "addMetricDomain");
        digester.addSetNext(DOMAIN_TYPE_TAG, "addDomainTypeModel");
    
        try {
			return (DomainConfigModelFeeder)digester.parse(new File(filename));
		} catch (Exception e) {		
			LOG.fatal("Impossible to parse Monitors config", e);
			throw new SystemException(e.getMessage(), e, BaseException.ERROR);
		} 
    }
	
	public static void main(String[] args) {
		try {
			DomainConfigModelFeeder model = (new DomainConfigModelFeeder()).parse("C:/dev/indiktor-suite/component/monitor-server/staticData/metricDomainConfig.xml");
			Map<String,List<MetricDomainConfigModel>> globalConfigs = model.getSupportedMetricDomainconfigs();
			List<MetricDomainConfigModel> processes = globalConfigs.get("PROCESS_ACTIVITY");
			List<MetricDomainConfigModel> jvms = globalConfigs.get("JVM_ACTIVITY");
			List<MetricDomainConfigModel> pings = globalConfigs.get("PING");
			System.out.println("FINISHED");
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
}
