package com.fsi.monitoring.datamodel.jobScheduler;

import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerAttributeConfig;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;

public class JobSchedulerAttribute_PURGE_TABLE extends JobSchedulerCustomAttribute {
	private static final long serialVersionUID = -5830249439261997715L;
	private final static Logger logger = Logger.getLogger(JobSchedulerAttribute_PURGE_TABLE.class);	

	private IkrJobSchedulerConfig schedulerConfig;
	
//	private SelectItem[] purgeFrequencyItems;
	private SelectItem[] booleanSelectionItems;
	private SelectItem[] valuesPerDayItems;
	
	private String purgeFrequency;
	private boolean archiveNeeded;
	private String valuesPerDay;
	
	public void onComponentValueChanged(ValueChangeEvent e) {
		String newValue = String.valueOf(e.getNewValue());
		System.out.println(newValue);
		HtmlSelectOneMenu component = (HtmlSelectOneMenu)e.getComponent();
		String attrKey = (String)component.getAttributes().get("attrKey");
//		if ("PURGE_FREQUENCY".equals(attrKey)) {
//			purgeFrequency = newValue;
//		}
		if ("ARCHIVE_NEEDED".equals(attrKey)) {
			archiveNeeded = Boolean.valueOf(newValue);
		}
		else {
			valuesPerDay = newValue;
		}
		schedulerConfig.getAttributes().put(attrKey, newValue);
	}
	
	@Override
	public void updateAttributes() {
		schedulerConfig.getAttributes().put("ARCHIVE_NEEDED", String.valueOf(archiveNeeded));
		schedulerConfig.getAttributes().put("VALUES_PER_DAY", valuesPerDay);
		schedulerConfig.getAttributes().put("PURGE_FREQUENCY", purgeFrequency);		
	}		

	@Override
	public void initSchedulerConfig(IkrJobSchedulerConfig schedulerConfig) {
		this.schedulerConfig = schedulerConfig;
		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		try {
			Map<String, String> schedulerConfigAttributes = schedulerConfig.getAttributes();
			Map<String, IkrJobSchedulerAttributeConfig> attrConfs = dataModelPM.getJobSchedulerAttributeConfigs(schedulerConfig.getJobStaticDomainId());
			
			// Purge Frequency:
//			IkrJobSchedulerAttributeConfig field = attrConfs.get("PURGE_FREQUENCY");
//			purgeFrequencyItems = new SelectItem[field.getSelectionValues().size()];
//			int j = 0;
//			for (String value : field.getSelectionValues()) {
//				purgeFrequencyItems[j] = new SelectItem(value, value);
//				j++;
//			}
			
			int j = 0;
			// Archive Needed
			booleanSelectionItems = new SelectItem[2];
			booleanSelectionItems[0] = new SelectItem(true, "true");
			booleanSelectionItems[1] = new SelectItem(false, "false");
			
			// Values Per Day
			IkrJobSchedulerAttributeConfig field = attrConfs.get("VALUES_PER_DAY");
			valuesPerDayItems = new SelectItem[field.getSelectionValues().size()];
			j = 0;
			for (String value : field.getSelectionValues()) {
				valuesPerDayItems[j] = new SelectItem(value, value);
				j++;
			}
			
			if (schedulerConfig.getId()>0) {
				purgeFrequency = schedulerConfigAttributes.get("PURGE_FREQUENCY");
				archiveNeeded = Boolean.valueOf(schedulerConfigAttributes.get("ARCHIVE_NEEDED"));
				valuesPerDay = schedulerConfigAttributes.get("VALUES_PER_DAY");
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.ERROR);
			error.addMessage(e.getMessage());
		}		
	}

	@Override
	public void schedulingModeChanged(String newMode) {
		if (IkrJobSchedulerConfig.WEEKLY.equals(newMode)) {
			purgeFrequency = "Week-1";
//			purgeFrequencyItems = new SelectItem[2];
//			purgeFrequencyItems[0] = new SelectItem("Day-1", "Day-1");
//			purgeFrequencyItems[1] = new SelectItem("Week-1", "Week-1");
		}
		else if (IkrJobSchedulerConfig.MONTHLY.equals(newMode)) {
			purgeFrequency = "Month-1";
//			purgeFrequencyItems = new SelectItem[3];
//			purgeFrequencyItems[0] = new SelectItem("Day-1", "Day-1");
//			purgeFrequencyItems[1] = new SelectItem("Week-1", "Week-1");
//			purgeFrequencyItems[2] = new SelectItem("Month-1", "Month-1");
		}	
		else {
			purgeFrequency = "Day-1";
//			purgeFrequencyItems = new SelectItem[1];
//			purgeFrequencyItems[0] = new SelectItem("Day-1", "Day-1");
		}
	}

	@Override
	public String getJspPageName() {
		return "jobScheduler_PURGE_TABLE.jspx";
	}

//	public SelectItem[] getPurgeFrequencyItems() {
//		return purgeFrequencyItems;
//	}

	public SelectItem[] getBooleanSelectionItems() {
		return booleanSelectionItems;
	}

	public SelectItem[] getValuesPerDayItems() {
		return valuesPerDayItems;
	}

	public String getPurgeFrequency() {
		return purgeFrequency;
	}

	public void setPurgeFrequency(String purgeFrequency) {
		this.purgeFrequency = purgeFrequency;
	}

	public boolean isArchiveNeeded() {
		return archiveNeeded;
	}

	public void setArchiveNeeded(boolean archiveNeeded) {
		this.archiveNeeded = archiveNeeded;
	}

	public String getValuesPerDay() {
		return valuesPerDay;
	}

	public void setValuesPerDay(String valuesPerDay) {
		this.valuesPerDay = valuesPerDay;
	}	
}
