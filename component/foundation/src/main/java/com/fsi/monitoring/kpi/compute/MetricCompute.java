package com.fsi.monitoring.kpi.compute;

public enum MetricCompute {
	RT,STATIC,MM20,MM50,MM100;
	
	public static MetricCompute getCompute(String value) {
		MetricCompute compute = null;
		if (RT.name().equals(value)) {
			compute = RT;
		}
		else if (STATIC.name().equals(value)) {
			compute = STATIC;
		}
		else if (MM20.name().equals(value)) {
			compute = MM20;
		}
		else if (MM50.name().equals(value)) {
			compute = MM50;
		}
		else if (MM100.name().equals(value)) {
			compute = MM100;
		}		
		return compute;
	}
	
}
