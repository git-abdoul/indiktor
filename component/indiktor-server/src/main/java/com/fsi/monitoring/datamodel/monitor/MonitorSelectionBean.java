package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.datamodel.ikrDefinition.expandableTable.IkrDefinitionTableRecordsManager;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class MonitorSelectionBean
extends SortableList
implements LogicalEnvSelectionVisitor, Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(MonitorSelectionBean.class);	
	
	private static final String envColumnName = "Environment";
	private static final String metricDomainColumnName = "Metric Domain";
	private static final String connectorTypeColumnName = "Connector Type";
	private static final String contextColumnName = "Context";
	private static final String autoStartColumnName = "Auto Start";
		
	private List<MonitorConfigBean> monitorConfigs;
	private List<MonitorConfigBean> monitorConfigsSelected;
	
	private MonitorConfig selectedMonitorConfig;

	private LogicalEnvSelectionBean logicalEnvSelectionBean;
	
	private MonitorCreationBean monitorCreationBean;
	private IkrDefinitionTableRecordsManager ikrDefinitionManager;
	
	private boolean rendererFilter = false;
	private boolean rendererHelp = false;
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	int numberMonitorsSelected = 0;
	
	private String searchQuery = "";
	
	public MonitorSelectionBean() {
		super(metricDomainColumnName);
		
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(false);
	}

	public void init(ActionEvent action) {
		if (!isAuthorized(91,"monitorSelection")) {
			return;
		}
		
		logicalEnvSelectionBean.accept(this);
		logicalEnvSelectionBean.init();
		
		monitorConfigsSelected = new ArrayList<MonitorConfigBean>();
		selectAll = false;
	}
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	public List<MonitorConfigBean> getMonitorConfigs() {
		filterConfigs();
		if (monitorConfigs != null && monitorConfigs.size()>0)
			sort();		
		return monitorConfigs;
	}

	public void setMonitorConfigs(List<MonitorConfigBean> monitorConfigs) {
		this.monitorConfigs = monitorConfigs;
	}
	
	public void changeLogicalEnv(int newLogicalEnv) {
		reloadMonitors();
	}

	public void reloadMonitors() {
		monitorConfigs = new ArrayList<MonitorConfigBean>();
		try {
			logicalEnvSelectionBean.accept(this);
			int logicalEnvId = logicalEnvSelectionBean.getLogicalEnvId();	
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			Map<Long, MonitorConfig> monitorConfigMap = dataModelPM.getMonitorConfigs(logicalEnvId);
			Map<Integer, ConnectorConfig> connectorConfigs = dataModelPM.getConnectorConfigs();			
			for (MonitorConfig config : monitorConfigMap.values()) {
				IkrStaticDomain domain = dataModelPM.getIkrStaticDomain(config.getMetricDomainConfig().getIkrStaticDomainId());	
				List<String> connectorTypes = new ArrayList<String>();
				for(int conId : config.getConnectorConfigIds()) {
					connectorTypes.add((connectorConfigs.get(conId)).getType());
				}
				monitorConfigs.add(new MonitorConfigBean(config, logicalEnvSelectionBean.getLogicalEnv(config.getLogicalEnvId()), domain, connectorTypes));
			}
    	} catch(Exception exc) {    		
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	public boolean isRendererFilter() {
		return rendererFilter;
	}

	public void openFilterPopup(ActionEvent event) {
		rendererFilter = true;
	}
	
	public void closeFilterPopup(ActionEvent event) {
		rendererFilter = false;
	}
		
	public boolean isRendererHelp() {
		return rendererHelp;
	}
	
	public void openHelpPopup(ActionEvent event) {
		rendererHelp = true;
	}
	
	public void closeHelpPopup(ActionEvent event) {
		rendererHelp = false;
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public boolean isPaginationVisible() {
		if (monitorConfigs.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}

	public void setPaginationVisible(boolean paginationVisible) {
		this.paginationVisible = paginationVisible;
	}
	
	public void searchMonitorQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		reloadMonitors();
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
			List<MonitorConfigBean> newMonitorConfigList = new ArrayList<MonitorConfigBean>();
			for (MonitorConfigBean bean : monitorConfigs) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newMonitorConfigList.add(bean);
			}
			monitorConfigs = new ArrayList<MonitorConfigBean>();
			monitorConfigs.addAll(newMonitorConfigList);
		}
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		MonitorConfigBean bean = monitorConfigs.get(rowId);
		this.selectedMonitorConfig = bean.getMonitorConfig();
		
		// Update Collector Definition for Edit
		monitorCreationBean = (MonitorCreationBean)FacesUtils.getManagedBean("monitorCreationBean");
		monitorCreationBean.update(selectedMonitorConfig);
		
		// Update Collector Metrics for Edit
		ikrDefinitionManager = (IkrDefinitionTableRecordsManager)FacesUtils.getManagedBean("ikrDefinitionsManager");
		ikrDefinitionManager.update(selectedMonitorConfig);
	}
	
	public void createNewCollector(ActionEvent event) {
		if (!isAuthorized(95,"")) {
			setAccessDenied();
			return;
		}
		monitorCreationBean = (MonitorCreationBean)FacesUtils.getManagedBean("monitorCreationBean");
		monitorCreationBean.create();
	}
	
	public void editCollector(ActionEvent event) {
		if (!isAuthorized(93,"")) {
			setAccessDenied();
			return;
		}
		MonitorConfigBean collectorSelected = (MonitorConfigBean)event.getComponent().getAttributes().get("monitorConfigBean");
		monitorCreationBean = (MonitorCreationBean)FacesUtils.getManagedBean("monitorCreationBean");
		monitorCreationBean.update(collectorSelected.getMonitorConfig());
		monitorCreationBean.edit();
	}
	
	public void deleteCollector(ActionEvent event) {
		if (!isAuthorized(92,"")) {
			setAccessDenied();
			return;
		}
		MonitorConfigBean collectorSelected = (MonitorConfigBean)event.getComponent().getAttributes().get("monitorConfigBean");
		monitorCreationBean = (MonitorCreationBean)FacesUtils.getManagedBean("monitorCreationBean");
		monitorCreationBean.update(collectorSelected.getMonitorConfig());
		monitorCreationBean.delete();
		monitorConfigsSelected = new ArrayList<MonitorConfigBean>();
		selectAll = false;
		selectedMonitorConfig = null;
	}
	
	public void deleteSelectedCollectors(ActionEvent event) {
		if (!isAuthorized(92,"")) {
			setAccessDenied();
			return;
		}
		for(MonitorConfigBean monitorConfig : monitorConfigs) {
			if(monitorConfig.isSelected()) {
				monitorCreationBean = (MonitorCreationBean)FacesUtils.getManagedBean("monitorCreationBean");
				monitorCreationBean.update(monitorConfig.getMonitorConfig());
				monitorCreationBean.delete();
			}
		}
		monitorConfigsSelected = new ArrayList<MonitorConfigBean>();
		selectAll = false;
		selectedMonitorConfig = null;
	}
	
	public void duplicateCollector(ActionEvent event) {
		if (!isAuthorized(94,"")) {
			setAccessDenied();
			return;
		}
		MonitorConfigBean collectorSelected = (MonitorConfigBean)event.getComponent().getAttributes().get("monitorConfigBean");
		monitorCreationBean = (MonitorCreationBean)FacesUtils.getManagedBean("monitorCreationBean");
		monitorCreationBean.update(collectorSelected.getMonitorConfig());
		monitorCreationBean.duplicate();
	}
	
	public void editCollectorMetrics(ActionEvent event) {
		if (!isAuthorized(97,"")) {
			setAccessDenied();
			return;
		}
		MonitorConfigBean collectorSelected = (MonitorConfigBean)event.getComponent().getAttributes().get("monitorConfigBean");
		ikrDefinitionManager = (IkrDefinitionTableRecordsManager)FacesUtils.getManagedBean("ikrDefinitionsManager");
		ikrDefinitionManager.update(collectorSelected.getMonitorConfig());
		ikrDefinitionManager.edit();
	}
	
	public String getDeleteMessage() {
		numberMonitorsSelected = 0;
		for (MonitorConfigBean monitorConfig : monitorConfigs) {
			if (monitorConfig.isSelected()){
				numberMonitorsSelected++;
				selectedMonitorConfig = monitorConfig.getMonitorConfig();
			}
		}
		String message = "No components selected";
		if (numberMonitorsSelected == 1) {
			message = "Are you sure to delete this collector : " + selectedMonitorConfig.getContext();
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberMonitorsSelected + " collectors?";
			return message;
		}
	}

	public MonitorConfig getSelectedMonitorConfig() {
		return selectedMonitorConfig;
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}
	
	public String getEnvColumnName() {
		return envColumnName;
	}
	
	public String getMetricDomainColumnName() {
		return metricDomainColumnName;
	}
	
	public String getConnectorTypeColumnName() {
		return connectorTypeColumnName;
	}
	
	public String getContextColumnName() {
		return contextColumnName;
	}
	
	public String getAutoStartColumnName() {
		return autoStartColumnName;
	}
	
	public void handleSelectedCollector(ValueChangeEvent event) {
		MonitorConfigBean collectorSelected = (MonitorConfigBean)event.getComponent().getAttributes().get("monitorConfigBean");
		if(collectorSelected != null) {
			for(MonitorConfigBean monitorConfig : monitorConfigs) {
				if(monitorConfig.equals(collectorSelected)) {
					monitorConfig.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						monitorConfigsSelected.add(collectorSelected);
					else
						monitorConfigsSelected.remove(collectorSelected);
				}
			}
		}
	}
	
	public void handleSelectAllCollectors(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		monitorConfigsSelected.clear();
		for(MonitorConfigBean monitorConfig : monitorConfigs) {
			monitorConfig.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				monitorConfigsSelected.add(monitorConfig);
		}
	}

	public int getMonitorConfigsSelected() {
		int size = monitorConfigsSelected.size();
		return size;
	}

	public void setMonitorConfigsSelected(List<MonitorConfigBean> monitorConfigsSelected) {
		this.monitorConfigsSelected = monitorConfigsSelected;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public boolean getListRendered() {
		return getMonitorConfigs().size() > 0;
	}

	@Override
	protected void sort() {
		Collections.sort(monitorConfigs, new Comparator<MonitorConfigBean>() {
			public int compare(MonitorConfigBean o1, MonitorConfigBean o2) {
				int res = 0;
				try {
					if (getEnvColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getLogicalEnv().getName().toLowerCase().compareTo(o2.getLogicalEnv().getName().toLowerCase()) :  o2.getLogicalEnv().getName().toLowerCase().compareTo(o1.getLogicalEnv().getName().toLowerCase());
					}
					else if (getMetricDomainColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()) : o2.getName().toLowerCase().compareTo(o1.getName().toLowerCase());
					}
					else if (getConnectorTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getConnectorTypes().toLowerCase().compareTo(o2.getConnectorTypes().toLowerCase()) : o2.getConnectorTypes().toLowerCase().compareTo(o1.getConnectorTypes().toLowerCase());
					}
					else if (getContextColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getContext().toLowerCase().compareTo(o2.getContext().toLowerCase()) : o2.getContext().toLowerCase().compareTo(o1.getContext().toLowerCase());
					}
					else if (getAutoStartColumnName().equals(getSortColumnName())) {
						res = ascending ? (new Boolean(o1.getMonitorConfig().isAutoStart())).compareTo(new Boolean(o2.getMonitorConfig().isAutoStart())) : (new Boolean(o2.getMonitorConfig().isAutoStart())).compareTo(new Boolean(o1.getMonitorConfig().isAutoStart()));						
					}
				}
				catch (Exception e) {}
				return res;
			}
		});		
	}	
}
