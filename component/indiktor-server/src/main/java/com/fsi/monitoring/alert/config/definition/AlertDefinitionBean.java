package com.fsi.monitoring.alert.config.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.action.AlertAction;
import com.fsi.monitoring.alert.action.SnmpAlertAction;
import com.fsi.monitoring.alert.action.UserAlertAction;
import com.fsi.monitoring.alert.condition.AlertCondition;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.monitor.LogicalEnvBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.user.UserPM;

public class AlertDefinitionBean 
implements Serializable {

	private static final long serialVersionUID = -9150356721249611952L;	
	
	protected final static Logger logger = Logger.getLogger(AlertDefinitionBean.class);	
	
	private AlertDefinition alertDefinition;
	
	private List<AlertConditionBean> alertConditionBeans;
	
	private AlertUserActionBean alertUserActionBean;
	private AlertSnmpActionBean alertSnmpActionBean;
	
	private String group;
	private String domain;
	private String subDomain;
	
	private LogicalEnv logicalEnv;
	
	public AlertDefinitionBean(AlertDefinition alertDefinition,
							   AlertPM alertPM,
							   DataModelPM dataModelPM,
							   MonitoringPM monitoringPM,
							   UserPM userPM,
							   BeanPM beanPM){
		this.alertDefinition = alertDefinition;		
		try {
			LogicalEnvBean logicalEnvBean = beanPM.getLogicalEnvBean(alertDefinition.getLogicalEnv());
			if (logicalEnvBean!=null)
				logicalEnv = logicalEnvBean.getLogicalEnv();
			
			Map<Integer, String> groups = alertPM.getExistingAlertGroups(alertDefinition.getLogicalEnv());
			group = groups.get(alertDefinition.getGroup());
			if (group == null)
				group = "";
			
			Map<Integer, String> domains = alertPM.getExistingAlertDomains(alertDefinition.getLogicalEnv(),
																		   alertDefinition.getGroup());
			domain = domains.get(alertDefinition.getDomain());
			if (domain == null)
				domain = "";
			
			Map<Integer, String> subDomains = alertPM.getExistingAlertSubDomains(alertDefinition.getLogicalEnv(),
																		   		 alertDefinition.getGroup(),
																		   		 alertDefinition.getDomain());
			subDomain = subDomains.get(alertDefinition.getSubDomain());
			if (subDomain == null)
				subDomain = "";
			
			alertConditionBeans = new ArrayList<AlertConditionBean>();
			for (AlertCondition alertCondition : alertDefinition.getAlertConditions()) {
				AbstractIkrDefinition ikrDefinition = monitoringPM.getIkrDefinition(alertCondition.getIkrDefinitionId());				
				if (ikrDefinition != null) {
					IkrDefinitionBean beanDef = beanPM.getIkrDefinitionBean(ikrDefinition.getId());
					AlertConditionBean alertConditionBean = new AlertConditionBean(alertCondition,beanDef);
					alertConditionBeans.add(alertConditionBean);
				}
			}
			
			List<AlertAction> alertActions = (List<AlertAction>)alertDefinition.getAlertActions();
			if (alertActions != null && alertActions.size() >0) {
				for (AlertAction alertAction : alertActions) {
					if (alertAction instanceof UserAlertAction) {
						alertUserActionBean = new AlertUserActionBean((UserAlertAction)alertAction,userPM);
					} else {
						alertSnmpActionBean = new AlertSnmpActionBean((SnmpAlertAction)alertAction,alertPM);
					}
				}
			} 
			if (alertUserActionBean == null) {
				UserAlertAction userAlertAction = new UserAlertAction();
				alertUserActionBean = new AlertUserActionBean(userAlertAction,userPM);			
				alertDefinition.addAlertAction(userAlertAction);
			}
			
			if (alertSnmpActionBean == null) {
				SnmpAlertAction snmpAlertAction = new SnmpAlertAction();
				alertSnmpActionBean = new AlertSnmpActionBean(snmpAlertAction,alertPM);
				alertDefinition.addAlertAction(snmpAlertAction);
			}			
			
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}	

	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}


	public void setLogicalEnv(LogicalEnv logicalEnv) {
		this.logicalEnv = logicalEnv;
	}

	public AlertDefinition getAlertDefinition() {
		return alertDefinition;
	}		

	public List<AlertConditionBean> getAlertConditionBeans() {
		return alertConditionBeans;
	}
	
	public AlertSnmpActionBean getAlertSnmpActionBean() {
		return alertSnmpActionBean;
	}
	
	public AlertUserActionBean getAlertUserActionBean() {
		return alertUserActionBean;
	}	
	
	public boolean isHowToRendered() {
		String howTo = alertDefinition.getHowTo();
		return (howTo!=null && howTo.length()>0) ;
	}	
	
	public String getGroup() {
		return group;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String getSubDomain() {
		return subDomain;
	}
}
