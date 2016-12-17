package com.fsi.monitoring.histo;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.histo.dao.HistoDAO;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public class HistoPMFactory 
implements HistoPM {
	
	private HistoDAO histoDAO;
	
	public void setHistoDAO(HistoDAO histoDAO) {
		this.histoDAO = histoDAO;
	}	
	
	public Map<Long, List<IkrValue>> getIkrValues(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException {
		return histoDAO.getIkrValues(ikrDefinitionIds, fromDate, toDate);
	}

	public Collection<Long> getIkrValueIds(List<Long> ikrDefinitionIds,Date fromDate, Date toDate) 
	throws PersistenceException {
		return histoDAO.getIkrValueIds(ikrDefinitionIds, fromDate, toDate);
	}
	
	public Collection<IkrValue> getIkrValues(List<Long> ikrDefinitionIds,Date fromDate, Date toDate, int prefetch) 
	throws PersistenceException {
		return histoDAO.getIkrValues(ikrDefinitionIds, fromDate, toDate, prefetch);
	}
	
	public List<Long> getLastIkrValuesId(long ikrDefinitionId, int nbOfValues) throws PersistenceException {
		return histoDAO.getLastIkrValuesId(ikrDefinitionId, nbOfValues);
	}

}
