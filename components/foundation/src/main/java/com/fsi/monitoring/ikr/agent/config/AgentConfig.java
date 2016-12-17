package com.fsi.monitoring.ikr.agent.config;

import java.io.Serializable;


public class AgentConfig
implements Serializable {

	private static final long serialVersionUID = 4742692332441419129L;

	private long id;
	private String name;
	private String type;
	
	public AgentConfig() {}
	
	public AgentConfig(long id,
					   String name,
					   String type) {
		this.id = id;
		this.name = name;
		this.type = type;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}	
}
