package com.fsi.monitoring.component.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.MetricGroupBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.util.FacesUtils;

public class ModifiableMetricBean
implements Serializable {

	private static final long serialVersionUID = 7558480525970257512L;

	private static final Logger logger = Logger.getLogger(ModifiableMetricBean.class);		
	
	private String label;
	private MetricGroupBean metricGroupBean = null;
	private boolean selected;
	
	private Set<String> searchIndexes;
	
	public ModifiableMetricBean(MetricGroupBean metricGroupBean) {
		this.metricGroupBean = metricGroupBean;
		
		initSearchIndexes();
	}
	
	private void initSearchIndexes() {
		searchIndexes = new HashSet<String>();
		searchIndexes.add(getLabel().toLowerCase());
		searchIndexes.add(metricGroupBean.getIkrCategory().getLabel().toLowerCase());
		searchIndexes.add(metricGroupBean.getIkrCategory().getDomainValue().toLowerCase());
		searchIndexes.add(metricGroupBean.getDomainType().getLabel().toLowerCase());
		searchIndexes.add(metricGroupBean.getMetricDomain().getLabel().toLowerCase());
		searchIndexes.add(metricGroupBean.getDomainType().getDomainValue().toLowerCase());
		searchIndexes.add(metricGroupBean.getMetricDomain().getDomainValue().toLowerCase());
		searchIndexes.add(metricGroupBean.getContext().toLowerCase());
		searchIndexes.add(metricGroupBean.getDomainView().toLowerCase());
		if (metricGroupBean.getLogicalEnv()!=null)
			searchIndexes.add(metricGroupBean.getLogicalEnv().getName().toLowerCase());
		if (metricGroupBean.getIkrCategory().getIkrUnitType()!=null)
			searchIndexes.add(metricGroupBean.getIkrCategory().getIkrUnitType().name().toLowerCase());
		if (metricGroupBean.getIkrCategory().getIkrUnit()!=null)
			searchIndexes.add(metricGroupBean.getIkrCategory().getIkrUnit().name().toLowerCase());
		for (String index : metricGroupBean.getIkrCategory().getSearchesIndexes()) {
			searchIndexes.add(index.toLowerCase());
		}
		IkrDefinitionBean ikrDefBean = (IkrDefinitionBean)metricGroupBean;
		searchIndexes.add(ikrDefBean.getFullIkrInstance().toLowerCase());
		AbstractIkrDefinition absDef = ikrDefBean.getIkrDefinition();
		if (absDef instanceof IkrDefinition) {
			IkrDefinition ikrDef = (IkrDefinition)absDef;
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			MonitorConfigBean monitorBean =  beanPM.getMonitorConfigBean(ikrDef.getMonitorId());	
			searchIndexes.add(monitorBean.getConnectorTypes().toLowerCase());
		}		
	}	
	
	public MetricGroupBean getMetricGroupBean() {
		return metricGroupBean;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {}
	
	public void updateSelected(boolean selected) {
		this.selected = selected;
	}
	
	public void onChangeSelected(ValueChangeEvent evnt) {	
		this.selected = (Boolean)evnt.getNewValue();
	}	
	
	public String getLabel() {
		String res = "";
		if (label!=null && label.length()>0)
			res =  label;
		else {
			IkrDefinitionBean bean = (IkrDefinitionBean)metricGroupBean;
			res = bean.getFullIkrInstance() + " (" + bean.getIkrCategory().getLabel() + ")"  + " - " + bean.getContext();
			if (bean.getContext().equalsIgnoreCase(bean.getFullIkrInstance()))
				res = bean.getFullIkrInstance() + " (" + metricGroupBean.getIkrCategory().getLabel() + ")";
		}
		return res;
	}	
	
	public void setLabel(String label) {
		this.label = label;
	}

	public int hashCode() {
		return metricGroupBean.hashCode();
	}
	
	 public boolean equals(Object obj) { 
		 ModifiableMetricBean other = (ModifiableMetricBean)obj;
		 return metricGroupBean.equals(other.getMetricGroupBean());
	 }

	public Set<String> getSearchIndexes() {
		return searchIndexes;
	}
}
