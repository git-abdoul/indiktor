package com.fsi.monitoring.user;

import java.io.Serializable;

public class AccessPerm 
implements Serializable {

	private static final long serialVersionUID = 4983327986363976506L;

	private long id;
	
	private String name;
	private String description;
	
	public AccessPerm() {}
	
	public AccessPerm(long id,
					  String name,
					  String description) {
		this.id = id;
		this.name=name;
		this.description=description;
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
