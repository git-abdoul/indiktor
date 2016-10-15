package com.fsi.publisher.snmp;

import com.fsi.monitoring.snmp.SnmpConfig;

public interface SnmpSession {
	 public void send(SnmpMessage message);
	 public void init(SnmpConfig config);
}
