package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.MonitorBusinessFilterBean;
import com.fsi.monitoring.datamodel.bean.SelectedItemBean;
import com.fsi.monitoring.datamodel.bean.factory.MonitorAttributeUIBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.IkrStaticDomainCreateBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainResource.IkrCategoryResourceBean;
import com.fsi.monitoring.ikr.model.DataFrequency;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfigField;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.monitor.MonitorConfigAttributeKey;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;
import com.fsi.monitoring.util.FacesUtils;

public class MetricDomainConfigAttributesBean implements Serializable {
	private static final long serialVersionUID = 4224971416355277243L;
	private final static Logger logger = Logger.getLogger(MetricDomainConfigAttributesBean.class);	
	
	private MonitorConfig monitorConfig;
		
	private List<SelectedItemBean> domainsItems;
	
	private Map<String, MonitorAttributeUIBean> attributeFields;
	private Map<String, MonitorAttributeUIBean> attributeSyncFields;
	private Map<String, String> attributeFieldKeys;
	private List<SelectedItemBean> filterKeys;	
	private List<SelectedItemBean> selectedMetricDomainItems;	
	private List<MonitorBusinessFilterBean> filterSet;
	private List<SelectedItemBean> monitorMetricDomainItems;	
	
	private boolean renderDataSynchonizationCpt;
	
	private Map<Integer, IkrCategoryResourceBean> ikrCategoriesWithNoResources;
	private  Map<Integer, IkrCategoryResourceBean> ikrCategoriesResourceBeans;
	private Set<Integer> categoryResourcesToDelete;
	
	public MetricDomainConfigAttributesBean(MonitorConfig monitorConfig) {
		this.monitorConfig = monitorConfig;	
	}
	
	public void init() {		
		selectedMetricDomainItems = new ArrayList<SelectedItemBean>();
		List<String> metricViewSet = new ArrayList<String>();
		String metricView = monitorConfig.getAttribute("METRIC_VIEW");
		if (metricView!=null && metricView.length()>0){
			String[] viewSet = metricView.split(":");
			for (String view : viewSet) {
				selectedMetricDomainItems.add(new SelectedItemBean(view));
				metricViewSet.add(view);
			}
		}
		
		monitorMetricDomainItems = new ArrayList<SelectedItemBean>();
		domainsItems = new ArrayList<SelectedItemBean>();
		filterKeys = new ArrayList<SelectedItemBean>();
		for (String item : monitorConfig.getMetricDomainConfig().getDomainItemConfigs()) { 
			filterKeys.add(new SelectedItemBean(item));
			domainsItems.add(new SelectedItemBean(item));
			if (!metricViewSet.contains(item))
				monitorMetricDomainItems.add(new SelectedItemBean(item));				
		}
		
		filterSet = new ArrayList<MonitorBusinessFilterBean>();
		String filters = monitorConfig.getAttribute("BUSINESS_FILTERS");
		if (filters!=null && filters.length()>0){
			String[] viewSet = filters.split(":");
			for (String view : viewSet) {
				String[] tmp = view.split("=");
				filterSet.add(new MonitorBusinessFilterBean(tmp[0], tmp[1]));				
			}
		}
		
		metricViewSet.clear();
		metricViewSet = null;
		
		initIkrCategoryResource();
		modifyFields();
	}
	
	public List<SelectedItemBean> getMonitorMetricDomainItems() {
		return monitorMetricDomainItems;
	}	
	
	public List<SelectedItemBean> getSelectedMetricDomainItems() {
		return selectedMetricDomainItems;
	}
	
	public List<MonitorBusinessFilterBean> getFilterSet() {
		return filterSet;
	}

	public List<SelectedItemBean> getFilterKeys() {
		return filterKeys;
	}
	
	public boolean isSqlQueryConfigEnabled() {
		boolean show = false;
		
		Map<String, String> attrs = monitorConfig.getMetricDomainConfig().getAttributes();
		if (attrs!=null) {
			try {
				show = Boolean.parseBoolean(attrs.get(MonitorConfigAttributeKey.ON_THE_FLY));
			}
			catch (Exception e) {}
		}
		
		return show;
	}
	
	public boolean isAttributeTabEnabled() {
		boolean show = false;
		
		show = isSqlQueryConfigEnabled();
		
		if (isAttributesEnabled()) {
			show = true;
		}
			
		if (isDomainItemsEnabled()) {
			show = true;
		}
		
		return show;
	}
	
	public boolean isAttributesEnabled() {
		boolean show = false;
		if (attributeFieldKeys!=null && attributeFieldKeys.size()>0) {
			show = true;
		}		
		return show;
	}

	public boolean isDomainItemsEnabled () {
		boolean show = false;
		if (domainsItems.size()>0)
			show = true;
		return  show;
	}
	
	private Map<Integer, IkrStaticDomain> getMetricCategories() {
		Map<Integer, IkrStaticDomain> metricCategories = new HashMap<Integer, IkrStaticDomain>();
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			List<IkrStaticDomain> categories = dataModelPM.getIkrStaticDomains(monitorConfig.getMetricDomainConfig().getIkrStaticDomainId());
			for (IkrStaticDomain cat : categories) {
				metricCategories.put(cat.getId(), cat);
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
		
		return metricCategories;
	}
	
	private Map<Integer, IkrCategoryResource> getCategoryResourcesConfigured() {
		Map<Integer, IkrCategoryResource> res = new HashMap<Integer, IkrCategoryResource>();
		try {			
			String ikrCategoryResourceIdsStr = monitorConfig.getAttribute(MonitorConfigAttributeKey.CATEGORY_RESOURCE);
			if (ikrCategoryResourceIdsStr!=null&&ikrCategoryResourceIdsStr.length()>0) {
				List<Integer> ids = new ArrayList<Integer>();
				for (String str : ikrCategoryResourceIdsStr.split(",")) {
					ids.add(Integer.parseInt(str));
				}
				DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
				List<IkrCategoryResource> categoryResources = dataModelPM.getIkrCategoryResources(ids);
				for (IkrCategoryResource resource : categoryResources) {
					res.put(resource.getIkrStaticDomainId(), resource);
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
		return res;
	}
	
	private void initIkrCategoryResource() {
		categoryResourcesToDelete = new HashSet<Integer>();
		ikrCategoriesWithNoResources = new HashMap<Integer, IkrCategoryResourceBean>();
		ikrCategoriesResourceBeans = new HashMap<Integer, IkrCategoryResourceBean>();
		if (monitorConfig.isOnTheFly()) {
			try {
				MetricDomainConfigResource configResource = monitorConfig.getMetricDomainConfig().getResources().get(0);
				int metricDomainResourceId = configResource.getResource().getId();
				Map<Integer, IkrStaticDomain> metricCategories = getMetricCategories();
				Map<Integer, IkrCategoryResource> categoryResourcesConfigured = getCategoryResourcesConfigured();			
				for (int metricCategoryId : metricCategories.keySet()) {
					IkrStaticDomain domain = metricCategories.get(metricCategoryId);
					IkrCategoryResource resource = categoryResourcesConfigured.get(metricCategoryId);				
					if (resource!=null) {					
						ikrCategoriesResourceBeans.put(metricCategoryId, new IkrCategoryResourceBean(domain.getLabel(), resource));
					}
					else {
						ikrCategoriesWithNoResources.put(metricCategoryId, new IkrCategoryResourceBean(domain.getLabel(), new IkrCategoryResource(0, metricCategoryId, metricDomainResourceId, "", true)));
					}
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void modifyFields() {
		initFields();
		
		Map<String, String> attributes = monitorConfig.getAttributes();
		for(String attribute : attributes.keySet()) {
			if ("METRIC_VIEW".equalsIgnoreCase(attribute) || "BUSINESS_FILTERS".equalsIgnoreCase(attribute))				
				continue;
			
			if (!attributeSyncFields.containsKey(attribute)) {
				String fieldKey = attributeFieldKeys.get(attribute);
				MonitorAttributeUIBean uiBean = attributeFields.get(fieldKey);
				if (uiBean!= null)
					uiBean.setValue(attributes.get(attribute));
			}
			else {
				MonitorAttributeUIBean uiBean = attributeSyncFields.get(attribute);
				if (uiBean!= null)
					uiBean.setValue(attributes.get(attribute));
			}
		}	
	}
	
	public void removeAllCategoryResources(ActionEvent action) {
		Iterator<IkrCategoryResourceBean> iterator = ikrCategoriesResourceBeans.values().iterator();
		while (iterator.hasNext()) {
			IkrCategoryResourceBean next = iterator.next();
			iterator.remove();
			ikrCategoriesWithNoResources.put(next.getCategoryResource().getIkrStaticDomainId(), next);
			categoryResourcesToDelete.add(next.getCategoryResource().getIkrStaticDomainId());
		}
	}	
	
	public void removeCategoryResource(ActionEvent action) {
		IkrCategoryResourceBean ikrCategoryResourceBean =(IkrCategoryResourceBean)action.getComponent().getAttributes().get("metricCategoryResource");
		Iterator<IkrCategoryResourceBean> iterator = ikrCategoriesResourceBeans.values().iterator();
		while (iterator.hasNext()) {
			IkrCategoryResourceBean next = iterator.next();
			if (ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId() == next.getCategoryResource().getIkrStaticDomainId()) {
				iterator.remove();
				ikrCategoriesWithNoResources.put(ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId(), ikrCategoryResourceBean);
				categoryResourcesToDelete.add(ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId());
			}
		}		
	}	
	
	public void addMetricCategoryResource(ActionEvent action) {
		IkrCategoryResourceBean ikrCategoryResourceBean =(IkrCategoryResourceBean)action.getComponent().getAttributes().get("metricCategoryResource");
		int ikrStaticDomainId = ikrCategoryResourceBean.getCategoryResource().getIkrStaticDomainId();
		ikrCategoriesResourceBeans.put(ikrStaticDomainId, ikrCategoryResourceBean);		
		ikrCategoriesWithNoResources.remove(ikrStaticDomainId);
		if (categoryResourcesToDelete.contains(ikrStaticDomainId)) {
			categoryResourcesToDelete.remove(ikrStaticDomainId);
		}
	}	
	
	public void addMetricDomainItem(ActionEvent event) {
		SelectedItemBean item = (SelectedItemBean)event.getComponent().getAttributes().get("item");
		selectedMetricDomainItems.add(item);
		monitorMetricDomainItems.remove(item);
	}

	public void removeMetricDomainItem(ActionEvent event) {
		SelectedItemBean item = (SelectedItemBean)event.getComponent().getAttributes().get("item");
		monitorMetricDomainItems.add(item);
		selectedMetricDomainItems.remove(item);
	}
	
	public void upMetricDomainItem(ActionEvent event) {
		SelectedItemBean itemToMoveUp = (SelectedItemBean)event.getComponent().getAttributes().get("item");
		int i = 0;
		for(SelectedItemBean item : selectedMetricDomainItems) {
			if (item.equals(itemToMoveUp)) {
				item.setSelected(false);
				if (i>0){
					selectedMetricDomainItems.add(i-1, item);
					selectedMetricDomainItems.remove(i+1);
				}
				break;
			}
			i++;
		}
	}
	
	public void downMetricDomainItem(ActionEvent event) {
		SelectedItemBean itemToMoveDown = (SelectedItemBean)event.getComponent().getAttributes().get("item");
		int i = 0;
		for(SelectedItemBean item : selectedMetricDomainItems) {
			if (item.equals(itemToMoveDown)) {				
				if (i<selectedMetricDomainItems.size()-1) {
					SelectedItemBean clone = new SelectedItemBean(item.getValue());
					selectedMetricDomainItems.remove(item);
					selectedMetricDomainItems.add(i+1, clone);					
				}
				break;			
			}
			i++;
		}
	}
	
	public void realtimeDataValueChanged(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
 			return;
 		}

		renderDataSynchonizationCpt = (Boolean)evt.getNewValue();
	}
	
	public void addBussinessFilter(ActionEvent event) {
		SelectedItemBean itemToAdd = (SelectedItemBean)event.getComponent().getAttributes().get("item");
		for(SelectedItemBean item : filterKeys) {
			if (item.equals(itemToAdd)) {
				item.setSelected(false);
				filterSet.add(new MonitorBusinessFilterBean((String)item.getValue(), ""));
				break;
			}			
		}
	}

	public void removeBussinessFilter(ActionEvent event) {
		MonitorBusinessFilterBean itemToRemove = (MonitorBusinessFilterBean)event.getComponent().getAttributes().get("item");
		for(MonitorBusinessFilterBean item : filterSet) {
			if (item.equals(itemToRemove)) {
				filterSet.remove(item);
				break;
			}			
		}
	}	
	
	private void initFields() {
		attributeFields = new HashMap<String, MonitorAttributeUIBean>();
		for (int i=1; i<21; i++) {
			attributeFields.put("attribute"+i, new MonitorAttributeUIBean(null));
		}
		
		attributeSyncFields = new HashMap<String, MonitorAttributeUIBean>();
		attributeSyncFields.put("DATA_SYNCHRONIZATION", new MonitorAttributeUIBean(null));
		attributeSyncFields.put("STAT_FREQUENCY", new MonitorAttributeUIBean(null));
		
		
		List<MetricDomainConfigField> attrConfigs = monitorConfig.getMetricDomainConfig().getFields();		
		attributeFieldKeys = new HashMap<String, String>();
		int i = 1;
		for (MetricDomainConfigField field : attrConfigs) {
			if (monitorConfig.getMetricDomainConfig().isUseDataSynchronization() && "STAT_FREQUENCY".equals(field.getName())) {
				List<String> newDataFrequencies = new ArrayList<String>();
				if (IkrMonitorSchedulerConfig.ONE_SHOT.equals(monitorConfig.getSchedulerConfig().getType())) {
					for (DataFrequency value : DataFrequency.values()) {
						if (!DataFrequency.NONE.name().equals(value.name())) {
							newDataFrequencies.add(value.name());
						}
					}
				}
				else {
					for (DataFrequency value : DataFrequency.values()) {
						newDataFrequencies.add(value.name());
					}
				}
				field.setFieldTypeValues(newDataFrequencies);
			}
			
			if (!attributeSyncFields.containsKey(field.getName())) {
				String key = "attribute"+i;
				attributeFields.get(key).setField(field);
				attributeFieldKeys.put(field.getName(), key);
				i++;
			}
			else {
				attributeSyncFields.get(field.getName()).setField(field);
			}
		}
	}
	
	public String saveIkrCategoryResource() throws PersistenceException{
		int sz = ikrCategoriesResourceBeans.size();
		if (sz==0)
			return null;
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		String ikrCategoryResourceIds = "";
		try {
			MetricDomainConfigResource configResource = monitorConfig.getMetricDomainConfig().getResources().get(0);
			int metricDomainResourceId = configResource.getResource().getId();			
			int i = 0;
			for (IkrCategoryResourceBean bean : ikrCategoriesResourceBeans.values()) {
				IkrCategoryResource ikrCategoryResource = bean.getCategoryResource();
				ikrCategoryResource.setMetricDomainResourceId(metricDomainResourceId);
				if (ikrCategoryResource.getName()!=null&&ikrCategoryResource.getName().length()>0){
					int id = dataModelPM.getIkrCategoryResourceId(ikrCategoryResource.getIkrStaticDomainId(), ikrCategoryResource.getMetricDomainResourceId(), ikrCategoryResource.getName());
					if (id==0)
						id = dataModelPM.saveIkrCategoryResource(ikrCategoryResource);
					ikrCategoryResourceIds = ikrCategoryResourceIds + id;
					if (i<sz-1)
						ikrCategoryResourceIds = ikrCategoryResourceIds + ",";					
				}
				i++;
			}
			
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
			throw new PersistenceException(e.getMessage(),PersistenceException.EXCEPTION);
		}
		
		return ikrCategoryResourceIds;
	}
	
	public void update() {
		for(String attribute:attributeFieldKeys.keySet()){
			String key = attributeFieldKeys.get(attribute);
			MonitorAttributeUIBean uiBean = attributeFields.get(key);
			monitorConfig.addAttribute(attribute, uiBean.getValue());
		}
		
		if (monitorConfig.getMetricDomainConfig().isUseDataSynchronization()) {
			for(MonitorAttributeUIBean uiBean:attributeSyncFields.values()){				
				monitorConfig.addAttribute(uiBean.getField().getName(), uiBean.getValue());
			}
		}
			
		String metricView = "";
		int len = selectedMetricDomainItems.size();
		int i = 1;
		for(SelectedItemBean item : selectedMetricDomainItems) {
			metricView = metricView + item.getValue();
			if (i < len)
				metricView = metricView + ":";
			i++;
		}
		
		if (monitorConfig.getAttributes().containsKey("METRIC_VIEW")) 
			monitorConfig.getAttributes().remove("METRIC_VIEW");
		
		if (metricView.length()>0) 
			monitorConfig.addAttribute("METRIC_VIEW", metricView);
	
		
		String filter = "";
		len = filterSet.size();
		i = 1;
		for(MonitorBusinessFilterBean item : filterSet) {
			filter = filter + item.toString();
			if (i < len)
				filter = filter + ":";
			i++;
		}
		
		if (monitorConfig.getAttributes().containsKey("BUSINESS_FILTERS")) 
			monitorConfig.getAttributes().remove("BUSINESS_FILTERS");
		
		if (filter.length()>0) 
			monitorConfig.addAttribute("BUSINESS_FILTERS", filter);
	}

	public Map<String, MonitorAttributeUIBean> getAttributeFields() {
		return attributeFields;
	}

	public Map<String, MonitorAttributeUIBean> getAttributeSyncFields() {
		return attributeSyncFields;
	}

	public boolean isRenderDataSynchonizationCpt() {
		return renderDataSynchonizationCpt;
	}
	
	public boolean isRenderIkrCatgoryResourceConfig() {
		return ikrCategoriesWithNoResources.size()>0;
	}
	
	public String getSqlSelectClause() {
		String clause = monitorConfig.getAttribute(MonitorConfigAttributeKey.SQL_SELECT);
		if (clause == null)
			clause = "";
		return clause;
	}
	
	public void setSqlSelectClause(String clause) {
		monitorConfig.addAttribute(MonitorConfigAttributeKey.SQL_SELECT, (clause!=null)?clause.toUpperCase().trim():null);
	}
	
	public String getSqlFromClause() {
		String clause = monitorConfig.getAttribute(MonitorConfigAttributeKey.SQL_FROM);
		if (clause == null)
			clause = "";
		return clause;
	}
	
	public void setSqlFromClause(String clause) {
		monitorConfig.addAttribute(MonitorConfigAttributeKey.SQL_FROM, (clause!=null)?clause.toUpperCase().trim():null);
	}
	
	public String getSqlWhereClause() {
		String clause = monitorConfig.getAttribute(MonitorConfigAttributeKey.SQL_WHERE);
		if (clause == null)
			clause = "";
		return clause;
	}
	
	public void setSqlWhereClause(String clause) {
		monitorConfig.addAttribute(MonitorConfigAttributeKey.SQL_WHERE, (clause!=null)?clause.toUpperCase().trim():null);
	}
	
	public String getSqlOrderByClause() {
		String clause = monitorConfig.getAttribute(MonitorConfigAttributeKey.SQL_ORDER_BY);
		if (clause == null)
			clause = "";
		return clause;
	}
	
	public void setSqlOrderByClause(String clause) {
		monitorConfig.addAttribute(MonitorConfigAttributeKey.SQL_ORDER_BY, (clause!=null)?clause.toUpperCase().trim():null);
	}
	
	public String getSqlGroupByClause() {
		String clause = monitorConfig.getAttribute(MonitorConfigAttributeKey.SQL_GROUP_BY);
		if (clause == null)
			clause = "";
		return clause;
	}
	
	public void setSqlGroupByClause(String clause) {
		monitorConfig.addAttribute(MonitorConfigAttributeKey.SQL_GROUP_BY, (clause!=null)?clause.toUpperCase().trim():null);
	}
	
	public List<IkrCategoryResourceBean> getIkrCategoriesWithNoResources() {
		List<IkrCategoryResourceBean> values = new ArrayList<IkrCategoryResourceBean>(ikrCategoriesWithNoResources.values());
		Collections.sort(values, new Comparator<IkrCategoryResourceBean>() {
			public int compare(IkrCategoryResourceBean o1, IkrCategoryResourceBean o2) {
				return o1.getMetricCategory().compareTo(o2.getMetricCategory());
			}
		});
		return values;
	}
	
	public List<IkrCategoryResourceBean> getCategoryResourceBeans() {
		List<IkrCategoryResourceBean> values = new ArrayList<IkrCategoryResourceBean>(ikrCategoriesResourceBeans.values());
		Collections.sort(values, new Comparator<IkrCategoryResourceBean>() {
			public int compare(IkrCategoryResourceBean o1, IkrCategoryResourceBean o2) {
				return o1.getMetricCategory().compareTo(o2.getMetricCategory());
			}
		});
		return values;
	}	
}
