package com.fsi.monitoring.dashboard.bean;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.bean.AlertBean;
import com.fsi.monitoring.alert.bean.AlertModifierBean;
import com.fsi.monitoring.alert.composite.AlertLeaf;
import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardGridComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponents;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.AccessControlBean;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class GlobalBoardBean
extends AccessControlBean {
	
	private static final Logger logger = Logger.getLogger(GlobalBoardBean.class);		
	
	// Components 
	private DashBoardComponents dashBoardComponents;
	
	private boolean onPreview = false;
	
	private boolean fromAlertBoard = true;
	
	private String alertItemName;
	private String alertItemNameTooLong;
	private String alertNameTooLong;
	
	private String alertBoardGridName;
	private String alertBoardGridNameTooLong;
	private String alertNameFromAlertBoardGridTooLong;
	
	// key = board, value = previous board
	private Map<DashBoardSummaryComponent,DashBoardSummaryComponent> route;
	
	private List<AlertModifierBean> alertModifierBeans;
	private AlertModifierBean selectedAlertModifierBean;
	
	private List<AlertModifierBean> alertsFromAlertBoardGrid;
	private AlertModifierBean selectedAlertFromAlertBoardGrid;
	
	//Selected Tab Index
	private int selectedIndex;
	
	private AlertPM alertPM;
	
	private void initBoard(String env,
						   String type,
						   String title) {
		try {
			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
			dashBoardComponents = factory.getDashBoardComponents(env,type,title);
			
		} catch (Exception exc) {
			logger.error(exc);
		}

	}
	
	public void navigate(ActionEvent action) {		
		// Access Perm protection
		if (isAuthorized(10,"board")) { 
			onPreview = !onPreview;
			String requestedEnv = null;
			String requestedType = null;
			String requestedTitle = null;
			
			DashBoardSummaryComponent from = null;
			
			DashBoardSummaryComponent to = 
				(DashBoardSummaryComponent)action.getComponent().getAttributes().get("to");
			
			if (to == null) {
				// this is a first navigation from the menu
				requestedEnv = (String)action.getComponent().getAttributes().get("boardEnv");
				requestedType = (String)action.getComponent().getAttributes().get("boardType");
				requestedTitle = (String)action.getComponent().getAttributes().get("boardTitle");
			} else {
				// this is a navigation from the navigation board component
				requestedEnv = to.getEnv();
				requestedType = to.getType();
				requestedTitle = to.getTitle();
				from = (DashBoardSummaryComponent)action.getComponent().getAttributes().get("from");
			}
			
			initBoard(requestedEnv, requestedType, requestedTitle);
			DashBoardSummaryComponent summary = dashBoardComponents.getSummary();
			String boardType = summary.getType();
			
			if (to == null) {
				// nav from menu
				to = (DashBoardSummaryComponent)dashBoardComponents.getSummary();
				route = new HashMap<DashBoardSummaryComponent,DashBoardSummaryComponent>();
				navigate(from, to);
			} else if (from != null) {
				// nav from the navigation board component 
				navigate(from, to);
			} else {
				// to != null && from == null
				// nav from the route, nothing to do.
			}
		}
	}
	
	/**
	 * This method is use to come back to the dashboard after viewing some details.
	 * Just check the security since the boars has already been initialized.
	 * @param action
	 */
	public void navigateBack(ActionEvent action) {
		String boardType = ((DashBoardSummaryComponent)dashBoardComponents.getSummary()).getType();
		// Access Perm protection
		if (isAuthorized(10,"board")) {  		
			return;
		}
	}	

	// Click on status name
	public void viewAlertBoardComponent(ActionEvent action) {
		fromAlertBoard = true;
		if (isAuthorized(11,"alertBoard") ) { 
			alertItemName = "Alert Item : ";
			AlertLeaf alertLeaf = (AlertLeaf)action.getComponent().getAttributes().get("alertItem");
			alertItemName = alertItemName + alertLeaf.getTitle();
			if (alertItemName.length() > 30) {
				alertItemNameTooLong = "";
				for (int i = 0; i < 30; i++) {
					alertItemNameTooLong = alertItemNameTooLong + alertItemName.charAt(i);
				}
				alertItemNameTooLong = alertItemNameTooLong + "...";
			}
			else {
				alertItemNameTooLong = alertItemName;
			}
			initAlertModifierBeans(alertLeaf);
			if (alertModifierBeans != null && alertModifierBeans.size()>0) {
				selectedAlertModifierBean = alertModifierBeans.get(0);
				selectedAlertModifierBean.setSelectedInDashboard(true);
			}
		}	
	}
	
	private void initAlertModifierBeans(AlertLeaf alertLeaf) {
		if (alertLeaf != null) {			
			alertModifierBeans = new ArrayList<AlertModifierBean>();			
			for (AlertBean alertBean : alertLeaf.getAllAlertBeans()) {
				AlertModifierBean alertModifierBean = new AlertModifierBean();
				alertModifierBean.setAlertPM(alertPM);
				alertModifierBean.setAlertBean(alertBean);
				alertModifierBeans.add(alertModifierBean);
			}
			sortAlertModifierTable();
		} 
	}
	
	//Click on the alert
	public void viewAlertBoardComponentOnAlertClick(ActionEvent action) {
		fromAlertBoard = true;
		if (isAuthorized(11,"alertBoard") ) {
			alertItemName = "Alert Item : ";
			AlertLeaf alertLeaf = (AlertLeaf)action.getComponent().getAttributes().get("alertItem");
			alertItemName = alertItemName + alertLeaf.getTitle();
			if (alertItemName.length() > 30) {
				alertItemNameTooLong = "";
				for (int i = 0; i < 30; i++) {
					alertItemNameTooLong = alertItemNameTooLong + alertItemName.charAt(i);
				}
				alertItemNameTooLong = alertItemNameTooLong + "...";
			}
			else {
				alertItemNameTooLong = alertItemName;
			}
			initAlertModifierBeans(alertLeaf);
			String alertItemSelected = (String)action.getComponent().getAttributes().get("alertItemSelected");
			for (AlertModifierBean alert : alertModifierBeans) {
				String name = alert.getAlertDefinitionBean().getAlertDefinition().getName();
				if (name.equals(alertItemSelected)) {
					selectedAlertModifierBean = alert;
					selectedAlertModifierBean.setSelectedInDashboard(true);
				}
			}
		}	
	}
	
	//Click on the alert from Alert Board Grid
	public void viewAlertFromAlertBoardGrid(ActionEvent action) {
		fromAlertBoard = false;
		if (isAuthorized(11,"alertBoard") ) {
			AlertBoardGridComponent alertBoardGrid = (AlertBoardGridComponent)action.getComponent().getAttributes().get("alertBoardGrid");
			alertsFromAlertBoardGrid = new ArrayList<AlertModifierBean>();
			alertsFromAlertBoardGrid = (List<AlertModifierBean>)alertBoardGrid.getAlertBeans();
			
			if (alertBoardGrid.getTitle().length() > 20) {
				alertBoardGridName = alertBoardGrid.getTitle();
				alertBoardGridNameTooLong = "";
				for (int i = 0; i < 20; i++) {
					alertBoardGridNameTooLong = alertBoardGridNameTooLong + alertBoardGrid.getTitle().charAt(i);
				}
				alertBoardGridNameTooLong = alertBoardGridNameTooLong + "...";
			}
			else {
				alertBoardGridName = alertBoardGrid.getTitle();
				alertBoardGridNameTooLong = alertBoardGrid.getTitle();
			}			
			
			String selectedAlert = (String)action.getComponent().getAttributes().get("selectedAlert");
			for(AlertModifierBean alertFromAlertBoardGrid : alertsFromAlertBoardGrid) {
				String name = alertFromAlertBoardGrid.getAlertDefinitionBean().getAlertDefinition().getName();
				if(name.equals(selectedAlert)) {
					selectedAlertFromAlertBoardGrid = alertFromAlertBoardGrid;
					selectedAlertFromAlertBoardGrid.setSelectedInDashboard(true);
				}
			}
		}
	}
	
	public Collection<AlertModifierBean> getAlertModifierBeans() {		
		sortAlertModifierTable();
		return alertModifierBeans;
	}
	
	private void sortAlertModifierTable() {
		if (alertModifierBeans != null && alertModifierBeans.size()>0) {
			Collections.sort(alertModifierBeans, new Comparator<AlertModifierBean>() {
				public int compare(AlertModifierBean o1, AlertModifierBean o2) {
					return (new Integer(o2.getAlertBean().getAlertState().getSeverity())).compareTo(new Integer(o1.getAlertBean().getAlertState().getSeverity()));
				}
			});				
		}
	}
	
	public DashBoardComponents getComponents() {
		return dashBoardComponents;
	}
	
	public Collection<DashBoardSummaryComponent> getNavigationRoute() {
		ArrayList<DashBoardSummaryComponent> res = new ArrayList<DashBoardSummaryComponent>();
		
		DashBoardSummaryComponent previous = route.get((DashBoardSummaryComponent)dashBoardComponents.getSummary());
		
		while (previous != null) {
			res.add(0, previous);
			previous = route.get(previous);
		}
		
		return res;
	}
	
	public Map<String,AlertBoardComponent> getAlertBoardMap() {
		Map<String,AlertBoardComponent> res = new HashMap<String,AlertBoardComponent>(0);
		DashBoardComponents dashboardComponents = getComponents();
		if (dashboardComponents != null) {
			Collection<AlertBoardComponent> components = dashboardComponents.getAlertBoardComponents();
			if (components != null) {
				res = new HashMap<String,AlertBoardComponent>(components.size());
			
				int i=0;
				for (AlertBoardComponent component : components) {
					res.put("alertBoard"+ (i++), component);
				}
			}
		}
		return res;
	}	
	
	private void navigate(DashBoardSummaryComponent from,
						  DashBoardSummaryComponent to) {
		route.put(to, from);
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {	
		selectedAlertModifierBean.setSelectedInDashboard(false);
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		
		int rowId = event.getRow();
		selectedAlertModifierBean = alertModifierBeans.get(rowId);
	}

	public void rowSelectionListenerForAlertBoardGrid(RowSelectorEvent event) {	
		selectedAlertFromAlertBoardGrid.setSelectedInDashboard(false);
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		
		int rowId = event.getRow();
		selectedAlertFromAlertBoardGrid = alertsFromAlertBoardGrid.get(rowId);
	}
	
	public AlertModifierBean getSelectedAlertModifierBean() {
		return selectedAlertModifierBean;
	}

	public boolean isOnPreview() {
		return onPreview;
	}

	public void setOnPreview(boolean onPreview) {
		this.onPreview = onPreview;
	}

	public String getAlertItemName() {
		return alertItemName;
	}

	public String getAlertItemNameTooLong() {
		return alertItemNameTooLong;
	}

	public String getAlertNameTooLong() {
		if(selectedAlertModifierBean != null) {
			alertNameTooLong = selectedAlertModifierBean.getAlertDefinitionBean().getAlertDefinition().getName();
			String tempString = alertNameTooLong;
			if (alertNameTooLong.length() > 50) {
				alertNameTooLong = "";
				for (int i = 0; i < 50; i++) {
					alertNameTooLong = alertNameTooLong + tempString.charAt(i);
				}
				alertNameTooLong = alertNameTooLong + "...";
			}
		}
		return alertNameTooLong;
	}

	public boolean isFromAlertBoard() {
		return fromAlertBoard;
	}

	public List<AlertModifierBean> getAlertsFromAlertBoardGrid() {
		sortAlertsFromAlertBoardGrid();
		return alertsFromAlertBoardGrid;
	}
	
	private void sortAlertsFromAlertBoardGrid() {
		if (alertsFromAlertBoardGrid != null && alertsFromAlertBoardGrid.size()>0) {
			Collections.sort(alertsFromAlertBoardGrid, new Comparator<AlertModifierBean>() {
				public int compare(AlertModifierBean o1, AlertModifierBean o2) {
					return (new Integer(o2.getAlertBean().getAlertState().getSeverity())).compareTo(new Integer(o1.getAlertBean().getAlertState().getSeverity()));
				}
			});				
		}
	}

	public AlertModifierBean getSelectedAlertFromAlertBoardGrid() {
		return selectedAlertFromAlertBoardGrid;
	}

	public String getAlertBoardGridName() {
		return alertBoardGridName;
	}

	public String getAlertBoardGridNameTooLong() {
		return alertBoardGridNameTooLong;
	}

	public String getAlertNameFromAlertBoardGridTooLong() {
		alertNameFromAlertBoardGridTooLong = selectedAlertFromAlertBoardGrid.getAlertDefinitionBean().getAlertDefinition().getName();
		String tempString = alertNameFromAlertBoardGridTooLong;
		if (alertNameFromAlertBoardGridTooLong.length() > 45) {
			alertNameFromAlertBoardGridTooLong = "";
			for (int i = 0; i < 45; i++) {
				alertNameFromAlertBoardGridTooLong = alertNameFromAlertBoardGridTooLong + tempString.charAt(i);
			}
			alertNameFromAlertBoardGridTooLong = alertNameFromAlertBoardGridTooLong + "...";
		}
		return alertNameFromAlertBoardGridTooLong;
	}
	            
	public void acknowledgeAllAlertsFromAlertBoard(ActionEvent action) {
		for(AlertModifierBean alertModifierBean : alertModifierBeans) {
			if(!alertModifierBean.getAlertBean().getAlert().isAcknowledged())
				alertModifierBean.acknowledge(action);
		}
	}
	
	public void acknowledgeAllAlertsFromAlertBoardGrid(ActionEvent action) {
		for(AlertModifierBean alertFromAlertBoardGrid : alertsFromAlertBoardGrid) {
			if(!alertFromAlertBoardGrid.getAlertBean().getAlert().isAcknowledged())
				alertFromAlertBoardGrid.acknowledge(action);
		}
	}

	public void setAlertPM(AlertPM alertPM) {
		this.alertPM = alertPM;
	}
}
