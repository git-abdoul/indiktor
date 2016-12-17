package com.fsi.monitoring.system.config;

import java.util.Properties;

import com.fsi.fwk.apps.config.AbstractApplicationContext;

public class SystemAgentContext extends AbstractApplicationContext {
	private String sigarHome;
	private String sigarLibName;
	private int systemAgentPort;
	private int jstatdPort;
	private String serverName;
	
	private String javaProcessIndexName;
	
	private boolean enableSocket;
	private int  socketPort;
	private int socketMaxConnection;
	
	private String blockPattern;
	private String tagPattern;
	private String[] mandatoryTags;
	
	private boolean enableLogAnalyzer;
	private long checkDelay;
	private String[] rootDirectories;
	private String fileFormat;
	private String fileFormatName;
	private String fileFormatDatetimeFormat;
	private String fileFormatExtension;	

	
	private Properties services;

	private SystemAgentContext() {}
	
	public static SystemAgentContext getContext() {
		if (singleton == null) {
			singleton = new SystemAgentContext();
		}	
		return (SystemAgentContext)singleton;
	}

	public String getSigarHome() {
		return sigarHome;
	}

	public String getSigarLibName() {
		return sigarLibName;
	}

	public int getSystemAgentPort() {
		return systemAgentPort;
	}

	public int getJstatdPort() {
		return jstatdPort;
	}

	public void setSigarHome(String sigarHome) {
		this.sigarHome = sigarHome;
	}

	public void setSigarLibName(String sigarLibName) {
		this.sigarLibName = sigarLibName;
	}

	public void setSystemAgentPort(int systemAgentPort) {
		this.systemAgentPort = systemAgentPort;
	}

	public void setJstatdPort(int jstatdPort) {
		this.jstatdPort = jstatdPort;
	}

	public Properties getServices() {
		return services;
	}

	public void setServices(Properties services) {
		this.services = services;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getJavaProcessIndexName() {
		return javaProcessIndexName;
	}

	public void setJavaProcessIndexName(String javaProcessIndexName) {
		this.javaProcessIndexName = javaProcessIndexName;
	}

	public boolean isEnableSocket() {
		return enableSocket;
	}

	public void setEnableSocket(boolean enableSocket) {
		this.enableSocket = enableSocket;
	}

	public int getSocketPort() {
		return socketPort;
	}

	public void setSocketPort(int socketPort) {
		this.socketPort = socketPort;
	}

	public int getSocketMaxConnection() {
		return socketMaxConnection;
	}

	public void setSocketMaxConnection(int socketMaxConnection) {
		this.socketMaxConnection = socketMaxConnection;
	}

	public String getBlockPattern() {
		return blockPattern;
	}

	public void setBlockPattern(String blockPattern) {
		this.blockPattern = blockPattern;
	}

	public String getTagPattern() {
		return tagPattern;
	}

	public void setTagPattern(String tagPattern) {
		this.tagPattern = tagPattern;
	}

	public String[] getMandatoryTags() {
		return mandatoryTags;
	}

	public void setMandatoryTags(String[] mandatoryTags) {
		this.mandatoryTags = mandatoryTags;
	}

	public boolean isEnableLogAnalyzer() {
		return enableLogAnalyzer;
	}

	public void setEnableLogAnalyzer(boolean enableLogAnalyzer) {
		this.enableLogAnalyzer = enableLogAnalyzer;
	}

	public long getCheckDelay() {
		return checkDelay;
	}

	public void setCheckDelay(long checkDelay) {
		this.checkDelay = checkDelay;
	}

	public String[] getRootDirectories() {
		return rootDirectories;
	}

	public void setRootDirectories(String[] rootDirectories) {
		this.rootDirectories = rootDirectories;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getFileFormatName() {
		return fileFormatName;
	}

	public void setFileFormatName(String fileFormatName) {
		this.fileFormatName = fileFormatName;
	}

	public String getFileFormatDatetimeFormat() {
		return fileFormatDatetimeFormat;
	}

	public void setFileFormatDatetimeFormat(String fileFormatDatetimeFormat) {
		this.fileFormatDatetimeFormat = fileFormatDatetimeFormat;
	}

	public String getFileFormatExtension() {
		return fileFormatExtension;
	}

	public void setFileFormatExtension(String fileFormatExtension) {
		this.fileFormatExtension = fileFormatExtension;
	}		
}
