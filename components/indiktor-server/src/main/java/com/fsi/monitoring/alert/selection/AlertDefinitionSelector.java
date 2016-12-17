package com.fsi.monitoring.alert.selection;

import java.util.ArrayList;
import java.util.Map;

import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.util.FacesUtils;

public class AlertDefinitionSelector 
extends AbstractAlertItemSelector<AlertDefinitionSelectionBean> {

	private static final long serialVersionUID = -1407276855183963691L;

	public void updateReferenceBeans() {
		try {
			Map<Long, AlertDefinition> alertDefinitions = alertPM.getAlertDefinitions();			
			
			referenceBeans = new ArrayList<AlertDefinitionSelectionBean>(alertDefinitions.size());
			
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			
			for(AlertDefinition alertDefinition : alertDefinitions.values()) {
				AlertDefinitionBean alertDefinitionBean = beanPM.getAlertDefinitionBean(alertDefinition.getId());
				
				AlertDefinitionSelectionBean referenceBean = new AlertDefinitionSelectionBean(alertDefinitionBean);
				referenceBeans.add(referenceBean);
			}
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}		
	}
	
	public synchronized void filterReferenceBeans() {
		displayedBeans = new ArrayList<AlertDefinitionSelectionBean>();
		
		if (referenceBeans!=null){
			for (AlertDefinitionSelectionBean alertDefinitionSelectionBean : referenceBeans) {
				AlertDefinition alertDefinition = alertDefinitionSelectionBean.getAlertDefinitionBean().getAlertDefinition();
				if (isValidAlertDefinition(alertDefinition) &&
					(active.equals("N/A") || alertDefinition.isEnable() == Boolean.valueOf(active))) {
	
					displayedBeans.add(alertDefinitionSelectionBean);
				}
			}
		}
	}
}
