package com.fsi.monitoring.alert.config;

import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.snmp.SnmpConfig;

public class SnmpConfigRowBean {
	private static final Map<String, Integer> genericTypes = new HashMap<String, Integer>();
	
	static {
		genericTypes.put("ColdStart", 0);
		genericTypes.put("WarmStart", 1);
		genericTypes.put("LinkDown", 2);
		genericTypes.put("LinkUp", 3);
		genericTypes.put("AuthenticationFailure", 4);
		genericTypes.put("egpNeighborLoss", 5);
		genericTypes.put("Enterprise", 6);
	}
	
	private boolean selected;
	private SnmpConfig config;
	
	public SnmpConfigRowBean(SnmpConfig config) {
		super();
		this.config = config;
	}
	
	public SnmpConfigRowBean() {
		super();
		this.config = new SnmpConfig();
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public SnmpConfig getConfig() {
		return config;
	}
	
	public String getGenericTrapType() {
		String type = "";
		for(String key : genericTypes.keySet()) {
			int val = genericTypes.get(key);
			if(val == config.getGenericTrapType()) {
				type = key;
				break;
			}
		}
		return type;
	}
	
	public void setGenericTrapType(String genericType) {
		config.setGenericTrapType(genericTypes.get(genericType));
	}
	
	public boolean equals(Object other) {
		SnmpConfigRowBean oth = (SnmpConfigRowBean)other;
		return config.getId() == oth.getConfig().getId();
	}
}
