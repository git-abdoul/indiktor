package com.fsi.monitoring.computeServer.alert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.nfunk.jep.JEP;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.AlertCompute;
import com.fsi.monitoring.alert.AlertComputeResolution;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;
import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.AlertValidity;
import com.fsi.monitoring.alert.condition.AlertCondition;
import com.fsi.monitoring.alert.condition.AlertConditionResolver;
import com.fsi.monitoring.alert.workflow.AlertComputeEvent;
import com.fsi.monitoring.alert.workflow.AlertEvent;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.alert.workflow.AlertWorkflow.Status;
import com.fsi.monitoring.computeServer.config.ComputeServerContext;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrValue;


public class ComputeAlert {	
	private static final Logger logger = Logger.getLogger(ComputeAlert.class);	
	
	private DataModelPM dataModelPM;
	
	private static ThreadLocal<ComputeServerAlertConditionResolver> alertConditionResolver = null;
	
	private Map<Long,IkrValue> ikrValuesCache;

	public ComputeAlert() {
		ikrValuesCache = new HashMap<Long, IkrValue>();
		alertConditionResolver = new ThreadLocal<ComputeServerAlertConditionResolver>() {
			@Override protected ComputeServerAlertConditionResolver initialValue() {
	            return new ComputeServerAlertConditionResolver();
			}
		};
	}
	
//	public Alert addAlertComment(long alertDefinitionId, AlertCommentEvent event) {
//		AlertPM alertPM = (AlertPM)ComputeServerContext.getBean(PersistencyBeanName.alertPM);
//		
//		Alert alert = alertPM.getAlert(alertDefinitionId);
//		
//		synchronized (alert) {
//			if (alert.getState() == event.getOldState() || event.getNewState() != AlertWorkflow.ACK) {
//
//				// can't acknowledged an alert if the status has changed or if it is already ACK
//				
//				alert.addEvent(event);
//				try {
//					alertPM.createAlertEvent(alert,event);
//				} catch (Exception exc) {
//					logger.error("Error while saving alerts when adding comment", exc);
//				}
//			}
//		}
//		return alert;
//	}	
	
	
	public Collection<Alert> getAlerts() {
		
		AlertPM alertPM = (AlertPM)ComputeServerContext.getBean(PersistencyBeanName.alertPM);
		
		Map<Long, Alert> alerts = null;
		try {
			alerts = alertPM.getAlerts();
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}		
		
		Collection<Alert> res = null;
		
		if (alerts != null) {
			res = new ArrayList<Alert>(alerts.values());
		}
			
		return res;
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}

	public Collection<Alert> computeAlerts(Collection<Long> ikrDefinitionIds, Map<Long,List<IkrValue>> currentIkrValuesCache) {
		if (ikrDefinitionIds.size() == 0) {
			return null;
		}
		
		logger.debug("------------------ ALERTS COMPUTATION ------------------");
		logger.debug("Nb IkrDefinition fetched : " + ikrDefinitionIds.size());
		
		AlertPM alertPM = (AlertPM)ComputeServerContext.getBean(PersistencyBeanName.alertPM);
		
		Map<Long,AlertDefinition> alertDefinitions = alertPM.getAlertDefinitionsByIkrDefinitionIds(ikrDefinitionIds);		
		Collection<Alert> alerts = new ArrayList<Alert>(alertDefinitions.size());
		
		logger.debug("Nb corresponding AlertDefinition found : " + alertDefinitions.size());
		
		try {
			for (AlertDefinition alertDefinition : alertDefinitions.values()) {				
				if (!isAlertValid(alertDefinition)) {
					continue;
				}
				
				long alertDefinitionId = alertDefinition.getId();
				Alert alert = alertPM.getAlert(alertDefinitionId);
				
				logger.debug("=== Start AlertDefinition analysis === id: " + alertDefinition.getId() + ", name: " + alertDefinition.getName());
				
				if (alert == null) {
					// First time this alert is raised since restart
					alert = new Alert(alertDefinitionId);
					
					List<AlertEvent> events = new ArrayList<AlertEvent>();					
					List<AlertEvent> commentEvents = alertPM.getAlertEvents(alertDefinitionId, "COMMENT");
					events.addAll(commentEvents);
					List<AlertEvent> computeEvents = alertPM.getAlertEvents(alertDefinitionId, "COMPUTE");
					events.addAll(computeEvents);
					Collections.sort(events, new Comparator<AlertEvent>() {
						public int compare(AlertEvent o1, AlertEvent o2) {
							return o1.getEventDate().compareTo(o2.getEventDate());
						}
					});
					
					for (AlertEvent alertEvent : events) {
						alert.addEvent(alertEvent, false);
					}
					
					logger.debug("!!! No alert found in the cache, this alert is raised for the first time");
				}
				
				synchronized (alert) {
					// Update Conditions Status
					logger.debug("-- Conditions analysis --");
					for (AlertCondition alertCondition : alertDefinition.getAlertConditions()) {
						long ikrDefinitionId = alertCondition.getIkrDefinitionId();						
						logger.debug("** ConditionId: " + alertCondition.getId() + ", ikrDefinitionId=" + ikrDefinitionId);						
						if (ikrDefinitionIds.contains(ikrDefinitionId)) {
							// This condition has to be updated
							logger.debug("This condition is concerned by fetched ikrDefinitionIds");
							List<IkrValue> ikrValues = currentIkrValuesCache.get(ikrDefinitionId);
							IkrValue valueToKeep = null;
							boolean checkValue = false;
							for (IkrValue ikrValue : ikrValues) {
								ComputeStatus conditionStatus = getConditionStatus(alertCondition, ikrValue);							
								logger.debug("Condition status: " + conditionStatus);							
								int conditionId = alertCondition.getId();
								if (conditionStatus == ComputeStatus.DOWN &&
									alert.isConditionUP(conditionId)) {
									alert.removeCondition(conditionId);
									logger.debug("T he condition was UP and is now DOWN => alertComputation requested");	
								} else if (conditionStatus == ComputeStatus.UP &&
										   !alert.isConditionUP(conditionId)) {
									alert.addCondition(conditionId);
									logger.debug("The condition was DOWN and is now UP => alertComputation requested");	
								}								
								if (conditionStatus == ComputeStatus.UP) {
									valueToKeep = ikrValue;	
									checkValue = true;
									break;
								}
							}
							if (checkValue) {
								for (IkrValue ikrValue : ikrValues) {
									if (ikrValue.getId()>0)
										valueToKeep = ikrValue; 
								}
								ikrValuesCache.put(ikrDefinitionId, valueToKeep);
							}
						}
					}
					
					// Every condition has been recompute on the alert
					// Recalculation of every AlertCompute
					
					boolean mustSendAlert = false;
					
					AlertWorkflow alertOldState = alert.getState();
					logger.debug("Alert OLD state: " + alertOldState);
					
					AlertComputeEvent newEvent = updateAlertState(alertDefinition, alert, ikrValuesCache);
					
					if (newEvent!=null) {	
						alertPM.addAlertEvent(alert, newEvent);
					} 
					
					AlertWorkflow alertNewState = alert.getState();
					logger.debug("Alert NEW state: " + alertNewState);
					
					if (alertNewState != alertOldState && 
							alertNewState.getStatus() != Status.AUTO_UP) {
							mustSendAlert = true;
					} else if (alertNewState.getStatus() == Status.AUTO_UP) {							
						long raisedTime = alert.getRaisedTime();							
						long now = new Date().getTime();
						
						long raisedDelay = (now-raisedTime)/1000;
						
						long alertDefinitionRaisingDelay = alertDefinition.getRaisingDelay();

						if (raisedDelay >= alertDefinitionRaisingDelay) {
							mustSendAlert = true;
						}
					}
					
					logger.debug("Alert Raising status: " + mustSendAlert);
					
					if (mustSendAlert) {
						alerts.add(alert);
						if (alertDefinition.getAlertWorkflowFilters().contains(alertNewState)) {
							AlertMessage alertMessage = new AlertMessage(alertDefinition, alert);
							alertMessage.publish();
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error while saving alerts", exc);
		}

		logger.debug("Nb Alert raised : " + alerts.size());
		
		return alerts;
	}	
	
	private AlertComputeEvent updateAlertState(AlertDefinition alertDefinition, Alert alert, Map<Long,IkrValue> ikrValuesCache) {
		AlertWorkflow alertOldState = alert.getState();
		
		// Find the new AlertWorkFlow
		Map<AlertCompute,ComputeStatus> newComputeStatus = getComputeStatus(alertDefinition, alert);
		AlertWorkflow alertNewState = AlertWorkflow.DOWN;				
		for (Map.Entry<AlertCompute, ComputeStatus> entry : newComputeStatus.entrySet()) {
			ComputeStatus computeStatus = entry.getValue();
			if (computeStatus == ComputeStatus.UP) {
				alertNewState = entry.getKey().getWorkflow();
			}
		}
	
		AlertComputeEvent newEvent = null;
		if (alertNewState != alertOldState) {			
			newEvent = 	new AlertComputeEvent(new Date(),alertOldState,alertNewState);			
			Map<AlertCompute,ComputeStatus> oldComputeStatus = alert.getComputeStatus();			
			for (Map.Entry<AlertCompute, ComputeStatus> entry : newComputeStatus.entrySet()) {
				AlertCompute alertCompute = entry.getKey();				
				ComputeStatus newStatus = entry.getValue();			
				ComputeStatus oldStatus = null;
				// oldComputeStatus must be null the first time the alert is raised
				if (oldComputeStatus != null) {						
					oldStatus = oldComputeStatus.get(alertCompute);
				} else {
					oldStatus = ComputeStatus.DOWN;
				}				
				if (oldStatus != newStatus) {
					String cause = alertCompute.getCause().toLowerCase();			
					Set<Long> ikrValueIds = new HashSet<Long>();
					List<IkrValue> nonPersistantValues = new ArrayList<IkrValue>();					
					for (AlertCondition alertCondition : alertDefinition.getAlertConditions()) {
						if (cause.contains("c" + alertCondition.getId())) {
							long ikrDefinitionId = alertCondition.getIkrDefinitionId();
							IkrValue ikrValue = ikrValuesCache.get(ikrDefinitionId);
							// ikrValue not added in the event to avoid double serialization
							if (ikrValue != null) {
								if (ikrValue.getId() == 0) {
									nonPersistantValues.add(ikrValue);
								}
								else {
									ikrValueIds.add(ikrValue.getId());
								}
							}			
						}
					}
					
					AlertComputeResolution acResolution = 
						new AlertComputeResolution(alertCompute,
												   newStatus,
												   ikrValueIds);
					if (nonPersistantValues.size()>0) {
						acResolution.setNonPersistantValues(nonPersistantValues);
					}
					newEvent.addAlertComputeResolution(acResolution);
					logger.debug("New Event Generated");
				}
			}
			
			alert.setComputeStatus(newComputeStatus);
			alert.addEvent(newEvent, true);	
		}
		return newEvent;
	}
	
	private boolean isAlertValid(AlertDefinition alertDefinition) {
		Date now = new Date();
		boolean ret = false;
		List<AlertValidity> alertValidities = alertDefinition.getAlertValidities();
		if (alertValidities != null && alertValidities.size()>0) {
			for (AlertValidity validity : alertValidities) {
				Calendar startCal = Calendar.getInstance();
				startCal.setTime(now);
				startCal.set(Calendar.HOUR_OF_DAY, validity.getStart().getHour());
				startCal.set(Calendar.MINUTE, validity.getStart().getMinute());
				
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(now);
				endCal.set(Calendar.HOUR_OF_DAY, 23);
				endCal.set(Calendar.MINUTE, 59);
				endCal.set(Calendar.SECOND, 59);
				if (validity.getEnd() != null) {
					endCal.set(Calendar.HOUR_OF_DAY, validity.getEnd().getHour());
					endCal.set(Calendar.MINUTE, validity.getEnd().getMinute());
				}
				
				if (now.after(startCal.getTime()) && now.before(endCal.getTime())) {
					ret = true;
				}
			}
		}
		else {
			ret = true;
		}
		
		return ret;
	}
	
	private ComputeStatus getConditionStatus(AlertCondition alertCondition, IkrValue ikrValue) throws Exception {		
		AlertConditionResolver resolver = alertConditionResolver.get();
		resolver.setIkrValue(ikrValue);		
		ComputeStatus res = alertCondition.resolveCondition(resolver);		
		return res;
	}	
	
	private Map<AlertCompute,ComputeStatus> getComputeStatus(AlertDefinition alertDefinition, Alert alert) {
		logger.debug("------ New AlertComputes Calculation ------");		
		Map<AlertCompute,ComputeStatus> res = new TreeMap<AlertCompute,ComputeStatus>();		
		try {	
			// get every conditionStatus
			List<AlertCondition> alertConditions = alertDefinition.getAlertConditions();
			 
			JEP jep = new JEP();
			
			for (AlertCondition alertCondition : alertConditions) {
				String conditionLabel = "c" + alertCondition.getId();
				int conditionStatus = alert.isConditionUP(alertCondition.getId()) ? 1 : 0;
				jep.addVariable("c" + alertCondition.getId(), conditionStatus);
				logger.debug("JEP variable replacement: Condition=" + conditionLabel + ",value=" + conditionStatus);
			}
			
			Collection<AlertCompute> alertComputes = alertDefinition.getAlertComputes();
			
			for(AlertCompute alertCompute: alertComputes) {				
				String cause = alertCompute.getCause();
				if (alertCompute.isEnable() && cause != null && cause.length() !=0) {	
					cause = cause.toLowerCase().replaceAll("and", "&&").replaceAll("or", "||");					
					logger.debug("**Cause: " + cause);
					jep.parseExpression(cause);
					double doubleRes =  jep.getValue();
					if (doubleRes == 0) {
						res.put(alertCompute, ComputeStatus.DOWN);
						logger.debug("Cause computation result: DOWN");
					} else {				
						res.put(alertCompute, ComputeStatus.UP);
						logger.debug("Cause computation result: UP");
					}
				}
			}
		} catch (Exception e) {
		     logger.error("An error occurred while performing alertComputations: " + e);
		}			
		
		return res;
	}
}
