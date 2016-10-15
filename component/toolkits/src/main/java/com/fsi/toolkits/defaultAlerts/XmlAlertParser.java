package com.fsi.toolkits.defaultAlerts;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;

public class XmlAlertParser {	
	private static final Logger LOG = Logger.getLogger(XmlAlertParser.class);	
	
	private static final String DEFAULT_ALERT_DEFS_TAG	= "DEFAULT_ALERT_DEFS";
	private static final String ALERT_DEF_TAG	= "DEFAULT_ALERT_DEFS/ALERT_DEF";
	private static final String INSTANCE_VARIABLES_TAG	= "DEFAULT_ALERT_DEFS/ALERT_DEF/INSTANCE_VARIABLES";
	private static final String CONDITION_TAG	= "DEFAULT_ALERT_DEFS/ALERT_DEF/CONDITIONS/CONDITION";
	private static final String COMPUTE_TAG	= "DEFAULT_ALERT_DEFS/ALERT_DEF/COMPUTES/COMPUTE";
	
	private List<AlertDefinitionModel> alertDefinitions;	
	
	public XmlAlertParser() {
		alertDefinitions = new ArrayList<AlertDefinitionModel>();
	}

	public void addAlertDef(AlertDefinitionModel model) {
		alertDefinitions.add(model);
	}
		
	public XmlAlertParser parse(String filename) throws SystemException {		
		Digester digester = new Digester();
		digester.setValidating(false);
		
		digester.addObjectCreate(DEFAULT_ALERT_DEFS_TAG, XmlAlertParser.class);
		
		digester.addObjectCreate(ALERT_DEF_TAG, AlertDefinitionModel.class);
		digester.addSetProperties(ALERT_DEF_TAG);
			digester.addObjectCreate(INSTANCE_VARIABLES_TAG, IkrInstanceVariableModel.class);
			digester.addSetProperties(INSTANCE_VARIABLES_TAG);
			digester.addObjectCreate(CONDITION_TAG, AlertConditionModel.class);		
			digester.addSetProperties(CONDITION_TAG);
			digester.addObjectCreate(COMPUTE_TAG, AlertComputeModel.class);		
			digester.addSetProperties(COMPUTE_TAG);
			digester.addSetNext(INSTANCE_VARIABLES_TAG, "setInstanceVariable");
			digester.addSetNext(CONDITION_TAG, "addCondition");
	        digester.addSetNext(COMPUTE_TAG, "addCompute");
	    digester.addSetNext(ALERT_DEF_TAG, "addAlertDef");
    
        try {
			return (XmlAlertParser)digester.parse(new File(filename));
		} catch (Exception e) {		
			LOG.fatal("Impossible to parse Monitors config", e);
			throw new SystemException(e.getMessage(), e, BaseException.ERROR);
		} 
    }	
	
	public List<AlertDefinitionModel> getAlertDefinitions() {
		return alertDefinitions;
	}

	public static void main(String[] args) {
		try {
			XmlAlertParser model = (new XmlAlertParser()).parse("C:/dev/indiktor-suite/component/toolkits/resources/defaultAlertDef.xml");
			System.out.println("FINISHED");
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
}
