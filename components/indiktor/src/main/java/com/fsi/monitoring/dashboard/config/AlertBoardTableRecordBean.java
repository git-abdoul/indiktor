package com.fsi.monitoring.dashboard.config;

import java.util.ArrayList;
import java.util.Comparator;

import javax.faces.event.ValueChangeEvent;

import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;
import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.datamodel.ikrDefinition.expandableTable.IkrDefinitionTableRecordBean;
import com.fsi.monitoring.util.StyleBean;

public class AlertBoardTableRecordBean extends TableRecordBean {
	private static final long serialVersionUID = 4906879384161568832L;
	private static RecordComparator recordComparator = new RecordComparator();	
	
	
//	private AlertItemSelection oldAlertItem = null;
	private AlertItemSelection alertItem = null;
	private String actionToDo = null;
	private AlertDefinitionSelectionBean alertDefinitionBean;
	
	private boolean selected;
	
	public AlertBoardTableRecordBean(AlertItemSelection alertItem,
									String indentStyleClass,
									String rowStyleClass,
									StyleBean styleBean,
									String expandImage,
									String contractImage,
									ArrayList<TableRecordBean> tableData,
									boolean isExpanded) {
		super(indentStyleClass,rowStyleClass,styleBean,expandImage,contractImage,tableData,isExpanded);
		this.alertItem = alertItem;
		selected = false;
	}
	
	public AlertBoardTableRecordBean(AlertDefinitionSelectionBean alertDefinitionBean,
										String indentStyleClass,
										String rowStyleClass,
										StyleBean styleBean,
										String spacerImage
										) {
		super(indentStyleClass,rowStyleClass,styleBean,spacerImage);
		this.alertDefinitionBean = alertDefinitionBean;
		selected = false;
	}	
	
	@Override
	protected ArrayList<TableRecordBean> getSortedChildFilesRecords() {
//		if (childFilesRecords != null && childFilesRecords.size() > 1) {
//			Collections.sort(childFilesRecords, recordComparator);
//		}
		return childFilesRecords;
	}
	
	private static class RecordComparator implements Comparator<TableRecordBean> {
		public int compare(TableRecordBean o1, TableRecordBean o2) {
			IkrDefinitionTableRecordBean rb1 = (IkrDefinitionTableRecordBean)o1;
			IkrDefinitionTableRecordBean rb2 = (IkrDefinitionTableRecordBean)o2;
			return rb1.getName().compareTo(rb2.getName());
		}	
	}
	
	public void itemTitleChanged(ValueChangeEvent event) {
		String name = (String)event.getNewValue();
		alertItem.getAlertItem().setTitle(name);
//		oldAlertItem = new AlertItemSelection(new A);
//		String oldName = (String)event.getOldValue();
//		actionToDo = "CHANGE_ITEM_NAME";
//		setChanged();
//		notifyObservers(this);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public AlertItemSelection getAlertItem() {
		return alertItem;
	}

	public void setAlertItem(AlertItemSelection alertItem) {
		this.alertItem = alertItem;
	}

	public AlertDefinitionSelectionBean getAlertDefinitionBean() {
		return alertDefinitionBean;
	}	
	
	public void setAlertItemTitle(String title) {
		alertItem.getAlertItem().setTitle(title);
	}
	
	public String getAlertItemTitle() {
		return alertItem.getAlertItem().getTitle();
	}
	
	public String getComputePanelStack() {
    	String res = "NO_ALERT";
    	
    	if (alertDefinitionBean != null)
    		res = "ALERT";
    	
    	return res;
    }
	
	public String getComputePanelStackbis() {
    	String res = "ITEMS";
    	
    	if (alertDefinitionBean != null)
    		res = "ALERTS";
    	
    	return res;
    }

//	@Override
//	public int hashCode() {
//		int code = 0;
//		if (alertDefinitionBean != null)
//			code = alertDefinitionBean.hashCode();
//		else
//			code = alertItem.hashCode();
//		return code;
//	}

//	@Override
//	public boolean equals(Object obj) {
//		boolean ret = false;
//		if (alertDefinitionBean != null && ((AlertBoardTableRecordBean)obj).getAlertDefinitionBean() != null)
//			ret = alertDefinitionBean.equals(((AlertBoardTableRecordBean)obj).getAlertDefinitionBean());
//		else if (alertItem != null && ((AlertBoardTableRecordBean)obj).getAlertItem()!= null)
//			ret = (alertItem.getAlertItem().getTitle().compareTo((((AlertBoardTableRecordBean)obj).getAlertItem()).getAlertItem().getTitle()) == 0);
//		return ret;
//	}
	
	
	
}
