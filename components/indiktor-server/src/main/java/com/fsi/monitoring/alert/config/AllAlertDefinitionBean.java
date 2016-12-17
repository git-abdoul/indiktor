package com.fsi.monitoring.alert.config;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import com.fsi.monitoring.alert.bean.AlertModifierBean;
import com.fsi.monitoring.alert.selection.AlertDefinitionSelector;
import com.fsi.monitoring.alert.selection.AlertSelectorItemVisitor;
import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.datamodel.bean.StaticDataDefinitionBean;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class AllAlertDefinitionBean 
extends SortableList
implements AlertSelectorItemVisitor {

	private int rowsByPage = 15;
	private int rowsByPageForAlertBoardComponent = 10;
	private boolean paginationVisible = false;
	private boolean paginationVisibleForAlertBoardComponent = false;
	
	private boolean selectAll = false;
	private boolean selectAllInAlertBoardComponent = false;
	
	private static final String labelColumnName = "Label";
	private static final String envColumnName = "Environment";
	private static final String groupColumnName = "Group";
	private static final String domainColumnName = "Domain";
	private static final String subDomainColumnName = "Sub Domain";
	private static final String activeColumnName = "Active";
	
	private AlertDefinitionSelectionBean selectedAlertDefinitionBean;
	
	private List<AlertDefinitionSelectionBean> alertDefinitions;
	private List<AlertDefinitionSelectionBean> alertDefinitionsSelected;
	
    private AlertDefinitionSelector alertDefinitionSelector;
    
    private String searchQuery = "";
    
	public AllAlertDefinitionBean() {
		super(labelColumnName);
		alertDefinitionSelector = new AlertDefinitionSelector();
		alertDefinitions = new ArrayList<AlertDefinitionSelectionBean>();
		alertDefinitionsSelected = new ArrayList<AlertDefinitionSelectionBean>();
	}
	
	public AlertDefinitionSelector getAlertDefinitionSelector() {
		return alertDefinitionSelector;
	}		
	
	public synchronized Collection<AlertDefinitionSelectionBean> getAlertDefinitionBeans() {		
		filterConfigs();
		if (alertDefinitions!=null && alertDefinitions.size()>0)
			sort();
		return alertDefinitions;
	}
	
	public void searchAlertDefQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		init(null);
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		selectAll = false;
	}
	
	public void filterConfigs() {
		if (searchQuery!=null && searchQuery.length()>0) {
			searchQuery = searchQuery.toLowerCase();
			List<String> queryItems = IkrUtils.splitSearchIndex(searchQuery);
			String[] searchQueryItems = (String[])queryItems.toArray(new String[queryItems.size()]);
			List<AlertDefinitionSelectionBean> newAlertDefinitionList = new ArrayList<AlertDefinitionSelectionBean>();
			for (AlertDefinitionSelectionBean bean : alertDefinitions) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newAlertDefinitionList.add(bean);
			}
			alertDefinitions = new ArrayList<AlertDefinitionSelectionBean>();
			alertDefinitions.addAll(newAlertDefinitionList);
		}
	}
	
	public void init(ActionEvent action) {
		alertDefinitionSelector.init(this);
		setAction("alertConfig");		
		paginationVisible = alertDefinitionSelector.getDisplayedBeans().size()>rowsByPage;
		paginationVisibleForAlertBoardComponent = alertDefinitionSelector.getDisplayedBeans().size()>rowsByPageForAlertBoardComponent;
		alertDefinitions = alertDefinitionSelector.getDisplayedBeans();
		selectAll = false;
		alertDefinitionsSelected = new ArrayList<AlertDefinitionSelectionBean>();
	}
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		selectedAlertDefinitionBean = alertDefinitions.get(rowId);
	}	
	
	public long getSelectedAlertDefinitionId() {
		long id = 0;
		if (selectedAlertDefinitionBean != null)
			id = selectedAlertDefinitionBean.getAlertDefinitionBean().getAlertDefinition().getId();
		return id;
	}	

	public AlertDefinitionSelectionBean getSelectedAlertDefinitionBean() {
		return selectedAlertDefinitionBean;
	}

	public void setSelectedAlertDefinitionBean(	AlertDefinitionSelectionBean selectedAlertDefinitionBean) {
		this.selectedAlertDefinitionBean = selectedAlertDefinitionBean;
	}
	
	
	public String getDeleteMessage() {
		int numberAlertDefinitionsSelected = 0;
		for (AlertDefinitionSelectionBean alertDefinition : alertDefinitions) {
			if (alertDefinition.isSelected()){
				numberAlertDefinitionsSelected++;
			}
		}
		String message = "";
		if (numberAlertDefinitionsSelected == 1) {
			for (AlertDefinitionSelectionBean alertDefinition : alertDefinitions) {
				if (alertDefinition.isSelected()){
					message = "Are you sure to delete this alert definition : " + alertDefinition.getAlertDefinitionBean().getAlertDefinition().getName();		
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberAlertDefinitionsSelected + " alert definitions?";
			return message;
		}
	}

	public void displayBeansUpdated() {
		 sort();
	}	
	
	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}	

	public int getRowsByPageForAlertBoardComponent() {
		return rowsByPageForAlertBoardComponent;
	}

	public boolean isPaginationVisible() {
		return paginationVisible;
	}

	public boolean isPaginationVisibleForAlertBoardComponent() {
		return paginationVisibleForAlertBoardComponent;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(alertDefinitions, new Comparator<AlertDefinitionSelectionBean>() {
			public int compare(AlertDefinitionSelectionBean o1, AlertDefinitionSelectionBean o2) {
				int res = 0;
				try {
					if (getLabelColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase().compareTo(o2.getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase()) :  o2.getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase().compareTo(o1.getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase());
					}
					else if (getEnvColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase().compareTo(o2.getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase()) :  o2.getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase().compareTo(o1.getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase());
					}
					else if (getGroupColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getAlertDefinitionBean().getGroup().toLowerCase().compareTo(o2.getAlertDefinitionBean().getGroup().toLowerCase()) :  o2.getAlertDefinitionBean().getGroup().toLowerCase().compareTo(o1.getAlertDefinitionBean().getGroup().toLowerCase());
					}
					else if (getDomainColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getAlertDefinitionBean().getDomain().toLowerCase().compareTo(o2.getAlertDefinitionBean().getDomain().toLowerCase()) :  o2.getAlertDefinitionBean().getDomain().toLowerCase().compareTo(o1.getAlertDefinitionBean().getDomain().toLowerCase());
					}
					else if (getSubDomainColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getAlertDefinitionBean().getSubDomain().toLowerCase().compareTo(o2.getAlertDefinitionBean().getSubDomain().toLowerCase()) :  o2.getAlertDefinitionBean().getSubDomain().toLowerCase().compareTo(o1.getAlertDefinitionBean().getSubDomain().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});	
	}

	public String getLabelColumnName() {
		return labelColumnName;
	}

	public String getEnvColumnName() {
		return envColumnName;
	}

	public String getGroupColumnName() {
		return groupColumnName;
	}

	public String getDomainColumnName() {
		return domainColumnName;
	}

	public String getSubDomainColumnName() {
		return subDomainColumnName;
	}

	public String getActiveColumnName() {
		return activeColumnName;
	}
	
	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public void handleSelectedAlert(ValueChangeEvent event) {
		AlertDefinitionSelectionBean checkedAlert = (AlertDefinitionSelectionBean)event.getComponent().getAttributes().get("checkedAlert");
		if(checkedAlert != null) {
			for(AlertDefinitionSelectionBean alertDefinition : alertDefinitions) {
				if(alertDefinition.equals(checkedAlert)) {
					checkedAlert.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						alertDefinitionsSelected.add(checkedAlert);
					else
						alertDefinitionsSelected.remove(checkedAlert);
				}
			}
		}
	}

	public int getAlertDefinitionsSelected() {
		int size = alertDefinitionsSelected.size();
		return size;
	}

	public void setAlertDefinitionsSelected(List<AlertDefinitionSelectionBean> alertDefinitionsSelected) {
		this.alertDefinitionsSelected = alertDefinitionsSelected;
	}
	
	public void handleSelectAllAlerts(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		alertDefinitionsSelected.clear();
		for(AlertDefinitionSelectionBean alertDefinition : alertDefinitions) {
			alertDefinition.onChangeSelected(evt);
			if((Boolean)evt.getNewValue())
				alertDefinitionsSelected.add(alertDefinition);
		}
	}
	
	public void handleDeleteNoSelection(Long alertId) {
		for(AlertDefinitionSelectionBean alertDefinition : alertDefinitions) {
			if(alertDefinition.getId() == alertId) {
				alertDefinitionsSelected.remove(alertDefinition);
				break;
			}
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	public void handleSelectAllInAlertBoardComponent(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		for(AlertDefinitionSelectionBean alertDefinition : alertDefinitions) {
			alertDefinition.onChangeSelected(evt);
		}
	}

	public boolean isSelectAllInAlertBoardComponent() {
		return selectAllInAlertBoardComponent;
	}

	public void setSelectAllInAlertBoardComponent(boolean selectAllInAlertBoardComponent) {
		this.selectAllInAlertBoardComponent = selectAllInAlertBoardComponent;
	}
}