package com.fsi.monitoring.report.tree.userObject;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.fsi.monitoring.report.ReportConfig;
import com.fsi.monitoring.report.ReportGenerationBean;
import com.fsi.monitoring.report.ReportType;
import com.fsi.monitoring.util.FacesUtils;

public class ReportConfigObject
extends AbstractObject {
	
	private ReportType reportType;
	private ReportConfig reportConfig;

	public ReportConfigObject(ReportType reportType,
							  ReportConfig reportConfig,
							  DefaultMutableTreeNode wrapper) {
		super(wrapper);
		this.reportConfig = reportConfig;
		this.reportType = reportType;
	}	

	@Override
	public String getText() {
		return reportConfig.getName();
	}
	
	@Override
	public void selectNodeObject(ActionEvent action) {
		ReportGenerationBean reportGenerationBean = (ReportGenerationBean)FacesUtils.getManagedBean("reportGenerationBean");
		reportGenerationBean.setReportConfig(reportConfig);
		reportGenerationBean.setReportType(reportType);
		reportGenerationBean.setActivateStack();
	}	
}
