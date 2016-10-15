package com.fsi.toolkits.crossCompute;

import java.io.Serializable;

public class ComputeModel implements Serializable {
	private static final long serialVersionUID = 4173940155317236828L;
	
	private String name;
	private String computation;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComputation() {
		return computation;
	}
	public void setComputation(String computation) {
		this.computation = computation;
	}

}
