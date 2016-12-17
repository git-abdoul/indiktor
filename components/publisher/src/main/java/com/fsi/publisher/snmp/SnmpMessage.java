package com.fsi.publisher.snmp;

import java.util.Collection;

public class SnmpMessage {
	public static int ALERT = 0;
	
	private Collection<SnmpClient> clients;
	private int type;
	protected String[] oids;
	protected String[] varValues;
	
	
	public SnmpMessage(int type, Collection<SnmpClient> clients) {
		this.type = type;
		this.clients = clients;
		oids = new String[6];
		varValues = new String[6];
	}

	public int getType() {
		return type;
	}

	public String[] getOids() {
		return oids;
	}

	public String[] getVarValues() {
		return varValues;
	}

	public Collection<SnmpClient> getClients() {
		return clients;
	}	
}
