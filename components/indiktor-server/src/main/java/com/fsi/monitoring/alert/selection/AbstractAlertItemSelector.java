package com.fsi.monitoring.alert.selection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.util.FacesUtils;

public abstract class AbstractAlertItemSelector<E>
implements LogicalEnvSelectionVisitor, Serializable {
	
	private static final long serialVersionUID = -733085150161285176L;
	
	protected final static Logger logger = Logger.getLogger(AbstractAlertItemSelector.class);		
	
	protected AlertPM alertPM;
	
	// groups by environment
	private Map<Integer, SelectItem[]> groupMap;	
	
	// domains  by environment-group
	private Map<String, SelectItem[]> domainMap;
	
	// subDomains  by environment-group-domain
	private Map<String, SelectItem[]> subDomainMap;
    
    private int group;
    private SelectItem[] groupItems;
    
    private int domain;
    private SelectItem[] domainItems;
    
    private int subDomain;
    private SelectItem[] subDomainItems;
    
    private String alertDefinitionNameFilter;

    // The reference list used to compute the displayedBeans list
	protected List<E> referenceBeans = null;	
    
    // The list sorted and filtered that will be displayed.
	protected List<E> displayedBeans = null;
	
    protected String active = "N/A";
    private static SelectItem[] activeItems;
    
    private LogicalEnvSelectionBean logicalEnvSelectionBean;
    
    private AlertSelectorItemVisitor visitor;
    
    static {
    	activeItems = new SelectItem[3];	
		activeItems[0] = new SelectItem("N/A", "N/A");
		activeItems[1] = new SelectItem("true", "TRUE");
		activeItems[2] = new SelectItem("false", "FALSE");
    }	
    
	public AbstractAlertItemSelector() {}
	
	public void init(AlertSelectorItemVisitor visitor) {
		this.visitor = visitor;
		alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
		
		groupMap = new HashMap<Integer, SelectItem[]>();
		domainMap = new HashMap<String, SelectItem[]>();
		subDomainMap = new HashMap<String, SelectItem[]>();
		
		alertDefinitionNameFilter = "";
		
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(false);
		logicalEnvSelectionBean.accept(this);
		logicalEnvSelectionBean.init();
		
		launchUpdateReferenceBeans();
	}

	protected abstract void updateReferenceBeans();
	protected abstract void filterReferenceBeans();
	
	public void launchUpdateReferenceBeans() {
		updateReferenceBeans();
		launchFilterReferenceBeans();
	}	
	
	private void launchFilterReferenceBeans() {
		filterReferenceBeans();
		visitor.displayBeansUpdated();
	}
	
	public List<E> getDisplayedBeans() {
		return displayedBeans;
	}

	public synchronized void filter(ActionEvent action) {
		launchFilterReferenceBeans();
	}	
	
	public boolean isDataEnable() {
		return displayedBeans.size() > 0;
	}	
	
	private void changeGroupItems() {
		try  {
			int logicalEnvId = logicalEnvSelectionBean.getLogicalEnvId();
			groupItems = groupMap.get(logicalEnvId);
			
			if (groupItems == null) {
				// first Load
				Map<Integer, String> groups = alertPM.getExistingAlertGroups(logicalEnvId);
				groupItems = new SelectItem[groups.size()+1];
			
				groupItems[0] = new SelectItem(0, "All Group");
				int i = 1;
				for (Map.Entry<Integer,String> group : groups.entrySet()) {
					groupItems[i++] = new SelectItem(group.getKey(), group.getValue());
				}
				
				groupMap.put(logicalEnvId, groupItems);
			}
		
			boolean reset = true;
			for (SelectItem groupItem : groupItems) {
				Integer itemValue = (Integer)groupItem.getValue();
				if (itemValue == group) {
					reset = false;
					break;
				}
			}
			
			if (reset) {
				group = (Integer)groupItems[0].getValue();
			}
			
			changeDomainItems();
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}
	
	private void changeDomainItems() {
		try {
			int logicalEnvId = logicalEnvSelectionBean.getLogicalEnvId();
			groupItems = groupMap.get(logicalEnvId);
			
			String key = logicalEnvId + "-" + group;
			domainItems = domainMap.get(key);
			
			if (domainItems == null) {
				// first Load
				Map<Integer, String> domains = alertPM.getExistingAlertDomains(logicalEnvId, group);
				domainItems = new SelectItem[domains.size()+1];
			
				domainItems[0] = new SelectItem(0, "All Domain");
				int i = 1;
				for (Map.Entry<Integer,String> domain : domains.entrySet()) {
					domainItems[i++] = new SelectItem(domain.getKey(), domain.getValue());
				}
				
				domainMap.put(key, domainItems);
			}
		
			boolean reset = true;
			for (SelectItem domainItem : domainItems) {
				Integer itemValue = (Integer)domainItem.getValue();
				if (itemValue == domain) {
					reset = false;
					break;
				}
			}
			
			if (reset) {
				domain = (Integer)domainItems[0].getValue();
			}		
			
		changeSubDomainItems();
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}
	
	private void changeSubDomainItems() {
		try {
			int logicalEnvId = logicalEnvSelectionBean.getLogicalEnvId();
			groupItems = groupMap.get(logicalEnvId);
			
			String key = logicalEnvId + "-" + group + "-" + domain;
			subDomainItems = subDomainMap.get(key);
			
			if (subDomainItems == null) {
				// first Load
				Map<Integer, String> subDomains = alertPM.getExistingAlertSubDomains(logicalEnvId, group, domain);
				subDomainItems = new SelectItem[subDomains.size()+1];
			
				subDomainItems[0] = new SelectItem(0, "All Sub-Domain");
				int i = 1;
				for (Map.Entry<Integer,String> subDomain : subDomains.entrySet()) {
					subDomainItems[i++] = new SelectItem(subDomain.getKey(), subDomain.getValue());
				}
				
				subDomainMap.put(key, subDomainItems);
			}
		
			boolean reset = true;
			for (SelectItem subDomainItem : subDomainItems) {
				Integer itemValue = (Integer)subDomainItem.getValue();
				if (itemValue == subDomain) {
					reset = false;
					break;
				}
			}
			
			if (reset) {
				subDomain = (Integer)subDomainItems[0].getValue();
			}
			
			filterReferenceBeans();
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}
	
	// ---- GET ITEMS ----
	public SelectItem[] getGroupItems() {
		return groupItems;
	}
	
	public SelectItem[] getDomainItems() {
		return domainItems;
	}	

	public SelectItem[] getSubDomainItems() {
		return subDomainItems;
	}		
	
	// ---- GET / SET ----	
	public int getGroup() {
		return group;
	}	
	
	public int getDomain() {
		return domain;
	}
	
	public int getSubDomain() {
		return subDomain;
	}		
	
	public String getAlertDefinitionNameFilter() {
		return alertDefinitionNameFilter;
	}
	
	public void setAlertDefinitionNameFilter(String alertDefinitionNameFilter) {
		this.alertDefinitionNameFilter = alertDefinitionNameFilter;
	}

	public void setEnvironment(String environment) {}	
	public void setGroup(int group) {}	
	public void setDomain(int domain) {}
	public void setSubDomain(int subdomain) {}

	
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public SelectItem[] getActiveItems() {
		return activeItems;
	}	
	
	// ----- ON CHANGE -----
	public void changeLogicalEnv(int logicalEnvId) {
		changeGroupItems();
	}	
	
	public void onChangeGroup(ValueChangeEvent evnt) {
		group = (Integer)evnt.getNewValue();
		changeDomainItems();
	}	
	
	public void onChangeDomain(ValueChangeEvent evnt) {
		domain = (Integer)evnt.getNewValue();		
		changeSubDomainItems();
	}
	
	public void onChangeSubDomain(ValueChangeEvent evnt) {
		subDomain = (Integer)evnt.getNewValue();
	}
	
	public boolean isValidAlertDefinition(AlertDefinition alertDefinition) {
		if (alertDefinition == null)
			return false;
		int logicalEnvId = logicalEnvSelectionBean.getLogicalEnvId();
		if (logicalEnvId !=0 && logicalEnvId != alertDefinition.getLogicalEnv()) {
			return false;
		} else if (group > 0 && alertDefinition.getGroup() != group) {
			return false;
		} else if (domain > 0 && alertDefinition.getDomain() != domain) {
			return false;
		} else if (subDomain > 0 && alertDefinition.getSubDomain() != subDomain) {
			return false;
		} else if (alertDefinitionNameFilter != null &&
				   alertDefinitionNameFilter.length()>0 &&
				   !alertDefinition.getName().toLowerCase().contains(alertDefinitionNameFilter.toLowerCase())) {
			return false;
		}
		return true;
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}	
	
}
