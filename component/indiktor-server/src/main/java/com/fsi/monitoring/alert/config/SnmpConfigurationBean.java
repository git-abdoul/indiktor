package com.fsi.monitoring.alert.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.dao.impl.AbstractAlertDAO;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.snmp.SnmpConfig;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class SnmpConfigurationBean extends AccessControlBean {
	protected final static Logger logger = Logger.getLogger(AbstractAlertDAO.class);
	
	private AlertPM alertPM;
	
	private SnmpConfigRowBean rowConfig;	
	private List<SnmpConfigRowBean> configs;
	private List<SnmpConfigRowBean> configsSelected;

	private boolean rendererNewSNMPAlertConfig = false;	
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	private boolean portWrongFormat = false;
	private String portStyle = "width:250px;";
	private boolean specificTrapTypeWrongFormat = false;
	private String specificTrapTypeStyle = "width:250px;";
	
	int numberSNMPSelected = 0;

	private int version;
	private String name = "";
	private String community = "public";
	private String hostname = "";
	private String port = "";
	private String genericTrapType = "";
	private String specificTrapType = "";
	private String user = "";
	private String authProtocol = "";
	private String authPassword = "";
	private String privProtocol = "";
	private String privPassword = "";
	private String contextName = "";
	private String engineID = "";
	
	private UICommand saveCommand = null;
	private UICommand removeCommand = null;
	private UICommand addNewConfigCommand = null;
	
	public void init(ActionEvent action) {
		if (isAuthorized(110,"snmpConfig")) {	
			saveCommand = new HtmlCommandButton();
			removeCommand = new HtmlCommandButton();
			addNewConfigCommand = new HtmlCommandButton();
			initSnmpConfigs();	
		}
	} 
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	private void initSnmpConfigs() {
		alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
		try {
			configs = new ArrayList<SnmpConfigRowBean>();
			Collection<SnmpConfig> confs = alertPM.getSnmpConfigs();
			for(SnmpConfig conf : confs)
				configs.add(new SnmpConfigRowBean(conf));
		} catch (PersistenceException e) {
			logger.error(e);
		}		
		rowConfig = new SnmpConfigRowBean();
		configsSelected = new ArrayList<SnmpConfigRowBean>();
		selectAll = false;
//		updateButtons();
	}
	
	private void updateButtons() {
		if (rowConfig.getConfig().getId() == 0) {			
			saveCommand.setRendered(true);
			saveCommand.setValue("Add");
			removeCommand.setRendered(false);
			addNewConfigCommand.setRendered(false);
		} else {
			saveCommand.setRendered(true);
			saveCommand.setValue("Update");
			removeCommand.setRendered(true);
			addNewConfigCommand.setRendered(true);
		}
	}
	
	public void saveConfig(ActionEvent action) {
//		if (!isAuthorized(112, "snmpConfig")) {
//			return;
//		}	
		testFields();
		if(portWrongFormat || specificTrapTypeWrongFormat)
			return;
		
		
		if(name.trim() != null && name.trim().length() > 0) {
			updateBean();
			
			try {
				long id = rowConfig.getConfig().getId();			
				if (id == 0) {
					alertPM.createSnmpConfig(rowConfig.getConfig());
				} else {
					alertPM.updateSnmpConfig(rowConfig.getConfig());
				}
			} catch (Exception exc) {
				exc.printStackTrace();
				logger.error(exc.getMessage(), exc);
			}
			initSnmpConfigs();
			rendererNewSNMPAlertConfig = false;
		}
		else {
			return;
		}
		
	}
	
	public void testFields() {
		portWrongFormat = !StringUtils.isNumeric(port);
		if(portWrongFormat)
			portStyle = "width:250px; border:1px solid red;";
		else
			portStyle = "width:250px;";
		

		specificTrapTypeWrongFormat = !StringUtils.isNumeric(specificTrapType);
		if(specificTrapTypeWrongFormat)
			specificTrapTypeStyle = "width:250px; border:1px solid red;";
		else
			specificTrapTypeStyle = "width:250px;";
	}
	
	public void deleteConfig(ActionEvent action) {
		if (!isAuthorized(111, "snmpConfig")) {
			setAccessDenied();
			return;
		}
		SnmpConfigRowBean configSelected = (SnmpConfigRowBean)action.getComponent().getAttributes().get("field");
		if(configSelected != null) {
			long id = configSelected.getConfig().getId();
			if (id != 0){
				try {
					alertPM.deleteSnmpConfig(configSelected.getConfig().getId());
				} catch(Exception exc) {
					exc.printStackTrace();
					logger.error(exc.getMessage(), exc);
				}			
			}
			initSnmpConfigs();
		}
	}
	
	public void deleteSelectedConfigs(ActionEvent action) {
		if (!isAuthorized(111, "snmpConfig")) {
			setAccessDenied();
			return;
		}
		
		numberSNMPSelected = 0;
		for (SnmpConfigRowBean config : configs) {
			if (config.isSelected()){
				numberSNMPSelected++;
			}
		}
		
		if (numberSNMPSelected > 0) {
			for (SnmpConfigRowBean config : configs) {
				if (config.isSelected()){
					long id = config.getConfig().getId();
					if (id != 0){
						try {
							alertPM.deleteSnmpConfig(config.getConfig().getId());
						} catch(Exception exc) {
							exc.printStackTrace();
							logger.error(exc.getMessage(), exc);
						}			
					}
				}
			}
			initSnmpConfigs();
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No SNMP config has been selected");
		}		
	}
	
	public void addNewConfig(ActionEvent action) {
		if (!isAuthorized(114, "snmpConfig")) {
			setAccessDenied();
			return;
		}	
//		initSnmpConfigs();
		rowConfig = new SnmpConfigRowBean();
		rendererNewSNMPAlertConfig = true;
//		updateButtons();
		name = "";
		community = "public";
		hostname = "";
		port = "";
		genericTrapType = "";
		specificTrapType = "";
		user = "";
		authProtocol = "";
		authPassword = "";
		privProtocol = "";
		privPassword = "";
		contextName = "";
		engineID = "";
		portWrongFormat = false;
		specificTrapTypeWrongFormat = false;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {
		if (!isAuthorized(112, "snmpConfig")) {
			return;
		}	
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		rowConfig = configs.get(rowId);
//		updateButtons();
	}

	public SnmpConfigRowBean getRowConfig() {
		return rowConfig;
	}

	public boolean isSnmpV3() {
		boolean ret = false;
//		if(rowConfig.getConfig().getVersion() == 3)
		if(version == 3)
			ret = true;		
		return ret;
	}

	public List<SnmpConfigRowBean> getConfigs() {
		return configs;
	}	
	
	public SelectItem[] getVersions() {		
		SelectItem[] res = new SelectItem[3];	
		res[0] = new SelectItem(1);
		res[1] = new SelectItem(2);
		res[2] = new SelectItem(3);
		return res;
	}	 
	
	public SelectItem[] getAuthProtocols() {		
		SelectItem[] res = new SelectItem[2];	
		res[0] = new SelectItem("SHA");
		res[1] = new SelectItem("MD5");
		return res;
	}	 
	
	public SelectItem[] getPrivProtocols() {		
		SelectItem[] res = new SelectItem[5];	
		res[0] = new SelectItem("AES-128");
		res[1] = new SelectItem("AES-192");
		res[2] = new SelectItem("AES-256");
		res[3] = new SelectItem("3DES");
		res[4] = new SelectItem("DES");
		return res;
	}
	
	public SelectItem[] getGenericTrapTypes() {		
		SelectItem[] res = new SelectItem[7];	
		res[0] = new SelectItem("ColdStart");
		res[1] = new SelectItem("WarmStart");
		res[2] = new SelectItem("LinkDown");
		res[3] = new SelectItem("LinkUp");
		res[4] = new SelectItem("AuthenticationFailure");
		res[5] = new SelectItem("egpNeighborLoss");
		res[6] = new SelectItem("Enterprise");
		return res;
	}

	public UICommand getSaveCommand() {
		return saveCommand;
	}

	public void setSaveCommand(UICommand saveCommand) {
		this.saveCommand = saveCommand;
	}

	public UICommand getRemoveCommand() {
		return removeCommand;
	}

	public void setRemoveCommand(UICommand removeCommand) {
		this.removeCommand = removeCommand;
	}

	public UICommand getAddNewConfigCommand() {
		return addNewConfigCommand;
	}

	public void setAddNewConfigCommand(UICommand addNewConfigCommand) {
		this.addNewConfigCommand = addNewConfigCommand;
	}

	public boolean isRendererNewSNMPAlertConfig() {
		return rendererNewSNMPAlertConfig;
	}	
	
	public void setRendererNewSNMPAlertConfig(boolean rendererNewSNMPAlertConfig) {
		this.rendererNewSNMPAlertConfig = rendererNewSNMPAlertConfig;
	}

	public void openNewSNMPAlertConfigPopup(ActionEvent event) {
		if (!isAuthorized(112, "")) {
			setAccessDenied();
			return;
		}
		rowConfig = (SnmpConfigRowBean)event.getComponent().getAttributes().get("field");
		long id = rowConfig.getConfig().getId();
		if (id != 0){
			rendererNewSNMPAlertConfig = true;
			version = rowConfig.getConfig().getVersion();
			port = String.valueOf(rowConfig.getConfig().getPort());
			contextName = rowConfig.getConfig().getContextName();
			name = rowConfig.getConfig().getName();
			community = rowConfig.getConfig().getCommunity();
			hostname = rowConfig.getConfig().getHostname();
			genericTrapType = rowConfig.getGenericTrapType();
			specificTrapType = String.valueOf(rowConfig.getConfig().getSpecificTrapType());
			user = rowConfig.getConfig().getUser();
			authPassword = rowConfig.getConfig().getAuthPassword();
			authProtocol = rowConfig.getConfig().getAuthProtocol();
			privPassword = rowConfig.getConfig().getPrivPassword();
			privProtocol = rowConfig.getConfig().getPrivProtocol();
			engineID = rowConfig.getConfig().getEngineID();
		}
		
//		numberSNMPSelected = 0;
//		for (SnmpConfigRowBean config : configs) {
//			if (config.isSelected()){
//				numberSNMPSelected++;
//			}
//		}
//		
//		if (numberSNMPSelected < 2) {
//			if(numberSNMPSelected == 1) {
//				long id = rowConfig.getConfig().getId();
//				if (id != 0){
//					rendererNewSNMPAlertConfig = true;
//					version = rowConfig.getConfig().getVersion();
//				}
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No SNMP config has been selected");
//			}
//		}
//		
////		long id = rowConfig.getConfig().getId();
////		if (id != 0){
////			rendererNewSNMPAlertConfig = true;
////		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one snmp config to edit");
//		}
	}
	
	public void closeNewSNMPAlertConfigPopup(ActionEvent event) {
		rendererNewSNMPAlertConfig = false;
		portWrongFormat = false;
		specificTrapTypeWrongFormat = false;
		specificTrapTypeStyle = "width:250px;";
		portStyle = "width:250px;";
	}
	
	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}
	
	public boolean isPaginationVisible() {
		if (configs.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}
	
	public String getDeleteMessage() {
		numberSNMPSelected = 0;
		for (SnmpConfigRowBean config : configs) {
			if (config.isSelected()){
				numberSNMPSelected++;
			}
		}
		String message = "";
		if (numberSNMPSelected == 1) {
			for(SnmpConfigRowBean config : configs) {
				if(config.isSelected()) {
					message = "Are you sure to delete this SNMP alert configuration : " + config.getConfig().getName();
					break;
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberSNMPSelected + " SNMP alert configurations?";
			return message;
		}
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	//-------------------------------------------------------------------------------------------------------
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public void onChangedVersion(ValueChangeEvent event) {
		int newValue = (Integer) event.getNewValue();
		this.version = newValue;
	}

	public String getContextName() {
//		return rowConfig.getConfig().getContextName();
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getName() {
//		return rowConfig.getConfig().getName();
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommunity() {
//		return rowConfig.getConfig().getCommunity();
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getHostname() {
//		return rowConfig.getConfig().getHostname();
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
//		return rowConfig.getConfig().getPort();
		return port;
	}

	public void setPort(String port) {
		this.port = port;
		
//		if(port.length()!=0) {
//			portWrongFormat = !isInteger(port);
//			if(portWrongFormat)
//				portStyle = "width:250px; border:1px solid red;";
//			else
//				portStyle = "width:250px;";
//		}
	}
	
	private boolean isInteger (String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public String getGenericTrapType() {
//		return rowConfig.getGenericTrapType();
		return genericTrapType;
	}

	public void setGenericTrapType(String genericTrapType) {
		this.genericTrapType = genericTrapType;
	}

	public String getSpecificTrapType() {
//		return rowConfig.getConfig().getSpecificTrapType();
		return specificTrapType;
		
	}

	public void setSpecificTrapType(String specificTrapType) {
		this.specificTrapType = specificTrapType;
	}

	public String getUser() {
//		return rowConfig.getConfig().getUser();
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAuthProtocol() {
//		return rowConfig.getConfig().getAuthProtocol();
		return authProtocol;
	}

	public void setAuthProtocol(String authProtocol) {
		this.authProtocol = authProtocol;
	}

	public String getAuthPassword() {
//		return rowConfig.getConfig().getAuthPassword();
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getPrivProtocol() {
//		return rowConfig.getConfig().getPrivProtocol();
		return privProtocol;
	}

	public void setPrivProtocol(String privProtocol) {
		this.privProtocol = privProtocol;
	}

	public String getPrivPassword() {
//		return rowConfig.getConfig().getPrivPassword();
		return privPassword;
	}

	public void setPrivPassword(String privPassword) {
		this.privPassword = privPassword;
	}

	public String getEngineID() {
//		return rowConfig.getConfig().getEngineID();
		return engineID;
	}

	public void setEngineID(String engineID) {
		this.engineID = engineID;
	}
	
	private void updateBean() {
		rowConfig.getConfig().setAuthPassword(this.authPassword);
		rowConfig.getConfig().setAuthProtocol(this.authPassword);
		rowConfig.getConfig().setCommunity(this.community);
		rowConfig.getConfig().setContextName(this.contextName);
		rowConfig.getConfig().setEngineID(this.engineID);
		rowConfig.setGenericTrapType(this.genericTrapType);
		rowConfig.getConfig().setHostname(this.hostname);
		rowConfig.getConfig().setName(this.name);
		rowConfig.getConfig().setPort(Integer.parseInt(this.port));
		rowConfig.getConfig().setPrivPassword(this.privPassword);
		rowConfig.getConfig().setPrivProtocol(this.privProtocol);
		rowConfig.getConfig().setSpecificTrapType(Integer.parseInt(this.specificTrapType));
		rowConfig.getConfig().setUser(this.user);
		rowConfig.getConfig().setVersion(this.version);
	}
	
	public void handleSelectedConfig(ValueChangeEvent event) {
		SnmpConfigRowBean configSelected = (SnmpConfigRowBean)event.getComponent().getAttributes().get("field");
		if(configSelected != null) {
			for(SnmpConfigRowBean config : configs) {
				if(config.equals(configSelected)) {
					config.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						configsSelected.add(configSelected);
					else
						configsSelected.remove(configSelected);
				}
			}
		}
	}
	
	public void handleSelectAllConfigs(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		configsSelected.clear();
		for(SnmpConfigRowBean config : configs) {
			config.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				configsSelected.add(config);
		}
	}

	public int getConfigsSelected() {
		int size = configsSelected.size();
		return size;
	}

	public void setConfigsSelected(List<SnmpConfigRowBean> configsSelected) {
		this.configsSelected = configsSelected;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public boolean isPortWrongFormat() {
		return portWrongFormat;
	}

	public String getPortStyle() {
		return portStyle;
	}

	public boolean isSpecificTrapTypeWrongFormat() {
		return specificTrapTypeWrongFormat;
	}

	public String getSpecificTrapTypeStyle() {
		return specificTrapTypeStyle;
	}
	
	public boolean getListRendered() {
		return getConfigs().size() > 0;
	}
}
