package com.fsi.monitoring.datamodel.ikrStaticDomain.selection;

public interface IkrStaticDomainSelectorVisitor {	
	void changeMetricDomain(int metricDomainId);
	void changeMetricGroup(int metricGroupId);
}
