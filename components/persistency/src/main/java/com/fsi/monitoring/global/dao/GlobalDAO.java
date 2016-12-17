package com.fsi.monitoring.global.dao;

import com.fsi.fwk.exception.persistence.PersistenceException;

public interface GlobalDAO {
	
	long getNextId(int batchSize, String objectType)
	throws PersistenceException;
	
	void updateId(long id, String objectType) throws PersistenceException;

}
