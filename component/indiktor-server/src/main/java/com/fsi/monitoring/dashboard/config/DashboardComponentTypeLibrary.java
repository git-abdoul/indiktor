package com.fsi.monitoring.dashboard.config;

import java.util.HashMap;
import java.util.Map;

public class DashboardComponentTypeLibrary {
	private static Map<String, String> libraries = new HashMap<String, String>();
	
	public static String  defaultComponentType = "navigationBoard";
	
	static  {
		libraries.put("alertBoard", "Alert Board");
		libraries.put("alertBoardGrid", "Alert Board Grid");
		libraries.put("batchBoard", "Batch Board");
		libraries.put("definitionChart", "Chart");
		libraries.put("infoBoard", "Info Board");
//		libraries.put("multiColumnInfoBoard", "Multicolumn Info Board");
		libraries.put("navigationBoard", "Navigation Board");
//		libraries.put("thread", "Thread");
	}
	
	public static String getComponentTypeLabel(String type) {
		return libraries.get(type);
	}
	
	public static Map<String, String> getLibrary() {
		return libraries;
	}
}
