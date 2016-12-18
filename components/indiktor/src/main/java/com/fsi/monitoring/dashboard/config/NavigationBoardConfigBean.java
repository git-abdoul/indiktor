package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.NavigationBoardType;
import generated.dashboard.config.schema.NavigationBoardType.NavigationItemType;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.info.InfoDetail;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;

public class NavigationBoardConfigBean 
extends DashBoardConfigBean {

	private static final long serialVersionUID = 2767684910418822131L;

	private static final Logger logger = Logger.getLogger(NavigationBoardConfigBean.class);
	
	public static final String envColumnName = "Environment";
	public static final String typeColumnName = "Type";
	public static final String titleColumnName = "Title";
	private String columnName = "Environment";	
	private boolean ascending = true;
	
	private NavigationBoardType navigationBoard;
	private NavigationItemConfig itemConfig;
	
	private List<DashBoardSummaryComponent> dashboards;
	
	private List<NavItemSelection> navItems;
	private boolean selected = false;
	private boolean rendered;
	
	private boolean errorDashboardListVisible = false;
	private boolean errorNavigationBoardTitleVisible = false;
	
	private boolean titleError;
	
	public void init(String env,
					 String type,
					 String title,
					 String componentId) {
		super.init(env, type, title);
		
		ComponentXmlManager componentXmlManager = (ComponentXmlManager)FacesUtils.getManagedBean("xmlComponentManager");
		
		DashBoard dashBoard = componentXmlManager.getDashBoardSchema(env, type, title);
		
		List<NavigationBoardType> navigationBoards = dashBoard.getNavigationBoards().getNavigationBoard();
		for (NavigationBoardType navigationBoard : navigationBoards) {
			if (navigationBoard.getId().equals(componentId)) {
				this.navigationBoard = navigationBoard;
			}
		}
		
		initItemConfig();
		navItems = new ArrayList<NavItemSelection>();
		
		List<NavigationItemType> items = this.navigationBoard.getNavigationItem();
		
		for(NavigationItemType item : items) {
			NavItemSelection navItem = new NavItemSelection(item);
			navItems.add(navItem);
		}	
		
		Iterator<NavItemSelection> itemIt = navItems.iterator();
		
		while (itemIt.hasNext()) {
			NavItemSelection item = itemIt.next();
			
			itemConfig.getItem().setEnv(item.getNavigationItem().getEnv());
			itemConfig.getItem().setTitle(item.getNavigationItem().getTitle());
			itemConfig.getItem().setType(item.getNavigationItem().getType());
			
			for (DashBoardSummaryComponent dashboard : itemConfig.getSummaries()) {
				if(item.getNavigationItem().getEnv().equals(dashboard.getEnv())
						&& item.getNavigationItem().getTitle().equals(dashboard.getTitle())
							&& item.getNavigationItem().getType().equals(dashboard.getType())) {
					dashboards.remove(dashboard);
					break;
				}
			}
		}
		
		rendered = false;
	}
	
	public void init(String env,
					 String type,
					 String title) {
		try {
			super.init(env, type, title);
			
			ObjectFactory objFactory = new ObjectFactory();
			navigationBoard = objFactory.createNavigationBoardType();
				
			initItemConfig();
			
			navItems = new ArrayList<NavItemSelection>();
			
			
			rendered = true;
		} catch (Exception exc) {
			logger.error(exc);
		}
	}

	private void initItemConfig() {
		try {
			
			ObjectFactory objFactory = new ObjectFactory();
			NavigationItemType navigationItem = objFactory.createNavigationBoardTypeNavigationItemType();
			itemConfig = new NavigationItemConfig(navigationItem);
			itemConfig.init();
			
			DashboardMainConfigBean dashboardMainConfigBean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			String selectedDashboardEnv = dashboardMainConfigBean.getDashBoardSummaryComponent().getEnv();
			String selectedDashboardType = dashboardMainConfigBean.getDashBoardSummaryComponent().getType();
			String selectedDashboardTitle = dashboardMainConfigBean.getDashBoardSummaryComponent().getTitle();
			String selectedDashboard = selectedDashboardEnv + selectedDashboardType + selectedDashboardTitle;
			
			dashboards = new ArrayList<DashBoardSummaryComponent>();
			if (itemConfig != null) {
				for (DashBoardSummaryComponent dashboard : itemConfig.getSummaries()) {
					String dashboardStr = dashboard.getEnv() + dashboard.getType() + dashboard.getTitle();
					if (!dashboardStr.equals("globalalertBoardDashBoard") && !dashboardStr.equals(selectedDashboard)) {
						dashboard.setSelected(false);
						dashboards.add(dashboard);
					}
				}
			}
			
		} catch (Exception exc) {
			logger.error(exc);
		}		
	}
	
	public void setSelected(boolean selected) {}	
	
	public boolean isSelected() {
		return selected;
	}
	
	public List<DashBoardSummaryComponent> getDashboards() {
		if (dashboards != null && dashboards.size()>0) {
			Collections.sort(dashboards, new Comparator<DashBoardSummaryComponent>() {
				public int compare(DashBoardSummaryComponent o1, DashBoardSummaryComponent o2) {
					Integer res = 0;					
					if (envColumnName.equals(columnName))
						res = ascending ? o1.getEnv().compareTo(o2.getEnv()) :  o2.getEnv().compareTo(o1.getEnv());
					else if (titleColumnName.equals(columnName))
						res = ascending ? o1.getTitle().compareTo(o2.getTitle()) :  o2.getTitle().compareTo(o1.getTitle());
					else if (typeColumnName.equals(columnName))
						res = ascending ? o1.getType().compareTo(o2.getType()) :  o2.getType().compareTo(o1.getType());
					
					return res;
				}
			});
		}
		return dashboards;
	}

	public boolean isErrorDashboardListVisible() {
		return errorDashboardListVisible;
	}

	public void setErrorDashboardListVisible(boolean errorDashboardListVisible) {
		this.errorDashboardListVisible = errorDashboardListVisible;
	}

	public boolean isErrorNavigationBoardTitleVisible() {
		return errorNavigationBoardTitleVisible;
	}

	public void setErrorNavigationBoardTitleVisible(
			boolean errorNavigationBoardTitleVisible) {
		this.errorNavigationBoardTitleVisible = errorNavigationBoardTitleVisible;
	}

	public void onChangeSelected(ValueChangeEvent evnt) {	
		this.selected = (Boolean)evnt.getNewValue();;
		
		for (NavItemSelection item : navItems) {
			item.updateSelected(selected);
		}
	}	
	
	public boolean isRendered() {
		return rendered;
	}
	
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	
	public List<NavItemSelection> getNavigationItems() {
		return navItems;
	}
	
	public NavigationBoardType getNavigationBoard() {
		return navigationBoard;
	}
	
	public NavigationItemConfig getNavItem() {
		return itemConfig;
	}	
	
	public void addDashboard(ActionEvent action) {
		DashBoardSummaryComponent dashboard = (DashBoardSummaryComponent)action.getComponent().getAttributes().get("dashboard");
		try {
			if (dashboard.getEnv().equals("global") && dashboard.getType().equals("alertBoard") && dashboard.getTitle().equals("DashBoard")) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("You cannot add the master dashboard in a navigation board");
				return;
			}
			
			DashboardMainConfigBean dashboardMainConfigBean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			String selectedDashboardEnv = dashboardMainConfigBean.getDashBoardSummaryComponent().getEnv();
			String selectedDashboardType = dashboardMainConfigBean.getDashBoardSummaryComponent().getType();
			String selectedDashboardTitle = dashboardMainConfigBean.getDashBoardSummaryComponent().getTitle();
			if(dashboard.getEnv().equals(selectedDashboardEnv) && dashboard.getType().equals(selectedDashboardType) && dashboard.getTitle().equals(selectedDashboardTitle)) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("You cannot add a dashboard to itself");
				return;
			}
			
			dashboards.remove(dashboard);
			
			itemConfig.getItem().setEnv(dashboard.getEnv());
			itemConfig.getItem().setTitle(dashboard.getTitle());
			itemConfig.getItem().setType(dashboard.getType());
			
			NavigationItemType navigationItem  = itemConfig.getItem();
			
			NavItemSelection item = new NavItemSelection(navigationItem);
			navItems.add(item);

			// reset the future item to add
			ObjectFactory objFactory = new ObjectFactory();
			navigationItem = objFactory.createNavigationBoardTypeNavigationItemType();
			itemConfig.setItem(navigationItem);
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public void addAllDashboards(ActionEvent action) {
		try {
			DashboardMainConfigBean dashboardMainConfigBean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			String selectedDashboardEnv = dashboardMainConfigBean.getDashBoardSummaryComponent().getEnv();
			String selectedDashboardType = dashboardMainConfigBean.getDashBoardSummaryComponent().getType();
			String selectedDashboardTitle = dashboardMainConfigBean.getDashBoardSummaryComponent().getTitle();
			List<DashBoardSummaryComponent> tempDashboardsList = new ArrayList<DashBoardSummaryComponent>();
			
			for(DashBoardSummaryComponent dashboard : dashboards) {
				if ((dashboard.getEnv().equals("global") && dashboard.getType().equals("alertBoard") && dashboard.getTitle().equals("DashBoard"))
						|| (dashboard.getEnv().equals(selectedDashboardEnv) && dashboard.getType().equals(selectedDashboardType) && dashboard.getTitle().equals(selectedDashboardTitle))) {
					continue;
				}
				else {
					tempDashboardsList.add(dashboard);
					
					itemConfig.getItem().setEnv(dashboard.getEnv());
					itemConfig.getItem().setTitle(dashboard.getTitle());
					itemConfig.getItem().setType(dashboard.getType());
					
					NavigationItemType navigationItem  = itemConfig.getItem();
					
					NavItemSelection item = new NavItemSelection(navigationItem);
					navItems.add(item);

					// reset the future item to add
					ObjectFactory objFactory = new ObjectFactory();
					navigationItem = objFactory.createNavigationBoardTypeNavigationItemType();
					itemConfig.setItem(navigationItem);
				}
			}
			for(DashBoardSummaryComponent dashboard : tempDashboardsList) {
				dashboards.remove(dashboard);
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public void addItem(ActionEvent action) {
		try {
			// add the new Item
			NavigationItemType navigationItem  = itemConfig.getItem();
			if (navigationItem.getEnv().equals("global") && navigationItem.getType().equals("alertBoard") && navigationItem.getTitle().equals("DashBoard")) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("You cannot add the master dashboard in a navigation board");
				return;
			}
			
			DashboardMainConfigBean dashboardMainConfigBean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			String selectedDashboardEnv = dashboardMainConfigBean.getDashBoardSummaryComponent().getEnv();
			String selectedDashboardType = dashboardMainConfigBean.getDashBoardSummaryComponent().getType();
			String selectedDashboardTitle = dashboardMainConfigBean.getDashBoardSummaryComponent().getTitle();
			
			if(navigationItem.getEnv().equals(selectedDashboardEnv) && navigationItem.getType().equals(selectedDashboardType) && navigationItem.getTitle().equals(selectedDashboardTitle)) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("You cannot add a dashboard to itself");
				return;
			}
			
			NavItemSelection item = new NavItemSelection(navigationItem);
			navItems.add(item);

			// reset the future item to add
			ObjectFactory objFactory = new ObjectFactory();
			navigationItem = objFactory.createNavigationBoardTypeNavigationItemType();
			itemConfig.setItem(navigationItem);	
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public void removeDashboard(ActionEvent action) {
		NavItemSelection item = (NavItemSelection)action.getComponent().getAttributes().get("item");
		navItems.remove(item);
		
		itemConfig.getItem().setEnv(item.getNavigationItem().getEnv());
		itemConfig.getItem().setTitle(item.getNavigationItem().getTitle());
		itemConfig.getItem().setType(item.getNavigationItem().getType());
		
		for (DashBoardSummaryComponent dashboard : itemConfig.getSummaries()) {
			if(item.getNavigationItem().getEnv().equals(dashboard.getEnv())
					&& item.getNavigationItem().getTitle().equals(dashboard.getTitle())
						&& item.getNavigationItem().getType().equals(dashboard.getType()))
				dashboards.add(dashboard);
		}
	}
	
	public void removeAllDashboards(ActionEvent action) {
		Iterator<NavItemSelection> itemIt = navItems.iterator();
		
		while (itemIt.hasNext()) {
			NavItemSelection item = itemIt.next();
			itemIt.remove();
			
			itemConfig.getItem().setEnv(item.getNavigationItem().getEnv());
			itemConfig.getItem().setTitle(item.getNavigationItem().getTitle());
			itemConfig.getItem().setType(item.getNavigationItem().getType());
			
			for (DashBoardSummaryComponent dashboard : itemConfig.getSummaries()) {
				if(item.getNavigationItem().getEnv().equals(dashboard.getEnv())
						&& item.getNavigationItem().getTitle().equals(dashboard.getTitle())
							&& item.getNavigationItem().getType().equals(dashboard.getType())) {
					dashboards.add(dashboard);
					break;
				}
			}
		}
		
		selected = false;
	}
	
	public void removeItems(ActionEvent action) {
		Iterator<NavItemSelection> itemIt = navItems.iterator();
		
		while (itemIt.hasNext()) {
			NavItemSelection item = itemIt.next();
			if (item.isSelected()) {
				itemIt.remove();
			}		
		}
		
		selected = false;
	}
		
	public void save() {
		if (navItems.isEmpty()) {
			errorDashboardListVisible = true;
//			error = true;
//			message = "DashBoards list cannot be empty";
//			setAction("navigationBoardConfig");
			return;
		}
		
		if (navigationBoard.getTitle().trim().length() == 0) {
			errorDashboardListVisible = false;
			errorNavigationBoardTitleVisible = true;
//			error = true;
//			message = "NavigationBoard Title cannot be empty";
//			setAction("navigationBoardConfig");
			return;	
		}
		
		errorDashboardListVisible = false;
		errorNavigationBoardTitleVisible = false;
		
		RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
		navigationBoard.getNavigationItem().clear();
		
		for(NavItemSelection item : navItems) {
			navigationBoard.getNavigationItem().add(item.getNavigationItem());
		}
		
		if (navigationBoard.getId() == null || navigationBoard.getId().length() == 0) {
			factory.addNewNavigationBoardComponent(env, type, title, navigationBoard);
		} else {
			factory.updateNavigationBoardComponent(env, type, title, navigationBoard);
		}
		
		DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
		bean.resetSelectedDashBoard();
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
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public class NavItemSelection {
		
		private boolean selected = false;
		private NavigationItemType navItem;
		
		public NavItemSelection(NavigationItemType navItem) {
			this.navItem = navItem;
			this.selected = false;
		}
		
		public NavigationItemType getNavigationItem() {
			return navItem;
		}
		
		public boolean isSelected() {
			return selected;
		}
		
		public void setSelected(boolean selected) {}
		
		public void updateSelected(boolean selected) {
			this.selected = selected;
		}
		
		public void onChangeSelected(ValueChangeEvent evnt) {	
			this.selected = (Boolean)evnt.getNewValue();
		}	
		
		public boolean equals(Object other) {
			NavigationItemType oni = ((NavItemSelection)other).getNavigationItem();
			boolean res = (navItem.getEnv().equals(oni.getEnv()) &&
					navItem.getType().equals(oni.getType()) &&
					navItem.getTitle().equals(oni.getTitle()));
			
			return res;
		}
		
		public int hashCode() {
			return navItem.getTitle().length();
		}
	}

	public String getNavigationPanelStyle() {
		if(errorDashboardListVisible && !getListRenderer())
			return "border: 1px solid red; text-align: center; width: 462px; height: 290px;";
		else
			return "border: 1px solid #336699; text-align: center; width: 462px; height: 290px;";
	}

	public String getNavigationTitleStyle() {
		if(errorNavigationBoardTitleVisible || titleError)
			return "border: 1px solid red;";
		else
			return "";
	}
	
	public boolean getListRenderer() {
		if(navItems.size() != 0)
			return true;
		else
			return false;
	}

	public void setTitleError(boolean titleError) {
		this.titleError = titleError;
	}
}
