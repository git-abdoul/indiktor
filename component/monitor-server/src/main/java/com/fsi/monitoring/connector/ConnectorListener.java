package com.fsi.monitoring.connector;

public interface ConnectorListener {
	void onStartedEvt(Connector connector);
	void onDisconnectEvt(Connector connector);
	long getListenerId();
}
