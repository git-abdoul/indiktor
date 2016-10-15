package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.AlertBoardGridType;
import generated.dashboard.config.schema.AlertBoardGridType.AlertGridItemType;
import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.bean.AlertModifierBean;
import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;
import com.fsi.monitoring.alert.selection.AlertDefinitionSelector;
import com.fsi.monitoring.alert.selection.AlertSelector;
import com.fsi.monitoring.alert.selection.AlertSelectorItemVisitor;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.dashboard.component.alert.States;
import com.fsi.monitoring.datamodel.monitor.LogicalEnvBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class AlertBoardGridConfigBean
extends DashBoardConfigBean
implements AlertSelectorItemVisitor {

	private static final Logger logger = Logger.getLogger(AlertBoardGridConfigBean.class);
	
	private AlertBoardGridType alertBoardGrid;
	
	private boolean selectAllEnvs = false;
	
	public String dataTableStyle = "";
	public String columnWidthsStyle = "";
	
	private String componentTitle;
	
	private List<LogicalEnvBean> logicalEnvBeans;
	private LogicalEnvBean selectedLogicalEnvBean;
	
	private List<AlertDefinitionSelectionBean> alerts;
	private List<AlertDefinitionSelectionBean> alertsToDisplay;
	private AlertDefinitionSelector alertDefinitionSelector;
	private AlertSelector alertSelector;
	private List<AlertModifierBean> alertBoard;
	
	private boolean upOn = true;
	private boolean downOn = false;
	private boolean ackOn = false;
	private boolean selectAll = false;
	
	private States low;
	private States medium;
	private States high;
	private States notRunning;
	
	private boolean onUpdate;

	private boolean errorAlertBoardGridTitleVisible = false;
	private boolean errorAlertBoardGridEnvVisible = false;
	private boolean errorAlertBoardGridWorkflow = false;
	
	private boolean titleError;
	
	public void init(String env,
					 String type,
					 String title) {
		try {
			super.init(env, type, title);
			
			onUpdate = false;
			selectAll = false;
			selectAllEnvs = false;
			
			ObjectFactory objFactory = new ObjectFactory();
			
			AlertGridItemType alertGridItemType = objFactory.createAlertBoardGridTypeAlertGridItemType();
			alertBoardGrid = objFactory.createAlertBoardGridType();
		
			init();			
		} catch (Exception exc) {
			logger.error(exc);
		}			
	}
	
	public void init() {
		upOn = true;
		downOn = false;
		ackOn = false;
		low = new States("LOW", true);
		medium = new States("MEDIUM", true);
		high = new States("HIGH", true);
		notRunning = new States("NOT RUNNING", true);
		alerts = new ArrayList<AlertDefinitionSelectionBean>();
		alertsToDisplay = new ArrayList<AlertDefinitionSelectionBean>();
		alertDefinitionSelector = new AlertDefinitionSelector();
		alertDefinitionSelector.init(this);
		alertSelector = new AlertSelector();
		alertSelector.init(this);
		componentTitle = "";
		selectAll = true;
		selectAllEnvs = false;
		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		try {
			logicalEnvBeans = new ArrayList<LogicalEnvBean>();
			List<LogicalEnv> logicalEnvs = new ArrayList<LogicalEnv>(dataModelPM.getLogicalEnvs().values());
			for(LogicalEnv env : logicalEnvs) {
				logicalEnvBeans.add(new LogicalEnvBean(env));
			}
			if(onUpdate == false) {
				for(LogicalEnvBean logicalEnvBean : logicalEnvBeans) {
					selectedLogicalEnvBean = null;
					logicalEnvBean.setSelected(false);
				}
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}

		initAlertList();
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
			
			List<AlertBoardGridType> alertBoardGrids = dashBoard.getAlertBoardGrids().getAlertBoardGrid();
			for(AlertBoardGridType alertBoardGrid : alertBoardGrids) {
				if(alertBoardGrid.getId().equals(componentId)) {
					this.alertBoardGrid = alertBoardGrid;
					componentTitle = alertBoardGrid.getTitle();
					upOn = alertBoardGrid.isUp();
					downOn = alertBoardGrid.isDown();
					ackOn = alertBoardGrid.isAck();
					low.setSelected(alertBoardGrid.isLow());
					medium.setSelected(alertBoardGrid.isMedium());
					high.setSelected(alertBoardGrid.isHigh());
					notRunning.setSelected(alertBoardGrid.isNotRunning());
					break;
				}
			}
			
			List<AlertGridItemType> alertGridItems = alertBoardGrid.getAlertGridItem();
			for(AlertGridItemType alertGridItem : alertGridItems) {
				for(LogicalEnvBean logicalEnvBean : logicalEnvBeans) {
					if(logicalEnvBean.getLogicalEnv().getName().equals(alertGridItem.getEnv())) {
						logicalEnvBean.setSelected(true);
					}
				}
			}
			initAlertList();
		} catch (Exception exc) {
			logger.error(exc);
		}			
	}
	
	public void rowSelectionListener(RowSelectorEvent event) throws PersistenceException {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		selectedLogicalEnvBean = logicalEnvBeans.get(rowId);
		initAlertList();
	}

	public AlertBoardGridType getAlertBoardGrid() {
		return alertBoardGrid;
	}

	public List<LogicalEnvBean> getLogicalEnvBeans() {
		return logicalEnvBeans;
	}

	public LogicalEnvBean getSelectedLogicalEnvBean() {
		return selectedLogicalEnvBean;
	}
	
	public boolean isUpOn() {
		return upOn;
	}

	public boolean isDownOn() {
		return downOn;
	}

	public boolean isAckOn() {
		return ackOn;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setUpOn(boolean upOn) {
		this.upOn = upOn;
	}

	public void setDownOn(boolean downOn) {
		this.downOn = downOn;
	}

	public void setAckOn(boolean ackOn) {
		this.ackOn = ackOn;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public States getLow() {
		return low;
	}

	public States getMedium() {
		return medium;
	}

	public States getHigh() {
		return high;
	}

	public States getNotRunning() {
		return notRunning;
	}
	
	public void handleUpOn(ValueChangeEvent evt) {
		upOn = (Boolean)evt.getNewValue();
	}
	
	public void handleDownOn(ValueChangeEvent evt) {
		downOn = (Boolean)evt.getNewValue();
	}
	
	public void handleAckOn(ValueChangeEvent evt) {
		ackOn = (Boolean)evt.getNewValue();
	}
	
	public void handleSelectAll(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		selectAll = (Boolean)evt.getNewValue();
		low.setSelected(selectAll);
		medium.setSelected(selectAll);
		high.setSelected(selectAll);
		notRunning.setSelected(selectAll);
	}
	
	public boolean isErrorAlertBoardGridTitleVisible() {
		return errorAlertBoardGridTitleVisible;
	}

	public void setErrorAlertBoardGridTitleVisible(
			boolean errorAlertBoardGridTitleVisible) {
		this.errorAlertBoardGridTitleVisible = errorAlertBoardGridTitleVisible;
	}

	public boolean isErrorAlertBoardGridEnvVisible() {
		return errorAlertBoardGridEnvVisible;
	}

	public void setErrorAlertBoardGridEnvVisible(
			boolean errorAlertBoardGridEnvVisible) {
		this.errorAlertBoardGridEnvVisible = errorAlertBoardGridEnvVisible;
	}

	public String getComponentTitle() {
		return componentTitle;
	}

	public void setComponentTitle(String componentTitle) {
		this.componentTitle = componentTitle;
	}

	public List<AlertDefinitionSelectionBean> getAlerts() {
		alerts = alertDefinitionSelector.getDisplayedBeans();
		return alerts;
	}
	
	public Collection<AlertModifierBean> getAlertBeans() {
		alertSelector.launchUpdateReferenceBeans();
		alertBoard = alertSelector.getDisplayedBeans();
		return alertBoard;
	}

	public List<AlertDefinitionSelectionBean> getAlertsToDisplay() {
		return alertsToDisplay;
	}

	private void initAlertList() {
//		alertsToDisplay = getAlerts();
		alertsToDisplay.clear();
		for(LogicalEnvBean env : logicalEnvBeans) {
			if(env.isSelected()) {
				for(AlertDefinitionSelectionBean alert : getAlerts()) {
					if(alert.getAlertDefinitionBean().getLogicalEnv().getName().equals(env.getLogicalEnv().getName()))
						alertsToDisplay.add(alert);
				}
			}
		}
	}

	public void displayBeansUpdated() {
	}
	
	public void save() {
		try {
			if(componentTitle.equals(null) || componentTitle.length()<1) {
				errorAlertBoardGridTitleVisible = true;
				errorAlertBoardGridEnvVisible = false;
				errorAlertBoardGridWorkflow = false;
				return;
			}
			
			int nSelected = 0;
			for(LogicalEnvBean env : logicalEnvBeans) {
				if(env.isSelected())
					nSelected++;
			}
			if(nSelected == 0) {
				errorAlertBoardGridTitleVisible = false;
				errorAlertBoardGridEnvVisible = true;
				errorAlertBoardGridWorkflow = false;
				return;
			}
			
			if(getRaisedAlertsToShow()) {
				errorAlertBoardGridWorkflow = true;
				errorAlertBoardGridEnvVisible = false;
				errorAlertBoardGridTitleVisible = false;
				return;
			}
			
			errorAlertBoardGridWorkflow = false;
			errorAlertBoardGridTitleVisible = false;
			errorAlertBoardGridEnvVisible = false;			
			
			ObjectFactory objFactory = new ObjectFactory();
			alertBoardGrid.getAlertGridItem().clear();
			
			alertBoardGrid.setTitle(componentTitle);
			alertBoardGrid.setUp(upOn);
			alertBoardGrid.setDown(downOn);
			alertBoardGrid.setAck(ackOn);
			alertBoardGrid.setLow(low.isSelected());
			alertBoardGrid.setMedium(medium.isSelected());
			alertBoardGrid.setHigh(high.isSelected());
			alertBoardGrid.setNotRunning(notRunning.isSelected());
			
			for(LogicalEnvBean env : logicalEnvBeans) {
				if(env.isSelected()) {
					AlertGridItemType alertGridItemType = objFactory.createAlertBoardGridTypeAlertGridItemType();
					alertGridItemType.setEnv(env.getLogicalEnv().getName());
					alertBoardGrid.getAlertGridItem().add(alertGridItemType);
				}
			}

			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
			
			if (alertBoardGrid.getId() == null || alertBoardGrid.getId().length() == 0) {
				factory.addNewAlertBoardGridComponent(env, type, title, alertBoardGrid);
			} else {
				factory.updateAlertBoardGridComponent(env, type, title, alertBoardGrid);
			}
			
			DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			bean.resetSelectedDashBoard();
		
		} catch (Exception exc) {
			logger.error("Error while saving Alert Board Grid details", exc);
		}
	}

	public boolean isSelectAllEnvs() {
		return selectAllEnvs;
	}

	public void setSelectAllEnvs(boolean selectAllEnvs) {
		this.selectAllEnvs = selectAllEnvs;
	}
	
	public void handleSelectAllEnvs(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		selectAllEnvs = (Boolean)evt.getNewValue();
		for(LogicalEnvBean logicalEnvBean : logicalEnvBeans) {
			logicalEnvBean.setSelected(selectAllEnvs);
		}
	}
	
	public void handleSelectedEnv(ValueChangeEvent evt) {
		LogicalEnvBean logicalEnvBeanSelected = (LogicalEnvBean)evt.getComponent().getAttributes().get("logicalEnvBean");
		if(logicalEnvBeanSelected != null) {
			for(LogicalEnvBean logicalEnvBean : logicalEnvBeans) {
				if(logicalEnvBean.equals(logicalEnvBeanSelected)) {
					logicalEnvBeanSelected.setSelected((Boolean)evt.getNewValue());
				}
			}	
		}
	}

	public String getDataTableStyle() {
		if(upOn) {
			if(errorAlertBoardGridEnvVisible)
				dataTableStyle = "text-align: center; border: 1px solid red; width: 296px; height: 200px;";
			else
				dataTableStyle = "text-align: center; border: 1px solid #336699; width: 296px; height: 200px;";
		}
		else {
			if(errorAlertBoardGridEnvVisible)
				dataTableStyle = "text-align: center; border: 1px solid red; width: 444px; height: 200px;";
			else
				dataTableStyle = "text-align: center; border: 1px solid #336699; width: 444px; height: 200px;";
		}
			
		return dataTableStyle;
	}

	public String getColumnWidthsStyle() {
		if(upOn)
			columnWidthsStyle = "220, 27";
		else
			columnWidthsStyle = "370, 27";
		return columnWidthsStyle;
	}

	public String getAlertBoardGridTitleStyle() {
		if(errorAlertBoardGridTitleVisible || titleError)
			return "border: 1px solid red;";
		else
			return "";
	}
	
	public boolean getRaisedAlertsToShow() {
		if(!upOn && !downOn && !ackOn)
			return true;
		else if (upOn && !low.isSelected() && !medium.isSelected() && !high.isSelected() && !notRunning.isSelected())
			return true;
		else
			return false;
	}

	public boolean isErrorAlertBoardGridWorkflow() {
		return errorAlertBoardGridWorkflow;
	}

	public void setErrorAlertBoardGridWorkflow(boolean errorAlertBoardGridWorkflow) {
		this.errorAlertBoardGridWorkflow = errorAlertBoardGridWorkflow;
	}

	public String getAlertBoardGridPanelWorkflowStyle() {
		if(errorAlertBoardGridWorkflow)
			return "border: 1px solid red; width: 144px; height: 200px;";
		else
			return "border: 1px solid #336699; width: 144px; height: 200px;";
	}

	public String getAlertBoardGridPanelStateStyle() {
		if(errorAlertBoardGridWorkflow)
			return "text-align: center; border: 1px solid red; width: 144px; height: 200px;";
		else
			return "text-align: center; border: 1px solid #336699; width: 144px; height: 200px;";
	}

	public void setTitleError(boolean titleError) {
		this.titleError = titleError;
	}
}
