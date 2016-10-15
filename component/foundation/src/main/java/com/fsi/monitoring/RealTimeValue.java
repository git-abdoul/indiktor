package com.fsi.monitoring;

import com.fsi.monitoring.jms.IkrJmsMessage;

public interface RealTimeValue extends IkrJmsMessage{

	long getValueDefinitionId();
	String getType();
	
}
