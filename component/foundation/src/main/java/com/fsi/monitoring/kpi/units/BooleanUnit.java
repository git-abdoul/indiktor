package com.fsi.monitoring.kpi.units;

import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum BooleanUnit implements IkrUnit { 
    BOOLEAN;	
    
    public String getSymbol() {
    	return "";
    }  
    
    public Number getDivider() {
		return 1;
	}
    
    public static String convert(String value, BooleanUnit fromUnit) {
    	if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
    		throw new IllegalArgumentException();
    	}
    	
    	Boolean res = Boolean.valueOf(value);
    	
    	return res.toString();
    }
    
    public static FormattedValue format(String value, BooleanUnit origUnit) {
    	FormattedValue res = null;    	
    	res = new FormattedValue(value, BOOLEAN);    	
    	return res;
    }
    
    public String convertTo(String value, BooleanUnit toUnit) {
    	return value;
    }
    
    public static String convertTo (String value, BooleanUnit fromUnit, BooleanUnit toUnit) {
    	return value;
    }

	public static Collection<MetricCompute> getSupportedComputes() {
		return null;
	}

	public boolean isChartSupported() {
		return false;
	}	
	
} 
