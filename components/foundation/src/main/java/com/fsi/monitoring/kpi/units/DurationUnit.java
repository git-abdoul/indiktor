package com.fsi.monitoring.kpi.units;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum DurationUnit implements IkrUnit { 
	MILLISECOND ("msec", 1),
    SECOND   ("sec", 1000), 
    MINUTE 	 ("min", 60000),
    HOUR     ("h", 3600000);
    
    private final String symbol; 
    private final long divider;  // divider of BASE unit 
    
    DurationUnit(String name, long divider) {
        this.symbol = name; 
        this.divider = divider; 
    }    
    
	public static Collection<MetricCompute> getSupportedComputes() {
		Collection<MetricCompute> res = new ArrayList<MetricCompute>();
		res.add(MetricCompute.MM20);
		res.add(MetricCompute.MM50);
		res.add(MetricCompute.MM100);
		return res;
	} 
    
    public static DurationUnit of(final double number) {
        if (number<1000) { 
            return MILLISECOND; 
        } else if (number>1000 && number<60000) { 
            return SECOND; 
        } else if (number>60000 && number<3600000) { 
            return MINUTE; 
        } else { 
            return HOUR; 
        }
    }
    
    public String getSymbol() {
    	return symbol;
    }
    
    public Number getDivider() {
		return divider;
	}
    
    public FormattedValue format(double number) {
    	String value = nf.format(number / divider);
    	FormattedValue result = new FormattedValue(value, this);        
        return result;
    }
    
    /**
     * Convert number in Base unit (MILLISECOND)
     * @param number
     * @return
     */
    public String convert(double number) {
    	long resL = Math.round(number * divider);    	
    	return nf.format(resL);
    }   
    
    public String convertTo(String value, DurationUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    public static String convertTo(String value, DurationUnit fromUnit, DurationUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = fromUnit.getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    /**
     * convert value in fromUnit to Base unit (MILLISECOND)
     * @param value
     * @param fromUnit
     * @return
     */
    public static String convert(String value, DurationUnit fromUnit) {
    	double number = Double.parseDouble(value);
    	String res = fromUnit.convert(number);
    	return res;
    }
    
    public static FormattedValue format(String value, DurationUnit origUnit) {
    	double number = Double.parseDouble(value);
    	FormattedValue res = DurationUnit.of(number).format(number);
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
    
    public static void main(String[] args) {
		
	}
} 
