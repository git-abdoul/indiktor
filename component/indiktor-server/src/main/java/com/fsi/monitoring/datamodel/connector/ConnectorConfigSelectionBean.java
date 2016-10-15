package com.fsi.monitoring.datamodel.connector;

import java.util.HashSet;
import java.util.Set;

import com.fsi.monitoring.connector.ConnectorConfig;

public class ConnectorConfigSelectionBean {
	
	private boolean selected;
	private ConnectorConfig connectorConfig;
	
	private Set<String> searchIndexes;

	public ConnectorConfigSelectionBean(ConnectorConfig connectorConfig) {
		super();
		this.connectorConfig = connectorConfig;
		
		this.initSearchIndexes();
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();
		searchIndexes.add(connectorConfig.getName().toLowerCase());
		searchIndexes.add(connectorConfig.getType().toLowerCase());
		if (connectorConfig.getDescription()!=null && connectorConfig.getDescription().length()>0)
			searchIndexes.add(connectorConfig.getDescription().toLowerCase());
		searchIndexes.add(connectorConfig.getConnectorContext().toLowerCase());
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public ConnectorConfig getConnectorConfig() {
		return connectorConfig;
	}

	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}	
}
