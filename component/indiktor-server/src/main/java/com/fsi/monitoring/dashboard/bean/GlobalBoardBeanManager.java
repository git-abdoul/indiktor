package com.fsi.monitoring.dashboard.bean;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.NavigationBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardGridComponent;
import com.fsi.monitoring.dashboard.component.batch.BatchBoardComponent;
import com.fsi.monitoring.dashboard.component.chart.IkrChartComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponents;
import com.fsi.monitoring.dashboard.component.info.InfoComponent;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.ext.HtmlPanelGroup;


public class GlobalBoardBeanManager
extends GlobalBoardBean {
	
	private static final Logger logger = Logger.getLogger(GlobalBoardBeanManager.class);		
	
	private Map<DashBoardComponent,String> newStyles;
	
	private boolean noAccessDashboardVisualization = false;
	
	public void init(ActionEvent action) {
		if (!isAuthorized(10,"")) {
			noAccessDashboardVisualization = true;
			return;
		}
		super.navigate(action);
		
		newStyles = new HashMap<DashBoardComponent, String>();
	}
	
	public Map<String, NavigationBoardComponent> getNavigationBoardMap() {		
		Map<String,NavigationBoardComponent> res = new HashMap<String,NavigationBoardComponent>(0);
		DashBoardComponents dashboardComponents = getComponents();
		if (dashboardComponents != null) {
			Collection<NavigationBoardComponent> components = dashboardComponents.getNavigationBoardComponents();
			if (components != null) {
				res = new HashMap<String,NavigationBoardComponent>(components.size());
				
				int i=0;
				for (NavigationBoardComponent component : components) {
					res.put("navBoard"+ (i++), component);
				}
			}
		}
		return res;
	}		

	public Map<String,InfoComponent> getInfoBoardMap() {
		Map<String,InfoComponent> res = new HashMap<String,InfoComponent>(0);
		DashBoardComponents dashboardComponents = getComponents();
		if (dashboardComponents != null) {
			Collection<InfoComponent> components = dashboardComponents.getInfoBoardComponents();
			if (components != null) {
				res = new HashMap<String,InfoComponent>(components.size());
			
				int i=0;
				for (InfoComponent component : components) {
					res.put("infoBoard" + (i++), component);
				}
			}
		}
		return res;
	}			

	public Map<String,BatchBoardComponent> getBatchBoardMap() {
		Map<String,BatchBoardComponent> res = new HashMap<String,BatchBoardComponent>(0);
		DashBoardComponents dashboardComponents = getComponents();
		if (dashboardComponents != null) {
			Collection<BatchBoardComponent> components = dashboardComponents.getBatchBoardComponents();
			if (components != null) {
				res = new HashMap<String,BatchBoardComponent>(components.size());
			
				int i=0;
				for (BatchBoardComponent component : components) {
					res.put("batchBoard" + (i++), component);
				}
			}
		}
		return res;
	}			

	public Map<String,AlertBoardGridComponent> getAlertBoardGridMap() {
		Map<String,AlertBoardGridComponent> res = new HashMap<String,AlertBoardGridComponent>(0);
		DashBoardComponents dashboardComponents = getComponents();
		if (dashboardComponents != null) {
			Collection<AlertBoardGridComponent> components = dashboardComponents.getAlertBoardGridComponents();
			if (components != null) {
				res = new HashMap<String,AlertBoardGridComponent>(components.size());
			
				int i=0;
				for (AlertBoardGridComponent component : components) {
					res.put("alertBoardGrid" + (i++), component);
				}
			}
		}
		return res;
	}

	public Map<String,IkrChartComponent> getIkrDefinitionChartMap() {
		Map<String,IkrChartComponent> res = new HashMap<String,IkrChartComponent>(0);
		DashBoardComponents dashboardComponents = getComponents();
		if (dashboardComponents != null) {
			Collection<IkrChartComponent> components = dashboardComponents.getIkrChartComponents();
			if (components != null) {
				res = new HashMap<String,IkrChartComponent>(components.size());
	
				int i=0;
				for (IkrChartComponent component : components) {
					res.put("ikrDefinitionChart"+ (i++), component);
				}
			}
		}
		return res;
	}	
	
//	public Map<String,IkrCategoryChartComponent> getIkrCategoryChartMap() {
//		
//		Collection<IkrCategoryChartComponent> components = getComponents().getIkrCategoryChartComponents();
//		Map<String,IkrCategoryChartComponent> res = null;
//		
//		if (components != null) {
//			res = new HashMap<String,IkrCategoryChartComponent>(components.size());
//		
//			int i=0;
//			for (IkrCategoryChartComponent component : components) {
//				res.put("ikrCategoryChart" + (i++), component);
//			}
//		} else {
//			res = new HashMap<String,IkrCategoryChartComponent>(0);
//		}
//		return res;
//	}		
	
	public void dragListener(DragEvent dragEvent){        
        if (dragEvent.getEventType() == DndEvent.DRAG_CANCEL) {
        	HtmlPanelGroup htmlPanelGroup = (HtmlPanelGroup)dragEvent.getComponent();
        	
        	String cssStyle = htmlPanelGroup.getCurrentStyle().getCssString();
        	DashBoardComponent component = (DashBoardComponent)htmlPanelGroup.getDragValue();
        	
        	System.out.println(component.getComponentId());
        	
        	System.out.println("NEW Style :");
        	System.out.println(cssStyle);
        	
        	newStyles.put(component, cssStyle);
        }
	}
	
	public boolean isNoAccessDashboardVisualization() {
		return noAccessDashboardVisualization;
	}

	public void save(ActionEvent action) {
		if (isAuthorized(85,"dashboardMainConfig") ) {			
			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();			
			DashBoardSummaryComponent summary = getComponents().getSummary();			
			factory.modifyComponentsDisplay(summary.getEnv(),
									 	    summary.getType(),
									 	    summary.getTitle(),
									 	    newStyles);
			setOnPreview(false);
		}
	}

	public void cancel(ActionEvent action) {
		setOnPreview(false);
	}
}
