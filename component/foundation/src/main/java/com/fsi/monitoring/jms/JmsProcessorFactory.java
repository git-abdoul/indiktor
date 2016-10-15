package com.fsi.monitoring.jms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

public class JmsProcessorFactory {
	public final ReentrantLock lock = new ReentrantLock();
	
	private ConnectionFactory factory;
	private Map<String,String> jmsDestinations = new HashMap<String, String>();
	
	private Connection connection;
	
	private Map<JmsProcessorType, JmsProcessor> consumers = Collections.synchronizedMap(new HashMap<JmsProcessorType, JmsProcessor>());
	private Map<JmsProcessorType, JmsProcessor> producers = Collections.synchronizedMap(new HashMap<JmsProcessorType, JmsProcessor>());
	
	public void initFactory() throws JMSException {
		connection = this.factory.createConnection();
	}
	
	public JmsProcessor getConsumerJmsProcessor(JmsProcessorType jmsProcessorType) {		
		return getJmsProcessor(jmsProcessorType, consumers, false);
	}
	
	public JmsProcessor getProducerJmsProcessor(JmsProcessorType jmsProcessorType) {
		return getJmsProcessor(jmsProcessorType, producers, true);
	}
	
	private JmsProcessor getJmsProcessor(JmsProcessorType jmsProcessorType, Map<JmsProcessorType, JmsProcessor> processorMap, boolean isProducer) {
		JmsProcessor processor = processorMap.get(jmsProcessorType);
		if (processor == null) {
			lock.lock();
			try {
				processor = processorMap.get(jmsProcessorType);
				if (processor == null){
					String destination = jmsDestinations.get(jmsProcessorType.name());
					if (jmsProcessorType.name().startsWith("HEARTBEAT")) {
						if (isProducer)
							processor = new JmsP2PProducer();
						else
							processor = new JmsP2PConsumer();
					}
					else {
						if (isProducer)
							processor = new JmsMulticastProducer();
						else
							processor = new JmsMulticastConsumer();
					}
					processor.init(connection, destination);
					processorMap.put(jmsProcessorType, processor);
				}	
			} finally {
				lock.unlock();
			}	
		}
		return processor;
	}
	
	public void setJmsConnection(ConnectionFactory jmsConnection) {
		this.factory = jmsConnection;
	}
	
	public void setJmsDestinations(Map<String, String> jmsDestinations) {
		this.jmsDestinations = jmsDestinations;
	}	
}
