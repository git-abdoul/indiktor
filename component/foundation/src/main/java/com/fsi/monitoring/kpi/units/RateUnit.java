package com.fsi.monitoring.kpi.units;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum RateUnit implements IkrUnit { 
	RATIO ("", 1),
	PERCENT   ("%", 0.01);
    
    private final String symbol; 
    private final double divider;  // divider of BASE unit 
    
   RateUnit(String name, double divider) {
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
    	String value = nf.format(number / divider);
    	FormattedValue result = new FormattedValue(value, this);        
        return result;
    }
    
   public String convert(double number) {
    	double resD = number * divider;    	
    	return nf.format(resD);
    }    
    
    /**
     * convert value in fromUnit to Base unit (BYTE)
     * @param value
     * @param fromUnit
     * @return
     */
    public static String convert(String value, RateUnit fromUnit) {
    	double number = Double.parseDouble(value);
    	String res = fromUnit.convert(number);    	
    	return res;
    }
    
    public String convertTo(String value, RateUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    public static String convertTo(String value, RateUnit fromUnit, RateUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = fromUnit.getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    public static FormattedValue format(String value, RateUnit origUnit) {
    	double number = Double.parseDouble(value);
    	FormattedValue res = null;
    	if (origUnit != null && origUnit == PERCENT){
    		res = new FormattedValue(nf.format(number), PERCENT);
    	}
    	else {
    		res = PERCENT.format(number);   
    	}
    	return res;
    }
    
    public static FormattedValue formatTo(String value, RateUnit toUnit) {
    	double number = Double.parseDouble(value);
    	FormattedValue res = toUnit.format(number);    	
    	return res;
    }  
    
    public boolean isChartSupported() {
		return true;
	}
    
    private static java.text.NumberFormat nf = new DecimalFormat("#.##");    
    static { 
        nf.setGroupingUsed(false); 
        nf.setMinimumFractionDigits(0); 
        nf.setMaximumFractionDigits(2); 
    }
} 
