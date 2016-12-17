package com.fsi.monitoring.datamodel.staticData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.CrossComputeDefinitionBean;
import com.fsi.monitoring.datamodel.bean.StaticDataDefinitionBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorVisitor;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;
import com.fsi.monitoring.msd.StaticData;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class StaticDataConfigBean
extends SortableList
implements IkrStaticDomainSelectorVisitor, LogicalEnvSelectionVisitor, Serializable{
	protected final static Logger logger = Logger.getLogger(StaticDataConfigBean.class);	
	private static final long serialVersionUID = 8797260417265317385L;
	
	private static final String envColumnName = "Environment";
	private static final String metricCategoryColumnName = "Metric Category";
	private static final String labelColumnName = "Label";
	private static final String valueColumnName = "Value";
	private static final String ikrUnitTypeColumnName = "Ikr Unit Type";
	private static final String ikrUnitColumnName = "Ikr Unit";
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private boolean metricCategoryMandatory = false;
	private boolean labelMandatory = false;
	private boolean coupleMCLabelError = false;
	private boolean valueMandatory = false;
	private String metricCategoryStyle = "width:250px";
	private String metricCategoryStyle2 = "";
	private String labelStyle = "width:250px";
	private String valueStyle = "width:250px";
	
	private String ikrInstanceOnEdit = "";
	private boolean onEdit = false;
	
	private DataModelPM dataModelPM;
	private MonitoringPM monitoringPM;
	private IkrUnitType ikrUnitType;
	private SelectItem[] ikrUnitTypes;
	
	private IkrUnitType ikrUnitTypeOld;
	private String ikrUnitOld;
	private String searchIndexesOld;
	private String description;
	private String ikrInstance;
	private String value;
	
	private boolean wellSaved;
	
	private LogicalEnvSelectionBean logicalEnvSelectionBean;
	private IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean;
	
	private StaticData staticData;
	
	private boolean newMetricCategory = false;
	private IkrCategory ikrCategory;
	
	private StaticDataDefinitionBean selectedStaticDataDefinitionBean;
	
	private List<StaticDataDefinitionBean> staticDatas;
	private List<StaticDataDefinitionBean> staticDatasSelected;
	
	private String searchIndexes;
	
	private String searchQuery = "";
	
	protected StaticDataConfigBean() {
		super(metricCategoryColumnName);
		dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());		
	}

	public void init(ActionEvent action) {
		if (isAuthorized(51,"staticDataConfig")) {
			logicalEnvSelectionBean = new LogicalEnvSelectionBean(false);
			logicalEnvSelectionBean.accept(this);
			logicalEnvSelectionBean.init();
			
			reloadStaticDataDefinitions();	
			selectAll = false;
			staticDatasSelected = new ArrayList<StaticDataDefinitionBean>();
		}
	}
	
	public void pageChangeListener(ActionEvent action) {
		for(StaticDataDefinitionBean staticData : staticDatas) {
			if(staticData.isSelected()) {
				staticData.setSelected(false);
			}
		}
		selectedStaticDataDefinitionBean = null;
		selectAll = false;
		staticDatasSelected = new ArrayList<StaticDataDefinitionBean>();
	}
	
	public void initUpdate(ActionEvent action) {		
		ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");
		ikrStaticDomainSelectorBean.initComponent(false, false, true, false);
		ikrStaticDomainSelectorBean.accept(this);
		
		initIkrUnitType();
		
		StaticDataDefinitionBean StaticDataDefinitionBean = (StaticDataDefinitionBean)action.getComponent().getAttributes().get("StaticDataDefinition");
		if(onEdit)
			ikrInstanceOnEdit = StaticDataDefinitionBean.getIkrDefinition().getIkrInstance();
			
		if (StaticDataDefinitionBean != null) {
			try {
				staticData = (StaticData)monitoringPM.getIkrDefinition(StaticDataDefinitionBean.getId());
				ikrStaticDomainSelectorBean.initMetricCategoryId(staticData.getIkrCategoryId());
				ikrCategory = (IkrCategory)ikrStaticDomainSelectorBean.getMetricCategory();
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
				searchIndexesOld = searchIndexes;
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}			
		} else {
			staticData = new StaticData();	
			ikrStaticDomainSelectorBean.initItems();
		}		

		logicalEnvSelectionBean = new LogicalEnvSelectionBean(true);
		logicalEnvSelectionBean.accept(this);
		if (staticData.getLogicalEnvId() != 0) {
			logicalEnvSelectionBean.initLogicalEnv(staticData.getLogicalEnvId());
		} else {
			logicalEnvSelectionBean.init();
		}
		
		ikrUnitTypeOld = getIkrUnitType();
		ikrUnitOld = getIkrUnit();
		ikrInstance = staticData.getIkrInstance();
		value = staticData.getValue();
	}	
	
	public void searchStaticDataQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		reloadStaticDataDefinitions();
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		selectAll = false;
	}
	
	public void filter() {
		if (searchQuery!=null && searchQuery.length()>0) {
			searchQuery = searchQuery.toLowerCase();
			List<String> queryItems = IkrUtils.splitSearchIndex(searchQuery);
			String[] searchQueryItems = (String[])queryItems.toArray(new String[queryItems.size()]);
			List<StaticDataDefinitionBean> newStaticDataList = new ArrayList<StaticDataDefinitionBean>();
			for (StaticDataDefinitionBean bean : staticDatas) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newStaticDataList.add(bean);
			}
			staticDatas = new ArrayList<StaticDataDefinitionBean>();
			staticDatas.addAll(newStaticDataList);
		}
	}
	
	public StaticData getSd() {
		return staticData;
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
		if (ikrCategory.getIkrUnitType() == null) {
			return new SelectItem[0];
		}
		
		Collection<IkrUnit> ikrUnits = ikrCategory.getIkrUnitType().getIkrUnits();
		
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
	
	public String getIkrUnit() {
		IkrUnit ikrUnit = ikrCategory.getIkrUnit();
		
		if (ikrUnit != null) {
			return ikrUnit.name();
		}
		
		return null;
	}
	
	public void setIkrUnit(String unit) {}
	
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
	
	public void cancelEditStaticData() {
		setSearchIndexes(searchIndexesOld);
		ikrCategory.setIkrUnitType(ikrUnitTypeOld);
		changeIkrUnit(ikrUnitOld);
	}
	
	public void changeLogicalEnv(int logicalEnvId) {
		if (staticData != null)
			staticData.setLogicalEnvId(logicalEnvId);
	}	
	
	public IkrUnitType getMainIkrUnitType() {
		return ikrUnitType;
	}
	
	public void setMainIkrUnitType(IkrUnitType ikrUnitType) {}
	
	public void onChangeIkrUnitType(ValueChangeEvent e) {
		IkrUnitType newValue = (IkrUnitType)e.getNewValue();	
		changeIkrUnitType(newValue);
	}
	
	private void changeIkrUnitType(IkrUnitType ikrUnitType) {
		ikrCategory.setIkrUnitType(ikrUnitType);
		
		List<IkrUnit> ikrUnits = ikrUnitType.getIkrUnits();
		changeIkrUnit(ikrUnits.get(0).name());
	}
	
	public IkrUnitType getIkrUnitType() {
		return ikrCategory.getIkrUnitType();
	}

	public void setIkrUnitType(IkrUnitType ikrUnitType) {}	
	
	public void changeMetricDomain(int metricDomainId) {
		newMetricCategory = (ikrStaticDomainSelectorBean.getMetricCategoryItems().size()>0) ? false : true;
		if (newMetricCategory)
			initMetricCategory();
	}
	
	private void initMetricCategory() {
		ikrCategory = new IkrCategory(ikrStaticDomainSelectorBean.getMetricDomainId());	
		changeIkrUnitType(ikrCategory.getIkrUnitType());
	}

	public void changeMetricGroup(int metricGroupId) {
		ikrCategory = (IkrCategory)ikrStaticDomainSelectorBean.getMetricCategory();
		staticData.setIkrCategoryId(metricGroupId);
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

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(staticDatas, new Comparator<StaticDataDefinitionBean>() {
			public int compare(StaticDataDefinitionBean o1, StaticDataDefinitionBean o2) {
				int res = 0;
				try {
					if (getLabelColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getIkrDefinition().getIkrInstance().compareTo(o2.getIkrDefinition().getIkrInstance()) :  o2.getIkrDefinition().getIkrInstance().compareTo(o1.getIkrDefinition().getIkrInstance());
					}
					else if (getIkrUnitTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getIkrCategory().getIkrUnitType().compareTo(o2.getIkrCategory().getIkrUnitType()) : o2.getIkrCategory().getIkrUnitType().compareTo(o1.getIkrCategory().getIkrUnitType());
					}
					else if (getIkrUnitColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getIkrCategory().getIkrUnit().name().compareTo(o2.getIkrCategory().getIkrUnit().name()) : o2.getIkrCategory().getIkrUnit().name().compareTo(o1.getIkrCategory().getIkrUnit().name());
					}	
					else if (getMetricCategoryColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getIkrCategory().getLabel().compareTo(o2.getIkrCategory().getLabel()) : o2.getIkrCategory().getLabel().compareTo(o1.getIkrCategory().getLabel());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});		
	}
	
	public String getEnvColumnName() {
		return envColumnName;
	}

	public String getMetricCategoryColumnName() {
		return metricCategoryColumnName;
	}

	public String getLabelColumnName() {
		return labelColumnName;
	}

	public String getValueColumnName() {
		return valueColumnName;
	}

	public String getIkrUnitTypeColumnName() {
		return ikrUnitTypeColumnName;
	}

	public String getIkrUnitColumnName() {
		return ikrUnitColumnName;
	}
	
	public SelectItem[] getIkrUnitTypes() {
		return ikrUnitTypes;
	}	
	
    public void onChangeMainIkrUnitType(ValueChangeEvent event) {
    	ikrUnitType = (IkrUnitType)event.getNewValue();
    }
		
	public boolean isUnitsRendered() {
		IkrUnitType unitType = ikrCategory.getIkrUnitType();
		if (unitType == null) {
			return false;
		}

		Collection<IkrUnit> units = unitType.getIkrUnits();
		
		return (units != null && units.size() > 1);
	}
	
	public void addNewMetricCategory(ActionEvent event) {
		newMetricCategory = true;
		initMetricCategory();
	}
	
	private void updateBean() {
		ikrCategory.setDescription(this.description);
		staticData.setValue(this.value);
		staticData.setIkrInstance(this.ikrInstance);
	}
	
	public void save(ActionEvent action) {  
		if(isAuthorized(53, "staticDataConfig") || isAuthorized(52, "staticDataConfig")) {
			wellSaved = false;
			testFields();
			
			if(metricCategoryMandatory || labelMandatory || coupleMCLabelError || valueMandatory)
				return;
			else {
				updateBean();
				try {
					List<String> searchIndexeList = new ArrayList<String>();
					if (searchIndexes!=null && searchIndexes.length()>0) {
						String[] indexes = searchIndexes.split(":");
						searchIndexeList = Arrays.asList(indexes);
					}
					ikrCategory.setSearchesIndexes(searchIndexeList);
					if (staticData.getId() == 0) {
						if (ikrCategory.getId() == 0){
							int metricCategoryId = dataModelPM.createIkrStaticDomain(ikrCategory);
							staticData.setIkrCategoryId(metricCategoryId);
							staticData.setActivated(true);
						}				
						monitoringPM.createStaticDataDefinition(staticData);
					}
					else {
						dataModelPM.updateIkrStaticDomain(ikrCategory);
						monitoringPM.updateStaticDataDefinition(staticData);
					}
				} catch (PersistenceException e) {
					logger.error("impossible to save staticData", e);
				}	
				
				reloadStaticDataDefinitions();
				wellSaved = true;
				staticDatasSelected = new ArrayList<StaticDataDefinitionBean>();
				selectAll = false;
				resetControlStyle();
			}
		}
	}
	
	public void testFields() {
		if(getLabel().trim().length() > 0) {
			metricCategoryMandatory = false;
			metricCategoryStyle = "width:250px;";
		}
		else {
			metricCategoryMandatory = true;
			metricCategoryStyle = "width:250px; border:1px solid red;";
		}
		
		if(ikrInstance.trim().length() > 0) {
			labelMandatory = false;
			labelStyle = "width:250px;";
		}
		else {
			labelMandatory = true;
			labelStyle = "width:250px; border:1px solid red;";
		}
		
		if(value.trim().length() > 0) {
			valueMandatory = false;
			valueStyle = "width:250px;";
		}
		else {
			valueMandatory = true;
			valueStyle = "width:250px; border:1px solid red;";
		}
		
		coupleMCLabelError = false;
		for(StaticDataDefinitionBean staticData : staticDatas) {
			if(onEdit && staticData.getIkrDefinition().getIkrInstance().equalsIgnoreCase(ikrInstanceOnEdit)
					&& staticData.getIkrCategory().getLabel().equalsIgnoreCase(getLabel()))
				continue;
			else {
				if(staticData.getIkrDefinition().getIkrInstance().equalsIgnoreCase(ikrInstance)
						&& staticData.getIkrCategory().getLabel().equalsIgnoreCase(getLabel())) {
					coupleMCLabelError = true;
					labelStyle = "width:250px; border:1px solid red;";
					metricCategoryStyle = "width:250px; border:1px solid red;";
					metricCategoryStyle2 = "color: red;";
					break;
				}
			}
		}
		if(!coupleMCLabelError && !labelMandatory) {
			labelStyle = "width:250px;";
		}
		if(!coupleMCLabelError && !metricCategoryMandatory) {
			metricCategoryStyle = "width:250px;";
			metricCategoryStyle2 = "";
		}
	}
	
	public void resetControlStyle() {
		metricCategoryMandatory = coupleMCLabelError = labelMandatory = valueMandatory = onEdit = false;
		metricCategoryStyle = labelStyle = valueStyle = "width: 250px;";
		metricCategoryStyle2 = "";
	}
	
	public void delete(StaticDataDefinitionBean staticData) {
		if (staticData != null) {
			try {
				monitoringPM.deleteIkrDefinition(staticData.getId());
			} catch(Exception exc) {
				logger.error(exc);
			}
			
			reloadStaticDataDefinitions();
			selectAll = false;
		}
	}
	
	public Collection<StaticDataDefinitionBean> getStaticDatas() {
		filter();
		if (staticDatas != null && staticDatas.size()>0)
			sort();
		return staticDatas;
	}
	
	public void reloadStaticDataDefinitions() {
		staticDatas = new ArrayList<StaticDataDefinitionBean>();
		try {			
			logicalEnvSelectionBean.accept(this);
			
			int logicalEnvId = 0;
			if (logicalEnvSelectionBean.getLogicalEnv()!=null)
				logicalEnvId = logicalEnvSelectionBean.getLogicalEnv().getId();
			
			Map<Long,AbstractIkrDefinition> staticDataDefinitionMap = monitoringPM.getStaticDataDefinitions(logicalEnvId);
			for (AbstractIkrDefinition ikrDefinition : staticDataDefinitionMap.values()) {	
				StaticData sd = (StaticData)ikrDefinition;
				IkrCategory metricCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(ikrDefinition.getIkrCategoryId());
				IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(metricCategory.getParentDomainId());
				LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(sd.getLogicalEnvId());
				staticDatas.add(new StaticDataDefinitionBean(logicalEnv, ikrDefinition, metricDomain, metricCategory));
			}
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
	}
	
	public boolean isUpdate() {
		return staticData.getId() != 0;
	}
	
	public String getLabel() {
		return ikrCategory.getLabel();
	}

	public void setLabel(String label) {
		ikrCategory.setDomainValue("STATIC DATA - " + label);
		ikrCategory.setLabel(label);
	}
	
	public String getDescription() {
		return ikrCategory.getDescription();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIkrInstance() {
		return ikrInstance;
	}

	public void setIkrInstance(String ikrInstance) {
		this.ikrInstance = ikrInstance;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSearchIndexesOld() {
		return searchIndexesOld;
	}

	public void setSearchIndexesOld(String searchIndexesOld) {
		this.searchIndexesOld = searchIndexesOld;
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

	public boolean isNewMetricCategory() {
		return newMetricCategory;
	}

	public void setNewMetricCategory(boolean newMetricCategory) {
		this.newMetricCategory = newMetricCategory;
	}
	
	public boolean isListRendered() {
		return staticDatas.size()>0;
	}
	
	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}
	
	public boolean isPaginationVisible() {
		if (staticDatas.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}
	
	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		selectedStaticDataDefinitionBean = staticDatas.get(rowId);
	}

	public StaticDataDefinitionBean getSelectedStaticDataDefinitionBean() {
		return selectedStaticDataDefinitionBean;
	}
	
	public void setSelectedStaticDataDefinitionBean(
			StaticDataDefinitionBean selectedStaticDataDefinitionBean) {
		this.selectedStaticDataDefinitionBean = selectedStaticDataDefinitionBean;
	}

	public String getDeleteMessage() {
		int numberSDSelected = 0;
		String message = "No static data definition selected";
		for (StaticDataDefinitionBean staticData : staticDatas) {
			if (staticData.isSelected()){
				numberSDSelected++;
			}
		}
		if (numberSDSelected == 1) {
			for (StaticDataDefinitionBean staticData : staticDatas) {
				if (staticData.isSelected()){
					message = "Are you sure to delete this Static Data : " + staticData.getIkrDefinition().getIkrInstance();
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberSDSelected + " Static Datas?";
			return message;
		}
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}

	public String getSearchIndexes() {
		return searchIndexes;
	}

	public void setSearchIndexes(String searchIndexes) {
		this.searchIndexes = searchIndexes;
	}	
	
	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public boolean isWellSaved() {
		return wellSaved;
	}
	
	public void handleSelectedSD(ValueChangeEvent event) {
		StaticDataDefinitionBean StaticDataDefinition = (StaticDataDefinitionBean)event.getComponent().getAttributes().get("StaticDataDefinition");
		if(StaticDataDefinition != null) {
			for(StaticDataDefinitionBean staticData : staticDatas) {
				if(staticData.equals(StaticDataDefinition)) {
					staticData.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						staticDatasSelected.add(StaticDataDefinition);
					else
						staticDatasSelected.remove(StaticDataDefinition);
				}
			}
		}
	}
	
	public void handleSelectAllSD(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		staticDatasSelected.clear();
		for(StaticDataDefinitionBean staticData : staticDatas) {
			staticData.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				staticDatasSelected.add(staticData);
		}
	}
	
	public void handleDeleteNoSelection(StaticDataDefinitionBean StaticDataDefinition) {
		if(StaticDataDefinition != null)
			staticDatasSelected.remove(StaticDataDefinition);
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getStaticDatasSelected() {
		int size = staticDatasSelected.size();
		return size;
	}

	public void setStaticDatasSelected(
			List<StaticDataDefinitionBean> staticDatasSelected) {
		this.staticDatasSelected = staticDatasSelected;
	}

	public boolean isOnEdit() {
		return onEdit;
	}

	public void setOnEdit(boolean onEdit) {
		this.onEdit = onEdit;
	}
	
	//-------------Control and style---------------//

	public boolean isMetricCategoryMandatory() {
		return metricCategoryMandatory;
	}

	public boolean isLabelMandatory() {
		return labelMandatory;
	}

	public boolean isCoupleMCLabelError() {
		return coupleMCLabelError;
	}

	public boolean isValueMandatory() {
		return valueMandatory;
	}

	public String getMetricCategoryStyle() {
		return metricCategoryStyle;
	}

	public String getMetricCategoryStyle2() {
		return metricCategoryStyle2;
	}

	public String getLabelStyle() {
		return labelStyle;
	}

	public String getValueStyle() {
		return valueStyle;
	}
}
