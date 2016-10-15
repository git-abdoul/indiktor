package com.fsi.publisher.snmp;

import com.fsi.monitoring.snmp.SnmpConfig;



public class SnmpClient {
	private SnmpConfig config;	
	private SnmpSession session;
	
	public SnmpClient(SnmpConfig config) {
		super();
		this.config = config;
	}

	public SnmpSession getSession() throws Exception {
		if (session == null) {
			switch (config.getVersion()) {
	            case 1:
	            	session = new SnmpSession_v1();	            	
	            case 2:
	            	session = new SnmpSession_v2c();
	              break;
	            case 3:
	            	session = new SnmpSession_v3();
	              break;
	            default:
	              throw new Exception("unsupported SNMP version");
			}
			
			session.init(config);
		}
		return session;
	}
	
}
