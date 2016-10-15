package com.fsi.monitoring.alert.action;

public interface AlertAction {
	
	enum AlertActionType {SMS,MAIL,SNMP};
	
	void launch();
	
}
