package com.fsi.monitoring.datamodel.bean;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.util.FacesUtils;

public class CrossComputeDefinitionBean extends IkrDefinitionBean {
	private static final Logger logger = Logger.getLogger(CrossComputeDefinitionBean.class);	
	private static final long serialVersionUID = -4066568698479546838L;
	
	private CrossComputeDefinition crossComputeDef;
	private IkrStaticDomain metricDomain;
	private boolean selected;		
	private Set<String> searchIndexes;
	
	private String style;
	
	public CrossComputeDefinitionBean(AbstractIkrDefinition ikrDefinition, IkrStaticDomain metricDomain, IkrCategory ikrCategory) {
		super(ikrDefinition, ikrCategory);	
		this.crossComputeDef = (CrossComputeDefinition)ikrDefinition;
		this.metricDomain = metricDomain;	
		initDomainType();
		this.initSearchIndexes();
	}
	
	private void initDomainType() {
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());	
		try {
			IkrStaticDomain domainType = dataModelPM.getIkrStaticDomain(metricDomain.getParentDomainId());				
			setDomainType(domainType);
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();
		searchIndexes.add(getIkrCategory().getLabel().toLowerCase());
		searchIndexes.add(getIkrCategory().getDomainValue().toLowerCase());
		searchIndexes.add(getDomainType().getLabel().toLowerCase());
		searchIndexes.add(metricDomain.getLabel().toLowerCase());
		searchIndexes.add(getDomainType().getDomainValue().toLowerCase());
		searchIndexes.add(metricDomain.getDomainValue().toLowerCase());
		searchIndexes.add(getContext().toLowerCase());
		searchIndexes.add(getDomainView().toLowerCase());
		searchIndexes.add(getLogicalEnv().getName().toLowerCase());
		if (getIkrCategory().getIkrUnitType()!=null)
			searchIndexes.add(getIkrCategory().getIkrUnitType().name().toLowerCase());
		if (getIkrCategory().getIkrUnit()!=null)
			searchIndexes.add(getIkrCategory().getIkrUnit().name().toLowerCase());
		for (String index : getIkrCategory().getSearchesIndexes()) {
			searchIndexes.add(index.toLowerCase());
		}
		searchIndexes.add(getFullIkrInstance().toLowerCase());
	}

	public IkrStaticDomain getMetricDomain() {
		return metricDomain;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}
	
	public boolean isValid() {
		boolean isvalid = true;
		Collection<Long> crossCompute = crossComputeDef.parse();
		BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
		for (Long ikrDefinitionId : crossCompute) {
			MetricGroupBean metricGroupBean = beanPM.getIkrDefinitionBean(ikrDefinitionId);
    		if (metricGroupBean == null)
    			isvalid = false;
		}
		return isvalid;
	}
	
	public String getStyle() {
		if(isValid())
			return "text-align: left;";
		else
			return "text-align: left; background-color: #F06161;";
	}	
}
