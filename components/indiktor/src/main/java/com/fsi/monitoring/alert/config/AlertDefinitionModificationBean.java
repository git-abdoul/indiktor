package com.fsi.monitoring.alert.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.AlertDefinition;
import com.fsi.monitoring.alert.AlertDomain;
import com.fsi.monitoring.alert.AlertGroup;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.AlertSubDomain;
import com.fsi.monitoring.alert.AlertValidity;
import com.fsi.monitoring.alert.action.AlertAction;
import com.fsi.monitoring.alert.action.SnmpAlertAction;
import com.fsi.monitoring.alert.action.UserAlertAction;
import com.fsi.monitoring.alert.condition.AlertCondition;
import com.fsi.monitoring.alert.config.definition.AlertConditionBean;
import com.fsi.monitoring.alert.config.definition.AlertSnmpActionBean;
import com.fsi.monitoring.alert.config.definition.AlertUserActionBean;
import com.fsi.monitoring.alert.dao.impl.AbstractAlertDAO;
import com.fsi.monitoring.alert.workflow.AlertWorkflow;
import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorVisitor;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionBean;
import com.fsi.monitoring.datamodel.logicalEnvironment.LogicalEnvSelectionVisitor;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.user.UserPM;
import com.fsi.monitoring.util.AccessControlBean;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.MessageBundleLoader;
import com.fsi.monitoring.utils.IkrInputCalendar;

public class AlertDefinitionModificationBean 
extends AccessControlBean
implements LogicalEnvSelectionVisitor, MetricSelectorVisitor, Serializable {

	private static final long serialVersionUID = -1576701700509413368L;
	
	protected final static Logger logger = Logger.getLogger(AbstractAlertDAO.class);	
	
	private AlertDefinition alertDefinition = null;
	
	private List<AlertConditionBean> alertConditionBeans = null;
	private AlertUserActionBean alertUserActionBean;
	private AlertSnmpActionBean alertSnmpActionBean;
	
	private boolean rendererMetricSelector = false;
	private boolean rendererMetricDetailsPopup = false;
	private boolean rendererNotificationSms = false;
	private boolean rendererNotificationMail = false;
	private boolean rendererNotificationSnmp = false;	
	
	private boolean smsAlreadyActive = false;
	private boolean mailAlreadyActive = false;
	private boolean snmpAlreadyActive = false;
	
	private boolean metricSelectorOn = false;
	
	private boolean conditionsError = false;
	private boolean computationsError = false;

	private SelectItem[] hourItems;
	private SelectItem[] minItems;
	
	private LogicalEnvSelectionBean logicalEnvSelectionBean;
	
	private boolean deleteConfirmation = false;
	
	private String actionBack = null;
	
	private int maxAlertConditionId = 0;
	
	private static final SelectItem[] levels = {
		new SelectItem(1,MessageBundleLoader.getMessage("alert.level.label.1")),
		new SelectItem(2,MessageBundleLoader.getMessage("alert.level.label.2")),
		new SelectItem(3,MessageBundleLoader.getMessage("alert.level.label.3")),
		new SelectItem(4,MessageBundleLoader.getMessage("alert.level.label.99"))
	};
	
	private Map<Integer, SelectItem[]> domainItemMap;
	private Map<Integer, SelectItem[]> subdomainItemMap;		
	
	private String environment;
	private SelectItem[] environmentItems;
	
	private int group;
	private SelectItem[] groupItems;
	
    private int domain;	
    private SelectItem[] domainItems;
    
    private int subdomain;
    private SelectItem[] subdomainItems;

    private String groupStr;
    private String domainStr;
    private String subdomainStr;
    
	private LogicalEnv logicalEnv; 
    private MetricSelectorBean metricSelectorBean;	
    private List<AlertWorkflow> alertWorkflowFilters;
    
    private String hourStart;
    private String hourEnd;
    
    private String minStart;
    private String minEnd;
    
    private String raisingDelay = "0";
    
    private List<AlertValidity> validities;    
    private List<AlertValidity> validitiesOld;
    
    private boolean rendererAlertValiditySetup;
    
    private IkrDefinitionBean selectedIkrDefinitionForMetricDetails;
    
    public AlertDefinitionModificationBean(AlertDefinition alertDefinition) {
		this();		
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(true);
		this.alertDefinition = alertDefinition;	
		validities = alertDefinition.getAlertValidities();
	}
	
	public AlertDefinitionModificationBean() {
		logicalEnvSelectionBean = new LogicalEnvSelectionBean(true);
	}
	
	public void closeAlertValiditySetup(ActionEvent action) {
		rendererAlertValiditySetup = false;
	}
	
	public void openAlertValiditySetup(ActionEvent action) {
		rendererAlertValiditySetup = true;
		hourStart = "";
		minStart = "";
		hourEnd = "";
		minEnd = "";
	}
	
	public void addAlertValidity(ActionEvent action) {
		int hour = 0;
		int min = 0;
		
		IkrInputCalendar start = null;
		if ((hourStart!=null && hourStart.length()>0) && (minStart!=null && minStart.length()>0)) {
			try {
				hour = Integer.parseInt(hourStart);
				min =  Integer.parseInt(minStart);
			}
			catch (NumberFormatException e) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("WRONG Format " + e.getMessage() + "\n Must be a Numeric");
				return;
			}
			
			start = new IkrInputCalendar();
			start.setHour(hour);
			start.setMinute(min);
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("The Start Date cannot be EMPTY");
			return;
		}
		
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.HOUR_OF_DAY, hour);
		startCal.set(Calendar.MINUTE, min);
		
		IkrInputCalendar end = null;
		Calendar endCal = null;
		if ((hourEnd!=null && hourEnd.length()>0) && (minEnd!=null && minEnd.length()>0)) {
			hour = Integer.parseInt(hourEnd);
			min =  Integer.parseInt(minEnd);
			end = new IkrInputCalendar();
			end.setHour(hour);
			end.setMinute(min);
			
			endCal = Calendar.getInstance();
			endCal.set(Calendar.HOUR_OF_DAY, hour);
			endCal.set(Calendar.MINUTE, min);
		}
		
		if (start.getHour()>23) {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.setRendered(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("Hour must be between 0 and 23");
			return;
		}
		
		if (start.getMinute()>59) {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.setRendered(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("Minute must be between 0 and 59");
			return;
		}
		
		if (end!=null){
			if (end.getHour()>23) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.setRendered(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("Hour must be between 0 and 23");
				return;
			}
			
			if (end.getMinute()>59) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.setRendered(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("Minute must be between 0 and 59");
				return;
			}
		}
		
		
		if (startCal!=null && endCal!=null) {
			if (startCal.getTime().after(endCal.getTime())) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.setRendered(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("The Start Date is after the End Time !!!");
				return;
			}
		}		
		
		
		validities.add(new AlertValidity(start, end));
		
		hourStart = "";
		minStart = "";
		hourEnd = "";
		minEnd = "";
		
		rendererAlertValiditySetup = false;
	}
	
	public void removeAlertValidity(ActionEvent action) {
		AlertValidity validity = (AlertValidity)action.getComponent().getAttributes().get("obj");	
		validities.remove(validity);
	}
	
	public void removeAllAlertValidity(ActionEvent action) {
		Iterator<AlertValidity> alertValidityIterator = validities.iterator();
		while (alertValidityIterator.hasNext()) {
			AlertValidity alertValidity = alertValidityIterator.next();
			alertValidityIterator.remove();
		}
	}
	
	public void delete(ActionEvent action) {
		try {
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			if (alertDefinition.getId()>0) 
			{
				alertPM.deleteAlertDefinition(alertDefinition.getId());
				beanPM.flushAlertDefinitionBean(alertDefinition.getId());
			}
		} catch (Exception exc) {
			System.out.println(exc);
		}	
		
		AllAlertDefinitionBean allAlertDefinitionBean = 
			(AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		
		allAlertDefinitionBean.init(action);
	}
	
	public String getActionBack() {
		return actionBack;
	}
	
	public void initCreate(ActionEvent action) {		
			setAction("alertProperties");
			maxAlertConditionId = 0;
			initHour();
			initMin();
			
			if (metricSelectorBean == null) {
				metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
				metricSelectorBean.init();
				metricSelectorBean.accept(this);
				metricSelectorBean.setRendered(true);
			}
			else {
				metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
				metricSelectorBean.accept(this);
				metricSelectorBean.setRendered(true);
			}
						
			alertDefinition = new AlertDefinition();
			
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
			
			SnmpAlertAction snmpAlertAction = new SnmpAlertAction();
			alertSnmpActionBean = new AlertSnmpActionBean(snmpAlertAction,alertPM);
			alertDefinition.addAlertAction(snmpAlertAction);
			
			UserAlertAction userAlertAction = new UserAlertAction();
			alertUserActionBean = new AlertUserActionBean(userAlertAction,userPM);			
			alertDefinition.addAlertAction(userAlertAction);
			
			alertConditionBeans = new ArrayList<AlertConditionBean>();
			
			alertWorkflowFilters = alertDefinition.getAlertWorkflowFilters();
			
			validities = alertDefinition.getAlertValidities();
			raisingDelay = "0";
			
			initGroupItems();
			
			logicalEnvSelectionBean.accept(this);
			logicalEnvSelectionBean.init();
	}
		
	public void init(Long alertId, String actionBack) {
		this.actionBack = actionBack;
		initHour();
		initMin();
		
		maxAlertConditionId = 0;
		try {							
			if (alertId != null) {
				AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
				MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
				DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
				UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
				alertDefinition = alertPM.getAlertDefinitionById(alertId);
				raisingDelay = String.valueOf(alertDefinition.getRaisingDelay());
				validities = alertDefinition.getAlertValidities();
				alertWorkflowFilters = alertDefinition.getAlertWorkflowFilters();
				alertConditionBeans = new ArrayList<AlertConditionBean>();
				for (AlertCondition alertCondition : alertDefinition.getAlertConditions()) {
					AbstractIkrDefinition ikrDefinition = monitoringPM.getIkrDefinition(alertCondition.getIkrDefinitionId());
					if (ikrDefinition != null) {
						BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
						IkrDefinitionBean beanDef = beanPM.getIkrDefinitionBean(ikrDefinition.getId());
						if (beanDef.getIkrCategory()!= null) {
							AlertConditionBean alertConditionBean = new AlertConditionBean(alertCondition,beanDef);
							alertConditionBeans.add(alertConditionBean);
							maxAlertConditionId = Math.max(maxAlertConditionId, alertCondition.getId());
						}
					}
				}
				List<AlertAction> alertActions = (List<AlertAction>)alertDefinition.getAlertActions();
				if (alertActions != null && alertActions.size() >0) {
					for (AlertAction alertAction : alertActions) {
						if (alertAction instanceof UserAlertAction) {
							alertUserActionBean = new AlertUserActionBean((UserAlertAction)alertAction,userPM);
						} else {
							alertSnmpActionBean = new AlertSnmpActionBean((SnmpAlertAction)alertAction,alertPM);
						}
					}
				} 
				if (alertUserActionBean == null) {
					UserAlertAction userAlertAction = new UserAlertAction();
					alertUserActionBean = new AlertUserActionBean(userAlertAction,userPM);			
					alertDefinition.addAlertAction(userAlertAction);
				}
				
				if (alertSnmpActionBean == null) {
					SnmpAlertAction snmpAlertAction = new SnmpAlertAction();
					alertSnmpActionBean = new AlertSnmpActionBean(snmpAlertAction,alertPM);
					alertDefinition.addAlertAction(snmpAlertAction);
				}
				
				logicalEnv = dataModelPM.getLogicalEnv(alertDefinition.getLogicalEnv());
			} 
			
			if (metricSelectorBean == null) {
				metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
				metricSelectorBean.init();
				metricSelectorBean.accept(this);
				metricSelectorBean.setRendered(true);
			}
			else {
				metricSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
				metricSelectorBean.accept(this);
				metricSelectorBean.setRendered(true);
			}
			
			initGroupItems();
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}
	
	public AlertDefinition getAlertDefinition() {
		return alertDefinition;
	}
	
	public LogicalEnv getLogicalEnv() {
		return logicalEnv;
	}

	public void setLogicalEnv(LogicalEnv logicalEnv) {
		this.logicalEnv = logicalEnv;
	}

	public AlertSnmpActionBean getAlertSnmpActionBean() {
		return alertSnmpActionBean;
	}
	
	public AlertUserActionBean getAlertUserActionBean() {
		return alertUserActionBean;
	}	
	
	public void select(Collection<ModifiableMetricBean> ikrDefinitionBeans) {
		// Get the (new) list of selected ikrDefinitions from ikrSelector		
		for (ModifiableMetricBean bean : ikrDefinitionBeans) {
			int alertConditionId = ++maxAlertConditionId;
//			IkrCategory ikrCategory = ikrDefinitionBean.getIkrCategory();
//			AbstractIkrDefinition ikrDefinition = ikrDefinitionBean.getIkrDefinition();
			
			AlertConditionBean alertConditionBean = new AlertConditionBean(alertConditionId, (IkrDefinitionBean)bean.getMetricGroupBean());
			alertConditionBeans.add(alertConditionBean);
		}
	}	
	
	public void deselect(Collection<ModifiableMetricBean> ikrDefinitionBeans) {
	}
	
	public Collection<AlertConditionBean> getAlertConditionBeans() {
		Collections.sort(alertConditionBeans, new Comparator<AlertConditionBean>() {
			public int compare(AlertConditionBean o1, AlertConditionBean o2) {
				return (new Integer(o1.getId())).compareTo(new Integer(o2.getId()));
			}
		});
		return alertConditionBeans;
	}	
	
	public String getGroupStr() {
		return groupStr;
	}
	
	public String getDomainStr() {
		return domainStr;
	}
	
	public String getSubDomainStr() {
		return subdomainStr;
	}	
	
	public void checkAlertValidity() {	
		if(alertDefinition.getName()==null || alertDefinition.getName().length()==0) {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("The alert definition name is empty");
		} else {		
			List<AlertCondition> alertConditions = alertDefinition.getAlertConditions();
			if (alertConditions == null) {
				alertConditions = new ArrayList<AlertCondition>(alertConditionBeans.size());
			} else {
				alertConditions.clear();
			}
			
			for(AlertConditionBean alertConditionBean : alertConditionBeans) {
				try {
					AlertCondition alertCondition = alertConditionBean.validate();
					alertConditions.add(alertCondition);
				} catch (IllegalArgumentException exc) {
					ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
					error.init();
					error.setRendered(true);
					error.setType(ErrorMessageBean.WARNING);
					error.addMessage("Alert Condition value " + alertConditionBean.getValue() + " is not correct");
				}
			}	
		}
	}
	
	public void removeCondition(ActionEvent action) {
		AlertConditionBean beanToRemove = (AlertConditionBean)action.getComponent().getAttributes().get("obj");
	
		// necessary to remove this bean otherwise it will be mapped again at the next getConditionBeans
		long idToRemove = beanToRemove.getId();
		
		Iterator<AlertConditionBean> alertConditionIterator = alertConditionBeans.iterator();
		while (alertConditionIterator.hasNext()) {
			AlertConditionBean alertConditionBean = alertConditionIterator.next();
			long tmpBeanId = alertConditionBean.getId() ;
			if (tmpBeanId == idToRemove) {
				alertConditionIterator.remove();
			}
		}
		
//		MetricSelectorBean ikrSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("ikrSelectorBean");
//		Collection<ModifiableMetricBean> ikrDefinitionBeans = ikrSelectorBean.getSelectedBeans();	
//		
//		Iterator<ModifiableMetricBean> ikrDefinitionBeansIterator = ikrDefinitionBeans.iterator();
//		while (ikrDefinitionBeansIterator.hasNext()) {
//			ModifiableMetricBean modifiableIkrDefinitionBean = ikrDefinitionBeansIterator.next();
//			IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)modifiableIkrDefinitionBean.getMetricGroupBean();
//			long ikrDefinitionId = ikrDefinitionBean.getIkrDefinition().getId();
//			if (ikrDefinitionId == ikrDefinitionIdToRemove) {
//				ikrDefinitionBeansIterator.remove();
//				break;
//			}
//		}
//	
//		Iterator<AlertConditionBean> alertConditionIterator = alertConditionBeans.iterator();
//		while (alertConditionIterator.hasNext()) {
//			AlertConditionBean bean = alertConditionIterator.next();
//			if (beanToRemove == bean) {
//				alertConditionIterator.remove();
//			}
//		}
	}	
	
	public void removeAllCondition(ActionEvent action) {
//		AlertConditionBean beanToRemove = (AlertConditionBean)action.getComponent().getAttributes().get("obj");
//	
//		// necessary to remove this bean otherwise it will be mapped again at the next getConditionBeans
//		long idToRemove = beanToRemove.getId();
		
		Iterator<AlertConditionBean> alertConditionIterator = alertConditionBeans.iterator();
		while (alertConditionIterator.hasNext()) {
			AlertConditionBean alertConditionBean = alertConditionIterator.next();
			alertConditionIterator.remove();
		}
	}
	
	public void duplicateCondition(ActionEvent action) {
		AlertConditionBean beanToDuplicate = (AlertConditionBean)action.getComponent().getAttributes().get("obj");
		
		int alertConditionId = ++maxAlertConditionId;
		
		AlertConditionBean alertConditionBean = beanToDuplicate.duplicate();
		alertConditionBean.setId(alertConditionId);
		
		alertConditionBeans.add(alertConditionBean);
	}
	
	public boolean isPreview() {
		return false;
	}
	
	public SelectItem[] getLevels() {
		return levels;
	}
	
	public void save() {
		
		checkAlertValidity();
		saveNotification();
		
		alertDefinition.setGroup(group);
		alertDefinition.setDomain(domain);
		alertDefinition.setSubDomain(subdomain);
		
		alertDefinition.setRaisingDelay(Long.valueOf(raisingDelay));
		
		alertDefinition.setAlertWorkflowFilters(alertWorkflowFilters);
		alertDefinition.setAlertValidities(validities);
		
		try {
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			if (alertDefinition.getId()==0) 
			{
				alertPM.createAlertDefinition(alertDefinition);
			}
			else 
			{
				alertPM.updateAlertDefinition(alertDefinition);
				BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
				beanPM.flushAlertDefinitionBean(alertDefinition.getId());
			}
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}	
		
		AllAlertDefinitionBean allAlertDefinitionBean = 
			(AllAlertDefinitionBean)FacesUtils.getManagedBean("allAlertDefinitionBean");
		
		allAlertDefinitionBean.init(null);
		setAction("alertDefinitionList");
	}
	
	private void initGroupItems() {
		domainItemMap = new HashMap<Integer, SelectItem[]>();
		subdomainItemMap = new HashMap<Integer, SelectItem[]>();
		
		
		group = alertDefinition.getGroup();
		domain = alertDefinition.getDomain();
		subdomain = alertDefinition.getSubDomain();
		
		try {
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());		
			Map<Integer, AlertGroup> groupMap = alertPM.getAllAlertGroups();

			Collection<AlertGroup> alertGroups = new ArrayList<AlertGroup>(groupMap.values());
			
			groupItems = new SelectItem[alertGroups.size()];
			
			boolean reset = true;
			
			int i=0;
			for (AlertGroup alertGroup : alertGroups) {
				groupItems[i++] = new SelectItem(alertGroup.getId(), alertGroup.getValue());
				if (alertGroup.getId() == group) {
					groupStr =  alertGroup.getValue();
					reset = false;
				}
			}
			
			if (reset) {
				group = (Integer)groupItems[0].getValue();
				groupStr = (String)groupItems[0].getLabel();
			}
			
			changeDomainItems();
		} catch(Exception exc) {
			logger.error(exc);
		}
	}
	
	private void changeDomainItems() {
		domainItems = domainItemMap.get(group);

		boolean reset = true;
		
		if (domainItems == null) {
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			Map<Integer, AlertDomain> domainMap = null;
			
			try {
				domainMap = alertPM.getAllAlertDomainsByGroupId(group);
			} catch(Exception exc) {
				logger.error(exc);
			}
			
			Collection<AlertDomain> alertDomains =  new ArrayList<AlertDomain>(domainMap.values());
			
			if (alertDomains != null && !alertDomains.isEmpty()) {
				
				domainItems = new SelectItem[alertDomains.size()];
				
				int i=0;
				for (AlertDomain alertDomain : alertDomains) {
					domainItems[i++] = new SelectItem(alertDomain.getId(), alertDomain.getValue());
					if (alertDomain.getId() == domain) {
						domainStr =  alertDomain.getValue();
						reset = false;
					}
				}
				domainItemMap.put(group, domainItems);
			} else {
				domainItems = new SelectItem[1];
				domainItems[0] = new SelectItem(0, "N/A");
			}
		}
		

		if (reset) {
			domain = (Integer)domainItems[0].getValue();
			domainStr = (String)domainItems[0].getLabel();
		}
		
		changeSubdomainItems();
	}
	
	private void changeSubdomainItems() {
		subdomainItems = subdomainItemMap.get(domain);
		
		boolean reset = true;
		
		if (subdomainItems == null) {
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			Collection<AlertSubDomain> alertSubDomains = null;

			try {
				alertSubDomains = alertPM.getAllAlertSubDomainsByDomainId(domain);
			} catch (Exception exc) {
				logger.error(exc);
			}
			
			if (alertSubDomains != null && !alertSubDomains.isEmpty()) {
			
				subdomainItems = new SelectItem[alertSubDomains.size()];
				
				int i=0;
				for (AlertSubDomain alertSubDomain : alertSubDomains) {
					subdomainItems[i++] = new SelectItem(alertSubDomain.getId(), alertSubDomain.getValue());
					if (alertSubDomain.getId() == subdomain) {
						subdomainStr =  alertSubDomain.getValue();
						reset = false;
					}
				}
				subdomainItemMap.put(domain, subdomainItems);
			} else {
				subdomainItems = new SelectItem[1];
				subdomainItems[0] = new SelectItem(0, "N/A");
			}
		}
		
		if (reset) {
			subdomain = (Integer)subdomainItems[0].getValue();
			subdomainStr = (String)subdomainItems[0].getLabel();
		}

	}
	
	public void onChangeGroup(ValueChangeEvent evnt) {
		group = (Integer)evnt.getNewValue();
		changeDomainItems();
	}	
	
	public void onChangeDomain(ValueChangeEvent evnt) {
		domain = (Integer)evnt.getNewValue();	
		changeSubdomainItems();
	}
	
	public void onChangeSubDomain(ValueChangeEvent evnt) {
		subdomain = (Integer)evnt.getNewValue();
	}	
	
	public SelectItem[] getEnvironmentItems() {
		return environmentItems;
	}
	
	public SelectItem[] getGroupItems() {
		return groupItems;
	}

	public SelectItem[] getDomainItems() {
		return domainItems;
	}

	public SelectItem[] getSubdomainItems() {
		return subdomainItems;
	}

	public String getEnvironment() {
		return environment;
	}
	
	public int getGroup() {
		return group;
	}

	public int getSubDomain() {
		return subdomain;
	}

	public int getDomain() {
		return domain;
	}
	
	public void setEnvironment() {}
	public void setGroup(int group) {}
	public void setDomain(int domain) {}	
	public void setSubDomain(int subDomain) {}

	public boolean isDeleteConfirmation() {
		return deleteConfirmation;
	}

	public void setDeleteConfirmation(boolean deleteConfirmation) {
		this.deleteConfirmation = deleteConfirmation;
	}

	public void changeLogicalEnv(int logicalEnvId) {
		alertDefinition.setLogicalEnv(logicalEnvId);
	}
	
	public boolean isAlertWorflowFilterRendered() {
		return (alertUserActionBean.isMail() || alertUserActionBean.isSms() || alertSnmpActionBean.isSnmp());
	}

	public boolean isLowWfw() {		
		return alertWorkflowFilters.contains(AlertWorkflow.UP_1);
	}

	public void setLowWfw(boolean lowWfw) {		
		AlertWorkflow workflow = AlertWorkflow.UP_1;		
		alertWorkflowFilters.remove(workflow);
		if (lowWfw)
			alertWorkflowFilters.add(workflow);
	}

	public boolean isMediumWfw() {
		return alertWorkflowFilters.contains(AlertWorkflow.UP_2);
	}

	public void setMediumWfw(boolean mediumWfw) {		
		AlertWorkflow workflow = AlertWorkflow.UP_2;		
		alertWorkflowFilters.remove(workflow);
		if (mediumWfw)
			alertWorkflowFilters.add(workflow);
	}

	public boolean isHighWfw() {
		return alertWorkflowFilters.contains(AlertWorkflow.UP_3);
	}

	public void setHighWfw(boolean highWfw) {
		AlertWorkflow workflow = AlertWorkflow.UP_3;		
		alertWorkflowFilters.remove(workflow);
		if (highWfw)
			alertWorkflowFilters.add(workflow);
	}

	public boolean isNotRunningWfw() {
		return alertWorkflowFilters.contains(AlertWorkflow.MAX);
	}

	public void setNotRunningWfw(boolean notRunningWfw) {
		AlertWorkflow workflow = AlertWorkflow.MAX;		
		alertWorkflowFilters.remove(workflow);
		if (notRunningWfw)
			alertWorkflowFilters.add(workflow);
	}

	public boolean isAutoDownWfw() {
		return alertWorkflowFilters.contains(AlertWorkflow.DOWN);
	}

	public void setAutoDownWfw(boolean autoDownWfw) {
		AlertWorkflow workflow = AlertWorkflow.DOWN;		
		alertWorkflowFilters.remove(workflow);
		if (autoDownWfw)
			alertWorkflowFilters.add(workflow);
	}

	public boolean isUserAckWfw() {
		return alertWorkflowFilters.contains(AlertWorkflow.ACK);
	}

	public void setUserAckWfw(boolean userAckWfw) {
		AlertWorkflow workflow = AlertWorkflow.ACK;		
		alertWorkflowFilters.remove(workflow);
		if (userAckWfw)
			alertWorkflowFilters.add(workflow);
	}	
	
	public boolean isRendererMetricSelector() {
		return rendererMetricSelector;
	}

	public boolean isRendererMetricDetailsPopup() {
		return rendererMetricDetailsPopup;
	}

	public boolean isRendererNotificationSms() {
		return rendererNotificationSms;
	}
	
	public boolean isRendererNotificationMail() {
		return rendererNotificationMail;
	}

	public boolean isRendererNotificationSnmp() {
		return rendererNotificationSnmp;
	}

//	public boolean isAlertWorflowFilterRendered() {
//		return alertWorflowFilterRendered;
//	}

	public void openMetricSelectorPopup(ActionEvent event) {
		rendererMetricSelector = true;
	}
	
	public void openMetricDetailsPopup(ActionEvent event) {
		AlertConditionBean alertCondition = (AlertConditionBean)event.getComponent().getAttributes().get("obj");
		selectedIkrDefinitionForMetricDetails = alertCondition.getIkrDefinitionBean();
		rendererMetricDetailsPopup = true;
	}	

	public IkrDefinitionBean getSelectedIkrDefinitionForMetricDetails() {
		return selectedIkrDefinitionForMetricDetails;
	}

	public void closeMetricSelectorPopup(ActionEvent event) {
		rendererMetricSelector = false;
	}
	
	public void closeMetricDetailsPopup(ActionEvent event) {
		rendererMetricDetailsPopup = false;
	}
	
	public void openNotificationSmsPopup(ActionEvent event) {
		boolean sms = getAlertUserActionBean().isSms();
		if (sms == true)
			smsAlreadyActive = true;
		rendererNotificationSms = true;
	}
	
	public void closeNotificationSmsPopup(ActionEvent event) {
		boolean sms = getAlertUserActionBean().isSms();
		if (sms == true && smsAlreadyActive == false)
			getAlertUserActionBean().setSms(false);
		else if (sms == false && smsAlreadyActive == true)
			getAlertUserActionBean().setSms(true);
		rendererNotificationSms = false;
	}
	
	public void openNotificationMailPopup(ActionEvent event) {
		boolean mail = getAlertUserActionBean().isMail();
		if (mail == true)
			mailAlreadyActive = true;
		rendererNotificationMail = true;
	}
	
	public void closeNotificationMailPopup(ActionEvent event) {
		boolean mail = getAlertUserActionBean().isMail();
		if (mail == true && mailAlreadyActive == false)
			getAlertUserActionBean().setMail(false);
		else if (mail == false && mailAlreadyActive == true)
			getAlertUserActionBean().setMail(true);
		rendererNotificationMail = false;
	}
	
	public void openNotificationSnmpPopup(ActionEvent event) {
		boolean snmp = getAlertSnmpActionBean().isSnmp();
		if (snmp == true)
			snmpAlreadyActive = true;
		rendererNotificationSnmp = true;
	}
	
	public void closeNotificationSnmpPopup(ActionEvent event) {
		boolean snmp = getAlertSnmpActionBean().isSnmp();
		if (snmp == true && snmpAlreadyActive == false)
			getAlertSnmpActionBean().setSnmp(false);
		else if (snmp == false && snmpAlreadyActive == true)
			getAlertSnmpActionBean().setSnmp(true);
		rendererNotificationSnmp = false;
	}
	
	public void saveNotification() {
		boolean sms = getAlertUserActionBean().isSms();
		if(getAlertUserActionBean().isSms())
			alertUserActionBean.setSms(true);
		else
			alertUserActionBean.setSms(false);
		
		if(getAlertUserActionBean().isMail())
			alertUserActionBean.setMail(true);
		else
			alertUserActionBean.setMail(false);
		
		if(getAlertSnmpActionBean().isSnmp())
			alertSnmpActionBean.setSnmp(true);
		else
			alertSnmpActionBean.setSnmp(false);
			
//		if (rendererNotificationSms == true) {
//			boolean sms = getAlertUserActionBean().isSms();
//			if(sms == true)
//				getAlertUserActionBean().setSms(true);
//			else
//				getAlertUserActionBean().setSms(false);
//			smsAlreadyActive = false;
//			rendererNotificationSms = false;
//		}
//		else if (rendererNotificationMail == true) {
//			boolean mail = getAlertUserActionBean().isMail();
//			if(mail == true)
//				getAlertUserActionBean().setMail(true);
//			else
//				getAlertUserActionBean().setMail(false);
//			mailAlreadyActive = false;
//			rendererNotificationMail = false;
//		}
//		else {
//			boolean snmp = getAlertSnmpActionBean().isSnmp();
//			if(snmp == true)
//				getAlertSnmpActionBean().setSnmp(true);
//			else
//				getAlertSnmpActionBean().setSnmp(false);
//			snmpAlreadyActive = false;
//			rendererNotificationSnmp = false;
//		}
	}

	public String getHourStart() {
		return hourStart;
	}

	public void setHourStart(String hourStart) {
		this.hourStart = hourStart;
	}

	public String getHourEnd() {
		return hourEnd;
	}

	public void setHourEnd(String hourEnd) {
		this.hourEnd = hourEnd;
	}

	public String getMinStart() {
		return minStart;
	}

	public void setMinStart(String minStart) {
		this.minStart = minStart;
	}

	public String getMinEnd() {
		return minEnd;
	}

	public void setMinEnd(String minEnd) {
		this.minEnd = minEnd;
	}

	public List<AlertValidity> getValidities() {
		Collections.sort(validities, new Comparator<AlertValidity>() {
			public int compare(AlertValidity o1, AlertValidity o2) {
				return o1.getStartStr().compareTo(o2.getStartStr());
			}
		});
		return validities;
	}
	
	public int getValiditySize() {
		return validities.size();
	}
	
	public void feedValiditiesOld() {
		validitiesOld = new ArrayList<AlertValidity>();
		for(AlertValidity val : validities) {
			validitiesOld.add(val);
		}
	}

	public List<AlertValidity> getValiditiesOld() {
		return validitiesOld;
	}

	public boolean isRendererAlertValiditySetup() {
		return rendererAlertValiditySetup;
	}

	public LogicalEnvSelectionBean getLogicalEnvSelectionBean() {
		return logicalEnvSelectionBean;
	}	
	
	public boolean getListRendered() {
		return getAlertConditionBeans().size() > 0;
	}
	
	public void setFromAlertDefinition(boolean value) {
	    if(metricSelectorBean != null)
	    	metricSelectorBean.setFromAlertDefinition(value);
	}

	public String getConditionsPanelStyle() {
		if(conditionsError && getListRendered() == false) {
			if(metricSelectorOn && metricSelectorBean.isSearchON())
				return "border: 1px solid red; height: 479px; width: 190px;";
			else if(metricSelectorOn && !metricSelectorBean.isSearchON())
				return "border: 1px solid red; height: 555px; width: 190px;";
			else 
				return "border: 1px solid red; height: 240px; width: 807px;";
		}
		else {
			if(metricSelectorOn && metricSelectorBean.isSearchON())
				return "border: 1px solid #336699; height: 479px; width: 190px;";
			else if(metricSelectorOn && !metricSelectorBean.isSearchON())
				return "border: 1px solid #336699; height: 555px; width: 190px;";
			else 
				return "border: 1px solid #336699; height: 240px; width: 807px;";
		}
	}
	
	public String getComputationsStyle() {
		if(computationsError)
			return "border: 1px solid red; width: 100%;";
		else
			return "border: 1px solid #336699; width: 100%;";
	}
	
	public String getMetricSelectorPanelStyle() {
		if(conditionsError && getListRendered() == false) {
			if(metricSelectorOn && metricSelectorBean.isSearchON())
				return "border: 1px solid red; height: 481px;";
			else if(metricSelectorOn && !metricSelectorBean.isSearchON())
				return "border: 1px solid red; height: 555px;";
			else 
				return "border: 1px solid red;";
		}
		else {
			if(metricSelectorOn && metricSelectorBean.isSearchON())
				return "border: 1px solid #336699; height: 481px;";
			else if(metricSelectorOn && !metricSelectorBean.isSearchON())
				return "border: 1px solid #336699; height: 555px;";
			else 
				return "border: 1px solid #336699;";
		}
	}
	
	public String getConditionsPanelScrollStyle() {
		if(metricSelectorOn)
			return "190px";
		else 
			return "807px";
	}
	
	public void handleMetricSelectorPanel(ActionEvent evt) {
		metricSelectorOn = !metricSelectorOn;
	}

	public boolean isMetricSelectorOn() {
		return metricSelectorOn;
	}

	public String getPanelGridColumns() {
		if(metricSelectorOn)
			return "2";
		else
		return "1";
	}
	
	private void initHour() {
		hourItems = new SelectItem[24];
		for(int i=0;i<24;i++) {
			hourItems[i] =  new SelectItem(i, String.valueOf(i));
		}
	}
	
	private void initMin() {
		minItems = new SelectItem[60];
		for(int i=0;i<10;i++) {
			minItems[i] =  new SelectItem(i, "0" + String.valueOf(i));
		}
		for(int i=10;i<60;i++) {
			minItems[i] =  new SelectItem(i, String.valueOf(i));
		}
	}

	public SelectItem[] getHourItems() {
		return hourItems;
	}

	public SelectItem[] getMinItems() {
		return minItems;
	}	
	
	public void onChangeHourStart(ValueChangeEvent e) {
		hourStart = (String)e.getNewValue();
	}
	
	public void onChangeMinStart(ValueChangeEvent e) {
		minStart = (String)e.getNewValue();
	}
	
	public void onChangeHourEnd(ValueChangeEvent e) {
		hourEnd = (String)e.getNewValue();
	}
	
	public void onChangeMinEnd(ValueChangeEvent e) {
		minEnd = (String)e.getNewValue();
	}

	public String getRaisingDelay() {
		return raisingDelay;
	}

	public void setRaisingDelay(String raisingDelay) {
		this.raisingDelay = raisingDelay;
	}
	
	public boolean getConditionsError() {
		return conditionsError;
	}
	
	public boolean getComputationsError() {
		return computationsError;
	}

	public void setConditionsError(boolean conditionsError) {
		this.conditionsError = conditionsError;
	}

	public void setComputationsError(boolean computationsError) {
		this.computationsError = computationsError;
	}

	public boolean isDateRenderer() {
		if(alertDefinition.getCreationDate() == null && alertDefinition.getLastUpdateDate() == null)
			return false;
		else
			return true;
	}
}
