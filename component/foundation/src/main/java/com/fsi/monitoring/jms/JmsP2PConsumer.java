package com.fsi.monitoring.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminRequest;

public class JmsP2PConsumer extends JmsProcessor implements MessageListener {
	private final static Logger logger = Logger.getLogger(JmsP2PConsumer.class);
	
	private IkrJmsMessageConsumer consumer;
	
	public JmsP2PConsumer() {
		super();
	}
	
	public void onMessage(Message message) {
		try {
			ObjectMessage objmess = (ObjectMessage) message;
			if (objmess.getObject() instanceof AdminRequest) {
				if (consumer!=null) {
					consumer.isAlive();
					Destination replyQueue = message.getJMSReplyTo();
					MessageProducer reply = session.createProducer(replyQueue);
					ObjectMessage returnMsg = session.createObjectMessage((AdminRequest)objmess.getObject());
					returnMsg.setJMSCorrelationID(objmess.getJMSCorrelationID());
					reply.send(returnMsg);
				}
			}
		} catch (Exception exc) {
			logger.error("InternalAMQPoller reception failed", exc);
		}
	}
	
	@Override
	protected void initDestination() throws JMSException {
		connection.start();
		Destination requestQueue = session.createQueue(destination+".REQ");
		MessageConsumer request = session.createConsumer(requestQueue);
		request.setMessageListener(this);		
	}
	
	public void setConsumer(IkrJmsMessageConsumer consumer) {
		this.consumer = consumer;
	}
}
