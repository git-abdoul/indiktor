package com.fsi.toolkits.crossCompute;

import java.io.Serializable;

public class MetricModel implements Serializable {
	private static final long serialVersionUID = 6593871142780355612L;
	
	private String id;
	private String ikrCategoryValue;
	private String ikrInstance;
	private String context;
	private String compute;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIkrCategoryValue() {
		return ikrCategoryValue;
	}
	public void setIkrCategoryValue(String ikrCategoryValue) {
		this.ikrCategoryValue = ikrCategoryValue;
	}
	public String getIkrInstance() {
		return ikrInstance;
	}
	public void setIkrInstance(String ikrInstance) {
		this.ikrInstance = ikrInstance;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getCompute() {
		return compute;
	}
	public void setCompute(String compute) {
		this.compute = compute;
	}
}
