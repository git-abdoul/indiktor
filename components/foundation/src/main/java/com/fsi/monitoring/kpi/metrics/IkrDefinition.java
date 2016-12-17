package com.fsi.monitoring.kpi.metrics;

import java.io.Serializable;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;


public class IkrDefinition
extends AbstractIkrDefinition
implements Serializable {
	
	private static final long serialVersionUID = 4929210202698825378L;

	private long 		monitorId;
	private Collection<Long>	linkedCrossComputeDefinitionIds;	

	public IkrDefinition(long id,
			 			 long monitorId,	
			 			 int ikrCategoryId,
			 			 String ikrInstance,
			 			 MetricCompute ikrCompute,
			 			 boolean isActivated) {
		super(id, ikrCategoryId, ikrInstance, ikrCompute, isActivated);
		this.monitorId = monitorId;
	}

	public long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(long monitorId) {
		this.monitorId = monitorId;
	}
	
	public Collection<Long> getLinkedCrossComputeDefinitionIds() {
		return linkedCrossComputeDefinitionIds;
	}

	public void setLinkedCrossComputeDefinitionIds(Collection<Long> linkedCrossComputeDefinitionIds) {
		this.linkedCrossComputeDefinitionIds = linkedCrossComputeDefinitionIds;
	}	

}
