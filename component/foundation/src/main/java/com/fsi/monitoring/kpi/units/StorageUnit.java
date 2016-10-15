package com.fsi.monitoring.kpi.units;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public enum StorageUnit implements IkrUnit { 
    BYTE     ( "Byte", 1L), 
    KILOBYTE ("KByte", 1L << 10), 
    MEGABYTE ("MByte", 1L << 20), 
    GIGABYTE ("GByte", 1L << 30), 
    TERABYTE ("TByte", 1L << 40), 
    PETABYTE ("PByte", 1L << 50), 
    EXABYTE  ("EByte", 1L << 60);
    
    private final String symbol; 
    private final long divider;  // divider of BASE unit
    
    StorageUnit(String name, long divider) {
        this.symbol = name; 
        this.divider = divider; 
    } 
    
    public static StorageUnit of(final double number) {
        final double n = number > 0 ? -number : number; 
        if (n > -(1L << 10)) { 
            return BYTE; 
        } else if (n > -(1L << 20)) { 
            return KILOBYTE; 
        } else if (n > -(1L << 30)) { 
            return MEGABYTE; 
        } else if (n > -(1L << 40)) { 
            return GIGABYTE; 
        } else if (n > -(1L << 50)) { 
            return TERABYTE; 
        } else if (n > -(1L << 60)) { 
            return PETABYTE; 
        } else { 
            return EXABYTE; 
        } 
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
    	return nf.format(resL);
    }    
    
    /**
     * convert value in fromUnit to Base unit (BYTE)
     * @param value
     * @param fromUnit
     * @return
     */
    public static String convert(String value, StorageUnit fromUnit) {
    	String res = null;
    	
    	double number = Double.parseDouble(value);
    	res = fromUnit.convert(number);
    	
    	return res;
    }
    
    public String convertTo(String value, StorageUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    public static String convertTo(String value, StorageUnit fromUnit, StorageUnit toUnit) {
    	double number = Double.parseDouble(value);
    	double ratio = fromUnit.getDivider().doubleValue()/toUnit.getDivider().doubleValue();
    	double res = number * ratio;
    	return nf.format(res);
    }
    
    public static FormattedValue format(String value, StorageUnit origUnit) {
    	double number = Double.parseDouble(value);
    	FormattedValue res = StorageUnit.of(number).format(number);
    	return res;
    }
    
    public static FormattedValue formatTo(String value, StorageUnit toUnit) {
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
