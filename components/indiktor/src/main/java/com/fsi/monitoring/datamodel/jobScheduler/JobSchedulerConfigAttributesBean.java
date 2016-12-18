package com.fsi.monitoring.datamodel.jobScheduler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.AttributeUIBean;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerAttributeConfig;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;

public class JobSchedulerConfigAttributesBean
implements Serializable {
	private static final long serialVersionUID = 4224971416355277243L;

	private final static Logger logger = Logger.getLogger(JobSchedulerConfigAttributesBean.class);	
	
	private IkrJobSchedulerConfig schedulerConfig;	
	private Map<String, IkrJobSchedulerAttributeConfig> taskAttributeConfigs;	
	
	private Map<String, UIOutput> labelUIs = new HashMap<String, UIOutput>();
	private Map<String, UIInput> inputUIs = new HashMap<String, UIInput>();	
	private Map<String, UIInput> selectUIs = new HashMap<String, UIInput>();
	private Map<String, SelectItem[]> selectionValues = new HashMap<String, SelectItem[]>();
	
	private Map<String, AttributeUIBean> attributeUIBeans;
	private Map<String, String> attributeMap;
	
	public JobSchedulerConfigAttributesBean(IkrJobSchedulerConfig schedulerConfig) {
		this.schedulerConfig = schedulerConfig;
		
		for (int i=1; i<11; i++){
			UIInput input = new HtmlInputText();
			UIOutput label = new HtmlOutputLabel();
			UIInput select = new HtmlSelectOneMenu();
			input.setRendered(false);
			input.setValue("");
			label.setRendered(false);
			label.setValue("");
			select.setRendered(false);
			((HtmlSelectOneMenu)select).setPartialSubmit(true);
			inputUIs.put("attribute"+i, input);
			labelUIs.put("attribute"+i, label);
			selectUIs.put("attribute"+i, select);
		}
	}
	
	public void init() {		
		initAttributeComponents();
		
//		Map<String, String> attributes = schedulerConfig.getAttributes();
//		for(String attribute : attributes.keySet()) {			
//			AttributeUIBean uiBean = attributeUIBeans.get(attribute);
//			if (uiBean!= null)
//				uiBean.getInputUI().setValue(attributes.get(attribute));
//		}	
	}
	
	public boolean isAttributesEnabled() {
		boolean show = false;
		if (taskAttributeConfigs!=null && taskAttributeConfigs.size()>0) {
			show = true;
		}		
		return show;
	}
	
	private void initAttributeComponents() {		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		Map<String, IkrJobSchedulerAttributeConfig> attrConfs;
		try {
			attrConfs = dataModelPM.getJobSchedulerAttributeConfigs(schedulerConfig.getJobStaticDomainId());
			this.taskAttributeConfigs = getTaskAttributeConfigs(attrConfs);
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.ERROR);
			error.addMessage(e.getMessage());
		}
		
		int i = 1;
		attributeUIBeans = new HashMap<String, AttributeUIBean>();
		attributeMap = new HashMap<String, String>();
		Map<String, String> schedulerConfigAttributes = schedulerConfig.getAttributes();
		for (String attribute : taskAttributeConfigs.keySet()) {
			IkrJobSchedulerAttributeConfig field = taskAttributeConfigs.get(attribute);
			UIOutput label = labelUIs.get("attribute"+i);
			UIInput input = inputUIs.get("attribute"+i);
			UIInput select = selectUIs.get("attribute"+i);
			String attrValue = schedulerConfigAttributes.get(attribute);
			if (!field.isEnabled()) {
				label.setRendered(false);
				input.setRendered(false);
				select.setRendered(false);
			} 
			else {
				label.setRendered(true);
				label.setValue(field.getLabel()+":");
				UIInput ui = null;
				if (field.isSelection()) {
					input.setRendered(false);
					select.setRendered(true);
					ui = select;
					SelectItem[] items = new SelectItem[field.getSelectionValues().size()];
					int j = 0;
					for (String value : field.getSelectionValues()) {
						items[j] = new SelectItem(value, value);
						j++;
					}
					selectionValues.put("attribute"+i, items);
					
					if (attrValue != null)
						ui.setValue(attrValue);
					else
						ui.setValue(items[0].getValue());
				}
				else {
					input.setRendered(true);
					select.setRendered(false);
					ui = input;
					
					if (attrValue != null)
						ui.setValue(attrValue);
					else
						ui.setValue("");
				}
				attributeUIBeans.put(attribute, new AttributeUIBean(label, ui));
				attributeMap.put("attribute"+i, attribute);
			}
			i++;
		}
	}
	
	private Map<String,IkrJobSchedulerAttributeConfig> getTaskAttributeConfigs(Map<String,IkrJobSchedulerAttributeConfig> attrs) {
		Map<String,IkrJobSchedulerAttributeConfig> tmp = new HashMap<String, IkrJobSchedulerAttributeConfig>();
		for(String attribute:attrs.keySet()){
			tmp.put(attribute, attrs.get(attribute));
		}
		return tmp;
	}
	
	public void onChangeAttributeValue(ValueChangeEvent e) {
		String newValue = (String)e.getNewValue();
		System.out.println(newValue);
		HtmlSelectOneMenu component = (HtmlSelectOneMenu)e.getComponent();
		String attrKey = (String)component.getAttributes().get("attrKey");
		AttributeUIBean uiBean = attributeUIBeans.get(attributeMap.get(attrKey));
		uiBean.setInputUI(component);
	}
	
	public void update() {		
		for(String attribute:attributeUIBeans.keySet()){
			AttributeUIBean uiBean = attributeUIBeans.get(attribute);
			String value = (String)uiBean.getInputUI().getValue();
			schedulerConfig.addAttribute(attribute, value);
		}
	}
	
	public Map<String, UIOutput> getAttributeLabelUIs() {
		return labelUIs;
	}	
	
	public Map<String, UIInput> getAttributeUIs() {
		return inputUIs;
	}	
	
	public Map<String, UIInput> getSelectUIs() {
		return selectUIs;
	}
	
	public Map<String, SelectItem[]> getAttributeSelectionValues() {
		return selectionValues;
	}	
	
}
