package com.fsi.publisher.snmp;

public class AlertSnmpOID {
	private String alertName;
	private String alertSeverity;
	private String alertState;
	private String alertDescription;
	private String alertDate;
	private String alertIdentifier;
	
	public String getAlertName() {
		return alertName;
	}
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
	public String getAlertSeverity() {
		return alertSeverity;
	}
	public void setAlertSeverity(String alertSeverity) {
		this.alertSeverity = alertSeverity;
	}
	public String getAlertState() {
		return alertState;
	}
	public void setAlertState(String alertState) {
		this.alertState = alertState;
	}
	public String getAlertDescription() {
		return alertDescription;
	}
	public void setAlertDescription(String alertDescription) {
		this.alertDescription = alertDescription;
	}
	public String getAlertDate() {
		return alertDate;
	}
	public void setAlertDate(String alertDate) {
		this.alertDate = alertDate;
	}
	public String getAlertIdentifier() {
		return alertIdentifier;
	}
	public void setAlertIdentifier(String alertIdentifier) {
		this.alertIdentifier = alertIdentifier;
	}
}
