package com.fsi.monitoring.user.dao.impl;

import org.apache.log4j.Logger;


public class SybaseUserDAO 
extends AbstractUserDAO {

	private static final Logger LOG = Logger.getLogger(SybaseUserDAO.class);		
	
	static {
		try {
			Class.forName("com.sybase.jdbc3.jdbc.SybDriver.class");
		} catch (ClassNotFoundException exc) {
			LOG.error("Sybase Driver not found.", exc);
		}
	}
}
