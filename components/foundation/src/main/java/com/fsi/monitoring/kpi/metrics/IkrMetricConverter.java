package com.fsi.monitoring.kpi.metrics;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class IkrMetricConverter {
	private static final DecimalFormat twoDForm = new DecimalFormat("#.##");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	/**
	 * 
	 * @return a string array size 2 : pos 0: formatted value, pos 1 : unit
	 */
	public static String[] convert(String value, IkrCategory ikrCategory) {
		String[] res = null;
//		if(value == null || value.length()>0) {
//			res = new String[2];
//			res[0] = "No Value";
//			res[1] = "";
//			
//			return res;
//		}
//		
//		IkrUnitType unitType = ikrCategory.getUnitType();
//		
//		if (IkrUnitType.RATIO == unitType) {
//			double valueDB = Double.parseDouble(value);
//			valueDB = valueDB * 100;
//			res = new String[2];
//			res[0] = twoDForm.format(valueDB);
//			res[1] = "%";
//			
//			return res;
//		} 
//	
//		if (IkrUnitType.BYTE == unitType) {
//			long valueL = Long.parseLong(value);
//			res = StorageUnit.of(valueL).format(valueL);
//			
//			res[0] = twoDForm.format(Double.parseDouble(res[0]));
//			
//			return res;
//		}
//		
//		if (IkrUnitType.DATETIME == unitType) {
//			res = new String[2];
//			long time = Long.parseLong(value);
//			res[0] = dateFormat.format(new Date(time));
//			res[1] = "dd/MM/yyyy HH:mm:ss";
//			
//			return res;
//		}	
//		
//		if (IkrUnitType.BOOLEAN == unitType) {
//			res = new String[2];
//			res[0] = value;
//			res[1] = "0/1";
//			
//			return res;
//		}
//
//		// No Unit found
//		res = new String[2];
//		res[0] = value;
//		res[1] = "";
		
		return res;

		
//		else if ("kbytes".equalsIgnoreCase(unitType)) {
//				res[0] = "";
//				if(value != null && value.length()>0) {	
//					double valueDB = Double.parseDouble(value);
//					valueDB = valueDB / 1000;
//					res[0] = twoDForm.format(valueDB);
//				}
//				res[1] = "Mb";
//			}
//			else if ("Mbytes".equalsIgnoreCase(unitType)) {
//				res[0] = "";
//				if(value != null && value.length()>0) {	
//					double valueDB = Double.parseDouble(value);
//					res[0] = twoDForm.format(valueDB);
//				}
//				res[1] = "Mb";
//			}
//			else if ("ms".equalsIgnoreCase(unitType)) {
//				res[0] = "";
//				if(value != null && value.length()>0) {	
//					double valueDB = Double.parseDouble(value);
//					valueDB = valueDB / 1000;
//					res[0] = twoDForm.format(valueDB);
//				}
//				res[1] = "s";
//			}
//			else if ("s".equalsIgnoreCase(unitType)) {
//				res[0] = "";
//				if(value != null && value.length()>0)
//					res[0] = value;
//				res[1] = "s";
//			}
//			else if (unitType==null || unitType.length()==0) {
//				if ("DOUBLE".equalsIgnoreCase(ikrCategory.getValueType().name())) {
//					res[0] = "";
//					if(value != null && value.length()>0) {	
//						double valueDB = Double.parseDouble(value);
//						res[0] = twoDForm.format(valueDB);;
//					}
//					res[1] = "";
//				} else {
//					res[0] = value;
//					res[1] = "";
//				}
//			}			
//		else if ("string".equalsIgnoreCase(metricType)) {
//			res[0] = "";
//			if(value != null && value.length()>0) 
//				res[0] = value;
//			res[1] = "";
//		}
//		else if ("throughput".equalsIgnoreCase(metricType)) {
//			res[0] = "";
//			if(value != null && value.length()>0) {	
//				double valueDB = Double.parseDouble(value);
//				res[0] = twoDForm.format(valueDB);
//			}
//			res[1] = unitType;
//		} 
//		else if ("datetime".equalsIgnoreCase(metricType)) {
//			res[0] = "";
//			if(value != null && value.length()>0) {	
//				long time = Long.parseLong(value);
//				res[0] = dateFormat.format(new Date(time));
//			}
//			res[1] = "";
//		}
//		else if ("boolean".equalsIgnoreCase(metricType)) {
//			res[0] = "";
//			if(value != null && value.length()>0)
//				res[0] = value;
//			res[1] = "";
//		}
//		else if ("bit".equalsIgnoreCase(metricType)) {
//			res[0] = "0";
//			if(value != null && value.length()>0) {
//				res[0] = value;
//			}
//			res[1] = "";
//		}
//		else {
//			res[0] = "";
//			if(value != null && value.length()>0) 
//				res[0] = value;
//			res[1] = "";
//		}		
	}
	

	
	public static String reverse(String value, IkrCategory ikrCategory) {
		String res = "";
//		String metricType = ikrCategory.getMetricType();
//		String unitType = ikrCategory.getUnitType();
//		if ("ratio".equalsIgnoreCase(metricType)) {
//			res = "0";
//			if(value != null && value.length()>0) {		
//				double valueDB = Double.parseDouble(value);
//				valueDB = valueDB / 100;
//				res = twoDForm.format(valueDB);
//			}
//		} 
//		else if ("count".equalsIgnoreCase(metricType)) {
//			res = "0";
//			if(value != null && value.length()>0)
//				res = value;
//		}
//		else if ("size".equalsIgnoreCase(metricType)) {
//			if ("bytes".equalsIgnoreCase(unitType)) {
//				res = "0";
//				if(value != null && value.length()>0) {	
//					double valueDB = Double.parseDouble(value);
//					valueDB = valueDB * 1000000;
//					res = twoDForm.format(valueDB);
//				}
//			}
//			else if ("kbytes".equalsIgnoreCase(unitType)) {
//				res = "0";
//				if(value != null && value.length()>0) {	
//					double valueDB = Double.parseDouble(value);
//					valueDB = valueDB * 1000;
//					res = twoDForm.format(valueDB);
//				}
//			}
//			else if ("Mbytes".equalsIgnoreCase(unitType)) {
//				res = "0";
//				if(value != null && value.length()>0) {	
//					double valueDB = Double.parseDouble(value);
//					res = twoDForm.format(valueDB);
//				}
//			}
//			else if ("ms".equalsIgnoreCase(unitType)) {
//				res = "0";
//				if(value != null && value.length()>0) {	
//					double valueDB = Double.parseDouble(value);
//					valueDB = valueDB * 1000;
//					res = twoDForm.format(valueDB);
//				}
//			}
//			else if ("s".equalsIgnoreCase(unitType)) {
//				res = "0";
//				if(value != null && value.length()>0)
//					res = value;
//			}
//			else if (unitType==null || unitType.length()==0) {
//				if ("DOUBLE".equalsIgnoreCase(ikrCategory.getValueType().name())) {
//					res = "0";
//					if(value != null && value.length()>0) {	
//						double valueDB = Double.parseDouble(value);
//						res = twoDForm.format(valueDB);;
//					}
//				} else {
//					res = value;
//				}
//			}			
//		}
//		else if ("string".equalsIgnoreCase(metricType)) {
//			res = "";
//			if(value != null && value.length()>0) 
//				res = value;
//		}
//		else if ("throughput".equalsIgnoreCase(metricType)) {
//			res = "0";
//			if(value != null && value.length()>0) {	
//				double valueDB = Double.parseDouble(value);
//				res = twoDForm.format(valueDB);
//			}
//		} 
//		else if ("datetime".equalsIgnoreCase(metricType)) {
//			res = "";
//			if(value != null && value.length()>0) {	
//				long time = Long.parseLong(value);
//				res = dateFormat.format(new Date(time));
//			}
//		}
//		else if ("boolean".equalsIgnoreCase(metricType)) {
//			res = "false";
//			if(value != null && value.length()>0)
//				res = value;
//		}
//		else {
//			res = "";
//			if(value != null && value.length()>0) 
//				res = value;
//		}		
		return res;
	}
	
	public static Date convertStringToDate(String source) 
	throws ParseException {
		return dateFormat.parse(source);
	}
}
