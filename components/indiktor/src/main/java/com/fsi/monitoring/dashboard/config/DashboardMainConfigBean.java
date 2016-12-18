package com.fsi.monitoring.dashboard.config;



import generated.dashboard.config.schema.NavigationBoardType.NavigationItemType;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponents;
import com.fsi.monitoring.datamodel.bean.StaticDataDefinitionBean;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class DashboardMainConfigBean 
extends SortableList {

	private static final long serialVersionUID = 2767684910418822131L;

	private static final Logger logger = Logger.getLogger(DashboardMainConfigBean.class);
	
	public static final String envColumnName = "Environment";
	public static final String typeColumnName = "Type";
	public static final String titleColumnName = "Title";
	
	private NavigationItemConfig navigationItemConfig;
    
    private String componentType;
    private SelectItem[] componentTypeItems;
    
//    private boolean hasBeenSelected = false;
    private Boolean sameDashboardTitle = false;
    
    private boolean rendererFilter = false;
    
    private SelectItem[] dashboardEnvItems;
          
//    private String componentId;
//    private SelectItem[] componentItems;
    
    private List<DashBoardSummaryComponent> dashboards;
    private List<DashBoardSummaryComponent> dashboardsSelected;
    private ArrayList<DashBoardSummaryComponent> dashboardsList;
    
    private String newEnv;
    private String newType;
    private String newTitle;
    private boolean newEnvWrongFormat = false;
    private boolean newTypeWrongFormat = false;
    private boolean newTitleMandatory = false;
    private boolean newTitleAlreadyExists = false;
    private boolean newTitleWrongFormat = false;
    private boolean newTitleTooLong = false;
    private String newEnvStyle = "width: 200px;";
    private String newTypeStyle = "width: 200px;";
    private String newTitleStyle = "width: 200px;";
    
    private boolean showComponentConfiguration = false;
    private boolean update;
    
    private DashBoardSummaryComponent dashBoardSummaryComponent = null;
    
    private boolean rendererPreviewButton;
    private boolean rendererNewDashboard = false;
    
    private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private String rowClass;
	private String colors;
	private boolean masterDashboardSelected;
	
	int numberDashboardsSelected = 0;
    
    private SelectItem[] allTypeItems;
    {
    	componentTypeItems = new SelectItem[6];
    	componentTypeItems[0] = new SelectItem("navigationBoard", "NavigationBoard");
    	componentTypeItems[1] = new SelectItem("alertBoard", "AlertBoard");
    	componentTypeItems[2] = new SelectItem("infoBoard", "InfoBoard");
    	componentTypeItems[3] = new SelectItem("categoryChart", "CategoryChart");
    	componentTypeItems[4] = new SelectItem("definitionChart", "DefinitionChart");
    	componentTypeItems[5] = new SelectItem("thread", "Thread");
    	
    	componentType = "navigationBoard";
    }
    
    public DashboardMainConfigBean() {
    	super(envColumnName);
    }
    
    public void editDashboard(ActionEvent action) {
		if (!isAuthorized(81,"dashboardSelectMainConfig")) {
			return;
		}
		DashBoardSummaryComponent dashboardSelected = (DashBoardSummaryComponent)action.getComponent().getAttributes().get("Dashboard");
		DashboardSelectMainConfigBean dashboardSelectMainConfigBean = (DashboardSelectMainConfigBean)FacesUtils.getManagedBean("dashboardSelectMainConfigBean");
		dashboardSelectMainConfigBean.initUpdate(dashboardSelected);
		dashBoardSummaryComponent = dashboardSelected;
		
//		numberDashboardsSelected = 0;
//		for (DashBoardSummaryComponent dashboard : dashboards) {
//			if (dashboard.isSelected())
//				numberDashboardsSelected++;
//		}
//		
//		if (numberDashboardsSelected < 2)
//			if (numberDashboardsSelected == 1){
//				DashboardSelectMainConfigBean dashboardSelectMainConfigBean = (DashboardSelectMainConfigBean)FacesUtils.getManagedBean("dashboardSelectMainConfigBean");
//				dashboardSelectMainConfigBean.initUpdate(dashBoardSummaryComponent);
//			}
//			else{
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No dashboard has been selected");
//				setAction("dashboardMainConfig");
//			}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one dashboard to edit");
//			setAction("dashboardMainConfig");
//		}
	}	
    
	public void initUpdate(ActionEvent action) {
		if (isAuthorized(81,"dashboardMainConfig") || isAuthorized(82,"dashboardMainConfig") || isAuthorized(86,"dashboardMainConfig")) { 
			dashBoardSummaryComponent = null;
			masterDashboardSelected = false;
			loadDashboards();
		}
		
		selectAll = false;
		dashboardsSelected = new ArrayList<DashBoardSummaryComponent>();
	}	
	
	public void addDashboard(ActionEvent action) {
		if (!isAuthorized(86,"dashboardMainConfig")) {
			setAccessDenied();
			return;
		}
			showComponentConfiguration = false;
			update = false;
			init();
			initAllTypeItems();
			rendererNewDashboard = true;
	}
	
	public void pageChangeListener(ActionEvent action) {
		for(DashBoardSummaryComponent dashboard : dashboards) {
			if(dashboard.isSelected()) {
				dashboard.setSelected(false);
			}
		}
		selectAll = false;
		dashboardsSelected = new ArrayList<DashBoardSummaryComponent>();
	}
	
	public void init() {
		try {
			if (navigationItemConfig == null) {
			
				ObjectFactory objFactory = new ObjectFactory();
				NavigationItemType itemType = objFactory.createNavigationBoardTypeNavigationItemType();
			
				navigationItemConfig = new NavigationItemConfig(itemType);
			}
			
			navigationItemConfig.init();
			
		} catch (Exception exc) {
			logger.error(exc);
		}
			
		newEnv = null;
		newType = null;
		newTitle = null;
	}
	
	private void loadDashboards() {
		try {
			if (navigationItemConfig == null) {
			
				ObjectFactory objFactory = new ObjectFactory();
				NavigationItemType itemType = objFactory.createNavigationBoardTypeNavigationItemType();
			
				navigationItemConfig = new NavigationItemConfig(itemType);
			}
			
			navigationItemConfig.init();
			
		} catch (Exception exc) {
			logger.error(exc);
		}
			
		newEnv = null;
		newType = null;
		newTitle = null;
		
		dashboards = new ArrayList<DashBoardSummaryComponent>();
		if (navigationItemConfig != null) {
			for (DashBoardSummaryComponent dashboard : navigationItemConfig.getSummaries()) {
				dashboard.setSelected(false);
				dashboards.add(dashboard);
			}
		}
	}
	
	public List<DashBoardSummaryComponent> getDashboards() {
		if (dashboards != null && dashboards.size()>0)
			sort();
			dashboardsList = new ArrayList<DashBoardSummaryComponent>();
			for (DashBoardSummaryComponent dashboard : dashboards) {
				if (isMasterDashboard(dashboard))
					dashboardsList.add(dashboard);
			}
			for (DashBoardSummaryComponent dashboard : dashboards) {
				if (!isMasterDashboard(dashboard))
					dashboardsList.add(dashboard);
			}
			dashboards.clear();
			for (DashBoardSummaryComponent dashboard : dashboardsList) {
					dashboards.add(dashboard);
			}
		return dashboards;
	}
	
	public NavigationItemConfig getNavItem() {
		return navigationItemConfig;
	}
	
	public void createNewDashBoard(ActionEvent action) {
		if (!isAuthorized(86,"dashboardMainConfig") ) {
			setAccessDenied();
			return;
		}
		sameDashboardTitle = false;
		RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();			
		
		if (newEnv != null && newEnv.trim().length()>0) {
			navigationItemConfig.getItem().setEnv(newEnv);
		}
		else {
			newEnv = navigationItemConfig.getEnvironment();
		}
		
		if (newType != null && newType.trim().length()>0) {
			navigationItemConfig.getItem().setType(newType);
		}
		else {
			newType = navigationItemConfig.getType();
		}
		
		if (newTitle != null && newTitle.trim().length()>0) {
			if(newTitle.length() > 40) {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.init();
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("The name of a dashboard cannot have more than 40 characters, please choose another title");
				newEnvStyle = "width: 200px;";
				newTitleStyle = "width: 200px; border: 1px solid red;";
				newTypeStyle = "width: 200px;";
				newEnvWrongFormat = false;
				newTypeWrongFormat = false;
				newTitleAlreadyExists = false;
				newTitleMandatory = false;
				newTitleTooLong = true;
				newTitleWrongFormat = false;
				return;
			}
			if(!StringUtils.isAlphanumericSpace(newEnv) || !StringUtils.isAlphanumericSpace(newType) || !StringUtils.isAlphanumericSpace(newTitle)) {
				if(!StringUtils.isAlphanumericSpace(newEnv)) {
					newEnvStyle = "width: 200px; border: 1px solid red;";
					newEnvWrongFormat = true;
				}
				else {
					newEnvStyle = "width: 200px;";
					newEnvWrongFormat = false;					
				}
				if(!StringUtils.isAlphanumericSpace(newType)) {
					newTypeStyle = "width: 200px; border: 1px solid red;";
					newTypeWrongFormat = true;
				}
				else {
					newTypeStyle = "width: 200px;";
					newTypeWrongFormat = false;				
				}
				if(!StringUtils.isAlphanumericSpace(newTitle)) {
					newTitleStyle = "width: 200px; border: 1px solid red;";
					newTitleWrongFormat = true;
				}
				else {
					newTitleStyle = "width: 200px;";
					newTitleWrongFormat = false;					
				}
				newTitleAlreadyExists = false;
				newTitleMandatory = false;
				newTitleTooLong = false;
				return;
			}
			
			
			for (DashBoardSummaryComponent dashBoard : dashboards) {
				if (dashBoard.getEnv().equalsIgnoreCase(newEnv) && dashBoard.getType().equalsIgnoreCase(newType) && dashBoard.getTitle().equalsIgnoreCase(newTitle)) {
					sameDashboardTitle = true;
					break;
				}
			}	
			if (!sameDashboardTitle){
				navigationItemConfig.getItem().setTitle(newTitle);
				   
				factory.createNewDashBoard(newEnv, newType, newTitle);				
				init();
				resetSelectedDashBoard();
				loadDashboards();
				showComponentConfiguration = true;
				sameDashboardTitle = false;
				rendererNewDashboard = false;
				newEnvStyle = "width: 200px;";
				newTitleStyle = "width: 200px;";
				newTypeStyle = "width: 200px;";
				newEnvWrongFormat = false;
				newTypeWrongFormat = false;
				newTitleAlreadyExists = false;
				newTitleMandatory = false;
				newTitleTooLong = false;
				newTitleWrongFormat = false;
			}
			else {
//				sameDashboardTitle = false;
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.init();
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("This dashboard already exist, please choose another title");
				newEnvStyle = "width: 200px; border: 1px solid red;";
				newTitleStyle = "width: 200px; border: 1px solid red;";
				newTypeStyle = "width: 200px; border: 1px solid red;";
				newEnvWrongFormat = false;
				newTypeWrongFormat = false;
				newTitleAlreadyExists = true;
				newTitleMandatory = false;
				newTitleTooLong = false;
				newTitleWrongFormat = false;
				return;
			}
		}
		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.init();
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Missing Title");
			newEnvStyle = "width: 200px;";
			newTitleStyle = "width: 200px; border: 1px solid red;";
			newTypeStyle = "width: 200px;";
			newEnvWrongFormat = false;
			newTypeWrongFormat = false;
			newTitleAlreadyExists = false;
			newTitleMandatory = true;
			newTitleTooLong = false;
			newTitleWrongFormat = false;
			return;
		}
		
		rendererNewDashboard = false;
		selectAll = false;
	}
	
	private void initAllTypeItems() {
		try {
			Collection<String> types = new HashSet<String>();
			
			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
	
			Collection<DashBoardComponents> components = factory.getDashBoardComponents();
			for (DashBoardComponents component : components) {
				DashBoardSummaryComponent summary = component.getSummary();
				types.add(summary.getType());
			}
			
			allTypeItems = new SelectItem[types.size()];
			
			int i=0;
			for(String type : types) {		
				allTypeItems[i++] = new SelectItem(type, type);
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}	
	
	public SelectItem[] getAllTypeItems() {	
		return allTypeItems;
	}		
	
	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {}

	public SelectItem[] getComponentTypeItems() {
		return componentTypeItems;
	}
	
	public void onChangeComponentType(ValueChangeEvent evnt) {
		componentType = (String)evnt.getNewValue();
	}
	
	public DashBoardComponents getComponents() {
		return navigationItemConfig.getComponents();
	}
	
	public void setComponentId(String componentId) {}

	public String getNewEnv() {
		return newEnv;
	}
	
	public void setNewEnv(String newEnv) {
		this.newEnv = newEnv;
	}
	
	public String getNewType() {
		return newType;
	}
	
	public void setNewType(String type) {
		this.newType = type;
	}
	
	public String getNewTitle() {
		return newTitle;
	}
	
	public void setNewTitle(String newTitle) {
		this.newTitle = newTitle;
	}
	
	public boolean isRenderUpdate() {
		return true;
	}
	
	public void resetSelectedDashBoard() {
		navigationItemConfig.updatedSelectedDashBoard();
	}
	
	public void deleteDashBoard(ActionEvent act) {
		if (!isAuthorized(82,"dashboardMainConfig") ) {
			setAccessDenied();
			return;
		}
		DashBoardSummaryComponent dashboardSelected = (DashBoardSummaryComponent)act.getComponent().getAttributes().get("Dashboard");
		if (isMasterDashboard(dashboardSelected)){
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("The master dashboard cannot be deleted!");
			return;
		}
		RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
		factory.deleteDashBoard(dashboardSelected.getEnv(),
								dashboardSelected.getType(),
								dashboardSelected.getTitle());
		dashboardsSelected = new ArrayList<DashBoardSummaryComponent>();
		selectAll = false;
		init();
		loadDashboards();
	}
	
	public void deleteSelectedDashBoards(ActionEvent act) {
		if (!isAuthorized(82,"dashboardMainConfig") ) {
			setAccessDenied();
			return;
		}
		
		numberDashboardsSelected = 0;
		for (DashBoardSummaryComponent dashboard : dashboards) {
			if (dashboard.isSelected())
				numberDashboardsSelected++;
		}
		
		if (numberDashboardsSelected > 0) {			
			for (DashBoardSummaryComponent dashboard : dashboards) {
				if (dashboard.isSelected()) {
					if (isMasterDashboard(dashboard)){
						ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
						error.init();
						error.setRendered(true);
						error.setModal(true);
						error.setType(ErrorMessageBean.WARNING);
						error.addMessage("The master dashboard cannot be deleted!");
						return;
					}
					RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
					factory.deleteDashBoard(dashboard.getEnv(),
											dashboard.getType(),
											dashboard.getTitle());
				}
			}
			init();
			loadDashboards();
			dashBoardSummaryComponent = null;
			dashboardsSelected = new ArrayList<DashBoardSummaryComponent>();
			selectAll = false;
		}
		else{
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No dashboard has been selected");
		}
		
		
//		if (dashBoardSummaryComponent != null) {
//			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
//			factory.deleteDashBoard(dashBoardSummaryComponent.getEnv(),
//									dashBoardSummaryComponent.getType(),
//									dashBoardSummaryComponent.getTitle());
//			init();
//			loadDashboards();
//			dashBoardSummaryComponent = null;
//		}
//		else{
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("No dashboard has been selected");
//		}
	}
	
	private boolean isMasterDashboard(DashBoardSummaryComponent dashBoardSummaryComponent) {
		if (dashBoardSummaryComponent.getEnv().equals("global") && dashBoardSummaryComponent.getType().equals("alertBoard") && dashBoardSummaryComponent.getTitle().equals("DashBoard"))
			dashBoardSummaryComponent.setMasterDashboards(true);
		return dashBoardSummaryComponent.isMasterDashboards();
	}

	public boolean isShowComponentConfiguration() {
		return showComponentConfiguration;
	}

	public boolean isUpdate() {
		return update;
	}	
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public boolean isRendererPreviewButton() {
		if (!isAuthorized(10,"board")) {
			return false;
		}
		return true;
	}

	public boolean isRendererNewDashboard() {
		return rendererNewDashboard;
	}

	public void setRendererNewDashboard(boolean rendererNewDashboard) {
		this.rendererNewDashboard = rendererNewDashboard;
	}
	
	public void closeNewDashboardPopup(ActionEvent event) {
		rendererNewDashboard = false;
		newEnvStyle = "width: 200px;";
		newTitleStyle = "width: 200px;";
		newTypeStyle = "width: 200px;";
		newEnvWrongFormat = false;
		newTypeWrongFormat = false;
		newTitleAlreadyExists = false;
		newTitleMandatory = false;
		newTitleTooLong = false;
		newTitleWrongFormat = false;
		return;
	}
	
	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}
	
	public boolean isPaginationVisible() {
		if (getDashboards().size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {	
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
//		hasBeenSelected = true;
		int rowId = event.getRow();
		dashBoardSummaryComponent = dashboards.get(rowId);
		if (isMasterDashboard(dashBoardSummaryComponent))
			masterDashboardSelected = true;
		else
			masterDashboardSelected = false;
	}

	public DashBoardSummaryComponent getDashBoardSummaryComponent() {
		return dashBoardSummaryComponent;
	}

	public void setDashBoardSummaryComponent(DashBoardSummaryComponent dashBoardSummaryComponent) {
		this.dashBoardSummaryComponent = dashBoardSummaryComponent;
	}
	
	public String getDeleteMessage() {
		numberDashboardsSelected =0;
		for (DashBoardSummaryComponent dashboard : dashboards) {
			if (dashboard.isSelected())
				numberDashboardsSelected++;
		}
		String message = "No dashboard selected";
		if (numberDashboardsSelected == 1) {
			for (DashBoardSummaryComponent dashboard : dashboards) {
				if (dashboard.isSelected())
					message = "Are you sure to delete this dashboard : " + dashboard.getEnv()
							  + " > " + dashboard.getType() + " > " + dashboard.getTitle();
			}	
			return message;
		}
		else {	
			message = "Are you sure to delete these " + numberDashboardsSelected + " dashboards?";		
			return message;
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
	
	public SelectItem[] getDashBoardEnvItems (){
		Collection<String> envs = new HashSet<String>();

		for (DashBoardSummaryComponent summary : dashboards) {
			envs.add(summary.getEnv());
		}
		
		dashboardEnvItems = new SelectItem[envs.size()];
		return dashboardEnvItems;
	}
	
	public void onChangeDashboardEnv(){
		
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(dashboards, new Comparator<DashBoardSummaryComponent>() {
			public int compare(DashBoardSummaryComponent o1, DashBoardSummaryComponent o2) {
				int res = 0;
				try {
					if (getEnvColumnName().equals(getSortColumnName())) {
						res = ascending ? o1. getEnv().toLowerCase().compareTo(o2.getEnv().toLowerCase()) :  o2.getEnv().toLowerCase().compareTo(o1.getEnv().toLowerCase());
					}
					else if (getTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getType().toLowerCase().compareTo(o2.getType().toLowerCase()) :  o2.getType().toLowerCase().compareTo(o1.getType().toLowerCase());
					}
					else if (getTitleColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase()) :  o2.getTitle().toLowerCase().compareTo(o1.getTitle().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});
	}

	public String getEnvColumnName() {
		return envColumnName;
	}

	public String getTypeColumnName() {
		return typeColumnName;
	}

	public String getTitleColumnName() {
		return titleColumnName;
	}
	
	public String getColors() {
		StringBuffer colorBuffer = new StringBuffer();	
		for (DashBoardSummaryComponent dashboard : dashboards) {
			colorBuffer.append(getRowClass(dashboard));
			colorBuffer.append(",");
		}
		colors = colorBuffer.toString();
		return colors;
	}
	
	public String getRowClass(DashBoardSummaryComponent dashboard) {
		if (isMasterDashboard(dashboard) && masterDashboardSelected == false) {
			rowClass = "masterDashboard";
		}
		else {
			rowClass = "iceDatTblRow1, iceDatTblRow2";
		}
		return rowClass;
	}
	
	public void handleSelectedDashboard(ValueChangeEvent event) {
		DashBoardSummaryComponent dashboardSelected = (DashBoardSummaryComponent)event.getComponent().getAttributes().get("Dashboard");
		if(dashboardSelected != null) {
			for(DashBoardSummaryComponent dashboard : dashboards) {
				if(dashboard.equals(dashboardSelected)) {
					dashboard.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						dashboardsSelected.add(dashboardSelected);
					else
						dashboardsSelected.remove(dashboardSelected);
				}
			}
		}
	}
	
	public void handleSelectAllDashboards(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		dashboardsSelected.clear();
		for(DashBoardSummaryComponent dashboard : dashboards) {
			if(!isMasterDashboard(dashboard)) {
				dashboard.setSelected((Boolean)evt.getNewValue());
				if((Boolean)evt.getNewValue())
					dashboardsSelected.add(dashboard);
			}
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getDashboardsSelected() {
		int size = dashboardsSelected.size();
		return size;
	}

	public void setDashboardsSelected(
			List<DashBoardSummaryComponent> dashboardsSelected) {
		this.dashboardsSelected = dashboardsSelected;
	}
	
	//-------------Control and style---------------//

	public boolean isNewEnvWrongFormat() {
		return newEnvWrongFormat;
	}

	public boolean isNewTypeWrongFormat() {
		return newTypeWrongFormat;
	}

	public boolean isNewTitleMandatory() {
		return newTitleMandatory;
	}

	public boolean isNewTitleAlreadyExists() {
		return newTitleAlreadyExists;
	}

	public boolean isNewTitleWrongFormat() {
		return newTitleWrongFormat;
	}

	public String getNewEnvStyle() {
		return newEnvStyle;
	}

	public String getNewTypeStyle() {
		return newTypeStyle;
	}

	public String getNewTitleStyle() {
		return newTitleStyle;
	}

	public boolean isNewTitleTooLong() {
		return newTitleTooLong;
	}
}
