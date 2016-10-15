package com.fsi.monitoring.indiktor.dao.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;

public class StaticDomainModelFeeder {	
	private static final Logger LOG = Logger.getLogger(StaticDomainModelFeeder.class);	
	
	private static final String STATIC_DOMAIN_TAG	= "STATIC_DOMAIN";
	private static final String DOMAIN_TYPE_TAG		= "STATIC_DOMAIN/DOMAIN_TYPE";
	private static final String METRIC_DOMAIN_TAG	= "STATIC_DOMAIN/DOMAIN_TYPE/METRIC_DOMAIN";
	private static final String METRIC_CATEGORY_TAG	= "STATIC_DOMAIN/DOMAIN_TYPE/METRIC_DOMAIN/METRIC_CATEGORY";
	
	private List<DomainTypeModel> supportedDomainTypes;
	
	// key domain type
	private Map<String,List<MetricDomainModel>> supportedMetricDomainTypes;
	
	public List<DomainTypeModel> getSupportedDomainTypes() {
		return supportedDomainTypes;
	}
	
	public Map<String,List<MetricDomainModel>> getSupportedMetricDomainTypes() {
		return supportedMetricDomainTypes;
	}

	public StaticDomainModelFeeder() {
		supportedDomainTypes = new ArrayList<DomainTypeModel>();
		supportedMetricDomainTypes =  new HashMap<String,List<MetricDomainModel>>();
	}
	
	
	
	public void addDomainTypeModel(DomainTypeModel domainTypeModel) {
		String domainType = domainTypeModel.getDomainType();
		supportedDomainTypes.add(domainTypeModel);		
		List<MetricDomainModel> metricDomainModels = domainTypeModel.getMetricDomainModels();
		supportedMetricDomainTypes.put(domainType, metricDomainModels);
	}
	
	public StaticDomainModelFeeder parse(String filename) throws SystemException {		
		Digester digester = new Digester();
		digester.setValidating(false);

		digester.addObjectCreate(STATIC_DOMAIN_TAG, StaticDomainModelFeeder.class);

		digester.addObjectCreate(DOMAIN_TYPE_TAG, DomainTypeModel.class);		
		digester.addSetProperties(DOMAIN_TYPE_TAG);
			digester.addObjectCreate(METRIC_DOMAIN_TAG, MetricDomainModel.class);		
			digester.addSetProperties(METRIC_DOMAIN_TAG);
				digester.addObjectCreate(METRIC_CATEGORY_TAG, MetricCategoryModel.class);
				digester.addSetProperties(METRIC_CATEGORY_TAG);
					digester.addSetNext(METRIC_CATEGORY_TAG, "addMetricCategory");
			digester.addSetNext(METRIC_DOMAIN_TAG, "addMetricDomain");
        digester.addSetNext(DOMAIN_TYPE_TAG, "addDomainTypeModel");
    
        try {
			return (StaticDomainModelFeeder)digester.parse(new File(filename));
		} catch (Exception e) {		
			LOG.fatal("Impossible to parse Monitors config", e);
			throw new SystemException(e.getMessage(), e, BaseException.ERROR);
		} 
    }
	
	public static void main(String[] args) {
		try {
			StaticDomainModelFeeder model = (new StaticDomainModelFeeder()).parse("C:/dev/indiktor-suite/component/monitor-server/staticData/staticDomainModel.xml");
			System.out.println("FINISHED");
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
}
