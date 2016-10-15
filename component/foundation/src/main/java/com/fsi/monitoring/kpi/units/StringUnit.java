package com.fsi.monitoring.kpi.units;

import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum StringUnit implements IkrUnit { 
    STRING;	
    
    public String getSymbol() {
    	return "";
    } 
    
    public Number getDivider() {
		return 1;
	}
    
    public static String convert(String value, StringUnit fromUnit) {
    	return value;
    }
    
    public String convertTo(String value, StringUnit toUnit) {
    	return value;
    }
    
    public static String convertTo(String value, StringUnit fromUnit, StringUnit toUnit) {
    	return value;
    }
    
    public static FormattedValue format(String value, StringUnit origUnit) {
    	FormattedValue res = null;
    	
    	res = new FormattedValue(value, STRING);
    	
    	return res;
    }

	public static Collection<MetricCompute> getSupportedComputes() {
		return null;
	}
	
	public boolean isChartSupported() {
		return false;
	}
} 
