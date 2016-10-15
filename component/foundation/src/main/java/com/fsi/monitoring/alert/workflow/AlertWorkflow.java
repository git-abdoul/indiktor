package com.fsi.monitoring.alert.workflow;


public enum AlertWorkflow {
	
	MAX(100, "NOT RUNNING", Status.AUTO_UP),
	UP_3(3, "HIGH", Status.AUTO_UP),
	UP_2(2, "MEDIUM", Status.AUTO_UP),
	UP_1(1, "LOW", Status.AUTO_UP),
	DOWN(0,	"DOWN", Status.AUTO_DOWN),
	ACK(0, "ACKNOWLEGDE", Status.USER_ACK);

	public enum Status {AUTO_UP,AUTO_DOWN,USER_ACK};
	
	private int severity;
	private String severityName;
	private Status status;
	
	private AlertWorkflow(int severity, String severityName, Status status) {
		this.severity = severity;
		this.status = status;
		this.severityName = severityName;
	}
	
	public int getSeverity() {
		return severity;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public String getSeverityName() {
		return severityName;
	}

	public String toString() {
		if (status == Status.AUTO_DOWN || status == Status.USER_ACK) {
			return status.name();
		} else {
			return status + "_" + severity;
		}
	}
	
	public static AlertWorkflow getStateBySeverity(int severity) {
		switch(severity) {
			case 1 :
				return UP_1;
			case 2 :
				return UP_2;
			case 3 :
				return UP_3;
			case 100 :
				return MAX;
		}
		return null;
	}
	
	public static AlertWorkflow getStateByName(String name) {
		if (MAX.toString().equals(name))
			return MAX;
		else if (UP_3.toString().equals(name))
			return UP_3;
		else if (UP_2.toString().equals(name))
			return UP_2;
		else if (UP_1.toString().equals(name))
			return UP_1;
		else if (DOWN.toString().equals(name))
			return DOWN;
		else if (ACK.toString().equals(name))
			return ACK;
	
		return null;
	}
}
