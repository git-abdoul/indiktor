package com.fsi.monitoring.dashboard.bean;


import java.util.ArrayList;

import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardComponent;
import com.fsi.monitoring.util.StyleBean;


public class DashboardTableRecordBean extends TableRecordBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8503018038529957146L;
	private AlertBoardComponent alertComponentBean;

	public DashboardTableRecordBean(AlertBoardComponent alertComponentBean,
									String indentStyleClass,
									String rowStyleClass, 
									StyleBean styleBean, 
									String expandImage,
									String contractImage, 
									ArrayList<TableRecordBean> tableData,
									boolean isExpanded) {
		super(indentStyleClass, rowStyleClass, styleBean, expandImage, contractImage,
				tableData, isExpanded);
		this.alertComponentBean = alertComponentBean;
	}

	public DashboardTableRecordBean(AlertBoardComponent alertComponentBean,
									String indentStyleClass,
									String rowStyleClass, 
									StyleBean styleBean, 
									String spacerImage) {
		super(indentStyleClass, rowStyleClass, styleBean, spacerImage);
		this.alertComponentBean = alertComponentBean;
	}

	public AlertBoardComponent getAlertComponentBean() {
		return alertComponentBean;
	}
//
//	public void setAlert(AlertBoardBean alert) {
//		this.alert = alert;
//	}

	@Override
	protected ArrayList<TableRecordBean> getSortedChildFilesRecords() {
		return childFilesRecords;
	}
}