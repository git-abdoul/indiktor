package com.fsi.monitoring.system.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.system.dto.IkrMessageValue;

public class IkrMessageHandler {
	private static Map<String, IkrMessageValue> messages = Collections.synchronizedMap(new HashMap<String, IkrMessageValue>());
	
//	private Map<K, V>
	
	public static synchronized void addMessage(IkrMessageValue message) {
		String key = message.getValue("MSG_ENV") + "_" + message.getValue("CATEGORY");
		messages.put(key, message);
	}
}
