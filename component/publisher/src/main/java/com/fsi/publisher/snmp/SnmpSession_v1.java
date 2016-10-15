package com.fsi.publisher.snmp;

import com.adventnet.snmp.beans.SnmpTarget;
import com.fsi.monitoring.snmp.SnmpConfig;

class SnmpSession_v1 extends AbstractSnmpSession{  
    
    public SnmpSession_v1() {    	
        version = SnmpTarget.VERSION1;
    }   

	@Override
	public void initSpecificConfig(SnmpConfig config) {
		// TODO NOTHING TO DO		
	}
    
}
