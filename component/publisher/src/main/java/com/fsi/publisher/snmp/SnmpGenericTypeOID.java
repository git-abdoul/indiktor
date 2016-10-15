package com.fsi.publisher.snmp;

import java.util.HashMap;
import java.util.Map;

public class SnmpGenericTypeOID {
	private Map<String,String> genericTypeIODs = new HashMap<String,String>();
	
	public void setGenericTypeIODs(Map<String,String> initGenericTypeIODs) {
		genericTypeIODs = initGenericTypeIODs;
	}
	
	public String getOID(String type) {
		return genericTypeIODs.get(type);
	}
}
