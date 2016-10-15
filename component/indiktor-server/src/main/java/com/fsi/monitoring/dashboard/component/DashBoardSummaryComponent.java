package com.fsi.monitoring.dashboard.component;

import java.util.Collection;

import com.fsi.monitoring.alert.composite.AlertItem;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardComponent;
import com.fsi.monitoring.dashboard.component.framework.ComputableComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.util.MessageBundleLoader;

public class DashBoardSummaryComponent 
extends DashBoardComponent 
implements ComputableComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3936810279797877176L;
	
	private String env;
	private String type;
	
	private boolean selected = false;
	private boolean masterDashboards = false;

	private Collection<AlertBoardComponent> alertBoardComponents;
	private Collection<NavigationBoardComponent> navigationBoardComponents;

	private int boardLevel;
	
	public DashBoardSummaryComponent(String componentId,
									 String title,
									 String style,
									 String env,
									 String type,
									 Collection<AlertBoardComponent> alertBoardComponents,
									 Collection<NavigationBoardComponent> navigationBoardComponents) {
		super(componentId, title, style, "", true);
		this.env = env;
		this.type = type;
		
		this.alertBoardComponents = alertBoardComponents;
		this.navigationBoardComponents = navigationBoardComponents;
		computeComponent();
	}	
	
	@Override
	public void synchronize() {
		// TODO Auto-generated method stub
		
	}
	
	public void updateAlertBoardComponents(Collection<AlertBoardComponent> alertBoardComponents) {
		this.alertBoardComponents = alertBoardComponents;
		computeComponent();	
	}
	
	public void updateNavigationBoardComponents(Collection<NavigationBoardComponent> navigationBoardComponents) {
		this.navigationBoardComponents = navigationBoardComponents;
		computeComponent();
	}
	
	private void resetLevel() {
		boardLevel = AlertItem.NO_ALERT_DEFINITION;
	}
	
	public String getType() {
		return type;
	}
	
	public String getEnv() {
		return env;
	}
	
	public void computeComponent() {
		resetLevel();
		if (alertBoardComponents != null) {
			for(AlertBoardComponent alertBoardComponent : alertBoardComponents) {
				boardLevel = Math.max(boardLevel, alertBoardComponent.getAlertComposite().getLevel());
			}
		}
		
		if (navigationBoardComponents != null) {
			for (NavigationBoardComponent navigationBoardComponent : navigationBoardComponents) {
				boardLevel = Math.max(boardLevel, navigationBoardComponent.getNavigationBoardLevel());
			}
		}
	}
	
	public int getBoardLevel() {
		return boardLevel;
	}
	
	public String getUrl() {
		return "/" + MessageBundleLoader.getMessage("alert.img." + boardLevel);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Collection<AlertBoardComponent> getAlertBoardComponents() {
		return alertBoardComponents;
	}

	public boolean isMasterDashboards() {
		return masterDashboards;
	}

	public void setMasterDashboards(boolean masterDashboards) {
		this.masterDashboards = masterDashboards;
	}
}
