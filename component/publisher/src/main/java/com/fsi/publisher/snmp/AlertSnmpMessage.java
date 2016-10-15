package com.fsi.publisher.snmp;

import java.util.Collection;

import com.fsi.fwk.apps.config.AbstractApplicationContext;
import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.alert.AlertDefinition;

public class AlertSnmpMessage extends SnmpMessage {	
	private static final int ALERT_NAME = 0;
	private static final int ALERT_SEVERITY = 1;
	private static final int ALERT_SATE = 2;
	private static final int ALERT_DESCRIPTION = 3;
	private static final int ALERT_DATE = 4;
	private static final int ALERT_IDENTIFIER = 5;
	

	public AlertSnmpMessage(AlertDefinition alertDefinition, Alert alert, Collection<SnmpClient> clients) {
		super(SnmpMessage.ALERT, clients);
		
		AlertSnmpOID alertSnmpOID = (AlertSnmpOID)AbstractApplicationContext.getBean("alertSnmpOID");
		oids[ALERT_NAME]=alertSnmpOID.getAlertName(); 
		varValues[ALERT_NAME]="Name: "+alertDefinition.getName();
		oids[ALERT_SEVERITY]=alertSnmpOID.getAlertSeverity(); 
		varValues[ALERT_SEVERITY]="Severity: "+String.valueOf(alert.getState().getSeverity());
		oids[ALERT_SATE]=alertSnmpOID.getAlertState();
		varValues[ALERT_SATE]="State: "+alert.getState().getStatus().name();
		oids[ALERT_DESCRIPTION]=alertSnmpOID.getAlertDescription();
		varValues[ALERT_DESCRIPTION]="Description: "+alertDefinition.getDescription();
		oids[ALERT_DATE]=alertSnmpOID.getAlertDate();
		varValues[ALERT_DATE]="Date: "+alert.getAlertEvent().getEventDate();
		oids[ALERT_IDENTIFIER]=alertSnmpOID.getAlertIdentifier();
		varValues[ALERT_IDENTIFIER]="Identifier: "+alertDefinition.getLogicalEnv()+","+alertDefinition.getGroup()+","+alertDefinition.getDomain()+","+alertDefinition.getSubDomain();
	}
}
