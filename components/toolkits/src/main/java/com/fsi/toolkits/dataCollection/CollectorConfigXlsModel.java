package com.fsi.toolkits.dataCollection;

import java.util.HashMap;
import java.util.Map;


public class CollectorConfigXlsModel {
	private String context;
	private String domainType;
	private String metricDomain;
	private String metricDomainImpl;
	private String connector;
	private boolean autoStart;
	
	private Map<String, String> attributes;

	public CollectorConfigXlsModel(String context, String domainType,
			String metricDomain, String connector, boolean autoStart) {
		super();
		this.context = context;
		this.domainType = domainType;
		this.metricDomain = metricDomain;
		this.connector = connector;
		this.autoStart = autoStart;
		
		attributes = new HashMap<String, String>();
	}

	public String getContext() {
		return context;
	}

	public String getDomainType() {
		return domainType;
	}

	public String getMetricDomain() {
		return metricDomain;
	}

	public String getConnector() {
		return connector;
	}

	public boolean isAutoStart() {
		return autoStart;
	}	

	public String getMetricDomainImpl() {
		return metricDomainImpl;
	}

	public void setMetricDomainImpl(String metricDomainImpl) {
		this.metricDomainImpl = metricDomainImpl;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	
}
