package com.fsi.monitoring.report;

import java.util.List;

import com.fsi.fwk.exception.persistence.PersistenceException;

public interface ReportPM {
	
	List<ReportType> getReportTypes() 
	throws PersistenceException;
	
	List<ReportConfig> getReportConfigs(long reportTypeId) 
	throws PersistenceException;
}
