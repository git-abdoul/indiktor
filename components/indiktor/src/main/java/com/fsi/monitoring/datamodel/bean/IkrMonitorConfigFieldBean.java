package com.fsi.monitoring.datamodel.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class IkrMonitorConfigFieldBean extends AccessControlBean {
	private static final Logger logger = Logger.getLogger(IkrMonitorConfigFieldBean.class);
	
	private MonitorConfigFieldItem currentField;
	private IkrStaticDomain domain;

	private List<MonitorConfigFieldItem> fields;

	private UICommand saveCommand = null;
	private UICommand removeCommand = null;
	private UICommand newFieldCommand = null;
	private UICommand newMonitorConfigCommand = null;
	private HtmlSelectOneMenu typeSelectionMenu = null;
	
	private HtmlInputText typeUI = null;

	public IkrMonitorConfigFieldBean() {
		saveCommand = new HtmlCommandButton();
		removeCommand = new HtmlCommandButton();
		newFieldCommand = new HtmlCommandButton();
		newMonitorConfigCommand = new HtmlCommandButton();
		typeSelectionMenu = new HtmlSelectOneMenu();
		typeSelectionMenu.setRendered(false);
		
		typeUI = new HtmlInputText();
		typeUI.setDisabled(true);
			
		setCurrentField(new MonitorConfigFieldItem(), false);			
	}
	
	
	public void updateConfigField(IkrStaticDomain domain) {
		if (!isAuthorized(1037, "ikrMonitorConfigField")) {
			return;
		}		
		
		this.domain = domain;		
		fields = new ArrayList<MonitorConfigFieldItem>();
		setCurrentField(new MonitorConfigFieldItem(), false);		
	}
	
	public MonitorConfigFieldItem getConfigField() {
		return currentField;
	}
	
	public void enabledChangeListener(ValueChangeEvent event) {
		 boolean enabled = (Boolean)event.getNewValue();
		 currentField.setEnabled(enabled);
	}

	private void setCurrentField(MonitorConfigFieldItem field, boolean newMonitor) {
		if (field.getId() == 0) {			
			saveCommand.setRendered(true);
			saveCommand.setValue("Add");
			removeCommand.setRendered(false);
			newFieldCommand.setRendered(false);
			if(!newMonitor){
				typeUI.setRendered(true);
				typeSelectionMenu.setRendered(false);
			}
		} else {
			saveCommand.setRendered(true);
			saveCommand.setValue("Update");
			removeCommand.setRendered(true);
			newFieldCommand.setRendered(true);
			typeUI.setRendered(true);
			typeSelectionMenu.setRendered(false);
		}
		currentField = field;
	}

	public List<MonitorConfigFieldItem> getConfigFields() {
		return fields;
	}

	public void rowSelectionListener(RowSelectorEvent event) {
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		MonitorConfigFieldItem field = fields.get(rowId);
		setCurrentField(field, false);
	}

	public void saveIkrMonitorConfigField(ActionEvent event) {	
		if (!isAuthorized(1037, "ikrMonitorConfigField")) {
			return;
		}		
		
		try {
			DataModelPM dataModelPM = (DataModelPM) FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			int id = currentField.getId();			
			if (id == 0) {
//				if (isFieldValid(currentField))
//					dataModelPM.addMetricDomainConfigField(currentField.getConfigField());
			} else {
//				dataModelPM.updateMetricDomainConfigField(currentField.getConfigField());
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			logger.error(exc.getMessage(), exc);
		}
		
		updateConfigField(domain);			
		setCurrentField(new MonitorConfigFieldItem(), false);
		
//		if(fields.size() < 2) {
//			IkrMonitorConfigFieldTreeController tree = (IkrMonitorConfigFieldTreeController)FacesUtils.getManagedBean("ikrMonitorConfigFieldController");
//			tree.setAgentTypeToExpand(domain.getDomainValue());
//			tree.init(event);
//		}
	}
	
	private boolean isFieldValid(MonitorConfigFieldItem configField) {
		boolean ret = true;
		if(configField.getFieldName()== null || configField.getFieldName().length()==0)
			ret = false;
		if(configField.getFieldLabel()== null || configField.getFieldLabel().length()==0)
			ret = false;
		return ret;
	}
	
	public void addNewIkrMonitorConfigField(ActionEvent event) {
		if (!isAuthorized(1037, "ikrMonitorConfigField")) {
			return;
		}		
		setCurrentField(new MonitorConfigFieldItem(), false);
	}
	
	public void addNewMonitorConfig(ActionEvent event) {
		if (!isAuthorized(1037, "ikrMonitorConfigField")) {
			return;
		}		
		typeUI.setRendered(false);
		typeSelectionMenu.setRendered(true);
		setCurrentField(new MonitorConfigFieldItem(), true);
	}
	
	public SelectItem[] getConfigTypes() {		
		SelectItem[] res = null;		
//		try{	
//			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			String agentType = domain.getDomainType();
//			if ("IKR_AGENT".equalsIgnoreCase(agentType)){
//				agentType = domain.getDomainValue();
//			}
//			List<IkrStaticDomain> monitorTypes = dataModelPM.getSupportedMonitorTypes(agentType);	
//			res = new SelectItem[monitorTypes.size()];
//			for (int i=0;i<monitorTypes.size();i++) {
//				IkrStaticDomain monitorType = monitorTypes.get(i);				
//				SelectItem item = new SelectItem(monitorType.getDomainValue());
//				res[i] = item;
//			}
//		} catch (Exception exc) {
//			exc.printStackTrace();
//			logger.error(exc.getMessage(), exc);
//		}		
		return res;
	}	 

	public void removeIkrMonitorConfigField(ActionEvent event) {
		if (!isAuthorized(1037, "ikrMonitorConfigField")) {
			return;
		}		
		try {
			 DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
//			 dataModelPM.deleteMetricDomainConfigField(currentField.getId());
		} catch(Exception exc) {
			exc.printStackTrace();
			logger.error(exc.getMessage(), exc);
		}
		
		updateConfigField(domain);			
		setCurrentField(new MonitorConfigFieldItem(), false);	
		
//		if(fields.size() == 0) {
//			IkrMonitorConfigFieldTreeController tree = (IkrMonitorConfigFieldTreeController)FacesUtils.getManagedBean("ikrMonitorConfigFieldController");
//			tree.setAgentTypeToExpand("");
//			tree.init(event);
//		}
	}

	public UICommand getSaveCommand() {
		return saveCommand;
	}

	public void setSaveCommand(UICommand saveCommand) {}

	public UICommand getRemoveCommand() {
		return removeCommand;
	}	

	public UICommand getNewFieldCommand() {
		return newFieldCommand;
	}

	public void setNewFieldCommand(UICommand newFieldCommand) {
		this.newFieldCommand = newFieldCommand;
	}

	public void setRemoveCommand(UICommand removeCommand) {}	

	public UICommand getNewMonitorConfigCommand() {
		return newMonitorConfigCommand;
	}
	public void setNewMonitorConfigCommand(UICommand newMonitorConfigCommand) {}

	public HtmlInputText getTypeUI() {
		return typeUI;
	}

	public void setTypeUI(HtmlInputText typeUI) {
		this.typeUI = typeUI;
	}

	public HtmlSelectOneMenu getTypeSelectionMenu() {
		return typeSelectionMenu;
	}

	public void setTypeSelectionMenu(HtmlSelectOneMenu typeSelectionMenu) {
		this.typeSelectionMenu = typeSelectionMenu;
	}
	
}
