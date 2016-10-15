package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.AlertBoardType.AlertItemType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;

public class AlertItemSelection {
	
//	private boolean selected = false;
	private boolean rendered;
	private AlertItemType itemType;
	
	private Set<AlertDefinitionSelectionBean> alertBeansSav;
	private Set<AlertDefinitionSelectionBean> alertBeans;
	
	public AlertItemSelection(AlertItemType itemType) {
		this.itemType = itemType;
//		this.selected = false;
		alertBeans = new HashSet<AlertDefinitionSelectionBean>();
	}
	
	public AlertItemType getAlertItem() {
		return itemType;
	}
	
//	public boolean isSelected() {
//		return selected;
//	}
	
//	public void setSelected(boolean selected) {}
//	
//	public void updateSelected(boolean selected) {
//		this.selected = selected;
//	}
//	
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	
	public boolean isRendered() {
		return rendered;
	}
//	
//	public void onChangeSelected(ValueChangeEvent evnt) {	
//		this.selected = (Boolean)evnt.getNewValue();
//		for (AlertDefinitionSelectionBean item : alertBeans) {
//			item.updateSelected(selected);
//		}
//	}		
//	
//	public void saveAlertList() {
//		alertBeansSav = new HashSet<AlertDefinitionSelectionBean>(alertBeans);
//	}
//	
	public Collection<AlertDefinitionSelectionBean> getAlertBeans() {
		return alertBeans;
	}
	
	public void add(AlertDefinitionSelectionBean alertBean) {
		alertBeans.add(alertBean);
	}
	
	public boolean contains(AlertDefinitionSelectionBean alertBean) {
		return alertBeans.contains(alertBean);
	}
	
//	public void removeAlerts(ActionEvent action) {
//		Iterator<AlertDefinitionSelectionBean> itemIt = alertBeans.iterator();
//		
//		while (itemIt.hasNext()) {
//			AlertDefinitionSelectionBean item = itemIt.next();
//			if (item.isSelected()) {
//				itemIt.remove();
//			}		
//		}
//		selected = false;
//	}
	
//	public void removeAlertsForHeaderButton(ActionEvent action) {
//	Iterator<AlertDefinitionSelectionBean> itemIt = alertBeans.iterator();
//	
//	while (itemIt.hasNext()) {
//		AlertDefinitionSelectionBean item = itemIt.next();
//		if (item.isSelected()) {
//			itemIt.remove();
//		}		
//	}
//	selected = false;
//}
//	
//	public void removeAlerts(ActionEvent action) {
//		AlertDefinitionSelectionBean selectedAlertInItem = (AlertDefinitionSelectionBean)action.getComponent().getAttributes().get("item");
//		
//		Iterator<AlertDefinitionSelectionBean> itemIt = alertBeans.iterator();
//		
//		while (itemIt.hasNext()) {
//			AlertDefinitionSelectionBean item = itemIt.next();
//			if (selectedAlertInItem == item) {
//				itemIt.remove();
//			}		
//		}
//	}
//	
//	public void cancel(ActionEvent action) {
//		getSelectedAlertItem().getAlertItem().setTitle(tempTitle);
//		alertBeans = alertBeansSav;
//	}
//	
//	public void save(ActionEvent action) {
//		if (alertBeans == null || alertBeans.isEmpty()) {
//			errorAlertListVisible = true;
////			error = true;
////			message = "Alert list cannot be empty";
////			setAction("alertItemBoardConfig");
//			return;
//		} else {
//			setAction("alertBoardConfig");
//		}
//		errorAlertListVisible = false;
//		setAlertBoardItemConfigVisible(false);
//	}		
}