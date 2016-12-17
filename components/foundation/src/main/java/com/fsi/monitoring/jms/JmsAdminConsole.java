package com.fsi.monitoring.jms;

public abstract class JmsAdminConsole implements IkrJmsMessageConsumer {
	
	private JmsProcessorFactory jmsFactory;
	private String heartbeatEventType;
	protected JmsMulticastConsumer adminRequestConsumer;
	protected JmsMulticastProducer eventLogProducer;
	protected JmsP2PConsumer heartBeatConsumer;
	
	protected void initJms() {
		adminRequestConsumer = (JmsMulticastConsumer)jmsFactory.getConsumerJmsProcessor(JmsProcessorType.ADMIN_REQUEST);
		adminRequestConsumer.addConsumer(this);
		eventLogProducer = (JmsMulticastProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.EVENT_LOG);
		heartBeatConsumer = (JmsP2PConsumer)jmsFactory.getConsumerJmsProcessor(JmsProcessorType.valueOf(heartbeatEventType));
		heartBeatConsumer.setConsumer(this);
	}
	
	public void setJmsFactory(JmsProcessorFactory jmsFactory) {
		this.jmsFactory = jmsFactory;
	}

	public void setHeartbeatEventType(String heartbeatEventType) {
		this.heartbeatEventType = heartbeatEventType;
	}	
}
