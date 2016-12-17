package com.fsi.monitoring.jms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

import org.apache.log4j.Logger;

public class JmsMulticastConsumer extends JmsProcessor implements MessageListener {
	private final static Logger logger = Logger.getLogger(JmsMulticastConsumer.class);
	
	private List<IkrJmsMessageConsumer> consumers;
	
	public JmsMulticastConsumer() {
		super();
		consumers = new ArrayList<IkrJmsMessageConsumer>();
	}
	
	public void onMessage(Message message) {
		try {
			ObjectMessage objmess = (ObjectMessage) message;
			Collection<IkrJmsMessage> jmsMessages = (Collection<IkrJmsMessage>)objmess.getObject();
			for(IkrJmsMessageConsumer consumer : consumers) {
				consumer.newMsgReceived(jmsMessages);
			}			
		} catch (Exception exc) {
			logger.error("InternalAMQPoller reception failed", exc);
		}
	}
	
	@Override
	protected void initDestination() throws JMSException {
		Topic topic = session.createTopic(destination);
		MessageConsumer consumer = session.createConsumer(topic);
		consumer.setMessageListener(this);
		connection.start();		
	}
	
	public void addConsumer(IkrJmsMessageConsumer consumer) {
		consumers.add(consumer);
	}
}
