package com.fsi.monitoring.computeServer.alert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.AlertComputeResolution;
import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.action.AlertAction;
import com.fsi.monitoring.alert.action.AlertAction.AlertActionType;
import com.fsi.monitoring.alert.action.SnmpAlertAction;
import com.fsi.monitoring.alert.action.UserAlertAction;
import com.fsi.monitoring.alert.workflow.AlertComputeEvent;
import com.fsi.monitoring.computeServer.config.ComputeServerContext;
import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.snmp.SnmpConfig;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.UserPM;
import com.fsi.publisher.email.EmailMessage;
import com.fsi.publisher.service.Message;
import com.fsi.publisher.snmp.AlertSnmpMessage;
import com.fsi.publisher.snmp.SnmpClient;


public class AlertMessage 
extends Message {
	private static final Logger logger = Logger.getLogger(AlertMessage.class);	
	private static final long serialVersionUID = -2746474808146725271L;
	MonitoringPM monitoringPM = (MonitoringPM)ComputeServerContext.getBean(PersistencyBeanName.monitoringPM);
	DataModelPM dataModelPM = (DataModelPM)ComputeServerContext.getBean(PersistencyBeanName.dataModelPM);
	
	public AlertMessage(AlertDefinition alertDefinition, Alert alert) {
	
		for (AlertAction alertAction : alertDefinition.getAlertActions()) {
			if (alertAction instanceof UserAlertAction) {
				UserAlertAction userAlertAction = (UserAlertAction)alertAction;
				Collection<AlertActionType> types = userAlertAction.getTypes();
				if (types.contains(AlertActionType.MAIL)) {
					String subject = "INDIKTOR ALERT EVENT : " + alertDefinition.getName() + " occured";
					
					String content =
						 " Alert Name    : " + alertDefinition.getName() + "\n" +
						 " Date          : " + alert.getAlertEvent().getEventDate() + "\n" +
						 " State         : " + alert.getState().name() + "\n" +
						 " Severity      : " + alert.getState().getSeverityName()+ "\n" +
						 " Description   : " + alertDefinition.getDescription() + "\n" + 
						 "\n" + 
						 "\n" +	
					 	 "Metrics involved : \n" + 
					 	 "------------------ \n" ;
					 	 
						
					AlertComputeEvent event = (AlertComputeEvent)alert.getAlertEvent();
					for(AlertComputeResolution res : event.getAlertComputeResolutions()) {
						for (Long ikrValueId : res.getIkrValueIds()) {
							try {
								IkrValue value = monitoringPM.getIkrValue(ikrValueId);
								AbstractIkrDefinition ikrDefinition = monitoringPM.getIkrDefinition(value.getValueDefinitionId());
								IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(value.getIkrCategoryId());
								IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(ikrCategory.getParentDomainId());
								IkrStaticDomain domainType = dataModelPM.getIkrStaticDomain(metricDomain.getParentDomainId());
								content = content + "\n" + " Domain Type    : " + domainType.getLabel();
								content = content + "\n" + " Metric Domain  : " + metricDomain.getLabel();
								content = content + "\n" + " Metric Category: " + ikrCategory.getLabel();	
								content = content + "\n" + " Metric Name    : " + ikrDefinition.getIkrInstance();
								if (ikrDefinition instanceof IkrDefinition) {
									MonitorConfig monitor = dataModelPM.getMonitorConfig(((IkrDefinition)ikrDefinition).getMonitorId());
									if (monitor != null)
										content = content + "\n" + " Context        : " + monitor.getContext();
								}								
								FormattedValue formattedValue = ikrCategory.getIkrUnitType().format(value.getValue(), ikrCategory.getIkrUnit());								
								content = content + "\n" + " Value          : " + formattedValue.getValue() + " " + formattedValue.getIkrUnit().getSymbol(); 
								content = content + "\n";
							} catch (PersistenceException e) {
								logger.error(e.getMessage());
							}
						}
					}
					
					Set<User> users = getUsers(userAlertAction.getUserIds());	
					emailMessage = new EmailMessage(users, subject, content);
				}
				
				if (types.contains(AlertActionType.SMS)) {
					/**
					 * TODO SEND SMS
					 */
				}
			}
			
			if (alertAction instanceof SnmpAlertAction) {
				SnmpAlertAction snmpAlertAction = (SnmpAlertAction)alertAction;		
				Collection<AlertActionType> types = snmpAlertAction.getTypes();
				if (types.contains(AlertActionType.SNMP)) {
					List<SnmpClient> clients = new ArrayList<SnmpClient>();
					Set<SnmpConfig> confs = getSnmpConfigs(snmpAlertAction.getSnmpConfigIds());
					for(SnmpConfig conf : confs) {
						clients.add(new SnmpClient(conf));
					}				
					snmpMessage = new AlertSnmpMessage(alertDefinition, alert, clients);
				}
			}
			
		}
	}
	
	private Set<User> getUsers(Collection<Long> userIds) {
		UserPM userPM = (UserPM)ComputeServerContext.getBean(PersistencyBeanName.userPM);
		
		Map<Long, User> users = userPM.getUsers(userIds);
		Set<User> res = new HashSet<User>(users.size());
		
		for(User user : users.values()) {
			res.add(user);
		}
		return res;
	}
	
	private Set<SnmpConfig> getSnmpConfigs(Collection<Long> snmpConfigIds) {
		Set<SnmpConfig> res = new HashSet<SnmpConfig>();		
		AlertPM alertPM = (AlertPM)ComputeServerContext.getBean(PersistencyBeanName.alertPM);		
		Collection<SnmpConfig> configs;
		try {
			configs = alertPM.getSnmpConfigs(snmpConfigIds);
			for(SnmpConfig conf : configs) {
				res.add(conf);
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}				
		return res;
	}
	
	
}
