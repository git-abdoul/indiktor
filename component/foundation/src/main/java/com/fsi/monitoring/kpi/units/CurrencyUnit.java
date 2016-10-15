package com.fsi.monitoring.kpi.units;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum CurrencyUnit implements IkrUnit { 
    EURO ("EURO", 1), 
    USD  ("USD", 1), 
    GBP  ("GBP", 1), 
    INR  ("INR", 1), 
    AUD  ("AUD", 1), 
    CAD  ("CAD", 1), 
    ZAR  ("ZAR", 1),
    NZD  ("NZD", 1),
    AED  ("AED", 1),
    BRL  ("BRL", 1),
    HKD  ("HKD", 1),
    SGD  ("SGD", 1),
    JPY  ("JPY", 1);
    
    private final String symbol; 
    private final long divider;  // divider of BASE unit
    
    CurrencyUnit(String name, long divider) {
        this.symbol = name; 
        this.divider = divider; 
    } 
    
    public String getSymbol() {
    	return symbol;
    }
    
    public Number getDivider() {
		return divider;
	}
    
	public static Collection<MetricCompute> getSupportedComputes() {
		Collection<MetricCompute> res = new ArrayList<MetricCompute>();
		res.add(MetricCompute.MM20);
		res.add(MetricCompute.MM50);
		res.add(MetricCompute.MM100);
		return res;
	}      
    
    public FormattedValue format(double number) {
    	FormattedValue result = null;    	
    	String value = nf.format(number/divider);
        result = new FormattedValue(value, this);        
        return result;
    }
    
    /**
     * Convert number in Base unit (BYTE)
     * @param number
     * @return
     */
    public String convert(double number) {
    	long resL = Math.round(number * divider); 
    	return nf.format(resL);
    }    
    
    /**
     * convert value in fromUnit to Base unit (BYTE)
     * @param value
     * @param fromUnit
     * @return
     */
    public static String convert(String value, CurrencyUnit fromUnit) {
    	String res = null;
    	
    	double number = Double.parseDouble(value);
    	res = fromUnit.convert(number);
    	
    	return res;
    }
    
    public String convertTo(String value, CurrencyUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    public static String convertTo(String value, CurrencyUnit fromUnit, CurrencyUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = fromUnit.getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    public static FormattedValue format(String value, CurrencyUnit origUnit) {
    	FormattedValue res = null;
    	if (origUnit != null)
    		res = new FormattedValue(value, origUnit);    	
    	return res;
    }
    
    public static FormattedValue formatTo(String value, CurrencyUnit toUnit) {
    	FormattedValue res = null;    	
    	double number = Double.parseDouble(value);
    	res = toUnit.format(number);    	
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
