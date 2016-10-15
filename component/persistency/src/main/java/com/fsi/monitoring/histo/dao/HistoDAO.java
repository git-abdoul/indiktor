package com.fsi.monitoring.histo.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.kpi.metrics.IkrValue;


public interface HistoDAO {
	public Map<Long, List<IkrValue>> getIkrValues(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException;
	public Collection<Long> getIkrValueIds(List<Long> ikrDefinitionIds, Date fromDate, Date toDate) throws PersistenceException;
	public Collection<IkrValue> getIkrValues(List<Long> ikrDefinitionIds, Date fromDate, Date toDate, int prefetch) throws PersistenceException;
	public List<Long> getLastIkrValuesId(long ikrDefinitionId, int nbOfValues) throws PersistenceException;
}
