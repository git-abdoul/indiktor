package com.fsi.monitoring.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.AdminRequestCommand;

public class JmsP2PProducer extends JmsProcessor { 
	private static final Logger logger = Logger.getLogger(JmsP2PProducer.class);	
	
	private Destination requestQueue;
	private Destination replyQueue;
	private MessageProducer request;
	private MessageConsumer reply;
	
	public JmsP2PProducer(){
		super();
	}	
	
	public boolean  isComponentAlive(AdminComponent component) throws Exception {
		boolean ret = false;
		if (component!=null) {
			try {
				ObjectMessage message = session.createObjectMessage(new AdminRequest(AdminRequestCommand.HEARTBEAT, 0, null));	
				message.setJMSReplyTo(replyQueue);
				request.send(message);				
				Message msgResponse = reply.receive(10000);
				if (msgResponse!=null){	
					ObjectMessage objMsg = (ObjectMessage) message;
					AdminRequest rec = (AdminRequest)objMsg.getObject();
					ret = true;
				}
			} catch(Exception e){
				String error = "Could not send collection of realtime values";
				logger.error(error, e);
				throw new Exception(error,e);
			}
		}
		
		return ret;
	}

	@Override
	protected void initDestination() throws JMSException {
		requestQueue = session.createQueue(destination+".REQ");
		replyQueue = session.createQueue(destination+".REP");
		request = session.createProducer(requestQueue);
		reply = session.createConsumer(replyQueue);
	}
}
