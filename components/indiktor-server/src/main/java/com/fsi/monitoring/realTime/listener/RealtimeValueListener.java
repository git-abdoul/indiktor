package com.fsi.monitoring.realTime.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import com.fsi.monitoring.RealTimeValue;
import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.IkrJmsMessageConsumer;
import com.fsi.monitoring.jms.JmsMulticastConsumer;
import com.fsi.monitoring.jms.JmsProcessorFactory;
import com.fsi.monitoring.jms.JmsProcessorType;
import com.fsi.monitoring.realTime.RealTimeRender;

public abstract class RealtimeValueListener implements Observer, IkrJmsMessageConsumer {
	
	private JmsProcessorFactory jmsFactory;
	private String jmsProcessorType;
	private JmsMulticastConsumer realtimeValueConsumer;
	
	private Collection<RealTimeValue> rtValueBuffer;
	
	protected abstract void notifRealTimeSubscribers(Collection<? extends RealTimeValue> realTimeValues);
	protected abstract void initRealTimeValues();	
	
	public void init() {
		realtimeValueConsumer = (JmsMulticastConsumer)jmsFactory.getConsumerJmsProcessor(JmsProcessorType.valueOf(jmsProcessorType));
		realtimeValueConsumer.addConsumer(this);
		rtValueBuffer = new ArrayList<RealTimeValue>(100000);
		initRealTimeValues();
	}	
	
	public synchronized void update(Observable o, Object arg) {
		if (o instanceof RealTimeRender) {
			notifRealTimeSubscribers(rtValueBuffer);
			rtValueBuffer.clear();
		}
	}
	
	public void newMsgReceived(Collection<IkrJmsMessage> jmsMessages) {
		Collection<RealTimeValue> realtimeValues = new ArrayList<RealTimeValue>();
		for (IkrJmsMessage message : jmsMessages) {
			realtimeValues.add((RealTimeValue)message);
		}
		bufferizeRTValues(realtimeValues);
	}
	
	private synchronized void bufferizeRTValues(Collection<RealTimeValue> realtimeValues) {
		rtValueBuffer.addAll(realtimeValues);
	}
	
	public boolean isAlive() {
		return false;
	}
	
	public AdminComponent getComponentType() {
		return null;
	}	
	
	public void setJmsProcessorType(String jmsProcessorType) {
		this.jmsProcessorType = jmsProcessorType;
	}
	public void setJmsFactory(JmsProcessorFactory jmsFactory) {
		this.jmsFactory = jmsFactory;
	}
}
