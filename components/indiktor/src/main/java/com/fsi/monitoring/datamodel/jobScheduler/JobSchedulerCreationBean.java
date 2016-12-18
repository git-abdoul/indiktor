package com.fsi.monitoring.datamodel.jobScheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.AdminRequestCommand;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.jobScheduler.selection.JobSchedulerStaticDomainSelectionBean;
import com.fsi.monitoring.datamodel.jobScheduler.selection.JobSchedulerStaticDomainSelectorVisitor;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;
import com.fsi.monitoring.datamodel.monitor.MonitorSelectionBean;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsMulticastProducer;
import com.fsi.monitoring.jms.JmsProcessorFactory;
import com.fsi.monitoring.jms.JmsProcessorType;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.scheduler.model.IkrJobSchedulerStaticDomain;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlCommandButton;

public class JobSchedulerCreationBean
extends AccessControlBean
implements JobSchedulerStaticDomainSelectorVisitor, LogicalEnvSelectionVisitor, Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(JobSchedulerCreationBean.class);
	
	private IkrJobSchedulerConfig schedulerConfig = null;

	private JobSchedulerConfigAttributesBean jobSchedulerConfigAttributesBean;
	private JobSchedulingConfigBean jobSchedulingConfigBean;
	
	private UICommand deleteCommand = null;
	private UICommand duplicateCommand = null;		
	
	private boolean duplicate = false;
	private boolean edit = false;
	
	private boolean rendererJobScheduler = false;
	
	private LogicalEnvSelectionBean logicalEnvSelectionBean;
	
	private JobSchedulerCustomAttribute customAttributeBean;
	
	//Properties Tab
	private boolean enable;
	private String name;
	private boolean nameMandatory = false;
	private String nameStyle = "width:250px;";
	private String description;
	//Scheduling Tab
	private String mode;
	private int day;
	private int hour;
	private int min;
	
	private boolean coupleEnvNameExist = false;
	private String envStyle = "width:250px;";
	private String coupleEnvNameErrorMsg = "";
	private String coupleEnvNameErrorMsgTrim = "";
	
	private JmsProcessorFactory jmsFactory;
	private JmsMulticastProducer adminRequestProducer;
	
	public JobSchedulerCreationBean() {
		deleteCommand = new HtmlCommandButton();
		duplicateCommand = new HtmlCommandButton();		
		schedulerConfig = new IkrJobSchedulerConfig();
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(true);
		initBean();		
	}
	
	public void initJms() {
		adminRequestProducer = (JmsMulticastProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.ADMIN_REQUEST);
	}
	
	public void create() {
		update(new IkrJobSchedulerConfig());		
		rendererJobScheduler = true;		
	}
	
	public void edit() {
		rendererJobScheduler = true;
		duplicate = false;
		edit = true;

		enable = schedulerConfig.isActive();
		name = schedulerConfig.getName();
		description = schedulerConfig.getDescription();
		mode = schedulerConfig.getMode();
		day = jobSchedulingConfigBean.getStartDate().getDay();
		hour = jobSchedulingConfigBean.getStartDate().getHour();
		min = jobSchedulingConfigBean.getStartDate().getMin();
	}
	
	public void update(IkrJobSchedulerConfig schedulerConfig) {				
		this.schedulerConfig = schedulerConfig;			
		initBean();			
		duplicate = false;	
		edit = false;
	}	
	
	private void initBean() {
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());	
		if (schedulerConfig.getJobStaticDomainId() == 0) {			
			try {
				Map<Integer, IkrJobSchedulerStaticDomain> staticDomainsMap = dataModelPM.getJobSchedulerStaticDomains();
				List<IkrJobSchedulerStaticDomain> staticDomains = new ArrayList<IkrJobSchedulerStaticDomain>(staticDomainsMap.values());
				if (staticDomains.size()>0) {					
					schedulerConfig.setJobStaticDomainId(staticDomains.get(0).getId());					
				}
				else {
					ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
					error.init();
					error.setRendered(true);
					error.setModal(true);
					error.setType(ErrorMessageBean.WARNING);
					error.addMessage("No Job Scheduler Static Domain has been set up...");
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
		
		jobSchedulingConfigBean =  new JobSchedulingConfigBean(schedulerConfig);		
		
		try {
			IkrJobSchedulerStaticDomain jobStaticDomain = dataModelPM.getJobSchedulerStaticDomain(schedulerConfig.getJobStaticDomainId());
			if (jobStaticDomain != null) {
				Class<JobSchedulerCustomAttribute> customclass = null;
				try {
					customclass = (Class<JobSchedulerCustomAttribute>)Class.forName("custom.jobScheduler.JobSchedulerAttribute_"+jobStaticDomain.getJobSchedulerType());
					customAttributeBean = customclass.newInstance();	
					customAttributeBean.setJspPageDirectory("/jsp/custom/jobScheduler");
				} catch (ClassNotFoundException e) {
					logger.warn(e.getMessage());
				} catch (InstantiationException e) {
					logger.warn(e.getMessage());
				} catch (IllegalAccessException e) {
					logger.warn(e.getMessage());
				}
				
				if (customclass == null) {
					try {
						customclass = (Class<JobSchedulerCustomAttribute>)Class.forName("com.fsi.monitoring.datamodel.jobScheduler.JobSchedulerAttribute_"+jobStaticDomain.getJobSchedulerType());
						customAttributeBean = customclass.newInstance();
						customAttributeBean.setJspPageDirectory("/jsp/jobScheduler");
					} catch (ClassNotFoundException e) {
						logger.warn(e.getMessage());
					} catch (InstantiationException e) {
						logger.warn(e.getMessage());
					} catch (IllegalAccessException e) {
						logger.warn(e.getMessage());
					}
				}
				
				if (customclass != null) {
					customAttributeBean.initSchedulerConfig(schedulerConfig);
					jobSchedulingConfigBean.setCustomAttributeBean(customAttributeBean);
				}
			}
		} catch (PersistenceException e) {
			logger.warn(e.getMessage(), e);
		}
		
		jobSchedulingConfigBean.initJobSchedulingConfig();
		SelectItem[] schedulerModeItems = new SelectItem[3];
		schedulerModeItems[0] =  new SelectItem(IkrJobSchedulerConfig.DAILY, IkrJobSchedulerConfig.DAILY);
		schedulerModeItems[1] =  new SelectItem(IkrJobSchedulerConfig.WEEKLY, IkrJobSchedulerConfig.WEEKLY);
		schedulerModeItems[2] =  new SelectItem(IkrJobSchedulerConfig.MONTHLY, IkrJobSchedulerConfig.MONTHLY);
		jobSchedulingConfigBean.setSchedulerModeItems(schedulerModeItems);	
		
		jobSchedulerConfigAttributesBean = new JobSchedulerConfigAttributesBean(schedulerConfig);
		jobSchedulerConfigAttributesBean.init();
		
		logicalEnvSelectionBean.accept(this);
		if (schedulerConfig.getLogicalEnvId() != 0) {
			logicalEnvSelectionBean.initLogicalEnv(schedulerConfig.getLogicalEnvId());
		} else {
			logicalEnvSelectionBean.init();
		}
		
		JobSchedulerStaticDomainSelectionBean jobSchedulerStaticDomainSelectionBean = (JobSchedulerStaticDomainSelectionBean)FacesUtils.getManagedBean("jobSchedulerStaticDomainSelectionBean");		
		jobSchedulerStaticDomainSelectionBean.accept(this);
		if (schedulerConfig.getJobStaticDomainId() != 0) {
			jobSchedulerStaticDomainSelectionBean.initTaskStaticDomain(schedulerConfig.getJobStaticDomainId());
		} else {
			jobSchedulerStaticDomainSelectionBean.init();
		}			
	}
	
	public JobSchedulerConfigAttributesBean getJobSchedulerConfigAttributesBean() {
		return jobSchedulerConfigAttributesBean;
	}	
	
	public JobSchedulingConfigBean getJobSchedulingConfigBean() {
		return jobSchedulingConfigBean;
	}
	
	public IkrJobSchedulerConfig getConfig() {
		return schedulerConfig;
	}

	public boolean isUpdate() {
		return schedulerConfig.getId() != 0;
	}
	
	public boolean isDuplicate() {
		return duplicate;
	}
	
	public void changeLogicalEnv(int logicalEnvId) {
		schedulerConfig.setLogicalEnvId(logicalEnvId);
	}
	
	public void changeTaskStaticDomain(int taskStaticDomainId) {
		schedulerConfig.setJobStaticDomainId(taskStaticDomainId);
		jobSchedulerConfigAttributesBean.init();
	}

	public UICommand getDeleteCommand() {
		return deleteCommand;
	}	
	
	public UICommand getDuplicateCommand() {
		return duplicateCommand;
	}		
	
	public void setDeleteCommand(UICommand removeCommand) {}
	public void setDuplicateCommand(UICommand duplicateCommand) {}		

	public void save(ActionEvent event) {
		JobSchedulerSelectionBean jobSchedulerSelectionBean = (JobSchedulerSelectionBean)FacesUtils.getManagedBean("jobSchedulerSelectionBean");
		for(JobSchedulerConfigBean jobScheduler : jobSchedulerSelectionBean.getJobSchedulerConfigs()) {
			if(jobScheduler.getName().equalsIgnoreCase(schedulerConfig.getName())
					&& jobScheduler.getLogicalEnv().getId() == schedulerConfig.getLogicalEnvId()
							&& jobScheduler.getId() != schedulerConfig.getId()) {
				nameMandatory = false;
				nameStyle = "width:250px; border:1px solid red;";
				coupleEnvNameExist = true;
				envStyle = "width:250px; border:1px solid red;";
				return;
			}
		}
		if(schedulerConfig.getName() != null && schedulerConfig.getName().length() > 0) {
//		updateBean();
			if (customAttributeBean == null)
				jobSchedulerConfigAttributesBean.update();
			else
				customAttributeBean.updateAttributes();
			
			jobSchedulingConfigBean.update();
			
			try {
				DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());			
				IkrJobSchedulerStaticDomain domain = dataModelPM.getJobSchedulerStaticDomain(schedulerConfig.getJobStaticDomainId());
				String name = schedulerConfig.getName();
				if (name == null || name.length() == 0)
					name = domain.getName();
				schedulerConfig.setName(name);
				int taskId = schedulerConfig.getId();
				if (taskId == 0) {
					// this is a creation
					taskId = dataModelPM.createJobScheduler(schedulerConfig);
					List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
					requests.add(new AdminRequest(AdminRequestCommand.ADD, (long)taskId, AdminComponent.JOB_TASK));
					adminRequestProducer.publish(requests);
				} else {
					dataModelPM.updateJobScheduler(schedulerConfig);
					List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
					requests.add(new AdminRequest(AdminRequestCommand.UPDATE, (long)taskId, AdminComponent.JOB_TASK));
					adminRequestProducer.publish(requests);
				}
				
				JobSchedulerSelectionBean scheduledTaskSelectionBean = (JobSchedulerSelectionBean)FacesUtils.getManagedBean("jobSchedulerSelectionBean");
				scheduledTaskSelectionBean.reloadJobSchedulerList();
				scheduledTaskSelectionBean.setSelectAll(false);
				scheduledTaskSelectionBean.setJobSchedulerConfigsSelected(new ArrayList<JobSchedulerConfigBean>());
				nameMandatory = false;
				nameStyle = "width:250px;";
				coupleEnvNameExist = false;
				envStyle = "width:250px;";
				rendererJobScheduler = false;
			} catch(Exception exc) {
				logger.error(exc.getMessage(), exc);
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.ERROR);
				error.addMessage(exc.getMessage());
			}
		}
		else {
			nameMandatory = true;
			nameStyle = "width:250px; border:1px solid red;";
			coupleEnvNameExist = false;
			envStyle = "width:250px;";
		}
	}
	
	public void duplicate() {
		schedulerConfig = cloneTaskConfig(schedulerConfig);
		initBean();
		duplicate = true;
		edit = false;
		deleteCommand.setRendered(false);
		duplicateCommand.setRendered(false);
		rendererJobScheduler = true;
	}
	
	public void delete() {
		try {
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());			
			dataModelPM.deleteJobScheduler(schedulerConfig.getId());
			List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
			requests.add(new AdminRequest(AdminRequestCommand.REMOVE, (long)schedulerConfig.getId(), AdminComponent.JOB_TASK));
			adminRequestProducer.publish(requests);
			
			JobSchedulerSelectionBean scheduledTaskSelectionBean = (JobSchedulerSelectionBean)FacesUtils.getManagedBean("jobSchedulerSelectionBean");
			scheduledTaskSelectionBean.reloadJobSchedulerList();
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.ERROR);
			error.addMessage(exc.getMessage());
		}	
	}
	
	private IkrJobSchedulerConfig cloneTaskConfig(IkrJobSchedulerConfig config) {
		IkrJobSchedulerConfig clone = new IkrJobSchedulerConfig();
		clone.setName(new String(config.getName()));
		clone.setLogicalEnvId(config.getLogicalEnvId());
		clone.setMode(config.getMode());
		clone.setJobStaticDomainId(config.getJobStaticDomainId());
		clone.setStartTime(config.getStartTime());
		clone.setEndTime(config.getEndTime());
		clone.setDescription(config.getDescription());
		clone.setActive(config.isActive());
		return clone;
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}
	
	public void closeJobSchedulerPopup(ActionEvent event) {	
		if(edit) {
			schedulerConfig.setActive(this.enable);
			schedulerConfig.setName(this.name);
			schedulerConfig.setDescription(this.description);
			
			jobSchedulingConfigBean.changeSchedulerMode(this.mode);
			jobSchedulingConfigBean.getStartDate().setDay(this.day);
			jobSchedulingConfigBean.getStartDate().setHour(this.hour);
			jobSchedulingConfigBean.getStartDate().setMin(this.min);
			
			JobSchedulerSelectionBean jobSchedulerSelectionBean = (JobSchedulerSelectionBean)FacesUtils.getManagedBean("jobSchedulerSelectionBean");
			jobSchedulerSelectionBean.init(event);
		}
		rendererJobScheduler = false;
		duplicate = false;
		edit = false;
		nameMandatory = false;
		nameStyle = "width:250px;";
		coupleEnvNameExist = false;
		envStyle = "width:250px;";
	}

	public boolean isRendererJobScheduler() {
		return rendererJobScheduler;
	}
	
	public boolean isRendererCustomAttribute() {
		return (customAttributeBean != null);
	}

	public JobSchedulerCustomAttribute getCustomAttributeBean() {
		return customAttributeBean;
	}

	public boolean isEdit() {
		return edit;
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

	public String getEnvStyle() {
		return envStyle;
	}
	
	public String getCoupleEnvNameErrorMsg() {
		coupleEnvNameErrorMsg = "";
		coupleEnvNameErrorMsgTrim = "";
		coupleEnvNameErrorMsg = logicalEnvSelectionBean.getLogicalEnvName() + " / " + schedulerConfig.getName();
		if(coupleEnvNameErrorMsg.length() > 15) {
			for(int i = 0; i < 15; i++) {
				coupleEnvNameErrorMsgTrim = coupleEnvNameErrorMsgTrim + coupleEnvNameErrorMsg.charAt(i);
			}
		}
		else
			coupleEnvNameErrorMsgTrim = coupleEnvNameErrorMsg;
		
		return "Couple Env/Name (" + coupleEnvNameErrorMsg + ") already exists";
	}

	public String getCoupleEnvNameErrorMsgTrim() {
		return "Couple Env/Name (" + coupleEnvNameErrorMsgTrim + ") already exists";
	}
	
	public void onChangeName(ActionEvent event) {
		JobSchedulerSelectionBean jobSchedulerSelectionBean = (JobSchedulerSelectionBean)FacesUtils.getManagedBean("jobSchedulerSelectionBean");
		for(JobSchedulerConfigBean jobScheduler : jobSchedulerSelectionBean.getJobSchedulerConfigs()) {
			if(jobScheduler.getName().equalsIgnoreCase(schedulerConfig.getName())
					&& jobScheduler.getLogicalEnv().getId() == schedulerConfig.getLogicalEnvId()
							&& jobScheduler.getId() != schedulerConfig.getId()) {
				nameMandatory = false;
				nameStyle = "width:250px; border:1px solid red;";
				coupleEnvNameExist = true;
				envStyle = "width:250px; border:1px solid red;";
				return;
			}
			else {
				nameMandatory = false;
				nameStyle = "width:250px;";
				coupleEnvNameExist = false;
				envStyle = "width:250px;";
			}
		}
	}

	public void setJmsFactory(JmsProcessorFactory jmsFactory) {
		this.jmsFactory = jmsFactory;
	}
}
