package com.fsi.fwk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static String Default_Pattern = "yyyy-MM-dd HH:mm:ss";
	
	public static String getDate(Date date, String pattern) {
		String res = null;		
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern(pattern);
		res = df.format(date);

		return res;
	}
	
	public static String getDate(Date date) {
		return getDate(date, Default_Pattern);
	}
	
	public static String getDateDefaultPattern() {
		return Default_Pattern;
	}
	
	public static String getOracleSQLDateDefaultPattern() {
		return "yyyy-mm-dd HH24:mi:SS";
	}
}
