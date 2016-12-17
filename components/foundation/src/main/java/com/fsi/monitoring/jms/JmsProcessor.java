package com.fsi.monitoring.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.log4j.Logger;

public abstract class JmsProcessor {
	private static final Logger logger = Logger.getLogger(JmsProcessor.class);	
	
	protected String destination;
	protected Session session;
	protected Connection connection;
	
	public JmsProcessor() {
		super();
	}
	
//	protected abstract void initBeforeSessionCreate() throws JMSException;
	protected abstract void initDestination() throws JMSException;

	public void init(Connection connection, String destination) {
		this.connection = connection;
		this.destination = destination;
		
		try {			
			session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			initDestination();
		} catch (JMSException jmsE) {
			logger.fatal(jmsE);
		}
	}
}
