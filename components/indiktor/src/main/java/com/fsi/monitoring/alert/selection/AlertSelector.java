package com.fsi.monitoring.alert.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.bean.AlertBean;
import com.fsi.monitoring.alert.bean.AlertModifierBean;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.alert.manager.AlertManagerBean.ColumnName;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.realTime.listener.AlertRealTimeListener;
import com.fsi.monitoring.util.FacesUtils;

public class AlertSelector 
extends AbstractAlertItemSelector<AlertModifierBean> {

	private static final long serialVersionUID = -1407276855183963691L;

    private AlertWorkflow state;
    private static SelectItem[] stateItems;
    
    private Map<Long, AlertModifierBean> alertModifierBeans;
    
    static {
		stateItems = new SelectItem[6];	
		stateItems[0] = new SelectItem(null,"All State");
		stateItems[1] = new SelectItem(AlertWorkflow.UP_1, "LOW");
		stateItems[2] = new SelectItem(AlertWorkflow.UP_2, "MEDIUM");
		stateItems[3] = new SelectItem(AlertWorkflow.UP_3, "HIGH");
		stateItems[4] = new SelectItem(AlertWorkflow.DOWN,"FIXED");
		stateItems[5] = new SelectItem(AlertWorkflow.ACK, "ACKNOWLEDGED");
    }    
	
    public AlertSelector() {
		super();
		alertModifierBeans = new HashMap<Long, AlertModifierBean>();	
		displayedBeans = new ArrayList<AlertModifierBean>();
	}

	@Override
	protected void updateReferenceBeans() {
		AlertRealTimeListener listener = (AlertRealTimeListener)FacesUtils.getManagedBean("alertRTListener");
		
		List<AlertBean> alertBeans = listener.getAlertBeans();
		referenceBeans = new ArrayList<AlertModifierBean>(alertBeans.size());
		
		for (AlertBean alertBean : alertBeans) {
			AlertModifierBean alertModifierBean = alertModifierBeans.get(alertBean.getAlertDefinitionId());
			if (alertModifierBean == null) {
				alertModifierBean = new AlertModifierBean();
				alertModifierBean.setAlertPM(alertPM);
				alertModifierBeans.put(alertBean.getAlertDefinitionId(), alertModifierBean);
			}
			alertModifierBean.setAlertBean(alertBean);	
			referenceBeans.add(alertModifierBean);
		}	
	}
	
    @Override  
	protected synchronized void filterReferenceBeans() {		
		if (referenceBeans == null)
			return;
		
		displayedBeans.clear();
		
		for (AlertModifierBean alertModifierBean : referenceBeans) {
			AlertBean alertBean  = alertModifierBean.getAlertBean();
			if (alertBean.getAlert() != null &&
				isValidAlertDefinition(alertBean.getAlertDefinitionBean().getAlertDefinition()) &&
				(state == null || alertBean.getAlertState() == state)) {
						
					AlertDefinitionBean alertDefinitionBean = alertBean.getAlertDefinitionBean();
					if (alertDefinitionBean == null) {
						logger.error("No AlertDefinitionBean found for alertDefinitionId=" + alertBean.getAlertDefinitionId());
						continue;
					}
					
					AlertDefinition alertDefinition = alertDefinitionBean.getAlertDefinition();
					if (alertDefinition == null) {
						logger.error("No AlertDefinition found for alertDefinitionId=" + alertBean.getAlertDefinitionId());
						continue;
					}
					
					AlertEvent alertEvent = alertBean.getAlert().getAlertEvent();
					if (alertEvent == null)
						continue;
					
					displayedBeans.add(alertModifierBean);
			}
		}
	}
	
	public AlertWorkflow getState() {
		return state;
	}

	public void setState(AlertWorkflow state) {
		this.state = state;
	}

	public SelectItem[] getStateItems() {
		return stateItems;
	}	

	public String getStateColumnName() {
		return ColumnName.State.name();
	}

	public String getDateColumnName() {
		return ColumnName.Date.name();
	}

	public String getAcknowledgedColumnName() {
		return ColumnName.Acknowledged.name();
	}	
}
