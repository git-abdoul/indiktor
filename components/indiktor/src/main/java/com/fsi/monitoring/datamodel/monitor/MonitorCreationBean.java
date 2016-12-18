package com.fsi.monitoring.datamodel.monitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.AdminRequestCommand;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.ikrStaticDomain.IkrStaticDomainCreateBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorBean;
import com.fsi.monitoring.datamodel.ikrStaticDomain.selection.IkrStaticDomainSelectorVisitor;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.ikr.model.MetricDomainConfig;
import com.fsi.monitoring.ikr.monitor.MonitorConfigAttributeKey;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsMulticastProducer;
import com.fsi.monitoring.jms.JmsProcessorFactory;
import com.fsi.monitoring.jms.JmsProcessorType;
import com.fsi.monitoring.scheduler.config.IkrMonitorSchedulerConfig;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.FacesUtils;

public class MonitorCreationBean
extends AccessControlBean
implements IkrStaticDomainSelectorVisitor, LogicalEnvSelectionVisitor, Serializable {

	private static final long serialVersionUID = -3211187085936131426L;
	private static final Logger logger = Logger.getLogger(MonitorCreationBean.class);
	
	private MonitorConfig monitorConfig = null;

	private MetricDomainConfigSelectorBean metricDomainConfigBean;
	private MetricDomainConfigAttributesBean metricDomainConfigAttributesBean;
	private MonitorSchedulingConfigBean monitorSchedulingConfigBean;
	
	private LogicalEnvSelectionBean logicalEnvSelectionBean;
	
	private boolean rendererCollector = false;
	
	private boolean changeMetricDomainConfig;
	
	private boolean duplicate = false;
	private boolean edit = false;
	
	private boolean startTimeMustBeActive = false;
	private boolean endTimeMustBeActive = false;
	private boolean endBeforeStart = false;
	private String startLabelStyle = "";
	private String endLabelStyle = "";
	private String dayStyle = "width: 50px;";
	private String hourStyle = "width: 50px;";
	private String minStyle = "width: 50px;";

	private boolean coupleEnvContextExist = false;
	private String envStyle = "width:250px;";
	private String coupleEnvContextErrorMsg = "";
	private String coupleEnvContextErrorMsgTrim = "";
	
	private String context;
	private boolean contextMandatory = false;
	private String contextStyle = "width:250px;";
	private boolean started;
	private String type;
	private String mode;
	private String delay;
	private boolean delayWrongFormat = false;
	private boolean delayMandatory = false;
	private String delayStyle = "width:250px;";
	private boolean startDate;
	private int startDateDay;
	private int startDateHour;
	private int startDateMin;
	private boolean endDate;
	private int endDateDay;
	private int endDateHour;
	private int endDateMin;
	
	private JmsProcessorFactory jmsFactory;
	private JmsMulticastProducer adminRequestProducer;
	
	public MonitorCreationBean() {
		monitorConfig = new MonitorConfig();
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(true);
		changeMetricDomainConfig = true;		
		initBean();		
	}
	
	public void initJms() {
		adminRequestProducer = (JmsMulticastProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.ADMIN_REQUEST);
	}
	
	public void create() {
		delay = "30";
		update(new MonitorConfig());		
		rendererCollector = true;		
	}
	
	public void edit() {
		rendererCollector = true;
		duplicate = false;

		started = monitorConfig.isAutoStart();
		context = monitorConfig.getContext();
		type = monitorSchedulingConfigBean.getSchedulerConfig().getType();
		mode = monitorSchedulingConfigBean.getSchedulerConfig().getMode();
		delay = String.valueOf(monitorSchedulingConfigBean.getSchedulerConfig().getDelay());
		startDate = monitorSchedulingConfigBean.getStartDate().isActive();
		startDateDay = monitorSchedulingConfigBean.getStartDate().getDay();
		startDateHour = monitorSchedulingConfigBean.getStartDate().getHour();
		startDateMin = monitorSchedulingConfigBean.getStartDate().getMin();
		endDate = monitorSchedulingConfigBean.getEndDate().isActive();
		endDateDay = monitorSchedulingConfigBean.getEndDate().getDay();
		endDateHour = monitorSchedulingConfigBean.getEndDate().getHour();
		endDateMin = monitorSchedulingConfigBean.getEndDate().getMin();
		edit = true;
	}
	
	public void update(MonitorConfig monitorConfig) {				
		this.monitorConfig = monitorConfig;	
		changeMetricDomainConfig = false;
		if (monitorConfig.getId() == 0)
			changeMetricDomainConfig = true;
		
		initBean();			
		duplicate = false;	
		edit = false;
	}	
	
	private void initBean() {
		// those beans are used for ikrStaticDomainSelectorBean init
		metricDomainConfigBean = new MetricDomainConfigSelectorBean(monitorConfig);
		metricDomainConfigAttributesBean = new MetricDomainConfigAttributesBean(monitorConfig);
		
		IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");	
		ikrStaticDomainSelectorBean.initComponent(true, true, true, true);
		ikrStaticDomainSelectorBean.accept(this);
		int metricDomainId = monitorConfig.getMetricDomainConfig().getIkrStaticDomainId();
		if (metricDomainId != 0) {
			ikrStaticDomainSelectorBean.initMetricDomainId(metricDomainId);	
			MetricDomainConfig metricDomainConfig = monitorConfig.getMetricDomainConfig();			
			monitorConfig.setMetricDomainConfig(metricDomainConfig);
		} else {
			ikrStaticDomainSelectorBean.initItems();
		}		
		
		logicalEnvSelectionBean.accept(this);
		if (monitorConfig.getLogicalEnvId() != 0) {
			logicalEnvSelectionBean.initLogicalEnv(monitorConfig.getLogicalEnvId());
		} else {
			logicalEnvSelectionBean.init();
		}

		monitorSchedulingConfigBean =  new MonitorSchedulingConfigBean(monitorConfig, metricDomainConfigAttributesBean);
	}
	
	public MetricDomainConfigSelectorBean getMetricDomainConfigBean() {
		return metricDomainConfigBean;
	}
	
	public MetricDomainConfigAttributesBean getMetricDomainConfigAttributesBean() {
		return metricDomainConfigAttributesBean;
	}	
	
	public MonitorSchedulingConfigBean getMonitorSchedulingConfigBean() {
		return monitorSchedulingConfigBean;
	}
	
	public MonitorConfig getConfig() {
		return monitorConfig;
	}
	
	public boolean isUpdate() {
		return monitorConfig.getId() != 0;
	}
	
	public boolean isDuplicate() {
		return duplicate;
	}
	
	public void changeMetricDomain(int metricDomainId) {
		if (changeMetricDomainConfig)
			metricDomainConfigBean.changeMetricDomain(metricDomainId);
		metricDomainConfigAttributesBean.init();		
	}	
	
	public void changeMetricDomainConfig(ValueChangeEvent e) {
		Integer newValue = (Integer)e.getNewValue();
		
		MetricDomainConfig metricDomainConfig = metricDomainConfigBean.getMetricDomainConfig(newValue.intValue());
		monitorConfig.setMetricDomainConfig(metricDomainConfig);
		
//		Map<String, MetricDomainResource> metricDomainResources = metricDomainConfigBean.getMetricDomainResources(metricDomainConfig.getIkrStaticDomainId());
//		monitorConfig.setMetricDomainResources(metricDomainResources);	
		
		metricDomainConfigAttributesBean.init();
		
		metricDomainConfigBean.resetConnectorNames();
	}
	
	public void changeMetricGroup(int metricGroupId) {}	
	
	public void changeLogicalEnv(int logicalEnvId) {
		monitorConfig.setLogicalEnvId(logicalEnvId);
	}	

	public void save(ActionEvent event) {
		MonitorSelectionBean monitorSelectionBean = (MonitorSelectionBean)FacesUtils.getManagedBean("monitorSelectionBean");
		coupleEnvContextExist = false;
		for(MonitorConfigBean monitor : monitorSelectionBean.getMonitorConfigs()) {
			if(monitor.getContext().equalsIgnoreCase(monitorConfig.getContext())
					&& monitor.getLogicalEnv().getId() == monitorConfig.getLogicalEnvId()
							&& monitor.getId() != monitorConfig.getId()) {
				contextMandatory = false;
				contextStyle = "width:250px; border:1px solid red;";
				coupleEnvContextExist = true;
				envStyle = "width:250px; border:1px solid red;";
				return;
			}
		}
		if(!coupleEnvContextExist) {
			contextMandatory = false;
			contextStyle = "width:250px;";
			coupleEnvContextExist = false;
			envStyle = "width:250px;";
		}
		
		String context = monitorConfig.getContext();
		if (context != null && context.length()>0) {
			testEntries();
			
			if(startTimeMustBeActive || endTimeMustBeActive || endBeforeStart || delayMandatory || delayWrongFormat)
				return;
				
			monitorSchedulingConfigBean.getSchedulerConfig().setDelay(Long.valueOf(this.delay));
			
			metricDomainConfigBean.update();
			metricDomainConfigAttributesBean.update();
			monitorSchedulingConfigBean.update();
			try {
				
				String ikrCategoryResourceIds = metricDomainConfigAttributesBean.saveIkrCategoryResource();
				if (ikrCategoryResourceIds!=null&&ikrCategoryResourceIds.length()>0)
					monitorConfig.addAttribute(MonitorConfigAttributeKey.CATEGORY_RESOURCE, ikrCategoryResourceIds);
				DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
				long monitorId = monitorConfig.getId();
				if (monitorId == 0) {
					// this is a creation
					monitorId = dataModelPM.createMonitor(monitorConfig);
					List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
					requests.add(new AdminRequest(AdminRequestCommand.ADD, monitorId, AdminComponent.COLLECTOR));
					adminRequestProducer.publish(requests);
				} else {
					BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);	
					beanPM.flushMonitorConfigBean(monitorConfig.getId());
					dataModelPM.updateMonitor(monitorConfig);
					List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
					requests.add(new AdminRequest(AdminRequestCommand.UPDATE, monitorId, AdminComponent.COLLECTOR));
					adminRequestProducer.publish(requests);
				}			
				
				monitorSelectionBean = (MonitorSelectionBean)FacesUtils.getManagedBean("monitorSelectionBean");
				monitorSelectionBean.reloadMonitors();
				monitorSelectionBean.setSelectAll(false);
				monitorSelectionBean.setMonitorConfigsSelected(new ArrayList<MonitorConfigBean>());
				contextMandatory = false;
				contextStyle = "width:250px;";
				coupleEnvContextExist = false;
				envStyle = "width:250px;";
				rendererCollector = false;
				resetControl();
			} catch(Exception exc) {
				logger.error(exc.getMessage(), exc);
			}			
		}
		else {
			contextMandatory = true;
			contextStyle = "width:250px; border:1px solid red;";
			coupleEnvContextExist = false;
			envStyle = "width:250px;";
		}
	}
	
	public String navigateToMetricDomainConfig() {
		rendererCollector = false;
		IkrStaticDomainCreateBean ikrStaticDomainBean = (IkrStaticDomainCreateBean)FacesUtils.getManagedBean("staticDomainBean");
		ikrStaticDomainBean.navigate(null);
		return ikrStaticDomainBean.action();
	}	
	
	public void duplicate() {
		if (!isAuthorized(94,"createMonitor")) {
			return;
		}
		
		monitorConfig = cloneMonitorConfig(monitorConfig);
		metricDomainConfigBean = new MetricDomainConfigSelectorBean(monitorConfig);
		metricDomainConfigAttributesBean = new MetricDomainConfigAttributesBean(monitorConfig);
		
		IkrStaticDomainSelectorBean ikrStaticDomainSelectorBean = (IkrStaticDomainSelectorBean)FacesUtils.getManagedBean("ikrStaticDomainSelectorBean");	
		ikrStaticDomainSelectorBean.initComponent(true, true, true, true);
		ikrStaticDomainSelectorBean.accept(this);
		ikrStaticDomainSelectorBean.initMetricDomainId(monitorConfig.getMetricDomainConfig().getIkrStaticDomainId());
		
		monitorSchedulingConfigBean =  new MonitorSchedulingConfigBean(monitorConfig,metricDomainConfigAttributesBean);
		duplicate = true;
		edit = false;
		
		delay = String.valueOf(monitorSchedulingConfigBean.getSchedulerConfig().getDelay());
		
		rendererCollector = true;
	}
	
	public void delete() {
		if (!isAuthorized(92,"monitorSelection")) {
			return;
		}
		
		try {
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
			List<Long> ikrDefinitionIds = monitoringPM.getIkrDefinitionIds(monitorConfig.getId());
			monitoringPM.deleteIkrDefinitions(monitorConfig.getId());			
			for (long id : ikrDefinitionIds) {
				beanPM.flushIkrDefinitionBean(id);
			}
			
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			dataModelPM.deleteMonitor(monitorConfig.getId());	
			beanPM.flushMonitorConfigBean(monitorConfig.getId());
			
			List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
			requests.add(new AdminRequest(AdminRequestCommand.REMOVE, monitorConfig.getId(), AdminComponent.COLLECTOR));
			adminRequestProducer.publish(requests);
			
			MonitorSelectionBean monitorSelectionBean = (MonitorSelectionBean)FacesUtils.getManagedBean("monitorSelectionBean");
			monitorSelectionBean.reloadMonitors();
			
			monitorConfig = null;
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}
	
	private MonitorConfig cloneMonitorConfig(MonitorConfig config) {
		MonitorConfig clone = new MonitorConfig(0, config.getLogicalEnvId(), new String(config.getContext()), config.getMetricDomainConfig(), null, config.isAutoStart());
		
		
		Calendar startTime = config.getSchedulerConfig().getStartTime();
		Calendar endTime = config.getSchedulerConfig().getEndTime();
		IkrMonitorSchedulerConfig schedulerConf = new IkrMonitorSchedulerConfig(0, 
														config.getSchedulerConfig().getType(), 
														config.getSchedulerConfig().getMode(), 
														(startTime!=null)?(Calendar)startTime.clone():null, 
														(endTime!=null)?(Calendar)endTime.clone():null,  
														config.getSchedulerConfig().getDelay());
		
		clone.setSchedulerConfig(schedulerConf);
		
		Map<String, String> attrs = new HashMap<String, String>();
		Map<String, String> confAttrs = config.getAttributes();
		if (confAttrs!=null) {			
			for(String key : config.getAttributes().keySet()) {
				attrs.put(key, confAttrs.get(key));
			}
		}		
		clone.setAttributes(attrs);
		
		List<Integer> connectorConfIds = new ArrayList<Integer>();
		Collection<Integer> ids = config.getConnectorConfigIds();
		if (ids != null) {
			for(int id : ids) {
				connectorConfIds.add(id);
			}
		}
		clone.setConnectorConfigIds(connectorConfIds);
		return clone;
	}
	
	public boolean isRendererCollector() {
		return rendererCollector;
	}
	
	public void closeCollectorPopup(ActionEvent event) {
		if(edit) {
			monitorConfig.setContext(this.context);
			monitorConfig.setAutoStart(this.started);

			metricDomainConfigAttributesBean.init();

			monitorSchedulingConfigBean.changeSchedulerType(this.type);
			monitorSchedulingConfigBean.changeSchedulerMode(this.mode);
			monitorSchedulingConfigBean.startDateValueChanged(this.startDate);
			monitorSchedulingConfigBean.endDateValueChanged(this.endDate);
			monitorSchedulingConfigBean.onChangeStartDateWeekly(this.startDateDay);
			monitorSchedulingConfigBean.onChangeEndDateWeekly(this.endDateDay);
			monitorSchedulingConfigBean.getStartDate().setHour(this.startDateHour);
			monitorSchedulingConfigBean.getStartDate().setMin(this.startDateMin);
			monitorSchedulingConfigBean.getEndDate().setHour(this.endDateHour);
			monitorSchedulingConfigBean.getEndDate().setMin(this.endDateMin);
		}
		rendererCollector = false;
		resetControl();
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}
	
	//-------------Control and style---------------//

	public boolean isContextMandatory() {
		return contextMandatory;
	}

	public String getContextStyle() {
		return contextStyle;
	}

	public boolean isCoupleEnvContextExist() {
		return coupleEnvContextExist;
	}

	public String getEnvStyle() {
		return envStyle;
	}

	public String getCoupleEnvContextErrorMsg() {
		coupleEnvContextErrorMsg = "";
		coupleEnvContextErrorMsgTrim = "";
		coupleEnvContextErrorMsg = logicalEnvSelectionBean.getLogicalEnvName() + " / " + monitorConfig.getContext();
		if(coupleEnvContextErrorMsg.length() > 13) {
			for(int i = 0; i < 13; i++) {
				coupleEnvContextErrorMsgTrim = coupleEnvContextErrorMsgTrim + coupleEnvContextErrorMsg.charAt(i);
			}
		}
		else
			coupleEnvContextErrorMsgTrim = coupleEnvContextErrorMsg;
		
		return "Couple Env/Context (" + coupleEnvContextErrorMsg + ") already exists";
	}

	public String getCoupleEnvContextErrorMsgTrim() {
		return "Couple Env/Context (" + coupleEnvContextErrorMsgTrim + ") already exists";
	}
	
	public void onChangeContext(ActionEvent event) {
		MonitorSelectionBean monitorSelectionBean = (MonitorSelectionBean)FacesUtils.getManagedBean("monitorSelectionBean");
		for(MonitorConfigBean monitor : monitorSelectionBean.getMonitorConfigs()) {
			if(monitor.getContext().equalsIgnoreCase(monitorConfig.getContext())
					&& monitor.getLogicalEnv().getId() == monitorConfig.getLogicalEnvId()
							&& monitor.getId() != monitorConfig.getId()) {
				contextMandatory = false;
				contextStyle = "width:250px; border:1px solid red;";
				coupleEnvContextExist = true;
				envStyle = "width:250px; border:1px solid red;";
				return;
			}
			else {
				contextMandatory = false;
				contextStyle = "width:250px;";
				coupleEnvContextExist = false;
				envStyle = "width:250px;";
			}
		}
	}

	public boolean isStartTimeMustBeActive() {
		return startTimeMustBeActive;
	}

	public boolean isEndTimeMustBeActive() {
		return endTimeMustBeActive;
	}

	public boolean isEndBeforeStart() {
		return endBeforeStart;
	}

	public String getStartLabelStyle() {
		return startLabelStyle;
	}

	public String getEndLabelStyle() {
		return endLabelStyle;
	}

	public String getDayStyle() {
		return dayStyle;
	}

	public String getHourStyle() {
		return hourStyle;
	}

	public String getMinStyle() {
		return minStyle;
	}
	
	public boolean isDelayWrongFormat() {
		return delayWrongFormat;
	}

	public String getDelayStyle() {
		return delayStyle;
	}

	public boolean isDelayMandatory() {
		return delayMandatory;
	}

	private void resetControl() {
		contextMandatory = coupleEnvContextExist = endBeforeStart = endTimeMustBeActive
			= delayMandatory = delayWrongFormat = startTimeMustBeActive = false;
		dayStyle = hourStyle = minStyle = "width: 50px;";
		endLabelStyle = startLabelStyle = "";
		contextStyle = envStyle = "width: 250px;" ;
	}

	private void testEntries() {
		resetControl();
		if(monitorSchedulingConfigBean.getSchedulerConfig().getType().equalsIgnoreCase("one shot")) {
			if(monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("daily")
					|| monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("weekly")
						|| monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("monthly")) {
				if(!monitorSchedulingConfigBean.getStartDate().isActive()) {
					startTimeMustBeActive = true;
					startLabelStyle = "color: red; text-decoration: underline;";
					return;
				}
				else
					startTimeMustBeActive = false;
					startLabelStyle = "";
			}
		}
		else {
//			delay = String.valueOf(monitorSchedulingConfigBean.getSchedulerConfig().getDelay()).trim();
			if (delay.length() == 0 || delay.equalsIgnoreCase("0")) {
				delayMandatory = true;
				delayWrongFormat = false;
			}
			else {
				delayWrongFormat = !StringUtils.isNumeric(delay);
				delayMandatory = false;
			}
			
			if(delayMandatory || delayWrongFormat)
				delayStyle = "width:250px; border:1px solid red;";
			else
				delayStyle = "width:250px;";
			
			if(monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("daily")
					|| monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("weekly")
						|| monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("monthly")) {
				if(!monitorSchedulingConfigBean.getStartDate().isActive()) {
					startTimeMustBeActive = true;
					startLabelStyle = "color: red; text-decoration: underline;";
				}
				else {
					startTimeMustBeActive = false;
					startLabelStyle = "";
				}
				
				if(!monitorSchedulingConfigBean.getEndDate().isActive()) {
					endTimeMustBeActive = true;
					endLabelStyle = "color: red; text-decoration: underline;";
				}
				else {
					endTimeMustBeActive = false;
					endLabelStyle = "";
				}
			}
			if(monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("daily") 
					&& !startTimeMustBeActive && !endTimeMustBeActive) {				
				if(monitorSchedulingConfigBean.getEndDate().getHour() < monitorSchedulingConfigBean.getStartDate().getHour()) {
					endBeforeStart = true;
					hourStyle = "width: 50px; border: 1px red solid;";
					minStyle = "width: 50px;";
				}
				else if(monitorSchedulingConfigBean.getEndDate().getHour() == monitorSchedulingConfigBean.getStartDate().getHour()) {
					if(monitorSchedulingConfigBean.getEndDate().getMin() < monitorSchedulingConfigBean.getStartDate().getMin()) {
						endBeforeStart = true;
						minStyle = "width: 50px; border: 1px red solid;";
						hourStyle = "width: 50px;";
					}
					else {
						endBeforeStart = false;
						hourStyle = "width: 50px;";
						minStyle = "width: 50px;";
					}
				}
				else {
					endBeforeStart = false;
					hourStyle = "width: 50px;";
					minStyle = "width: 50px;";
				}
			}
			if(monitorSchedulingConfigBean.getSchedulerConfig().getMode().equalsIgnoreCase("monthly")
					&& !startTimeMustBeActive && !endTimeMustBeActive) {
				if(monitorSchedulingConfigBean.getEndDate().getDay() < monitorSchedulingConfigBean.getStartDate().getDay()) {
					endBeforeStart = true;
					dayStyle = "width: 50px; border: 1px red solid;";
					hourStyle = "width: 50px;";
					minStyle = "width: 50px;";
				}
				else if(monitorSchedulingConfigBean.getEndDate().getDay() == monitorSchedulingConfigBean.getStartDate().getDay()) {
					if(monitorSchedulingConfigBean.getEndDate().getHour() < monitorSchedulingConfigBean.getStartDate().getHour()) {
						endBeforeStart = true;
						dayStyle = "width: 50px;";
						hourStyle = "width: 50px; border: 1px red solid;";
						minStyle = "width: 50px;";
					}
					else if(monitorSchedulingConfigBean.getEndDate().getHour() == monitorSchedulingConfigBean.getStartDate().getHour()) {
						if(monitorSchedulingConfigBean.getEndDate().getMin() < monitorSchedulingConfigBean.getStartDate().getMin()) {
							endBeforeStart = true;
							dayStyle = "width: 50px;";
							hourStyle = "width: 50px;";
							minStyle = "width: 50px; border: 1px red solid;";
						}
						else {
							endBeforeStart = false;
							dayStyle = hourStyle = minStyle = "width: 50px;";
						}
					}
				}
				else {
					endBeforeStart = false;
					dayStyle = hourStyle = minStyle = "width: 50px;";
				}
			}
		}
	}

	public void setJmsFactory(JmsProcessorFactory jmsFactory) {
		this.jmsFactory = jmsFactory;
	}
	
}
