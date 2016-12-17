package com.fsi.monitoring.dashboard.component.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.NavigationBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardGridComponent;
import com.fsi.monitoring.dashboard.component.batch.BatchBoardComponent;
import com.fsi.monitoring.dashboard.component.chart.IkrChartComponent;
import com.fsi.monitoring.dashboard.component.info.InfoComponent;
import com.fsi.monitoring.dashboard.thread.ThreadAnalysisBean;

public class DashBoardComponents
implements Serializable {

	private static final long serialVersionUID = -9145537523008826076L;

	private DashBoardSummaryComponent			summary;
	private List<NavigationBoardComponent>		navigationBoardComponents;
	private List<AlertBoardComponent> 			alertBoardComponents;
	private List<InfoComponent> 				infoBoardComponents;
	private List<IkrChartComponent> 			ikrChartComponents;
	private ThreadAnalysisBean 					threadAnalysisComponent;
	private List<BatchBoardComponent>   		batchBoardComponents;
	private List<AlertBoardGridComponent>  		alertBoardGridComponents;
	
	public int getSize() {
		int res = 0;
	
		if (navigationBoardComponents != null) {
			res += navigationBoardComponents.size();
		}
		if (alertBoardComponents != null) {
			res += alertBoardComponents.size();
		}
		if (alertBoardGridComponents != null) {
			res += alertBoardGridComponents.size();
		}
		if (infoBoardComponents != null) {
			res += infoBoardComponents.size();
		}
		if (ikrChartComponents != null) {
			res += ikrChartComponents.size();
		}
		if (batchBoardComponents != null) {
			res += batchBoardComponents.size();
		}
		if (threadAnalysisComponent != null) {
			res += 1;
		}
		
		return res;
	}
	
	public DashBoardSummaryComponent getSummary() {
		return summary;
	}
	
	public void setSummary(DashBoardSummaryComponent summary) {
		this.summary = summary;
	}

	public Collection<NavigationBoardComponent> getNavigationBoardComponents() {
		return navigationBoardComponents;
	}
	
	public Collection<AlertBoardComponent> getAlertBoardComponents() {
		return alertBoardComponents;
	}
	
	public void addAlertBoardComponent(AlertBoardComponent alertBoardComponent) {
		if (alertBoardComponents == null) {
			alertBoardComponents = new ArrayList<AlertBoardComponent>();
		}
		alertBoardComponent.synchronize();
		alertBoardComponents.add(alertBoardComponent);
	}
	
	public void replaceAlertBoardComponent(AlertBoardComponent oldCpt,
										   AlertBoardComponent newCpt) {
		alertBoardComponents.remove(oldCpt);
		newCpt.synchronize();
		alertBoardComponents.add(newCpt);
	}
	
	public void replaceNavigationBoardComponent(NavigationBoardComponent oldCpt,
												NavigationBoardComponent newCpt) {
		navigationBoardComponents.remove(oldCpt);
		newCpt.synchronize();
		navigationBoardComponents.add(newCpt);
	}
	
	public void replaceChartComponent(IkrChartComponent oldCpt,	IkrChartComponent newCpt) {
		ikrChartComponents.remove(oldCpt);
		newCpt.synchronize();
		ikrChartComponents.add(newCpt);
	}
	
	public void replaceInfoBoardComponent(InfoComponent oldCpt,
									 	  InfoComponent newCpt) {
		infoBoardComponents.remove(oldCpt);
		newCpt.synchronize();
		infoBoardComponents.add(newCpt);
	}		
	
	public void removeAlertBoardComponent(AlertBoardComponent component) {
		alertBoardComponents.remove(component);
	}
	
	public void removeThreadComponent() {
		threadAnalysisComponent = null;
	}
	
	public void removeNavigationBoardComponent(NavigationBoardComponent component) {
		navigationBoardComponents.remove(component);
	}
	
	public void removeInfoBoardComponent(InfoComponent component) {
		infoBoardComponents.remove(component);
	}

	public void removeChartComponent(IkrChartComponent component) {
		ikrChartComponents.remove(component);
	}
	
	public void addNavigationBoardComponent(NavigationBoardComponent navigationBoardComponent) {
		if (navigationBoardComponents == null) {
			navigationBoardComponents = new ArrayList<NavigationBoardComponent>();
		}
		navigationBoardComponent.synchronize();
		navigationBoardComponents.add(navigationBoardComponent);
	}
	
	public void addInfoComponent(InfoComponent infoComponent) {
		if (infoBoardComponents == null) {
			infoBoardComponents = new ArrayList<InfoComponent>();
		}
		infoComponent.synchronize();
		infoBoardComponents.add(infoComponent);
	}

	public Collection<BatchBoardComponent> getBatchBoardComponents() {
		return batchBoardComponents;
	}

	public void addBatchBoardComponent(BatchBoardComponent batchBoardComponent) {
		if (batchBoardComponents == null) {
			batchBoardComponents = new ArrayList<BatchBoardComponent>();
		}
		batchBoardComponent.synchronize();
		batchBoardComponents.add(batchBoardComponent);
	}
	
	public void removeBatchBoardComponent(BatchBoardComponent component) {
		batchBoardComponents.remove(component);
	}	
	
	public void replaceBatchBoardComponent(BatchBoardComponent oldCpt, BatchBoardComponent newCpt) {
		batchBoardComponents.remove(oldCpt);
		newCpt.synchronize();
		batchBoardComponents.add(newCpt);
	}	
	
	public void addIkrChartComponent(IkrChartComponent component) {
		if (ikrChartComponents == null) {
			ikrChartComponents = new ArrayList<IkrChartComponent>();
		}
		component.synchronize();
		ikrChartComponents.add(component);
	}
	
	public Collection<InfoComponent> getInfoBoardComponents() {		
		return infoBoardComponents;
	}
	
	public Collection<IkrChartComponent> getIkrChartComponents() {
		return ikrChartComponents;
	}
	
	public ThreadAnalysisBean getThreadAnalysisComponent() {
		return threadAnalysisComponent;
	}
	
	public void setThreadAnalysisComponent(ThreadAnalysisBean threadAnalysisComponent) {
		this.threadAnalysisComponent = threadAnalysisComponent; 
	}

	public List<AlertBoardGridComponent> getAlertBoardGridComponents() {
		return alertBoardGridComponents;
	}

	public void addAlertBoardGridComponent(AlertBoardGridComponent alertBoardGridComponent) {
		if (alertBoardGridComponents == null) {
			alertBoardGridComponents = new ArrayList<AlertBoardGridComponent>();
		}
		alertBoardGridComponent.synchronize();
		alertBoardGridComponents.add(alertBoardGridComponent);
	}
	
	public void removeAlertBoardGridComponent(AlertBoardGridComponent component) {
		alertBoardGridComponents.remove(component);
	}	
	
	public void replaceAlertBoardGridComponent(AlertBoardGridComponent oldCpt, AlertBoardGridComponent newCpt) {
		alertBoardGridComponents.remove(oldCpt);
		newCpt.synchronize();
		alertBoardGridComponents.add(newCpt);
	}
}
