package com.fsi.monitoring.query.dao.impl;

import org.apache.log4j.Logger;

public class SybaseQueryDAO 
extends AbstractQueryDAO {

	private static final Logger LOG = Logger.getLogger(SybaseQueryDAO.class);		
	
	static {
		try {
			Class.forName("com.sybase.jdbc3.jdbc.SybDriver.class");
		} catch (ClassNotFoundException exc) {
			LOG.error("Sybase Driver not found.", exc);
		}
	}
}
