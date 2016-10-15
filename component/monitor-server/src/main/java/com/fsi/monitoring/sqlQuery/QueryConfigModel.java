package com.fsi.monitoring.sqlQuery;

import java.util.ArrayList;
import java.util.List;

public class QueryConfigModel {
	private String connector;
	private String query;
	
	private List<QueryItemModel> queryItems;	
	
	
	public QueryConfigModel() {
		queryItems = new ArrayList<QueryItemModel>();
	}		

	public void addItem(QueryItemModel item) {
		queryItems.add(item);
	}
	
	public List<QueryItemModel> getQueryItems() {
		return queryItems;
	}
	
	public String getConnector() {
		return connector;
	}
	
	public void setConnector(String connector) {
		this.connector = connector;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
}
