package com.fsi.monitoring.datamodel.ikrStaticDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfigField;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;


public class IkrStaticDomainCreateBean extends AccessControlBean {
	
	public static final short IKR_CATEGORY_LEVEL = 3;
	
	private static final Logger logger = Logger.getLogger(IkrStaticDomainCreateBean.class);
	
	private IkrStaticDomain domain;	
	private int domainLevel;	
	
	private String selectedPanel;
	private String selectedDomainPath;
	private String selectedDomainPathShort;
	
	private boolean addCommandRendered;
	private boolean removeCommandRendered;
	private String addCommandName;
	
	public IkrStaticDomainCreateBean() {}
	
	public void setDomain(IkrStaticDomain domain, int domainLevel) {
		this.domain = domain;
		this.domainLevel = domainLevel;
		
		if (domain.getId() == 0) {
			addCommandRendered = false;
			removeCommandRendered = false;
		} else {
			addCommandRendered = true;
			removeCommandRendered = true;
		}	
		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		
		try {
	
			switch(domainLevel) {
				case IKR_CATEGORY_LEVEL-2:
					addCommandName = "Add new metric domain";
					selectedDomainPath = domain.getDomainValue();
					selectedPanel = "none";
					break;
					
				case IKR_CATEGORY_LEVEL-1:
					addCommandName = "Add new metric category";				
					IkrStaticDomain parentDomain = dataModelPM.getIkrStaticDomain(domain.getParentDomainId());
					selectedDomainPath = parentDomain.getDomainValue() + " > " + domain.getDomainValue();
					selectedPanel = "metricDomain";
					break;
					
				case IKR_CATEGORY_LEVEL:
					addCommandRendered = false;
					selectedPanel = "metricCategory";
					parentDomain = dataModelPM.getIkrStaticDomain(domain.getParentDomainId());
					IkrStaticDomain grandParentDomain = dataModelPM.getIkrStaticDomain(parentDomain.getParentDomainId());
					selectedDomainPath = grandParentDomain.getDomainValue() + " > " + parentDomain.getDomainValue() + " > " + domain.getDomainValue();
					
					IkrCategoryConfigBean ikrCategoryConfigBean = (IkrCategoryConfigBean)FacesUtils.getManagedBean("ikrCategoryConfigBean");
					ikrCategoryConfigBean.setIkrCategory((IkrCategory)domain);
					
					break;
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	public IkrStaticDomain getDomain() {
		return domain;
	}
	
	public void navigate(ActionEvent action) {
		if (!isAuthorized(43,"ikrDomainConfig")){
			setAccessDenied();
			return;
		}
		IkrDomainTreeController ikrDomainController = (IkrDomainTreeController)FacesUtils.getManagedBean("ikrDomainController");
		ikrDomainController.initController(null);
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}
	
	public void addNewDomainType(ActionEvent event) {
		if (!isAuthorized(44, "ikrDomainConfig")) {
			setAccessDenied();
			return;
		}
		
		domainLevel = IKR_CATEGORY_LEVEL-3;
		
		setDomain(new IkrStaticDomain(0), ++domainLevel);
	}
	
	
	public void addNew(ActionEvent event) {
		if (!isAuthorized(44, "ikrDomainConfig")) {
			setAccessDenied();
			return;
		}
		
		IkrStaticDomain newIkrStaticDomain = null;
		
		if (domainLevel == (IKR_CATEGORY_LEVEL-1)) {
			newIkrStaticDomain = new IkrCategory(domain.getId());
		} else {
			newIkrStaticDomain = new IkrStaticDomain(domain.getId());
		}
		
		setDomain(newIkrStaticDomain, ++domainLevel);
	}
	
	public void saveStaticDomain(ActionEvent event) {		
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
			int id = domain.getId();	
			
			if (domain instanceof IkrCategory) {
				IkrCategoryConfigBean ikrCategoryConfigBean = (IkrCategoryConfigBean)FacesUtils.getManagedBean("ikrCategoryConfigBean");
				String indexeStr = ikrCategoryConfigBean.getSearchIndexes();
				List<String> searchIndexes = new ArrayList<String>();
				if (indexeStr!=null && indexeStr.length()>0) {
					String[] indexes = indexeStr.split(":");
					searchIndexes = Arrays.asList(indexes);
				}
				((IkrCategory)domain).setSearchesIndexes(searchIndexes);
			}
			if (id == 0) {
				if (!isAuthorized(44, "ikrDomainConfig")) {
					setAccessDenied();
					return;
				}
				if (domain.getDomainValue() != null && domain.getDomainValue().length()>0) {					
					int metricDomainId = dataModelPM.createIkrStaticDomain(domain);
					if (domainLevel == (IKR_CATEGORY_LEVEL-1)) {
						MetricDomainConfig config = new MetricDomainConfig();
						config.setIkrStaticDomainId(metricDomainId);
						config.setClassName("com.fsi.monitoring.kpi.monitor.sqlQuery.GenericSQLQueryCollector");
						config.setUseDataSynchronization(false);
						String[] connectorTypes = {"RDBMS"};
						config.setConnectorType(Arrays.asList(connectorTypes));
						config.setDescription("Generic SQL Query Collector from IndiKtor");						
						config.setDomainItemConfigs(new ArrayList<String>());						
						config.setFields(new ArrayList<MetricDomainConfigField>());	
						
						Map<String, String> attributes = new HashMap<String, String>();
						attributes.put("ON_THE_FLY", "true");
						config.setAttributes(attributes);
						
						List<MetricDomainConfigResource> resources = new ArrayList<MetricDomainConfigResource>();
						MetricDomainResource resource = dataModelPM.getMetricDomainResource(metricDomainId, "SQL_QUERY");
						int metricResourceId = 0;
						if (resource==null) {
							resource = new MetricDomainResource(0, metricDomainId, "SQL_QUERY");
							metricResourceId = dataModelPM.saveMetricDomainResource(resource); // Save Resources for Metric Domain
						}
						if ( metricResourceId == 0) {
							logger.error("An Error occured when trying to Insert Metric domain config for metric Domain <" + domain.getDomainValue() + ">");
							return;
						}
						resource.setId(metricResourceId);
						resources.add(new MetricDomainConfigResource(metricResourceId, resource, true));
						config.setResources(resources);	
						
						dataModelPM.addMetricDomainConfig(config);
					}
					IkrDomainTreeController tree = (IkrDomainTreeController)FacesUtils.getManagedBean("ikrDomainController");
					tree.initController(null);
				}
			} else {
				if (!isAuthorized(45, "ikrDomainConfig")) {
					setAccessDenied();
					return;
				}
				dataModelPM.updateIkrStaticDomain(domain);
				BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
				List<Long> ikrDefinitionIds = monitoringPM.getIkrDefinitionIds(domain.getId());
				beanPM.flushIkrDefinitionBeans(ikrDefinitionIds);					
				beanPM.flushIkrStaticDomainBean(domain.getId());				
			}
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}	
	
	public void removeStaticDomain(ActionEvent event) {	
		if (!isAuthorized(46, "ikrDomainConfig")) {
			setAccessDenied();
			return;
		}
		
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			dataModelPM.deleteIkrStaticDomain(domain.getId(), domainLevel);
			if (domainLevel==IKR_CATEGORY_LEVEL-1)
				dataModelPM.removeMetricDomainConfigByIkrStaticDomain(domain.getId());
			IkrDomainTreeController tree = (IkrDomainTreeController)FacesUtils.getManagedBean("ikrDomainController");
			tree.initController(null);
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}
	
	public String getSelectedPanel() {
		return selectedPanel;
	}

	public boolean isAddCommandRendered() {
		return addCommandRendered;
	}

	public boolean isRemoveCommandRendered() {
		return removeCommandRendered;
	}

	public String getAddCommandName() {
		return addCommandName;
	}

	public String getSelectedDomainPath() {
		return selectedDomainPath;
	}

	public String getSelectedDomainPathShort() {
		selectedDomainPathShort = selectedDomainPath;
		if(selectedDomainPathShort != null) {
			if(selectedDomainPathShort.length() > 65){
				selectedDomainPathShort = "";
				for (int i = 0; i < 65; i++) {
					selectedDomainPathShort = selectedDomainPathShort + selectedDomainPath.charAt(i);
				}
				selectedDomainPathShort = selectedDomainPathShort + "...";
			}
		}
		return selectedDomainPathShort;
	}

	public void setSelectedDomainPathShort(String selectedDomainPathShort) {
		this.selectedDomainPathShort = selectedDomainPathShort;
	}	
	
//	public void initialize(ActionEvent event) {
//		try {
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			dataModelPM.initIkrStaticDomains();
//			toggleModal(null);
//		} catch(Exception exc) {
//			logger.error(exc.getMessage(), exc);
//		}
//	}	
	
//	public void activeLinkListener(ValueChangeEvent event) {
//		if (!isAuthorized(1030, "adminHome")) {
//			return;
//		}
//		 activateLink = (Boolean)event.getNewValue();
//		 linkUI.setDisabled(!activateLink);
//	}
//	
//	public void toggleModal(ActionEvent event) {
//		if (!isAuthorized(1030, "adminHome")) {
//			return;
//		}
//		withConfirmation = !withConfirmation;
//    }
//
//	public UICommand getAddCommand() {
//		return addCommand;
//	}
//	
//	public UICommand getRemoveCommand() {
//		return removeCommand;
//	}	
//	
//	public UICommand getSaveDomainConfigCommand() {
//		return saveDomainConfigCommand;
//	}	
//
//	public UICommand getRemoveDomainConfigCommand() {
//		return removeDomainConfigCommand;
//	}	
//	
//	public HtmlInputText getDomainTypeUI() {
//		return domainTypeUI;
//	}
//
//	public void setDomainTypeUI(HtmlInputText domainTypeUI) {
//		this.domainTypeUI = domainTypeUI;
//	}
//
//	public HtmlInputText getDomainValueUI() {
//		return domainValueUI;
//	}
//
//	public void setDomainValueUI(HtmlInputText domainValueUI) {
//		this.domainValueUI = domainValueUI;
//	}
//
//	public void setSaveCommand(UICommand saveCommand) {}
//	
//	public void setAddCommand(UICommand saveCommand) {}
//	
//	public void setAddCrossCommand(UICommand saveCommand) {}	
//
//	public void setRemoveCommand(UICommand removeCommand) {}
//	
//	public void setSaveDomainConfigCommand(UICommand saveCommand) {}
//	
//	public void setAddDomainConfigCommand(UICommand saveCommand) {}
//
//	public void setRemoveDomainConfigCommand(UICommand removeCommand) {}	
//	
//	public boolean isWithConfirmation() {
//        return withConfirmation;
//    }
//
//    public void setWithConfirmation(boolean withConfirmation) {
//        this.withConfirmation = withConfirmation;
//    }
//
//	public HtmlCommandLink getLinkUI() {
//		return linkUI;
//	}
//
//	public void setLinkUI(HtmlCommandLink linkUI) {
//		this.linkUI = linkUI;
//	}
	

//	public boolean isDeleteConfirmation() {
//		return deleteConfirmation;
//	}
//
//	public String getDeleteDomainConfirm() {
//		return deleteDomainConfirm;
//	}	
}
