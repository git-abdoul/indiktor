package com.fsi.monitoring.datamodel.jobScheduler.selection;

import java.io.Serializable;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.tree.AbstractTreeController;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;
import com.fsi.monitoring.util.FacesUtils;

public class JobSchedulerStaticDomainSelectionBean
extends AbstractTreeController
implements Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	
	private static final Logger logger = Logger.getLogger(JobSchedulerStaticDomainSelectionBean.class);	

	private SelectItem[] taskStaticDomainItems = null;
	
	private IkrJobSchedulerStaticDomain taskStaticDomain;
	
	private Map<Integer, IkrJobSchedulerStaticDomain> taskStaticDomainMap;
	
	private JobSchedulerStaticDomainSelectorVisitor visitor;
	
	public JobSchedulerStaticDomainSelectionBean() {}
	
	public void init() {
		initTaskStaticDomainItem();
	}
	
	public void initTaskStaticDomain(int taskStaticDomainId) {
		initTaskStaticDomainItem();
		changeTaskStaticDomain(taskStaticDomainId);
	}	
	
	
	private void initTaskStaticDomainItem() {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			taskStaticDomainMap = dataModelPM.getJobSchedulerStaticDomains();
			
			int i=0;
			taskStaticDomainItems = new SelectItem[taskStaticDomainMap.size()];
		    for (IkrJobSchedulerStaticDomain taskStaticDomain : taskStaticDomainMap.values()) {		        	
		    	SelectItem item = new SelectItem(taskStaticDomain.getId(),taskStaticDomain.getName());
		    	taskStaticDomainItems[i++] = item;
			}
    	} catch(Exception exc) {
    		logger.error(exc);
    	}
    	
    	changeTaskStaticDomain((Integer)taskStaticDomainItems[0].getValue());
	}

	public void accept(JobSchedulerStaticDomainSelectorVisitor visitor) {
		this.visitor = visitor;
	}	

	public void setTaskStaticDomainItems(SelectItem[] taskStaticDomainItems) {
		this.taskStaticDomainItems = taskStaticDomainItems;
	}
	
	public SelectItem[] getTaskStaticDomainItems() {
		return taskStaticDomainItems;
	}	
	
	public int getTaskStaticDomainId() {
		return taskStaticDomain.getId();
	}
	
	public void setTaskStaticDomainId(int taskStaticDomainId) {}
	
	public void onChangeTaskStaticDomain(ValueChangeEvent e) {
		int taskStaticDomainId = (Integer)e.getNewValue();
		changeTaskStaticDomain(taskStaticDomainId);
	}		
	
	public IkrJobSchedulerStaticDomain getTaskStaticDomain() {
		return taskStaticDomain;
	}
	
	private void changeTaskStaticDomain(int taskStaticDomainId) {
		taskStaticDomain = taskStaticDomainMap.get(taskStaticDomainId);
		if (visitor != null) {
			visitor.changeTaskStaticDomain(taskStaticDomainId);
		}
	}
}
