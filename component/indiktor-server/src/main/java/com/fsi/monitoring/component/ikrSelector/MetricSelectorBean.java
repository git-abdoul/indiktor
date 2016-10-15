package com.fsi.monitoring.component.ikrSelector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.MetricGroupBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.msd.StaticData;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;

public class MetricSelectorBean 
extends IkrStaticDomainSelectorBean 
implements Serializable {
	
	private static final long serialVersionUID = -2220970006763460987L;

	private static final Logger logger = Logger.getLogger(MetricSelectorBean.class);
	
	private static final String envColumnName = "Environment";
	private static final String contextColumnName = "Context";
	private static final String categoryColumnName = "Metric Category";
	private static final String instanceColumnName = "Metric Instance";
	private static final String domainColumnName = "Metric Domain";
	
	private String filter;
	
	private boolean fromAlertDefinition = false;

	private List<SelectItem> logicalEnvItems = null;
	private int logicalEnvId;
	
	private List<SelectItem> contextItems = null;
	private String context;
	
	private List<SelectItem> domainViewItems = null;
	private String domainView;
	private long monitorId;
	private int metricDomainId;
	
	private Collection<Integer> domainTypeIds;
	private Collection<Integer> metricDomainIds;
	private Collection<Integer> metricCategoryIds;
	private Collection<IkrUnitType> existingUnitTypes;
	
	private IkrUnitType ikrUnitType;
	private SelectItem[] ikrUnitTypes;
	
	private int selectedTableScrollWitdh;
	private int selectedTableWitdh;
	private int selectedTableScrollHeight;
	
	private boolean rendered;	
	private boolean dashboardMode = false;
	private List<ModifiableMetricBean> selectionBeans = null;
	private List<ModifiableMetricBean> orderedSelections = null;

	// The selected Beans
	private ArrayList<ModifiableMetricBean> selectedBeans = null;

	private MetricSelectorVisitor visitor;	
	private boolean allSelected;
	
	private boolean metricGroupSelector;
	
	private boolean searchON;
	private String searchQuery;
	
	private int rowsByPage = 6;
	private boolean paginationVisible = false;
	
	private String sortColumnName;
	private boolean ascending = true;
	
	private boolean selectAll;
	private boolean testSelectAll = false;
	
	private boolean reInitSelected;
	
	private String searchQStyle;
	
	private Map<Long, MonitorConfig> monitorConfigs;
	
	public MetricSelectorBean() {
		super();
		reInitSelected = false;
	}

	public void init() {
		searchON = true;
		sortColumnName = categoryColumnName;
		metricGroupSelector = false; // default value	
		selectedTableScrollWitdh = 305;
		selectedTableWitdh = 300;
		selectedTableScrollHeight = 600;
		dashboardMode = false;
		selectionBeans = new ArrayList<ModifiableMetricBean>();
		if (reInitSelected) {
			if (selectedBeans == null)
				selectedBeans = new ArrayList<ModifiableMetricBean>();
		}
		else {
			selectedBeans = new ArrayList<ModifiableMetricBean>();
		}
		this.visitor = null;
		initSelections();
	}
	
	public void activateSearch (ActionEvent event) {
		searchON = true;
		initSelections();	
		if(testSelectAll) {
			if (!event.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
				event.setPhaseId(PhaseId.INVOKE_APPLICATION);
				event.queue();
				return;
			}
		}
		selectAll = false;
		testSelectAll = true;
	}
	
	private void initSelections() {
		selectionBeans = new ArrayList<ModifiableMetricBean>();
		MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
		BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
		List<Long> ikrDefinitionIds;
		try {
			ikrDefinitionIds = monitoringPM.getIkrDefinitionIds();
			List<IkrDefinitionBean> beans = beanPM.getIkrDefinitionBeans(ikrDefinitionIds);
			for (IkrDefinitionBean bean : beans) {
				selectionBeans.add(new ModifiableMetricBean(bean));
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void activateQuickSearch (ActionEvent event) {
		searchON = false;
		selectionBeans = new ArrayList<ModifiableMetricBean>();
		initComponent(false, true, true, true);
		initLogicalEnvItems();
		if(testSelectAll) {
			if (!event.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
				event.setPhaseId(PhaseId.INVOKE_APPLICATION);
				event.queue();
				return;
			}
		}
		selectAll = false;
		testSelectAll = true;
	}
	
	public void searchMetricQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		initSelections();
		if(testSelectAll) {
			if (!event.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
				event.setPhaseId(PhaseId.INVOKE_APPLICATION);
				event.queue();
				return;
			}
		}
		selectAll = false;
		testSelectAll = true;
	}
	
	public void filterMetrics() {
		if (searchQuery!=null && searchQuery.length()>0) {
			searchQuery = searchQuery.toLowerCase();
			List<String> queryItems = IkrUtils.splitSearchIndex(searchQuery);
			String[] searchQueryItems = (String[])queryItems.toArray(new String[queryItems.size()]);
			List<ModifiableMetricBean> newSelectionList = new ArrayList<ModifiableMetricBean>();
			for (ModifiableMetricBean bean : selectionBeans) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newSelectionList.add(bean);
			}
			selectionBeans = new ArrayList<ModifiableMetricBean>();
			selectionBeans.addAll(newSelectionList);
		}
	}
	
	public void accept(MetricSelectorVisitor visitor) {
		this.visitor = visitor;
	}
	
	public void setMetricGroupSelectorMode() {
		metricGroupSelector = true;
	}
	
	public void onChangeIkrUnitType(ValueChangeEvent event) {
		IkrUnitType ikrUnitType = (IkrUnitType)event.getNewValue();
		changeIkrUnitType(ikrUnitType);
	}
	
	public void changeIkrUnitType(IkrUnitType ikrUnitType) {
		this.ikrUnitType = ikrUnitType;
	}
	
	private void initLogicalEnvItems() {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			Map<Integer, LogicalEnv> logicalEnvMap = dataModelPM.getLogicalEnvs();
			
			logicalEnvItems = new ArrayList<SelectItem>(logicalEnvMap.size());
			
		    for (LogicalEnv logicalEnv : logicalEnvMap.values()) {		        	
		    	SelectItem item = new SelectItem(logicalEnv.getId(),logicalEnv.getName());
		    	logicalEnvItems.add(item);
			}
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
    	
    	changeLogicalEnv((Integer)logicalEnvItems.get(0).getValue());
	}
	
	public List<SelectItem> getLogicalEnvItems() {
		if (logicalEnvItems!=null && logicalEnvItems.size()>0){
			Collections.sort(logicalEnvItems, new Comparator<SelectItem>() {
				public int compare(SelectItem o1, SelectItem o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}
		return logicalEnvItems;
	}	
	
	public int getLogicalEnvId() {
		return logicalEnvId;
	}
	
	public void setLogicalEnvId(int logicalEnvId) {}
	
	public String getContext() {
		return context;
	}
	
	public void setContext(String context) {}	
	
	public List<SelectItem> getContextItems() {
		if (contextItems!=null && contextItems.size()>0){
			Collections.sort(contextItems, new Comparator<SelectItem>() {
				public int compare(SelectItem o1, SelectItem o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}
		return contextItems;	
	}	
	
	public List<SelectItem> getDomainViewItems() {
		if (domainViewItems!=null && domainViewItems.size()>0){
			Collections.sort(domainViewItems, new Comparator<SelectItem>() {
				public int compare(SelectItem o1, SelectItem o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}
		return domainViewItems;
	}

	public String getDomainView() {
		return domainView;
	}

	public void setDomainView(String domainView) {
		this.domainView = domainView;
	}
	
	public IkrUnitType getIkrUnitType() {
		return ikrUnitType;
	}

	public void setIkrUnitType(IkrUnitType ikrUnitType) {}	
	
	public SelectItem[] getIkrUnitTypes() {
		return ikrUnitTypes;
	}	

	public void onChangeLogicalEnv(ValueChangeEvent e) {
		int logicalEnvId = (Integer)e.getNewValue();
		changeLogicalEnv(logicalEnvId);
	}
	
	public void onChangeContext(ValueChangeEvent e) {
		context = (String)e.getNewValue();
		changeContext(context);
	}	
	
	public void onChangeDomainView(ValueChangeEvent e) {
		monitorId = Long.parseLong((String)e.getNewValue());
		changeDomainView(monitorId);
	}
	
	public void changeDomainView(long monitorId) {
		this.monitorId = monitorId;
		this.changeMetricCategory(getMetricCategoryId());
	}	
	
	@Override
	protected void changeMetricDomain(int metricDomainId) {	
		this.metricDomainId = metricDomainId;	
		for(MonitorConfig config : monitorConfigs.values()) {
			if ((metricDomainId == config.getMetricDomainConfig().getIkrStaticDomainId()) && config.getContext().equals(context) ) {
				monitorId = config.getId();
				break;
			}
		}
		super.changeMetricDomain(metricDomainId);
		changeDomainViewItems();
	}

	public void changeDomainViewItems() {
		try {
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			Map<Long, MonitorConfig> monitorConfigs = dataModelPM.getMonitorConfigs(logicalEnvId);			
			domainViewItems = new ArrayList<SelectItem>();
			domainViewItems.add(new SelectItem(0, "N/A"));
			for(MonitorConfig config : monitorConfigs.values()) {
				if (metricDomainId == config.getMetricDomainConfig().getIkrStaticDomainId()) {
					if (config.getDomainView()!= null && config.getDomainView().length()>0) {
						SelectItem item = new SelectItem(config.getId(), config.getDomainView());
						domainViewItems.add(item);
					}
				}
			}
			
			changeDomainView((Integer)domainViewItems.get(0).getValue());
			
		} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	private void changeContext(String context) {
		this.context = context;
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			
			domainTypeIds = new ArrayList<Integer>();
			metricDomainIds = new ArrayList<Integer>();
			metricCategoryIds = new ArrayList<Integer>();
			
			if (context.equals(CrossComputeDefinition.CROSS_COMPUTE_CONTEXT)) {
				this.monitorId = 0;
				MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
				Map<Long, AbstractIkrDefinition> crossComputeDefinitions = monitoringPM.getCrossComputeDefinitions(logicalEnvId);
				
				for(AbstractIkrDefinition definition: crossComputeDefinitions.values()) {
					int metricCategoryId = definition.getIkrCategoryId();
					metricCategoryIds.add(metricCategoryId);
					IkrStaticDomain metricCategory = dataModelPM.getIkrStaticDomain(metricCategoryId);
					int metricDomainId = metricCategory.getParentDomainId();
					metricDomainIds.add(metricDomainId);
					IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricDomainId);
					domainTypeIds.add(metricDomain.getParentDomainId());
				}
			} 
			else if (context.equals(StaticData.STATIC_DATA_CONTEXT)) {
				this.monitorId = -1;
				MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
				Map<Long, AbstractIkrDefinition> staticDatas = monitoringPM.getStaticDataDefinitions(logicalEnvId);
				
				for(AbstractIkrDefinition definition: staticDatas.values()) {
					int metricCategoryId = definition.getIkrCategoryId();
					metricCategoryIds.add(metricCategoryId);
					IkrStaticDomain metricCategory = dataModelPM.getIkrStaticDomain(metricCategoryId);
					int metricDomainId = metricCategory.getParentDomainId();
					metricDomainIds.add(metricDomainId);
					IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricDomainId);
					domainTypeIds.add(metricDomain.getParentDomainId());
				}
			} 			
			else {					
				Map<Long, MonitorConfig> monitorConfigs = dataModelPM.getMonitorConfigs(logicalEnvId);
				
				for (MonitorConfig monitorConfig : monitorConfigs.values()) {
					if (monitorConfig.getContext().equals(context)) {
						this.monitorId = monitorConfig.getId();
						int metricDomainId = monitorConfig.getMetricDomainConfig().getIkrStaticDomainId();
						metricDomainIds.add(metricDomainId);
						List<IkrStaticDomain> metricCategories = dataModelPM.getIkrStaticDomains(metricDomainId);	
						for (IkrStaticDomain domain : metricCategories)
							metricCategoryIds.add(domain.getId());
						
						IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricDomainId);
						domainTypeIds.add(metricDomain.getParentDomainId());
					}
				}
			}
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
		super.initItems();
	}
	
	private void changeContextItems() {
		try {			
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			monitorConfigs = dataModelPM.getMonitorConfigs(logicalEnvId);
			
			Set<String> ctxTmp = new HashSet<String>();
			contextItems = new ArrayList<SelectItem>();
			contextItems.add(new SelectItem(CrossComputeDefinition.CROSS_COMPUTE_CONTEXT, CrossComputeDefinition.CROSS_COMPUTE_CONTEXT));
			contextItems.add(new SelectItem(StaticData.STATIC_DATA_CONTEXT, StaticData.STATIC_DATA_CONTEXT));
			for (MonitorConfig monitorConfig : monitorConfigs.values()) {
				String ctx = monitorConfig.getContext();
				if (!ctxTmp.contains(ctx)) {				
					SelectItem item = new SelectItem(ctx, ctx);
					contextItems.add(item);
					ctxTmp.add(ctx);
				}
			}
			
			changeContext((String)contextItems.get(0).getValue());
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	private void changeLogicalEnv(int logicalEnvId) {
		this.logicalEnvId = logicalEnvId;
		changeContextItems();
	}	
	
	public boolean isValidDomainType(int domainTypeId) {
		return domainTypeIds.contains(domainTypeId);
	}
	
	public boolean isValidMetricDomain(int metricDomainId) {
		return metricDomainIds.contains(metricDomainId);
	}	
	
	public boolean isValidMetricCategory(int metricCategoryId) {
		return metricCategoryIds.contains(metricCategoryId);
	}	

	protected void changeMetricCategory(int metricCategoryId) {
		super.changeMetricCategory(metricCategoryId);
		
		try {
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			Map<Long, AbstractIkrDefinition> ikrDefinitions = monitoringPM.getIkrDefinitions(logicalEnvId, context, getMetricCategoryId());
			
			selectionBeans.clear();
			
		    for (AbstractIkrDefinition ikrDefinition : ikrDefinitions.values()) {
		    	long id = 0;
	    		if (ikrDefinition instanceof StaticData)
	    			id = -1;
	    		else if (ikrDefinition instanceof IkrDefinition)
	    			id = ((IkrDefinition)ikrDefinition).getMonitorId();	    			
	    		
		    	if (monitorId == id || monitorId==0 || monitorId==-1) {
					MetricGroupBean metricGroupBean = beanPM.getIkrDefinitionBean(ikrDefinition.getId());
		    		ModifiableMetricBean bean = new ModifiableMetricBean(metricGroupBean);
		    		selectionBeans.add(bean);
		    	}
	    	}
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	public List<ModifiableMetricBean> getSelectionBeans() {
		if (searchON)
			filterMetrics();		
		orderedSelections = new ArrayList<ModifiableMetricBean>(selectionBeans);
		
		Collections.sort(orderedSelections, new Comparator<ModifiableMetricBean>() {
			public int compare(ModifiableMetricBean o1, ModifiableMetricBean o2) {
				int res = 0;
					if (getEnvColumnName().equals(getSortColumnName())) {
						String val1 = o1.getMetricGroupBean().getLogicalEnv().getName().toLowerCase();
						String val2 = o2.getMetricGroupBean().getLogicalEnv().getName().toLowerCase();
						res = ascending ? val1.compareTo(val2) : val2.compareTo(val1);
					}
					else if (getContextColumnName().equals(getSortColumnName())) {
						String val1 = o1.getMetricGroupBean().getContext().toLowerCase();
						String val2 = o2.getMetricGroupBean().getContext().toLowerCase();
						res = ascending ? val1.compareTo(val2) : val2.compareTo(val1);
					}
					else if (getDomainColumnName().equals(getSortColumnName())) {
						String val1 = o1.getMetricGroupBean().getMetricDomain().getLabel().toLowerCase();
						String val2 = o2.getMetricGroupBean().getMetricDomain().getLabel().toLowerCase();
						res = ascending ? val1.compareTo(val2) : val2.compareTo(val1);
					}
					else if (getCategoryColumnName().equals(getSortColumnName())) {
						String val1 = o1.getMetricGroupBean().getIkrCategory().getLabel().toLowerCase();
						String val2 = o2.getMetricGroupBean().getIkrCategory().getLabel().toLowerCase();
						res = ascending ? val1.compareTo(val2) : val2.compareTo(val1);
					}
					else if (getInstanceColumnName().equals(getSortColumnName())) {
						String val1 = ((IkrDefinitionBean)(o1.getMetricGroupBean())).getFullIkrInstance().toLowerCase();
						String val2 = ((IkrDefinitionBean)(o2.getMetricGroupBean())).getFullIkrInstance().toLowerCase();
						res = ascending ? val1.compareTo(val2) : val2.compareTo(val1);
					}
				return res;
			}
		});	
		return orderedSelections;
	}
	
	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}	

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}	
	
	public boolean isMetricGroupSelector() {
		return metricGroupSelector;
	}

	private ModifiableMetricBean cloneModifiableMetricBean(ModifiableMetricBean ikrDefinitionBean) {
		MetricGroupBean newBean = null;
		MetricGroupBean oldBean = ikrDefinitionBean.getMetricGroupBean();
		if (metricGroupSelector) {
			newBean = new MetricGroupBean(oldBean.getIkrCategory(), oldBean.getContext(), oldBean.getLogicalEnv(), oldBean.getDomainView());
			newBean.setDomainType(oldBean.getDomainType());
			newBean.setMetricDomain(oldBean.getMetricDomain());
		} else {
			newBean = oldBean;
		}
		return new ModifiableMetricBean(newBean);
	}	
	
	public void add(ActionEvent event) {
		ModifiableMetricBean selectedMetric = (ModifiableMetricBean)event.getComponent().getAttributes().get("filesRecord");
		Collection<ModifiableMetricBean> beans = new ArrayList<ModifiableMetricBean>();
		ModifiableMetricBean newBean = cloneModifiableMetricBean(selectedMetric);
		beans.add(newBean);
		if (!selectedBeans.contains(newBean))
			selectedBeans.add(newBean);
		
//		Collection<ModifiableMetricBean> beans = new ArrayList<ModifiableMetricBean>();
//		for(ModifiableMetricBean ikrDefinitionBean : selectionBeans) {
//			if (ikrDefinitionBean.isSelected()) {						
//				ModifiableMetricBean newBean = cloneModifiableMetricBean(ikrDefinitionBean);
//				beans.add(newBean);
//				if (!selectedBeans.contains(newBean))
//					selectedBeans.add(newBean);
//			}
//		}
		
		if (visitor != null) {
			visitor.select(beans);
		}	
	}
	
	public void addAll(ActionEvent event) {
		Collection<ModifiableMetricBean> beans = new ArrayList<ModifiableMetricBean>();
		for(ModifiableMetricBean ikrDefinitionBean : selectionBeans) {
			ModifiableMetricBean newBean = cloneModifiableMetricBean(ikrDefinitionBean);
			beans.add(newBean);
			if (!selectedBeans.contains(newBean))
				selectedBeans.add(newBean);
		}
		
		if (visitor != null) {
			visitor.select(beans);
		}
	}
	
	// ---------------------- SELECTED LIST MANAGEMENT ----------------------
	
	private boolean isMatch(String str, String idx) {
		boolean ret = false;
		if(idx.indexOf(",")>0) {
			ret = true;
			String[] chars = idx.split(",");
			for(String tmp : chars) {
				ret = ret && str.contains(tmp);
			}
		}
		else if (idx.indexOf(":")>0) {
			String[] chars = idx.split(":");
			for(String tmp : chars) {
				ret = ret || str.contains(tmp);
			}
		} else {
			ret = str.contains(idx);
		}
		return ret;
	}
	
	public void filterSelection(ValueChangeEvent event) {
		filter = (String)event.getNewValue();
		changeMetricCategory(getMetricCategoryId());		
		Collection<ModifiableMetricBean> toFilter = new ArrayList<ModifiableMetricBean>();
		toFilter.addAll(selectionBeans);			
		if (filter!=null && filter.length()>0){
			selectionBeans.clear();
			for(ModifiableMetricBean ikrDefinition : toFilter){
				String instance = ((IkrDefinitionBean)ikrDefinition.getMetricGroupBean()).getFullIkrInstance();
				if(isMatch(instance.toLowerCase(), filter.toLowerCase()))
					selectionBeans.add(ikrDefinition);
			}
		}
	}
	
	public boolean isAllSelected() {
		return allSelected;
	}
	
	public void setAllSelected(boolean selected) {}	
	
	public void onChangeAllSelected(ValueChangeEvent evnt) {	
		this.allSelected = (Boolean)evnt.getNewValue();
	
		for (ModifiableMetricBean item : selectedBeans) {
			item.updateSelected(allSelected);
		}
	}
	
	public ArrayList<ModifiableMetricBean> getSelectedBeans() {
		return selectedBeans;
	}
	
	public void setSelectedBeans(ArrayList<ModifiableMetricBean> selectedBeans) {
		this.selectedBeans = selectedBeans;
	}
	
	public void removeAllItems(ActionEvent event) {
		Collection<ModifiableMetricBean> beans = new ArrayList<ModifiableMetricBean>();
		Iterator<ModifiableMetricBean> selectionIT = selectedBeans.iterator();
		while (selectionIT.hasNext()) {
			ModifiableMetricBean ikrMetricBean = selectionIT.next();
			beans.add(ikrMetricBean);
			selectionIT.remove();
		}	
		
		if (visitor != null) {
			visitor.deselect(beans);
		}	
		
		allSelected = false;
	}
	
	public void removeItem(ActionEvent event) {
		ModifiableMetricBean selectedMetric = (ModifiableMetricBean)event.getComponent().getAttributes().get("filesRecord");
		Collection<ModifiableMetricBean> beans = new ArrayList<ModifiableMetricBean>();
		Iterator<ModifiableMetricBean> selectionIT = selectedBeans.iterator();
		while (selectionIT.hasNext()) {
			ModifiableMetricBean ikrMetricBean = selectionIT.next();
			if (ikrMetricBean.equals(selectedMetric)) {
				beans.add(ikrMetricBean);
				selectionIT.remove();
				break;
			}
		}	
		
		if (visitor != null) {
			visitor.deselect(beans);
		}	
		
		allSelected = false;
	}

	public int getSelectedTableScrollWitdh() {
		return selectedTableScrollWitdh;
	}

	public void setSelectedTableScrollWitdh(int selectedTableScrollWitdh) {
		this.selectedTableScrollWitdh = selectedTableScrollWitdh;
	}

	public int getSelectedTableWitdh() {
		return selectedTableWitdh;
	}

	public void setSelectedTableWitdh(int selectedTableWitdh) {
		this.selectedTableWitdh = selectedTableWitdh;
	}

	public int getSelectedTableScrollHeight() {
		return selectedTableScrollHeight;
	}

	public void setSelectedTableScrollHeight(int selectedTableScrollHeight) {
		this.selectedTableScrollHeight = selectedTableScrollHeight;
	}

	public boolean isDashboardMode() {
		return dashboardMode;
	}

	public void setDashboardMode(boolean dashboardMode) {
		this.dashboardMode = dashboardMode;
	}

	@Override
	protected void filterMetricCategoryItems(Map<Integer, IkrStaticDomain> ikrStaticDomains, List<SelectItem> metricCategoryItems) {
		List<SelectItem> newMetricCategoryItems = new ArrayList<SelectItem>();
		if (monitorId>0) {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());	
			MetricDomainConfig config = monitorConfigs.get(monitorId).getMetricDomainConfig();
			for (MetricDomainConfigResource configResource : config.getResources()) {
				if (configResource.isEnabled()) {
					Map<String, IkrCategoryResource> categoryResources;
					try {
						categoryResources = dataModelPM.getIkrCategoryResources(configResource.getResource().getId());
						for (IkrCategoryResource categoryResource : categoryResources.values()) {
							IkrStaticDomain domain = ikrStaticDomains.get(categoryResource.getIkrStaticDomainId());
							newMetricCategoryItems.add(new SelectItem(domain.getId(),domain.getLabel()));
						}
					} catch (PersistenceException e) {
						logger.error(e.getMessage(), e);
					}
					
				}
			}
			metricCategoryItems.clear();
			metricCategoryItems.addAll(newMetricCategoryItems);
		}
		
	}

	public boolean isSearchON() {
		return searchON;
	}
	
	public String getSearchStyle() {
		String style = "font-size: 10px; font-weight: bold";
		if (!searchON)
			style = "font-size: 10px; font-weight: bold";
		return style;
	}
	
	public String getSearchONStyle() {
		return "font-size: 11px; font-weight: bold;";
	}
	
	public String getSearchOFFStyle() {
		return "font-size: 9px; font-weight: normal;";
	}
	

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}
	
	public void setPaginationVisible(boolean paginationVisible) {
		this.paginationVisible = paginationVisible;
	}
	
	public boolean isPaginationVisible() {
		if (selectionBeans.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}

	public String getEnvColumnName() {
		return envColumnName;
	}

	public String getContextColumnName() {
		return contextColumnName;
	}

	public String getCategoryColumnName() {
		return categoryColumnName;
	}

	public String getInstanceColumnName() {
		return instanceColumnName;
	}
	
	public String getDomainColumnName() {
		return domainColumnName;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public String getSortColumnName() {
		return sortColumnName;
	}

	public void setSortColumnName(String sortColumnName) {
		this.sortColumnName = sortColumnName;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}	
	
	public void setReInitSelected(boolean reInitSelected) {
		this.reInitSelected = reInitSelected;
	}

	public void handleSelectAll(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		for(ModifiableMetricBean selection : selectionBeans) {
			selection.updateSelected((Boolean)evt.getNewValue());
		}
	}

	public void setFromAlertDefinition(boolean fromAlertDefinition) {
		this.fromAlertDefinition = fromAlertDefinition;
	}
	
	public String getScrollWidthMetricSelector() {
		String scrollWidth = "590px;";
		if(fromAlertDefinition)
			return scrollWidth = "590px;";
		return scrollWidth;
	}
}