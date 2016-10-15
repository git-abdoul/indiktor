package com.fsi.monitoring.indiktor.dao.impl;

import java.util.Date;

import org.apache.log4j.Logger;



public class SybaseMonitorDAO 
extends AbstractMonitorDAO {

	private static final Logger LOG = Logger.getLogger(SybaseMonitorDAO.class);		
	
	static {
		try {
			Class.forName("com.sybase.jdbc3.jdbc.SybDriver");
		} catch (ClassNotFoundException exc) {
			LOG.error("Sybase Driver not found.", exc);
		}
	}

	public void cleanIkrValues(Date beforeDate) throws Exception {
		
	}
	
	
}