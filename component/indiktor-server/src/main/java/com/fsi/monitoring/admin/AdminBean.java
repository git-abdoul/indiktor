package com.fsi.monitoring.admin;

import javax.faces.event.ActionEvent;

import com.fsi.monitoring.datamodel.monitor.LogicalEnvConfigBean;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;

public class AdminBean 
extends AccessControlBean {
	
	private boolean dashBoardMenuVisible = false;
	private boolean dataCollectionMenuVisible = false;
	private boolean securityMenuVisible = false;
	private boolean thirdPartyMenuVisible = false;
	private boolean extensionsMenuVisible = false;
	
	private boolean rendererLogicalEnv = false;
	
	private boolean rendererFilter = false;
	
	LogicalEnvConfigBean logicalEnvConfigBean;
	
	public void init(ActionEvent action) {
		if (!isAuthorized(80,"adminHome")) {			
			return;
		}
		logicalEnvConfigBean = (LogicalEnvConfigBean)FacesUtils.getManagedBean("logicalEnvConfigBean");
	}
	
	public void openLogicalEnvView(ActionEvent event) {
		if (!isAuthorized(87,"")) {
			setAccessDenied();
			return;
		}
		else{
			logicalEnvConfigBean.init(event);
			rendererLogicalEnv = true;
		}
	}
	
	public void closeLogicalEnvView(ActionEvent event) {
		if(logicalEnvConfigBean != null) {
			logicalEnvConfigBean.setNameAlreadyExists(false);
			logicalEnvConfigBean.setNameMandatory(false);
			logicalEnvConfigBean.setNameStyle("font-size: 10px; width: 265px;");
		}
		rendererLogicalEnv = false;
	}
	
	public void handleDashBoardMenu(ActionEvent event) {
		dashBoardMenuVisible = !dashBoardMenuVisible;
	}
	
	public void handleDataCollectionMenu(ActionEvent event) {
		dataCollectionMenuVisible = !dataCollectionMenuVisible;
	}
	
	public void handleSecurityMenu(ActionEvent event) {
		securityMenuVisible = !securityMenuVisible;
	}
	
	public void handleThirdPartyMenu(ActionEvent event) {
		thirdPartyMenuVisible = !thirdPartyMenuVisible;
	}
	
	public void handleExtensionsMenu(ActionEvent event) {
		extensionsMenuVisible = !extensionsMenuVisible;
	}

	public boolean isDashBoardMenuVisible() {
		return dashBoardMenuVisible;
	}

	public boolean isDataCollectionMenuVisible() {
		return dataCollectionMenuVisible;
	}
	
	public boolean isSecurityMenuVisible() {
		return securityMenuVisible;
	}

	public boolean isThirdPartyMenuVisible() {
		return thirdPartyMenuVisible;
	}

	public boolean isExtensionsMenuVisible() {
		return extensionsMenuVisible;
	}	

	public boolean isRendererFilter() {
		return rendererFilter;
	}	
	
	public void openFilterPopup(ActionEvent event) {
		rendererFilter = true;
	}
	
	public void closeFilterPopup(ActionEvent event) {
		rendererFilter = false;
	}

	public boolean isRendererLogicalEnv() {
		return rendererLogicalEnv;
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}
}
