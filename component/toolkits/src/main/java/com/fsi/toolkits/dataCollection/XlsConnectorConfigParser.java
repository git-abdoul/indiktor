package com.fsi.toolkits.dataCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.connector.HttpConnectorConfig;
import com.fsi.monitoring.connector.JmxConnectorConfig;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.SysloadConnectorConfig;
import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.toolkits.config.XlsConfigParser;

public class XlsConnectorConfigParser extends XlsConfigParser {
	
	private Map<String, ConnectorConfig> connectors;	
	
	public XlsConnectorConfigParser(String logicalEnvName) {
		super(logicalEnvName);
		connectors = new HashMap<String, ConnectorConfig>();
	}
	
	public void parseDocument() {
		String connectorPathName = toolkitConfigHome + File.separator + "connectors" + logicalEnvName + ".xls";
		
		try {  			
			File file = new File(connectorPathName);
			POIFSFileSystem poifs = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook workbook = new HSSFWorkbook(poifs);
			int sheetNumber = workbook.getNumberOfSheets();
			for(int i=0; i<sheetNumber; i++) {	
				String name = workbook.getSheetName(i);
				if("CALYPSO".equalsIgnoreCase(name.trim())){
					parseCalypso(workbook.getSheetAt(i));
				}
				else if ("JMX".equalsIgnoreCase(name.trim())) {
					parseJmx(workbook.getSheetAt(i));
				}
				else if ("SYSTEM_AGENT".equalsIgnoreCase(name.trim())) {
					parseSystem(workbook.getSheetAt(i));
				}
				else if ("DBMS".equalsIgnoreCase(name.trim())) {
					parseDbms(workbook.getSheetAt(i));
				}
				else if ("HTTP".equalsIgnoreCase(name.trim())) {
					parseHTTP(workbook.getSheetAt(i));
				}
				else if ("SYSLOAD".equalsIgnoreCase(name.trim())) {
					parseSysload(workbook.getSheetAt(i));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseCalypso(HSSFSheet sheet) {
		List<String[]> rowValues = getRowValues(sheet);
		for (String[] values : rowValues) {
			String nameVar = values[0];
			String usernameVar = values[1];
			String passwordVar = values[2];
			String calypsoEnvVar = values[3];
			
			String name = getValue(nameVar, "CALYPSO_ENV");
			String username = getValue(usernameVar, "CALYPSO_USER");
			String password = getValue(passwordVar, "CALYPSO_PWD");
			String calypsoEnv = getValue(calypsoEnvVar, "CALYPSO_ENV");
			
			if (calypsoEnv!=null && calypsoEnv.length()>0) {			
				CalypsoConnectorConfig config = new CalypsoConnectorConfig(0, name, name, 5, 20);
				config.setUserName(username);
				config.setPassword(password);
				config.setConnectorContext(calypsoEnv);
				config.setAsofdateActive(false);
				config.setApplicationName("IndiKtor");
				
				connectors.put(name, config);
			}
		}
	}
	
	private void parseJmx(HSSFSheet sheet) {
		List<String[]> rowValues = getRowValues(sheet);
		for (String[] values : rowValues) {
			String nameVar = values[0];
			String processName = values[1];			
			String hostnameVar = values[3];
			String usernameVar = "";
			String passwordVar = "";
			if (values.length>4) {
				usernameVar = values[4];
				passwordVar = values[5];
			}
			
			String name = getValue(nameVar, "JMX_HOSTNAME");
			String hostname = getValue(hostnameVar, "JMX_HOSTNAME");
			String username = getValue(usernameVar, "JMX_USERNAME");
			String password = getValue(passwordVar, "JMX_PASSWORD");
			
			String portStr = (String)values[2];
			
			if (portStr!=null && portStr.length()>0) {
				
				double port = Double.valueOf(portStr);
				
				JmxConnectorConfig config = new JmxConnectorConfig(0, name, name, 5, 20);
				config.setConnectorContext(hostname);
				config.setPassword(password);
				config.setPort((int)port);
				config.setUserName(username);
				config.setProcessName(processName);
				
				connectors.put(name, config);
			}
		}
	}

	private void parseSystem(HSSFSheet sheet) {
		List<String[]> rowValues = getRowValues(sheet);
		for (String[] values : rowValues) {
			String nameVar = values[0];
			String hostnameVar = values[1];
			String portVar = values[2];
			
			String name = getValue(nameVar, "SYSTEM_AGENT_HOSTNAME");
			String hostname = getValue(hostnameVar, "SYSTEM_AGENT_HOSTNAME");
			String portStr = getValue(portVar, "SYSTEM_AGENT_PORT");
			
			if ((portStr!=null && portStr.length()>0) && (hostname!=null && hostname.length()>0)) {
				int port = Integer.valueOf(portStr);
				
				SystemAgentConnectorConfig config = new SystemAgentConnectorConfig(0, name, name, 5, 20);
				config.setConnectorContext(hostname);
				config.setPort(port);
				
				connectors.put(name, config);
			}
		}
	}
	
	private void parseHTTP(HSSFSheet sheet) {
		List<String[]> rowValues = getRowValues(sheet);
		for (String[] values : rowValues) {
			String nameVar = values[0];
			String hostnameVar = values[1];
			String portVar = values[2];
			
			String name = getValue(nameVar, "HTTP_HOSTNAME");
			String hostname = getValue(hostnameVar, "HTTP_HOSTNAME");
			String portStr = getValue(portVar, "HTTP_PORT");
			
			if (portStr!=null && portStr.length()>0) {
				int port = Integer.valueOf(portStr);
				
				HttpConnectorConfig config = new HttpConnectorConfig(0, name, name, 5, 20);
				config.setConnectorContext(hostname);
				config.setPort(port);
				
				connectors.put(name, config);
			}
		}
	}
	
	private void parseSysload(HSSFSheet sheet) {
		List<String[]> rowValues = getRowValues(sheet);
		for (String[] values : rowValues) {
			String nameVar = values[0];
			String hostnameVar = values[1];
			String portVar = values[2];
			String usernameVar = values[3];
			String passwordVar = values[4];
			String agentVar = values[5];
			
			String name = getValue(nameVar, "SYSLOAD_HOSTNAME");
			String hostname = getValue(hostnameVar, "SYSLOAD_HOSTNAME");
			String username = getValue(usernameVar, "SYSLOAD_USER");
			String password = getValue(passwordVar, "SYSLOAD_USER_PASSWORD");
			String agent =  getValue(agentVar, "SYSLOAD_AGENT");
			String portStr = getValue(portVar, "SYSLOAD_PORT");
			
			if (portStr!=null && portStr.length()>0) {
				int port = Integer.valueOf(portStr);
				
				SysloadConnectorConfig config = new SysloadConnectorConfig(0, name, name, 5, 20);
				config.setConnectorContext(hostname);
				config.setPort(port);
				config.setAgent(agent);
				config.setUserName(username);
				config.setPassword(password);
				
				connectors.put(name, config);
			}
		}
	}

	private void parseDbms(HSSFSheet sheet) {
		List<String[]> rowValues = getRowValues(sheet);
		for (String[] values : rowValues) {
			String nameVar = values[0];
			String usernameVar = values[1];
			String passwordVar = values[2];
			String connectionUriVar = values[3];
			String driverVar = values[4];
			
			String name = getValue(nameVar, "DB_INSTANCE");
			String username = getValue(usernameVar, "DB_USER");
			String password = getValue(passwordVar, "DB_USER_PWD");
			String connectionUri = getValue(connectionUriVar, "DB_HOSTNAME");
			connectionUri = getValue(connectionUri, "DB_PORT");
			connectionUri = getValue(connectionUri, "DB_INSTANCE");
			String driver = getValue(driverVar, "DB_DRIVER");
			
			if ((username!=null && username.length()>0)) {
				RdbmsConnectorConfig config = new RdbmsConnectorConfig(0, name, name, 5, 20);
				config.setDriver(driver);
				config.setUserName(username);
				config.setPassword(password);
				config.setUri(connectionUri);
				
				connectors.put(name, config);
			}
		}
	}	

	public Map<String, ConnectorConfig> getConnectors() {
		return connectors;
	}	
}
