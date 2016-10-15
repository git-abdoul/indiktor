package com.fsi.toolkits.crossCompute;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;

public class XmlCrossComputeParser {	
	private static final Logger LOG = Logger.getLogger(XmlCrossComputeParser.class);	
	
	private static final String CROSS_COMPUTES_TAG	= "CROSS_COMPUTES";
	private static final String COMPUTE_TAG	= "CROSS_COMPUTES/COMPUTE";
	private static final String METRIC_TAG	= "CROSS_COMPUTES/COMPUTE/METRICS/METRIC";
	private static final String COMPUTE_DEF_TAG	= "CROSS_COMPUTES/COMPUTE/COMPUTE_DEF";
	
	private List<CrossComputeDefinitionModel> crossComputeDefinitions;	
	
	public XmlCrossComputeParser() {
		crossComputeDefinitions = new ArrayList<CrossComputeDefinitionModel>();
	}

	public void addCrossComputeDef(CrossComputeDefinitionModel model) {
		crossComputeDefinitions.add(model);
	}
		
	public XmlCrossComputeParser parse(String filename) throws SystemException {		
		Digester digester = new Digester();
		digester.setValidating(false);
		
		digester.addObjectCreate(CROSS_COMPUTES_TAG, XmlCrossComputeParser.class);
		
		digester.addObjectCreate(COMPUTE_TAG, CrossComputeDefinitionModel.class);
		digester.addSetProperties(COMPUTE_TAG);
			digester.addObjectCreate(METRIC_TAG, MetricModel.class);
			digester.addSetProperties(METRIC_TAG);
			digester.addObjectCreate(COMPUTE_DEF_TAG, ComputeModel.class);		
			digester.addSetProperties(COMPUTE_DEF_TAG);
			digester.addSetNext(COMPUTE_DEF_TAG, "setCompute");
			digester.addSetNext(METRIC_TAG, "addMetric");
	    digester.addSetNext(COMPUTE_TAG, "addCrossComputeDef");
    
        try {
			return (XmlCrossComputeParser)digester.parse(new File(filename));
		} catch (Exception e) {		
			LOG.fatal("Impossible to parse Monitors config", e);
			throw new SystemException(e.getMessage(), e, BaseException.ERROR);
		} 
    }	
	
	public List<CrossComputeDefinitionModel> getCrossComputeDefinitions() {
		return crossComputeDefinitions;
	}

	public static void main(String[] args) {
		try {
			XmlCrossComputeParser model = (new XmlCrossComputeParser()).parse("C:/dev/indiktor-suite/component/toolkits/resources/crossComputeDefinition.xml");
			System.out.println("FINISHED");
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
}
