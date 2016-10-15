package com.fsi.monitoring.indiktor.dao.impl;

import org.apache.log4j.Logger;

public class SybaseDataModelDAO 
extends AbstractDataModelDAO {

	private static final Logger LOG = Logger.getLogger(SybaseDataModelDAO.class);		
	
	static {
		try {
			Class.forName("com.sybase.jdbc3.jdbc.SybDriver.class");
		} catch (ClassNotFoundException exc) {
			LOG.error("Sybase Driver not found.", exc);
		}
	}
}
