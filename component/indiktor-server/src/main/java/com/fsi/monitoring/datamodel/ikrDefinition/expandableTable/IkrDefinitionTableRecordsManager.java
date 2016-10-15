package com.fsi.monitoring.datamodel.ikrDefinition.expandableTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.expandableTable.TableRecordBean;
import com.fsi.monitoring.component.expandableTable.TableRecordsManager;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrStaticDomainBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorBean;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;

public class IkrDefinitionTableRecordsManager 
extends TableRecordsManager {
	
	private static final long serialVersionUID = 6716195458835649271L;
	
	private static final Logger logger = Logger.getLogger(IkrDefinitionTableRecordsManager.class);	

	private Map<Integer, IkrDefinitionTableRecordBean> ikrCategoryRecords = null;
	
	private static RecordComparator recordComparator = new RecordComparator();	
	
	private IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean;
	
	private String instanceToCreate;
	
	private boolean rendererIkrDefinitionsManager = false;
	
	private MonitorConfig monitorConfig;
	
	public void edit() {
		rendererIkrDefinitionsManager = true;
	}
	
	public void update(MonitorConfig monitorConfig) {
		ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");
		this.monitorConfig = monitorConfig;
		ikrStaticDomainSelectorBean.initMetricDomainId(monitorConfig.getMetricDomainConfig().getIkrStaticDomainId());		
		init();
	}
	
	@Override
	protected void initTableData() {				
		instanceToCreate = null;
		List<IkrStaticDomain> ikrCategories = new ArrayList<IkrStaticDomain>();    	
    	Collection<AbstractIkrDefinition> ikrDefinitions = new ArrayList<AbstractIkrDefinition>();

    	try {
    		BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);	
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
			
//			MonitorConfigBean monitorConfigBean = beanPM.getMonitorConfigBean(monitorId);
			
			List<Integer> ikrCategorieIds = new ArrayList<Integer>();
			List<MetricDomainConfigResource> resources = monitorConfig.getMetricDomainConfig().getResources();
			for (MetricDomainConfigResource configResource : resources) {
				if (configResource.isEnabled()) {
					Map<String, IkrCategoryResource> categoryResources = dataModelPM.getIkrCategoryResources(configResource.getResource().getId());
					for (IkrCategoryResource categoryResource : categoryResources.values()) {
						ikrCategorieIds.add(categoryResource.getIkrStaticDomainId());
					}
				}
			}
			
			List<IkrStaticDomainBean> ikrCategorieBeans = beanPM.getIkrStaticDomainBeans(ikrCategorieIds);
			for(IkrStaticDomainBean domain : ikrCategorieBeans) {
				ikrCategories.add(domain.getIkrStaticDomain());
			}
			
			List<Long> ikrDefinitionIds = monitoringPM.getIkrDefinitionIds(monitorConfig.getId());
			List<IkrDefinitionBean> ikrDefinitionBeans = beanPM.getIkrDefinitionBeans(ikrDefinitionIds);
			for (IkrDefinitionBean bean : ikrDefinitionBeans) {
				ikrDefinitions.add(bean.getIkrDefinition());
			}
        	
     	} catch(Exception exc) {
    		logger.error(exc);
    	}
    	
    	ikrCategoryRecords = new HashMap<Integer, IkrDefinitionTableRecordBean>();

    	for (IkrStaticDomain metricCategory : ikrCategories) {
    		IkrCategory ikrCategory = (IkrCategory)metricCategory;
    		if (ikrCategory != null) {		
    			String domainValue = ikrCategory.getDomainValue();
    			if (!domainValue.contains("COMPUTED - ") && !domainValue.contains("STATIC DATA - ")) {
		    		IkrDefinitionTableRecordBean ikrCategoryRecord =
		    			new IkrDefinitionTableRecordBean(GROUP_INDENT_STYLE_CLASS,
		                            					 GROUP_ROW_STYLE_CLASS,
		                            					 styleBean,
		                            					 EXPAND_IMAGE,
		                            					 CONTRACT_IMAGE,
		                            					 recordBeans, 
		                            					 false);
		    			
		    		ikrCategoryRecord.setIkrCategory(ikrCategory);
		    		ikrCategoryRecord.setId(ikrCategory.getId());
		    		ikrCategoryRecords.put(ikrCategory.getId(), ikrCategoryRecord); 
    			}
    		}
    	}
    	
    	for (AbstractIkrDefinition ikrDefinition: ikrDefinitions) {
    		if (ikrDefinition != null)  {
	    		Integer ikrCategoryId = Integer.valueOf(ikrDefinition.getIkrCategoryId());
	    		
	    		IkrDefinitionTableRecordBean ikrCategoryRecord = ikrCategoryRecords.get(ikrCategoryId);
	    		if (ikrCategoryRecord != null) {
	    		
		    		IkrDefinitionTableRecordBean ikrDefinitionRecord =
		                	new IkrDefinitionTableRecordBean(CHILD_INDENT_STYLE_CLASS,
		                        							 CHILD_ROW_STYLE_CLASS,
		                        							 styleBean,
		                        							 SPACER_IMAGE);
		    		
		            ikrDefinitionRecord.setId(ikrDefinition.getId());
		            ikrDefinitionRecord.setIkrCategory(ikrCategoryRecord.getIkrCategory());
		            ikrDefinitionRecord.setIkrDefinition((IkrDefinition)ikrDefinition);
		            ikrDefinitionRecord.setMetricCompute(((IkrDefinition)ikrDefinition).getIkrCompute());
		            ikrDefinitionRecord.modifyEnable(ikrDefinition.isActivated());
		            ikrCategoryRecord.addChildRecord(ikrDefinitionRecord);
	    		}
    		}
    	}
    	
//    	Collections.sort(recordBeans,recordComparator);    	
		if (recordBeans != null && recordBeans.size()>0){
			Collections.sort(recordBeans, new Comparator<TableRecordBean>() {
				public int compare(TableRecordBean o1, TableRecordBean o2) {
					return ((IkrDefinitionTableRecordBean)o1).getName().compareTo(((IkrDefinitionTableRecordBean)o2).getName());
				}
			});
		}
    }	
	

	public String getInstanceToCreate() {
		return instanceToCreate;
	}

	public void setInstanceToCreate(String instanceToCreate) {
		this.instanceToCreate = instanceToCreate;
	}
	
	public List<SelectItem> getMetricCategoryItems() {		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
		List<MetricDomainConfigResource> resources = monitorConfig.getMetricDomainConfig().getResources();
		List<SelectItem> metricCategoryItems = new ArrayList<SelectItem>();
		for (MetricDomainConfigResource configResource : resources) {
			if (configResource.isEnabled()) {
				Map<String, IkrCategoryResource> categoryResources = null;
				try {
					categoryResources = dataModelPM.getIkrCategoryResources(configResource.getResource().getId());
					for (IkrCategoryResource categoryResource : categoryResources.values()) {
						IkrStaticDomainBean bean = beanPM.getIkrStaticDomainBean(categoryResource.getIkrStaticDomainId());
						metricCategoryItems.add(new SelectItem(bean.getIkrStaticDomain().getId(),bean.getIkrStaticDomain().getLabel()));    
					}
				} catch (PersistenceException e) {
					logger.error(e.getMessage(), e);
				}				
			}
		}
		
		Collections.sort(metricCategoryItems, new Comparator<SelectItem>() {
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		return metricCategoryItems;
	}
	
	public void createMetric(ActionEvent event) {
		if (!isAuthorized(99,"ikrDefinitionsUpdate")) {
			setAccessDenied();
			return;
		}
		
    	try {
    		MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
 
    		int ikrCategoryId = ikrStaticDomainSelectorBean.getMetricCategoryId();
    		IkrDefinitionTableRecordBean ikrCategoryRecord = ikrCategoryRecords.get(ikrCategoryId);
    		
    		IkrDefinition ikrDefinition = new IkrDefinition(0,monitorConfig.getId(),ikrCategoryId,instanceToCreate, MetricCompute.RT, false);    		
    		monitoringPM.createIkrDefinition(ikrDefinition);
    		
    		AbstractIkrDefinition ikrDefinitionCreated = monitoringPM.getIkrDefinition(monitorConfig.getId(), ikrCategoryId, instanceToCreate, MetricCompute.RT);
    		
    		IkrDefinitionTableRecordBean ikrDefinitionRecord =
        		new IkrDefinitionTableRecordBean(CHILD_INDENT_STYLE_CLASS,
                    							 CHILD_ROW_STYLE_CLASS,
                    							 styleBean,
                    							 SPACER_IMAGE);
	
		    ikrDefinitionRecord.setId(ikrDefinitionCreated.getId());
		    ikrDefinitionRecord.setIkrCategory(ikrCategoryRecord.getIkrCategory());
		    ikrDefinitionRecord.setIkrDefinition((IkrDefinition)ikrDefinitionCreated);
		    ikrDefinitionRecord.setMetricCompute(((IkrDefinition)ikrDefinitionCreated).getIkrCompute());
		    ikrDefinitionRecord.modifyEnable(ikrDefinitionCreated.isActivated());
		    ikrCategoryRecord.addChildRecord(ikrDefinitionRecord);
		    
	    	IkrDefinitionTableRecordBean toExpand = ikrCategoryRecords.get(ikrCategoryId);
	    	if (toExpand != null) {
	    		toExpand.toggleSubGroupAction(null);
	    	}  
    		
    	} catch(Exception exc) {
    		logger.error(exc);
    	}
	}
	
	public void createNewMetricCompute(ActionEvent event) {	
		if (!isAuthorized(98,"ikrDefinitionsUpdate")) {
			setAccessDenied();
			return;
		}
	
		String ikrInstance = (String)event.getComponent().getAttributes().get("ikrInstance");
		MetricCompute metricCompute = (MetricCompute)event.getComponent().getAttributes().get("compute");
		int ikrCategoryId = (Integer)event.getComponent().getAttributes().get("ikrCategoryId");	
		IkrDefinitionTableRecordBean ikrCategoryRecord = ikrCategoryRecords.get(ikrCategoryId);
		try {
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());			
			IkrDefinition ikrDefinition = new IkrDefinition(0,monitorConfig.getId(),ikrCategoryId,ikrInstance, metricCompute, false);			
			monitoringPM.createIkrDefinition(ikrDefinition);	
			if (ikrCategoryRecord != null) {		
	    		IkrDefinitionTableRecordBean ikrDefinitionRecord =
	                		new IkrDefinitionTableRecordBean(CHILD_INDENT_STYLE_CLASS,
		                        							 CHILD_ROW_STYLE_CLASS,
		                        							 styleBean,
		                        							 SPACER_IMAGE);
	    		
	            ikrDefinitionRecord.setId(ikrDefinition.getId());
	            ikrDefinitionRecord.setIkrCategory(ikrCategoryRecord.getIkrCategory());
	            ikrDefinitionRecord.setIkrDefinition((IkrDefinition)ikrDefinition);
	            ikrDefinitionRecord.setMetricCompute(((IkrDefinition)ikrDefinition).getIkrCompute());
	            ikrDefinitionRecord.modifyEnable(ikrDefinition.isActivated());
	            ikrCategoryRecord.addChildRecord(ikrDefinitionRecord);
			}	
		} catch (Exception exc) {
    		logger.error(exc);
		}		
	}

	public void validate(ActionEvent event) {		
		Collection<IkrDefinition> ikrDefinitions = new ArrayList<IkrDefinition>();
		
		for (IkrDefinitionTableRecordBean ikrCategoryTableRecordBean: ikrCategoryRecords.values()) {
			// ikrDefinitions 
			List<TableRecordBean> ikrDefinitionTableRecordBeans = ikrCategoryTableRecordBean.getChildFilesRecords();
			
			if (ikrDefinitionTableRecordBeans != null) {
				for (TableRecordBean ikrDefinitionTableRecordBean :ikrDefinitionTableRecordBeans) {
					IkrDefinitionTableRecordBean ikrDefinitionBean = (IkrDefinitionTableRecordBean)ikrDefinitionTableRecordBean;
					IkrDefinition ikrDefinition = ikrDefinitionBean.getIkrDefinition();
					ikrDefinition.setActivated(ikrDefinitionBean.getEnable());
					ikrDefinitions.add(ikrDefinition);
				}
			}
		}
		
    	try {
    		MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());	
    		monitoringPM.updateIkrDefinitions(ikrDefinitions);
    	} catch(Exception exc) {
    		logger.error(exc);
    	}
    	rendererIkrDefinitionsManager = false;		
	}	
	
	@Override
	public ArrayList<TableRecordBean> getFilesGroupRecordBeans() {
		ArrayList<TableRecordBean> records = super.getFilesGroupRecordBeans();
		return records;
	}

	private static class RecordComparator implements Comparator<TableRecordBean> {
		public int compare(TableRecordBean o1, TableRecordBean o2) {
			long id1 = ((IkrDefinitionTableRecordBean)o1).getId();
			long id2 = ((IkrDefinitionTableRecordBean)o2).getId();
			return (int)(id1 - id2);
		}	
	}
	
	public boolean isRendererIkrDefinitionsManager() {
		return rendererIkrDefinitionsManager;
	}
	
	public void closeIkrDefinitionsManagerPopup(ActionEvent event) {		
		rendererIkrDefinitionsManager = false;
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}
}
