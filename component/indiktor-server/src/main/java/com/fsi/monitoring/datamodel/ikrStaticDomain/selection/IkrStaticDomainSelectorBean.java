package com.fsi.monitoring.datamodel.ikrStaticDomain.selection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.FacesUtils;

public class IkrStaticDomainSelectorBean
implements Serializable {
	
	private static final long serialVersionUID = -2220970006763460987L;
	
	private static final Logger logger = Logger.getLogger(IkrStaticDomainSelectorBean.class);	
	
	private IkrStaticDomain domainType = null;
	private IkrStaticDomain metricDomain = null;
	private IkrStaticDomain metricCategory = null;
	
	protected List<SelectItem> domainTypeItems = null;
	private List<SelectItem> metricDomainItems = null;
	private List<SelectItem> metricCategoryItems = null;
	
	private boolean excludeMetricDomainWithNoConfig = false;	
	private boolean acceptCrossComputeMetricCategory = false;
	private boolean acceptStaticDataMetricCategory = false;
	private boolean acceptCollectedMetricCategory = false;
	
	private IkrStaticDomainSelectorVisitor visitor;
	
	protected void filterMetricCategoryItems(Map<Integer, IkrStaticDomain> ikrStaticDomains, List<SelectItem> metricCategoryItems){};
	
	public void initComponent(boolean excludeMetricDomainWithNoConfig, boolean acceptCrossComputeMetricCategory, boolean acceptStaticDataMetricCategory, boolean acceptCollectedMetricCategory) {
		this.excludeMetricDomainWithNoConfig = excludeMetricDomainWithNoConfig;
		this.acceptCrossComputeMetricCategory = acceptCrossComputeMetricCategory;
		this.acceptStaticDataMetricCategory = acceptStaticDataMetricCategory;
		this.acceptCollectedMetricCategory = acceptCollectedMetricCategory;
		
		domainTypeItems = new ArrayList<SelectItem>();
		metricDomainItems = new ArrayList<SelectItem>();
		metricCategoryItems = new ArrayList<SelectItem>();		
	}	
	
	public void initItems() {
		changeDomainTypeItems();
	}
	
	public void initMetricCategoryId(int metricCategoryId) {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			IkrStaticDomain metricCategory = dataModelPM.getIkrStaticDomain(metricCategoryId);
			IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricCategory.getParentDomainId());
		    changeDomainType(metricDomain.getParentDomainId());
		    changeMetricDomain(metricDomain.getId());
		    changeMetricCategory(metricCategory.getId());
		    
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}		
	}
	
	public void initMetricDomainId(int metricDomainId) {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricDomainId);
		    changeDomainType(metricDomain.getParentDomainId());
		    changeMetricDomain(metricDomain.getId());
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}		
	}	
	
	public void accept(IkrStaticDomainSelectorVisitor visitor) {
		this.visitor = visitor;
	}
	
	public IkrStaticDomain getDomainType() {
		return domainType;
	}
	
	public IkrStaticDomain getMetricDomain() {
		return metricDomain;
	}		
	
	public IkrStaticDomain getMetricCategory() {
		return metricCategory;
	}
	
	public List<SelectItem> getDomainTypeItems() {
		Collections.sort(domainTypeItems, new Comparator<SelectItem>() {
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		return domainTypeItems;
	}
	
	public List<SelectItem> getMetricDomainItems() {
		Collections.sort(metricDomainItems, new Comparator<SelectItem>() {
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		return metricDomainItems;
	}	
	
	public List<SelectItem> getMetricCategoryItems() {
		Collections.sort(metricCategoryItems, new Comparator<SelectItem>() {
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}
		});
		return metricCategoryItems;
	}
	
	public int getDomainTypeId() {
		if (domainType == null) {
			return 0;
		}
		return domainType.getId();
	}
	
	public int getMetricDomainId() {
		if (metricDomain == null) {
			return 0;
		}
		return metricDomain.getId();
	}	
	
	public int getMetricCategoryId() {
		if (metricCategory == null) {
			return 0;
		}
		return metricCategory.getId();
	}		
	
	public void setDomainTypeId(int domainTypeId) {}
	public void setMetricDomainId(int metricDomainId) {}
	public void setMetricCategoryId(int metricCategoryId) {}	
	
	public void onChangeDomainType(ValueChangeEvent e) {
		int domainTypeId = (Integer)e.getNewValue();
		changeDomainType(domainTypeId);
	}
	
	public void onChangeMetricDomain(ValueChangeEvent e) {
		int metricDomainId = (Integer)e.getNewValue();
		changeMetricDomain(metricDomainId);
	}	
	
	public void onChangeMetricCategory(ValueChangeEvent e) {
		int metricCategoryId = (Integer)e.getNewValue();
		changeMetricCategory(metricCategoryId);
	}		
	
	protected void changeDomainType(int domainTypeId) {	
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			domainType = dataModelPM.getIkrStaticDomain(domainTypeId);

			changeMetricDomainItems();
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	protected void changeMetricDomain(int metricDomainId) {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			metricDomain = dataModelPM.getIkrStaticDomain(metricDomainId);

			changeMetricCategoryItems();
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}		
		if (visitor != null) {
			visitor.changeMetricDomain(metricDomainId);
		}
	}
	
	protected void changeMetricCategory(int metricCategoryId) {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			metricCategory = dataModelPM.getIkrStaticDomain(metricCategoryId);
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
    	
		if (visitor != null) {
			visitor.changeMetricGroup(metricCategoryId);
		}
	}
	
	private void changeMetricDomainItems() {
		int domainTypeId = domainType.getId();
		
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			
			List<IkrStaticDomain> ikrStaticDomains = dataModelPM.getIkrStaticDomains(domainTypeId);

			metricDomainItems = new ArrayList<SelectItem>();
			
		    for (IkrStaticDomain ikrStaticDomain : ikrStaticDomains) {	
		    	int metricDomainId = ikrStaticDomain.getId();		    	
		    	if (isValidMetricDomain(metricDomainId)) {
		    		if (!excludeMetricDomainWithNoConfig) {
		    			SelectItem item = new SelectItem(ikrStaticDomain.getId(),ikrStaticDomain.getLabel());
		    			metricDomainItems.add(item);
		    		}
		    		else {
		    			List<MetricDomainConfig> configs = dataModelPM.getMetricDomainConfigs(ikrStaticDomain.getId());
		    			if (configs != null && configs.size()>0) {
		    				SelectItem item = new SelectItem(ikrStaticDomain.getId(),ikrStaticDomain.getLabel());
			    			metricDomainItems.add(item);
		    			}
		    		}
		    	}
			}
		    
		    if (metricDomainItems.size()>0)
		    	changeMetricDomain((Integer)metricDomainItems.get(0).getValue());
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}		
	}
	
	protected void changeMetricCategoryItems() {
		int metricDomainId = metricDomain.getId();
		
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());			
			List<IkrStaticDomain> ikrStaticDomains = dataModelPM.getIkrStaticDomains(metricDomainId);
			metricCategoryItems = new ArrayList<SelectItem>();		
			Map<Integer, IkrStaticDomain> ikrStaticDomainMap = new HashMap<Integer, IkrStaticDomain>();
			for (IkrStaticDomain ikrStaticDomain : ikrStaticDomains) {	
		    	int metricCategoryId = ikrStaticDomain.getId();
		    	ikrStaticDomainMap.put(ikrStaticDomain.getId(), ikrStaticDomain);
		    	if (isValidMetricCategory(metricCategoryId)) {
		    		boolean alreadyAdded = false;
		    		SelectItem item = new SelectItem(ikrStaticDomain.getId(),ikrStaticDomain.getLabel());
		    		if (acceptCrossComputeMetricCategory) {
		    			if (ikrStaticDomain.getDomainValue().contains("COMPUTED - ")) {
		    				metricCategoryItems.add(item);
		    				alreadyAdded = true;
		    			}
		    		}
		    		
		    		if (acceptStaticDataMetricCategory) {
		    			if (ikrStaticDomain.getDomainValue().contains("STATIC DATA - ")) {
		    				metricCategoryItems.add(item);
		    				alreadyAdded = true;
		    			}
		    		}
		    		
		    		if (acceptCollectedMetricCategory) {
		    			if (!alreadyAdded)
		    				metricCategoryItems.add(item);
		    		}
		    	}
			}
		    
			if (metricCategoryItems.size()>0) {
				filterMetricCategoryItems(ikrStaticDomainMap, metricCategoryItems);
				changeMetricCategory((Integer)metricCategoryItems.get(0).getValue());
			}
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}		
	}	
	
	private void changeDomainTypeItems() {
		try {
			
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			List<IkrStaticDomain> ikrStaticDomains = dataModelPM.getIkrStaticDomains(0);
			
			Map<Integer, List<MetricDomainConfig>> metricDomainConfigs = dataModelPM.getMetricDomainConfigMap();
			domainTypeItems = new ArrayList<SelectItem>();
			
			for (IkrStaticDomain ikrStaticDomain : ikrStaticDomains) {		        	
		    	int domainTypeId = ikrStaticDomain.getId();
		    	if (!excludeMetricDomainWithNoConfig) {
			    	if (isValidDomainType(domainTypeId)) {
			    		SelectItem item = new SelectItem(ikrStaticDomain.getId(),ikrStaticDomain.getLabel());
			    		domainTypeItems.add(item);
			    	}
		    	}
		    	else {
		    		List<IkrStaticDomain> domainTypeChildren = dataModelPM.getIkrStaticDomains(domainTypeId);
		    		boolean exclude = true;
		    		for (IkrStaticDomain domain : domainTypeChildren) {
		    			List<MetricDomainConfig> configs = metricDomainConfigs.get(domain.getId());
		    			if (configs != null && configs.size()>0)
		    				exclude = false;
		    		}
		    		if (!exclude && isValidDomainType(domainTypeId)) {
		    			SelectItem item = new SelectItem(ikrStaticDomain.getId(),ikrStaticDomain.getLabel());
			    		domainTypeItems.add(item);
		    		}	    			
		    	}
			}
		    
			if (domainTypeItems.size()>0)
				changeDomainType((Integer)domainTypeItems.get(0).getValue());
			
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	public boolean isValidDomainType(int domainTypeId) {
		return true;
	}
	
	public boolean isValidMetricDomain(int metricDomainId) {
		return true;
	}
	
	public boolean isValidMetricCategory(int metricCategoryId) {
		return true;
	}		

	public boolean isAcceptCrossComputeMetricCategory() {
		return acceptCrossComputeMetricCategory;
	}

//	public void setOnlyCrossComputeMetricCategory(
//			boolean onlyCrossComputeMetricCategory) {
//		this.onlyCrossComputeMetricCategory = onlyCrossComputeMetricCategory;
//	}

	public boolean isExcludeMetricDomainWithNoConfig() {
		return excludeMetricDomainWithNoConfig;
	}

//	public void setExcludeMetricDomainWithNoConfig(
//			boolean excludeMetricDomainWithNoConfig) {
//		this.excludeMetricDomainWithNoConfig = excludeMetricDomainWithNoConfig;
//	}

	public boolean isAcceptStaticDataMetricCategory() {
		return acceptStaticDataMetricCategory;
	}

//	public void setOnlyStaticDataMetricCategory(boolean onlyStaticDataMetricCategory) {
//		this.onlyStaticDataMetricCategory = onlyStaticDataMetricCategory;
//	}	
	
	
}
