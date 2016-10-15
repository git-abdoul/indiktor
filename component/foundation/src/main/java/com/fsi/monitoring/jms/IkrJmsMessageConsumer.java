package com.fsi.monitoring.jms;

import java.util.Collection;

import com.fsi.monitoring.admin.AdminComponent;

public interface IkrJmsMessageConsumer {
	public void newMsgReceived(Collection<IkrJmsMessage> jmsMessages);
	public boolean isAlive();
	
	public AdminComponent getComponentType();
}
