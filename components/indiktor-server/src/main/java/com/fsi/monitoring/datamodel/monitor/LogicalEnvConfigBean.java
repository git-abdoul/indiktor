package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.admin.AdminBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class LogicalEnvConfigBean
extends AccessControlBean
implements Serializable{
	private static final long serialVersionUID = 2897131854144792279L;
	private final static Logger logger = Logger.getLogger(LogicalEnvConfigBean.class);		
	
	private LogicalEnvBean selectedLogicalEnv;	
	private List<LogicalEnvBean> logicalEnvBeans;
	
	private DataModelPM dataModelPM; 
	
	private String panelStack;
	
	private boolean renderedDeleteButton;
	private boolean nameMandatory = false;
	private boolean nameAlreadyExists = false;
	private String nameStyle = "font-size: 10px; width: 265px;";
	
	private AdminBean Temp = (AdminBean)FacesUtils.getManagedBean("adminBean");
	
	public void init(ActionEvent action) {
		dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		load();
		panelStack = "LOGICAL_ENV_LIST";
	}
	
	private void load() {
		try {
			logicalEnvBeans = new ArrayList<LogicalEnvBean>();
			List<LogicalEnv> logicalEnvs = new ArrayList<LogicalEnv>(dataModelPM.getLogicalEnvs().values());
			for(LogicalEnv env : logicalEnvs) {
				logicalEnvBeans.add(new LogicalEnvBean(env));
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void createNewLogicalEnv(ActionEvent action) {
		if (!isAuthorized(90,"")) {
			setAccessDenied();
			return;
		}
		else {
			selectedLogicalEnv = new LogicalEnvBean(new LogicalEnv(0, "", ""));
			panelStack = "LOGICAL_ENV_UPDATE";
		}	
	}	
	
	public void edit(ActionEvent action) {
		if (!isAuthorized(89,"")) {
			setAccessDenied();
			return;
		}
		LogicalEnvBean logicalEnvBean = (LogicalEnvBean)action.getComponent().getAttributes().get("logicalEnvBean");
		selectedLogicalEnv = logicalEnvBean;
		if ((action != null && isAuthorized(89,"")) || action == null) {
			if (selectedLogicalEnv != null) {
				panelStack = "LOGICAL_ENV_UPDATE";
			}
			else {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("No Logical Environment Selected");
			}				
		}	
	}	
	
	public void backToList(ActionEvent action) {
		panelStack = "LOGICAL_ENV_LIST";
		load();
		selectedLogicalEnv = null;
		nameMandatory = false;
		nameAlreadyExists = false;
		nameStyle = "font-size: 10px; width: 265px;";
	}	
	
	
	public LogicalEnvBean getSelectedLogicalEnv() {
		return selectedLogicalEnv;
	}	
	
	public List<LogicalEnvBean> getLogicalEnvBeans() {
		if (logicalEnvBeans == null) {
			return new ArrayList<LogicalEnvBean>();
		}
		
		Collections.sort(logicalEnvBeans, new Comparator<LogicalEnvBean>() {
			public int compare(LogicalEnvBean o1, LogicalEnvBean o2) {
				return o1.getLogicalEnv().getName().compareTo(o2.getLogicalEnv().getName());
			}
		});
		
		if(logicalEnvBeans.size() > 1)
			renderedDeleteButton = true;
		else
			renderedDeleteButton = false;
		
		return logicalEnvBeans;
	}	
	
	public void save(ActionEvent action) {    
		if (isAuthorized(89,"") || isAuthorized(90,"")) {
			if(selectedLogicalEnv.getLogicalEnv().getName().trim() == null 
					|| selectedLogicalEnv.getLogicalEnv().getName().trim().length() == 0) {
				nameMandatory = true;
				nameAlreadyExists = false;
				nameStyle = "font-size: 10px; width: 265px; border: 1px red solid;";
				return;
			}
			
			for(LogicalEnvBean logicalEnvBean : logicalEnvBeans) {
				if(logicalEnvBean.getLogicalEnv().getName().equalsIgnoreCase(selectedLogicalEnv.getLogicalEnv().getName())
						&& logicalEnvBean.getLogicalEnv().getId() != selectedLogicalEnv.getLogicalEnv().getId()) {
					nameMandatory = false;
					nameAlreadyExists = true;
					nameStyle = "font-size: 10px; width: 265px; border: 1px red solid;";
					return;
				}
			}
			
			try {				
				if (selectedLogicalEnv.getLogicalEnv().getId() == 0)				
					dataModelPM.createLogicalEnv(selectedLogicalEnv.getLogicalEnv());
				else
					dataModelPM.updateLogicalEnv(selectedLogicalEnv.getLogicalEnv());
				
				init(null);
			} catch (PersistenceException e) {
				logger.error("impossible to save logicalEnv", e);
			}
			
			selectedLogicalEnv = null;
			nameMandatory = false;
			nameAlreadyExists = false;
			nameStyle = "font-size: 10px; width: 265px;";
		}
	}
	
	public void delete(ActionEvent action) {   
		if (!isAuthorized(88,"")) {
			setAccessDenied();
			return;
		}
		else {
			if(logicalEnvBeans.size() > 1) {
				LogicalEnvBean logicalEnvBean = (LogicalEnvBean)action.getComponent().getAttributes().get("logicalEnvBean");
				selectedLogicalEnv = logicalEnvBean;
				if (selectedLogicalEnv != null) {
					try {					
						dataModelPM.deleteLogicalEnv(selectedLogicalEnv.getLogicalEnv().getId());
						
						init(null);
					} catch (PersistenceException e) {
						logger.error("impossible to delete logicalEnv", e);
					}
					selectedLogicalEnv = null;
				}
			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.init();
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No Logical Environment Selected");
//			}				
		}		
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		selectedLogicalEnv = logicalEnvBeans.get(rowId);
	}

	public String getPanelStack() {
		return panelStack;
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public boolean isRenderedDeleteButton() {
		return renderedDeleteButton;
	}

	public boolean isNameMandatory() {
		return nameMandatory;
	}

	public boolean isNameAlreadyExists() {
		return nameAlreadyExists;
	}

	public String getNameStyle() {
		return nameStyle;
	}

	public void setNameMandatory(boolean nameMandatory) {
		this.nameMandatory = nameMandatory;
	}

	public void setNameAlreadyExists(boolean nameAlreadyExists) {
		this.nameAlreadyExists = nameAlreadyExists;
	}

	public void setNameStyle(String nameStyle) {
		this.nameStyle = nameStyle;
	}
}
