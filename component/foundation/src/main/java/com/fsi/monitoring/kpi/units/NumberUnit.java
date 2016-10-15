package com.fsi.monitoring.kpi.units;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum NumberUnit implements IkrUnit { 
    NUMBER;	
    
    public String getSymbol() {
    	return "";
    }  
    
    public Number getDivider() {
		return 1;
	}
    
    public static Collection<MetricCompute> getSupportedComputes() {
		Collection<MetricCompute> res = new ArrayList<MetricCompute>();
		res.add(MetricCompute.MM20);
		res.add(MetricCompute.MM50);
		res.add(MetricCompute.MM100);
		return res;
	}      
    
    public static String convert(String value, NumberUnit fromUnit) {
    	return nf.format(Double.valueOf(value));
    }
    
    public String convertTo(String value, NumberUnit toUnit) {
    	return nf.format(Double.valueOf(value));
    }
    
    public static String convertTo(String value, NumberUnit fromUnit, NumberUnit toUnit) {
    	return nf.format(Double.valueOf(value));
    }
    
    public static FormattedValue format(String value, NumberUnit origUnit) {
    	FormattedValue res = null;    	
    	res = new FormattedValue(nf.format(Double.valueOf(value)), NUMBER);    	
    	return res;
    }
    
    public boolean isChartSupported() {
		return true;
	}
    
    private static java.text.NumberFormat nf  = new DecimalFormat("#.##");    
    static { 
        nf.setGroupingUsed(false); 
        nf.setMinimumFractionDigits(0); 
        nf.setMaximumFractionDigits(2); 
    }
} 
