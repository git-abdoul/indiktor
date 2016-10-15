package com.fsi.monitoring.dashboard.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.fsi.monitoring.alert.config.AllAlertDefinitionBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.NavigationBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardGridComponent;
import com.fsi.monitoring.dashboard.component.batch.BatchBoardComponent;
import com.fsi.monitoring.dashboard.component.chart.IkrChartComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponents;
import com.fsi.monitoring.dashboard.component.info.InfoComponent;
import com.fsi.monitoring.dashboard.thread.ThreadAnalysisBean;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class DashboardSelectMainConfigBean
extends SortableList {
	
	private DashBoardSummaryComponent dashBoardSummaryComponent;
	private DashBoardComponents dashboard;

	private static final String typeColumnName = "Type";
	private static final String titleColumnName = "Title";
	
	private List<DashBoardComponent> components;
	private List<DashBoardComponent> componentsSelected;
	
	private DashBoardComponent selectedComponent;
	
	private DashBoardComponent selectedAddComponent;
	
//	private boolean hasBeenSelected = false;
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private boolean rendererAddComponent = false;
	private boolean rendererUpdateComponent = false;
	private boolean componentTypeVisible = false;
	private boolean componentConfigVisible = false;
	private boolean backComponentConfigVisible = false;
	private boolean addButtonVisible = false;
	private boolean deleteButtonVisible = false;
	private boolean addMetricPanelVisible = false;
	private boolean addDashboardPanelVisible = false;
	private boolean addAlertBoardItemPanelVisible = false;
	private boolean addVisible = false;
	private boolean updatePopupOpen = false;
	
	private boolean titleAlreadyExists = false;
	
//	public boolean multipleDeletionActive = false;
	int numberComponentsSelected = 0;
	
	private String componentType = DashboardComponentTypeLibrary.defaultComponentType;
	
    private SelectItem[] componentTypeItems;
    {
    	componentTypeItems = new SelectItem[DashboardComponentTypeLibrary.getLibrary().size()];    	
    	int i = 0;
    	for (String componentType : DashboardComponentTypeLibrary.getLibrary().keySet())  {
    		componentTypeItems[i++] = new SelectItem(componentType, DashboardComponentTypeLibrary.getComponentTypeLabel(componentType));
    	}
    }
    
    public DashboardSelectMainConfigBean() {
		super(typeColumnName);
	}
    
	private void reloadComponents() {
		RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
		dashboard = factory.getDashBoardComponents(dashBoardSummaryComponent.getEnv(),
													dashBoardSummaryComponent.getType(),
													dashBoardSummaryComponent.getTitle());
		
		components = new ArrayList<DashBoardComponent>();
		
		Collection<AlertBoardComponent> alertBoardComponents = dashboard.getAlertBoardComponents();
		if (alertBoardComponents != null && alertBoardComponents.size() > 0) {
			for (AlertBoardComponent component : alertBoardComponents) {
				component.setSelected(false);
				components.add(component);
			}
		}
		Collection<AlertBoardGridComponent> alertBoardGridComponents = dashboard.getAlertBoardGridComponents();
		if (alertBoardGridComponents != null && alertBoardGridComponents.size() > 0) {
			for (AlertBoardGridComponent component : alertBoardGridComponents) {
				component.setSelected(false);
				components.add(component);
			}
		}
		Collection<NavigationBoardComponent> navigationBoardComponents = dashboard.getNavigationBoardComponents();
		if (navigationBoardComponents != null && navigationBoardComponents.size() > 0) {
			for (NavigationBoardComponent component : navigationBoardComponents) {
				component.setSelected(false);
				components.add(component);
			}
		}
		
		Collection<InfoComponent> infoBoardComponents = dashboard.getInfoBoardComponents();
		if (infoBoardComponents != null && infoBoardComponents.size() > 0) {
			for (InfoComponent component : infoBoardComponents) {
				component.setSelected(false);
				components.add(component);
			}
		}
		
		Collection<BatchBoardComponent> batchBoardComponents = dashboard.getBatchBoardComponents();
		if (batchBoardComponents != null && batchBoardComponents.size() > 0) {
			for (BatchBoardComponent component : batchBoardComponents) {
				component.setSelected(false);
				components.add(component);
			}
		}
		
//		Collection<IkrCategoryChartComponent> categoryChartComponents = dashboard.getIkrCategoryChartComponents();
//		if (categoryChartComponents != null && categoryChartComponents.size() > 0)
//			components.addAll(categoryChartComponents);
		
		Collection<IkrChartComponent> definitionChartComponents = dashboard.getIkrChartComponents();
		if (definitionChartComponents != null && definitionChartComponents.size() > 0) {
			for (IkrChartComponent component : definitionChartComponents) {
				component.setSelected(false);
				components.add(component);
			}	
		}
		
		ThreadAnalysisBean thread = dashboard.getThreadAnalysisComponent();
		if (thread != null) {
			thread.setSelected(false);
			components.add(thread);		
		}
	}

	public void initUpdate(DashBoardSummaryComponent dashBoardSummaryComponent) {				
		this.dashBoardSummaryComponent = dashBoardSummaryComponent;
		selectedComponent = null;
		componentsSelected = new ArrayList<DashBoardComponent>();
		selectAll = false;
		reloadComponents();
	}
	
	public void pageChangeListener(ActionEvent action) {
		for(DashBoardComponent component : components) {
			if(component.isSelected()) {
				component.setSelected(false);
			}
		}
		selectedComponent = null;
		componentsSelected = new ArrayList<DashBoardComponent>();
		selectAll = false;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {	
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		
		int rowId = event.getRow();
		selectedComponent = components.get(rowId);		
	}

	public List<DashBoardComponent> getComponentElements() {
		if (components != null && components.size()>0)
			sort();
				
		return components;
	}
	
	public void previewDashboard(ActionEvent event) {
		
	}
	
	public void saveComponent(ActionEvent event) {
		titleAlreadyExists = false;
		if (componentType!=null && componentType.length()>0) {
			if (componentType.equalsIgnoreCase("alertBoard")) {
				AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
				if (alertBoardConfigBean.getAlertBoard().getId() != null && alertBoardConfigBean.getAlertBoard().getId().length() > 0) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						String componentIdTest = component.getComponentId();
						if (componentTypeTest.equalsIgnoreCase("alertBoard") && !componentIdTest.equalsIgnoreCase(alertBoardConfigBean.getAlertBoard().getId())) {
							if(alertBoardConfigBean.getAlertBoard().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								alertBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
							}
						}
					}	
				}
				else if (!updatePopupOpen) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						if (componentTypeTest.equalsIgnoreCase("alertBoard") 
								&& alertBoardConfigBean.getAlertBoard().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								alertBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
						}
					}	
				}
				if (alertBoardConfigBean.getAlertBoard().getTitle().trim().length() == 0) {
					alertBoardConfigBean.setErrorAlertBoardTitleVisible(true);
					alertBoardConfigBean.setErrorAlertListVisible(false);
					alertBoardConfigBean.setErrorItemListVisible(false);
					alertBoardConfigBean.setErrorItemTitleVisible(false);
					alertBoardConfigBean.setTitleError(false);
					return;
				}
				
				if (alertBoardConfigBean.getTableRecordManager().getFilesGroupRecordBeans().isEmpty()) {
					alertBoardConfigBean.setErrorAlertBoardTitleVisible(false);
					alertBoardConfigBean.setErrorItemTitleVisible(false);
					alertBoardConfigBean.setErrorAlertListVisible(false);
					alertBoardConfigBean.setErrorItemListVisible(true);
					alertBoardConfigBean.setTitleError(false);
					return;
				}
				alertBoardConfigBean.save();

				if (alertBoardConfigBean.isAlertInListTest() == false) {
					alertBoardConfigBean.setErrorAlertBoardTitleVisible(false);
					alertBoardConfigBean.setErrorItemTitleVisible(false);
					alertBoardConfigBean.setErrorItemListVisible(false);
					alertBoardConfigBean.setErrorAlertListVisible(true);
					alertBoardConfigBean.setTitleError(false);
					return;
				}
				
				alertBoardConfigBean.setAllErrorFalse();
				alertBoardConfigBean.setAlertInListTest(false);
				alertBoardConfigBean.setAlertBoardItemConfigVisible(false);
				addMetricPanelVisible = false;
				AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
				if(allAlertDefinitionBean != null)
					allAlertDefinitionBean.setSelectAllInAlertBoardComponent(false);
			} else if (componentType.equalsIgnoreCase("navigationBoard")) {
				NavigationBoardConfigBean navigationBoardConfigBean = (NavigationBoardConfigBean)FacesUtils.getManagedBean("navigationBoardConfigBean");	
				if (navigationBoardConfigBean.getNavigationBoard().getId() != null && navigationBoardConfigBean.getNavigationBoard().getId().length() > 0) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						String componentIdTest = component.getComponentId();
						if (componentTypeTest.equalsIgnoreCase("navigationBoard") && !componentIdTest.equalsIgnoreCase(navigationBoardConfigBean.getNavigationBoard().getId())) {
							if(navigationBoardConfigBean.getNavigationBoard().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								navigationBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
							}
						}
					}
				}
				else if (!updatePopupOpen) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						if (componentTypeTest.equalsIgnoreCase("navigationBoard") 
								&& navigationBoardConfigBean.getNavigationBoard().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								navigationBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
						}
					}	
				}
				
				navigationBoardConfigBean.save();
				if (navigationBoardConfigBean.isErrorDashboardListVisible() == true || navigationBoardConfigBean.isErrorNavigationBoardTitleVisible() == true)
					return;
				addDashboardPanelVisible = false;
			} else if (componentType.equalsIgnoreCase("definitionChart")) {
				DefinitionChartConfigBean definitionChartConfigBean = (DefinitionChartConfigBean)FacesUtils.getManagedBean("definitionChartConfigBean");
				if (definitionChartConfigBean.getDefinitionChart().getId() != null && definitionChartConfigBean.getDefinitionChart().getId().length() > 0) {	
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						String componentIdTest = component.getComponentId();
						if (componentTypeTest.equalsIgnoreCase("definitionChart") && !componentIdTest.equalsIgnoreCase(definitionChartConfigBean.getDefinitionChart().getId())) {
							if(definitionChartConfigBean.getDefinitionChart().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								definitionChartConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
							}
						}
					}
				}
				else if (!updatePopupOpen) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						if (componentTypeTest.equalsIgnoreCase("definitionChart") 
								&& definitionChartConfigBean.getDefinitionChart().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								definitionChartConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
						}
					}	
				}
				
				definitionChartConfigBean.save();
				if (definitionChartConfigBean.isErrorDefinitionChartTitleVisible() == true || definitionChartConfigBean.isErrorDefinitionlistVisible() == true)
					return;
				addMetricPanelVisible = false;
			} else if (componentType.equalsIgnoreCase("infoBoard")) {
				InfoBoardConfigBean infoBoardConfigBean = (InfoBoardConfigBean)FacesUtils.getManagedBean("infoBoardConfigBean");				
				if (infoBoardConfigBean.getInfoBoard().getId() != null && infoBoardConfigBean.getInfoBoard().getId().length() > 0) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						String componentIdTest = component.getComponentId();
						if (componentTypeTest.equalsIgnoreCase("infoBoard") && !componentIdTest.equalsIgnoreCase(infoBoardConfigBean.getInfoBoard().getId())) {
							if(infoBoardConfigBean.getInfoBoard().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								infoBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
							}
						}
					}
				}
				else if (!updatePopupOpen) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						if (componentTypeTest.equalsIgnoreCase("infoBoard") 
								&& infoBoardConfigBean.getInfoBoard().getTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								infoBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
						}
					}	
				}
				
				infoBoardConfigBean.save();
				if (infoBoardConfigBean.isErrorInfoBoardTitleVisible() == true || infoBoardConfigBean.isErrorInfoListVisible() == true)
					return;
				addMetricPanelVisible = false;
			} else if (componentType.equalsIgnoreCase("thread")) {
				ThreadConfigBean threadConfigBean = (ThreadConfigBean)FacesUtils.getManagedBean("threadConfigBean");				
//				if (threadConfigBean.getThreadType().getId() != null && threadConfigBean.getInfoBoard().getId().length() > 0) {
//					for (DashBoardComponent component : components){
//						String componentTypeTest = component.getComponentType();
//						if (componentTypeTest.equalsIgnoreCase("thread")) {
//							if(threadConfigBean.getThreadType().getTitle().equalsIgnoreCase(component.getTitle())){
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
//								return;
//							}
//						}
//					}
//				}
				
				threadConfigBean.save();
				if (threadConfigBean.isErrorThreadTitleVisible() == true)
					return;
			} else if (componentType.equalsIgnoreCase("batchBoard")) {
				BatchBoardConfigBean batchBoardConfigBean = (BatchBoardConfigBean)FacesUtils.getManagedBean("batchBoardConfigBean");
				if (batchBoardConfigBean.getBatchBoard().getId() != null && batchBoardConfigBean.getBatchBoard().getId().length() > 0) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						String componentIdTest = component.getComponentId();
						if (componentTypeTest.equalsIgnoreCase("batchBoard") && !componentIdTest.equalsIgnoreCase(batchBoardConfigBean.getBatchBoard().getId())) {
							if(batchBoardConfigBean.getComponentTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								batchBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
							}
						}
					}
				}
				else if (!updatePopupOpen) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						if (componentTypeTest.equalsIgnoreCase("batchBoard") 
								&& batchBoardConfigBean.getComponentTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								batchBoardConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
						}
					}	
				}				
				
				batchBoardConfigBean.save();
				if (batchBoardConfigBean.isErrorBatchBoardSelectedCollectorVisible() == true
						|| batchBoardConfigBean.isErrorBatchBoardTitleVisible() == true
						|| batchBoardConfigBean.isErrorBatchNameSelectedVisible() == true)
					return;
			} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
				AlertBoardGridConfigBean alertBoardGridConfigBean = (AlertBoardGridConfigBean)FacesUtils.getManagedBean("alertBoardGridConfigBean");
				if (alertBoardGridConfigBean.getAlertBoardGrid().getId() != null && alertBoardGridConfigBean.getAlertBoardGrid().getId().length() > 0) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						String componentIdTest = component.getComponentId();
						if (componentTypeTest.equalsIgnoreCase("alertBoardGrid") && !componentIdTest.equalsIgnoreCase(alertBoardGridConfigBean.getAlertBoardGrid().getId())) {
							if(alertBoardGridConfigBean.getComponentTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								alertBoardGridConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
							}
						}
					}
				}
				else if (!updatePopupOpen) {
					for (DashBoardComponent component : components){
						String componentTypeTest = component.getComponentType();
						if (componentTypeTest.equalsIgnoreCase("alertBoardGrid") 
								&& alertBoardGridConfigBean.getComponentTitle().equalsIgnoreCase(component.getTitle())){
								titleAlreadyExists = true;
								alertBoardGridConfigBean.setTitleError(true);
//								ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//								error.init();
//								error.setRendered(true);
//								error.setModal(true);
//								error.setType(ErrorMessageBean.WARNING);
//								error.addMessage("This title is already used, please, choose another title !");
								return;
						}
					}	
				}	
								
				alertBoardGridConfigBean.save();
				if (alertBoardGridConfigBean.isErrorAlertBoardGridEnvVisible() == true
					|| alertBoardGridConfigBean.isErrorAlertBoardGridTitleVisible() == true
						|| alertBoardGridConfigBean.isErrorAlertBoardGridWorkflow() == true)
					return;
			}
			

			MetricSelectorBean metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
			if(metricSelectorBean != null)
				metricSelectorBean.setSelectAll(false);
			
			reloadComponents();
			setSelectedComponent(null);
			deleteButtonVisible = false;
			rendererAddComponent = false;
			updatePopupOpen = false;
			componentsSelected = new ArrayList<DashBoardComponent>();
			selectAll = false;
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No Component has been selected");
		}	
	}
	
	public void addComponent() {		
		if (componentType!=null && componentType.length()>0) {
			String environment = dashboard.getSummary().getEnv();
			String type = dashboard.getSummary().getType();
			String title = dashboard.getSummary().getTitle();
			
			if (componentType.equalsIgnoreCase("alertBoard")) {
				AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
				alertBoardConfigBean.init(environment,type,title);
				addButtonVisible = true;
			} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
				AlertBoardGridConfigBean alertBoardGridConfigBean = (AlertBoardGridConfigBean)FacesUtils.getManagedBean("alertBoardGridConfigBean");
				alertBoardGridConfigBean.init(environment,type,title);
			} else if (componentType.equalsIgnoreCase("navigationBoard")) {
				NavigationBoardConfigBean navigationBoardConfigBean = (NavigationBoardConfigBean)FacesUtils.getManagedBean("navigationBoardConfigBean");	
				navigationBoardConfigBean.init(environment,type,title);
				addButtonVisible = true;
			} else if (componentType.equalsIgnoreCase("definitionChart")) {
				DefinitionChartConfigBean definitionChartConfigBean = (DefinitionChartConfigBean)FacesUtils.getManagedBean("definitionChartConfigBean");	
				definitionChartConfigBean.init(environment,type,title);
				addButtonVisible = true;
				deleteButtonVisible = true;
			} else if (componentType.equalsIgnoreCase("infoBoard")) {
				InfoBoardConfigBean infoBoardConfigBean = (InfoBoardConfigBean)FacesUtils.getManagedBean("infoBoardConfigBean");	
				infoBoardConfigBean.init(environment,type,title);
				addButtonVisible = true;
				deleteButtonVisible = true;
			} else if (componentType.equalsIgnoreCase("thread")) {
				ThreadConfigBean threadConfigBean = (ThreadConfigBean)FacesUtils.getManagedBean("threadConfigBean");	
				threadConfigBean.init(environment,type,title);
			} else if (componentType.equalsIgnoreCase("batchBoard")) {
				BatchBoardConfigBean batchBoardConfigBean = (BatchBoardConfigBean)FacesUtils.getManagedBean("batchBoardConfigBean");
				batchBoardConfigBean.init(environment, type, title);
			} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
				AlertBoardGridConfigBean alertBoardGridConfigBean = (AlertBoardGridConfigBean)FacesUtils.getManagedBean("alertBoardGridConfigBean");
				alertBoardGridConfigBean.init(environment, type, title);
			}
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No Component has been selected");
		}	
	}
	
	public void editComponent(ActionEvent event) {
		if (!isAuthorized(85,"dashboardMainConfig") ) {
			setAccessDenied();
			return;
		}
		
		updatePopupOpen = true;
		setAddVisible(false);
		
		String environment = dashboard.getSummary().getEnv();
		String type = dashboard.getSummary().getType();
		String title = dashboard.getSummary().getTitle();	
		DashBoardComponent componentSelected = (DashBoardComponent)event.getComponent().getAttributes().get("Component");
		selectedComponent = componentSelected;
		String componentId = selectedComponent.getComponentId();
		componentType = selectedComponent.getComponentType();
		
		if (componentType.equalsIgnoreCase("alertBoard")) {			
			Collection<AlertBoardComponent> alertBoardComponents = dashboard.getSummary().getAlertBoardComponents();				
			AlertBoardComponent component = null;				
			if (alertBoardComponents != null && !alertBoardComponents.isEmpty()) {
				for (AlertBoardComponent alertBoardComponent : alertBoardComponents) {
					if (alertBoardComponent.getComponentId().equals(componentId)) {
						component = alertBoardComponent;
						break;
					}
				}
			}	
			AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
			alertBoardConfigBean.init(environment,type,title,component);
			if (alertBoardConfigBean.isAlertBoardItemConfigVisible() == true)
				addButtonVisible = false;
			else
				addButtonVisible = true;
		} else if (componentType.equalsIgnoreCase("navigationBoard")) {	
			NavigationBoardConfigBean navigationBoardConfigBean = (NavigationBoardConfigBean)FacesUtils.getManagedBean("navigationBoardConfigBean");
			navigationBoardConfigBean.init(environment,type,title,componentId);
			addButtonVisible = true;
		} else if (componentType.equalsIgnoreCase("definitionChart")) {	
			DefinitionChartConfigBean definitionChartConfigBean = (DefinitionChartConfigBean)FacesUtils.getManagedBean("definitionChartConfigBean");
			definitionChartConfigBean.init(environment,type,title,componentId);
			addButtonVisible = true;
			deleteButtonVisible = true;
		} else if (componentType.equalsIgnoreCase("infoBoard")) {
			InfoBoardConfigBean infoBoardConfigBean = (InfoBoardConfigBean)FacesUtils.getManagedBean("infoBoardConfigBean");
			infoBoardConfigBean.init(environment,type,title,componentId);
			addButtonVisible = true;
			deleteButtonVisible = true;
		} else if (componentType.equalsIgnoreCase("thread")) {
			ThreadConfigBean threadConfigBean = (ThreadConfigBean)FacesUtils.getManagedBean("threadConfigBean");
			threadConfigBean.init(environment,type,title);
		} else if (componentType.equalsIgnoreCase("batchBoard")) {
			BatchBoardConfigBean batchBoardConfigBean = (BatchBoardConfigBean)FacesUtils.getManagedBean("batchBoardConfigBean");
			batchBoardConfigBean.init(environment,type,title,componentId);
		} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
			AlertBoardGridConfigBean alertBoardGridConfigBean = (AlertBoardGridConfigBean)FacesUtils.getManagedBean("alertBoardGridConfigBean");
			alertBoardGridConfigBean.init(environment, type, title,componentId);
		}
		
		rendererAddComponent = true;
		
		setComponentTypeVisible(false);
		setComponentConfigVisible(true);
		setBackComponentConfigVisible(false);
		
//		numberComponentsSelected = 0;
//		for (DashBoardComponent selectedComponentForMultipleDeletion : components) {
//			if (selectedComponentForMultipleDeletion.isSelected()){
//				numberComponentsSelected++;
//			}
//		}
//		
//		if (numberComponentsSelected < 2) {
//			if (numberComponentsSelected == 1){
//				String environment = dashboard.getSummary().getEnv();
//				String type = dashboard.getSummary().getType();
//				String title = dashboard.getSummary().getTitle();		
//				String componentId = selectedComponent.getComponentId();
//				componentType = selectedComponent.getComponentType();
//				
//				if (componentType.equalsIgnoreCase("alertBoard")) {			
//					Collection<AlertBoardComponent> alertBoardComponents = dashboard.getSummary().getAlertBoardComponents();				
//					AlertBoardComponent component = null;				
//					if (alertBoardComponents != null && !alertBoardComponents.isEmpty()) {
//						for (AlertBoardComponent alertBoardComponent : alertBoardComponents) {
//							if (alertBoardComponent.getComponentId().equals(componentId)) {
//								component = alertBoardComponent;
//								break;
//							}
//						}
//					}	
//					AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
//					alertBoardConfigBean.init(environment,type,title,component);
//					if (alertBoardConfigBean.isAlertBoardItemConfigVisible() == true)
//						addButtonVisible = false;
//					else
//						addButtonVisible = true;
//				} else if (componentType.equalsIgnoreCase("navigationBoard")) {	
//					NavigationBoardConfigBean navigationBoardConfigBean = (NavigationBoardConfigBean)FacesUtils.getManagedBean("navigationBoardConfigBean");
//					navigationBoardConfigBean.init(environment,type,title,componentId);
//					addButtonVisible = true;
//					deleteButtonVisible = true;
//				} else if (componentType.equalsIgnoreCase("definitionChart")) {	
//					DefinitionChartConfigBean definitionChartConfigBean = (DefinitionChartConfigBean)FacesUtils.getManagedBean("definitionChartConfigBean");
//					definitionChartConfigBean.init(environment,type,title,componentId);
//					addButtonVisible = true;
//					deleteButtonVisible = true;
//				} else if (componentType.equalsIgnoreCase("infoBoard")) {
//					InfoBoardConfigBean infoBoardConfigBean = (InfoBoardConfigBean)FacesUtils.getManagedBean("infoBoardConfigBean");
//					infoBoardConfigBean.init(environment,type,title,componentId);
//					addButtonVisible = true;
//					deleteButtonVisible = true;
//				} else if (componentType.equalsIgnoreCase("thread")) {
//					ThreadConfigBean threadConfigBean = (ThreadConfigBean)FacesUtils.getManagedBean("threadConfigBean");
//					threadConfigBean.init(environment,type,title);
//				} else if (componentType.equalsIgnoreCase("batchBoard")) {
//					BatchBoardConfigBean batchBoardConfigBean = (BatchBoardConfigBean)FacesUtils.getManagedBean("batchBoardConfigBean");
//					batchBoardConfigBean.init(environment,type,title,componentId);
//				} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
//					AlertBoardGridConfigBean alertBoardGridConfigBean = (AlertBoardGridConfigBean)FacesUtils.getManagedBean("alertBoardGridConfigBean");
//					alertBoardGridConfigBean.init(environment, type, title,componentId);
//				}
//				
//				rendererAddComponent = true;
//				
//				setComponentTypeVisible(false);
//				setComponentConfigVisible(true);
//				setBackComponentConfigVisible(false);
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No Component has been selected");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one component to edit");
//		}
	}
	
	public void deleteComponent(ActionEvent event) {
		if (!isAuthorized(84,"dashboardMainConfig") ) {
			setAccessDenied();
			return;
		}
		
		DashBoardComponent componentSelected = (DashBoardComponent)event.getComponent().getAttributes().get("Component");
		String environment = dashboard.getSummary().getEnv();
		String type = dashboard.getSummary().getType();
		String title = dashboard.getSummary().getTitle();		
		String componentId = componentSelected.getComponentId();
		String componentType = componentSelected.getComponentType();
		
		if (componentType!=null && componentType.length()>0) {		
			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();			
			if (componentType.equalsIgnoreCase("alertBoard")) {			
				factory.removeAlertBoardComponent(environment,type,title,componentId);
				componentsSelected.remove(componentSelected);
			} else if (componentType.equalsIgnoreCase("navigationBoard")) {	
				factory.removeNavigationBoardComponent(environment,type,title,componentId);
				componentsSelected.remove(componentSelected);
			} else if (componentType.equalsIgnoreCase("definitionChart")) {	
				factory.removeChartComponent(environment,type,title,componentId);
				componentsSelected.remove(componentSelected);
			} else if (componentType.equalsIgnoreCase("infoBoard")) {
				factory.removeInfoBoardComponent(environment,type,title,componentId);
				componentsSelected.remove(componentSelected);
			} else if (componentType.equalsIgnoreCase("thread")) {
				factory.removeThreadComponent(environment,type,title);
				componentsSelected.remove(componentSelected);
			} else if (componentType.equalsIgnoreCase("batchBoard")) {
				factory.removeBatchBoardComponent(environment, type, title,componentId);
				componentsSelected.remove(componentSelected);
			} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
				factory.removeAlertBoardGridComponent(environment, type, title, componentId);
				componentsSelected.remove(componentSelected);
			}
		}
		reloadComponents();
		selectedComponent = null;
		componentsSelected = new ArrayList<DashBoardComponent>();
		selectAll = false;
	}
	
	public void deleteSelectedComponents(ActionEvent event) {
		if (!isAuthorized(84,"dashboardMainConfig") ) {
			setAccessDenied();
			return;
		}
		numberComponentsSelected = 0;
		for (DashBoardComponent selectedComponentForMultipleDeletion : components) {
			if (selectedComponentForMultipleDeletion.isSelected()){
				numberComponentsSelected++;
			}
		}
		if (numberComponentsSelected > 0) {
			for (DashBoardComponent component : components) {
				if (component.isSelected()){
					String environment = dashboard.getSummary().getEnv();
					String type = dashboard.getSummary().getType();
					String title = dashboard.getSummary().getTitle();		
					String componentId = component.getComponentId();
					String componentType = component.getComponentType();
					
					if (componentType!=null && componentType.length()>0) {		
						RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();			
						if (componentType.equalsIgnoreCase("alertBoard")) {			
							factory.removeAlertBoardComponent(environment,type,title,componentId);
						} else if (componentType.equalsIgnoreCase("navigationBoard")) {	
							factory.removeNavigationBoardComponent(environment,type,title,componentId);
						} else if (componentType.equalsIgnoreCase("definitionChart")) {	
							factory.removeChartComponent(environment,type,title,componentId);
						} else if (componentType.equalsIgnoreCase("infoBoard")) {
							factory.removeInfoBoardComponent(environment,type,title,componentId);
						} else if (componentType.equalsIgnoreCase("thread")) {
							factory.removeThreadComponent(environment,type,title);
						} else if (componentType.equalsIgnoreCase("batchBoard")) {
							factory.removeBatchBoardComponent(environment, type, title,componentId);
						} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
							factory.removeAlertBoardGridComponent(environment, type, title, componentId);
						}
					}
				}
			}
			reloadComponents();
			selectedComponent = null;
			componentsSelected = new ArrayList<DashBoardComponent>();
			selectAll = false;
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No Component has been selected");
		}
	}
	
//	public void backToMainDashboard(ActionEvent action) {
//		reloadComponents();
//		selectedComponent = null;
//	}

	public List<DashBoardComponent> getComponents() {
		if (components != null && components.size()>0)
			sort();
		return components;
	}
	
	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}
	
	public boolean isPaginationVisible() {
		if (getComponents().size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}
	
	public String getDeleteMessage() {
		numberComponentsSelected = 0;
		for (DashBoardComponent component : components) {
			if (component.isSelected()){
				numberComponentsSelected++;
			}
		}
		String message = "No components selected";
		if (numberComponentsSelected == 1) {
			for (DashBoardComponent component : components) {
				if(component.isSelected())
					message = "Are you sure to delete this component : " + component.getComponentType() + " > " + component.getTitle();
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberComponentsSelected + " components?";
			return message;
		}
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public boolean isRendererAddComponent() {
		return rendererAddComponent;
	}

	public void setRendererAddComponent(boolean rendererAddComponent) {
		this.rendererAddComponent = rendererAddComponent;
	}
	
	public boolean isUpdatePopupOpen() {
		return updatePopupOpen;
	}

	public void openAddComponentPopup(ActionEvent event){
		if (!isAuthorized(83,"dashboardMainConfig") ) {
			setAccessDenied();
			return;
		}
		
		rendererAddComponent = true;
		componentTypeVisible = true;
		componentConfigVisible = false;
		backComponentConfigVisible = false;
		addButtonVisible = false;
		addVisible = true;
	}
	
	public void closeAddComponentPopup(ActionEvent event){
		rendererAddComponent = false;
		addVisible = false;
		addButtonVisible = false;
		deleteButtonVisible = false;
		titleAlreadyExists = false;
		
		AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
		alertBoardConfigBean.setAllErrorFalse();
		alertBoardConfigBean.setAlertBoardItemConfigVisible(false);
		alertBoardConfigBean.setTitleError(false);
		alertBoardConfigBean.setErrorItemTitleVisible(false);
		addMetricPanelVisible = false;
		AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		if(allAlertDefinitionBean != null)
			allAlertDefinitionBean.setSelectAllInAlertBoardComponent(false);
		
		NavigationBoardConfigBean navigationBoardConfigBean = (NavigationBoardConfigBean)FacesUtils.getManagedBean("navigationBoardConfigBean");	
		navigationBoardConfigBean.setErrorDashboardListVisible(false);
		navigationBoardConfigBean.setTitleError(false);
		navigationBoardConfigBean.setErrorNavigationBoardTitleVisible(false);
		addDashboardPanelVisible = false;
		
		DefinitionChartConfigBean definitionChartConfigBean = (DefinitionChartConfigBean)FacesUtils.getManagedBean("definitionChartConfigBean");	
		definitionChartConfigBean.setErrorDefinitionChartTitleVisible(false);
		definitionChartConfigBean.setTitleError(false);
		definitionChartConfigBean.setErrorDefinitionlistVisible(false);
		
		InfoBoardConfigBean infoBoardConfigBean = (InfoBoardConfigBean)FacesUtils.getManagedBean("infoBoardConfigBean");	
		infoBoardConfigBean.setErrorInfoBoardTitleVisible(false);
		infoBoardConfigBean.setTitleError(false);
		infoBoardConfigBean.setErrorInfoListVisible(false);
		addMetricPanelVisible = false;
		
		ThreadConfigBean threadConfigBean = (ThreadConfigBean)FacesUtils.getManagedBean("threadConfigBean");
		threadConfigBean.setErrorThreadTitleVisible(false);
		
		BatchBoardConfigBean batchBoardConfigBean = (BatchBoardConfigBean)FacesUtils.getManagedBean("batchBoardConfigBean");
		batchBoardConfigBean.setComponentTitle("");
		batchBoardConfigBean.setErrorBatchBoardSelectedCollectorVisible(false);
		batchBoardConfigBean.setErrorBatchBoardTitleVisible(false);
		batchBoardConfigBean.setErrorBatchNameSelectedVisible(false);
		batchBoardConfigBean.setTitleError(false);
		batchBoardConfigBean.setAutoDiscovery(true);
		batchBoardConfigBean.setSelectAllBatches(false);
		
		AlertBoardGridConfigBean alertBoardGridConfigBean = (AlertBoardGridConfigBean)FacesUtils.getManagedBean("alertBoardGridConfigBean");
		alertBoardGridConfigBean.setComponentTitle("");
		alertBoardGridConfigBean.setErrorAlertBoardGridEnvVisible(false);
		alertBoardGridConfigBean.setErrorAlertBoardGridTitleVisible(false);
		alertBoardGridConfigBean.setErrorAlertBoardGridWorkflow(false);
		alertBoardGridConfigBean.setSelectAllEnvs(false);
		alertBoardGridConfigBean.setTitleError(false);
		alertBoardGridConfigBean.setSelectAll(false);

		MetricSelectorBean metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
		metricSelectorBean.setSelectAll(false);
		
		updatePopupOpen = false;
	}
	
	public boolean isRendererUpdateComponent() {
		return rendererUpdateComponent;
	}

	public void setRendererUpdateComponent(boolean rendererUpdateComponent) {
		this.rendererUpdateComponent = rendererUpdateComponent;
	}
	
	public void closeUpdateComponentPopup(ActionEvent event){
		rendererAddComponent = false;
		titleAlreadyExists = false;
		componentType = "NavigationBoard";
		MetricSelectorBean metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
		metricSelectorBean.setSelectAll(false);
	}

	public String getSelectedPanel() {
		return componentType;		
	}
	
	public void handleComponentConfig(ActionEvent event) {		
		componentTypeVisible = !componentTypeVisible;
		componentConfigVisible = !componentConfigVisible;
		backComponentConfigVisible = !backComponentConfigVisible;
		addComponent();
	}

	public boolean isComponentTypeVisible() {
		return componentTypeVisible;
	}

	public void setComponentTypeVisible(boolean componentTypeVisible) {
		this.componentTypeVisible = componentTypeVisible;
	}

	public boolean isComponentConfigVisible() {
		return componentConfigVisible;
	}

	public void setComponentConfigVisible(boolean componentConfigVisible) {
		this.componentConfigVisible = componentConfigVisible;
	}

	public boolean isBackComponentConfigVisible() {
		return backComponentConfigVisible;
	}

	public void setBackComponentConfigVisible(boolean backComponentConfigVisible) {
		this.backComponentConfigVisible = backComponentConfigVisible;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public SelectItem[] getComponentTypeItems() {
		return componentTypeItems;
	}	

	public DashBoardComponent getSelectedAddComponent() {
		return selectedAddComponent;
	}

	public DashBoardComponent getSelectedComponent() {
		return selectedComponent;
	}

	public void setSelectedComponent(DashBoardComponent selectedComponent) {
		this.selectedComponent = selectedComponent;
	}

	public void backToComponentType(ActionEvent action) {
		AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
	
		componentTypeVisible = !componentTypeVisible;
		componentConfigVisible = !componentConfigVisible;
		backComponentConfigVisible = !backComponentConfigVisible;
		addButtonVisible = false;
		
		if (componentType.equalsIgnoreCase("definitionChart") || componentType.equalsIgnoreCase("infoBoard")
		    || componentType.equalsIgnoreCase("navigationBoard")) {
			addButtonVisible = false;
			deleteButtonVisible = false;
		}
		
		titleAlreadyExists = false;
		
//		alertBoardConfigBean.setErrorAlertItemTitleVisible(false);
		alertBoardConfigBean.setAllErrorFalse();
		alertBoardConfigBean.setAlertBoardItemConfigVisible(false);
		alertBoardConfigBean.setTitleError(false);
		deleteButtonVisible = false;
		addButtonVisible = false;
		AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		if(allAlertDefinitionBean != null) {
			allAlertDefinitionBean.setSelectAllInAlertBoardComponent(false);
			allAlertDefinitionBean.setSelectAll(false);
		}
		
		NavigationBoardConfigBean navigationBoardConfigBean = (NavigationBoardConfigBean)FacesUtils.getManagedBean("navigationBoardConfigBean");	
		navigationBoardConfigBean.setErrorDashboardListVisible(false);
		navigationBoardConfigBean.setErrorNavigationBoardTitleVisible(false);
		navigationBoardConfigBean.setTitleError(false);
		addDashboardPanelVisible = false;
		
		DefinitionChartConfigBean definitionChartConfigBean = (DefinitionChartConfigBean)FacesUtils.getManagedBean("definitionChartConfigBean");	
		definitionChartConfigBean.setErrorDefinitionChartTitleVisible(false);
		definitionChartConfigBean.setErrorDefinitionlistVisible(false);
		definitionChartConfigBean.setTitleError(false);
		addMetricPanelVisible = false;
		
		InfoBoardConfigBean infoBoardConfigBean = (InfoBoardConfigBean)FacesUtils.getManagedBean("infoBoardConfigBean");	
		infoBoardConfigBean.setErrorInfoBoardTitleVisible(false);
		infoBoardConfigBean.setErrorInfoListVisible(false);
		infoBoardConfigBean.setTitleError(false);
		addMetricPanelVisible = false;
		
		ThreadConfigBean threadConfigBean = (ThreadConfigBean)FacesUtils.getManagedBean("threadConfigBean");
		threadConfigBean.setErrorThreadTitleVisible(false);
		
		BatchBoardConfigBean batchBoardConfigBean = (BatchBoardConfigBean)FacesUtils.getManagedBean("batchBoardConfigBean");
		batchBoardConfigBean.setComponentTitle("");
		batchBoardConfigBean.setErrorBatchBoardSelectedCollectorVisible(false);
		batchBoardConfigBean.setErrorBatchBoardTitleVisible(false);
		batchBoardConfigBean.setErrorBatchNameSelectedVisible(false);
		batchBoardConfigBean.setTitleError(false);
		batchBoardConfigBean.setSelectAllBatches(false);
		
		AlertBoardGridConfigBean alertBoardGridConfigBean = (AlertBoardGridConfigBean)FacesUtils.getManagedBean("alertBoardGridConfigBean");
		alertBoardGridConfigBean.setComponentTitle("");
		alertBoardGridConfigBean.setErrorAlertBoardGridEnvVisible(false);
		alertBoardGridConfigBean.setErrorAlertBoardGridTitleVisible(false);
		alertBoardGridConfigBean.setTitleError(false);
		alertBoardGridConfigBean.setErrorAlertBoardGridWorkflow(false);
		
		MetricSelectorBean metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
		if(metricSelectorBean != null)
			metricSelectorBean.setSelectAll(false);
	}

	public DashBoardComponents getDashboard() {
		return dashboard;
	}
	
	public boolean isAddButtonVisible() {
		return addButtonVisible;
	}

	public void setAddButtonVisible(boolean addButtonVisible) {
		this.addButtonVisible = addButtonVisible;
	}
	
	public boolean isDeleteButtonVisible() {
		return deleteButtonVisible;
	}

	public void setDeleteButtonVisible(boolean deleteButtonVisible) {
		this.deleteButtonVisible = deleteButtonVisible;
	}

	public void add(ActionEvent action) {
		if (componentType.equalsIgnoreCase("navigationBoard")) {	
			addDashboard(action);
		} else if (componentType.equalsIgnoreCase("definitionChart")) {	
			addMetric(action);
		} else if (componentType.equalsIgnoreCase("infoBoard")) {
			addMetric(action);
		} else if (componentType.equalsIgnoreCase("alertBoard")) {
			addAlert(action);
		}
	}
	
	public void delete(ActionEvent action) {
		if (componentType.equalsIgnoreCase("navigationBoard")) {
			NavigationBoardConfigBean navigationBoardConfigBean = (NavigationBoardConfigBean)FacesUtils.getManagedBean("navigationBoardConfigBean");
			navigationBoardConfigBean.removeItems(action);
		} else if (componentType.equalsIgnoreCase("definitionChart") || componentType.equalsIgnoreCase("infoBoard")) {
			MetricSelectorBean metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
			metricSelectorBean.removeAllItems(action);
		} 
	}
	
	public void addDashboard(ActionEvent action) {
		addDashboardPanelVisible = true;
		addButtonVisible = false;
	}
	
	public void addMetric(ActionEvent action) {
		addMetricPanelVisible = true;
		addButtonVisible = false;
	}
	
	public void addItem(ActionEvent action) {
		addAlertBoardItemPanelVisible = true;
//		addButtonVisible = true;
	}
	
	public void addAlert(ActionEvent action) {
		AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
		alertBoardConfigBean.setAlertBoardItemConfigVisible(true);
		addButtonVisible = false;
	}

	public void closeAddPanel(ActionEvent action) {
		addMetricPanelVisible = false;
		addDashboardPanelVisible = false;
		addAlertBoardItemPanelVisible = false;
		addButtonVisible = true;
		titleAlreadyExists = false;
	}

	public boolean isAddMetricPanelVisible() {
		return addMetricPanelVisible;
	}

	public void setAddMetricPanelVisible(boolean addMetricPanelVisible) {
		this.addMetricPanelVisible = addMetricPanelVisible;
	}

	public boolean isAddDashboardPanelVisible() {
		return addDashboardPanelVisible;
	}

	public void setAddDashboardPanelVisible(boolean addDashboardPanelVisible) {
		this.addDashboardPanelVisible = addDashboardPanelVisible;
	}

	public boolean isAddAlertBoardItemPanelVisible() {
		return addAlertBoardItemPanelVisible;
	}

	public void setAddAlertBoardItemPanelVisible(
			boolean addAlertBoardItemPanelVisible) {
		this.addAlertBoardItemPanelVisible = addAlertBoardItemPanelVisible;
	}

	public boolean isAddVisible() {
		return addVisible;
	}

	public void setAddVisible(boolean addVisible) {
		this.addVisible = addVisible;
	}
	
//	public void openFilterPanel(ActionEvent action) {
//		AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
//		alertBoardConfigBean.setAddAlertPanelVisible(true);
//	}

	public String getPopupTitle() {
		String popupTitle = "null";
		String name = "";
		if (componentType.equalsIgnoreCase("navigationBoard")) {	
			name = "Navigation Board";
		} else if (componentType.equalsIgnoreCase("definitionChart")) {	
			name = "Chart";
		} else if (componentType.equalsIgnoreCase("infoBoard")) {
			name = "Info Board";
		} else if (componentType.equalsIgnoreCase("alertBoard")) {
			name = "Alert Board";
		} else if (componentType.equalsIgnoreCase("batchBoard")) {
			name = "Batch Board";
		} else if (componentType.equalsIgnoreCase("alertBoardGrid")) {
			name = "Alert Board Grid";
		}
		
		if (addVisible == true){
			popupTitle = "Add a new " + name;
		}
		else {
			popupTitle = "Update " + name + " : " + selectedComponent.getTitle();
		}
		return popupTitle;
	}

	public String getAddButtonName() {
		String name = "";
		if (componentType.equalsIgnoreCase("navigationBoard")) {	
			name = "Add dashboard into the list";
		} else if (componentType.equalsIgnoreCase("definitionChart")) {	
			name = "Add metric";
		} else if (componentType.equalsIgnoreCase("infoBoard")) {
			name = "Add metric";
		} else if (componentType.equalsIgnoreCase("alertBoard")) {
			name = "Add alert";
		}
		return name;
	}
	
	public String getDeleteButtonName() {
		String name = "";
		if (componentType.equalsIgnoreCase("navigationBoard")) {	
			name = "Delete selected dashboard from the list";
		} else if (componentType.equalsIgnoreCase("definitionChart") || componentType.equalsIgnoreCase("infoBoard")) {	
			name = "Delete selected metric from the list";
		} else if (componentType.equalsIgnoreCase("alertBoard")) {
			AlertBoardConfigBean alertBoardConfigBean = (AlertBoardConfigBean)FacesUtils.getManagedBean("alertBoardConfigBean");
			if (alertBoardConfigBean.isAlertBoardItemConfigVisible() == false) {
				name = "Delete selected item from the list";
			} else {
				name = "Delete selected alert from the list";
			}			
		} 
		return name;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(components, new Comparator<DashBoardComponent>() {
			public int compare(DashBoardComponent o1, DashBoardComponent o2) {
				int res = 0;
				try {
					if (getTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getComponentTypeLabel().toLowerCase().compareTo(o2.getComponentTypeLabel().toLowerCase()) :  o2.getComponentTypeLabel().toLowerCase().compareTo(o1.getComponentTypeLabel().toLowerCase());
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

	public String getTypeColumnName() {
		return typeColumnName;
	}

	public String getTitleColumnName() {
		return titleColumnName;
	}
	
	public void handleSelectedComponent(ValueChangeEvent event) {
		DashBoardComponent componentSelected = (DashBoardComponent)event.getComponent().getAttributes().get("Component");
		if(componentSelected != null) {
			for(DashBoardComponent component : components) {
				if(component.equals(componentSelected)) {
					component.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						componentsSelected.add(componentSelected);
					else
						componentsSelected.remove(componentSelected);
				}
			}
		}
	}
	
	public void handleSelectAllComponents(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		componentsSelected.clear();
		for(DashBoardComponent component : components) {
			component.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				componentsSelected.add(component);
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getComponentsSelected() {
		int size = componentsSelected.size();
		return size;
	}

	public void setComponentsSelected(List<DashBoardComponent> componentsSelected) {
		this.componentsSelected = componentsSelected;
	}
	
	public boolean getListRendered() {
		return getComponents().size() > 0;
	}

	public boolean isTitleAlreadyExists() {
		return titleAlreadyExists;
	}
}
