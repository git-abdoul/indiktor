package com.fsi.publisher.service;

import java.io.Serializable;

import com.fsi.fwk.apps.config.AbstractApplicationContext;
import com.fsi.publisher.email.EmailMessage;
import com.fsi.publisher.email.EmailPublisher;
import com.fsi.publisher.sms.SMSMessage;
import com.fsi.publisher.sms.SMSPublisher;
import com.fsi.publisher.snmp.SnmpMessage;
import com.fsi.publisher.snmp.SnmpPublisher;

public abstract class Message 
implements Serializable {

	private static final long serialVersionUID = -3851501518609945270L;
	
	protected EmailMessage emailMessage;
	protected SMSMessage smsMessage;
	protected SnmpMessage snmpMessage;
	
	public void publish() {
		if (emailMessage != null) {
			EmailPublisher emailPublisher = (EmailPublisher)AbstractApplicationContext.getBean("emailPublisher");
			emailPublisher.publish(emailMessage);
		}
		
		if (smsMessage != null) {
			SMSPublisher smsPublisher = (SMSPublisher)AbstractApplicationContext.getBean("SMSPublisher");
			smsPublisher.publish(smsMessage);
		}		
		
		if (snmpMessage != null) {
			SnmpPublisher snmpPublisher = (SnmpPublisher)AbstractApplicationContext.getBean("snmpPublisher");
			snmpPublisher.publish(snmpMessage);
		}		
	}
}
