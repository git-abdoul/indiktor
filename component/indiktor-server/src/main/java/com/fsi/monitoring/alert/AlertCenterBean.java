package com.fsi.monitoring.alert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.parsing.NullSourceExtractor;

import com.fsi.monitoring.alert.bean.AlertModifierBean;
import com.fsi.monitoring.alert.config.AlertDefinitionModificationBean;
import com.fsi.monitoring.alert.config.AlertDefinitionSelectionBean;
import com.fsi.monitoring.alert.config.AllAlertDefinitionBean;
import com.fsi.monitoring.alert.manager.AlertManagerBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;

public class AlertCenterBean extends AccessControlBean implements Serializable {
	private static final long serialVersionUID = 5628819211604907863L;
	
	private static Map<String, String> menus = new HashMap<String, String>();
	
	private AlertDefinitionModificationBean alertDefinitionModificationBean;
	private AllAlertDefinitionBean allAlertDefinitionBean;

    private MetricSelectorBean metricSelectorBean;	
	
	private String selectedMenu;
	private String currentMenu;

	private boolean rendererAlertFilter = false;
	private boolean onEdit = false; 
	private String oldName;
	
	private boolean saveAnyway = false;
	private boolean saveNotActivated = false;
	
	private boolean nameMandatory = false;
	private String nameStyle = "width:275px;";
	private boolean coupleEnvNameExist = false;
	private String envStyle = "width: 275px;";
	private String envStyle2 = "";
	private boolean delayWrongFormat = false;
	private String delayStyle = "";
	
	int numberAlertDefinitionsSelected = 0;
	
	private boolean popUpNewAlert = false;
	
	private boolean enableOld, smsOld, mailOld, snmpOld;
	private String descriptionOld, howToOld;
	private long raisingDelayOld;
	private List<AlertCompute> alertComputes;
    private List<AlertCompute> alertComputesOld;
	
	static {
		menus.put("alertBoard", "Board");
		menus.put("alertDefinitionList", "Definition");
	}
	
	public void init(ActionEvent action) {
		if (!isAuthorized(20,"alertCenter")) {
			return;
		}
		
		alertDefinitionModificationBean = (AlertDefinitionModificationBean)FacesUtils.getManagedBean("alertDefinitionModificationBean");
		
		if (isAuthorized(21,"alertCenter")) {
			selectedMenu = "alertBoard";
			currentMenu = selectedMenu;			
			navigate(null);
		}
		else if (isAuthorized(20,"alertCenter")) {
			selectedMenu = "alertDefinitionList";
			currentMenu = selectedMenu;
			navigate(null);
		}
		else {
			return;
		}
		
		currentMenu = selectedMenu;
	}
	
	public void showAlertDefinition(ActionEvent action) {
		if (isAuthorized(25,"alertPreview")) {
			Long alertId = (Long) action.getComponent().getAttributes().get("alertDefinitionId");
			String actionBack = (String) action.getComponent().getAttributes().get("actionBack");
			
			alertDefinitionModificationBean.init(alertId, actionBack);			
			selectedMenu = action();
		}
	}
	
	public void showAlertDetails(ActionEvent action) {
		if (!isAuthorized(22,"alertDetail")) {
			setAccessDenied(currentMenu);
			return;
		}
		
		AlertModifierBean checkedAlert = (AlertModifierBean)action.getComponent().getAttributes().get("alert");
		AlertManagerBean alertManagerBean = (AlertManagerBean)FacesUtils.getManagedBean("alertManagerBean");
		
		alertManagerBean.setSelectedAlertBean(checkedAlert);
		selectedMenu = action();
		currentMenu = selectedMenu;
	}
	
//	public void showAlertDetails(ActionEvent action) {
//		if (!isAuthorized(22,"alertDetail")) {
//			setAccessDenied(currentMenu);
//			return;
//		}
//		
////		AlertModifierBean selectedAlertBean = null;
//		AlertManagerBean alertManagerBean = (AlertManagerBean)FacesUtils.getManagedBean("alertManagerBean");
//		Collection<AlertModifierBean> beans =  alertManagerBean.getAlertBeans();
//		
//		int alertSelected = 0;
//		for (AlertModifierBean alertBean : beans) {
//			if (alertBean.isSelected()) {
//				alertSelected++;
//			}			
//		}
//		if (alertSelected < 2) {
//			if (alertSelected == 1) {
//				for (AlertModifierBean alertBean : beans) {
//					if (alertBean.isSelected()) {
//						alertManagerBean.setSelectedAlertBean(alertBean);
////						selectedAlertBean = alertBean;
//						selectedMenu = action();
//						currentMenu = selectedMenu;
//						break;
//					}			
//				}
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No alert definition has been selected");
//			}	
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one alert definition to edit");
//		}
//	}
	
	public void saveAnyway(ActionEvent action) {
		saveAnyway = true;
		saveNotActivated = false;
		save(action);
	}
	
	public void resetControlStyle() {
		nameMandatory = coupleEnvNameExist = delayWrongFormat = saveNotActivated = false;
		if(alertDefinitionModificationBean != null) {
			alertDefinitionModificationBean.setComputationsError(false);
			alertDefinitionModificationBean.setConditionsError(false);
		}
		nameStyle = envStyle = "width:275px;";
		envStyle2 = "";
		delayStyle = "";
	}
	
	public void save(ActionEvent action) {
		resetControlStyle();
		if(alertDefinitionModificationBean.getAlertDefinition().getName().trim().length() > 0) {
			nameMandatory = false;
			nameStyle = "width:275px;";
		}
		else {
			nameMandatory = true;
			nameStyle = "width:275px; border:1px solid red;";
		}
		
		if (String.valueOf(alertDefinitionModificationBean.getRaisingDelay()).trim().length() == 0) {
			alertDefinitionModificationBean.getAlertDefinition().setRaisingDelay(0);
		}
		else if (!StringUtils.isNumeric(alertDefinitionModificationBean.getRaisingDelay())) {
			delayWrongFormat = true;
			delayStyle = "border:1px solid red;";
		}
		else {
			delayWrongFormat = false;
			delayStyle = "";
		}
		
		boolean alreadyExist = false;
		Collection<AlertDefinitionSelectionBean> alertDefinitionBeans = allAlertDefinitionBean.getAlertDefinitionBeans();
		String env = "", name = "", envTest = "", newName = "";
		long id = 0, idTest = 0;
		for(AlertDefinitionSelectionBean alertDefinitionBean : alertDefinitionBeans) {
			if (onEdit == true) {
				env = alertDefinitionBean.getAlertDefinitionBean().getLogicalEnv().getName();
				name = alertDefinitionBean.getAlertDefinitionBean().getAlertDefinition().getName();
				id = alertDefinitionBean.getAlertDefinitionBean().getAlertDefinition().getId();
				envTest = alertDefinitionModificationBean.getLogicalEnv().getName();
				newName = alertDefinitionModificationBean.getAlertDefinition().getName();
				idTest = alertDefinitionModificationBean.getAlertDefinition().getId();
				if(envTest.equalsIgnoreCase(env) && oldName.equalsIgnoreCase(newName) && idTest == id) {
					alreadyExist = true;
					break;
				}
				else if((envTest.equalsIgnoreCase(env) && newName.equalsIgnoreCase(name)) && idTest != id) {
					alreadyExist = true;
					break;
				}
			}
			else {
				env = alertDefinitionBean.getAlertDefinitionBean().getLogicalEnv().getName();
				name = alertDefinitionBean.getAlertDefinitionBean().getAlertDefinition().getName();
				envTest = alertDefinitionModificationBean.getLogicalEnvSelectionBean().getLogicalEnvName();
				newName = alertDefinitionModificationBean.getAlertDefinition().getName();
				if(envTest.equalsIgnoreCase(env) && newName.equalsIgnoreCase(name)) {
					alreadyExist = true;
					break;
				}
			}
		}
		
		if(alertDefinitionModificationBean != null && alertDefinitionModificationBean.getAlertConditionBeans().size() == 0) {
			alertDefinitionModificationBean.setConditionsError(true);
		}
		else {
			alertDefinitionModificationBean.setConditionsError(false);
		}
		
		if(alertDefinitionModificationBean != null) { 
			int i = 0;
			for(AlertCompute compute : alertDefinitionModificationBean.getAlertDefinition().getAlertComputes()) {
				if(compute.getCause() != null && compute.getCause().trim().length() != 0)
					i++;
			}
			if(i > 0)
				alertDefinitionModificationBean.setComputationsError(false);
			else
				alertDefinitionModificationBean.setComputationsError(true);
		}

		if(!alertDefinitionModificationBean.getAlertDefinition().isEnable() && !saveAnyway) {
			saveNotActivated = true;
		}
		
		if(saveNotActivated || nameMandatory || delayWrongFormat 
				|| alertDefinitionModificationBean.getComputationsError() 
						|| alertDefinitionModificationBean.getConditionsError()) {
			saveAnyway = false;
			return;
		}
		
		if(alreadyExist == true && onEdit == true && oldName.equalsIgnoreCase(alertDefinitionModificationBean.getAlertDefinition().getName())) {
			alertDefinitionModificationBean.save();
			alertDefinitionModificationBean.setFromAlertDefinition(false);
			popUpNewAlert = false;
			saveAnyway = false;
			saveNotActivated = false;
//			selectedMenu = alertDefinitionModificationBean.action();
			onEdit = false;			
		}
		else if(alreadyExist == false && onEdit == true) {
			alertDefinitionModificationBean.save();
			alertDefinitionModificationBean.setFromAlertDefinition(false);
			popUpNewAlert = false;
			saveAnyway = false;
			saveNotActivated = false;
//			selectedMenu = alertDefinitionModificationBean.action();
			onEdit = false;			
		}
		else if(alreadyExist == false && onEdit == false) {
			alertDefinitionModificationBean.save();
			alertDefinitionModificationBean.setFromAlertDefinition(false);
			popUpNewAlert = false;
			saveAnyway = false;
			saveNotActivated = false;
//			selectedMenu = alertDefinitionModificationBean.action();			
		}
		else {
			coupleEnvNameExist = true;
			saveAnyway = false;
			envStyle = nameStyle = "width:275px; border:1px solid red;";
			envStyle2 = "color: red;";
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("The couple Environment/Name already exists !!");
//			error.addMessage("Please, enter another couple Environment/Name");
		}
	}
	
	public void checkAlertValidity(ActionEvent action) {
		alertDefinitionModificationBean.checkAlertValidity();
		selectedMenu = alertDefinitionModificationBean.action();
	}	
	
	public void modifyAlertDef(ActionEvent action) {
		if (!isAuthorized(27,"alertDefinitionConfig")) {
			setAccessDenied(currentMenu);
			return;
		}
		long alertId = (Long) action.getComponent().getAttributes().get("alertDefinitionId");
		if (alertId != 0) {	
			onEdit = true;
			alertDefinitionModificationBean.init(alertId, "");	
			popUpNewAlert = true;
//			selectedMenu = this.action();	
//			currentMenu = selectedMenu;
			oldName = alertDefinitionModificationBean.getAlertDefinition().getName();
			descriptionOld = alertDefinitionModificationBean.getAlertDefinition().getDescription();
			howToOld = alertDefinitionModificationBean.getAlertDefinition().getHowTo();
			raisingDelayOld = alertDefinitionModificationBean.getAlertDefinition().getRaisingDelay();
			alertDefinitionModificationBean.feedValiditiesOld();
			feedAlertComputesOld();
			enableOld = alertDefinitionModificationBean.getAlertDefinition().isEnable();
			smsOld = alertDefinitionModificationBean.getAlertUserActionBean().isSms();
			mailOld = alertDefinitionModificationBean.getAlertUserActionBean().isMail();
			snmpOld = alertDefinitionModificationBean.getAlertSnmpActionBean().isSnmp();
		}
				
//		Collection<AlertDefinitionSelectionBean> alertDefinitionBeans = allAlertDefinitionBean.getAlertDefinitionBeans();
//		numberAlertDefinitionsSelected = 0;
//		for (AlertDefinitionSelectionBean alertDefinitionBean : alertDefinitionBeans) {
//			if (alertDefinitionBean.isSelected()){
//				numberAlertDefinitionsSelected++;
//			}
//		}
//		if (numberAlertDefinitionsSelected < 2) {
//			if (numberAlertDefinitionsSelected == 1) {
//				onEdit = true;
//				long alertId = allAlertDefinitionBean.getSelectedAlertDefinitionId();
//				if (alertId != 0) {	
//					alertDefinitionModificationBean.init(alertId, "");		
//					selectedMenu = this.action();	
//					currentMenu = selectedMenu;
//					oldName = allAlertDefinitionBean.getSelectedAlertDefinitionBean().getAlertDefinitionBean().getAlertDefinition().getName();
//					descriptionOld = alertDefinitionModificationBean.getAlertDefinition().getDescription();
//					howToOld = alertDefinitionModificationBean.getAlertDefinition().getHowTo();
//					raisingDelayOld = alertDefinitionModificationBean.getAlertDefinition().getRaisingDelay();
//					alertDefinitionModificationBean.feedValiditiesOld();
//					feedAlertComputesOld();
//					enableOld = alertDefinitionModificationBean.getAlertDefinition().isEnable();
//					smsOld = alertDefinitionModificationBean.getAlertUserActionBean().isSms();
//					mailOld = alertDefinitionModificationBean.getAlertUserActionBean().isMail();
//					snmpOld = alertDefinitionModificationBean.getAlertSnmpActionBean().isSnmp();
//				}
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No alert definition has been selected");
//			}	
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one alert definition to edit");			
//		}
	}
	
	public void deleteAlertDef(ActionEvent action) {
		if (!isAuthorized(28,"alertDefinitionList")) {
			setAccessDenied(currentMenu);
			return;
		}
		
		long alertId = (Long) action.getComponent().getAttributes().get("alertDefinitionId");
		if (alertId != 0) {	
			alertDefinitionModificationBean.init(alertId, "");	
			alertDefinitionModificationBean.delete(action);
			selectedMenu = this.action();
			allAlertDefinitionBean.handleDeleteNoSelection(alertId);
			allAlertDefinitionBean.setSelectedAlertDefinitionBean(null);
			allAlertDefinitionBean.setAlertDefinitionsSelected(new ArrayList<AlertDefinitionSelectionBean>());
		}
			
//		AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
//		Collection<AlertDefinitionSelectionBean> alertDefinitionBeans = allAlertDefinitionBean.getAlertDefinitionBeans();
//		numberAlertDefinitionsSelected = 0;
//		for (AlertDefinitionSelectionBean alertDefinitionBean : alertDefinitionBeans) {
//			if (alertDefinitionBean.isSelected()){
//				numberAlertDefinitionsSelected++;
//			}
//		}
//		if (numberAlertDefinitionsSelected > 0) {
//			for (AlertDefinitionSelectionBean alertDefinitionBean : alertDefinitionBeans) {
//				if (alertDefinitionBean.isSelected()) {
//					long alertId = alertDefinitionBean.getAlertDefinitionBean().getAlertDefinition().getId();
//					if (alertId != 0) {	
//						alertDefinitionModificationBean.init(alertId, "");	
//						alertDefinitionModificationBean.delete(action);	
//						alertDefinitionBean.setSelected(false);
//					}
//				}
//			}		
//			selectedMenu = this.action();
//			allAlertDefinitionBean.setSelectedAlertDefinitionBean(null);
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("No alert definition has been selected");
//		}
	}
	
	public void deleteSelectedAlertDef(ActionEvent action) {
		if (!isAuthorized(28,"alertDefinitionList")) {
			setAccessDenied(currentMenu);
			return;
		}
			
		AllAlertDefinitionBean allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		Collection<AlertDefinitionSelectionBean> alertDefinitionBeans = allAlertDefinitionBean.getAlertDefinitionBeans();
		numberAlertDefinitionsSelected = 0;
		for (AlertDefinitionSelectionBean alertDefinitionBean : alertDefinitionBeans) {
			if (alertDefinitionBean.isSelected()){
				numberAlertDefinitionsSelected++;
			}
		}
		if (numberAlertDefinitionsSelected > 0) {
			for (AlertDefinitionSelectionBean alertDefinitionBean : alertDefinitionBeans) {
				if (alertDefinitionBean.isSelected()) {
					long alertId = alertDefinitionBean.getAlertDefinitionBean().getAlertDefinition().getId();
					if (alertId != 0) {	
						alertDefinitionModificationBean.init(alertId, "");	
						alertDefinitionModificationBean.delete(action);	
						alertDefinitionBean.setSelected(false);
					}
				}
			}		
			selectedMenu = this.action();
			allAlertDefinitionBean.setSelectedAlertDefinitionBean(null);
			allAlertDefinitionBean.setAlertDefinitionsSelected(new ArrayList<AlertDefinitionSelectionBean>());
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No alert definition has been selected");
		}
	}
	
	public void navigate(ActionEvent action) {		
		boolean isNewAlertDef = true;
		if (action != null) {
			selectedMenu = (String)action.getComponent().getAttributes().get("to");
			String isNew = (String)action.getComponent().getAttributes().get("isNew");
			if (isNew!= null && isNew.length()>0)
				isNewAlertDef =  Boolean.valueOf(isNew);
		}
		
		if ("alertBoard".equals(selectedMenu)) {
			AlertManagerBean alertManagerBean = (AlertManagerBean)FacesUtils.getManagedBean("alertManagerBean");
			if (!isAuthorized(21,"alertCenter")) {			
				setAccessDenied(currentMenu);
				return;
			}	
			alertManagerBean.init(null);
		}
		else if ("alertDefinitionConfig".equals(selectedMenu)) {
			if (isNewAlertDef) {
				if (!isAuthorized(26,"alertCenter")) {
					currentMenu = "alertDefinitionList";
					setAccessDenied(currentMenu);
					return;
				}
				alertDefinitionModificationBean.initCreate(null);
				alertDefinitionModificationBean.getAlertDefinition().setName("");
			}
		}
		else {
			allAlertDefinitionBean = (AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
			if (!isAuthorized(24,"alertCenter")) {			
				setAccessDenied(currentMenu);
				return;
			}
			allAlertDefinitionBean.init(null);
		}
		
		if(onEdit) {
			alertDefinitionModificationBean.getAlertDefinition().setEnable(enableOld);
			alertDefinitionModificationBean.getAlertDefinition().setDescription(descriptionOld);
			alertDefinitionModificationBean.getAlertDefinition().setHowTo(howToOld);
			alertDefinitionModificationBean.getAlertDefinition().setRaisingDelay(raisingDelayOld);
			List<AlertValidity> validities = alertDefinitionModificationBean.getValidities();
			List<AlertValidity> validitiesOld = alertDefinitionModificationBean.getValiditiesOld();
			alertDefinitionModificationBean.removeAllAlertValidity(action);
			for(AlertValidity valOld : validitiesOld) {
				alertDefinitionModificationBean.getValidities().add(valOld);
			}
			alertComputes = alertDefinitionModificationBean.getAlertDefinition().getAlertComputes();
			alertComputes.clear();
			for(AlertCompute alertComputeBis : alertComputesOld) {
				alertDefinitionModificationBean.getAlertDefinition().getAlertComputes().add(alertComputeBis);
			}
			alertDefinitionModificationBean.getAlertUserActionBean().setSms(smsOld);
			alertDefinitionModificationBean.getAlertUserActionBean().setMail(mailOld);
			alertDefinitionModificationBean.getAlertSnmpActionBean().setSnmp(snmpOld);
		}
		
		currentMenu = selectedMenu;
		if (alertDefinitionModificationBean.getAlertDefinition() != null && onEdit == true) {
			alertDefinitionModificationBean.getAlertDefinition().setName(oldName);
		}
		onEdit = false;
	}
	
	private void setAccessDenied(String currentMenu) {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
		selectedMenu = currentMenu;
	}

	public Map<String, String> getMenus() {
		return menus;
	}

	public String getSelectedMenu() {
		return selectedMenu;
	}
	
	public String getStyle() {
		return "font-weight:bold; color: #ff774f;";
	}
	
	public boolean isRendererAlertFilter() {
		return rendererAlertFilter;
	}
	
	public void setRendererAlertFilter(boolean rendererAlertFilter) {
		this.rendererAlertFilter = rendererAlertFilter;
	}

	public void openAlertFilterPopup(ActionEvent event) {
		rendererAlertFilter = true;
	}
	
	public void closeAlertFilterPopup(ActionEvent event) {
		rendererAlertFilter = false;
	}	

	public void feedAlertComputesOld() {
		alertComputesOld = new ArrayList<AlertCompute>();
		alertComputes = alertDefinitionModificationBean.getAlertDefinition().getAlertComputes();
		for(AlertCompute alertCompute : alertComputes) {
			alertComputesOld.add(new AlertCompute(alertCompute.getWorkflow(), alertCompute.getLabel(), alertCompute.getCause(), alertCompute.isEnable()));
		}
	}

	public List<AlertCompute> getAlertComputesOld() {
		return alertComputesOld;
	}

	public boolean isPopUpNewAlert() {
		return popUpNewAlert;
	}
	
	public void openNewAlertPopUp(ActionEvent action) {
		popUpNewAlert = true;
		alertDefinitionModificationBean.initCreate(null);
		alertDefinitionModificationBean.getAlertDefinition().setName("");
		alertDefinitionModificationBean.setFromAlertDefinition(true);
	}
	
	public void closeNewAlertPopUp(ActionEvent action) {
		popUpNewAlert = false;
		alertDefinitionModificationBean.setFromAlertDefinition(false);
		saveAnyway = false;
		saveNotActivated = false;
		
		if(onEdit) {
			alertDefinitionModificationBean.getAlertDefinition().setEnable(enableOld);
			alertDefinitionModificationBean.getAlertDefinition().setDescription(descriptionOld);
			alertDefinitionModificationBean.getAlertDefinition().setHowTo(howToOld);
			alertDefinitionModificationBean.getAlertDefinition().setRaisingDelay(raisingDelayOld);
			List<AlertValidity> validities = alertDefinitionModificationBean.getValidities();
			List<AlertValidity> validitiesOld = alertDefinitionModificationBean.getValiditiesOld();
			alertDefinitionModificationBean.removeAllAlertValidity(action);
			for(AlertValidity valOld : validitiesOld) {
				alertDefinitionModificationBean.getValidities().add(valOld);
			}
			alertComputes = alertDefinitionModificationBean.getAlertDefinition().getAlertComputes();
			alertComputes.clear();
			for(AlertCompute alertComputeBis : alertComputesOld) {
				alertDefinitionModificationBean.getAlertDefinition().getAlertComputes().add(alertComputeBis);
			}
			alertDefinitionModificationBean.getAlertUserActionBean().setSms(smsOld);
			alertDefinitionModificationBean.getAlertUserActionBean().setMail(mailOld);
			alertDefinitionModificationBean.getAlertSnmpActionBean().setSnmp(snmpOld);
		}
		
		if (alertDefinitionModificationBean.getAlertDefinition() != null && onEdit == true) {
			alertDefinitionModificationBean.getAlertDefinition().setName(oldName);
		}
		onEdit = false;
		resetControlStyle();
	}

	public boolean isSaveAnyway() {
		return saveAnyway;
	}

	public void setSaveAnyway(boolean saveAnyway) {
		this.saveAnyway = saveAnyway;
	}

	public boolean isSaveNotActivated() {
		return saveNotActivated;
	}

	public void setSaveNotActivated(boolean saveNotActivated) {
		this.saveNotActivated = saveNotActivated;
	}

	//-------------Control and style---------------//

	public boolean isNameMandatory() {
		return nameMandatory;
	}

	public String getNameStyle() {
		return nameStyle;
	}

	public boolean isCoupleEnvNameExist() {
		return coupleEnvNameExist;
	}

	public boolean isDelayWrongFormat() {
		return delayWrongFormat;
	}

	public String getDelayStyle() {
		return delayStyle;
	}

	public String getEnvStyle() {
		return envStyle;
	}

	public String getEnvStyle2() {
		return envStyle2;
	}

	public String getTabPropertiesStyle() {
		if(saveAnyway || saveNotActivated || nameMandatory || coupleEnvNameExist || delayWrongFormat)
			return "font-size: 10px; color: red;";
		else
			return "font-size: 10px;";
	}

	public String getTabCCStyle() {
		if(alertDefinitionModificationBean.getConditionsError() || alertDefinitionModificationBean.getComputationsError())
			return "font-size: 10px; color: red;";
		else
			return "font-size: 10px;";
	}
}
