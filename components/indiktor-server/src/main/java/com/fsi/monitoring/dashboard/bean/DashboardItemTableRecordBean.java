package com.fsi.monitoring.dashboard.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.util.StyleBean;

public class DashboardItemTableRecordBean extends TableRecordBean implements Serializable {
	private static final long serialVersionUID = -4457335849646568460L;

	
	 private DashBoardSummaryComponent dashboard = null;
	 
	 public DashboardItemTableRecordBean(DashBoardSummaryComponent dashboard,
				String indentStyleClass,
				String rowStyleClass, 
				StyleBean styleBean, 
				String expandImage,
				String contractImage, 
				ArrayList<TableRecordBean> tableData,
				boolean isExpanded) {
		super(indentStyleClass, rowStyleClass, styleBean, expandImage, contractImage,
		tableData, isExpanded);
		this.dashboard = dashboard;
	}

	public DashboardItemTableRecordBean(DashBoardSummaryComponent dashboard,
					String indentStyleClass,
					String rowStyleClass, 
					StyleBean styleBean, 
					String spacerImage) {
		super(indentStyleClass, rowStyleClass, styleBean, spacerImage);
		this.dashboard = dashboard;
	}
	
	public DashBoardSummaryComponent getDashboard() {
		return dashboard;
	}

	@Override
	protected ArrayList<TableRecordBean> getSortedChildFilesRecords() {
		return childFilesRecords;
	}

}
