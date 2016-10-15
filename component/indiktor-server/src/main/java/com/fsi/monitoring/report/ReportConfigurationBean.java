package com.fsi.monitoring.report;

public class ReportConfigurationBean {

	private String actionFolder;
	private String templateFolder;
	private String outFolder;
	private String watchFolder;
	
	public void setWatchFolder(String watchFolder) {
		this.watchFolder = watchFolder;
	}
	
	public String getWatchFolder() {
		return watchFolder;
	}
	
	public void setActionFolder(String actionFolder) {
		this.actionFolder = actionFolder;
	}
	
	public String getActionFolder() {
		return actionFolder;
	}
	
	public void setTemplateFolder(String template) {
		this.templateFolder = template;
	}
	
	public String getTemplateFolder() {
		return templateFolder;
	}
	
	public void setOutFolder(String out) {
		this.outFolder = out;
	}
	
	public String getOutFolder() {
		return outFolder;
	}	
	
}
