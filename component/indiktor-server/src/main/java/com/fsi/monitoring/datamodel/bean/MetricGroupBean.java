package com.fsi.monitoring.datamodel.bean;

import java.io.Serializable;

import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.kpi.metrics.IkrCategory;


public class MetricGroupBean
implements Serializable {	

	private static final long serialVersionUID = 2788132227450478443L;
	
	protected IkrStaticDomain domainType;
	protected IkrStaticDomain metricDomain;
	protected IkrCategory ikrCategory = null;
	protected String context = null;
	protected LogicalEnv logicalEnv = null;
	protected String domainView;
	protected String label;
	
	public MetricGroupBean(IkrCategory ikrCategory,
						   String context,
						   LogicalEnv logicalEnv,
						   String domainView) {
		this.ikrCategory = ikrCategory;
		this.context = context;
		this.logicalEnv = logicalEnv;
		this.domainView = domainView;
	}
	
	public MetricGroupBean(IkrCategory ikrCategory) {
		this.ikrCategory = ikrCategory;
		this.context = "N/A";
		this.logicalEnv = new LogicalEnv(0, "N/A", "");
		this.domainView = "N/A";
	}
	
	public IkrCategory getIkrCategory() {
		return ikrCategory;
	}
	
	public String getContext() {
		return context;
	}
	
	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}
	
	public String getDomainView() {
		return domainView;
	}	

//	public void setLabel(String label) {
//		this.label = label;
//	}

	public int hashCode() {
		return (int)ikrCategory.getId();
	}	
	
	
	
//	public String getSdFullInstance() {
//		String res = "";
//		if (ikrCategory instanceof StaticData) {
//			StaticData data = (StaticData)ikrCategory;
//			res = data.getLabel() + " = " + data.getValue() + " " + data.getIkrUnit().getSymbol();
//		}		
//		return res;
//	}
	
	 public IkrStaticDomain getDomainType() {
		return domainType;
	}

	public void setDomainType(IkrStaticDomain domainType) {
		this.domainType = domainType;
	}

	public IkrStaticDomain getMetricDomain() {
		return metricDomain;
	}

	public void setMetricDomain(IkrStaticDomain metricDomain) {
		this.metricDomain = metricDomain;
	}

	public boolean equals(Object obj) { 
		 MetricGroupBean other = (MetricGroupBean)obj;
		 return (this.ikrCategory.getId() == other.ikrCategory.getId() &&
				 this.logicalEnv.getId() == other.logicalEnv.getId() &&
				 this.context.equals(other.context) &&
				 this.domainView.equals(other.domainView));
	 }
}
