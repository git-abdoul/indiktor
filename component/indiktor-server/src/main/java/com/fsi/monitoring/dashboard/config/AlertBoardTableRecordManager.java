package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.AlertBoardType.AlertItemType;
import generated.dashboard.config.schema.AlertBoardType.AlertItemType.AlertType;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;
import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.component.expandableTable.TableRecordsManager;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;

public class AlertBoardTableRecordManager extends TableRecordsManager implements Observer{
	
	private static final Logger logger = Logger.getLogger(AlertBoardTableRecordManager.class);	
	
	private List<AlertItemSelection> alertItems;
	
//	private Map<AlertItemSelection, List<AlertDefinitionSelectionBean>> recordAlertItems;
	
//	private Map<String, AlertBoardTableRecordBean> itemRecords;
	
//	private static final String ADD_NEW_ITEM = "addNewItem";
	
//	private static final String CHANGE_ITEM_NAME = "CHANGE_ITEM_NAME";
	
	private boolean errorAlertItemTitleVisible = false;
	private boolean noItemSelectedError = false;
	private boolean multipleItemsSelectedError = false;
	
	public AlertBoardTableRecordManager(List<AlertItemSelection> alertItems) {
		this.alertItems = alertItems;
//		recordAlertItems = new HashMap<AlertItemSelection, List<AlertDefinitionSelectionBean>>();
	}

	@Override
	protected void initTableData() {
		for (AlertItemSelection item : alertItems){
			AlertBoardTableRecordBean itemRecord = new AlertBoardTableRecordBean(item,
																				GROUP_INDENT_STYLE_CLASS,
																				GROUP_ROW_STYLE_CLASS,
																				styleBean,
																				EXPAND_IMAGE,
																				CONTRACT_IMAGE,
																				recordBeans,
																				false);
			itemRecord.addObserver(this);
			
//			List<AlertDefinitionSelectionBean> alertBeans = recordAlertItems.get(item);
//			if (alertBeans == null) {
//				alertBeans = new ArrayList<AlertDefinitionSelectionBean>();
//				recordAlertItems.put(item, alertBeans);
//			}
			
			for (AlertDefinitionSelectionBean alert : item.getAlertBeans()) {
				AlertBoardTableRecordBean alertItemRecord = new AlertBoardTableRecordBean(alert.clone(),
																					GROUP_INDENT_STYLE_CLASS,
																					GROUP_ROW_STYLE_CLASS,
																					styleBean,
																					SPACER_IMAGE);
				alertItemRecord.addObserver(this);
				itemRecord.addChildRecord(alertItemRecord);
//				alertBeans.add(alert);
			}
		}
	}	
	
	public void update(Observable o, Object arg) {
		AlertBoardTableRecordBean obs = (AlertBoardTableRecordBean)arg;
//		if(CHANGE_ITEM_NAME.equals(obs.getActionToDo())) {
//			if ("".equals(obs.getOldAlertItem())) 
//				itemRecords.remove(ADD_NEW_ITEM);
//			else 
//				itemRecords.remove(obs.getOldAlertItem());
//			itemRecords.put(obs.getAlertItem(), obs);
//		}
	}

	public void addItem() {		
		try {
			ObjectFactory objFactory = new ObjectFactory();
			AlertItemType alertItemType = objFactory.createAlertBoardTypeAlertItemType();	
			AlertItemSelection alertItemToAdd = new AlertItemSelection(alertItemType);
			AlertBoardTableRecordBean itemRecord = new AlertBoardTableRecordBean(alertItemToAdd,
																			GROUP_INDENT_STYLE_CLASS,
																			GROUP_ROW_STYLE_CLASS,
																			styleBean,
																			EXPAND_IMAGE,
																			CONTRACT_IMAGE,
																			recordBeans,
																			true);
			for (TableRecordBean record : recordBeans) {
				((AlertBoardTableRecordBean)record).setSelected(false);
			}
			itemRecord.setSelected(true);
//			recordAlertItems.put(alertItemToAdd, new ArrayList<AlertDefinitionSelectionBean>());
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
		}	
		
//		itemRecord.addObserver(this);
//		itemRecords.put(ADD_NEW_ITEM, itemRecord);
		
//		AlertItemSelection itemToAdd = new AlertItemSelection(null);
//		alertItems.add(itemToAdd);
//		for (AlertItemSelection item : alertItems){
//			List<AlertDefinitionSelectionBean> alertBeans = recordAlertItems.get(alertItems);
//			if (alertBeans == null) {
//				alertBeans = new ArrayList<AlertDefinitionSelectionBean>();
//				recordAlertItems.put(item, alertBeans);
//			}
//		}
	}
	
	public void removeItems(AlertBoardTableRecordBean recordToDelete) {
		if(recordToDelete.getChildFilesRecords() != null && recordToDelete.isExpanded()) {
			recordToDelete.contractNodeAction();
			recordToDelete.getChildFilesRecords().clear();
		}
		recordBeans.remove(recordToDelete);
		if(recordBeans.isEmpty()) {
			AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
			if (alertBoardConfigBean != null)
				alertBoardConfigBean.setAlertInListTest(false);
		}
//		recordAlertItems.remove(recordToDelete.getAlertItem());		
	}
	
	public void addAlerts(List<AlertDefinitionSelectionBean> beans) {	
		multipleItemsSelectedError = false;
		noItemSelectedError = false;
		AlertBoardTableRecordBean selectedItem = null;
		AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
		int i = 0;
		
		for (TableRecordBean record : recordBeans) {
			if (((AlertBoardTableRecordBean)record).isSelected()) {
				i++;
			}
		}
		
		if (i > 1) {
			multipleItemsSelectedError = true;
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.init();
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("You cannot select multiple item to add an alert");
//			error.addMessage("Please Select only one Item");
			return;
		}
		
		for (TableRecordBean record : recordBeans) {
			if (((AlertBoardTableRecordBean)record).isSelected()) {
				selectedItem = (AlertBoardTableRecordBean)record;
				String selectedItemTitle = selectedItem.getAlertItem().getAlertItem().getTitle();
				if (selectedItemTitle == null || selectedItemTitle.trim().length() == 0){
					alertBoardConfigBean.setErrorAlertBoardTitleVisible(false);
					alertBoardConfigBean.setErrorAlertListVisible(false);
					alertBoardConfigBean.setErrorItemListVisible(false);
					alertBoardConfigBean.setErrorItemTitleVisible(true);
					return;
				}
				break;
			}
		}
		
		if (selectedItem != null) {	
//			List<AlertDefinitionSelectionBean> alertBeans = recordAlertItems.get(selectedItem.getAlertItem());
			for(AlertDefinitionSelectionBean alertDefinitionBean : beans) {
				AlertBoardTableRecordBean alertItemRecord = new AlertBoardTableRecordBean(alertDefinitionBean.clone(),
																						GROUP_INDENT_STYLE_CLASS,
																						GROUP_ROW_STYLE_CLASS,
																						styleBean,
																						SPACER_IMAGE);
				
//				if (selectedItem.getChildFilesRecords()!= null && (selectedItem.getChildFilesRecords().contains(alertItemRecord)))
//					continue;				
				
				if (!selectedItem.getAlertItem().contains(alertDefinitionBean)) {
					selectedItem.addChildRecord(alertItemRecord);
					selectedItem.getAlertItem().add(alertDefinitionBean);
					alertBoardConfigBean.setAlertInListTest(true);
				}
			}
			alertBoardConfigBean.setErrorItemTitleVisible(false);
		}
		else {
			noItemSelectedError = true;
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.init();
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("No Alert Item Selected !!!");
//			error.addMessage("Please Select an Item on the right side");
		}
	}
	
	public void removeAlerts(AlertBoardTableRecordBean recordToDelete) {		
		// Remove Alert from Display

//		AlertBoardTableRecordBean parentRecord = (AlertBoardTableRecordBean)recordToDelete.getParent();
//		List<TableRecordBean> childRecord = parentRecord.getChildFilesRecords();
//		
//		for (TableRecordBean child : childRecord) {
//			if (child == recordToDelete)
//				parentRecord.removeChildFilesGroupRecord(recordToDelete);
//		}
		recordToDelete.removeChildFilesGroupRecord(recordToDelete);
		recordBeans.remove(recordToDelete);
		
		// Remove alert from Record Manager		
		AlertBoardTableRecordBean parentRecord = (AlertBoardTableRecordBean)recordToDelete.getParent();
		parentRecord.getAlertItem().getAlertBeans().remove(recordToDelete.getAlertDefinitionBean());
		parentRecord.removeChildFilesGroupRecord(recordToDelete);
	}
	
	public void save() {			
		try {
			AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
			ObjectFactory objFactory = new ObjectFactory();
			for (int i=0; i<recordBeans.size(); i++) {
				AlertBoardTableRecordBean record = (AlertBoardTableRecordBean)recordBeans.get(i);
				AlertItemSelection alertItem = record.getAlertItem();
				
				if (alertItem == null)
					continue;
				
				AlertItemType alertTypeItem = alertItem.getAlertItem();			
				alertTypeItem.getAlert().clear();			
				Collection<AlertDefinitionSelectionBean> beans = alertItem.getAlertBeans();
				if (!(beans == null || beans.isEmpty())) {
					for (AlertDefinitionSelectionBean bean : beans) {
						int logicalEnvId = bean.getAlertDefinitionBean().getAlertDefinition().getLogicalEnv();
						
						String env = null;
						
						try {
							DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
							env = dataModelPM.getLogicalEnv(logicalEnvId).getName();
						} catch (Exception e) {
							logger.error(e);
						}
						
						String name = bean.getAlertDefinitionBean().getAlertDefinition().getName();
						
						AlertType alert = objFactory.createAlertBoardTypeAlertItemTypeAlertType();
						alert.setEnv(env);
						alert.setLabel(name);
						
						alertTypeItem.getAlert().add(alert);
						alertBoardConfigBean.setAlertInListTest(true);
					}		
					alertBoardConfigBean.getAlertBoard().getAlertItem().add(alertTypeItem);
				}
//				else {
//					removeItems(record);
//				}
			}
		} catch (Exception exc) {
			logger.error("Impossible to save alertBoard", exc);
		}
	}	
	
	public ArrayList<TableRecordBean> getFilesGroupRecordBeans() {
		ArrayList<TableRecordBean> records = super.getFilesGroupRecordBeans();
		return records;
	}
	
	public boolean isErrorAlertItemTitleVisible() {
		return errorAlertItemTitleVisible;
	}

	public void setErrorAlertItemTitleVisible(boolean errorAlertItemTitleVisible) {
		this.errorAlertItemTitleVisible = errorAlertItemTitleVisible;
	}

	public boolean isNoItemSelectedError() {
		return noItemSelectedError;
	}

	public void setNoItemSelectedError(boolean noItemSelectedError) {
		this.noItemSelectedError = noItemSelectedError;
	}

	public boolean isMultipleItemsSelectedError() {
		return multipleItemsSelectedError;
	}

	public void setMultipleItemsSelectedError(boolean multipleItemsSelectedError) {
		this.multipleItemsSelectedError = multipleItemsSelectedError;
	}
}
