package com.fsi.monitoring.workSpace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.CrossComputeDefinitionBean;
import com.fsi.monitoring.datamodel.bean.StaticDataDefinitionBean;
import com.fsi.monitoring.datamodel.ikrDefinition.CrossComputeDefinitionCreationBean;
import com.fsi.monitoring.datamodel.ikrDefinition.CrossComputeDefinitionSelectionBean;
import com.fsi.monitoring.datamodel.staticData.StaticDataConfigBean;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.workSpace.bean.WorkSpaceBean;

public class DataCenterBean extends AccessControlBean implements Serializable {
	private static final long serialVersionUID = 5628819211604907863L;
	
	private static Map<String, String> menus = new HashMap<String, String>();
	
	private WorkSpaceBean workSpaceBean = null;
	
	private String selectedMenu;
	private String currentMenu;
	
	private CrossComputeDefinition crossComputeDefinitionBean = null;
	
	private boolean rendererMetricSelector = false;
	private boolean rendererNewStaticData = false;
	private boolean rendererUpdateStaticData = false;
	private boolean rendererStaticData = false;
	private boolean rendererFilter = false;
	private boolean rendererProgressBar = false;
	
	private boolean onEditCC;
	
	int numberCCSelected = 0;
	int numberSDSelected = 0;
	
	static {
		menus.put("analysis", "Analysis");
		menus.put("crossComputeDefinitionSelection", "Cross Compute");
		menus.put("staticDataConfig", "Static Data");
	}
	
	public void init(ActionEvent action) {
		if (!isAuthorized(40,"dataCenter")) {
			return;
		}
		
		if (isAuthorized(41,"analysis")) {
			selectedMenu = "analysis";
			currentMenu = selectedMenu;
			navigate(null);
		}
		else if (isAuthorized(47,"crossComputeDefinitionSelection")) {
			selectedMenu = "crossComputeDefinitionSelection";
			currentMenu = selectedMenu;
			navigate(null);
		}
		else if (isAuthorized(51,"staticDataConfig")) {
			selectedMenu = "staticDataConfig";
			currentMenu = selectedMenu;
			navigate(null);
		}
		else {
			return;
		}
	}
	
	public void generateAnalysis(ActionEvent action) {
		if (!isAuthorized(41,"ikrWorkSpace")) {
			return;
		}
		
		closeMetricSelectorPopup(null);
		
		workSpaceBean.generate(action);		
		selectedMenu = "analysis";
	}
	
	public void saveCrossCompute(ActionEvent action) {
		CrossComputeDefinitionCreationBean crossComputeDefinitionCreationBean = (CrossComputeDefinitionCreationBean)FacesUtils.getManagedBean("crossComputeDefinitionCreationBean");
		crossComputeDefinitionCreationBean.save(action);
		if(crossComputeDefinitionCreationBean.isWellSaved())
			selectedMenu = crossComputeDefinitionCreationBean.action();
	}
	
	public void deleteCrossCompute(ActionEvent action) {
		if (!isAuthorized(50,"crossComputeDefinitionSelection")) {
			setAccessDenied(currentMenu);
			return;
		}	
		CrossComputeDefinitionBean crossComputeDefinition = (CrossComputeDefinitionBean)action.getComponent().getAttributes().get("crossComputeDefinition");
		CrossComputeDefinitionCreationBean crossComputeDefinitionCreationBean = (CrossComputeDefinitionCreationBean)FacesUtils.getManagedBean("crossComputeDefinitionCreationBean");
		crossComputeDefinitionCreationBean.delete(crossComputeDefinition);
		selectedMenu = crossComputeDefinitionCreationBean.action();
		CrossComputeDefinitionSelectionBean crossComputeDefinitionSelectionBean = (CrossComputeDefinitionSelectionBean)FacesUtils.getManagedBean("crossComputeDefinitionSelectionBean");
		crossComputeDefinitionSelectionBean.handleDeleteNoSelection(crossComputeDefinition);
		crossComputeDefinitionSelectionBean.setCrossComputeDefinitionBeansSelected(new ArrayList<CrossComputeDefinitionBean>());
	}
	
	public void deleteSelectedCrossComputes(ActionEvent action) {
		if (!isAuthorized(50,"crossComputeDefinitionSelection")) {
			setAccessDenied(currentMenu);
			return;
		}	
		
		CrossComputeDefinitionSelectionBean crossComputeDefinitionSelectionBean = (CrossComputeDefinitionSelectionBean)FacesUtils.getManagedBean("crossComputeDefinitionSelectionBean");
		List<CrossComputeDefinitionBean> crossComputeDefinitions = crossComputeDefinitionSelectionBean.getCrossComputeDefinitions();
		numberCCSelected = 0;
		for (CrossComputeDefinitionBean crossComputeDefinition : crossComputeDefinitions) {
			if (crossComputeDefinition.isSelected()){
				numberCCSelected++;
			}
		}
		if (numberCCSelected > 0) {
			for (CrossComputeDefinitionBean crossComputeDefinition : crossComputeDefinitions) {
				if (crossComputeDefinition.isSelected()){
					CrossComputeDefinitionCreationBean crossComputeDefinitionCreationBean = (CrossComputeDefinitionCreationBean)FacesUtils.getManagedBean("crossComputeDefinitionCreationBean");
					crossComputeDefinitionCreationBean.delete(crossComputeDefinition);
					selectedMenu = crossComputeDefinitionCreationBean.action();
				}
			}
			crossComputeDefinitionSelectionBean.setSelectedCrossComputeDefinitionBean(null);
			crossComputeDefinitionSelectionBean.setCrossComputeDefinitionBeansSelected(new ArrayList<CrossComputeDefinitionBean>());
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No cross compute definition has been selected");
		}		
	}
	
	public void showCrossComputeCreation(ActionEvent action) {
		if (!isAuthorized(48,"createCrossComputeDefinition")) {
			setAccessDenied(currentMenu);
			return;
		}
		CrossComputeDefinitionCreationBean crossComputeDefinitionCreationBean = (CrossComputeDefinitionCreationBean)FacesUtils.getManagedBean("crossComputeDefinitionCreationBean");
		crossComputeDefinitionCreationBean.initForCreate(action);
		selectedMenu = crossComputeDefinitionCreationBean.action();
	}
	
	public void showStaticData(ActionEvent action) {
		if (!isAuthorized(52,"updateStaticData")) {
			setAccessDenied(currentMenu);
			return;
		}
		rendererUpdateStaticData = false;
		rendererStaticData = true;
		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
		staticDataConfigBean.initUpdate(action);
	}
	
	public void showStaticDataForUpdate(ActionEvent action) {
		if (!isAuthorized(53,"updateStaticData")) {
			setAccessDenied(currentMenu);
			return;
		}
		rendererUpdateStaticData = true;
		rendererStaticData = true;
		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
		staticDataConfigBean.setOnEdit(true);
		staticDataConfigBean.initUpdate(action);
		selectedMenu = staticDataConfigBean.action();
		
//		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
//		Collection<StaticDataDefinitionBean> staticDatas = staticDataConfigBean.getStaticDatas();
//		numberSDSelected = 0;
//		for (StaticDataDefinitionBean staticData : staticDatas) {
//			if (staticData.isSelected()){
//				numberSDSelected++;
//			}
//		}
//		if (numberSDSelected < 2) {
//			if (numberSDSelected == 1) {
//				staticDataConfigBean.initUpdate(action);
//				selectedMenu = staticDataConfigBean.action();
//				rendererUpdateStaticData = true;
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No static data has been selected");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one static data to edit");
//		}		
	}
	
	public void saveStaticData(ActionEvent action) {
		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
		staticDataConfigBean.save(action);
		if(staticDataConfigBean.isWellSaved()) {
			selectedMenu = staticDataConfigBean.action();
			rendererStaticData = false;
		}
	}
	
	public void deleteStaticData(ActionEvent action) {
		if (!isAuthorized(54,"staticDataConfig")) {
			setAccessDenied(currentMenu);
			return;
		}
		StaticDataDefinitionBean StaticDataDefinition = (StaticDataDefinitionBean)action.getComponent().getAttributes().get("StaticDataDefinition");
		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
		staticDataConfigBean.delete(StaticDataDefinition);
		selectedMenu = staticDataConfigBean.action();
		staticDataConfigBean.handleDeleteNoSelection(StaticDataDefinition);
		staticDataConfigBean.setStaticDatasSelected(new ArrayList<StaticDataDefinitionBean>());
	}
	
	public void deleteSelectedStaticDatas(ActionEvent action) {
		if (!isAuthorized(54,"staticDataConfig")) {
			setAccessDenied(currentMenu);
			return;
		}
		
		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
		Collection<StaticDataDefinitionBean> staticDatas = staticDataConfigBean.getStaticDatas();
		numberSDSelected = 0;
		for (StaticDataDefinitionBean staticData : staticDatas) {
			if (staticData.isSelected()){
				numberSDSelected++;
			}
		}
		if (numberSDSelected > 0) {
			for (StaticDataDefinitionBean staticData : staticDatas) {
				if (staticData.isSelected()){
					staticDataConfigBean.delete(staticData);
					selectedMenu = staticDataConfigBean.action();
				}
			}
			staticDataConfigBean.setSelectedStaticDataDefinitionBean(null);
			staticDataConfigBean.setStaticDatasSelected(new ArrayList<StaticDataDefinitionBean>());
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No static data has been selected");
		}
	}
	
	public void showCrossComputeUpdate(ActionEvent action) {
		if (!isAuthorized(49,"createCrossComputeDefinition")) {
			setAccessDenied(currentMenu);
			return;
		}
		
		CrossComputeDefinitionCreationBean crossComputeDefinitionCreationBean = (CrossComputeDefinitionCreationBean)FacesUtils.getManagedBean("crossComputeDefinitionCreationBean");
		crossComputeDefinitionCreationBean.initForUpdate(action);
		selectedMenu = action();
		onEditCC = true;
				
//		CrossComputeDefinitionSelectionBean crossComputeDefinitionSelectionBean = (CrossComputeDefinitionSelectionBean)FacesUtils.getManagedBean("crossComputeDefinitionSelectionBean");
//		List<CrossComputeDefinitionBean> crossComputeDefinitions = crossComputeDefinitionSelectionBean.getCrossComputeDefinitions();
//		numberCCSelected = 0;
//		for (CrossComputeDefinitionBean crossComputeDefinition : crossComputeDefinitions) {
//			if (crossComputeDefinition.isSelected()){
//				numberCCSelected++;
//			}
//		}
//		if (numberCCSelected < 2) {
//			if (numberCCSelected == 1) {
//				CrossComputeDefinitionCreationBean crossComputeDefinitionCreationBean = (CrossComputeDefinitionCreationBean)FacesUtils.getManagedBean("crossComputeDefinitionCreationBean");
//				crossComputeDefinitionCreationBean.initForUpdate(action);
//				selectedMenu = action();
//				onEditCC = true;
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No cross compute definition has been selected");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one cross compute to edit");
//		}
	}
	
	
	public void navigate(ActionEvent action) {		
		if (action != null)
			selectedMenu = (String)action.getComponent().getAttributes().get("to");
		if(onEditCC){
			CrossComputeDefinitionCreationBean crossComputeDefinitionCreationBean = (CrossComputeDefinitionCreationBean)FacesUtils.getManagedBean("crossComputeDefinitionCreationBean");
			crossComputeDefinitionCreationBean.cancelEditCrossCompute();
		}
		onEditCC = false;
		
		if ("analysis".equals(selectedMenu)) {
			if (!isAuthorized(41,"dataCenter")){
				setAccessDenied(currentMenu);
				return;
			}
			workSpaceBean = (WorkSpaceBean)FacesUtils.getManagedBean("workSpaceBean");
			workSpaceBean.init(null);
		}
		else if ("crossComputeDefinitionSelection".equals(selectedMenu)) {
			if (!isAuthorized(47,"dataCenter")){
				setAccessDenied(currentMenu);
				return;
			}
			CrossComputeDefinitionSelectionBean crossComputeDefinitionSelectionBean = (CrossComputeDefinitionSelectionBean)FacesUtils.getManagedBean("crossComputeDefinitionSelectionBean");
			crossComputeDefinitionSelectionBean.init(null);	
		}
		else {
			if (!isAuthorized(51,"dataCenter")){
				setAccessDenied(currentMenu);
				return;
			}
			StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
			staticDataConfigBean.init(null);
		}

		workSpaceBean.getMetricSelectorBean().setSelectAll(false);
		currentMenu = selectedMenu;
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

	public String getCurrentMenu() {
		return currentMenu;
	}

	public String getStyle() {
		return "font-weight:bold; color: #ff774f;";
	}	
	
	public boolean isRendererMetricSelector() {
		return rendererMetricSelector;
	}

	public boolean isRendererHistoricalDataAnalysis() {
		return workSpaceBean.isRendererHistoricalDataAnalysis();
	}
	
	public void setRendererHistoricalDataAnalysis(boolean rendererHistoricalDataAnalysis) {
		workSpaceBean.setRendererHistoricalDataAnalysis(rendererHistoricalDataAnalysis);
	}
	
	public boolean isRendererStaticData() {
		return rendererStaticData;
	}

	public boolean isRendererUpdateStaticData() {
		return rendererUpdateStaticData;
	}
	
//	public boolean isRendererNewStaticData() {
//		return rendererNewStaticData;
//	}
	
	public boolean isRendererFilter() {
		return rendererFilter;
	}
	
	public boolean isRendererProgressBar() {
		return rendererProgressBar;
	}

	public void setRendererProgressBar(boolean rendererProgressBar) {
		this.rendererProgressBar = rendererProgressBar;
	}

	public void openMetricSelectorPopup(ActionEvent event) {
		rendererMetricSelector = true;
	}
	
	public void closeMetricSelectorPopup(ActionEvent event) {
		rendererMetricSelector = false;
	}
	
	public void openHistoricalDataAnalysisPopup(ActionEvent event) {
		workSpaceBean.getProgressBarController().initProgressBar();
		workSpaceBean.setRendererHistoricalDataAnalysis(true);
	}
	
	public void closeHistoricalDataAnalysisPopup(ActionEvent event) {
		workSpaceBean.setRendererHistoricalDataAnalysis(false);
	}
	
	public void closeStaticDataPopup(ActionEvent event) {
		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");		
		staticDataConfigBean.resetControlStyle();
		
		if (rendererUpdateStaticData) {
			staticDataConfigBean.cancelEditStaticData();
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());	
			try {
				monitoringPM.updateStaticDataDefinition(staticDataConfigBean.getSd());
			} catch (PersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	public void closeNewStaticDataPopup(ActionEvent event) {
//		rendererNewStaticData = false;
//		StaticDataConfigBean staticDataConfigBean = (StaticDataConfigBean)FacesUtils.getManagedBean("staticDataConfigBean");
//		staticDataConfigBean.resetControlStyle();
//	}
	
	public void openFilterPopup(ActionEvent event) {
		rendererFilter = true;
	}
	
	public void closeFilterPopup(ActionEvent event) {
		rendererFilter = false;
	}
	
}
