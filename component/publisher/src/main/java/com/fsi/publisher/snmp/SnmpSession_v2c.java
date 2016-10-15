package com.fsi.publisher.snmp;

import com.adventnet.snmp.beans.SnmpTarget;
import com.fsi.monitoring.snmp.SnmpConfig;

class SnmpSession_v2c extends AbstractSnmpSession {
	
	public SnmpSession_v2c() {
        this.version = SnmpTarget.VERSION2C;
    }

	@Override
	public void initSpecificConfig(SnmpConfig config) {		
		target.setMaxRepetitions(snmpDefaultConfig.getMaxRepetition());
    	target.setNonRepeaters(snmpDefaultConfig.getNonRepeaters());		
	}
}
