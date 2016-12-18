package com.fsi.monitoring.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.icesoft.faces.context.Resource;

public class XlsMonitorConfigParser implements Serializable{
	public static final String RESOURCE_PATH = "/WEB-INF/classes/";
	
	private String configFileName;	
	private Map<String, List<MonitorConfig>> monitorConfigs;	
	
	private Resource resource;
	private ExternalContext ec;
	
	public XlsMonitorConfigParser() {
		this.configFileName = "MonitorConfigs.xls";		
		monitorConfigs = new HashMap<String, List<MonitorConfig>>();
		FacesContext fc = FacesContext.getCurrentInstance();
		ec = fc.getExternalContext();
        resource = new ResourceDownload(ec, RESOURCE_PATH, configFileName, ResourceDownload.EXCEL);
	}
	
	public void initParser(File file) {		
		try {  
			POIFSFileSystem poifs = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook workbook = new HSSFWorkbook(poifs);
			int sheetNumber = workbook.getNumberOfSheets();
			for(int i=0; i<sheetNumber; i++) {	
				String name = workbook.getSheetName(i);
				if("CALYPSO".equalsIgnoreCase(name.trim())){
					parseCalypso(workbook.getSheetAt(i));
				}
				else if ("JVM".equalsIgnoreCase(name.trim())) {
					parseJmx(workbook.getSheetAt(i), "JVM");
				}
				else if ("THREAD".equalsIgnoreCase(name.trim())) {
					parseJmx(workbook.getSheetAt(i), "THREAD");
				}
				else if ("SYSTEM_PROCESS".equalsIgnoreCase(name.trim())) {
					parseSystemProcess(workbook.getSheetAt(i));
				}
				else if ("SYSTEM".equalsIgnoreCase(name.trim())) {
					parseSystem(workbook.getSheetAt(i));
				}
				else if ("DBMS".equalsIgnoreCase(name.trim())) {
					parseDbms(workbook.getSheetAt(i));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getConfigFileName() {
		return configFileName;
	}

	public Resource getResource() {
		return resource;
	}

	private void parseCalypso(HSSFSheet sheet) {
		List<Object[]> rowValues = getRowValues(sheet);
		DataModelPM dataModelPM = (DataModelPM) FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		for (Object[] values : rowValues) {
			String type = (String)values[0];
			String username = (String)values[1];
			String password = (String)values[2];
			String calEnv = (String)values[3];			
			long captureDelay = Long.valueOf((int)((Double)values[4]).doubleValue());
			boolean started = Boolean.valueOf((String)values[5]).booleanValue();
			MonitorConfig config = null;// new MonitorConfig();
			String name = type + " Monitor";
//			try {
//				IkrStaticDomain domain = dataModelPM.getIkrStaticDomain("CALYPSO_AGENT", type);
//				name = domain.getLabel() + " Monitor";
//			} catch (PersistenceException e) {}			
//			config.setName(name);
//			config.setCaptureDelay(captureDelay);
//			config.setMonitorType(type);
//			config.setUserName(username);
//			config.setPassword((password!=null && password.length()>0)?password:"");			
			config.setAutoStart(started);
			config.addAttribute("CALYPSO_ENV", calEnv);
			config.addAttribute("APPLICATION_NAME", "Indiktor");
			List<MonitorConfig> configs = monitorConfigs.get("CALYPSO");
			if (configs == null) {
				configs = new ArrayList<MonitorConfig>();
				monitorConfigs.put("CALYPSO", configs);
			}
			configs.add(config);
		}
	}
	
	private void parseJmx(HSSFSheet sheet, String type) {
		List<Object[]> rowValues = getRowValues(sheet);
		for (Object[] values : rowValues) {
			String processName = (String)values[0];
			String hostname = (String)values[1];
			String username = (String)values[2];
			String password = (String)values[3];
			int port = Integer.valueOf((int)((Double)values[4]).doubleValue());
			long captureDelay = Long.valueOf((int)((Double)values[5]).doubleValue());
			boolean started = Boolean.valueOf((String)values[6]).booleanValue();
			MonitorConfig config = null;//new MonitorConfig();
//			config.setName(processName + " Monitor");
//			if("JVM".equalsIgnoreCase(type))
//				config.setMonitorType("JMX_MONITOR");
//			else if("THREAD".equalsIgnoreCase(type))
//				config.setMonitorType("JMX_THREAD_MONITOR");
//			config.setHostname(hostname);
//			config.setUserName((username!=null && username.length()>0)?username:"");
//			config.setPassword((password!=null && password.length()>0)?password:"");
//			config.setCaptureDelay(captureDelay);
//			config.setPort(port);
			config.addAttribute("PROCESS_NAME", processName);
			config.setAutoStart(started);
			List<MonitorConfig> configs = monitorConfigs.get(type);
			if (configs == null) {
				configs = new ArrayList<MonitorConfig>();
				monitorConfigs.put(type, configs);
			}
			configs.add(config);
		}
	}

	private void parseSystemProcess(HSSFSheet sheet) {
		List<Object[]> rowValues = getRowValues(sheet);
		for (Object[] values : rowValues) {
			String name = (String)values[0];
			long captureDelay = Long.valueOf((int)((Double)values[1]).doubleValue());
			String hostname = (String)values[2];
			int port = Integer.valueOf((int)((Double)values[3]).doubleValue());
			String processNameFilter = (String)values[4];
			String jarNameFilter = (String)values[5];
			String javaClassNameFilter = (String)values[6];
			boolean started = Boolean.valueOf((String)values[7]).booleanValue();
			MonitorConfig config = null;//new MonitorConfig();
//			config.setName(name);
//			config.setMonitorType("PROCESS_MONITOR");
//			config.setCaptureDelay(captureDelay);
//			config.setHostname(hostname);
//			config.setPort(port);
			config.addAttribute("PROCESS_NAME_FILTERS", processNameFilter);
			config.addAttribute("JAR_NAME_FILTERS", jarNameFilter);
			config.addAttribute("CLASS_NAME_FILTERS", javaClassNameFilter);
			config.setAutoStart(started);
			List<MonitorConfig> configs = monitorConfigs.get("SYSTEM_PROCESS");
			if (configs == null) {
				configs = new ArrayList<MonitorConfig>();
				monitorConfigs.put("SYSTEM_PROCESS", configs);
			}
			configs.add(config);
		}
	}

	private void parseSystem(HSSFSheet sheet) {
		List<Object[]> rowValues = getRowValues(sheet);
		for (Object[] values : rowValues) {
			long captureDelay = Long.valueOf((int)((Double)values[0]).doubleValue());
			String hostname = (String)values[1];
			int port = Integer.valueOf((int)((Double)values[2]).doubleValue());
			boolean started = Boolean.valueOf((String)values[3]).booleanValue();
			MonitorConfig config = null;//new MonitorConfig();
//			config.setName(hostname + " Monitor");
//			config.setMonitorType("SYSTEM_MONITOR");
//			config.setCaptureDelay(captureDelay);
//			config.setHostname(hostname);
//			config.setPort(port);
			config.setAutoStart(started);
			List<MonitorConfig> configs = monitorConfigs.get("SYSTEM");
			if (configs == null) {
				configs = new ArrayList<MonitorConfig>();
				monitorConfigs.put("SYSTEM", configs);
			}
			configs.add(config);
		}
	}

	private void parseDbms(HSSFSheet sheet) {
		List<Object[]> rowValues = getRowValues(sheet);
		for (Object[] values : rowValues) {
			String monitorType = (String)values[0];
			long captureDelay = Long.valueOf((int)((Double)values[1]).doubleValue());
			String userName = (String)values[2];
			String tmp = (String)values[3];
			String pwd = (tmp!=null && tmp.length()>0)?tmp:"";
			String hostname = (String)values[4];			
			int port = Integer.valueOf((int)((Double)values[5]).doubleValue());
			String connectionUri = (String)values[6];
			String instance = (String)values[7];
			String driver = (String)values[8];
			boolean started = Boolean.valueOf((String)values[9]).booleanValue();
			MonitorConfig config = null;//new MonitorConfig();
//			if("sybase".equalsIgnoreCase(monitorType))
//				config.setMonitorType("SYBASE_MONITOR");
//			else if ("oracle".equalsIgnoreCase(monitorType))
//				config.setMonitorType("ORACLE_MONITOR");
//			else
//				continue;
//			config.setName(instance + " Monitor");
//			config.setCaptureDelay(captureDelay);
//			config.setUserName(userName);
//			config.setPassword(pwd);
//			config.setHostname(hostname);
//			config.setPort(port);
			config.addAttribute("CONNECTION_URI", connectionUri);
			config.addAttribute("INSTANCE", instance);
			config.addAttribute("DRIVER", driver);
			config.setAutoStart(started);
			List<MonitorConfig> configs = monitorConfigs.get("DBMS");
			if (configs == null) {
				configs = new ArrayList<MonitorConfig>();
				monitorConfigs.put("DBMS", configs);
			}
			configs.add(config);
		}
	}
	
	private List<Object[]> getRowValues(HSSFSheet sheet) {
		List<Object[]> rowValues = new ArrayList<Object[]>();
		for (int j = 1; j < sheet.getLastRowNum() + 1; j++) {
			HSSFRow row = sheet.getRow(j);
			if (row == null)
				continue;
			int sz = row.getLastCellNum();
			Object[] values = new Object[sz];
			for (int i = 0; i < sz; i++) {
				HSSFCell cell = row.getCell((short) i);
				if (cell != null) {	
					if (HSSFCell.CELL_TYPE_STRING == cell.getCellType())
						values[i] = cell.getRichStringCellValue().getString();
					else if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType())				
						values[i] = cell.getNumericCellValue();
					else
						values[i] = "";
				}
				else 
					values[i] = "";		
			}
			rowValues.add(values);
		}
		
		return rowValues;
	}
	

	public List<MonitorConfig> getCalypsoMonitorConfigs() {
		return monitorConfigs.get("CALYPSO");
	}

	public List<MonitorConfig> getJvmMonitorConfigs() {
		return monitorConfigs.get("JVM");
	}
	
	public List<MonitorConfig> getThreadMonitorConfigs() {
		return monitorConfigs.get("THREAD");
	}

	public List<MonitorConfig> getProcessMonitorConfigs() {
		return monitorConfigs.get("SYSTEM_PROCESS");
	}

	public List<MonitorConfig> getSystemMonitorConfigs() {
		return monitorConfigs.get("SYSTEM");
	}

	public List<MonitorConfig> getDbmsMonitorConfigs() {
		return monitorConfigs.get("DBMS");
	}
}
