package com.fsi.monitoring.global;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.global.dao.GlobalDAO;

public class IdGenerator {

	protected final static Logger LOG = Logger.getLogger(IdGenerator.class);	
	
	private GlobalDAO globalDAO;
	private String objectType;
	
	private int batchSize = 100;
	  
	private long current = 0;
	private long max;

	public void setBatchSize(int batchSize) {
	    this.batchSize = batchSize;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	public void setGlobalDAO(GlobalDAO globalDAO) {
		this.globalDAO = globalDAO;
	}
	  
	public long getNextId(int granularity) {
		long res = current;		
		if (granularity> max-current) {
			// the amount requested is more than the amount available
			try {
				int request = Math.max(granularity,batchSize);
				res = globalDAO.getNextId(request, objectType);
				max = res + request;
		    } catch(PersistenceException exc) {
	    		LOG.fatal("Impossible to generate ID for " + objectType, exc);
	    	}
			
		}	
		current = res + granularity;
		return res;
	}
	
	public void updateId(long id) {
		try {
			globalDAO.updateId(id, objectType);
	    } catch(PersistenceException exc) {
    		LOG.fatal("Impossible to update ID for " + objectType, exc);
    	}
	}
	
}
