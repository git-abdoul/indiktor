package com.fsi.monitoring.kpi.monitor.calypso.event;

public class CalypsoEventObject {
	private int eventId;
	private int objectId;
	private String eventClassName;
	private String engineName;
	
	public CalypsoEventObject(int eventId, int objectId, String eventClassName,
			String engineName) {
		super();
		this.eventId = eventId;
		this.objectId = objectId;
		this.eventClassName = eventClassName;
		this.engineName = engineName;
	}

	public int getEventId() {
		return eventId;
	}

	public int getObjectId() {
		return objectId;
	}

	public String getEventClassName() {
		return eventClassName;
	}

	public String getEngineName() {
		return engineName;
	}	
	
	public String getKey() {
		return engineName + "_" + eventClassName + "_" + eventId;
	}
}
