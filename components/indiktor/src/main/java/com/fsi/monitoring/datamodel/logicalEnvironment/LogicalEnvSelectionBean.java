package com.fsi.monitoring.datamodel.logicalEnvironment;

import java.io.Serializable;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.tree.AbstractTreeController;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.FacesUtils;

public class LogicalEnvSelectionBean
extends AbstractTreeController
implements Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	
	private static final Logger logger = Logger.getLogger(LogicalEnvSelectionBean.class);	

	private SelectItem[] logicalEnvItems = null;
	
	private LogicalEnv logicalEnv;
	
	private Map<Integer, LogicalEnv> logicalEnvMap;
	
	private LogicalEnvSelectionVisitor visitor;
	
	private boolean excludeSelectAll;
	
	public LogicalEnvSelectionBean(boolean excludeSelectAll) {
		this.excludeSelectAll = excludeSelectAll;
	}
	
	public void init() {
		initLogicalEnvItem();
	}
	
	public void initLogicalEnv(int logicalEnvId) {
		initLogicalEnvItem();
		changeLogicalEnv(logicalEnvId);
	}	
	
	
	private void initLogicalEnvItem() {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			logicalEnvMap = dataModelPM.getLogicalEnvs();
			
			int i=0;
			logicalEnvItems = null;
			if (excludeSelectAll) {
				logicalEnvItems = new SelectItem[logicalEnvMap.size()];
			}
			else {
				logicalEnvItems = new SelectItem[logicalEnvMap.size()+1];
				logicalEnvItems[i++] = new SelectItem(0,"ALL");
			}		
			
		    for (LogicalEnv logicalEnv : logicalEnvMap.values()) {		        	
		    	SelectItem item = new SelectItem(logicalEnv.getId(),logicalEnv.getName());
		    	logicalEnvItems[i++] = item;
			}
    	} catch(Exception exc) {
    		logger.error(exc);
    	}
    	
    	int lastLogicalEnvId = (Integer)logicalEnvItems[0].getValue();
//    	if (logicalEnv != null)
//    		lastLogicalEnvId = logicalEnv.getId();
    	changeLogicalEnv(lastLogicalEnvId);
	}

	public void accept(LogicalEnvSelectionVisitor visitor) {
		this.visitor = visitor;
	}	

	public void setLogicalEnvItems(SelectItem[] logicalEnvItems) {
		this.logicalEnvItems = logicalEnvItems;
	}
	
	public SelectItem[] getLogicalEnvItems() {
		return logicalEnvItems;
	}	
	
	public int getLogicalEnvId() {
		return (logicalEnv!=null)?logicalEnv.getId():0;
	}
	
	public String getLogicalEnvName() {
		return logicalEnv.getName();
	}
	
	public void setLogicalEnvId(int logicalEnvId) {}
	
	public void onChangeLogicalEnv(ValueChangeEvent e) {
		int logicalEnvId = (Integer)e.getNewValue();
		changeLogicalEnv(logicalEnvId);
	}		
	
	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}
	
	public LogicalEnv getLogicalEnv(int id) {
		return logicalEnvMap.get(id);
	}
	
	private void changeLogicalEnv(int logicalEnvId) {
		logicalEnv = logicalEnvMap.get(logicalEnvId);
		if (visitor != null) {
			visitor.changeLogicalEnv(logicalEnvId);
		}
	}
}
