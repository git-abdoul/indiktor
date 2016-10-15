package com.fsi.monitoring.alert.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SnmpAlertAction 
extends AbstractAlertAction 
implements AlertAction,Serializable {	
	private static final long serialVersionUID = -408080430220197335L;
	
	private Set<Long> snmpConfigIds;
	
	public SnmpAlertAction(int id, 
						   long alertDefinitionId,
						   Collection<AlertActionType> types) {
		super(id, alertDefinitionId, types);
		snmpConfigIds = new HashSet<Long>();
	}
	
	public SnmpAlertAction() {
		super(0, 0, new ArrayList<AlertActionType>());
		snmpConfigIds = new HashSet<Long>();
	}
	
	public void addSnmpConfigId(long snmpConfigId) {
		snmpConfigIds.add(snmpConfigId);
	}
	
	public void removeUserId(long snmpConfigId) {
		snmpConfigIds.remove(snmpConfigId);
	}
	
	public Collection<Long> getSnmpConfigIds() {
		return snmpConfigIds;
	}
	
	public void launch() {
		
	}
}
