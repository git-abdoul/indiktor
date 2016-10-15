package com.fsi.monitoring.sqlQuery;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;

public class XmlSQLQueryParser {	
	private static final Logger LOG = Logger.getLogger(XmlSQLQueryParser.class);	
	
	private static final String CONFIG_TAG	= "CONFIG";
	private static final String QUERY_CONFIG_TAG	= "CONFIG/QUERY_CONFIG";
	private static final String QUERY_ITEM_TAG	= "CONFIG/QUERY_CONFIG/QUERY_ITEM";
	
	private List<QueryConfigModel> queryConfigs;	
	
	public XmlSQLQueryParser() {
		queryConfigs = new ArrayList<QueryConfigModel>();
	}

	public void addQueryConfig(QueryConfigModel model) {
		queryConfigs.add(model);
	}
		
	public XmlSQLQueryParser parse(String filename) throws SystemException {		
		Digester digester = new Digester();
		digester.setValidating(false);
		
		digester.addObjectCreate(CONFIG_TAG, XmlSQLQueryParser.class);
		
		digester.addObjectCreate(QUERY_CONFIG_TAG, QueryConfigModel.class);
		digester.addSetProperties(QUERY_CONFIG_TAG);
			digester.addObjectCreate(QUERY_ITEM_TAG, QueryItemModel.class);
			digester.addSetProperties(QUERY_ITEM_TAG);
			digester.addSetNext(QUERY_ITEM_TAG, "addItem");
	    digester.addSetNext(QUERY_CONFIG_TAG, "addQueryConfig");
    
        try {
			return (XmlSQLQueryParser)digester.parse(new File(filename));
		} catch (Exception e) {		
			LOG.fatal("Impossible to parse Query Config", e);
			throw new SystemException(e.getMessage(), e, BaseException.ERROR);
		} 
    }	
	
	public List<QueryConfigModel> getQueryConfigs() {
		return queryConfigs;
	}

	public static void main(String[] args) {
		try {
			XmlSQLQueryParser model = (new XmlSQLQueryParser()).parse("C:/dev/indiktor-suite/component/monitor-server/sql/murex_BATCH_PROCESS_query.xml");
			System.out.println("FINISHED");
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
}
