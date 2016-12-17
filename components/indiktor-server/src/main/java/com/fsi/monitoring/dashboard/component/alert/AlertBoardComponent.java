package com.fsi.monitoring.dashboard.component.alert;



import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.fsi.monitoring.alert.bean.AlertBean;
import com.fsi.monitoring.alert.composite.AlertComposite;
import com.fsi.monitoring.alert.composite.AlertLeaf;
import com.fsi.monitoring.dashboard.component.framework.ComputableComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.dashboard.component.info.InfoDetail;


public class AlertBoardComponent
extends DashBoardComponent 
implements ComputableComponent {

	private static final long serialVersionUID = -7179367980584357957L;

	private static final Logger logger = Logger.getLogger(AlertBoardComponent.class);
	
	private AlertComposite alertComposite;
	
	private boolean showAllAlerts = true;
	private String commandLinkTitle;
	
	public AlertBoardComponent(String componentId,
							   String title,
							   String style,
							   AlertComposite alertComposite) {
		super(componentId, title, style, "alertBoard", true);
		this.alertComposite = alertComposite;
		computeComponent();
	}
	
	@Override
	public void synchronize() {
		// TODO Auto-generated method stub
		
	}
	
	public AlertComposite getAlertComposite() {
		return alertComposite;
	}

	public Collection<AlertLeaf> getAlertLeafs() {
		return alertComposite.getAlertLeafs();
	}

	public void computeComponent() {
		alertComposite.updateLevel();
	}
	
	public boolean isShowAllAlerts() {
		return showAllAlerts;
	}
	
	public String getCommandLinkTitle() {
		if (showAllAlerts == true)
			commandLinkTitle = "(Show raised alerts only)";
		else
			commandLinkTitle = "(Show all alerts)";
		return commandLinkTitle;
	}

	public void showAlertsOn(ActionEvent event) {
		showAllAlerts = !showAllAlerts;
	}	
	
	public String getScrollHeight() {
		Collection<AlertLeaf> alertLeafs = alertComposite.getAlertLeafs();
		int nAlerts = 0;
		for(AlertLeaf alertLeaf : alertLeafs) {
			if(showAllAlerts) {
				Collection<AlertBean> alertBeans = alertLeaf.getAllAlertBeans();
				nAlerts = nAlerts + alertBeans.size();
			}
			else {
				Collection<AlertBean> alertBeans = alertLeaf.getAlertBeans();
				nAlerts = nAlerts + alertBeans.size();		
			}
		}
		if(nAlerts > 10)
			return "270px";
		else
			return "auto";	
	}	
	
	public String getScrollHeightOnPreview() {
		Collection<AlertLeaf> alertLeafs = alertComposite.getAlertLeafs();
		int nAlerts = 0;
		for(AlertLeaf alertLeaf : alertLeafs) {
			Collection<AlertBean> alertBeans = alertLeaf.getAllAlertBeans();
			nAlerts = nAlerts + alertBeans.size();
		}
		if(nAlerts > 10)
			return "270px";
		else
			return "auto";	
	}
}
