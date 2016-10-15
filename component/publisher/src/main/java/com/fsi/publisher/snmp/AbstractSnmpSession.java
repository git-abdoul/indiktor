package com.fsi.publisher.snmp;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.adventnet.snmp.beans.DataException;
import com.adventnet.snmp.beans.SnmpTarget;
import com.fsi.fwk.apps.config.AbstractApplicationContext;
import com.fsi.monitoring.snmp.SnmpConfig;


public abstract class AbstractSnmpSession implements SnmpSession {
	private static final Logger logger = Logger.getLogger(AbstractSnmpSession.class);
	
	private static final Map<Integer, String> genericTypes = new HashMap<Integer, String>();
	
	static {
		genericTypes.put(0, "ColdStart");
		genericTypes.put(1, "WarmStart");
		genericTypes.put(2, "LinkDown");
		genericTypes.put(3, "LinkUp");
		genericTypes.put(4, "AuthenticationFailure");
		genericTypes.put(5, "egpNeighborLoss ");
		genericTypes.put(6, "Enterprise");
	}
	
	protected SnmpDefaultConfig snmpDefaultConfig;
	protected int version;
	protected String snmpObjectID;
	protected int genericType;
	protected int specificType;
	protected SnmpTarget target;
	
	public void init(SnmpConfig config) {     
		snmpDefaultConfig = (SnmpDefaultConfig)AbstractApplicationContext.getBean("snmpDefaultConfig");		
		SnmpGenericTypeOID snmpGenericTypeOID = (SnmpGenericTypeOID)AbstractApplicationContext.getBean("snmpGenericTypeOID");
		
		genericType = config.getGenericTrapType();
		specificType = config.getSpecificTrapType();
		snmpObjectID = snmpGenericTypeOID.getOID(genericTypes.get(genericType));
		
    	target = new SnmpTarget();
    	target.setSnmpVersion(version);
    	target.setLoadFromCompiledMibs(true);
    	target.setDebug(false);
    	target.setTargetHost(config.getHostname()); 
    	target.setTargetPort(config.getPort()); 
    	if (config.getCommunity() != null && config.getCommunity().length()>0) 
            target.setCommunity(config.getCommunity());
    	
    	String mibFile = snmpDefaultConfig.getMIBFile();
    	if (mibFile != null && mibFile.length()>0) {
	    	try {
		        target.loadMibs(mibFile);
		    } catch (Exception ex) {
		       logger.error("Error loading MIBs: "+ex, ex);		
		    }
    	}
    	
    	target.setTimeout(snmpDefaultConfig.getTimeout());
    	target.setRetries(snmpDefaultConfig.getRetries());
    	
    	this.initSpecificConfig(config);
    }
	 
	public abstract void initSpecificConfig(SnmpConfig config);	
	
	protected void sendTrap(SnmpMessage message) {
		RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();	
		long uptime = mx.getUptime();
		try {
			String agentHost = InetAddress.getLocalHost().getHostName();
			target.snmpSendTrap(snmpObjectID, agentHost, genericType, specificType, uptime, message.getVarValues());	
			logger.debug("SNMP sent to <" + target.getTargetHost() + ":" + target.getTargetPort() + "> at " + (new Date(uptime)).toString());
		} catch (UnknownHostException e) {
			 logger.error(e.getMessage(), e);		
		} catch (DataException e) {
			logger.error(e.getMessage(), e);
		}	
	}
	
    public void send(SnmpMessage message) {
    	if (SnmpMessage.ALERT == message.getType()) {
    		target.setObjectIDList(message.getOids());
    		sendTrap(message);
    	}
    }

}
