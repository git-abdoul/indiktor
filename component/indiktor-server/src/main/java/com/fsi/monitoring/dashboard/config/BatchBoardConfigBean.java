package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.BatchBoardType;
import generated.dashboard.config.schema.BatchBoardType.BatchItemType;
import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.dashboard.component.batch.BatchBean;
import com.fsi.monitoring.datamodel.monitor.LogicalEnvBean;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class BatchBoardConfigBean
extends DashBoardConfigBean {

	private static final Logger logger = Logger.getLogger(BatchBoardConfigBean.class);

	private List<LogicalEnvBean> logicalEnvBeans;
	private List<MonitorConfigBean> monitorConfigs;
	private List<MonitorConfigBean> monitorConfigsFiltered;
	private MonitorConfig selectedMonitorConfig = null;
	private boolean autoDiscovery = true;
	private String componentTitle;
	private Set<String> batchNames;
	
	private List<BatchBean> batchBeans = new ArrayList<BatchBean>();
	private BatchBean selectedBatchBean;
	
	private boolean selectAllBatches = false;
	
	private boolean errorBatchBoardTitleVisible = false;
	private boolean errorBatchBoardSelectedCollectorVisible = false;
	private boolean errorBatchNameSelectedVisible = false;
	private boolean onUpdate;
	
	private boolean titleError;
	
	private BatchBoardType batchBoard;
	
	private int rowsByPage = 10;
	private boolean paginationVisible = false;
	
	public void init(String env,
					 String type,
					 String title) {
		try {
			super.init(env, type, title);
			
			onUpdate = false;
			autoDiscovery = true;
			
			ObjectFactory objFactory = new ObjectFactory();
			
			BatchItemType batchBoardItem = objFactory.createBatchBoardTypeBatchItemType();
			batchBoard = objFactory.createBatchBoardType();
			
//			List<BatchItemType> batchBoardItems = batchBoard.getBatchItem();
//			batchBoardItems.add(batchBoardItem);

			init();			
		} catch (Exception exc) {
			logger.error(exc);
		}			
	}
	
	public void init() {
		batchBeans = new ArrayList<BatchBean>();
		componentTitle = "";
		monitorConfigs = new ArrayList<MonitorConfigBean>();
		monitorConfigsFiltered = new ArrayList<MonitorConfigBean>();
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		try {
			logicalEnvBeans = new ArrayList<LogicalEnvBean>();
			List<LogicalEnv> logicalEnvs = new ArrayList<LogicalEnv>(dataModelPM.getLogicalEnvs().values());
			for(LogicalEnv env : logicalEnvs) {
				logicalEnvBeans.add(new LogicalEnvBean(env));
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		
		try {
			for(LogicalEnvBean logicalEnvBean : logicalEnvBeans) {
				int logicalEnvId = logicalEnvBean.getLogicalEnv().getId();
			
				Map<Long,MonitorConfig> monitorConfigMap = dataModelPM.getMonitorConfigs(logicalEnvId);
				
				Map<Integer, ConnectorConfig> connectorConfigs = dataModelPM.getConnectorConfigs();
				
				for (MonitorConfig config : monitorConfigMap.values()) {
					IkrStaticDomain domain = dataModelPM.getIkrStaticDomain(config.getMetricDomainConfig().getIkrStaticDomainId());	
					List<String> connectorTypes = new ArrayList<String>();
					for(int conId : config.getConnectorConfigIds()) {
						connectorTypes.add((connectorConfigs.get(conId)).getType());
					}
					monitorConfigs.add(new MonitorConfigBean(config, logicalEnvBean.getLogicalEnv(), domain, connectorTypes));
				}
			}

			if(onUpdate == false) {
				for (MonitorConfigBean monitorConfig : monitorConfigs) {
					String metricDomainConfig = monitorConfig.getMonitorConfig().getMetricDomainConfig().getDescription();
					if(metricDomainConfig.contains("Calypso Scheduled Tasks Monitoring")){
						monitorConfigsFiltered.add(monitorConfig);
					}
				}
				for(MonitorConfigBean monitorConfig : monitorConfigsFiltered) {
					selectedMonitorConfig = null;
					monitorConfig.setSelected(false);
				}
			}
    	} catch(Exception exc) {    		
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	public void init(String env,
					 String type,
					 String title,
					 String componentId) {
		super.init(env, type, title);
		
		try {
			onUpdate = true;
			init();
			ComponentXmlManager componentXmlManager = (ComponentXmlManager)FacesUtils.getManagedBean("xmlComponentManager");
			
			DashBoard dashBoard = componentXmlManager.getDashBoardSchema(env, type, title);
			
			List<BatchBoardType> batchBoards = dashBoard.getBatchBoards().getBatchBoard();
			for (BatchBoardType batchBoard : batchBoards) {
				if (batchBoard.getId().equals(componentId)) {
					this.batchBoard = batchBoard;
					componentTitle = batchBoard.getTitle();
					autoDiscovery = batchBoard.isAutoDiscovery();
					break;
				}			
			}	
			for (MonitorConfigBean monitorConfig : monitorConfigs) {
				String metricDomainConfig = monitorConfig.getMonitorConfig().getMetricDomainConfig().getDescription();
				if(metricDomainConfig.contains("Calypso Scheduled Tasks Monitoring")){
					if (monitorConfig.getLogicalEnv().getName().equals(batchBoard.getLogicalEnv()) && monitorConfig.getContext().equals(batchBoard.getContext())) { 
						monitorConfig.setSelected(true);
						selectedMonitorConfig = monitorConfig.getMonitorConfig();
					}
					monitorConfigsFiltered.add(monitorConfig);
				}
			}
			initUniqueIkrInstanceValue();
			List<BatchItemType> batchItems = batchBoard.getBatchItem();
			for(BatchBean batchBean : batchBeans){
				for(BatchItemType batchItem : batchItems) {
					if(batchItem.getIkrInstance().equals(batchBean.getBatchName())) {
						batchBean.setSelected(true);
					}
				}
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}

	public List<MonitorConfigBean> getMonitorConfigsFiltered() {
		return monitorConfigsFiltered;
	}
	
	public MonitorConfig getSelectedMonitorConfig() {
		return selectedMonitorConfig;
	}
	
	public String getComponentTitle() {
		return componentTitle;
	}

	public void setComponentTitle(String componentTitle) {
		this.componentTitle = componentTitle;
	}

	public void onAutoDiscoveryChanged(ValueChangeEvent evt) {
		autoDiscovery = (Boolean)evt.getNewValue();
		if (autoDiscovery)
			batchBeans = new ArrayList<BatchBean>();
		else
			initUniqueIkrInstanceValue();
		if (evt.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		}
		selectAllBatches = false;
	}
	
	private void initUniqueIkrInstanceValue() {
		batchNames = new HashSet<String>();
		batchBeans = new ArrayList<BatchBean>();
		MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
		try {
			if(selectedMonitorConfig != null) {
				Map<Long, AbstractIkrDefinition> ikrDefs = monitoringPM.getIkrDefinitions(selectedMonitorConfig.getId());
				for (AbstractIkrDefinition def : ikrDefs.values()){
					batchNames.add(def.getIkrInstance());
				}
				for(String batchname : batchNames) {
					BatchBean batchbean = new BatchBean(batchname);
					batchBeans.add(batchbean);
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}

	public List<BatchBean> getBatchBeans() {
		return batchBeans;
	}

	public List<String> getBatchNames() {
		return new ArrayList<String>(batchNames);
	}

	public void rowSelectionListener(RowSelectorEvent event) throws PersistenceException {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		MonitorConfigBean bean = monitorConfigsFiltered.get(rowId);
		this.selectedMonitorConfig = bean.getMonitorConfig();
		
		if (!autoDiscovery) {
			initUniqueIkrInstanceValue();
		}
	}
	
	public void rowSelectionListenerBatchNames(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		BatchBean batchBean = batchBeans.get(rowId);
		this.selectedBatchBean = batchBean;
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public boolean isPaginationVisible() {
		return paginationVisible;
	}

	public void setPaginationVisible(boolean paginationVisible) {
		this.paginationVisible = paginationVisible;
	}

	public BatchBoardType getBatchBoard() {
		return batchBoard;
	}

	public boolean isAutoDiscovery() {
		return autoDiscovery;
	}

	public void setAutoDiscovery(boolean autoDiscovery) {
		this.autoDiscovery = autoDiscovery;
	}
	
	public boolean isErrorBatchBoardTitleVisible() {
		return errorBatchBoardTitleVisible;
	}

	public void setErrorBatchBoardTitleVisible(boolean errorBatchBoardTitleVisible) {
		this.errorBatchBoardTitleVisible = errorBatchBoardTitleVisible;
	}

	public boolean isErrorBatchBoardSelectedCollectorVisible() {
		return errorBatchBoardSelectedCollectorVisible;
	}

	public void setErrorBatchBoardSelectedCollectorVisible(
			boolean errorBatchBoardSelectedCollectorVisible) {
		this.errorBatchBoardSelectedCollectorVisible = errorBatchBoardSelectedCollectorVisible;
	}

	public boolean isErrorBatchNameSelectedVisible() {
		return errorBatchNameSelectedVisible;
	}

	public void setErrorBatchNameSelectedVisible(
			boolean errorBatchNameSelectedVisible) {
		this.errorBatchNameSelectedVisible = errorBatchNameSelectedVisible;
	}

	public void save() {				
		try {
			if(componentTitle.trim().length()<1) {
				errorBatchBoardTitleVisible = true;
				errorBatchBoardSelectedCollectorVisible = false;
				errorBatchNameSelectedVisible = false;
				return;
			}
			
			if(selectedMonitorConfig == null) {
				errorBatchBoardSelectedCollectorVisible = true;
				errorBatchBoardTitleVisible = false;
				errorBatchNameSelectedVisible = false;
				return;
			}
			
			if(!autoDiscovery) {
				int batchNamesSelected = 0;
				for(BatchBean batchBean : batchBeans) {
					if(batchBean.isSelected()){
						batchNamesSelected++;
					}
				}
				
				if(batchNamesSelected == 0) {
					errorBatchNameSelectedVisible = true;
					errorBatchBoardSelectedCollectorVisible = false;
					errorBatchBoardTitleVisible = false;
					return;
				}
			}
			
			errorBatchBoardSelectedCollectorVisible = false;
			errorBatchBoardTitleVisible = false;
			errorBatchNameSelectedVisible = false;
			
			ObjectFactory objFactory = new ObjectFactory();
			batchBoard.getBatchItem().clear();
			
			batchBoard.setTitle(componentTitle);
			batchBoard.setContext(selectedMonitorConfig.getContext());
			
			int logicalEnvId = selectedMonitorConfig.getLogicalEnvId();
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(logicalEnvId);
			batchBoard.setLogicalEnv(logicalEnv.getName());
			
			if(autoDiscovery == true){
				batchBoard.setAutoDiscovery(true);
			}
			else {
				batchBoard.setAutoDiscovery(false);
				for(BatchBean batchBean : batchBeans) {
					if(batchBean.isSelected()){
						BatchItemType batchItemType = objFactory.createBatchBoardTypeBatchItemType();
						batchItemType.setIkrInstance(batchBean.getBatchName());
						batchItemType.setLabel(batchBean.getBatchName());
						batchBoard.getBatchItem().add(batchItemType);
					}
				}
			}

			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
			
			if (batchBoard.getId() == null || batchBoard.getId().length() == 0) {
				factory.addNewBatchBoardComponent(env, type, title, batchBoard);
			} else {
				factory.updateBatchBoardComponent(env, type, title, batchBoard);
			}
			
			DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			bean.resetSelectedDashBoard();
		
		} catch (Exception exc) {
			logger.error("Error while saving Batch Board details", exc);
		}
		
		selectAllBatches = false;
	}
	
	public void handleSelectedCollector(ValueChangeEvent evt) {
		if (evt.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		}
		for(MonitorConfigBean monitorConfigFiltered : monitorConfigsFiltered) {
			monitorConfigFiltered.setSelected(false);
		}
		
		MonitorConfigBean collectorSelected = (MonitorConfigBean)evt.getComponent().getAttributes().get("monitorConfigBean");
		if((Boolean)evt.getNewValue() == true)
			selectedMonitorConfig = collectorSelected.getMonitorConfig(); 
		else
			selectedMonitorConfig = null;
			
		if(collectorSelected != null) {
			for(MonitorConfigBean monitorConfigFiltered : monitorConfigsFiltered) {
				if(monitorConfigFiltered.equals(collectorSelected)) {
					collectorSelected.setSelected((Boolean)evt.getNewValue());
				}
			}	
		}
		
		if (!autoDiscovery) {
			initUniqueIkrInstanceValue();
		}
		selectAllBatches = false;
	}
	
	public void handleSelectedBatch(ValueChangeEvent evt) {
		if (evt.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		}
		BatchBean bacthSelected = (BatchBean)evt.getComponent().getAttributes().get("batchNames");
		selectedBatchBean = bacthSelected;
		if(bacthSelected != null) {
			for(BatchBean batchBean : batchBeans) {
				if(batchBean.equals(bacthSelected)) {
					bacthSelected.setSelected((Boolean)evt.getNewValue());
				}
			}	
		}
	}
	
	public void handleSelectAllBatches(ValueChangeEvent evt) {
		if (evt.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		}
		for(BatchBean batchBean : batchBeans) {
			batchBean.setSelected((Boolean)evt.getNewValue());
		}
	}

	public boolean isSelectAllBatches() {
		return selectAllBatches;
	}

	public void setSelectAllBatches(boolean selectAllBatches) {
		this.selectAllBatches = selectAllBatches;
	}

	public String getBatchBoardPanelStyle() {
		if(errorBatchBoardSelectedCollectorVisible)
			return "border: 1px solid red; height: 325px;";
		else
			return "border: 1px solid #336699; height: 325px;";
	}

	public String getBatchBoardPanel2Style() {
		if(errorBatchNameSelectedVisible)
			return "border: 1px solid red; height: 416px; padding: 0px; margin: 0px;";
		else
			return "border: 1px solid #336699; height: 416px; padding: 0px; margin: 0px;";
	}

	public String getBatchBoardTitleStyle() {
		if(errorBatchBoardTitleVisible || titleError)
			return "border: 1px solid red;";
		else
			return "";
	}

	public void setTitleError(boolean titleError) {
		this.titleError = titleError;
	}
}
