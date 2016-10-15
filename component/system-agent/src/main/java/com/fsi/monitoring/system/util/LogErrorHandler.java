package com.fsi.monitoring.system.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class LogErrorHandler {
	private static List<String> errors = new ArrayList<String>();
	
	public static synchronized void displayError(String error, Logger log) {
		if (!errors.contains(error)) {
			errors.add(error);
			log.error(error);
		}
	}
}
