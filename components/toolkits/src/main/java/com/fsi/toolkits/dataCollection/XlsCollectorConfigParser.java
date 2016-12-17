package com.fsi.toolkits.dataCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.fsi.toolkits.config.XlsConfigParser;

public class XlsCollectorConfigParser extends XlsConfigParser{
	
	private Map<String, List<CollectorConfigXlsModel>> collectors;
	
	public XlsCollectorConfigParser(String logicalEnvName) {
		super(logicalEnvName);
		collectors = new HashMap<String, List<CollectorConfigXlsModel>>();
		
	}
	
	public void parseDocument() {	
		String collectorPathName = toolkitConfigHome + File.separator + "collectors" + logicalEnvName + ".xls";
		
		try {  		
			File file = new File(collectorPathName);
			POIFSFileSystem poifs = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook workbook = new HSSFWorkbook(poifs);
			int sheetNumber = workbook.getNumberOfSheets();
			for(int i=0; i<sheetNumber; i++) {	
				String name = workbook.getSheetName(i);
				if("CALYPSO".equalsIgnoreCase(name.trim())){
					parseConfig(workbook.getSheetAt(i), "CALYPSO");
				}
				else if ("JVM".equalsIgnoreCase(name.trim())) {
					parseConfig(workbook.getSheetAt(i), "JVM");
				}
				else if ("THREAD".equalsIgnoreCase(name.trim())) {
					parseConfig(workbook.getSheetAt(i), "THREAD");
				}
				else if ("SYSTEM_PROCESS".equalsIgnoreCase(name.trim())) {
					parseConfig(workbook.getSheetAt(i), "SYSTEM_PROCESS");
				}
				else if ("SYSTEM".equalsIgnoreCase(name.trim())) {
					parseConfig(workbook.getSheetAt(i), "SYSTEM");
				}
				else if ("DBMS".equalsIgnoreCase(name.trim())) {
					parseConfig(workbook.getSheetAt(i), "DBMS");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parseConfig(HSSFSheet sheet, String type) {
		List<String[]> rowValues = getRowValues(sheet);
		for (String[] values : rowValues) {
			String contextVar = values[0];
			String domainType = values[1];
			String metricDomain = values[2];
			String connectorVar = values[3];			
			boolean autoStart = Boolean.valueOf(values[4]).booleanValue();
			
			String context = null;
			String connector = null;
			if ("CALYPSO".equals(type)) {			
				context = getValue(contextVar, "CALYPSO_ENV");
				connector = getValue(connectorVar, "CALYPSO_ENV");
			}
			else if ("JVM".equals(type) || "THREAD".equals(type)) {
				context = contextVar;
				connector = getValue(connectorVar, "JMX_HOSTNAME");
			}
			else if ("SYSTEM_PROCESS".equals(type) || "SYSTEM".equals(type)) {
				context = getValue(contextVar, "SYSTEM_AGENT_HOSTNAME");
				connector = getValue(connectorVar, "SYSTEM_AGENT_HOSTNAME");
			}
			else if ("DBMS".equals(type)) {
				context = getValue(contextVar, "DB_INSTANCE");
				connector = getValue(connectorVar, "DB_INSTANCE");
			}
			
			if (context!=null && connector!=null) {
				CollectorConfigXlsModel config = new CollectorConfigXlsModel(context, domainType, metricDomain, connector, autoStart);
				config.setMetricDomainImpl("INDIKTOR_"+metricDomain);
				
				parseAttributes(config, values, type);
				
				List<CollectorConfigXlsModel> configs = collectors.get(domainType);
				if (configs == null) {
					configs = new ArrayList<CollectorConfigXlsModel>();
				}			
				configs.add(config);
				collectors.put(domainType, configs);
			}
		}
	}
	
	private void parseAttributes(CollectorConfigXlsModel config, Object[] values, String type) {
		if ("SYSTEM_PROCESS".equals(type)) {
			String processNameFilterVar = (String)values[5];
			String jarNameFilterVar = (String)values[6];
			String javaClassNameFilterVar = (String)values[7];
			String argFilterVar = (String)values[8];
			
			String processNameFilter = getValue(processNameFilterVar, "PROCESS_NAME_FILTERS");
			String jarNameFilter = getValue(jarNameFilterVar, "JAR_NAME_FILTERS");
			String javaClassNameFilter = getValue(javaClassNameFilterVar, "JAVA_CLASS_NAME_FILTERS");
			String argFilter = getValue(argFilterVar, "ARGUMENT_FILTERS");
			
			config.addAttribute("PROCESS_NAME_FILTERS", (processNameFilter!=null&&processNameFilter.length()>0)?processNameFilter:"*");
			config.addAttribute("JAR_NAME_FILTERS", (jarNameFilter!=null&&jarNameFilter.length()>0)?jarNameFilter:"*");
			config.addAttribute("CLASS_NAME_FILTERS", (javaClassNameFilter!=null&&javaClassNameFilter.length()>0)?javaClassNameFilter:"*");
			config.addAttribute("ARGUMENT_FILTERS", (argFilter!=null&&argFilter.length()>0)?argFilter:"*");
		}
	}

	public Map<String, List<CollectorConfigXlsModel>> getCollectorMap() {
		return collectors;
	}	
	
	public List<CollectorConfigXlsModel> getCollectorList() {
		List<CollectorConfigXlsModel> res = new ArrayList<CollectorConfigXlsModel>();
		for(List<CollectorConfigXlsModel> configs : collectors.values()) {
			res.addAll(configs);
		}
		return res;
	}
}
