package com.fsi.monitoring.report;

import java.util.List;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.report.dao.ReportDAO;

public class ReportPMFactory 
implements ReportPM {
	
	private ReportDAO reportDAO;
	
	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}

	public List<ReportType> getReportTypes()
	throws PersistenceException {
		return reportDAO.getReportTypes();
	}

	public List<ReportConfig> getReportConfigs(long reportTypeId)
	throws PersistenceException {
		return reportDAO.getReportConfigs(reportTypeId);
	}

}
