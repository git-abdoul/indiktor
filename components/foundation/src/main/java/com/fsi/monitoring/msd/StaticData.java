package com.fsi.monitoring.msd;

import java.io.Serializable;

import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;

public class StaticData
extends AbstractIkrDefinition
implements Serializable {
	private static final long serialVersionUID = 1062825353833577332L;
	
	public static final String STATIC_DATA_CONTEXT = "STATIC_DATA";
	
	private String value;
	private int logicalEnvId;
	
	public StaticData() {
		ikrCompute = MetricCompute.STATIC;
	}
	
	public StaticData(long id,
					  int logicalEnvId,
					  int ikrCategoryId,
					  String ikrInstance) {
		super(id, ikrCategoryId, ikrInstance, MetricCompute.STATIC, true);
		this.logicalEnvId = logicalEnvId;
	}

	public int getLogicalEnvId() {
		return logicalEnvId;
	}

	public void setLogicalEnvId(int logicalEnvId) {
		this.logicalEnvId = logicalEnvId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getFullIkrInstance() {
		return getIkrInstance()  + "  [" + value + "]";
	}
	
	
}
