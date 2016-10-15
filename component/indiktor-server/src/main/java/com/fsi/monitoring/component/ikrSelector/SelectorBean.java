package com.fsi.monitoring.component.ikrSelector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;


import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.FacesUtils;


public abstract class SelectorBean 
implements Serializable {
	
	private static final long serialVersionUID = -2220970006763460987L;

	private static final Logger logger = Logger.getLogger(SelectorBean.class);		
	
	protected String env;
	private SelectItem[] envItems;
	
	private String issueType;
	private SelectItem[] issueTypeItems;	
	
	protected String issueSubType;
	private SelectItem[] issueSubTypeItems;
	
	protected String issueCategoryGroup;
	private SelectItem[] issueCategoryGroupItems;	
	
	protected Map<Long, MonitorConfig> monitorConfigs = null;	
	
	protected MonitorConfig selectedMonitor = null;
	
	private boolean rendered;	
	protected boolean selected = false;	
	
	public void init() {
		initIssueTypeItems();
		initEnvItems();
		
		rendered = true;
	}	
	
	public String getEnv() {
		return env;
	}
	
	public void setEnv(String env) {}
	
	public SelectItem[] getEnvItems() {
		return envItems;
	}	
	
	public String getIssueType() {
		return issueType;
	}

	public String getIssueSubType() {
		return issueSubType;
	}	
	
	public void setIssueType(String issueType) {}
	
	public SelectItem[] getIssueTypeItems() {
		return issueTypeItems;
	}
	
	public SelectItem[] getIssueSubTypeItems() {
		return issueSubTypeItems;
	}		
	
	public SelectItem[] getIssueCategoryGroupItems() {
		return issueCategoryGroupItems;
	}	
	
	public void setIssueSubType(String issueSubType) {
		//this.issueSubType = issueSubType;
	}
	
	public String getIssueCategoryGroup() {
		return issueCategoryGroup;
	}	
	
	public void setIssueCategoryGroup(String issueCategoryGroup) {
		//this.issueCategoryGroup = issueCategoryGroup;
	}		
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {}	
	
	public void onChangeEnv(ValueChangeEvent evnt) {
		env = (String)evnt.getNewValue();
		
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			monitorConfigs = dataModelPM.getMonitorConfigs(env);
		} catch (Exception exc) {
			logger.error(exc);
		}

		changeIssueSubTypeItems();
	}
	
	public void onChangeIssueSubType(ValueChangeEvent evnt) {
		this.issueSubType = (String)evnt.getNewValue();
//		System.out.println("ONCHANGE ISSUE SUB TYPE EVENT : " + newIssueSubType);
		
		changeIssueCategoryGroupItems();
	}	

	public void onChangeIssueType(ValueChangeEvent evnt) {
		this.issueType = (String)evnt.getNewValue();
		
		changeIssueSubTypeItems();
	}
	
	private void initEnvItems() {
//		try {
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			Collection<String> envs = dataModelPM.getEnvironments();
//			envItems = new SelectItem[envs.size()];
//			
//			int i=0;
//			for (String env : envs) {
//				envItems[i++] = new SelectItem(env, env);
//			}
//			
//			env = (String)envItems[0].getValue();
//			
//			monitorConfigs = dataModelPM.getMonitorConfigs(env);
//			
//			changeIssueSubTypeItems();
//			
//		} catch (Exception exc) {
//			logger.error(exc);
//		}
	}	
	
	private void initIssueTypeItems() {
//		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//		List<IkrStaticDomain> agents = new ArrayList<IkrStaticDomain>();
//		try {
//			agents = dataModelPM.getIkrStaticDomains("IKR_AGENT");
//		} catch (PersistenceException e) {
//			Log.error(e.getMessage(), e);
//		}
//		
//		issueTypeItems = new SelectItem[agents.size()];
//		for(int i=0; i<agents.size(); i++) {
//			IkrStaticDomain domain = agents.get(i);
//			issueTypeItems[i] = new SelectItem(domain.getDomainValue(),domain.getLabel());
//		}
//		
//		issueType = (String)issueTypeItems[0].getValue();
	}	
	
	private void changeIssueSubTypeItems() {

//	    try {
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());		
//					
//			Map<String, String> tmpMap = new TreeMap<String, String>();
//			
//			List<IkrStaticDomain> supportedStaticDomains = dataModelPM.getSupportedMonitorTypes(issueType);
//				
//			if (supportedStaticDomains==null || supportedStaticDomains.size()==0) {
//				issueSubTypeItems = new SelectItem[1];
//				issueSubTypeItems[0] = new SelectItem("n/a","n/a");
//			}
//			
//			List<String> supportedMonitorTypes = new ArrayList<String>();
//			for(IkrStaticDomain domain:supportedStaticDomains) {
//				supportedMonitorTypes.add(domain.getDomainValue());
//			}
//			
//			int i=0;
//			for (MonitorConfig config : monitorConfigs.values()) {
//				if (supportedMonitorTypes.contains(config.getType())) {
//					String label = config.getStandardizedName();
//					tmpMap.put(label, config.getType()+":"+config.getId());
//				}
//			}			
//			
//			if (tmpMap.size() > 0) { 
//				issueSubTypeItems = new SelectItem[tmpMap.size()];
//				for (String key : tmpMap.keySet()) {
//					issueSubTypeItems[i++] = new SelectItem(tmpMap.get(key), key);
//				}
//			} else {
//				issueSubTypeItems = new SelectItem[1];
//				issueSubTypeItems[0] = new SelectItem("n/a","n/a");
//			}
//			
//			boolean reset = true;
//			for (SelectItem issueSubTypeItem : issueSubTypeItems) {
//				String itemValue = (String)issueSubTypeItem.getValue();
//				if (itemValue == issueSubType) {
//					reset = false;
//					break;
//				}
//			}
//			
//			if (reset) {
//				issueSubType = (String)issueSubTypeItems[0].getValue();
//			}			
//
//			changeIssueCategoryGroupItems();
//	    } catch(Exception exc) {
//	    	logger.error(exc);
//	    }	
	}	
	
	public void onChangeIssueCategoryGroup(ValueChangeEvent evnt) {
//		System.out.println("ONCHANGE CATGROUP EVENT");
		issueCategoryGroup = (String)evnt.getNewValue();

		changeIssueCategoryItems();
	}		
	
	private void changeIssueCategoryGroupItems() {
//	    System.out.println("INIT CATGROUP ITEMS");
//		try {
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			
//						
//			if (issueSubType == null) {
//				issueCategoryGroupItems = new SelectItem[1];
//				issueCategoryGroupItems[0] = new SelectItem("n/a","n/a");
//			} else {		
//				String[] tmp = issueSubType.split(":");
//				String monitorType = tmp[0];
//				List<IkrStaticDomain> supportedIkrCategoryGroup = dataModelPM.getSupportedIkrCategoryGroups(monitorType);
//				int i = 0;
//				if (supportedIkrCategoryGroup.size() > 0) { 
//					Map<String, String> labelCategoryGroups = new TreeMap<String, String>();
//					for(IkrStaticDomain cg: supportedIkrCategoryGroup) {
//						labelCategoryGroups.put(cg.getLabel(), cg.getDomainValue());
//					}
//					issueCategoryGroupItems = new SelectItem[labelCategoryGroups.size()];
//					for (String label:labelCategoryGroups.keySet()) {						
//						issueCategoryGroupItems[i++] = new SelectItem(labelCategoryGroups.get(label), label);
//					}
//				} else {
//					issueCategoryGroupItems = new SelectItem[1];
//					issueCategoryGroupItems[0] = new SelectItem("n/a","n/a");
//				}
//			}
//			
//			boolean reset = true;
//			for (SelectItem issueCategoryGroupItem : issueCategoryGroupItems) {
//				String itemValue = (String)issueCategoryGroupItem.getValue();
//				if (itemValue == issueCategoryGroup) {
//					reset = false;
//					break;
//				}
//			}
//			
//			if (reset) {
//				issueCategoryGroup = (String)issueCategoryGroupItems[0].getValue();
//			}			
//			
//			changeIssueCategoryItems();
//	    } catch(Exception exc) {
//	    	logger.error(exc);
//	    }	
	}	
	
	protected abstract void changeIssueCategoryItems();

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}		
	
}