package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.AlertBoardType;
import generated.dashboard.config.schema.AlertBoardType.AlertItemType;
import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.bean.AlertBean;
import com.fsi.monitoring.alert.composite.AlertLeaf;
import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;
import com.fsi.monitoring.alert.config.AllAlertDefinitionBean;
import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardComponent;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.FacesUtils;
import com.lowagie.text.pdf.AcroFields.Item;

public class AlertBoardConfigBean 
extends DashBoardConfigBean {

	private static final long serialVersionUID = 2767684910418822131L;

	private static final Logger logger = Logger.getLogger(AlertBoardConfigBean.class);	
	
	private AlertBoardType alertBoard;
	
	private List<AlertItemSelection> alertItems;
	private AlertItemType alertItemToAdd;
	private AlertItemSelection selectedAlertItem;
	
	private boolean alertBoardItemConfigVisible = false;
	private boolean addItemVisible = false;
	private boolean errorAlertListVisible = false;
	private boolean errorItemListVisible = false;
	private boolean errorAlertBoardTitleVisible = false;
	private boolean errorItemTitleVisible = false;
	
	private boolean titleError;
	
	private boolean alertInListTest = false;
	
	private String tempTitle;
	private String newTitle = "";
	
	private boolean selected = false;
	private boolean rendered;
	private boolean update = false;
	
	private AlertBoardTableRecordManager tableRecordManager;
//	private boolean rendererNewItemTitle = false;
	
	public void init(String env,
					 String type,
					 String title,
					 AlertBoardComponent component) {
		super.init(env, type, title);
		AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		allAlertDefinitionBean.init(null);
		ComponentXmlManager componentXmlManager = (ComponentXmlManager)FacesUtils.getManagedBean("xmlComponentManager");
		
		DashBoard dashBoard = componentXmlManager.getDashBoardSchema(env, type, title);
		
		List<AlertBoardType> alertBoards = dashBoard.getAlertBoards().getAlertBoard();
		for (AlertBoardType alertBoard : alertBoards) {
			if (alertBoard.getId().equals(component.getComponentId())) {
				this.alertBoard = alertBoard;
			}
		}
		
		alertItems = new ArrayList<AlertItemSelection>();
		alertBoard.getAlertItem().clear();
		
		try {	
			ObjectFactory objFactory = new ObjectFactory();
			
			alertItemToAdd = objFactory.createAlertBoardTypeAlertItemType();
			
			for (AlertLeaf alertLeaf : component.getAlertLeafs()) {
				AlertItemType alertItem = objFactory.createAlertBoardTypeAlertItemType();
				alertItem.setTitle(alertLeaf.getTitle());
				alertItem.setType(alertLeaf.getType());
			
				AlertItemSelection alertItemSelection = new AlertItemSelection(alertItem);
	//			alertItemSelection.setRendered(false);
				
				Collection<AlertBean> alertBeans = alertLeaf.getAllAlertBeans();
				
				for (AlertBean alertBean : alertBeans) {
					AlertDefinitionSelectionBean selectionBean =
						new AlertDefinitionSelectionBean(alertBean.getAlertDefinitionBean());
					alertItemSelection.getAlertBeans().add(selectionBean);
				}

				alertItems.add(alertItemSelection);
			}
			
			tableRecordManager = new AlertBoardTableRecordManager(alertItems);
			tableRecordManager.init();
			
			rendered = false;
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public void init(String env,
					 String type,
					 String title) {
	
		super.init(env, type, title);
		try {
			AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
			allAlertDefinitionBean.init(null);
			ObjectFactory objFactory = new ObjectFactory();

			alertItemToAdd = objFactory.createAlertBoardTypeAlertItemType();
			
			alertBoard = objFactory.createAlertBoardType();
	
			alertItems = new ArrayList<AlertItemSelection>();
			
			rendered = true;
			
			tableRecordManager = new AlertBoardTableRecordManager(alertItems);
			tableRecordManager.init();
		} catch (Exception exc) {
			logger.error(exc);
		}		
	}
	
	public AlertBoardTableRecordManager getTableRecordManager() {
		return tableRecordManager;
	}

	public void openAddItemPanel(ActionEvent action) {
		addItemVisible = true;
	}
	
	public boolean isAddItemVisible() {
		return addItemVisible;
	}
	
	public void closeAddAlertPanel(ActionEvent action) {
		errorAlertListVisible = false;
		errorItemListVisible = false;
		errorAlertBoardTitleVisible = false;
		alertBoardItemConfigVisible = false;
		DashboardSelectMainConfigBean dashboardSelectMainConfigBean = (DashboardSelectMainConfigBean)FacesUtils.getManagedBean("dashboardSelectMainConfigBean");
		dashboardSelectMainConfigBean.setAddButtonVisible(true);
	}

//	public String getNewTitle() {
//		return newTitle;
//	}
//	
//	public void setNewTitle(String newTitle) {
//		this.newTitle = newTitle;
//	}
//
//	public boolean isUpdate() {
//		return update;
//	}

	public AlertBoardType getAlertBoard() {
		return alertBoard;
	}
	
	public AlertItemType getAlertItemToAdd() {
		return alertItemToAdd;
	}
	
	public AlertItemSelection getSelectedAlertItem() {
		return selectedAlertItem;
	}	

	public Collection<AlertItemSelection> getAlertItems() {
		return alertItems;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {}
	
	public boolean isAlertBoardItemConfigVisible() {
		return alertBoardItemConfigVisible;
	}

	public void setAlertBoardItemConfigVisible(boolean alertBoardItemConfigVisible) {
		this.alertBoardItemConfigVisible = alertBoardItemConfigVisible;
	}

//	public boolean isAddAlertPanelVisible() {
//		return addAlertPanelVisible;
//	}
//
//	public void setAddAlertPanelVisible(boolean addAlertPanelVisible) {
//		this.addAlertPanelVisible = addAlertPanelVisible;
//	}
	
	public void setAllErrorFalse(){
		errorAlertBoardTitleVisible = false;
		errorAlertListVisible = false;
		errorItemListVisible = false;
		errorItemTitleVisible = false;
	}

	public boolean isErrorAlertBoardTitleVisible() {
		return errorAlertBoardTitleVisible;
	}

	public void setErrorAlertBoardTitleVisible(boolean errorAlertBoardTitleVisible) {
		this.errorAlertBoardTitleVisible = errorAlertBoardTitleVisible;
	}

	public boolean isErrorItemTitleVisible() {
		return errorItemTitleVisible;
	}

	public void setErrorItemTitleVisible(boolean errorItemTitleVisible) {
		this.errorItemTitleVisible = errorItemTitleVisible;
	}

	public boolean isErrorItemListVisible() {
		return errorItemListVisible;
	}

	public void setErrorItemListVisible(boolean errorItemListVisible) {
		this.errorItemListVisible = errorItemListVisible;
	}

	public boolean isErrorAlertListVisible() {
		return errorAlertListVisible;
	}

	public void setErrorAlertListVisible(boolean errorAlertListVisible) {
		this.errorAlertListVisible = errorAlertListVisible;
	}

//	public void onChangeSelected(ValueChangeEvent evnt) {	
//		this.selected = (Boolean)evnt.getNewValue();;
//		
//		for (AlertItemSelection item : alertItems) {
//			item.updateSelected(selected);
//		}
//	}	
	
	public boolean isRendered() {
		return rendered;
	}
	
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}	
	
//	public void addItem(ActionEvent action) {
//		try {
//			
//			if (alertItemToAdd.getTitle() == null || alertItemToAdd.getTitle().length() == 0) {
//				errorAlertItemTitleVisible = true;
//				return;
//			}
//			errorAlertItemTitleVisible = false;
//			// add the new Item
//			AlertItemSelection item = new AlertItemSelection(alertItemToAdd);
//			item.setRendered(true);
//			alertItems.add(item);
//
//			// reset the future item to add
//			ObjectFactory objFactory = new ObjectFactory();
//			alertItemToAdd = objFactory.createAlertBoardTypeAlertItemType();
//		} catch (Exception exc) {
//			logger.error(exc);
//		}
//	}
	
	public void addItem(ActionEvent action) {
		tableRecordManager.addItem();
	}
	
	public void removeItem(ActionEvent action) {
		AlertBoardTableRecordBean recordToDelete = (AlertBoardTableRecordBean)action.getComponent().getAttributes().get("item");
		tableRecordManager.removeItems(recordToDelete);
	}	
	
	public void removeAlert(ActionEvent action) {
		AlertBoardTableRecordBean recordToDelete = (AlertBoardTableRecordBean)action.getComponent().getAttributes().get("alert");
		tableRecordManager.removeAlerts(recordToDelete);
	}	
	
//	public void editItem(ActionEvent action) {
//		
//		selectedAlertItem = (AlertItemSelection)action.getComponent().getAttributes().get("item");
//		selectedAlertItem.saveAlertList();
//		tempTitle = getSelectedAlertItem().getAlertItem().getTitle();
//		newTitle = tempTitle;
//
//		alertBoardItemConfigVisible = true;
//		errorAlertListVisible = false;
//	}
	
//	public void removeItems(ActionEvent action) {
//		Iterator<AlertItemSelection> itemIt = alertItems.iterator();
//		
//		while (itemIt.hasNext()) {
//			AlertItemSelection item = itemIt.next();
//			if (item.isSelected()) {
//				itemIt.remove();
//			}		
//		}
//		
//		selected = false;
//	}	
	
//	public void removeItemsForHeaderButton(ActionEvent action) {
//	Iterator<AlertItemSelection> itemIt = alertItems.iterator();
//	
//	while (itemIt.hasNext()) {
//		AlertItemSelection item = itemIt.next();
//		if (item.isSelected()) {
//			itemIt.remove();
//		}		
//	}
//	
//	selected = false;
//}
	
//	public void removeItems(ActionEvent action) {
//		selectedAlertItem = (AlertItemSelection)action.getComponent().getAttributes().get("item");
//		
//		Iterator<AlertItemSelection> itemIt = alertItems.iterator();
//		while (itemIt.hasNext()) {
//			AlertItemSelection item = itemIt.next();
//			if (selectedAlertItem == item) {
//				itemIt.remove();
//			}	
//		}
//	}	
	
	
	public void addAlerts(ActionEvent action) {	
		AlertDefinitionSelectionBean alertToAdd = (AlertDefinitionSelectionBean)action.getComponent().getAttributes().get("item");
		
		AllAlertDefinitionBean aabean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		Collection<AlertDefinitionSelectionBean> beans = aabean.getAlertDefinitionSelector().getDisplayedBeans();
		
		List<AlertDefinitionSelectionBean> selectedAlerts = new ArrayList<AlertDefinitionSelectionBean>();
		
		for(AlertDefinitionSelectionBean alertDefinitionBean : beans) {
			if (alertDefinitionBean.equals(alertToAdd)) {
				selectedAlerts.add(alertDefinitionBean.clone());
			}
//			if (alertDefinitionBean.isSelected()) {
//				selectedAlerts.add(alertDefinitionBean.clone());
//			}
		}
		
		tableRecordManager.addAlerts(selectedAlerts);
	}
	
	public void addAllAlerts(ActionEvent action) {	
		AllAlertDefinitionBean aabean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		Collection<AlertDefinitionSelectionBean> beans = aabean.getAlertDefinitionSelector().getDisplayedBeans();
		
		List<AlertDefinitionSelectionBean> selectedAlerts = new ArrayList<AlertDefinitionSelectionBean>();
		
		for(AlertDefinitionSelectionBean alertDefinitionBean : beans) {
			selectedAlerts.add(alertDefinitionBean.clone());
		}
		
		tableRecordManager.addAlerts(selectedAlerts);
	}
	
	public void save() {
		tableRecordManager.save();	
		if (isAlertInListTest() == false)
			return;
		RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();		
		if (alertBoard.getId() == null || alertBoard.getId().length() == 0) {
			factory.addNewAlertBoardComponent(env, type, title, alertBoard);
		} else {
			factory.updateAlertBoardComponent(env, type, title, alertBoard);
		}
		
		DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
		bean.resetSelectedDashBoard();
	}

	public boolean isAlertInListTest() {
		return alertInListTest;
	}

	public void setAlertInListTest(boolean alertInListTest) {
		this.alertInListTest = alertInListTest;
	}

	public String getAlertBoardTitleStyle() {
		if(errorAlertBoardTitleVisible || titleError)
			return "border: 1px solid red;";
		else
			return "";
	}

	public String getItemTitleStyle() {
		if(!getListRenderer()) {
			errorItemTitleVisible = false;
			return "width: 150px;";
		}
		else if(errorItemTitleVisible)
			return "width: 150px; border: 1px solid red;";
		else
			return "width: 150px;";
	}
	
	public boolean getListRenderer() {
		if(tableRecordManager.getFilesGroupRecordBeans() != null && tableRecordManager.getFilesGroupRecordBeans().size() != 0)
			return true;
		else
			return false;
	}
	
	public boolean getAlertlist() {
		ArrayList<TableRecordBean> items = tableRecordManager.getFilesGroupRecordBeans();
		int i = 0;
		for(TableRecordBean item : items) {
			if(item.getChildFilesRecords()!= null && item.getChildFilesRecords().size() != 0)
				i++;
		}
		if(i > 0)
			return false;
		else
			return true;
	}
	
	public String getItemsTableScrollHeight() {
		if(!getListRenderer() && getAlertlist())
			return "315px;";
		else if(getListRenderer() && getAlertlist())
			return "345px;";
		else
			return "380px;";
	}

	public void setTitleError(boolean titleError) {
		this.titleError = titleError;
	}
		
//	public void save() {
//		
//		if (alertBoard.getTitle() == null || alertBoard.getTitle().length() == 0) {
//			errorAlertTitleVisible = true;
//			return;	
//		}	
//		
//		if (alertItems.isEmpty()) {
////			errorAlertItemTitleVisible = true;
//			errorAlertTitleVisible = false;
//			return;
//		}		
//		
////		errorAlertItemTitleVisible = false;
//		errorAlertTitleVisible = false;
//		try {
//			ObjectFactory objFactory = new ObjectFactory();
//			
//			alertBoard.getAlertItem().clear();
//			for (AlertItemSelection alertItem : alertItems) {
//				AlertItemType alertTypeItem = alertItem.getAlertItem();
//				
//				alertTypeItem.getAlert().clear();
//				
//				Collection<AlertDefinitionSelectionBean> beans = alertItem.getAlertBeans();
//				if (beans == null || beans.isEmpty()) {
//					errorAlertListVisible = true;
//					return;	
//				}
//				errorAlertListVisible = false;
//				
//				for (AlertDefinitionSelectionBean bean : beans) {
//					int logicalEnvId = bean.getAlertDefinitionBean().getAlertDefinition().getLogicalEnv();
//					
//					String env = null;
//					
//					try {
//						DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//						env = dataModelPM.getLogicalEnv(logicalEnvId).getName();
//					} catch (Exception e) {
//						logger.error(e);
//					}
//					
//					String name = bean.getAlertDefinitionBean().getAlertDefinition().getName();
//					
//					AlertType alert = objFactory.createAlertBoardTypeAlertItemTypeAlertType();
//					alert.setEnv(env);
//					alert.setLabel(name);
//					
//					alertTypeItem.getAlert().add(alert);
//				}
//				alertBoard.getAlertItem().add(alertTypeItem);
//			}
//			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
//			
//			if (alertBoard.getId() == null || alertBoard.getId().length() == 0) {
//				factory.addNewAlertBoardComponent(env, type, title, alertBoard);
//			} else {
//				factory.updateAlertBoardComponent(env, type, title, alertBoard);
//			}
//			
//			DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
//			bean.resetSelectedDashBoard();
//			
//		} catch (Exception exc) {
//			logger.error("Impossible to save alertBoard", exc);
//		}
//	}	
}
