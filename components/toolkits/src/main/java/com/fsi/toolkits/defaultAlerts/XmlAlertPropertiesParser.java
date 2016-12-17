package com.fsi.toolkits.defaultAlerts;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;
import com.fsi.toolkits.VarDefModel;
import com.fsi.toolkits.VariableModel;

public class XmlAlertPropertiesParser {	
	private static final Logger LOG = Logger.getLogger(XmlAlertPropertiesParser.class);	
	
	private static final String DEFAULT_ALERT_PROPS	= "DEFAULT_ALERT_PROPS";
	private static final String ALERT_PROP_TAG	= "DEFAULT_ALERT_PROPS/ALERT_PROP";
	private static final String ALERT_VAR_DEF_TAG	= "DEFAULT_ALERT_PROPS/ALERT_PROP/ALERT_VAR_DEF";
	private static final String VAR_TAG	= "DEFAULT_ALERT_PROPS/ALERT_PROP/ALERT_VAR_DEF/VAR";
	
	private Map<String, AlertPropertiesModel> alertProperties;
	
	public XmlAlertPropertiesParser() {
		alertProperties = new HashMap<String, AlertPropertiesModel>();
	}

	public void addAlertPropertie(AlertPropertiesModel model) {
		alertProperties.put(model.getType(), model);
	}
		
	public XmlAlertPropertiesParser parse(String filename) throws SystemException {		
		Digester digester = new Digester();
		digester.setValidating(false);
		
		digester.addObjectCreate(DEFAULT_ALERT_PROPS, XmlAlertPropertiesParser.class);
		
		digester.addObjectCreate(ALERT_PROP_TAG, AlertPropertiesModel.class);
		digester.addSetProperties(ALERT_PROP_TAG);
			digester.addObjectCreate(ALERT_VAR_DEF_TAG, VarDefModel.class);		
			digester.addSetProperties(ALERT_VAR_DEF_TAG);
				digester.addObjectCreate(VAR_TAG, VariableModel.class);		
				digester.addSetProperties(VAR_TAG);
				digester.addSetNext(VAR_TAG, "addVar");
	        digester.addSetNext(ALERT_VAR_DEF_TAG, "addVarDefinition");
	    digester.addSetNext(ALERT_PROP_TAG, "addAlertPropertie");
    
        try {
			return (XmlAlertPropertiesParser)digester.parse(new File(filename));
		} catch (Exception e) {		
			LOG.fatal("Impossible to parse Monitors config", e);
			throw new SystemException(e.getMessage(), e, BaseException.ERROR);
		} 
    }	
	
	public Map<String, AlertPropertiesModel> getAlertProperties() {
		return alertProperties;
	}

	public static void main(String[] args) {
		try {
			XmlAlertPropertiesParser model = (new XmlAlertPropertiesParser()).parse("C:/dev/indiktor-suite/component/toolkits/resources/defaultAlertProperties.xml");
			System.out.println("FINISHED");
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
}
