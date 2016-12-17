package com.fsi.monitoring.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Role
implements Serializable {

	private static final long serialVersionUID = -4126679876599740399L;

	private long id;
	private String name;
	private String description;
	
	private List<Long> accessPermIds;

	public Role() {
		this.id = 0;
		this.name = "";
		this.description = "";
		this.accessPermIds = new ArrayList<Long>();
	}
	
	public Role(long id, 
				String name,
				String description,
				List<Long> accessPermIds) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.accessPermIds = accessPermIds;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public List<Long> getAccessPermIds() {
		return accessPermIds;
	}
	
	public void setAccessPermIds(List<Long> accessPermIds) {
		this.accessPermIds = accessPermIds;
	}
}
