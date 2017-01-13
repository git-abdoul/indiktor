package com.fsi.monitoring.system.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SystemAgentConfigManager {
	private static final Logger LOG = Logger.getLogger(SystemAgentConfigManager.class);
	
	public SystemAgentConfigManager() throws Exception {}

	public void loadConfiguration() {
		try {
			SystemAgentContext context = getContext();
			
			Properties appPropertiesFile = new Properties();
			appPropertiesFile.load(new FileInputStream(System.getProperty("app.prop")));
			
			Properties services = new Properties();
			services.load(new FileInputStream(System.getProperty("services.prop")));
			context.setServices(services);
			
			context.setSigarHome(System.getProperty("sigar.home"));
			context.setSigarLibName(appPropertiesFile.getProperty("system_agent.sigar.lib.name"));
			context.setSystemAgentPort(Integer.parseInt(appPropertiesFile.getProperty("system_agent.port")));
			context.setJstatdPort(Integer.parseInt(appPropertiesFile.getProperty("system_agent.jstatd.port")));
			context.setServerName(appPropertiesFile.getProperty("system_agent.jstatd.servername"));
			context.setJavaProcessIndexName(appPropertiesFile.getProperty("java.process.index.name"));
			context.setEnableSocket(Boolean.parseBoolean(appPropertiesFile.getProperty("system_agent.socket.enable")));
			context.setSocketPort(Integer.parseInt(appPropertiesFile.getProperty("system_agent.socket.port")));
			context.setSocketMaxConnection(Integer.parseInt(appPropertiesFile.getProperty("system_agent.socket.max-connections")));
			context.setBlockPattern(appPropertiesFile.getProperty("system_agent.socket_message.BLOCK_PATTERN"));
			context.setTagPattern(appPropertiesFile.getProperty("system_agent.socket_message.TAG_PATTERN"));
			
			String mandatoryTags = appPropertiesFile.getProperty("system_agent.socket_message.MANDATORY_TAGS");
			context.setMandatoryTags((mandatoryTags!=null&&mandatoryTags.length()>0) ? mandatoryTags.split(",") : null);
			
			context.setEnableLogAnalyzer(Boolean.parseBoolean(appPropertiesFile.getProperty("system_agent.log_analyzer.enable")));
			context.setCheckDelay(Long.parseLong(appPropertiesFile.getProperty("system_agent.log_analyzer.check_delay")));
			String rootDirectories = appPropertiesFile.getProperty("system_agent.log_analyzer.root_directory");
			context.setRootDirectories((rootDirectories!=null&&rootDirectories.length()>0) ? rootDirectories.split(",") : null);
			context.setFileFormat(appPropertiesFile.getProperty("system_agent.log_analyzer.file_format"));
			context.setFileFormatName(appPropertiesFile.getProperty("system_agent.log_analyzer.file_format.NAME"));
			context.setFileFormatDatetimeFormat(appPropertiesFile.getProperty("system_agent.log_analyzer.file_format.DATETIME-FORMAT"));
			context.setFileFormatExtension(appPropertiesFile.getProperty("system_agent.log_analyzer.file_format.EXTENSION"));
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e);
			System.err.println("Exiting ...");
			System.exit(1);
		} 		
	}		
	
	protected SystemAgentContext getContext() {
		return SystemAgentContext.getContext();
	}
}
