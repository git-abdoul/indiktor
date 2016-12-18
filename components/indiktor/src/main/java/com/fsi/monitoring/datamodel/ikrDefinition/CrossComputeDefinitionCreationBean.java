package com.fsi.monitoring.datamodel.ikrDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorVisitor;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.CrossComputeDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.MetricGroupBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorVisitor;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlCommandButton;

public class CrossComputeDefinitionCreationBean
extends AccessControlBean
implements IkrStaticDomainSelectorVisitor, LogicalEnvSelectionVisitor, Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(CrossComputeDefinitionCreationBean.class);
	
	private List<String> datas;
	
	private CrossComputeDefinition crossComputeDefinition = null;
		
	private UICommand deleteCommand = null;
	
	private String lastEntry = "";
	private String textEntry = "";
	
	private String ikrInstance;
	private String crossComputation;
	private IkrUnitType ikrUnitTypeOld;
	private String ikrUnitOld;
	private String description;
	private double threshold;
	private String searchIndexesOld;
	private boolean persistent;
	private boolean archive;
	
	private MetricSelectorBean ikrSelectorBean;		
	IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean;
	
	private boolean newMetricCategory = false;
	private IkrCategory ikrCategory;
	
	private boolean rendererThreshold = true;
	private boolean enableArchive = true;
	
	private boolean wellSaved;
	
	private LogicalEnvSelectionBean logicalEnvSelectionBean;
	
	private SelectItem[] ikrUnitTypes;
	
	private String searchIndexes;
	
	public CrossComputeDefinitionCreationBean() {
		deleteCommand = new HtmlCommandButton();
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(true);
	}
	
	public void initForCreate(ActionEvent action) {
		if (isAuthorized(48,"createCrossComputeDefinition")) {
			crossComputeDefinition =  new CrossComputeDefinition();			
			init();
			
			ikrInstance = "";
			deleteCommand.setRendered(false);
		}
	}
	
	public void initForUpdate(ActionEvent action) {
		CrossComputeDefinitionBean crossComputeDefinitionBean = (CrossComputeDefinitionBean)action.getComponent().getAttributes().get("crossComputeDefinition");
		if (crossComputeDefinitionBean != null) {
			try {
				MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());				
				crossComputeDefinition = (CrossComputeDefinition)monitoringPM.getIkrDefinition(crossComputeDefinitionBean.getId());

				crossComputation = crossComputeDefinition.getCrossComputation();
				init();
				deleteCommand.setRendered(true);

				updateIkrDefinitionList(crossComputeDefinition.getCrossComputation());
				
//				MetricSelectorBean metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
//				ArrayList<ModifiableMetricBean> listTemp = metricSelectorBean.getSelectedBeans();
//				for(ModifiableMetricBean selectedBean : listTemp) {
//					if(!selectedBeans.contains(selectedBean)) {
//						selectedBeans.add(selectedBean);
//					}
//				}
				
	    	} catch(Exception exc) {
	    		logger.error(exc);
	    	}

			ikrUnitTypeOld = getIkrUnitType();
			ikrUnitOld = getIkrUnit();
			searchIndexesOld = searchIndexes;
			persistent = ikrCategory.isPersistent();
			archive = ikrCategory.isArchive();
			description = ikrCategory.getDescription();
			threshold = ikrCategory.getThreshold();
			ikrInstance = crossComputeDefinition.getIkrInstance();
		}
	}
	
	public void onInputValue(ValueChangeEvent event) {
		lastEntry = (String)event.getNewValue();
	}	
	
	private void updateIkrDefinitionList(String computation) {
		BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
		
		Collection<Long> crossCompute = CrossComputeDefinition.parse(computation);
		
		ikrSelectorBean.getSelectedBeans().clear();
		
		for (Long ikrDefinitionId : crossCompute) {
			MetricGroupBean metricGroupBean = beanPM.getIkrDefinitionBean(ikrDefinitionId);
    		ModifiableMetricBean bean = new ModifiableMetricBean(metricGroupBean);
			bean.updateSelected(false);
			ikrSelectorBean.getSelectedBeans().add(bean);
		}
	}
	
	private void init() {
		datas = new ArrayList<String>();
		
		ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");
		ikrStaticDomainSelectorBean.initComponent(false, true, false, false);
		ikrStaticDomainSelectorBean.accept(this);
		if (crossComputeDefinition.getIkrCategoryId() != 0) {
			ikrStaticDomainSelectorBean.initMetricCategoryId(crossComputeDefinition.getIkrCategoryId());
		} else {
			ikrStaticDomainSelectorBean.initItems();
		}
		
		initIkrUnitType();
		
		logicalEnvSelectionBean.accept(this);
		if (crossComputeDefinition.getLogicalEnvId() != 0) {
			logicalEnvSelectionBean.initLogicalEnv(crossComputeDefinition.getLogicalEnvId());
		} else {
			logicalEnvSelectionBean.init();
		}
	
		ikrSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
		ikrSelectorBean.init();
		ikrSelectorBean.setRendered(true);		
	}
	
	private void initMetricCategory() {
		ikrCategory = new IkrCategory(ikrStaticDomainSelectorBean.getMetricDomainId());	
		changeIkrUnitType(IkrUnitType.NUMBER);
	}
	
	private void initIkrUnitType() {
		EnumSet<IkrUnitType> unitTypes = EnumSet.allOf(IkrUnitType.class);				
		ikrUnitTypes = new SelectItem[unitTypes.size()-1];		
		int i = 0;
		for(IkrUnitType unitType : unitTypes) {
			String name = unitType.name();
			if (unitType != IkrUnitType.NA) {
				ikrUnitTypes[i++] = new SelectItem(unitType,name);
			}			
		}
	}
	
	public SelectItem[] getUnits() {
		IkrUnitType unitType = ikrCategory.getIkrUnitType();
		if (unitType == null) {
			return new SelectItem[0];
		}
		
		Collection<IkrUnit> ikrUnits = unitType.getIkrUnits();
		
		SelectItem[] items = null;
		if (ikrUnits != null) {
			items = new SelectItem[ikrUnits.size()];
		
			int i = 0;
			for(IkrUnit ikrUnit : ikrUnits) {
				items[i++] = new SelectItem(ikrUnit.name(),ikrUnit.getSymbol());
			}
		}
		return items;
	}

	public boolean isUpdate() {
		return crossComputeDefinition.getId() != 0;
	}
	
	public void onChangeIkrUnitType(ValueChangeEvent e) {
		IkrUnitType newValue = (IkrUnitType)e.getNewValue();	
		changeIkrUnitType(newValue);
	}
	
	private void changeIkrUnitType(IkrUnitType ikrUnitType) {
		ikrCategory.setIkrUnitType(ikrUnitType);
		
		List<IkrUnit> ikrUnits = ikrUnitType.getIkrUnits();
		changeIkrUnit(ikrUnits.get(0).name());
	}
	
	public void onChangeIkrUnit(ValueChangeEvent e) {
		String unit = (String)e.getNewValue();
		changeIkrUnit(unit);
	}
	
	private void changeIkrUnit(String unit) {
		if (unit != null && unit.length() > 0) {
			try {
				IkrUnit ikrUnit = ikrCategory.getIkrUnitType().getIkrUnit(unit);
				ikrCategory.setIkrUnit(ikrUnit);
			} catch (Exception exc) {
				Log.error("Invalid IkrUnit " + unit);
			}
		}		
	}
	
	public SelectItem[] getBooleanSelection() {
		SelectItem[] items = new SelectItem[2];		
		items[0] = new SelectItem(true, "true");
		items[1] = new SelectItem(false, "false");
		return items;
	}
	
	public void changeMetricDomain(int metricDomainId) {
		newMetricCategory = (ikrStaticDomainSelectorBean.getMetricCategoryItems().size()>0) ? false : true;
		if (newMetricCategory)
			initMetricCategory();
	}
	
	public void changeMetricGroup(int metricGroupId) {
		ikrCategory = (IkrCategory)ikrStaticDomainSelectorBean.getMetricCategory();
		crossComputeDefinition.setIkrCategoryId(metricGroupId);
		List<String> oldSearchIndexes = ikrCategory.getSearchesIndexes();
		int sz = oldSearchIndexes.size();
        String indexeStr = "";
        int i = 0;
        for (String index : oldSearchIndexes) {
        	indexeStr  = indexeStr + index;
        	if (i < sz-1) {
        		indexeStr  = indexeStr + ":";
        	}
        	i++;
        }
        searchIndexes = indexeStr;
	}
	
	public void changeLogicalEnv(int logicalEnvId) {
		crossComputeDefinition.setLogicalEnvId(logicalEnvId);
	}	
		
	public CrossComputeDefinition getCrossComputeDefinition() {
		return crossComputeDefinition;
	}

	public void setCrossComputeDefinition(CrossComputeDefinition crossComputeDefinition) {
		this.crossComputeDefinition = crossComputeDefinition;
	}
	
	public boolean isActivated() {
		return crossComputeDefinition.isActivated();
	}

	public void setActivated(boolean activated) {
		crossComputeDefinition.setActivated(activated);
	}
	
	public UICommand getDeleteCommand() {
		return deleteCommand;
	}	
	
	public void setDeleteCommand(UICommand removeCommand) {}	
	
	public void cancelEditCrossCompute() {
		setSearchIndexes(searchIndexesOld);
		ikrCategory.setIkrUnitType(ikrUnitTypeOld);
		changeIkrUnit(ikrUnitOld);
		crossComputeDefinition.setCrossComputation(this.crossComputation);
	}
	
	private void updateBean() {
		crossComputeDefinition.setIkrInstance(this.ikrInstance);
		ikrCategory.setDescription(this.description);
		ikrCategory.setThreshold(this.threshold);
		ikrCategory.setArchive(this.archive);
		ikrCategory.setPersistent(this.persistent);
//		MetricSelectorBean metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
//		metricSelectorBean.setSelectedBeans(this.selectedBeans);
	}

	public void save(ActionEvent event) {	
		if (isAuthorized(48,"crossComputeDefinitionSelection") || isAuthorized(49,"crossComputeDefinitionSelection")) {	
			wellSaved = false;
			String ikrCatLabel = ikrCategory.getLabel();
			String errorStr = "";
			
			if(ikrCatLabel == null || ikrCatLabel.length() == 0) {
				errorStr = "Metric Category";
			}
			if(ikrInstance == null || ikrInstance.length() == 0) {
				if(errorStr.equalsIgnoreCase(""))
					errorStr = errorStr + "Metric Name";
				else
					errorStr = errorStr + " / Metric Name";
			}
			if(crossComputeDefinition.getCrossComputation() == null	|| crossComputeDefinition.getCrossComputation().length() == 0) {
				if(errorStr.equalsIgnoreCase(""))
					errorStr = errorStr + "Computation Formula";
				else
					errorStr = errorStr + " / Computation Formula";
			}
			
			if(ikrCatLabel != null && ikrCatLabel.length() > 0 && ikrInstance != null && ikrInstance.length() > 0
					&& crossComputeDefinition.getCrossComputation() != null	&& crossComputeDefinition.getCrossComputation().length() > 0) {
			
				updateBean();
				try {
					DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
					MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
					
					List<String> searchIndexeList = new ArrayList<String>();
					if (searchIndexes!=null && searchIndexes.length()>0) {
						String[] indexes = searchIndexes.split(":");
						searchIndexeList = Arrays.asList(indexes);
					}
					
					ikrCategory.setSearchesIndexes(searchIndexeList);
					if (crossComputeDefinition.getId() == 0) {					
						if (ikrCategory.getId() == 0){
							int metricCategoryId = dataModelPM.createIkrStaticDomain(ikrCategory);
							crossComputeDefinition.setIkrCategoryId(metricCategoryId);
						}				
						monitoringPM.createCrossComputeDefinition(crossComputeDefinition);
					} else {
						dataModelPM.updateIkrStaticDomain(ikrCategory);
						monitoringPM.updateCrossComputeDefinition(crossComputeDefinition);
					}
					
					CrossComputeDefinitionSelectionBean crossComputeDefinitionSelectionBean = (CrossComputeDefinitionSelectionBean)FacesUtils.getManagedBean("crossComputeDefinitionSelectionBean");
					crossComputeDefinitionSelectionBean.reloadCrossComputeDefinitions();
					wellSaved = true;
				} catch(Exception exc) {
					logger.error(exc);
				}
			}
			else {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage(errorStr + " cannot be empty !");
			}
		}
	}
	
	public void delete(CrossComputeDefinitionBean crossComputeDefinition) {
		if (!isAuthorized(50,"crossComputeDefinitionSelection")) {
			return;
		}	
		
		if (crossComputeDefinition != null) {
			try {
				MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
				monitoringPM.deleteIkrDefinition(crossComputeDefinition.getId());
				
				CrossComputeDefinitionSelectionBean crossComputeDefinitionSelectionBean = (CrossComputeDefinitionSelectionBean)FacesUtils.getManagedBean("crossComputeDefinitionSelectionBean");
				crossComputeDefinitionSelectionBean.reloadCrossComputeDefinitions();
				
			} catch(Exception exc) {
				logger.error(exc);
			}
		}
	}
	
	public void addMetricToFormula(ActionEvent event) {
		ModifiableMetricBean selectedMetric = (ModifiableMetricBean)event.getComponent().getAttributes().get("obj");
		
		lastEntry = "M"+((IkrDefinitionBean)selectedMetric.getMetricGroupBean()).getId();
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
	}
	
	public void removeMetricFromList(ActionEvent event) {
		ModifiableMetricBean metricToRemove = (ModifiableMetricBean)event.getComponent().getAttributes().get("obj");		
		if (isInFormula(metricToRemove)) {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("Can't remove this metric. It already exist in the Formula. Please remove the Id from the Formula to proceed");
		} 
		else {	
			// necessary to remove this bean otherwise it will be mapped again at the next getConditionBeans
			long idToRemove = ((IkrDefinitionBean)metricToRemove.getMetricGroupBean()).getId();
			Iterator<ModifiableMetricBean> metricIterator = ikrSelectorBean.getSelectedBeans().iterator();
			while (metricIterator.hasNext()) {
				ModifiableMetricBean metricBean = metricIterator.next();
				long tmpBeanId =((IkrDefinitionBean)metricBean.getMetricGroupBean()).getId();
				if (tmpBeanId == idToRemove) {
					metricIterator.remove();
				}
			}
		}
	}
	
	public void removeAllMetricFromList(ActionEvent event) {
		Iterator<ModifiableMetricBean> metricIterator = ikrSelectorBean.getSelectedBeans().iterator();
		int metricInFormula = 0;
		while (metricIterator.hasNext()) {
			ModifiableMetricBean metricBean = metricIterator.next();
			if (isInFormula(metricBean)) {
				metricInFormula++;
			} 
			else {
				metricIterator.remove();
			}
		}
		if (metricInFormula > 0) {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("Can't remove some metrics. They already exist in the Formula. Please remove the Id from the Formula to proceed");
		} 
	}
	
	public int getSelectedMetricSize() {
		int sz = 0;
		if (ikrSelectorBean.getSelectedBeans() != null)
			sz = ikrSelectorBean.getSelectedBeans().size();
		return sz;
	}
	
	public boolean isInFormula(ModifiableMetricBean metric) {
		boolean ret = false;
		if (crossComputeDefinition.getCrossComputation() != null) {
			long id = ((IkrDefinitionBean)metric.getMetricGroupBean()).getId();
			ret = crossComputeDefinition.getCrossComputation().contains("M"+id);
		}
		return ret;
	}
	
	public void addNewMetricCategory(ActionEvent event) {
		newMetricCategory = true;
		initMetricCategory();
	}

	public boolean isNewMetricCategory() {
		return newMetricCategory;
	}

	public void setNewMetricCategory(boolean newMetricCategory) {
		this.newMetricCategory = newMetricCategory;
	}

	public IkrCategory getIkrCategory() {
		return ikrCategory;
	}

	public void setIkrCategory(IkrCategory ikrCategory) {
		this.ikrCategory = ikrCategory;		
		List<String> oldSearchIndexes = ikrCategory.getSearchesIndexes();
		int sz = oldSearchIndexes.size();
        String indexeStr = "";
        int i = 0;
        for (String index : oldSearchIndexes) {
        	indexeStr  = indexeStr + index;
        	if (i < sz-1) {
        		indexeStr  = indexeStr + ":";
        	}
        	i++;
        }
        searchIndexes = indexeStr;
	}	
	
	public SelectItem[] getIkrUnitTypes() {
		return ikrUnitTypes;
	}
	
	public IkrUnitType getIkrUnitType() {
		return ikrCategory.getIkrUnitType();
	}
	
	public void setIkrUnitType(IkrUnitType ikrUnitType) {}

	public String getIkrUnit() {
		IkrUnit ikrUnit = ikrCategory.getIkrUnit();
		
		if (ikrUnit != null) {
			return ikrUnit.name();
		}
		
		return null;
	}
	
	public void setIkrUnit(String ikrUnit) {
		if (ikrUnit != null && ikrUnit.length() > 0) {
			try {
				IkrUnit unit = ikrCategory.getIkrUnitType().getIkrUnit(ikrUnit);
				ikrCategory.setIkrUnit(unit);
			} catch (Exception exc) {
				// nothing to do it could happen when ikrUnitType is changed
			}
		}		
	}
	
	public String getIkrInstance() {
		return ikrInstance;
	}

	public void setIkrInstance(String ikrInstance) {
		this.ikrInstance = ikrInstance;
	}

	public void setCrossComputation(String crossComputation) {
		this.crossComputation = crossComputation;
	}

	public IkrUnitType getIkrUnitTypeOld() {
		return ikrUnitTypeOld;
	}

	public void setIkrUnitTypeOld(IkrUnitType ikrUnitTypeOld) {
		this.ikrUnitTypeOld = ikrUnitTypeOld;
	}

	public String getIkrUnitOld() {
		return ikrUnitOld;
	}

	public void setIkrUnitOld(String ikrUnitOld) {
		this.ikrUnitOld = ikrUnitOld;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public boolean isPersistent() {
		return persistent;
	}
	
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	public boolean isArchive() {
		return archive;
	}
	
	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public boolean isUnitsRendered() {
		IkrUnitType unitType = ikrCategory.getIkrUnitType();
		if (unitType == null) {
			return false;
		}

		Collection<IkrUnit> units = unitType.getIkrUnits();
		
		return (units != null && units.size() > 1);
	}

	public String getMetricCategoryLabel() {
		return ikrCategory.getLabel();
	}

	public void setMetricCategoryLabel(String metricCategoryLabel) {
		ikrCategory.setDomainValue("COMPUTED - " + metricCategoryLabel);
		ikrCategory.setLabel(metricCategoryLabel);
	}
	
	public void zeroListener(ActionEvent e) {
		lastEntry = "0";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void oneListener(ActionEvent e) {
		lastEntry = "1";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void twoListener(ActionEvent e) {
		lastEntry = "2";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void threeListener(ActionEvent e) {
		lastEntry = "3";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void fourListener(ActionEvent e) {
		lastEntry = "4";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void fiveListener(ActionEvent e) {
		lastEntry = "5";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void sixListener(ActionEvent e) {
		lastEntry = "6";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void sevenListener(ActionEvent e) {
		lastEntry = "7";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void eightListener(ActionEvent e) {
		lastEntry = "8";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void nineListener(ActionEvent e) {
		lastEntry = "9";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void commaListener(ActionEvent e) {
		lastEntry = ".";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	
	public void openParenthesisListener(ActionEvent e) {
		lastEntry = "(";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	 
	public void closeParenthesisListener(ActionEvent e) {
		lastEntry = ")";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	 
	public void clearEntryListener(ActionEvent e) {
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString != null) {
			crossComputeDefinition.setCrossComputation(tempString.substring(0, tempString.length() - lastEntry.length()));
			tempString = crossComputeDefinition.getCrossComputation();
			setLastEntry("");
		}
	}
	 
	public void sumListener(ActionEvent e) {
		lastEntry = "+";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	 
	public void substractListener(ActionEvent e) {
		lastEntry = "-";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	 
	public void clearListener(ActionEvent e) {
		crossComputeDefinition.setCrossComputation("");
		setLastEntry("");
	}
	 
	public void multiplyListener(ActionEvent e) {
		lastEntry = "*";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	 
	public void divideListener(ActionEvent e) {
		lastEntry = "/";
		datas.add(lastEntry);
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		crossComputeDefinition.setCrossComputation(tempString + lastEntry);
		tempString = crossComputeDefinition.getCrossComputation();
	}
	 
	public void backspaceListener(ActionEvent e) {
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString != "") {
			crossComputeDefinition.setCrossComputation(tempString.substring(0, tempString.length() - 1));
			tempString = crossComputeDefinition.getCrossComputation();
			setLastEntry("");
		}
	}
	
	

	public String getLastEntry() {
		return lastEntry;
	}

	public void setLastEntry(String lastEntry) {
		this.lastEntry = lastEntry;
	}
	
	public String getTextEntry() {
		return textEntry;
	}

	public void setTextEntry(String textEntry) {
		this.textEntry = textEntry;
	}
	
	public void sendTextEntry(ActionEvent e) {
		String tempString = crossComputeDefinition.getCrossComputation();
		if (tempString == null) {
			tempString = "";
		}
		lastEntry = getTextEntry();
		if (lastEntry != "") {
			tempString = crossComputeDefinition.getCrossComputation();
			crossComputeDefinition.setCrossComputation(tempString + lastEntry);
			setTextEntry("");
			tempString = crossComputeDefinition.getCrossComputation();
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No entry has been written");
		}
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}

	public boolean isRendererThreshold() {
		switch (ikrCategory.getIkrUnitType()) {		
			case NUMBER :
			case RATE :
			case STORAGE :
			case CURRENCY :
			case THROUGHPUT :
			case DURATION :
				rendererThreshold = true; 
			break;
			
			case STRING :
			case BOOLEAN :
			case DATETIME :
				rendererThreshold = false; 
			break;
		}		
		return rendererThreshold;
	}	
	
	public boolean isEnableArchive() {
		switch (ikrCategory.getIkrUnitType()) {		
			case NUMBER :
			case RATE :
			case STORAGE :
			case CURRENCY :
			case THROUGHPUT :
			case DURATION :
				enableArchive = true;
			break;
			
			case STRING :
				enableArchive = false;
			break;
				
			case BOOLEAN :
			case DATETIME :
				enableArchive = true;
			break;
		}		
		return rendererThreshold;
	}

	public String getSearchIndexes() {
		return searchIndexes;
	}

	public void setSearchIndexes(String searchIndexes) {
		this.searchIndexes = searchIndexes;
	}

	public IkrStaticDomainSelectorBean getIkrStaticDomainSelectorBean() {
		return ikrStaticDomainSelectorBean;
	}

	public boolean isWellSaved() {
		return wellSaved;
	}

	public List<ModifiableMetricBean> getSelectedBeans() {
		List<ModifiableMetricBean> selectedBeans = new ArrayList<ModifiableMetricBean>();
		List<ModifiableMetricBean> beans = ikrSelectorBean.getSelectedBeans();
		for (ModifiableMetricBean bean : beans) {
			if(!selectedBeans.contains(bean)) {
				selectedBeans.add(bean);
			}
		}			
		return selectedBeans;
	}

//	public void setSelectedBeans(ArrayList<ModifiableMetricBean> selectedBeans) {
//		this.selectedBeans = selectedBeans;
//	}

//	public void select(Collection<ModifiableMetricBean> ikrDefinitionBeans) {
//		for (ModifiableMetricBean bean : ikrDefinitionBeans) {
//			if(!selectedBeans.contains(bean)) {
//				selectedBeans.add(bean);
//			}
//		}	
//	}
//
//	public void deselect(Collection<ModifiableMetricBean> ikrDefinitionBeans) {
//		for (ModifiableMetricBean bean  : ikrDefinitionBeans) {
//			if(selectedBeans.contains(bean)) {
//				selectedBeans.remove(bean);
//			}
//		}		
//	}
}
