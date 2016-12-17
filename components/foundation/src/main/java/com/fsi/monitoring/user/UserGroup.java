package com.fsi.monitoring.user;

import java.io.Serializable;
import java.util.List;

public class UserGroup
implements Serializable {

	private static final long serialVersionUID = -3985798972877692690L;

	private long id;
	private String name;
	private String description;
	private List<Long> roleIds;
	
	public UserGroup(long id,
					 String name,
					 String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public List<Long> getRoleIds() {
		return roleIds;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
}
