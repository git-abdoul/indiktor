package com.fsi.monitoring.alert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.fsi.monitoring.alert.action.AlertAction;
import com.fsi.monitoring.alert.condition.AlertCondition;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;

public class AlertDefinition 
implements Serializable {

	private static final long serialVersionUID = -6216935956666963591L;

	private long id;
	private String name;
	private String description;
	
	private String howTo;
	private boolean enable;
	
	private int group;
	private int domain;
	private int subDomain;
	private int logicalEnv;
	
	private List<AlertValidity> alertValidities;
	
	private Date creationDate;
	private Date lastUpdateDate;

	private List<AlertCondition> alertConditions;
	private Collection<AlertAction> alertActions;
	private List<AlertCompute> alertComputes;
	private List<AlertWorkflow> alertWorkflowFilters;
	
	private long raisingDelay;
	
	public AlertDefinition() {
		alertConditions = new ArrayList<AlertCondition>();
		alertActions = new ArrayList<AlertAction>();
		alertWorkflowFilters = new ArrayList<AlertWorkflow>();
		alertValidities = new ArrayList<AlertValidity>();
		initAlertComputes();
		initAlertWorkflowFilters();
	};
	
	public AlertDefinition(long id,
						   String name,
						   int group,
						   int domain,
						   int subDomain,
						   int logicalEnv,
						   String description,
						   String howTo,
						   boolean enable,
						   Date creationDate,
						   Date lastUpdateDate,
						   long raisingDelay) {

			this.id = id;
			this.name = name;
			this.group = group;
			this.domain = domain;
			this.subDomain = subDomain;
			this.logicalEnv = logicalEnv;
			this.description = description;
			this.howTo = howTo;
			this.enable = enable;
			this.creationDate = creationDate;
			this.lastUpdateDate = lastUpdateDate;
			this.raisingDelay = raisingDelay;
			
			alertComputes = new ArrayList<AlertCompute>();
			alertConditions = new ArrayList<AlertCondition>();
			alertActions = new ArrayList<AlertAction>();
			alertWorkflowFilters = new ArrayList<AlertWorkflow>();
			alertValidities = new ArrayList<AlertValidity>();
	}

	private void initAlertComputes() {
		alertComputes = new ArrayList<AlertCompute>();
		
		alertComputes.add(new AlertCompute(AlertWorkflow.UP_1));
		alertComputes.add(new AlertCompute(AlertWorkflow.UP_2));
		alertComputes.add(new AlertCompute(AlertWorkflow.UP_3));
		alertComputes.add(new AlertCompute(AlertWorkflow.MAX));
	}
	
	private void initAlertWorkflowFilters() {
		alertWorkflowFilters = new ArrayList<AlertWorkflow>();
		
		alertWorkflowFilters.add(AlertWorkflow.UP_1);
		alertWorkflowFilters.add(AlertWorkflow.UP_2);
		alertWorkflowFilters.add(AlertWorkflow.UP_3);
		alertWorkflowFilters.add(AlertWorkflow.MAX);
		alertWorkflowFilters.add(AlertWorkflow.DOWN);
		alertWorkflowFilters.add(AlertWorkflow.ACK);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getSubDomain() {
		return subDomain;
	}

	public void setSubDomain(int subDomain) {
		this.subDomain = subDomain;
	}

	public int getLogicalEnv() {
		return logicalEnv;
	}

	public void setLogicalEnv(int logicalEnv) {
		this.logicalEnv = logicalEnv;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public List<AlertCondition> getAlertConditions() {
		return alertConditions;
	}

	public void setAlertConditions(List<AlertCondition> alertConditions) {
		this.alertConditions = alertConditions;
	}

	public Collection<AlertAction> getAlertActions() {
		return alertActions;
	}

	public void setAlertActions(Collection<AlertAction> alertActions) {
		this.alertActions = alertActions;
	}
	
	public List<AlertCompute> getAlertComputes() {
		Collections.sort(alertComputes, new Comparator<AlertCompute>() {
			public int compare(AlertCompute o1, AlertCompute o2) {
				return (new Integer(o1.getWorkflow().getSeverity())).compareTo(new Integer(o2.getWorkflow().getSeverity()));
			}
		});
		return alertComputes;
	}
	
	public void addAlertCompute(AlertCompute alertCompute) {
		alertComputes.add(alertCompute);
	}
	
	public void addAlertCondition(AlertCondition alertCondition) {
		alertConditions.add(alertCondition);
	}
	
	public void removeAlertCondition(AlertCondition alertCondition) {
		alertConditions.remove(alertCondition);
	}
	
	public void addAlertAction(AlertAction alertAction) {
		alertActions.add(alertAction);
	}
	
	public String getHowTo() {
		return howTo;
	}
	
	public void setHowTo(String howTo) {
		this.howTo = howTo;
	}
	
	public long getRaisingDelay() {
		return raisingDelay;
	}
	
	public void setRaisingDelay(long delay) {
		this.raisingDelay = delay;
	}
	
	public List<AlertWorkflow> getAlertWorkflowFilters() {
		return alertWorkflowFilters;
	}

	public void addAlertWorkflowFilter(AlertWorkflow alertWorkflowFilter) {
		this.alertWorkflowFilters.add(alertWorkflowFilter);
	}

	public void setAlertWorkflowFilters(List<AlertWorkflow> alertWorkflowFilters) {
		this.alertWorkflowFilters = alertWorkflowFilters;
	}

	public List<AlertValidity> getAlertValidities() {
		return alertValidities;
	}

	public void setAlertValidities(List<AlertValidity> alertValidities) {
		this.alertValidities = alertValidities;
	}	
}
