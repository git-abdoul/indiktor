package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;

public class MonitorConfigBean implements Serializable{
	private static final long serialVersionUID = -6162585330147622499L;
	
	private MonitorConfig monitorConfig;
	private LogicalEnv logicalEnv;
	private IkrStaticDomain ikrStaticDomain;	
	private String domainView;
	private List<String> connectorTypes;
	
	private String style;
	
	private boolean selected;
	
	private Set<String> searchIndexes;
	
	public MonitorConfigBean(MonitorConfig monitorConfig, LogicalEnv logicalEnv,  IkrStaticDomain ikrStaticDomain, List<String> connectorTypes) {
		super();
		this.monitorConfig = monitorConfig;
		this.ikrStaticDomain = ikrStaticDomain;
		String domainView = "";
		String views = monitorConfig.getAttribute("METRIC_VIEW");
		if (views!=null && views.length()>0) {
			domainView = "[";
			String[] items = views.split(":");
			int i = 0;
			for(String item : items) {
				domainView = domainView + item;
				if (i<items.length-1)
					domainView = domainView + ",";
				i++;
			}
			domainView = domainView + "]";
		}
		this.domainView = domainView;
		this.connectorTypes = connectorTypes;
		this.logicalEnv = logicalEnv;
		
		this.initSearchIndexes();
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();
		if (logicalEnv!=null)
			searchIndexes.add(logicalEnv.getName().toLowerCase());
		searchIndexes.add(monitorConfig.getContext().toLowerCase());
		for (String connector : connectorTypes) {
			searchIndexes.add(connector.toLowerCase());
		}
		searchIndexes.add(domainView.toLowerCase());
		searchIndexes.add(ikrStaticDomain.getLabel().toLowerCase());
		searchIndexes.add(ikrStaticDomain.getDomainValue().toLowerCase());
	}
	
	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}

	public MonitorConfig getMonitorConfig() {
		return monitorConfig;
	}
	
	public IkrStaticDomain getIkrStaticDomain() {
		return ikrStaticDomain;
	}
	 
	public String getContext() {
		return monitorConfig.getContext();
	}
	
	public long getId() {
		return monitorConfig.getId();
	}

	public String getDomainView() {
		return domainView;
	}	
	
	public String getName() {
		return ikrStaticDomain.getLabel() + " " + domainView;
	}
	
	public String getConnectorTypes() {
		String connector = "";
		for(int i=0; i<connectorTypes.size(); i++) {
			connector = connector + connectorTypes.get(i);
			
			if (i < connectorTypes.size()-1)
				connector = connector + ",";
		}
		
		return connector;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}	
	
	public String getStyle() {
		if(monitorConfig.isAutoStart())
			return "text-align: left;";
		else
			return "text-align: left; background-color: #F06161;";
	}
}
