package com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.ikrStaticDomain.IkrStaticDomainCreateBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorVisitor;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class MetricDomainResourceSelectionBean extends SortableList implements IkrStaticDomainSelectorVisitor, Serializable{
	private static final long serialVersionUID = -9005282266357397278L;

	private static final Logger logger = Logger.getLogger(MetricDomainResourceSelectionBean.class);
	
	private static final String domainTypeColumnName = "Domain Type";
	private static final String metricDomainColumnName = "Metric Domain";
	private static final String resourceNameColumnName = "Resource Name";
	
	private List<MetricDomainResourceBean> metricDomainResources;
	private List<MetricDomainResourceBean> metricDomainResourcesSelected;
	
	private Map<Integer, IkrCategoryResource> ikrCategoriesResources;
	private Map<Integer, IkrStaticDomain> ikrCategories;
	private Map<Integer, List<IkrStaticDomain>> ikrCategoriesByMetricDomain;
	
	private MetricDomainResourceBean metricDomainResourceBean;
	
	private boolean rendererConfigPanel;
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	private boolean onEdit = false;
	private boolean onCreation = false;
	
	int numberMDRSelected = 0;
	
	private String searchQuery = "";
	
	public MetricDomainResourceSelectionBean() {
		super(domainTypeColumnName);
	}

	public void init(ActionEvent action) {
		if (!isAuthorized(125,"metricDomainResourceSelection")) {
			return;
		}
		
		rendererConfigPanel = false;
		metricDomainResourceBean = null;
		loadMetricDomainResources();
		if(!onEdit && !onCreation) {
			selectAll = false;
			metricDomainResourcesSelected = new ArrayList<MetricDomainResourceBean>();
		}
	}
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	public List<MetricDomainResourceBean> getMetricDomainResources() {
		filterConfigs();
		if (metricDomainResources != null && metricDomainResources.size()>0)
			sort();
		return metricDomainResources;
	}
	
	public void searchMetricDomainResourceQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		loadMetricDomainResources();
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
			List<MetricDomainResourceBean> newMetricDomainResourceList = new ArrayList<MetricDomainResourceBean>();
			for (MetricDomainResourceBean bean : metricDomainResources) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newMetricDomainResourceList.add(bean);
			}
			metricDomainResources = new ArrayList<MetricDomainResourceBean>();
			metricDomainResources.addAll(newMetricDomainResourceList);
		}
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		metricDomainResourceBean = metricDomainResources.get(rowId);
	}
	
	public void changeMetricDomain(int metricDomainId) {
		metricDomainResourceBean.getMetricDomainResource().setIkrStaticDomainId(metricDomainId);	
		
		List<IkrStaticDomain> metricDomainCategories = ikrCategoriesByMetricDomain.get(metricDomainId);
		Map<Integer, IkrCategoryResourceBean> ikrCategoriesBeanNotSet = new HashMap<Integer, IkrCategoryResourceBean>();
		if (metricDomainCategories!=null){	
			for (IkrStaticDomain ikrCategory : metricDomainCategories) {
				if (!metricDomainResourceBean.getIkrCategoryResources().containsKey(ikrCategory.getId()))
					ikrCategoriesBeanNotSet.put(ikrCategory.getId(), new IkrCategoryResourceBean(ikrCategory.getLabel(), new IkrCategoryResource(0, ikrCategory.getId(), 0, "", true)));
			}
		}
		
		metricDomainResourceBean.removeAllCategoryResources(null);
		metricDomainResourceBean.setIkrCategoriesWithNoResources(ikrCategoriesBeanNotSet);
	}
	
	private void initMetricDomainResource() {
		IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");	
		ikrStaticDomainSelectorBean.initComponent(true, true, true, true);
		ikrStaticDomainSelectorBean.accept(this);
		int metricDomainId = metricDomainResourceBean.getMetricDomainResource().getId();
		if (metricDomainId == 0) {
			ikrStaticDomainSelectorBean.initItems();
		}	
	}

	public void changeMetricGroup(int metricGroupId) {}
	
	public void addMetricDomainResource(ActionEvent action) {
		this.rendererConfigPanel = true;
		metricDomainResourceBean = new MetricDomainResourceBean();		
		initMetricDomainResource();
		onEdit = false;
		onCreation = true;
	}
	
	public String navigateToMetricDomainConfig() {
		rendererConfigPanel = false;
		IkrStaticDomainCreateBean ikrStaticDomainBean = (IkrStaticDomainCreateBean)FacesUtils.getManagedBean("staticDomainBean");
		ikrStaticDomainBean.navigate(null);
		return ikrStaticDomainBean.action();
	}	
	
	public void deleteMetricDomainResource(ActionEvent action) {
		MetricDomainResourceBean metricDomainResourceSelected = (MetricDomainResourceBean)action.getComponent().getAttributes().get("metricDomainResourceBean");
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		try {
			dataModelPM.removeMetricDomainResource(metricDomainResourceSelected.getMetricDomainResource().getId());
			loadMetricDomainResources();
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		selectAll = false;
		metricDomainResourcesSelected = new ArrayList<MetricDomainResourceBean>();
	}
	
	public void deleteSelectedMetricDomainResources(ActionEvent action) {
		for (MetricDomainResourceBean metricDomainResourceBean : metricDomainResources) {
			if (metricDomainResourceBean.isSelected()){
				DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
				try {
					dataModelPM.removeMetricDomainResource(metricDomainResourceBean.getMetricDomainResource().getId());
					loadMetricDomainResources();
				} catch (PersistenceException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		selectAll = false;
		metricDomainResourcesSelected = new ArrayList<MetricDomainResourceBean>();
	}
	
	public void editMetricDomainResource(ActionEvent action) {
		this.rendererConfigPanel = true;
		metricDomainResourceBean = (MetricDomainResourceBean)action.getComponent().getAttributes().get("metricDomainResourceBean");
		onEdit = true;
		onCreation = false;
		
//		numberMDRSelected = 0;
//		for (MetricDomainResourceBean metricDomainResourceBean : metricDomainResources) {
//			if (metricDomainResourceBean.isSelected()){
//				numberMDRSelected++;
//			}
//		}
//		if (numberMDRSelected < 2) {
//			if (numberMDRSelected == 1) {
//				this.rendererConfigPanel = true;
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No metric domain resource has been selected");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one metric domain resource to edit");
//		}		
	}
	
	public void saveMetricDomainResource(ActionEvent action) {
		metricDomainResourceBean.save();		
		loadMetricDomainResources();
		
		metricDomainResourceBean = null;
		rendererConfigPanel = false;
		selectAll = false;
		onEdit = false;
		onCreation = false;
		metricDomainResourcesSelected = new ArrayList<MetricDomainResourceBean>();
	}
	
	public void closeConfigPanel(ActionEvent action) {
		init(action);
		this.rendererConfigPanel = false;
		for(MetricDomainResourceBean metricDomainResource : metricDomainResources) {
			for(MetricDomainResourceBean metricDomainResourceSelected : metricDomainResourcesSelected) {
				if(metricDomainResource.getDomainType().equals(metricDomainResourceSelected.getDomainType())
						&& metricDomainResource.getMetricDomain().equals(metricDomainResourceSelected.getMetricDomain())
							&& metricDomainResource.getResourceName().equals(metricDomainResourceSelected.getResourceName())) {
					metricDomainResource.setSelected(true);
				}
			}
		}
		onEdit = false;
		onCreation = false;
	}
	
	private void initDomains() throws PersistenceException {
		ikrCategoriesResources = new HashMap<Integer, IkrCategoryResource>();
		ikrCategories = new HashMap<Integer, IkrStaticDomain>();
		ikrCategoriesByMetricDomain = new HashMap<Integer, List<IkrStaticDomain>>();		
		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		
		ikrCategoriesResources = dataModelPM.loadIkrCategoryResources();
		
		Map<Integer, IkrStaticDomain> ikrStaticDomainList = dataModelPM.loadIkrStaticDomains();
		for (IkrStaticDomain ikrStaticDomain : ikrStaticDomainList.values()) {
			if (ikrStaticDomain instanceof IkrCategory && isExtensible(ikrStaticDomain)) {		
				int ikrCategoryId = ikrStaticDomain.getId();
				ikrCategories.put(ikrCategoryId, ikrStaticDomain);	
				int metricDomainId = ikrStaticDomain.getParentDomainId();
				List<IkrStaticDomain> values = ikrCategoriesByMetricDomain.get(metricDomainId);
				if (values == null) {
					values = new ArrayList<IkrStaticDomain>();
					ikrCategoriesByMetricDomain.put(metricDomainId, values);
				}
				values.add(ikrStaticDomain);
			}
		}		
	}
	
	private void loadMetricDomainResources() {		
		metricDomainResources = new ArrayList<MetricDomainResourceBean>();		
		try {
			
			initDomains();
			
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			List<MetricDomainResource> resources = dataModelPM.loadMetricDomainResources();			
			for (MetricDomainResource resource : resources) {
				IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(resource.getIkrStaticDomainId());	
				IkrStaticDomain domainType = null;
				if (metricDomain != null) {
					domainType = dataModelPM.getIkrStaticDomain(metricDomain.getParentDomainId());
					if (domainType !=null) {
						Map<Integer, IkrCategoryResource> categoryResources = dataModelPM.getIkrCategoryResourcesById(resource.getId());
						Map<Integer, IkrStaticDomain> ikrCategoriesMap = new HashMap<Integer, IkrStaticDomain>();
						for (int ikrCategoryId : categoryResources.keySet()) {							
							ikrCategoriesMap.put(ikrCategoryId, ikrCategories.get(ikrCategoryId));
						}
						List<IkrStaticDomain> metricCategoryNotSet = ikrCategoriesByMetricDomain.get(resource.getIkrStaticDomainId());
						Map<Integer, IkrCategoryResourceBean> ikrCategoriesBeanWithNoResources = new HashMap<Integer, IkrCategoryResourceBean>();
						if (metricCategoryNotSet!=null){	
							for (IkrStaticDomain ikrCategory : metricCategoryNotSet) {
								if (!categoryResources.containsKey(ikrCategory.getId()))
									ikrCategoriesBeanWithNoResources.put(ikrCategory.getId(), new IkrCategoryResourceBean(ikrCategory.getLabel(), new IkrCategoryResource(0, ikrCategory.getId(), resource.getId(), "", true)));
							}
						}
						MetricDomainResourceBean bean = new MetricDomainResourceBean(domainType.getLabel(), metricDomain.getLabel(), resource, ikrCategoriesMap, categoryResources, ikrCategoriesBeanWithNoResources);
						bean.initCategoryResourceBeans();
						metricDomainResources.add(bean);
					}
				}
			}			
    	} catch(Exception exc) {    		
    		logger.error(exc.getMessage(), exc);
    	}
	}	
	
	public MetricDomainResourceBean getMetricDomainResourceBean() {
		return metricDomainResourceBean;
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public boolean isPaginationVisible() {
		if (metricDomainResources.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}

	public void setPaginationVisible(boolean paginationVisible) {
		this.paginationVisible = paginationVisible;
	}

	public boolean isRendererConfigPanel() {
		return rendererConfigPanel;
	}
	
	public String getMetricDomainColumnName() {
		return metricDomainColumnName;
	}
	
	public String getDomainTypeColumnName() {
		return domainTypeColumnName;
	}
	
	public String getResourceNameColumnName() {
		return resourceNameColumnName;
	}
	
	public String getDeleteMessage() {
		numberMDRSelected = 0;
		for (MetricDomainResourceBean metricDomainResourceBean : metricDomainResources) {
			if (metricDomainResourceBean.isSelected()){
				numberMDRSelected++;
			}
		}
		String message = "No metric domain resource selected";
		if (numberMDRSelected == 1) {
			for(MetricDomainResourceBean metricDomainResource : metricDomainResources) {
				if(metricDomainResource.isSelected()) {
					message = "Are you sure to delete the metric domain resource : " + metricDomainResource.getDomainType() + " > " 
							  + metricDomainResource.getMetricDomain() + " > " + metricDomainResource. getResourceName() + " ?";
					break;
				}
			}			
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberMDRSelected + " metric domain resources?";
			return message;
		}
	}
	
	private boolean isExtensible(IkrStaticDomain ikrCategory) {
		boolean ret = true;
		if (ikrCategory.getDomainValue().contains("COMPUTED") || ikrCategory.getDomainValue().contains("STATIC"))
			ret = false;
		return ret;
	}
	
	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	public void handleSelectedMetricDomainResource(ValueChangeEvent event) {
		MetricDomainResourceBean metricDomainResourceSelected = (MetricDomainResourceBean)event.getComponent().getAttributes().get("metricDomainResourceBean");
		if(metricDomainResourceSelected != null) {
			for(MetricDomainResourceBean metricDomainResource : metricDomainResources) {
				if(metricDomainResource.equals(metricDomainResourceSelected)) {
					metricDomainResource.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						metricDomainResourcesSelected.add(metricDomainResourceSelected);
					else
						metricDomainResourcesSelected.remove(metricDomainResourceSelected);
				}
			}
		}
	}
	
	public void handleSelectAllMetricDomainResources(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		metricDomainResourcesSelected.clear();
		for(MetricDomainResourceBean metricDomainResource : metricDomainResources) {
			metricDomainResource.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				metricDomainResourcesSelected.add(metricDomainResource);
		}
	}

	public int getMetricDomainResourcesSelected() {
		int size = metricDomainResourcesSelected.size();
		return size;
	}

	public void setMetricDomainResourcesSelected(List<MetricDomainResourceBean> metricDomainResourcesSelected) {
		this.metricDomainResourcesSelected = metricDomainResourcesSelected;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}
	
	public boolean getListRendered() {
		return getMetricDomainResources().size() > 0;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(metricDomainResources, new Comparator<MetricDomainResourceBean>() {
			public int compare(MetricDomainResourceBean o1, MetricDomainResourceBean o2) {
				int res = 0;
				try {
					if (getDomainTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getDomainType().toLowerCase().compareTo(o2.getDomainType().toLowerCase()) :  o2.getDomainType().toLowerCase().compareTo(o1.getDomainType().toLowerCase());
					}
					else if (getMetricDomainColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getMetricDomain().toLowerCase().compareTo(o2.getMetricDomain().toLowerCase()) : o2.getMetricDomain().toLowerCase().compareTo(o1.getMetricDomain().toLowerCase());
					}
					else if (getResourceNameColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getResourceName().toLowerCase().compareTo(o2.getResourceName().toLowerCase()) : o2.getResourceName().toLowerCase().compareTo(o1.getResourceName().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});		
	}
	
}
