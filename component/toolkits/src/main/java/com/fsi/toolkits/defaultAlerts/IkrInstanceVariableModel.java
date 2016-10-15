package com.fsi.toolkits.defaultAlerts;

import java.io.Serializable;

public class IkrInstanceVariableModel implements Serializable {
	private static final long serialVersionUID = 3522830888450632694L;
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
