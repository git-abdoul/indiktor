package com.fsi.monitoring.kpi.units;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum DatetimeUnit implements IkrUnit { 
    DATETIME;	
    
    public String getSymbol() {
    	return "";
    } 
    
    public Number getDivider() {
		return 1;
	}
    
    public static Collection<MetricCompute> getSupportedComputes() {
		return null;
	} 
    
    public static String convert(String value, DatetimeUnit fromUnit) {
    	return value;
    }
    
    public String convertTo(String value, DatetimeUnit toUnit) {
    	return String.valueOf(value);
    }
    
    public static String convertTo(String value, DatetimeUnit fromUnit, DatetimeUnit toUnit) {
    	return value;
    }
    
    public static FormattedValue format(String value, DatetimeUnit origUnit) {
    	FormattedValue res = null;
    	long longVal = Long.parseLong(value);
    	if (longVal > 0) {
	    	Date date = new Date(Long.parseLong(value));
	    	String dateStr = dateFormat.format(date);
	    	res = new FormattedValue(dateStr, DATETIME);
    	}
    	else {
    		res = new FormattedValue("", DATETIME);
    	}  
    	
    	return res;
    }
    
    public boolean isChartSupported() {
		return false;
	}
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
} 
