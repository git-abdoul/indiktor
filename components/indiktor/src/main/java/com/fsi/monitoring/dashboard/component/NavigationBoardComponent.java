package com.fsi.monitoring.dashboard.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fsi.monitoring.alert.composite.AlertItem;
import com.fsi.monitoring.dashboard.component.framework.ComputableComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;

public class NavigationBoardComponent
extends DashBoardComponent
implements ComputableComponent {
	private static final long serialVersionUID = 5079707870082034167L;

	private Collection<DashBoardSummaryComponent> dashBoardSummaries;
	
	private int boardLevel;
	
	public NavigationBoardComponent(String componentId, 
									String title, 
									String style) {
		super(componentId, title, style, "navigationBoard", true);
		this.dashBoardSummaries = new ArrayList<DashBoardSummaryComponent>();
	}

	public void addDashBoardSummary(DashBoardSummaryComponent summary) {
		dashBoardSummaries.add(summary);
	}
		
	public Collection<DashBoardSummaryComponent> getSummaries() {
		List<DashBoardSummaryComponent> sortedList = new ArrayList<DashBoardSummaryComponent>(dashBoardSummaries);
		Collections.sort(sortedList, new Comparator<DashBoardSummaryComponent>() {
			public int compare(DashBoardSummaryComponent o1, DashBoardSummaryComponent o2) {
				String title1 = o1.getTitle().toLowerCase();
				String title2 = o2.getTitle().toLowerCase();
				return title1.compareTo(title2);			
			}
		});		
		return sortedList;
	}
	
	public int getNavigationBoardLevel() {
		return boardLevel;
	}

	private void resetLevel() {
		boardLevel = AlertItem.NO_ALERT_DEFINITION;
	}	
	
	@Override
	public void synchronize() {
		// TODO Auto-generated method stub
		
	}

	public void computeComponent() {
		resetLevel();
		if (dashBoardSummaries != null) {
			for(DashBoardSummaryComponent dashBoardSummaryComponent : dashBoardSummaries) {
				boardLevel = Math.max(boardLevel, dashBoardSummaryComponent.getBoardLevel());
			}
		}
	}
}