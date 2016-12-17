package com.fsi.toolkits.crossCompute;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;
import com.fsi.toolkits.VarDefModel;
import com.fsi.toolkits.VariableModel;
import com.fsi.toolkits.defaultAlerts.AlertPropertiesModel;

public class XmlCrossComputePropertiesParser {	
	private static final Logger LOG = Logger.getLogger(XmlCrossComputePropertiesParser.class);	
	
	private static final String CROSS_COMPUTES_PROPS_TAG	= "CROSS_COMPUTES_PROPS";
	private static final String COMPUTE_PROP_TAG	= "CROSS_COMPUTES_PROPS/COMPUTE_PROP";
	private static final String COMPUTE_VAR_DEF_TAG	= "CROSS_COMPUTES_PROPS/COMPUTE_PROP/COMPUTE_VAR_DEF";
	private static final String VAR_TAG	= "CROSS_COMPUTES_PROPS/COMPUTE_PROP/COMPUTE_VAR_DEF/VAR";
	
	private Map<String, CrossComputePropertiesModel> CrossComputeProperties;
	
	public XmlCrossComputePropertiesParser() {
		CrossComputeProperties = new HashMap<String, CrossComputePropertiesModel>();
	}

	public void addCrossComputePropertie(CrossComputePropertiesModel model) {
		CrossComputeProperties.put(model.getKey(), model);
	}
		
	public XmlCrossComputePropertiesParser parse(String filename) throws SystemException {		
		Digester digester = new Digester();
		digester.setValidating(false);
		
		digester.addObjectCreate(CROSS_COMPUTES_PROPS_TAG, XmlCrossComputePropertiesParser.class);
		
		digester.addObjectCreate(COMPUTE_PROP_TAG, CrossComputePropertiesModel.class);
		digester.addSetProperties(COMPUTE_PROP_TAG);
			digester.addObjectCreate(COMPUTE_VAR_DEF_TAG, VarDefModel.class);		
			digester.addSetProperties(COMPUTE_VAR_DEF_TAG);
				digester.addObjectCreate(VAR_TAG, VariableModel.class);		
				digester.addSetProperties(VAR_TAG);
				digester.addSetNext(VAR_TAG, "addVar");
	        digester.addSetNext(COMPUTE_VAR_DEF_TAG, "addVarDefinition");
	    digester.addSetNext(COMPUTE_PROP_TAG, "addCrossComputePropertie");
    
        try {
			return (XmlCrossComputePropertiesParser)digester.parse(new File(filename));
		} catch (Exception e) {		
			LOG.fatal("Impossible to parse Monitors config", e);
			throw new SystemException(e.getMessage(), e, BaseException.ERROR);
		} 
    }	
	
	public Map<String, CrossComputePropertiesModel> getCrossComputeProperties() {
		return CrossComputeProperties;
	}

	public static void main(String[] args) {
		try {
			XmlCrossComputePropertiesParser model = (new XmlCrossComputePropertiesParser()).parse("C:/dev/indiktor-suite/component/toolkits/resources/crossComputePropertiesDEFAULT.xml");
			System.out.println("FINISHED");
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
}
