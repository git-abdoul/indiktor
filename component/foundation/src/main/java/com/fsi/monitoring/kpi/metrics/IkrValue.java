package com.fsi.monitoring.kpi.metrics;

import java.io.Serializable;
import java.util.Date;

import com.fsi.monitoring.RealTimeValue;
import com.fsi.monitoring.RealtimeValueType;

public class IkrValue 
implements Serializable, RealTimeValue {
	
	private static final long serialVersionUID = 3649047087589576590L;

	protected long id;
	
	protected long ikrDefinitionId;
	protected int ikrCategoryId;
	protected Date captureTime;
	
	protected String value;
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long kpiValId) {
		this.id = kpiValId;
	}
	
	public long getValueDefinitionId() {
		return ikrDefinitionId;
	}
	
	public void setIkrDefinitionId(long ikrDefinitionId) {
		this.ikrDefinitionId = ikrDefinitionId;
	}
	
	public int getIkrCategoryId() {
		return ikrCategoryId;
	}
	
	public void setIkrCategoryId(int ikrCategoryId) {
		this.ikrCategoryId = ikrCategoryId;
	}	
	
	public Date getCaptureTime() {
		return captureTime;
	}
	
	public void setCaptureTime(Date captureTime) {
		this.captureTime = captureTime;
	}

	public String getType() {
		return RealtimeValueType.IKR_VALUE.name();
	}
}
