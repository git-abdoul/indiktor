package com.fsi.monitoring.kpi.metrics;

import java.io.Serializable;
import java.util.Collection;

import com.fsi.monitoring.kpi.compute.MetricCompute;

public abstract class AbstractIkrDefinition 
implements Serializable {
	
	private static final long serialVersionUID = 4929210202698825378L;

	private long 		id;
	
	private int					ikrCategoryId;
	private String				ikrInstance;
	protected MetricCompute		ikrCompute;
	private Collection<Long>	linkedStatisticDefinitionIds;	
	
	private boolean		activated;

	public AbstractIkrDefinition() {
		ikrCompute = MetricCompute.RT;
	}
	
	public AbstractIkrDefinition(long id,
			 			 		 int ikrCategoryId,
			 			 		 String ikrInstance,
			 			 		 MetricCompute ikrCompute,
			 			 		 boolean activated) {
		this.id = id;
		this.ikrCategoryId = ikrCategoryId;
		this.ikrInstance = ikrInstance;
		this.ikrCompute = ikrCompute;
		this.activated = activated;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public int getIkrCategoryId() {
		return ikrCategoryId;
	}

	public String getIkrInstance() {
		return ikrInstance;
	}
	
	public void setIkrInstance(String ikrInstance) {
		this.ikrInstance = ikrInstance;
	}
	
	public void setIkrCategoryId(int ikrCategoryId) {
		this.ikrCategoryId = ikrCategoryId;
	}

	public String getFullIkrInstance() {
		String res = null;
		if (ikrCompute == null || ikrCompute == MetricCompute.RT) {
			res = ikrInstance;
		} 			
		else {
			res = ikrInstance + "  [" + ikrCompute + "]";
		}
		return res;
	}
	
	public MetricCompute getIkrCompute() {
		return ikrCompute;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public Collection<Long> getLinkedStatisticDefinitionIds() {
		return linkedStatisticDefinitionIds;
	}

	public void setLinkedStatisticDefinitionIds(Collection<Long> linkedStatisticDefinitionIds) {
		this.linkedStatisticDefinitionIds = linkedStatisticDefinitionIds;
	}
}
