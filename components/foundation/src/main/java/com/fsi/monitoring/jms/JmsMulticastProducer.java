package com.fsi.monitoring.jms;

import java.io.Serializable;
import java.util.Collection;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

import org.apache.log4j.Logger;

public class JmsMulticastProducer extends JmsProcessor{ 
	private static final Logger logger = Logger.getLogger(JmsMulticastProducer.class);	

	private MessageProducer messageProducer;

	public JmsMulticastProducer(){
		super();
	}	
	
	public void publish(Collection<IkrJmsMessage> values) throws Exception {		
		if (values != null) {
			try {
				ObjectMessage message = session.createObjectMessage((Serializable)values);
				messageProducer.send(message);	
			} catch(Exception e){
				String error = "Could not send collection of realtime values";
				logger.error(error, e);
				throw new Exception(error,e);
			}
		}
	}
	
	@Override
	protected void initDestination() throws JMSException {
		Topic topic = session.createTopic(destination);
		messageProducer = session.createProducer(topic);		
	}
}
