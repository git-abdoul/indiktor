package com.fsi.monitoring.datamodel.ikrDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.CrossComputeDefinitionBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class CrossComputeDefinitionSelectionBean
extends SortableList
implements LogicalEnvSelectionVisitor, Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(CrossComputeDefinitionSelectionBean.class);

	private static final String metricInstanceColumnName = "Metric Instance";
	private static final String metricDomainColumnName = "Metric Domain";
	private static final String metricCategoryColumnName = "Metric Category";
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private CrossComputeDefinitionBean selectedCrossComputeDefinitionBean;
		
	private List<CrossComputeDefinitionBean> crossComputeDefinitionBeans;
	private List<CrossComputeDefinitionBean> crossComputeDefinitionBeansSelected;

	private LogicalEnvSelectionBean logicalEnvSelectionBean;	
	
	private String searchQuery = "";
	
	public CrossComputeDefinitionSelectionBean() {
		super(metricInstanceColumnName);
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(false);
	}

	public void init(ActionEvent action) {
		if (!isAuthorized(47,"crossComputeDefinitionSelection")) {
			return;
		}
		
		logicalEnvSelectionBean.accept(this);
		logicalEnvSelectionBean.init();
		selectAll = false;
		crossComputeDefinitionBeansSelected = new ArrayList<CrossComputeDefinitionBean>();
	}
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	public void searchCrossComputeQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		reloadCrossComputeDefinitions();
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		selectAll = false;
	}
	
	public void filter() {
		if (searchQuery!=null && searchQuery.length()>0) {
			searchQuery = searchQuery.toLowerCase();
			List<String> queryItems = IkrUtils.splitSearchIndex(searchQuery);
			String[] searchQueryItems = (String[])queryItems.toArray(new String[queryItems.size()]);
			List<CrossComputeDefinitionBean> newCrossComputeDefinitionList = new ArrayList<CrossComputeDefinitionBean>();
			for (CrossComputeDefinitionBean bean : crossComputeDefinitionBeans) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newCrossComputeDefinitionList.add(bean);
			}
			crossComputeDefinitionBeans = new ArrayList<CrossComputeDefinitionBean>();
			crossComputeDefinitionBeans.addAll(newCrossComputeDefinitionList);
		}
	}
	
	public List<CrossComputeDefinitionBean> getCrossComputeDefinitions() {
		filter();
		if (crossComputeDefinitionBeans != null && crossComputeDefinitionBeans.size()>0)
			sort();
		return crossComputeDefinitionBeans;
	}

	public void setCrossComputeDefinitions(List<CrossComputeDefinitionBean> crossComputeDefinitionBeans) {
		this.crossComputeDefinitionBeans = crossComputeDefinitionBeans;
	}

	public void changeLogicalEnv(int logicalEnvId) {
		reloadCrossComputeDefinitions();
	}
	
	public void reloadCrossComputeDefinitions() {
		crossComputeDefinitionBeans = new ArrayList<CrossComputeDefinitionBean>();
		try {			
			logicalEnvSelectionBean.accept(this);
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());			
			int logicalEnvId = 0;
			if (logicalEnvSelectionBean.getLogicalEnv()!=null)
				logicalEnvId = logicalEnvSelectionBean.getLogicalEnv().getId();
			
			Map<Long,AbstractIkrDefinition> crossComputeDefinitionMap = monitoringPM.getCrossComputeDefinitions(logicalEnvId);
			
			for (AbstractIkrDefinition ikrDefinition : crossComputeDefinitionMap.values()) {				
				IkrCategory metricCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(ikrDefinition.getIkrCategoryId());
				IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricCategory.getParentDomainId());
				crossComputeDefinitionBeans.add(new CrossComputeDefinitionBean(ikrDefinition, metricDomain, metricCategory));
			}
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	public boolean isListRendered() {
		return crossComputeDefinitionBeans.size()>0;
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}
	
	public boolean isPaginationVisible() {
		if (crossComputeDefinitionBeans.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		selectedCrossComputeDefinitionBean = crossComputeDefinitionBeans.get(rowId);
	}

	public CrossComputeDefinitionBean getSelectedCrossComputeDefinitionBean() {
		return selectedCrossComputeDefinitionBean;
	}	
	
	public void setSelectedCrossComputeDefinitionBean(
			CrossComputeDefinitionBean selectedCrossComputeDefinitionBean) {
		this.selectedCrossComputeDefinitionBean = selectedCrossComputeDefinitionBean;
	}

	public String getDeleteMessage() {
		int numberCCSelected = 0;
		String message = "No cross compute definition selected";
		for (CrossComputeDefinitionBean crossComputeDefinitionBean : crossComputeDefinitionBeans) {
			if (crossComputeDefinitionBean.isSelected()){
				numberCCSelected++;
			}
		}
		if (numberCCSelected == 1) {
			for (CrossComputeDefinitionBean crossComputeDefinitionBean : crossComputeDefinitionBeans) {
				if (crossComputeDefinitionBean.isSelected()){
					message = "Are you sure to delete this cross compute definition : " + crossComputeDefinitionBean.getIkrDefinition().getIkrInstance();
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberCCSelected + " cross compute definitions?";
			return message;
		}
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(crossComputeDefinitionBeans, new Comparator<CrossComputeDefinitionBean>() {
			public int compare(CrossComputeDefinitionBean o1, CrossComputeDefinitionBean o2) {
				int res = 0;
				try {
					if (getMetricInstanceColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getIkrDefinition().getIkrInstance().toLowerCase().compareTo(o2.getIkrDefinition().getIkrInstance().toLowerCase()) :  o2.getIkrDefinition().getIkrInstance().toLowerCase().compareTo(o1.getIkrDefinition().getIkrInstance().toLowerCase());
					}
					else if (getMetricDomainColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getMetricDomain().getLabel().toLowerCase().compareTo(o2.getMetricDomain().getLabel().toLowerCase()) :  o2.getMetricDomain().getLabel().toLowerCase().compareTo(o1.getMetricDomain().getLabel().toLowerCase());
					}
					else if (getMetricCategoryColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getIkrCategory().getLabel().toLowerCase().compareTo(o2.getIkrCategory().getLabel().toLowerCase()) :  o2.getIkrCategory().getLabel().toLowerCase().compareTo(o1.getIkrCategory().getLabel().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});
	}

	public String getMetricInstanceColumnName() {
		return metricInstanceColumnName;
	}

	public String getMetricDomainColumnName() {
		return metricDomainColumnName;
	}

	public String getMetricCategoryColumnName() {
		return metricCategoryColumnName;
	}
	
	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public void handleSelectedCC(ValueChangeEvent event) {
		CrossComputeDefinitionBean crossCompute = (CrossComputeDefinitionBean)event.getComponent().getAttributes().get("crossCompute");
		if(crossCompute != null) {
			for(CrossComputeDefinitionBean crossComputeDefinitionBean : crossComputeDefinitionBeans) {
				if(crossComputeDefinitionBean.equals(crossCompute)) {
					crossCompute.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						crossComputeDefinitionBeansSelected.add(crossCompute);
					else
						crossComputeDefinitionBeansSelected.remove(crossCompute);
				}
			}
		}
	}
	
	public void handleSelectAllCC(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		crossComputeDefinitionBeansSelected.clear();
		for(CrossComputeDefinitionBean crossComputeDefinitionBean : crossComputeDefinitionBeans) {
			crossComputeDefinitionBean.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				crossComputeDefinitionBeansSelected.add(crossComputeDefinitionBean);
		}
	}
	
	public void handleDeleteNoSelection(CrossComputeDefinitionBean crossComputeDefinitionBean) {
		if(crossComputeDefinitionBean != null)
			crossComputeDefinitionBeansSelected.remove(crossComputeDefinitionBean);
		selectAll = false;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getCrossComputeDefinitionBeansSelected() {
		int size = crossComputeDefinitionBeansSelected.size();
		return size;
	}

	public void setCrossComputeDefinitionBeansSelected(List<CrossComputeDefinitionBean> crossComputeDefinitionBeansSelected) {
		this.crossComputeDefinitionBeansSelected = crossComputeDefinitionBeansSelected;
	}
}
