package com.fsi.monitoring.component.ikrSelector;

import java.util.Collection;

import com.fsi.monitoring.component.bean.ModifiableMetricBean;

public interface MetricSelectorVisitor {
	void select(Collection<ModifiableMetricBean> ikrDefinitionBeans);
	void deselect(Collection<ModifiableMetricBean> ikrDefinitionBeans);
}
