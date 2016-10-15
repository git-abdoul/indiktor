package com.fsi.monitoring.kpi.units;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum ThroughputUnit implements IkrUnit { 
	TRADES_PER_SEC     ( "trades/sec", 1),
    TRADES_PER_HOUR     ( "trades/hour", 1), 
    TRADES_PER_MIN     ( "trades/min", 1),
    MESSAGES_PER_SEC     ( "messages/sec", 1), 
    MESSAGES_PER_HOUR     ( "messages/hour", 1), 
    MESSAGES_PER_MIN     ( "messages/min", 1), 
    DOCUMENTS_PER_SEC     ( "documents/sec", 1), 
    DOCUMENTS_PER_HOUR     ( "documents/hour", 1), 
    DOCUMENTS_PER_MIN     ( "documents/min", 1), 
    TRANSFERS_PER_SEC     ( "transfers/sec", 1), 
    TRANSFERS_PER_HOUR     ( "transfers/hour", 1), 
    TRANSFERS_PER_MIN     ( "transfers/min", 1), 
    PAYMENTS_PER_SEC     ( "payments/sec", 1), 
    PAYMENTS_PER_HOUR     ( "payments/hour", 1), 
    PAYMENTS_PER_MIN     ( "payments/min", 1), 
    POSTINGS_PER_SEC     ( "postings/sec", 1), 
    POSTINGS_PER_HOUR     ( "postings/hour", 1), 
    POSTINGS_PER_MIN     ( "postings/min", 1), 
    CRES_PER_SEC     ( "cres/hour", 1),
    CRES_PER_HOUR     ( "cres/hour", 1), 
    CRES_PER_MIN     ( "cres/min", 1); 
    
    private final String symbol; 
    private final long divider;  // divider of BASE unit 
    
    ThroughputUnit(String name, long divider) {
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
		res.add(MetricCompute.MM100);
		return res;
	}      
    
    public FormattedValue format(double number) {
    	FormattedValue result = null;
    	
    	String value = nf.format(number / divider);
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
    	
    	String res = String.valueOf(resL);
    	return res;
    }    
    
    /**
     * convert value in fromUnit to Base unit (BYTE)
     * @param value
     * @param fromUnit
     * @return
     */
    public static String convert(String value, ThroughputUnit fromUnit) {
    	String res = null;
    	
    	double number = Double.parseDouble(value);
    	res = fromUnit.convert(number);
    	
    	return res;
    }
    
    public String convertTo(String value, ThroughputUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return String.valueOf(res);
    }
    
    public static String convertTo(String value, ThroughputUnit fromUnit, ThroughputUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = fromUnit.getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return String.valueOf(res);
    }
    
    public static FormattedValue formatTo(String value, ThroughputUnit toUnit) {
    	FormattedValue res = null;
    	
    	double number = Double.parseDouble(value);
    	res = toUnit.format(number);
    	
    	return res;
    }   
    
    public static FormattedValue format(String value, ThroughputUnit origUnit) {
    	FormattedValue res = null;
    	if (origUnit != null)
    		res = new FormattedValue(value, origUnit);    	
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
