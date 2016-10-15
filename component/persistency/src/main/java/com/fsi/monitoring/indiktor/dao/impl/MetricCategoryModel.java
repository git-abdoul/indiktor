package com.fsi.monitoring.indiktor.dao.impl;


public class MetricCategoryModel {
	private String resource;
	private String name;
	private String label;
	private String unitType;
	private String unit;
	private String description;
	private boolean persistent;
	private boolean archive;
	private String searchIndexes;
	
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isPersistent() {
		return persistent;
	}
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	public boolean isArchive() {
		return archive;
	}
	public void setArchive(boolean archive) {
		this.archive = archive;
	}
	public String getSearchIndexes() {
		return searchIndexes;
	}
	public void setSearchIndexes(String searchIndexes) {
		this.searchIndexes = searchIndexes;
	}		
}
