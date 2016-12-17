package com.fsi.monitoring.report.dao;

import java.util.List;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.report.ReportConfig;
import com.fsi.monitoring.report.ReportType;

public interface ReportDAO {

	List<ReportType> getReportTypes()
	throws PersistenceException;
	
	List<ReportConfig> getReportConfigs(long reportTypeId)
	throws PersistenceException;	
	
}
