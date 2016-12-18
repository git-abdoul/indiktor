package com.fsi.monitoring.datamodel.ikrStaticDomain.metricDomainConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.connector.ConnectorType;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorVisitor;
import com.fsi.monitoring.ikr.model.DataFrequency;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.model.MetricDomainConfigExtension;
import com.fsi.monitoring.ikr.model.MetricDomainConfigField;
import com.fsi.monitoring.ikr.model.MetricDomainConfigFieldType;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlCommandButton;

public class MetricDomainConfigCreationBean
extends AccessControlBean
implements IkrStaticDomainSelectorVisitor, Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(MetricDomainConfigCreationBean.class);
	
	private MetricDomainConfig metricDomainConfig;
	
	private List<String> connectorTypes;
	private List<String> availableConnectorTypes;
	private List<MetricDomainConfigField> fields;
	private List<String> businessItems;
	private List<MetricDomainConfigExtensionBean> extensionConfigs;
	private Map<Integer, MetricDomainResource> availableResources;
	private List<MetricDomainConfigResource> resources;
	
	private boolean descriptionMandatory = false;
	private boolean descriptionError = false;
	private boolean classNameMandatory = false;
	private boolean classNameError = false;
	private boolean resourceMandatory = false;
	private boolean connectorMandatory = false;
	private String descriptionStyle = "width: 400px;";
	private String classNameStyle = "width: 400px;";
	private String resourceStyle = "border: 1px solid #336699; height: 175px; width: 345px; text-align: center;";
	private String connectorStyle = "border: 1px solid #336699; height: 140px; width: 345px;  text-align: center;";
	private boolean keyMandatory = false;
	private boolean keyError = false;
	private boolean labelMandatory = false;
	private boolean labelAlreadyUsed = false;
	private String keyStyle = "font-size: 10px; width: 200px;";
	private String labelStyle = "font-size: 10px; width: 200px;";
	private boolean typeValueMandatory = false;
	private boolean typeValueError = false;
	private boolean noTypeValue = false;
	private String typeValueStyle = "font-size: 10px; width: 160px;";
	private boolean businessItemMandatory = false;
	private boolean businessItemError = false;
	private String businessItemStyle = "font-size: 10px; width: 180px;";

	private List<SelectItem> metricDomainConfigItems = null;
	private Map<Integer,MetricDomainConfig> metricDomainConfigMap = null;

	private UICommand deleteCommand = null;	
	
	private MetricDomainConfigField configFieldToAdd;
	private String businessItemToAdd;
	
	private MetricDomainConfigExtension extensionToAdd;
	
	private boolean rendererMetricDomainConfig = false;
	
	int numberMDCSelected = 0;
	private List<MetricDomainConfigBean> metricDomainConfigBeans;
	
	private boolean rendererConnectorTypeConfig = false;
	private String selectedConnectorType;
	private int selectedResource;
	
	private Map<String, MetricDomainConfigField> dataSynchronizationFields;
	
//	private boolean renderFieldTypeValues;
	private String fieldTypeToAdd;
	
	public MetricDomainConfigCreationBean() {
		deleteCommand = new HtmlCommandButton();	
	}
	
	private void initDataSynchronizationFields() {
		dataSynchronizationFields = new HashMap<String, MetricDomainConfigField>();
//		dataSynchronizationFields.put("REALTIME_DATA", new MetricDomainConfigField(0, metricDomainConfig.getId(), "REALTIME_DATA", "Realtime Data", true, MetricDomainConfigFieldType.selectBooleanCheckbox.name(), new ArrayList<String>()));
		dataSynchronizationFields.put("DATA_SYNCHRONIZATION", new MetricDomainConfigField(0, metricDomainConfig.getId(), "DATA_SYNCHRONIZATION", "Synchronize", true, MetricDomainConfigFieldType.selectBooleanCheckbox.name(), new ArrayList<String>()));
		List<String> frequencyDatas = new ArrayList<String>();
		for (DataFrequency value : DataFrequency.values()) {
			frequencyDatas.add(value.name());
		}
		dataSynchronizationFields.put("STAT_FREQUENCY", new MetricDomainConfigField(0, metricDomainConfig.getId(), "STAT_FREQUENCY", "Stat Frequency", true, MetricDomainConfigFieldType.selectOneMenu.name(), frequencyDatas));

	}
	
	public void initForCreate(ActionEvent action) {
		if (!isAuthorized(129,"createMetricDomainConfig")) {
			setAccessDenied();
			return;
		}
		metricDomainConfig = new MetricDomainConfig();				
		try {
			init();
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}			
		deleteCommand.setRendered(false);
		rendererMetricDomainConfig = true;
	}
	
	public void initForUpdate(ActionEvent action) {
		if (!isAuthorized(128, "")) {
			setAccessDenied();
			return;
		}
		MetricDomainConfigBean metricDomainConfigBeanSelected = (MetricDomainConfigBean)action.getComponent().getAttributes().get("metricDomainConfigBean");
		try {				
			metricDomainConfig = metricDomainConfigBeanSelected.getMetricDomainConfig();			
			init();						
			deleteCommand.setRendered(true);
    	} catch(Exception exc) {
    		logger.error(exc.getMessage(), exc);
    	}
    	rendererMetricDomainConfig = true;
		
//		MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
//		metricDomainConfigBeans = metricDomainConfigSelectionBean.getMetricDomainConfigBeans();
//		MetricDomainConfigBean selectedmetricDomainConfigBean = metricDomainConfigSelectionBean.getSelectedMetricDomainConfigBean();
//		numberMDCSelected = 0;
//		for (MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
//			if (metricDomainConfigBean.isSelected()){
//				numberMDCSelected++;
//			}
//		}
//		if (numberMDCSelected < 2) {
//			if (numberMDCSelected == 1) {
//				try {
////					DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());				
//					metricDomainConfig = selectedmetricDomainConfigBean.getMetricDomainConfig();			
//					init();						
//					deleteCommand.setRendered(true);
//		    	} catch(Exception exc) {
//		    		logger.error(exc.getMessage(), exc);
//		    	}
//		    	rendererMetricDomainConfig = true;
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No metric domain config has been selected");
//			}
//		}
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one metric domain config to edit");
//		}
	}	
	
	private void init() throws PersistenceException {
		connectorTypes = new ArrayList<String>();
		fields = new ArrayList<MetricDomainConfigField>();
		businessItems = new ArrayList<String>();
		extensionConfigs = new ArrayList<MetricDomainConfigExtensionBean>(50);
		configFieldToAdd = new MetricDomainConfigField(0, metricDomainConfig.getId(), "", "", true, MetricDomainConfigFieldType.inputText.name(), new ArrayList<String>());
		extensionToAdd = new MetricDomainConfigExtension(metricDomainConfig.getId());
		resources = new ArrayList<MetricDomainConfigResource>();
		businessItemToAdd = "";
		
		initDataSynchronizationFields();
		
		availableConnectorTypes = new ArrayList<String>();
		for (ConnectorType type : ConnectorType.values()) {
			availableConnectorTypes.add(type.name());
		}
		
		availableResources = new HashMap<Integer, MetricDomainResource>();
		
		if (metricDomainConfig.getId() > 0) {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			List<MetricDomainResource> myResources = dataModelPM.getMetricDomainResources(metricDomainConfig.getIkrStaticDomainId());
			for (MetricDomainResource res : myResources) {
				availableResources.put(res.getId(), res);
			}
			
			for (MetricDomainConfigResource configResource : metricDomainConfig.getResources()) {
				resources.add(configResource);
				availableResources.remove(configResource.getResource().getId());
			}
			
			for(String type : metricDomainConfig.getConnectorTypes()) {
				connectorTypes.add(type);
				availableConnectorTypes.remove(type);
			}
			
			for(MetricDomainConfigField configField : metricDomainConfig.getFields()) {
				fields.add(configField);
			}
			
			for(String item : metricDomainConfig.getDomainItemConfigs()) {
				businessItems.add(item);
			}
			
			for(MetricDomainConfigExtension extensionConfig : metricDomainConfig.getExtensionConfigs()) {
				extensionConfigs.add(extensionConfig.getPriority()-1, new MetricDomainConfigExtensionBean(extensionConfig));
			}
		}
		
		IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");
		ikrStaticDomainSelectorBean.initComponent(false, true, true, true);
		ikrStaticDomainSelectorBean.accept(this);
		if (metricDomainConfig.getIkrStaticDomainId() != 0) {
			ikrStaticDomainSelectorBean.initMetricDomainId(metricDomainConfig.getIkrStaticDomainId());
		} else {
			ikrStaticDomainSelectorBean.initItems();
		}
	}

	public MetricDomainConfig getMetricDomainConfig() {
		return metricDomainConfig;
	}	
	
	public boolean isUpdate() {
		return metricDomainConfig.getId() != 0;
	}
	
	public void changeMetricDomain(int metricDomainId) {
		availableResources = new HashMap<Integer, MetricDomainResource>();
		metricDomainConfig.setIkrStaticDomainId(metricDomainId);
		initMetricDomainConfigItems(metricDomainId);
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		List<MetricDomainResource> myResources;
		try {
			myResources = dataModelPM.getMetricDomainResources(metricDomainId);
			for (MetricDomainResource res : myResources) {
				availableResources.put(res.getId(), res);
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		
	}
	
	public void changeMetricGroup(int metricGroupId) {}
	
	public Collection<SelectItem> getMetricDomainConfigItems() {
		return metricDomainConfigItems;
	}		
	
	public UICommand getDeleteCommand() {
		return deleteCommand;
	}	
	
	public void setDeleteCommand(UICommand removeCommand) {}

	public List<String> getConnectorTypes() {
		return connectorTypes;
	}
	
//	public String getExtendedDescription() {
//		String res = null;
//		if (metricDomainConfig != null) {
//			MetricDomainConfig extended = metricDomainConfig.getExtendedConfig();
//			res = extended == null ? "none" : extended.getDescription();
//		}
//		return res;
//	}
//	
//	public boolean isRenderedExtendedSelection() {
//		if (metricDomainConfig != null) {
//			return metricDomainConfig.getId() == 0;
//		} 
//		return false;
//	}
//	
//	public int getMetricDomainConfigExtendedId() {
//		int res = 0;
//		MetricDomainConfig configExtended = metricDomainConfig.getExtendedConfig();
//		if (configExtended != null) {
//			res = configExtended.getId();
//		}
//		return res;
//	}
//	
//	public void setMetricDomainConfigExtendedId(int id) {
//		if (id != 0) {
//			MetricDomainConfig config = metricDomainConfigMap.get(id);
//			metricDomainConfig.setExtendedConfig(config);
//		}
//	}	
	
//	public void setConnectorTypes(String types) {
//		metricDomainConfig.setConnectorType(Arrays.asList(types.split(",")));
//	}	
	
	private void updateConfig() {
		metricDomainConfig.setDomainItemConfigs(businessItems);
		metricDomainConfig.setFields(fields);
		metricDomainConfig.setExtendedConfig(new ArrayList<MetricDomainConfigExtension>());
		for (MetricDomainConfigExtensionBean bean : extensionConfigs) {
			metricDomainConfig.addExtension(bean.getExtensionConfig());			
		}		
		metricDomainConfig.setConnectorType(connectorTypes);
		metricDomainConfig.setResources(resources);
	}
	
	public void save(ActionEvent event) {
		if (isAuthorized(128,"metricDomainConfigSelection") || isAuthorized(129,"metricDomainConfigSelection")) {
			testFields();
			if(descriptionError || descriptionMandatory || classNameError || classNameMandatory || resourceMandatory || connectorMandatory)
				return;
			
			try {
				updateConfig();
				
				DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
				int id = metricDomainConfig.getId();			
				if (id == 0) {
					dataModelPM.addMetricDomainConfig(metricDomainConfig);
				} else {
					dataModelPM.updateMetricDomainConfig(metricDomainConfig);
				}
				
				MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
				metricDomainConfigSelectionBean.reload();
				
			} catch(Exception exc) {
				logger.error(exc.getMessage(), exc);
			}	
			resetControlStyle();
		}
		rendererMetricDomainConfig = false;
		MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
		metricDomainConfigSelectionBean.setSelectAll(false);
		metricDomainConfigSelectionBean.setMetricDomainConfigBeansSelected(new ArrayList<MetricDomainConfigBean>());
	}
	
	public void testFields() {
		if(metricDomainConfig.getDescription().trim() == null || metricDomainConfig.getDescription().trim().length() == 0) {
			descriptionMandatory = true;
			descriptionStyle = "width: 400px; border: 1px red solid;";
		}
		else {
			descriptionMandatory = false;
			descriptionStyle = "width: 400px;";
		}

		if(metricDomainConfig.getClassName().trim() == null || metricDomainConfig.getClassName().trim().length() == 0) {
			classNameMandatory = true;
			classNameStyle = "width: 400px; border: 1px red solid;";
		}
		else {
			classNameMandatory = false;
			classNameStyle = "width: 400px;";
		}
		
		descriptionError = classNameError = false;
		MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
		if(metricDomainConfigSelectionBean != null) {
			metricDomainConfigBeans = metricDomainConfigSelectionBean.getMetricDomainConfigBeans();
			for(MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
				if(metricDomainConfigBean.getMetricDomainConfig().getDescription().trim().equalsIgnoreCase(metricDomainConfig.getDescription().trim())
						&& metricDomainConfigBean.getMetricDomainConfig().getId() != metricDomainConfig.getId()) {
					descriptionError = true;
					descriptionStyle = "width: 400px; border: 1px red solid;";
				}
				if(metricDomainConfigBean.getMetricDomainConfig().getClassName().trim().equalsIgnoreCase(metricDomainConfig.getClassName().trim())
						&& metricDomainConfigBean.getMetricDomainConfig().getId() != metricDomainConfig.getId()) {
					classNameError = true;
					classNameStyle = "width: 400px; border: 1px red solid;";
				}
			}
		}
		
		if(!descriptionError && !descriptionMandatory)
			descriptionStyle = "width: 400px;";
		if(!classNameError && !classNameMandatory)
			classNameStyle = "width: 400px;";
		
		if(!(resources.size() > 0)) {
			resourceMandatory = true;
			resourceStyle = "border: 1px solid red; height: 175px; width: 345px; text-align: center;";
		}
		else {
			resourceMandatory = false;
			resourceStyle = "border: 1px solid #336699; height: 175px; width: 345px; text-align: center;";
		}
		
		if(!(connectorTypes.size() > 0)) {
			connectorMandatory = true;
			connectorStyle = "border: 1px solid red; height: 140px; width: 345px;  text-align: center;";
		}
		else {
			connectorMandatory = false;
			connectorStyle = "border: 1px solid #336699; height: 140px; width: 345px;  text-align: center;";
		}
	}
	
	public void resetControlStyle() {
		descriptionError = descriptionMandatory = classNameError = classNameMandatory = resourceMandatory = connectorMandatory = false;
		descriptionStyle = classNameStyle = "width: 400px;";
		resourceStyle = "border: 1px solid #336699; height: 175px; width: 345px; text-align: center;";
		connectorStyle = "border: 1px solid #336699; height: 140px; width: 345px;  text-align: center;";
		keyMandatory = keyError = labelAlreadyUsed = labelMandatory = typeValueError = typeValueMandatory = false;
		keyStyle = "font-size: 10px; width: 200px;";
		labelStyle = "font-size: 10px; width: 200px;";
		typeValueStyle = "font-size: 10px; width: 160px;";
	}
	
	public void cancel(ActionEvent event) {
		MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
		metricDomainConfigSelectionBean.reload();
	}	
	
	public void deleteMetricDomainConfigBean(ActionEvent event) {
		if (!isAuthorized(129,"metricDomainConfigSelection")) {	
			setAccessDenied();
			return;
		}
		
		MetricDomainConfigBean metricDomainConfigBeanSelected = (MetricDomainConfigBean)event.getComponent().getAttributes().get("metricDomainConfigBean");
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			int configId = metricDomainConfigBeanSelected.getMetricDomainConfig().getId();			
			dataModelPM.removeMetricDomainConfig(configId);
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
		MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
		metricDomainConfigSelectionBean.reload();
		metricDomainConfigSelectionBean.setSelectedMetricDomainConfigBean(null);
		metricDomainConfigSelectionBean.setSelectAll(false);
		metricDomainConfigSelectionBean.setMetricDomainConfigBeansSelected(new ArrayList<MetricDomainConfigBean>());
	}
	
	public void deleteSelectedMetricDomainConfigBeans(ActionEvent event) {
		if (!isAuthorized(129,"metricDomainConfigSelection")) {	
			setAccessDenied();
			return;
		}
		
		MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
		metricDomainConfigBeans = metricDomainConfigSelectionBean.getMetricDomainConfigBeans();
		for (MetricDomainConfigBean metricDomainConfigBean : metricDomainConfigBeans) {
			if (metricDomainConfigBean.isSelected()){
				try {
					DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
					int configId = metricDomainConfigBean.getMetricDomainConfig().getId();			
					dataModelPM.removeMetricDomainConfig(configId);
				} catch(Exception exc) {
					logger.error(exc.getMessage(), exc);
				}
			}
		}
		metricDomainConfigSelectionBean.reload();
		metricDomainConfigSelectionBean.setSelectedMetricDomainConfigBean(null);
		metricDomainConfigSelectionBean.setSelectAll(false);
		metricDomainConfigSelectionBean.setMetricDomainConfigBeansSelected(new ArrayList<MetricDomainConfigBean>());
	}
	
//	public Collection<SelectItem> getMetricDomainConfigExtendedItems() {
//		Collection<SelectItem> res = new ArrayList<SelectItem>();
//		
//		SelectItem noneItem = new SelectItem(0,"None");
//		res.add(noneItem);
//		
//		for (SelectItem item : metricDomainConfigItems) {
//			int configId = (Integer)item.getValue();
//			MetricDomainConfig config = metricDomainConfigMap.get(configId);
//			if (config.getExtendedConfig() == null) {
//				res.add(item);
//			}
//		}
//		
//		return res;
//	}	
	
	private void initMetricDomainConfigItems(int metricDomainId) {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			List<MetricDomainConfig> metricDomainConfigs = dataModelPM.getMetricDomainConfigs(metricDomainId);
		
			metricDomainConfigItems = new ArrayList<SelectItem>(metricDomainConfigs.size());
			metricDomainConfigMap = new HashMap<Integer,MetricDomainConfig>();
			
			for (MetricDomainConfig metricDomainConfig: metricDomainConfigs) {
				SelectItem item = new SelectItem(metricDomainConfig.getId(), metricDomainConfig.getDescription());
				metricDomainConfigItems.add(item);
				metricDomainConfigMap.put(metricDomainConfig.getId(), metricDomainConfig);				
//				MetricDomainConfig extendedConfig = metricDomainConfig.getExtendedConfig();
//				if (extendedConfig != null) {
//					item = new SelectItem(extendedConfig.getId(), extendedConfig.getDescription());
//					metricDomainConfigItems.add(item);
//					metricDomainConfigMap.put(extendedConfig.getId(), extendedConfig);
//				}
			}
		} catch(Exception exc) {
			Log.error(exc);
		}
	}
	
	public void removeAttribute(ActionEvent action) {
		int idToRemove =(Integer)action.getComponent().getAttributes().get("attrId");
		String name = (String)action.getComponent().getAttributes().get("attrName");
		Iterator<MetricDomainConfigField> iterator = fields.iterator();
		while (iterator.hasNext()) {
			MetricDomainConfigField attribute = iterator.next();
			if (attribute.getName().equals(name) && attribute.getId() == idToRemove) {
				iterator.remove();
			}
		}		
	}
	
	public void editAttribute(ActionEvent action) {
		int idToRemove =(Integer)action.getComponent().getAttributes().get("attrId");
		String name = (String)action.getComponent().getAttributes().get("attrName");
		Iterator<MetricDomainConfigField> iterator = fields.iterator();
		while (iterator.hasNext()) {
			MetricDomainConfigField attribute = iterator.next();
			if (attribute.getName().equals(name) && attribute.getId() == idToRemove) {
				iterator.remove();
				configFieldToAdd = attribute;					
			}
		}		
	}
	
	public void addNewAttribute(ActionEvent action) {
		testAttributeFields();
		if(keyError || keyMandatory || labelMandatory || labelAlreadyUsed)
			return;
		
		if(configFieldToAdd.getFieldType().toString().equalsIgnoreCase("selectOneMenu") && noTypeValue)
			return;
		
		MetricDomainConfigField attribute = new MetricDomainConfigField(0, metricDomainConfig.getId(), configFieldToAdd.getName(), configFieldToAdd.getLabel(), configFieldToAdd.isEnable(),configFieldToAdd.getFieldType(),configFieldToAdd.getFieldTypeValues());
		fields.add(attribute);
		configFieldToAdd = new MetricDomainConfigField(0, 0, "", "", true, MetricDomainConfigFieldType.inputText.name(), new ArrayList<String>());
	}	
	
	public void addAnyway(ActionEvent action) {
		if(keyError || keyMandatory || labelMandatory)
			return;
		
		if(configFieldToAdd.getFieldType().toString().equalsIgnoreCase("selectOneMenu") && noTypeValue)
			return;
		
		MetricDomainConfigField attribute = new MetricDomainConfigField(0, metricDomainConfig.getId(), configFieldToAdd.getName(), configFieldToAdd.getLabel(), configFieldToAdd.isEnable(),configFieldToAdd.getFieldType(),configFieldToAdd.getFieldTypeValues());
		fields.add(attribute);
		configFieldToAdd = new MetricDomainConfigField(0, 0, "", "", true, MetricDomainConfigFieldType.inputText.name(), new ArrayList<String>());
		labelAlreadyUsed = false;
		labelStyle = "font-size: 10px; width: 200px;";
	}
	
	public void testAttributeFields() {
		if(configFieldToAdd.getName().trim() == null || configFieldToAdd.getName().trim().length() == 0) {
			keyMandatory = true;
			keyStyle = "font-size: 10px; width: 200px; border: 1px red solid;";
		}
		else {
			keyMandatory = false;
			keyStyle = "font-size: 10px; width: 200px;";
		}
		
		if(configFieldToAdd.getLabel().trim() == null || configFieldToAdd.getLabel().trim().length() == 0) {
			labelMandatory = true;
			labelStyle = "font-size: 10px; width: 200px; border: 1px red solid;";
		}
		else {
			labelMandatory = false;
			labelStyle = "font-size: 10px; width: 200px;";
		}
		
		keyError = labelAlreadyUsed = false;
		for(MetricDomainConfigField attribute : fields) {
			if(attribute.getName().trim().equalsIgnoreCase(configFieldToAdd.getName().trim())) {
				keyError = true;
				keyStyle = "font-size: 10px; width: 200px; border: 1px red solid;";
			}
			if(attribute.getLabel().trim().equalsIgnoreCase(configFieldToAdd.getLabel().trim())) {
				labelAlreadyUsed = true;
			}
		}
		
		if(keyError || keyMandatory || labelMandatory) {
			labelAlreadyUsed = false;
		}
		else
			labelStyle = "font-size: 10px; width: 200px; border: 1px orange solid;";
		
		if(!keyError && !keyMandatory)
			keyStyle = "font-size: 10px; width: 200px;";
		if(!labelAlreadyUsed && !labelMandatory)
			labelStyle = "font-size: 10px; width: 200px;";
		

		List<String> fieldTypeValues = configFieldToAdd.getFieldTypeValues();
		if(!(fieldTypeValues.size() > 0)) {
			noTypeValue = true;
			typeValueStyle = "font-size: 10px; width: 160px; border: 1px red solid;";
		}
		else {
			noTypeValue = false;
			typeValueStyle = "font-size: 10px; width: 160px;";
		}
	}	
	
	public void removeResource(ActionEvent action) {
		String resourceName = (String)action.getComponent().getAttributes().get("resourceName");
		Iterator<MetricDomainConfigResource> iterator = resources.iterator();
		while (iterator.hasNext()) {
			MetricDomainConfigResource resource = iterator.next();
			if (resource.getResource().getResourceName().equals(resourceName)) {
				iterator.remove();
				availableResources.put(resource.getResource().getId(), resource.getResource());
			}
		}		
	}
	
	public void addNewFieldTypeValue(ActionEvent action) {
		testFieldsTypeValue();
		if(typeValueError || typeValueMandatory)
			return;
		
		if (fieldTypeToAdd!=null&&fieldTypeToAdd.length()>0) {
			configFieldToAdd.getFieldTypeValues().add(fieldTypeToAdd);
		}
		fieldTypeToAdd = "";
	}
	
	public void testFieldsTypeValue() {
		if(fieldTypeToAdd.trim() == null || fieldTypeToAdd.trim().length() == 0) {
			typeValueMandatory = true;
			typeValueStyle = "font-size: 10px; width: 160px; border: 1px red solid;";
		}
		else {
			typeValueMandatory = false;
			typeValueStyle = "font-size: 10px; width: 160px;";
		}
		
		typeValueError = false;
		List<String> fieldTypeValues = configFieldToAdd.getFieldTypeValues();		
		for(String fieldTypeValue : fieldTypeValues) {
			if(fieldTypeValue.trim().equalsIgnoreCase(fieldTypeToAdd.trim())) {
				typeValueError = true;
				typeValueStyle = "font-size: 10px; width: 160px; border: 1px red solid;";
			}
		}
		
		if(!typeValueError && !typeValueMandatory)
			typeValueStyle = "font-size: 10px; width: 160px;";
		
		noTypeValue = false;
	}
	
	public void removeFieldTypeValue(ActionEvent action) {
		String fieldTypeValueToRemove = (String)action.getComponent().getAttributes().get("fieldTypeValueToRemove");
		Iterator<String> iterator = configFieldToAdd.getFieldTypeValues().iterator();
		while (iterator.hasNext()) {
			String value = iterator.next();
			if (value.equals(fieldTypeValueToRemove)) {
				iterator.remove();
			}
		}		
	}
	
	public void addNewResource(ActionEvent action) {
		MetricDomainResource resource = availableResources.remove(selectedResource);
		MetricDomainConfigResource configResource = new MetricDomainConfigResource(metricDomainConfig.getIkrStaticDomainId(), resource, true);
		resources.add(configResource);
		testFields();
	}
	
	public void onChangeResourceValue(ValueChangeEvent evt) {
		selectedResource = (Integer)evt.getNewValue();
	}
	
	public void addNewExtensionConfig(ActionEvent action) {
		int priority = extensionConfigs.size()+1;
		MetricDomainConfigExtension extensionConf = new MetricDomainConfigExtension( metricDomainConfig.getId(), priority, extensionToAdd.getDescription(), extensionToAdd.getClassName());
		extensionConfigs.add(priority-1, new MetricDomainConfigExtensionBean(extensionConf));
		extensionToAdd = new MetricDomainConfigExtension( metricDomainConfig.getId());
	}	
	
	public void removeExtensionConfig(ActionEvent action) {
		String description =(String)action.getComponent().getAttributes().get("description");
		String className = (String)action.getComponent().getAttributes().get("className");
		Iterator<MetricDomainConfigExtensionBean> iterator = extensionConfigs.iterator();
		while (iterator.hasNext()) {
			MetricDomainConfigExtensionBean extensionConf = iterator.next();
			if (extensionConf.getExtensionConfig().getDescription().equals(description) && extensionConf.getExtensionConfig().getClassName().equals(className)) {
				iterator.remove();
			}
		}
		
		for (int i=0; i<extensionConfigs.size(); i++) {
			MetricDomainConfigExtensionBean bean = extensionConfigs.get(i);
			bean.getExtensionConfig().setPriority(i+1);
		}
	}
	
	public void upExtensionConfigPriority(ActionEvent event) {
		int i = 0;
		for(MetricDomainConfigExtensionBean item : extensionConfigs) {
			if (item.isSelected()) {
//				item.setSelected(false);
				if (i>0){
					MetricDomainConfigExtensionBean previousItem = extensionConfigs.get(i-1);
					int currentPriority = item.getExtensionConfig().getPriority();
					int previousPriority = previousItem.getExtensionConfig().getPriority();					
					previousItem.getExtensionConfig().setPriority(currentPriority);
					item.getExtensionConfig().setPriority(previousPriority);
					extensionConfigs.add(i-1, item);
					extensionConfigs.remove(i+1);
				}
				break;
			}
			i++;
		}
	}
	
	public void downExtensionConfigPriority(ActionEvent event) {
		int i = 0;
		for(MetricDomainConfigExtensionBean item : extensionConfigs) {
			if (item.isSelected()) {				
				if (i<extensionConfigs.size()-1) {					
					MetricDomainConfigExtension extensionConfig = item.getExtensionConfig();
					MetricDomainConfigExtensionBean nextItem = extensionConfigs.get(i+1);
					int currentPriority = item.getExtensionConfig().getPriority();
					int nextPriority = nextItem.getExtensionConfig().getPriority();
					nextItem.getExtensionConfig().setPriority(currentPriority);	
					extensionConfig.setPriority(nextPriority);
					extensionConfigs.remove(item);
					MetricDomainConfigExtensionBean newItem = new MetricDomainConfigExtensionBean(extensionConfig);
					newItem.setSelected(true);
					extensionConfigs.add(i+1, newItem);					
				}
				break;			
			}
			i++;
		}
	}
	
	public void removeBusinessItem(ActionEvent action) {
		String itemToRemove = (String)action.getComponent().getAttributes().get("itemName");
		Iterator<String> iterator = businessItems.iterator();
		while (iterator.hasNext()) {
			String current = iterator.next();
			if (current.equals(itemToRemove)) {
				iterator.remove();
			}
		}
	}	
	
	public void removeConnectorType(ActionEvent action) {
		String type = (String)action.getComponent().getAttributes().get("connectorType");
		Iterator<String> iterator = connectorTypes.iterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (type.equals(next)) {
				iterator.remove();
				availableConnectorTypes.add(next);
			}
		}		
	}
	
//	public void addNewConnectorType(ActionEvent action) {
//		rendererConnectorTypeConfig = true;
//	}
	
	public void validateNewConnectorType(ActionEvent action) {
		connectorTypes.add(selectedConnectorType);
		availableConnectorTypes.remove(selectedConnectorType);
//		rendererConnectorTypeConfig = false;
		testFields();
	}
	
//	public void cancelAddConnectorType(ActionEvent action) {
//		rendererConnectorTypeConfig = false;
//	}
	
	public void onChangeConnectorTypeValue(ValueChangeEvent evt) {
		selectedConnectorType = (String)evt.getNewValue();
	}
	
	public void extensionConfigCheckboxValueChanged(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
 			return;
 		}

		String description =(String)evt.getComponent().getAttributes().get("description");
		String className = (String)evt.getComponent().getAttributes().get("className");		
		boolean value = (Boolean)evt.getNewValue();
		if (value) {
			for (MetricDomainConfigExtensionBean bean : extensionConfigs) {
				if (bean.getExtensionConfig().getDescription().equals(description) && bean.getExtensionConfig().getClassName().equals(className)) {
					bean.setSelected(true);
				}
				else {
					bean.setSelected(false);
				}
			}
		}
	}
	
	public void useDataSynchronizationValueChanged(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
 			return;
 		}

		boolean value = (Boolean)evt.getNewValue();
		if (value) {
			for (MetricDomainConfigField field : dataSynchronizationFields.values()) {
				fields.add(field);
			}
		}
		else {
			Iterator<MetricDomainConfigField> iterator = fields.iterator();
			while (iterator.hasNext()) {
				MetricDomainConfigField attribute = iterator.next();
				if ("REALTIME_DATA".equals(attribute.getName()) || "DATA_SYNCHRONIZATION".equals(attribute.getName()) || "STAT_FREQUENCY".equals(attribute.getName())) {
					iterator.remove();
				}
			}		
		}
	}
	
	public void fieldTypeValueChanged(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
 			return;
 		}

		String value = (String)evt.getNewValue();
		if (MetricDomainConfigFieldType.inputText.name().equals(value)) {
			configFieldToAdd.setFieldTypeValues(new ArrayList<String>());
		}
		else if (MetricDomainConfigFieldType.selectBooleanCheckbox.name().equals(value)) {
			configFieldToAdd.setFieldTypeValues(new ArrayList<String>());
		}
		else if (MetricDomainConfigFieldType.selectOneMenu.name().equals(value)) {
		}
		else {
			configFieldToAdd.setFieldTypeValues(new ArrayList<String>());
		}
		configFieldToAdd.setFieldType(value);
	}
	
	public SelectItem[] getConnectorTypeSelectItems() {
		int sz = availableConnectorTypes.size();
		SelectItem[] connectorTypeSelectItems = new SelectItem[sz];
		int i = 0;
		for (String type : availableConnectorTypes) {
			connectorTypeSelectItems[i] = new SelectItem(type,type);	
			i++;
		}
		return connectorTypeSelectItems;
	}
	
	public SelectItem[] getResourceSelectItems() {
		int sz = availableResources.size();
		SelectItem[] resourceSelectItems = new SelectItem[sz];
		int i = 0;
		for (MetricDomainResource resource : availableResources.values()) {
			resourceSelectItems[i] = new SelectItem(resource.getId(),resource.getResourceName());	
			i++;
		}
		return resourceSelectItems;
	}
	
	public boolean isRenderOrderItem() {
		boolean isRendered = false;
		for (MetricDomainConfigExtensionBean bean : extensionConfigs) {
			if (bean.isSelected()) {
				isRendered=true;
				break;
			}
		}
		return isRendered;
	}
	
	public boolean isConnectorTypeListRenderered() {
		return connectorTypes.size()>0;
	}
	
	public boolean isResourceListRenderered() {
		return resources.size()>0;
	}
	
	public boolean isExtensionConfigListRenderered() {
		return extensionConfigs.size()>0;
	}
	
	public boolean isAttributeConfigListRenderered() {
		return fields.size()>0;
	}
	
	public boolean isBusinessItemsConfigListRenderered() {
		return businessItems.size()>0;
	}
	
	public boolean isRendererConnectorTypeConfig() {
		return availableConnectorTypes.size()>0;
	}
	
	public boolean isRendererResourceConfig() {
		return availableResources.size()>0;
	}
	
	public void addNewBusinessItem(ActionEvent action) {
		testFieldsBusinessItem();
		if(businessItemError || businessItemMandatory)
			return;
		
		String item = businessItemToAdd;
		businessItems.add(item);
		businessItemToAdd = "";
	}
	
	public void testFieldsBusinessItem() {
		if(businessItemToAdd.trim() == null || businessItemToAdd.trim().length() == 0) {
			businessItemMandatory = true;
			businessItemStyle = "font-size: 10px; width: 180px; border: 1px red solid;";
		}
		else {
			businessItemMandatory = false;
			businessItemStyle = "font-size: 10px; width: 180px;";
		}
		
		businessItemError = false;
		for(String businessItem : businessItems) {
			if(businessItem.trim().equalsIgnoreCase(businessItemToAdd.trim())) {
				businessItemError = true;
				businessItemStyle = "font-size: 10px; width: 180px; border: 1px red solid;";
			}
		}
		
		if(!businessItemMandatory && !businessItemError)
			businessItemStyle = "font-size: 10px; width: 180px;";
	}

	public List<MetricDomainConfigField> getFields() {
		Collections.sort(fields, new Comparator<MetricDomainConfigField>() {
			public int compare(MetricDomainConfigField o1, MetricDomainConfigField o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return fields;
	}
	
	public List<MetricDomainConfigResource> getResources() {
		Collections.sort(resources, new Comparator<MetricDomainConfigResource>() {
			public int compare(MetricDomainConfigResource o1, MetricDomainConfigResource o2) {
				return o1.getResource().getResourceName().compareTo(o2.getResource().getResourceName());
			}
		});
		return resources;
	}
	
	public List<MetricDomainResource> getAvailableResources() {
		List<MetricDomainResource> values = new ArrayList<MetricDomainResource>(availableResources.values());
		Collections.sort(values, new Comparator<MetricDomainResource>() {
			public int compare(MetricDomainResource o1, MetricDomainResource o2) {
				return o1.getResourceName().compareTo(o2.getResourceName());
			}
		});
		return values;
	}
	
	public List<MetricDomainConfigExtensionBean> getExtensionConfigs() {
		return extensionConfigs;
	}

	public Collection<String> getBusinessItems() {
		Collections.sort(businessItems, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return businessItems;
	}

	public String getSelectedConnectorType() {
		return selectedConnectorType;
	}

	public void setSelectedConnectorType(String selectedConnectorType) {
		this.selectedConnectorType = selectedConnectorType;
	}

	public MetricDomainConfigField getConfigFieldToAdd() {
		return configFieldToAdd;
	}

	public void setConfigFieldToAdd(MetricDomainConfigField configFieldToAdd) {
		this.configFieldToAdd = configFieldToAdd;
	}

	public String getBusinessItemToAdd() {
		return businessItemToAdd;
	}

	public MetricDomainConfigExtension getExtensionToAdd() {
		return extensionToAdd;
	}
	
	 public SelectItem[] getFieldTypeItems() {
		 SelectItem[] items = new SelectItem[MetricDomainConfigFieldType.values().length];
		 int i = 0;
		 for (MetricDomainConfigFieldType type : MetricDomainConfigFieldType.values()) {
			 items[i] = new SelectItem(type.name(), type.getLabel());
			 i++;
		 }		 
	     return items;
	 }

	public String getFieldTypeToAdd() {
		return fieldTypeToAdd;
	}

	public void setFieldTypeToAdd(String fieldTypeToAdd) {
		this.fieldTypeToAdd = fieldTypeToAdd;
	}

	public void setExtensionToAdd(MetricDomainConfigExtension extensionToAdd) {
		this.extensionToAdd = extensionToAdd;
	}

	public void setBusinessItemToAdd(String businessItemToAdd) {
		this.businessItemToAdd = businessItemToAdd;
	}
	
	public boolean isRendererMetricDomainConfig() {
		return rendererMetricDomainConfig;
	}
	
	public void closeMetricDomainConfigPopup(ActionEvent event) {
		MetricDomainConfigSelectionBean metricDomainConfigSelectionBean = (MetricDomainConfigSelectionBean)FacesUtils.getManagedBean("metricDomainConfigSelectionBean");
		metricDomainConfigSelectionBean.reload();
		metricDomainConfigSelectionBean.reloadSelected();
		resetControlStyle();
		rendererMetricDomainConfig = false;
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public int getNumberMDCSelected() {
		return numberMDCSelected;
	}
	
	public int getSelectedResource() {
		return selectedResource;
	}

	public void setSelectedResource(int selectedResource) {
		this.selectedResource = selectedResource;
	}	
	
	public boolean isRenderFieldTypeValueList() {
		return configFieldToAdd.getFieldTypeValues().size()>0;
	}

	public boolean isRenderFieldTypeValues() {
		boolean renderer = false;
		if (MetricDomainConfigFieldType.selectOneMenu.name().equals(configFieldToAdd.getFieldType()))
			renderer = true;
		return renderer;
	}
	
	//-------------Control and style---------------//
	
	public boolean isDescriptionMandatory() {
		return descriptionMandatory;
	}

	public boolean isDescriptionError() {
		return descriptionError;
	}

	public boolean isClassNameMandatory() {
		return classNameMandatory;
	}

	public boolean isClassNameError() {
		return classNameError;
	}

	public boolean isConnectorMandatory() {
		return connectorMandatory;
	}

	public boolean isResourceMandatory() {
		return resourceMandatory;
	}

	public String getDescriptionStyle() {
		return descriptionStyle;
	}

	public String getClassNameStyle() {
		return classNameStyle;
	}

	public String getConnectorStyle() {
		return connectorStyle;
	}
	
	public String getResourceStyle() {
		return resourceStyle;
	}

	public boolean isKeyMandatory() {
		return keyMandatory;
	}

	public boolean isKeyError() {
		return keyError;
	}

	public boolean isLabelMandatory() {
		return labelMandatory;
	}

	public boolean isLabelAlreadyUsed() {
		return labelAlreadyUsed;
	}

	public String getKeyStyle() {
		return keyStyle;
	}

	public String getLabelStyle() {
		return labelStyle;
	}

	public boolean isNoTypeValue() {
		return noTypeValue;
	}

	public boolean isTypeValueMandatory() {
		return typeValueMandatory;
	}

	public boolean isTypeValueError() {
		return typeValueError;
	}

	public String getTypeValueStyle() {
		return typeValueStyle;
	}

	public boolean isBusinessItemMandatory() {
		return businessItemMandatory;
	}

	public boolean isBusinessItemError() {
		return businessItemError;
	}

	public String getBusinessItemStyle() {
		return businessItemStyle;
	}
	
	//-------------Control and style---------------//

	public class MetricDomainConfigExtensionBean {
		private boolean selected;
		private MetricDomainConfigExtension extensionConfig;		
		
		public MetricDomainConfigExtensionBean(
				MetricDomainConfigExtension extensionConfig) {
			super();
			this.extensionConfig = extensionConfig;
			selected = false;
		}
		
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		public MetricDomainConfigExtension getExtensionConfig() {
			return extensionConfig;
		}	
	}
}
