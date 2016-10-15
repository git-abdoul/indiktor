package com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.config.SnmpConfigRowBean;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class MetricDomainConfigSelectionBean
extends SortableList
implements Serializable {	

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(MetricDomainConfigSelectionBean.class);	
	
	private static final String domainTypeColumnName = "Domain Type";
	private static final String metricDomainColumnName = "Metric Domain";
	private static final String metricDomainConfigColumnName = "Metric Domain Config";
	
	private List<MetricDomainConfigBean> metricDomainConfigBeans;
	private List<MetricDomainConfigBean> metricDomainConfigBeansSelected;
//	private IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean;	
	private MetricDomainConfigBean selectedMetricDomainConfigBean;
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private String searchQuery = "";
	
	public MetricDomainConfigSelectionBean() {
		super(domainTypeColumnName);
	}
	
	public void init(ActionEvent action) {
		if (!isAuthorized(125,"metricDomainConfigSelection")) {
			return;
		}
		metricDomainConfigBeansSelected = new ArrayList<MetricDomainConfigBean>();
		selectAll = false;
		reload();
//		ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");
//		ikrStaticDomainSelectorBean.initComponent(false, true, true, true);
//		ikrStaticDomainSelectorBean.accept(this);
//		ikrStaticDomainSelectorBean.initItems();			
	}
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	public List<MetricDomainConfigBean> getMetricDomainConfigBeans() {
		filterConfigs();
		if (metricDomainConfigBeans != null && metricDomainConfigBeans.size()>0)
			sort();
		else
			return new ArrayList<MetricDomainConfigBean>();
		return metricDomainConfigBeans;
	}

	public void setMetricDomainConfigBeans(List<MetricDomainConfigBean> metricDomainConfigBeans) {
		this.metricDomainConfigBeans = metricDomainConfigBeans;
	}
	
//	public void changeMetricGroup(int metricGroupId) {}	
	
//	public void changeMetricDomain(int metricDomainId) {
//		try {
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			List<MetricDomainConfig> metricDomainConfigs = dataModelPM.getMetricDomainConfigs(metricDomainId);
//			metricDomainConfigBeans = new ArrayList<MetricDomainConfigBean>();
//			for (MetricDomainConfig config : metricDomainConfigs) {
//				IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(config.getIkrStaticDomainId());
//				IkrStaticDomain domainType = dataModelPM.getIkrStaticDomain(metricDomain.getParentDomainId());
//				metricDomainConfigBeans.add(new MetricDomainConfigBean(config, domainType.getLabel(), metricDomain.getLabel()));
//			}
//    	} catch(Exception exc) {
//    		logger.error(exc);
//    	}
//	}
	
	public void reload() {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			List<MetricDomainConfig> metricDomainConfigs = dataModelPM.getMetricDomainConfigs();
			metricDomainConfigBeans = new ArrayList<MetricDomainConfigBean>();
			for (MetricDomainConfig config : metricDomainConfigs) {
				IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(config.getIkrStaticDomainId());
				IkrStaticDomain domainType = dataModelPM.getIkrStaticDomain(metricDomain.getParentDomainId());
				metricDomainConfigBeans.add(new MetricDomainConfigBean(config, domainType.getLabel(), metricDomain.getLabel()));
			}
    	} catch(Exception exc) {
    		logger.error(exc);
    	}
	}
	
	public void reloadSelected() {
		for(MetricDomainConfigBean metricDomainConfigBeanSelected : metricDomainConfigBeansSelected) {
			for(MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
				if(metricDomainConfigBeanSelected.getMetricDomainConfig().getDescription().equals(metricDomainConfigBean.getMetricDomainConfig().getDescription())
						&& metricDomainConfigBeanSelected.getDomainType().equals(metricDomainConfigBean.getDomainType())
							&& metricDomainConfigBeanSelected.getMetricDomain().equals(metricDomainConfigBean.getMetricDomain()))
	    			metricDomainConfigBean.setSelected(true);
			}
    	}		
	}

//	public void reInitMetricDomainConfigs() {
//		ikrStaticDomainSelectorBean.accept(this);
//		ikrStaticDomainSelectorBean.initItems();
//	}
	
	public void searchMetricDomainConfigQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		reload();
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		selectAll = false;
	}
	
	public void filterConfigs() {
		if (searchQuery!=null && searchQuery.length()>0) {
			searchQuery = searchQuery.toLowerCase();
			List<String> queryItems = IkrUtils.splitSearchIndex(searchQuery);
			String[] searchQueryItems = (String[])queryItems.toArray(new String[queryItems.size()]);
			List<MetricDomainConfigBean> newMetricDomainConfigList = new ArrayList<MetricDomainConfigBean>();
			for (MetricDomainConfigBean bean : metricDomainConfigBeans) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newMetricDomainConfigList.add(bean);
			}
			metricDomainConfigBeans = new ArrayList<MetricDomainConfigBean>();
			metricDomainConfigBeans.addAll(newMetricDomainConfigList);
		}
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		selectedMetricDomainConfigBean = metricDomainConfigBeans.get(rowId);
	}

	public MetricDomainConfigBean getSelectedMetricDomainConfigBean() {
		return selectedMetricDomainConfigBean;
	}
	
	public void setSelectedMetricDomainConfigBean(MetricDomainConfigBean selectedMetricDomainConfigBean) {
		this.selectedMetricDomainConfigBean = selectedMetricDomainConfigBean;
	}

	public String getDeleteMessage() {
		int numberMDCSelected = 0;
		for (MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
			if (metricDomainConfigBean.isSelected()){
				numberMDCSelected++;
			}
		}
		String message = "No metric domain config selected";
		if (numberMDCSelected == 1) {
			for(MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
				if(metricDomainConfigBean.isSelected()) {
					message = "Are you sure to delete this metric domain config : <" + metricDomainConfigBean.getDomainType() + ":" + metricDomainConfigBean.getMetricDomainConfig().getDescription() + ">";
					break;
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberMDCSelected + " metric domain configs?";
			return message;
		}
	}
	
	public boolean isPaginationVisible() {
		if (metricDomainConfigBeans.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public String getDomainTypeColumnName() {
		return domainTypeColumnName;
	}

	public String getMetricDomainColumnName() {
		return metricDomainColumnName;
	}

	public String getMetricDomainConfigColumnName() {
		return metricDomainConfigColumnName;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public void handleSelectedMetricDomainConfigBean(ValueChangeEvent event) {
		MetricDomainConfigBean metricDomainConfigBeanSelected = (MetricDomainConfigBean)event.getComponent().getAttributes().get("metricDomainConfigBean");
		if(metricDomainConfigBeanSelected != null) {
			for(MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
				if(metricDomainConfigBean.equals(metricDomainConfigBeanSelected)) {
					metricDomainConfigBean.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						metricDomainConfigBeansSelected.add(metricDomainConfigBeanSelected);
					else
						metricDomainConfigBeansSelected.remove(metricDomainConfigBeanSelected);
				}
			}
		}
	}
	
	public void handleSelectAllMetricDomainConfigBeans(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		metricDomainConfigBeansSelected.clear();
		for(MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
			metricDomainConfigBean.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				metricDomainConfigBeansSelected.add(metricDomainConfigBean);
		}
	}

	public int getMetricDomainConfigBeansSelected() {
		int size = metricDomainConfigBeansSelected.size();
		return size;
	}

	public void setMetricDomainConfigBeansSelected(List<MetricDomainConfigBean> metricDomainConfigBeansSelected) {
		this.metricDomainConfigBeansSelected = metricDomainConfigBeansSelected;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	public boolean getListRendered() {
		return getMetricDomainConfigBeans().size() > 0;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(metricDomainConfigBeans, new Comparator<MetricDomainConfigBean>() {
			public int compare(MetricDomainConfigBean o1, MetricDomainConfigBean o2) {
				int res = 0;
				try {
					if (getDomainTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getDomainType().toLowerCase().compareTo(o2.getDomainType().toLowerCase()) :  o2.getDomainType().toLowerCase().compareTo(o1.getDomainType().toLowerCase());
					}
					else if (getMetricDomainColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getMetricDomain().toLowerCase().compareTo(o2.getMetricDomain().toLowerCase()) : o2.getMetricDomain().toLowerCase().compareTo(o1.getMetricDomain().toLowerCase());
					}
					else if (getMetricDomainConfigColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getMetricDomainConfig().getDescription().toLowerCase().compareTo(o2.getMetricDomainConfig().getDescription().toLowerCase()) : o2.getMetricDomainConfig().getDescription().toLowerCase().compareTo(o1.getMetricDomainConfig().getDescription().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});	
	}	
}
