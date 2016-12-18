package com.fsi.monitoring.dashboard.component.alert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.bean.AlertModifierBean;
import com.fsi.monitoring.alert.selection.AlertSelector;
import com.fsi.monitoring.alert.selection.AlertSelectorItemVisitor;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.dashboard.component.info.InfoDetail;

public class AlertBoardGridComponent
extends DashBoardComponent
implements AlertSelectorItemVisitor {
	private static final long serialVersionUID = 7197546706514125430L;
	
	private static final Logger logger = Logger.getLogger(AlertBoardGridComponent.class);
	
	private List<String> logicalEnvs;
	private boolean upOn;
	private boolean downOn;
	private boolean ackOn;
	private boolean lowOn;
	private boolean mediumOn;
	private boolean highOn;
	private boolean notRunningOn;
	
//	private boolean initialized = false;
	
	private String colors;
	private String columnName = "Date";	
	private boolean ascending = false;
	
    private AlertSelector alertSelector;
	private List<AlertModifierBean> alertBoardGrid;
	private List<AlertModifierBean> alertToDisplay;

	public AlertBoardGridComponent(String componentId,
								   String title,
								   String style) {
		super(componentId, title, style,  "alertBoardGrid", true);
	}
	
	public void setInfo(List<String> logicalEnvs, boolean upOn, boolean downOn, boolean ackOn,
						boolean lowOn, boolean mediumOn, boolean highOn, boolean notRunningOn) {		
		
		this.logicalEnvs = logicalEnvs;
		this.upOn = upOn;
		this.downOn = downOn;
		this.ackOn = ackOn;
		this.lowOn = lowOn;
		this.mediumOn = mediumOn;
		this.highOn = highOn;
		this.notRunningOn = notRunningOn;
	}
	
	@Override
	public void synchronize() {
		// TODO Auto-generated method stub
		
	}
	
	public AlertSelector getAlertSelector() {
		return alertSelector;
	}
	
	public Collection<AlertModifierBean> getAlertBeans() {
		alertSelector = new AlertSelector();
		alertSelector.init(this);
		alertToDisplay = new ArrayList<AlertModifierBean>();
		alertSelector.launchUpdateReferenceBeans();
		alertBoardGrid = alertSelector.getDisplayedBeans();
		
		for(AlertModifierBean alert : alertBoardGrid) {
			for (String env : logicalEnvs) {
				if(alert.getAlertBean().getAlertDefinitionBean().getLogicalEnv().getName().equals(env)) {
					if(upOn) {
						if(lowOn) {
							String state = alert.getAlertBean().getAlertState().getSeverityName();
							if(state.equals("LOW")) {
								alertToDisplay.add(alert);
							}
						}
						if(mediumOn) {
							String state = alert.getAlertBean().getAlertState().getSeverityName();
							if(state.equals("MEDIUM")) {
								alertToDisplay.add(alert);
							}
						}
						if(highOn) {
							String state = alert.getAlertBean().getAlertState().getSeverityName();
							if(state.equals("HIGH")) {
								alertToDisplay.add(alert);
							}
						}
						if(notRunningOn) {
							String state = alert.getAlertBean().getAlertState().getSeverityName();
							if(state.equals("NOT RUNNING")) {
								alertToDisplay.add(alert);
							}
						}
					}
					if(downOn) {
						String state = alert.getAlertBean().getAlertState().getSeverityName();
						if(state.equals("DOWN")) {
							alertToDisplay.add(alert);
						}
					}
					if(ackOn) {
						String state = alert.getAlertBean().getAlertState().getSeverityName();
						if(state.equals("ACKNOWLEGDE")) {
							alertToDisplay.add(alert);
						}
					}
				}
			}
		}
		
		if(alertToDisplay != null && alertToDisplay.size()>0) {
			Collections.sort(alertToDisplay, new Comparator<AlertModifierBean>() {
				public int compare(AlertModifierBean o1, AlertModifierBean o2) {
					Integer res = 0;					
					if ("State".equals(columnName))
						res = ascending ? o1.getAlertBean().getAlertState().getSeverityName().toLowerCase().compareTo(o2.getAlertBean().getAlertState().getSeverityName().toLowerCase()) :  o2.getAlertBean().getAlertState().getSeverityName().toLowerCase().compareTo(o1.getAlertBean().getAlertState().getSeverityName().toLowerCase());
					else if ("Date".equals(columnName))
						res = ascending ? o1.getAlertBean().getAlert().getAlertEvent().getEventDate().compareTo(o2.getAlertBean().getAlert().getAlertEvent().getEventDate()) :  o2.getAlertBean().getAlert().getAlertEvent().getEventDate().compareTo(o1.getAlertBean().getAlert().getAlertEvent().getEventDate());
					else if ("Label".equals(columnName))
						res = ascending ? o1.getAlertBean().getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase().compareTo(o2.getAlertBean().getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase()) :  o2.getAlertBean().getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase().compareTo(o1.getAlertBean().getAlertDefinitionBean().getAlertDefinition().getName().toLowerCase());
					else if ("Environment".equals(columnName))
						res = ascending ? o1.getAlertBean().getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase().compareTo(o2.getAlertBean().getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase()) :  o2.getAlertBean().getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase().compareTo(o1.getAlertBean().getAlertDefinitionBean().getLogicalEnv().getName().toLowerCase());
					return res;
				}
			});
		}
		
		computeColors(alertToDisplay);
//		initialized = true;
		return alertToDisplay;
	}
	
	private void computeColors(Collection<AlertModifierBean> displayBeans) {
		//	System.out.println("Compute colors");
		StringBuffer colorBuffer = new StringBuffer();	
		for (AlertModifierBean alertModifierBean : displayBeans) {
			colorBuffer.append(alertModifierBean.getAlertBean().getColorStr());
			colorBuffer.append(",");
		}
		colors = colorBuffer.toString();
	}

	public String getColors() {
		return colors;
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

	public void displayBeansUpdated() {
	}
	
//	public String getScrollHeight() {
//		if(!initialized)
//			getAlertBeans();
//		
//		if(alertToDisplay.size() > 9)
//			return "270px";
//		else
//			return "auto";
//	}
}
