package com.fsi.monitoring.ikr.monitor.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.monitor.MonitorConfigAttributeKey;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;

public class MonitorConfig 
implements Serializable, Comparable<MonitorConfig>, Cloneable{

	private static final long serialVersionUID = 404951353021159118L;
	
	private long id;
	private int logicalEnvId;
	private String context;
	private MetricDomainConfig metricDomainConfig;
	private Map<String, String> attributes;
	private Collection<Integer> connectorConfigIds;
	private IkrMonitorSchedulerConfig schedulerConfig;	
	
	private boolean autoStart;	
	
	public MonitorConfig() {
		connectorConfigIds = new ArrayList<Integer>();
		metricDomainConfig = new MetricDomainConfig();
		attributes = new HashMap<String, String>();
		schedulerConfig = new IkrMonitorSchedulerConfig();
	}
	
	public MonitorConfig(long id,
					   	 int logicalEnvId,
					   	 String context,
					   	 MetricDomainConfig metricDomainConfig,
					   	 IkrMonitorSchedulerConfig schedulerConfig,
					     boolean autoStart) {
		super();
		this.id = id;
		this.logicalEnvId = logicalEnvId;
		this.context = context;
		this.metricDomainConfig = metricDomainConfig;
		this.schedulerConfig = schedulerConfig;
		this.attributes = new HashMap<String, String>();
		this.autoStart = autoStart;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public int getMainConnectorId() {
		if (connectorConfigIds != null) {
			return connectorConfigIds.iterator().next();
		} else {
			return 0;
		}
	}

	public int getLogicalEnvId() {
		return logicalEnvId;
	}
	
	public void setLogicalEnvId(int logicalEnvId) {
		this.logicalEnvId = logicalEnvId;
	}
	
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public MetricDomainConfig getMetricDomainConfig() {
		return metricDomainConfig;
	}
	
	public void setMetricDomainConfig(MetricDomainConfig metricDomainConfig) {
		this.metricDomainConfig = metricDomainConfig;		
	}
	
	public Collection<Integer> getConnectorConfigIds() {
		return connectorConfigIds;
	}
	
	public void setConnectorConfigIds(Collection<Integer> connectorConfigIds) {
		this.connectorConfigIds = connectorConfigIds;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}	

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	public String getAttribute(String name) {
		return attributes.get(name);
	}
	
	public String getDomainView() {
		String domainView = "";
		String views = getAttribute("METRIC_VIEW");
		if (views!=null && views.length()>0) {
			domainView  = "[";
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
		return domainView;
	}

	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}
	
	public boolean isAutoStart() {
		return autoStart;
	}	

	public IkrMonitorSchedulerConfig getSchedulerConfig() {
		return schedulerConfig;
	}

	public void setSchedulerConfig(IkrMonitorSchedulerConfig schedulerConfig) {
		this.schedulerConfig = schedulerConfig;
	}

	public int compareTo(MonitorConfig o) {
		long res = id-o.getId();
		return (int)res;
	}

	public boolean isOnTheFly() {
		Map<String, String> domainConfAttrs = metricDomainConfig.getAttributes();
		String val = domainConfAttrs.get(MonitorConfigAttributeKey.ON_THE_FLY);
		boolean onTheFly = false;
		if (val!=null) {
			try {
				onTheFly = Boolean.parseBoolean(val);
			}
			catch (Exception e) {}
		}
		return onTheFly;
	}	
}
