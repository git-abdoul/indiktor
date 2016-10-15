package com.fsi.monitoring.admin.console;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.AdminRequestCommand;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.admin.console.bean.IkrAdminLoggingBean;
import com.fsi.monitoring.admin.console.bean.IkrEventLogBean;
import com.fsi.monitoring.admin.console.bean.LoggingCategoryBean;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsP2PProducer;
import com.fsi.monitoring.jms.JmsMulticastProducer;
import com.fsi.monitoring.jms.JmsProcessorFactory;
import com.fsi.monitoring.jms.JmsProcessorType;
import com.fsi.monitoring.scheduler.config.IkrJobSchedulerConfig;
import com.fsi.monitoring.util.AccessControlBean;

public class AdminConsoleBean extends AccessControlBean implements Serializable{
	private static final long serialVersionUID = -7110028591829190076L;
	private static final Logger logger = Logger.getLogger(AdminConsoleBean.class);	
	
	private DataModelPM dataModelPM;
	
	private JmsProcessorFactory jmsFactory;
	private JmsMulticastProducer adminRequestProducer;
	
	private AdminConsole connectorConsole;
	private AdminConsole collectorConsole;
	private AdminConsole jobSchedulerConsole;	
	
	private AdminConsole selectedAdminConsole;
	
	private IkrEventLogBean selectedEventLogBean;
	
	private boolean renderEventLogDetails;
	private boolean renderFilterScreen;
	
	private Set<IkrAdminLoggingCategory> logFilters; 
	private Map<Integer, LoggingCategoryBean> logFilterBeans;
	
	public void initBean() {
		adminRequestProducer = (JmsMulticastProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.ADMIN_REQUEST);
		connectorConsole = new AdminConsole(AdminComponent.CONNECTOR,adminRequestProducer, (JmsP2PProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.HEARTBEAT_CONNECTOR));
		collectorConsole = new AdminConsole(AdminComponent.COLLECTOR, adminRequestProducer,(JmsP2PProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.HEARTBEAT_COLLECTOR));
		jobSchedulerConsole = new AdminConsole(AdminComponent.JOB_TASK, adminRequestProducer,(JmsP2PProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.HEARTBEAT_JOB_TASK));
		
		logFilterBeans = new HashMap<Integer, LoggingCategoryBean>();
		logFilters = new HashSet<IkrAdminLoggingCategory>();
		for (IkrAdminLoggingCategory category : IkrAdminLoggingCategory.values()) {
			logFilters.add(category);
			LoggingCategoryBean categoryBean = new LoggingCategoryBean(category);
			categoryBean.setSelected(true);
			logFilterBeans.put(category.getLevel(), categoryBean);
		}
	}
	
	public void init(ActionEvent action) {
		if (!isAuthorized(100,"adminConsole")) {
			return;
		}
		
		try {			
			List<Integer> idInts = dataModelPM.getConnectorConfigIds();
			List<Long> ids = new ArrayList<Long>();
			for (int id : idInts) {
				ids.add((long)id);
			}
			connectorConsole.initConsole(ids);
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		try {			
			List<Long> ids = dataModelPM.getMonitorConfigIds(0);
			collectorConsole.initConsole(ids);
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		try {	
			List<Long> ids = new ArrayList<Long>();
			Map<Integer, IkrJobSchedulerConfig> configs = dataModelPM.getJobSchedulerConfigs(0);
			for (int id : configs.keySet()) {
				ids.add((long)id);
			}
			jobSchedulerConsole.initConsole(ids);
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}		
	}
	
	public List<LoggingCategoryBean> getLogFilterBeans() {
		List<LoggingCategoryBean> logFilterBeansToSort = new ArrayList<LoggingCategoryBean>(logFilterBeans.values());
		Collections.sort(logFilterBeansToSort, new Comparator<LoggingCategoryBean>() {
			public int compare(LoggingCategoryBean o1, LoggingCategoryBean o2) {
				int res = 0;
				try {
					int level1 = o1.getCategory().getLevel();
		        	int level2 = o2.getCategory().getLevel();
		            return level1-level2;
				}
				catch (Exception e) {}
				return res;
			}
		});			
		return logFilterBeansToSort;
	}	
	
	public void removeLogFilter(IkrAdminLoggingCategory filter) {
		logFilters.remove(filter);
		LoggingCategoryBean bean = logFilterBeans.get(filter.getLevel());
		bean.setSelected(false);
	}
	
	public void addLogFilter(IkrAdminLoggingCategory filter) {
		logFilters.add(filter);
		LoggingCategoryBean bean = logFilterBeans.get(filter.getLevel());
		bean.setSelected(true);
	}
	
	
	
	public void startComponent(ActionEvent action) {
		long componentId = (Long)action.getComponent().getAttributes().get("componentId");
		AdminComponent componentType = (AdminComponent)action.getComponent().getAttributes().get("componentType");
		List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
		requests.add(new AdminRequest(AdminRequestCommand.START, componentId, componentType));
		try {
			adminRequestProducer.publish(requests);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void stopComponent(ActionEvent action) {
		long componentId = (Long)action.getComponent().getAttributes().get("componentId");
		AdminComponent componentType = (AdminComponent)action.getComponent().getAttributes().get("componentType");
		List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
		requests.add(new AdminRequest(AdminRequestCommand.STOP, componentId, componentType));
		try {
			adminRequestProducer.publish(requests);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void showComponentLogDetails(ActionEvent action) {
		long componentId = (Long)action.getComponent().getAttributes().get("componentId");
		AdminComponent componentType = (AdminComponent)action.getComponent().getAttributes().get("componentType");
		selectedEventLogBean = getEvenLogBean(componentId, componentType);
		renderEventLogDetails = true;
	}
	
	public void openFilterScreen(ActionEvent action) {
		selectedAdminConsole = (AdminConsole)action.getComponent().getAttributes().get("adminConsole");
		renderFilterScreen = true;
	}
	
	public void closeFilterScreen(ActionEvent action) {
		renderFilterScreen = false;
	}
	
	public void openLogFilterScreen(ActionEvent action) {
		selectedAdminConsole = null;
		renderFilterScreen = true;
	}
	
	public void filterValueChanged(ValueChangeEvent event) {
		if (selectedAdminConsole!=null) {
			ComponentStatus filter = (ComponentStatus)event.getComponent().getAttributes().get("filter");
			boolean checked = (Boolean)event.getNewValue();
			if (checked) {
				selectedAdminConsole.addFilter(filter);
			}
			else {
				selectedAdminConsole.removeFilter(filter);
			}
		}
		else {
			IkrAdminLoggingCategory filter = (IkrAdminLoggingCategory)event.getComponent().getAttributes().get("filter");
			boolean checked = (Boolean)event.getNewValue();
			if (checked) {
				addLogFilter(filter);
			}
			else {
				removeLogFilter(filter);
			}
		}
	}
	
	private IkrEventLogBean getEvenLogBean(long componentId, AdminComponent componentType) {
		IkrEventLogBean bean  = null;
		switch (componentType) {
			case COLLECTOR:
				bean = collectorConsole.getEventLogBean(componentId);
				break;
				
			case CONNECTOR:
				bean = connectorConsole.getEventLogBean(componentId);
				break;
				
			case JOB_TASK:
				bean = jobSchedulerConsole.getEventLogBean(componentId);
				break;
	
			default:
				break;
		}
		return bean;
	}
	
	public void closeComponentLogDetailsScreen(ActionEvent action) {
		renderEventLogDetails = false;
	}
	
	public AdminConsole getSelectedAdminConsole() {
		return selectedAdminConsole;
	}

	public AdminConsole getConnectorConsole() {
		return connectorConsole;
	}
	
	public AdminConsole getCollectorConsole() {
		return collectorConsole;
	}
	
	public AdminConsole getJobSchedulerConsole() {
		return jobSchedulerConsole;
	}

	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}

	public void setJmsFactory(JmsProcessorFactory jmsFactory) {
		this.jmsFactory = jmsFactory;
	}

	public String getEnvColumnName() {
		return AdminConsole.envColumnName;
	}
	
	public String getTypeColumnName() {
		return AdminConsole.typeColumnName;
	}
	
	public String getNameColumnName() {
		return AdminConsole.nameColumnName;
	}
	
	public String getStartTimeColumnName() {
		return AdminConsole.startTimeColumnName;
	}
	
	public String getStatusColumnName() {
		return AdminConsole.statusColumnName;
	}
	
	public IkrEventLogBean getSelectedEventLogBean() {
		return selectedEventLogBean;
	}
	
	public boolean isRenderEventLogDetailsScreen() {
		return renderEventLogDetails;
	}
	
	public boolean isRenderFilterScreen() {
		return renderFilterScreen;
	}
	
	public boolean isRenderLogFilterScreen() {
		return selectedAdminConsole==null;
	}

	public List<IkrAdminLoggingBean> getGlobalLogBuffer() {
		List<IkrAdminLoggingBean> res = new ArrayList<IkrAdminLoggingBean>();
		List<IkrAdminLoggingBean> globalLogBuffer = new ArrayList<IkrAdminLoggingBean>();
		globalLogBuffer.addAll(connectorConsole.getLogBuffer());
		globalLogBuffer.addAll(collectorConsole.getLogBuffer());
		globalLogBuffer.addAll(jobSchedulerConsole.getLogBuffer());
		if (globalLogBuffer.size()>0) {
			res = filterLog(globalLogBuffer);
			Collections.sort(res, new Comparator<IkrAdminLoggingBean>(){
				public int compare(IkrAdminLoggingBean o1,IkrAdminLoggingBean o2) {
					Date date1 = o1.getLog().getLogDatetime();
					Date date2 = o2.getLog().getLogDatetime();
		            return date2.compareTo(date1);
				}
			}			
			);
		}
		return res;
	}
	
	private List<IkrAdminLoggingBean> filterLog(List<IkrAdminLoggingBean> listToFilter) {
		List<IkrAdminLoggingBean> filtered = new ArrayList<IkrAdminLoggingBean>();
		for (IkrAdminLoggingBean bean : listToFilter) {
			if(logFilters.contains(bean.getLog().getCategory()))
				filtered.add(bean);
		}	
		return filtered;
	}
}
