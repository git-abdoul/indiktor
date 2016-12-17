package com.fsi.publisher.snmp;

import java.util.Collection;


public class SnmpPublisher {
	
	public void publish(SnmpMessage message) {
		try {	
			Collection<SnmpClient> clients = message.getClients();
			for(SnmpClient client : clients) {
				SnmpSession session = client.getSession();
				session.send(message);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
