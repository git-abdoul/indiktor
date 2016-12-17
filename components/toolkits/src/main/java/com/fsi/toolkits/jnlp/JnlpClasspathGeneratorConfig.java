package com.fsi.toolkits.jnlp;

import java.util.ArrayList;
import java.util.List;

public class JnlpClasspathGeneratorConfig {
	private String jnlpDownloadProtocol;
	private String jnlpDownloadHostname;
	private String jnlpDownloadPort;
	private String jnlpDownloadPath;
	private String jarDownloadProtocol;
	private String jarDownloadHostname;
	private String jarDownloadPort;
	private String jarDownloadPath;
	private String jarDirectoryWrite;
	private String classpathEnv;
	
	private List<String> jarsExcluded;	
	
	public JnlpClasspathGeneratorConfig() {
		jarsExcluded = new ArrayList<String>();
	}
	
	public String getJnlpDownloadProtocol() {
		return jnlpDownloadProtocol;
	}
	public void setJnlpDownloadProtocol(String jnlpDownloadProtocol) {
		this.jnlpDownloadProtocol = jnlpDownloadProtocol;
	}
	public String getJnlpDownloadHostname() {
		return jnlpDownloadHostname;
	}
	public void setJnlpDownloadHostname(String jnlpDownloadHostname) {
		this.jnlpDownloadHostname = jnlpDownloadHostname;
	}
	public String getJnlpDownloadPort() {
		return jnlpDownloadPort;
	}
	public void setJnlpDownloadPort(String jnlpDownloadPort) {
		this.jnlpDownloadPort = jnlpDownloadPort;
	}
	public String getJnlpDownloadPath() {
		return jnlpDownloadPath;
	}
	public void setJnlpDownloadPath(String jnlpDownloadPath) {
		this.jnlpDownloadPath = jnlpDownloadPath;
	}
	public String getJarDownloadProtocol() {
		return jarDownloadProtocol;
	}
	public void setJarDownloadProtocol(String jarDownloadProtocol) {
		this.jarDownloadProtocol = jarDownloadProtocol;
	}
	public String getJarDownloadHostname() {
		return jarDownloadHostname;
	}
	public void setJarDownloadHostname(String jarDownloadHostname) {
		this.jarDownloadHostname = jarDownloadHostname;
	}
	public String getJarDownloadPort() {
		return jarDownloadPort;
	}
	public void setJarDownloadPort(String jarDownloadPort) {
		this.jarDownloadPort = jarDownloadPort;
	}
	public String getJarDownloadPath() {
		return jarDownloadPath;
	}
	public void setJarDownloadPath(String jarDownloadPath) {
		this.jarDownloadPath = jarDownloadPath;
	}
	public String getJarDirectoryWrite() {
		return jarDirectoryWrite;
	}
	public void setJarDirectoryWrite(String jarDirectoryWrite) {
		this.jarDirectoryWrite = jarDirectoryWrite;
	}
	public String getClasspathEnv() {
		return classpathEnv;
	}
	public void setClasspathEnv(String classpathEnv) {
		this.classpathEnv = classpathEnv;
	}
	public List<String> getJarsExcluded() {
		return jarsExcluded;
	}
	
	public void setJarNamesExcluded(String jarNamesExcluded) {
		String[] jars = (jarNamesExcluded!=null && jarNamesExcluded.length()>0)?jarNamesExcluded.split(":"):new String[0];
		for (String jar : jars) {
			if (jar!=null && jar.length()>0)
				this.jarsExcluded.add(jar);
		}
	}	
}
