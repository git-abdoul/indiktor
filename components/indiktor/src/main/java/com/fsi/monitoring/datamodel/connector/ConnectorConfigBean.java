package com.fsi.monitoring.datamodel.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.AdminRequestCommand;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorConfig;
import com.fsi.monitoring.connector.HttpConnectorConfig;
import com.fsi.monitoring.connector.JmxConnectorConfig;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.SysloadConnectorConfig;
import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsMulticastProducer;
import com.fsi.monitoring.jms.JmsProcessorFactory;
import com.fsi.monitoring.jms.JmsProcessorType;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrUtils;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ConnectorConfigBean
extends SortableList {
	
	private static final Logger logger = Logger.getLogger(ConnectorConfigBean.class);
	
	private static final String nameColumnName = "Name";
	private static final String typeColumnName = "Type";
	
	private List<ConnectorConfigSelectionBean> connectorConfigs = null;	
	private List<ConnectorConfigSelectionBean> connectorConfigsSelected = null;	
	
	private ConnectorConfig connectorConfig = null;

    private static SelectItem[] connectorTypes;	
	private String connectorType;
	
	private ConnectorConfig selectedConnectorConfig;
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;
	
	private String nameOnEdit = "";
	private boolean onEdit = false;
	
	private String name;
	private boolean nameMandatory = false;
	private String nameStyle = "width:230px;";
	private String description;
	private boolean descriptionMandatory = false;
	private String descriptionStyle = "width:230px;";
	private String maxAttempt;
	private boolean maxAttemptMandatory = false;
	private boolean maxAttemptWrongFormat = false;
	private String maxAttemptStyle = "width:230px;";
	private String attemptDelay;
	private boolean attemptDelayMandatory = false;
	private boolean attemptDelayWrongFormat = false;
	private String attemptDelayStyle = "width:230px;";
	private String port;
	private boolean portMandatory = false;
	private boolean portWrongFormat = false;
	private String portStyle = "width:230px;";
	private String connectorContext;
	private boolean connectorContextMandatory = false;
	private String connectorContextStyle = "width:230px;";
	private String userName;
	private boolean userNameMandatory = false;
	private String userNameStyle = "width:230px;";
	private String password;
	private boolean passwordMandatory = false;
	private String passwordStyle = "width:230px;";
	private String calypsoDbUserName;
	private String calypsoDbPassword;
	private String processName;
	private boolean processNameMandatory = false;
	private String processNameStyle = "width:230px;";
	private boolean asofdateActive = false;
	private String asofdate;
	private String applicationName; 
	private String driver;
	private boolean driverMandatory = false;
	private String driverStyle = "width:230px;";
	private String uri;
	private boolean uriMandatory = false;
	private String uriStyle = "width:230px;";
	private String agent;
	
	private boolean connectorAlreadyExists = false;
	
	private boolean rendererConnectorConfig = false;
	private boolean rendererConnectorConfigUpdate = false;
	private boolean ConnectorTypeVisible = false;
	private boolean ConnectorConfigVisible = false;
	private boolean BackConnectorConfigVisible = false;
	
	private int numberConnectorsSelected = 0;
	
	private String searchQuery = "";
	
	private JmsProcessorFactory jmsFactory;
	private JmsMulticastProducer adminRequestProducer;
	
	static {
		connectorTypes = new SelectItem[4];
		connectorTypes[0] = new SelectItem("CALYPSO","CALYPSO");		
		connectorTypes[1] = new SelectItem("JMX","JMX");
		connectorTypes[2] = new SelectItem("SYSTEM_AGENT","SYSTEM_AGENT");		
		connectorTypes[3] = new SelectItem("RDBMS","RDBMS");	
	}
	
	public ConnectorConfigBean() {
		super(nameColumnName);
	}
	
	public void initJms() {
		adminRequestProducer = (JmsMulticastProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.ADMIN_REQUEST);
	}

	public void initForConfig(ActionEvent action) {
		if (!isAuthorized(102,"connectorConfig")) {
			setAccessDenied();
			return;
		}
		
		rendererConnectorConfig = true;
		rendererConnectorConfigUpdate = false;
		setConnectorType("CALYPSO");
		setConnectorTypeVisible(true);
		setConnectorConfigVisible(false);
		setBackConnectorConfigVisible(false);
	}
	
	public void initForUpdate(ActionEvent action) {
		if (!isAuthorized(102,"connectorConfig")) {
			setAccessDenied();
			return;
		}
		onEdit = true;
		ConnectorConfigSelectionBean configBeanSelected = (ConnectorConfigSelectionBean)action.getComponent().getAttributes().get("configBean");
		connectorConfig = configBeanSelected.getConnectorConfig();
		rendererConnectorConfig = true;
		rendererConnectorConfigUpdate = true;
		setConnectorTypeVisible(false);
		setConnectorConfigVisible(true);
		setBackConnectorConfigVisible(false);
		connectorType = connectorConfig.getType();
		name = connectorConfig.getName();
		nameOnEdit = name;
		description = connectorConfig.getDescription();
		maxAttempt = String.valueOf(connectorConfig.getMaxAttempt());
		attemptDelay = String.valueOf(connectorConfig.getAttemptDelay());	
		connectorContext = connectorConfig.getConnectorContext();
		if (connectorType.equals("CALYPSO")) {
			CalypsoConnectorConfig connector = (CalypsoConnectorConfig)connectorConfig;	
			applicationName = connector.getApplicationName();
			password = connector.getPassword();
			userName = connector.getUserName();
			calypsoDbUserName = connector.getDbPassword();
			calypsoDbPassword = connector.getDbPassword();
			asofdateActive = connector.isAsofdateActive();
			asofdate = connector.getAsofdate();
		} else if (connectorType.equals("RDBMS")) {
			RdbmsConnectorConfig connector = (RdbmsConnectorConfig)connectorConfig;	
			password = connector.getPassword();
			userName = connector.getUserName();
			driver = connector.getDriver();
			uri = connector.getUri();
		} else if (connectorType.equals("JMX")) {
			JmxConnectorConfig connector = (JmxConnectorConfig)connectorConfig;	
			password = connector.getPassword();
			userName = connector.getUserName();
			port = String.valueOf(connector.getPort());
			processName = connector.getProcessName();
		} else if (connectorType.equals("SYSLOAD")) {
			SysloadConnectorConfig connector = (SysloadConnectorConfig)connectorConfig;	
			password = connector.getPassword();
			userName = connector.getUserName();
			port = String.valueOf(connector.getPort());
			agent = connector.getAgent();
		} else if (connectorType.equals("SYSTEM_AGENT")) {
			SystemAgentConnectorConfig connector = (SystemAgentConnectorConfig)connectorConfig;	
			port = String.valueOf(connector.getPort());
		} else if (connectorType.equals("HTTP")) {
			HttpConnectorConfig connector = (HttpConnectorConfig)connectorConfig;	
			port = String.valueOf(connector.getPort());
		}
	}
	
	public boolean isConnectorTypeRendered() {
		return connectorConfig.getId() == 0;
	}
	
	public void init(ActionEvent action) {
		if (!isAuthorized(100,"connectors")) {
			return;
		}
		
		updateConnectors();
		connectorConfigsSelected = new ArrayList<ConnectorConfigSelectionBean>();
		selectAll = false;
	}	
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	public void searchMonitorQuery(ValueChangeEvent event) {
		searchQuery = (String)event.getNewValue();	
		updateConnectors();
	}
	
	public void filterConfigs() {
		if (searchQuery!=null && searchQuery.length()>0) {
			searchQuery = searchQuery.toLowerCase();
			List<String> queryItems = IkrUtils.splitSearchIndex(searchQuery);
			String[] searchQueryItems = (String[])queryItems.toArray(new String[queryItems.size()]);
			List<ConnectorConfigSelectionBean> newConnectorConfigList = new ArrayList<ConnectorConfigSelectionBean>();
			for (ConnectorConfigSelectionBean bean : connectorConfigs) {
				Set<String> indexes = bean.getSearchIndexes();
				String[] searchIndexes = (String[])indexes.toArray(new String[indexes.size()]);	
				if (IkrUtils.accepts(searchIndexes, searchQueryItems, true,  true))
					newConnectorConfigList.add(bean);
			}
			connectorConfigs = new ArrayList<ConnectorConfigSelectionBean>();
			connectorConfigs.addAll(newConnectorConfigList);
		}
	}
	
	private void updateConnectors() {
		
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM);
		try {			
			List<ConnectorConfig> configs = new ArrayList<ConnectorConfig>(dataModelPM.getConnectorConfigs().values());
			connectorConfigs = new ArrayList<ConnectorConfigSelectionBean>();
			for (ConnectorConfig conf : configs) {
				connectorConfigs.add(new ConnectorConfigSelectionBean(conf));
			}
		} catch(Exception exc) {
			logger.error(exc);
		}
	}	
	
	public Collection<ConnectorConfigSelectionBean> getConnectorConfigs() {
		filterConfigs();
		if (connectorConfigs != null && connectorConfigs.size()>0)
			sort();
		
		return connectorConfigs;
	}
	
	public String getConnectorType() {
		return connectorType;
	}
	
	public void setConnectorType(String connectorType) {
		initVar();
		
		this.connectorType = connectorType;
		
		if (connectorType.equals("CALYPSO")) {
			connectorConfig = new CalypsoConnectorConfig(0,"","",5,20);
		} else if (connectorType.equals("RDBMS")) {
			connectorConfig = new RdbmsConnectorConfig(0,"","",5,20);
		} else if (connectorType.equals("JMX")) {
			connectorConfig = new JmxConnectorConfig(0,"","",5,20);
		} else if (connectorType.equals("SYSLOAD")) {
			connectorConfig = new SysloadConnectorConfig(0,"","",5,20);
		} else if (connectorType.equals("SYSTEM_AGENT")) {
			connectorConfig = new SystemAgentConnectorConfig(0,"","",5,20);
		} else if (connectorType.equals("HTTP")) {
			connectorConfig = new HttpConnectorConfig(0,"","",5,20);
		}
	}
	
	private void initVar() {
		name = "";
		description = "";
		maxAttempt = "5";
		attemptDelay = "20";
		connectorContext = "";
		applicationName = "IndiKtor";
		password = "";
		userName = "";
		calypsoDbUserName = "";
		calypsoDbPassword = "";
		asofdateActive = false;
		asofdate = "";
		driver = "";
		uri = "";
		port = "0";
		processName = "";
		agent = "";
	}
	
	public String getSelectedPanel() {
		return connectorType;		
	}
	
	public ConnectorConfig getConnectorConfig() {
		return connectorConfig;
	}
	
	public SelectItem[] getConnectorTypes() {
		return connectorTypes;
	}
	
	public void validate(ActionEvent action) {
		testEntries();
		if(nameMandatory || descriptionMandatory || maxAttemptMandatory || maxAttemptWrongFormat
				|| attemptDelayMandatory || attemptDelayWrongFormat || connectorAlreadyExists)
			return;
		
		if (connectorType.equals("CALYPSO")) {
			if(connectorContextMandatory || userNameMandatory || passwordMandatory)
				return;
		} else if (connectorType.equals("RDBMS")) {
			if(driverMandatory || uriMandatory || userNameMandatory || passwordMandatory)
				return;
		} else if (connectorType.equals("JMX")) {
			if(connectorContextMandatory || portMandatory || portWrongFormat || processNameMandatory)
				return;
		} else if (connectorType.equals("SYSTEM_AGENT")) {
			if(connectorContextMandatory || portMandatory || portWrongFormat)
				return;
		}
		
		updateBean();
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM);

		try {			
			if (connectorConfig.getId()==0) {
				long connectorId = dataModelPM.saveConnector(connectorConfig);
				List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
				requests.add(new AdminRequest(AdminRequestCommand.ADD, connectorId, AdminComponent.CONNECTOR));
				adminRequestProducer.publish(requests);
			} else {
				dataModelPM.updateConnector(connectorConfig);
				List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
				requests.add(new AdminRequest(AdminRequestCommand.UPDATE, (long)connectorConfig.getId(), AdminComponent.CONNECTOR));
				adminRequestProducer.publish(requests);
				BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);	
				beanPM.flushConnectorConfigBean(connectorConfig.getId());
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
		
		init(action);
		rendererConnectorConfig = false;
		rendererConnectorConfigUpdate = false;
		onEdit = false;
		selectAll = false;
		initError();
	}
	
	public void deleteConnector(ActionEvent action) {
		if (!isAuthorized(101,"connectors")) {
			setAccessDenied();
			return;
		}
		ConnectorConfigSelectionBean configBeanSelected = (ConnectorConfigSelectionBean)action.getComponent().getAttributes().get("configBean");
		DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM);
		try {
			dataModelPM.deleteConnector(configBeanSelected.getConnectorConfig().getId());
			List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
			requests.add(new AdminRequest(AdminRequestCommand.REMOVE, (long)configBeanSelected.getConnectorConfig().getId(), AdminComponent.CONNECTOR));
			adminRequestProducer.publish(requests);
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);	
			beanPM.flushConnectorConfigBean(configBeanSelected.getConnectorConfig().getId());
		} catch (Exception exc) {
			logger.error(exc);
		}
		init(action);
		setSelectedConnectorConfig(null);
		connectorConfigsSelected = new ArrayList<ConnectorConfigSelectionBean>();
		selectAll = false;
	}
	
	public void deleteSelectedConnectors(ActionEvent action) {
		if (!isAuthorized(101,"connectors")) {
			setAccessDenied();
			return;
		}
		
		numberConnectorsSelected = 0;
		for (ConnectorConfigSelectionBean connectorConfig : connectorConfigs) {
			if (connectorConfig.isSelected())
				numberConnectorsSelected++;
		}
		
		if (numberConnectorsSelected > 0) {			
			for (ConnectorConfigSelectionBean connectorConfig : connectorConfigs) {
				if (connectorConfig.isSelected()) {
					DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM);
					try {
						dataModelPM.deleteConnector(connectorConfig.getConnectorConfig().getId());
						List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
						requests.add(new AdminRequest(AdminRequestCommand.REMOVE, (long)connectorConfig.getConnectorConfig().getId(), AdminComponent.CONNECTOR));
						adminRequestProducer.publish(requests);
						BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);	
						beanPM.flushConnectorConfigBean(connectorConfig.getConnectorConfig().getId());
					} catch (Exception exc) {
						logger.error(exc);
					}
				}
			}
			init(action);
			setSelectedConnectorConfig(null);
			connectorConfigsSelected = new ArrayList<ConnectorConfigSelectionBean>();
			selectAll = false;
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No connector has been selected");
		}
	}
	
	public void duplicate(ActionEvent action) {
		if (!isAuthorized(103,"connectorConfig")) {
			setAccessDenied();
			return;
		}
		ConnectorConfigSelectionBean configBeanSelected = (ConnectorConfigSelectionBean)action.getComponent().getAttributes().get("configBean");
		selectedConnectorConfig = configBeanSelected.getConnectorConfig();
		connectorConfig = cloneConnectorConfig(selectedConnectorConfig);
		connectorType = connectorConfig.getType();
		name = connectorConfig.getName();
		description = connectorConfig.getDescription();
		maxAttempt = String.valueOf(connectorConfig.getMaxAttempt());
		attemptDelay = String.valueOf(connectorConfig.getAttemptDelay());	
		connectorContext = connectorConfig.getConnectorContext();
		if (connectorType.equals("CALYPSO")) {
			CalypsoConnectorConfig connector = (CalypsoConnectorConfig)connectorConfig;	
			applicationName = connector.getApplicationName();
			password = connector.getPassword();
			userName = connector.getUserName();
			calypsoDbUserName = connector.getDbPassword();
			calypsoDbPassword = connector.getDbPassword();
			asofdateActive = connector.isAsofdateActive();
			asofdate = connector.getAsofdate();
		} else if (connectorType.equals("RDBMS")) {
			RdbmsConnectorConfig connector = (RdbmsConnectorConfig)connectorConfig;	
			password = connector.getPassword();
			userName = connector.getUserName();
			driver = connector.getDriver();
			uri = connector.getUri();
		} else if (connectorType.equals("JMX")) {
			JmxConnectorConfig connector = (JmxConnectorConfig)connectorConfig;	
			password = connector.getPassword();
			userName = connector.getUserName();
			port = String.valueOf(connector.getPort());
			processName = connector.getProcessName();
		} else if (connectorType.equals("SYSLOAD")) {
			SysloadConnectorConfig connector = (SysloadConnectorConfig)connectorConfig;	
			password = connector.getPassword();
			userName = connector.getUserName();
			port = String.valueOf(connector.getPort());
			agent = connector.getAgent();
		} else if (connectorType.equals("SYSTEM_AGENT")) {
			SystemAgentConnectorConfig connector = (SystemAgentConnectorConfig)connectorConfig;	
			port = String.valueOf(connector.getPort());
		} else if (connectorType.equals("HTTP")) {
			HttpConnectorConfig connector = (HttpConnectorConfig)connectorConfig;	
			port = String.valueOf(connector.getPort());
		}
		rendererConnectorConfigUpdate = true;
		setConnectorTypeVisible(false);
		setConnectorConfigVisible(true);
		setBackConnectorConfigVisible(false);
	}
	
	private ConnectorConfig cloneConnectorConfig(ConnectorConfig config) {
		ConnectorConfig connectorConfig = null;
		String connectorType = config.getType();
		if (connectorType.equals("CALYPSO")) {
			CalypsoConnectorConfig clone = new CalypsoConnectorConfig(0,config.getName(),config.getDescription(),config.getMaxAttempt(),config.getAttemptDelay());			 
			clone.setConnectorContext(config.getConnectorContext());
			clone.setUserName(((CalypsoConnectorConfig)config).getUserName());
			clone.setPassword(((CalypsoConnectorConfig)config).getPassword());
			clone.setDbPassword(((CalypsoConnectorConfig)config).getDbPassword());
			clone.setDbUserName(((CalypsoConnectorConfig)config).getDbUserName());
			clone.setAsofdateActive(((CalypsoConnectorConfig)config).isAsofdateActive());
			clone.setAsofdate(((CalypsoConnectorConfig)config).getAsofdate());
			clone.setApplicationName(((CalypsoConnectorConfig)config).getApplicationName());
			connectorConfig = clone;
		} else if (connectorType.equals("RDBMS")) {
			RdbmsConnectorConfig clone = new RdbmsConnectorConfig(0,config.getName(),config.getDescription(),config.getMaxAttempt(),config.getAttemptDelay());			 
			clone.setUri(((RdbmsConnectorConfig)config).getUri());
			clone.setUserName(((RdbmsConnectorConfig)config).getUserName());
			clone.setPassword(((RdbmsConnectorConfig)config).getPassword());
			clone.setDriver(((RdbmsConnectorConfig)config).getDriver());
			connectorConfig = clone;
		} else if (connectorType.equals("JMX")) {
			JmxConnectorConfig clone = new JmxConnectorConfig(0,config.getName(),config.getDescription(),config.getMaxAttempt(),config.getAttemptDelay());			 
			clone.setConnectorContext(config.getConnectorContext());
			clone.setUserName(((JmxConnectorConfig)config).getUserName());
			clone.setPassword(((JmxConnectorConfig)config).getPassword());
			clone.setProcessName(((JmxConnectorConfig)config).getProcessName());
			clone.setPort(((JmxConnectorConfig)config).getPort());
			connectorConfig = clone;
		} else if (connectorType.equals("SYSLOAD")) {
			SysloadConnectorConfig clone = new SysloadConnectorConfig(0,config.getName(),config.getDescription(),config.getMaxAttempt(),config.getAttemptDelay());			 
			clone.setConnectorContext(config.getConnectorContext());
			clone.setUserName(((SysloadConnectorConfig)config).getUserName());
			clone.setPassword(((SysloadConnectorConfig)config).getPassword());
			clone.setAgent(((SysloadConnectorConfig)config).getAgent());
			connectorConfig = clone;
		} else if (connectorType.equals("SYSTEM_AGENT")) {
			SystemAgentConnectorConfig clone = new SystemAgentConnectorConfig(0,config.getName(),config.getDescription(),config.getMaxAttempt(),config.getAttemptDelay());			 
			clone.setConnectorContext(config.getConnectorContext());
			clone.setPort(((SystemAgentConnectorConfig)config).getPort());
			connectorConfig = clone;
		} else if (connectorType.equals("HTTP")) {
			HttpConnectorConfig clone = new HttpConnectorConfig(0,config.getName(),config.getDescription(),config.getMaxAttempt(),config.getAttemptDelay());			 
			clone.setConnectorContext(config.getConnectorContext());
			clone.setPort(((SystemAgentConnectorConfig)config).getPort());
			connectorConfig = clone;
		}
		
		return connectorConfig;
	}

	public boolean isRendererConnectorConfig() {
		return rendererConnectorConfig;
	}

	public void setRendererConnectorConfig(boolean rendererConnectorConfig) {
		this.rendererConnectorConfig = rendererConnectorConfig;
	}
	
	public boolean isRendererConnectorConfigUpdate() {
		return rendererConnectorConfigUpdate;
	}

	public void setRendererConnectorConfigUpdate(boolean rendererConnectorConfigUpdate) {
		this.rendererConnectorConfigUpdate = rendererConnectorConfigUpdate;
	}

	public void closeConnectorConfigPopup(ActionEvent event) {		
		rendererConnectorConfig = false;
		onEdit = false;
		initError();
	}
	
	public void closeConnectorConfigUpdatePopup(ActionEvent event) {		
		rendererConnectorConfigUpdate = false;
		onEdit = false;
		initError();
	}

	public void rowSelectionListener(RowSelectorEvent event) {		
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		int rowId = event.getRow();
		selectedConnectorConfig = connectorConfigs.get(rowId).getConnectorConfig();
	}

	public boolean isConnectorTypeVisible() {
		return ConnectorTypeVisible;
	}

	public boolean isConnectorConfigVisible() {
		return ConnectorConfigVisible;
	}
	
	public void setConnectorTypeVisible(boolean connectorTypeVisible) {
		ConnectorTypeVisible = connectorTypeVisible;
	}

	public void setConnectorConfigVisible(boolean connectorConfigVisible) {
		ConnectorConfigVisible = connectorConfigVisible;
	}
	
	public boolean isBackConnectorConfigVisible() {
		return BackConnectorConfigVisible;
	}

	public void setBackConnectorConfigVisible(boolean backConnectorConfigVisible) {
		BackConnectorConfigVisible = backConnectorConfigVisible;
	}

	public void handleConnectorConfig(ActionEvent event) {
		ConnectorTypeVisible = !ConnectorTypeVisible;
		ConnectorConfigVisible = !ConnectorConfigVisible;
		BackConnectorConfigVisible = !BackConnectorConfigVisible;
		initError();
	}

	public ConnectorConfig getSelectedConnectorConfig() {
		return selectedConnectorConfig;
	}

	public void setSelectedConnectorConfig(ConnectorConfig selectedConnectorConfig) {
		this.selectedConnectorConfig = selectedConnectorConfig;
	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public boolean isPaginationVisible() {
		if (connectorConfigs.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}

	public void setPaginationVisible(boolean paginationVisible) {
		this.paginationVisible = paginationVisible;
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(connectorConfigs, new Comparator<ConnectorConfigSelectionBean>() {
			public int compare(ConnectorConfigSelectionBean o1, ConnectorConfigSelectionBean o2) {
				int res = 0;
				try {
					if (getNameColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getConnectorConfig().getName().toLowerCase().compareTo(o2.getConnectorConfig().getName().toLowerCase()) :  o2.getConnectorConfig().getName().toLowerCase().compareTo(o1.getConnectorConfig().getName().toLowerCase());
					}
					else if (getTypeColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getConnectorConfig().getType().toLowerCase().compareTo(o2.getConnectorConfig().getType().toLowerCase()) :  o2.getConnectorConfig().getType().toLowerCase().compareTo(o1.getConnectorConfig().getType().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});				
	}
	
	public String getNameColumnName() {
		return nameColumnName;
	}
	
	public String getTypeColumnName() {
		return typeColumnName;
	}
	
	public String getDeleteMessage() {
		numberConnectorsSelected = 0;
		for (ConnectorConfigSelectionBean connector : connectorConfigs) {
			if (connector.isSelected())
				numberConnectorsSelected++;
		}
		
		String message = "No dashboard selected";
		if (numberConnectorsSelected == 1) {
			for(ConnectorConfigSelectionBean connectorConfig : connectorConfigs) {
				if(connectorConfig.isSelected()) {
					message = "Are you sure to delete this connector : " + connectorConfig.getConnectorConfig().getName();	
					break;
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberConnectorsSelected + " connectors?";		
			return message;
		}
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	//-----------------------------------------------------------------------------------------------------

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMaxAttempt() {
		return this.maxAttempt;
	}

	public void setMaxAttempt(String maxAttempt) {
		this.maxAttempt = maxAttempt;
	}

	public String getAttemptDelay() {
		return this.attemptDelay;
	}

	public void setAttemptDelay(String attemptDelay) {
		this.attemptDelay = attemptDelay;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProcessName() {
		return this.processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public boolean isAsofdateActive() {
		return asofdateActive;
	}

	public void setAsofdateActive(boolean asofdateActive) {
		this.asofdateActive = asofdateActive;
	}

	public String getAsofdate() {
		return this.asofdate;
	}

	public void setAsofdate(String asofdate) {
		this.asofdate = asofdate;
	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getAgent() {
		return this.agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getConnectorContext() {
		return this.connectorContext;
	}

	public void setConnectorContext(String connectorContext) {
		this.connectorContext = connectorContext;
	}	
	
	private void updateBean() {
		if (connectorType.equals("CALYPSO")) {
			CalypsoConnectorConfig connector = (CalypsoConnectorConfig)connectorConfig;	
			connector.setApplicationName(this.applicationName);
			connector.setAsofdate(this.asofdate);
			connector.setAsofdateActive(this.asofdateActive);
			connector.setAttemptDelay(Integer.parseInt(this.attemptDelay));
			connector.setConnectorContext(this.connectorContext);
			connector.setDescription(this.description);
			connector.setMaxAttempt(Integer.parseInt(this.maxAttempt));
			connector.setPassword(this.password);
			connector.setUserName(this.userName);
			connector.setDbPassword(this.calypsoDbPassword);
			connector.setDbUserName(this.calypsoDbUserName);
			connector.setName(this.name);
		} else if (connectorType.equals("RDBMS")) {
			RdbmsConnectorConfig connector = (RdbmsConnectorConfig)connectorConfig;
			connector.setAttemptDelay(Integer.parseInt(this.attemptDelay));
			connector.setDescription(this.description);
			connector.setMaxAttempt(Integer.parseInt(this.maxAttempt));
			connector.setPassword(this.password);
			connector.setUserName(this.userName);
			connector.setName(this.name);
			connector.setDriver(this.driver);
			connector.setUri(this.uri);
		} else if (connectorType.equals("JMX")) {
			JmxConnectorConfig connector = (JmxConnectorConfig)connectorConfig;
			connector.setAttemptDelay(Integer.parseInt(this.attemptDelay));
			connector.setDescription(this.description);
			connector.setMaxAttempt(Integer.parseInt(this.maxAttempt));
			connector.setPassword(this.password);
			connector.setUserName(this.userName);
			connector.setName(this.name);
			connector.setConnectorContext(this.connectorContext);
			connector.setPort(Integer.parseInt(this.port));
			connector.setProcessName(this.processName);
		} else if (connectorType.equals("SYSLOAD")) {
			SysloadConnectorConfig connector = (SysloadConnectorConfig)connectorConfig;
			connector.setAttemptDelay(Integer.parseInt(this.attemptDelay));
			connector.setDescription(this.description);
			connector.setMaxAttempt(Integer.parseInt(this.maxAttempt));
			connector.setPassword(this.password);
			connector.setUserName(this.userName);
			connector.setName(this.name);
			connector.setConnectorContext(this.connectorContext);
			connector.setPort(Integer.parseInt(this.port));
			connector.setAgent(this.agent);
		} else if (connectorType.equals("SYSTEM_AGENT")) {
			SystemAgentConnectorConfig connector = (SystemAgentConnectorConfig)connectorConfig;
			connector.setAttemptDelay(Integer.parseInt(this.attemptDelay));
			connector.setDescription(this.description);
			connector.setMaxAttempt(Integer.parseInt(this.maxAttempt));
			connector.setName(this.name);
			connector.setConnectorContext(this.connectorContext);
			connector.setPort(Integer.parseInt(this.port));
		} else if (connectorType.equals("HTTP")) {
			HttpConnectorConfig connector = (HttpConnectorConfig)connectorConfig;
			connector.setAttemptDelay(Integer.parseInt(this.attemptDelay));
			connector.setDescription(this.description);
			connector.setMaxAttempt(Integer.parseInt(this.maxAttempt));
			connector.setName(this.name);
			connector.setConnectorContext(this.connectorContext);
			connector.setPort(Integer.parseInt(this.port));
		}
	}
	
	public void handleSelectedConnector(ValueChangeEvent event) {
		ConnectorConfigSelectionBean configBeanSelected = (ConnectorConfigSelectionBean)event.getComponent().getAttributes().get("configBean");
		if(configBeanSelected != null) {
			for(ConnectorConfigSelectionBean connectorConfig : connectorConfigs) {
				if(connectorConfig.equals(configBeanSelected)) {
					connectorConfig.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						connectorConfigsSelected.add(configBeanSelected);
					else
						connectorConfigsSelected.remove(configBeanSelected);
				}
			}
		}
	}
	
	public void handleSelectAllConnectors(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		connectorConfigsSelected.clear();
		for(ConnectorConfigSelectionBean connectorConfig : connectorConfigs) {
			connectorConfig.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				connectorConfigsSelected.add(connectorConfig);
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getConnectorConfigsSelected() {
		int size = connectorConfigsSelected.size();
		return size;
	}

	public void setConnectorConfigsSelected(
			List<ConnectorConfigSelectionBean> connectorConfigsSelected) {
		this.connectorConfigsSelected = connectorConfigsSelected;
	}

	public String getCalypsoDbUserName() {
		return this.calypsoDbUserName;
	}

	public void setCalypsoDbUserName(String calypsoDbUserName) {
		this.calypsoDbUserName = calypsoDbUserName;
	}

	public String getCalypsoDbPassword() {
		return this.calypsoDbPassword;
	}

	public void setCalypsoDbPassword(String calypsoDbPassword) {
		this.calypsoDbPassword = calypsoDbPassword;
	}
	
	//-------------Control and style---------------//

	public boolean isNameMandatory() {
		return nameMandatory;
	}

	public String getNameStyle() {
		return nameStyle;
	}

	public boolean isDescriptionMandatory() {
		return descriptionMandatory;
	}

	public String getDescriptionStyle() {
		return descriptionStyle;
	}

	public boolean isMaxAttemptMandatory() {
		return maxAttemptMandatory;
	}

	public String getMaxAttemptStyle() {
		return maxAttemptStyle;
	}

	public boolean isAttemptDelayMandatory() {
		return attemptDelayMandatory;
	}

	public String getAttemptDelayStyle() {
		return attemptDelayStyle;
	}

	public boolean isPortMandatory() {
		return portMandatory;
	}

	public String getPortStyle() {
		return portStyle;
	}

	public boolean isConnectorContextMandatory() {
		return connectorContextMandatory;
	}

	public String getConnectorContextStyle() {
		return connectorContextStyle;
	}

	public boolean isUserNameMandatory() {
		return userNameMandatory;
	}

	public String getUserNameStyle() {
		return userNameStyle;
	}

	public boolean isPasswordMandatory() {
		return passwordMandatory;
	}

	public String getPasswordStyle() {
		return passwordStyle;
	}

	public boolean isDriverMandatory() {
		return driverMandatory;
	}

	public String getDriverStyle() {
		return driverStyle;
	}

	public boolean isUriMandatory() {
		return uriMandatory;
	}

	public String getUriStyle() {
		return uriStyle;
	}

	public boolean isProcessNameMandatory() {
		return processNameMandatory;
	}

	public String getProcessNameStyle() {
		return processNameStyle;
	}

	public boolean isMaxAttemptWrongFormat() {
		return maxAttemptWrongFormat;
	}

	public boolean isAttemptDelayWrongFormat() {
		return attemptDelayWrongFormat;
	}

	public boolean isPortWrongFormat() {
		return portWrongFormat;
	}
	
	public boolean isConnectorAlreadyExists() {
		return connectorAlreadyExists;
	}

	public void initError() {
		attemptDelayMandatory = attemptDelayWrongFormat = connectorContextMandatory = descriptionMandatory
			= driverMandatory = maxAttemptMandatory = maxAttemptWrongFormat = nameMandatory = passwordMandatory
				= portMandatory = portWrongFormat = processNameMandatory = uriMandatory = userNameMandatory = connectorAlreadyExists = false;
		attemptDelayStyle = connectorContextStyle = descriptionStyle = driverStyle = maxAttemptStyle
			= nameStyle = passwordStyle = portStyle = processNameStyle = uriStyle = userNameStyle = "width:230px;";
	}
	
	public void testEntries() {		
		if(name!=null&&name.trim().length() > 0) {
			nameMandatory = false;
			nameStyle = "width:230px;";
			connectorAlreadyExists = false;
			for(ConnectorConfigSelectionBean connector : connectorConfigs) {
				if(onEdit && connector.getConnectorConfig().getName().equalsIgnoreCase(nameOnEdit))
					continue;
				else {
					if(connector.getConnectorConfig().getName().equalsIgnoreCase(name)) {
						connectorAlreadyExists = true;
						nameStyle = "width:230px; border:1px solid red;";
					}
				}
			}
			if(!connectorAlreadyExists) {
				nameStyle = "width:230px;";
			}
		}
		else {
			connectorAlreadyExists = false;
			nameMandatory = true;
			nameStyle = "width:230px; border:1px solid red;";
		}
		
		if(description!=null&&description.trim().length() > 0) {
			descriptionMandatory = false;
			descriptionStyle = "width:230px;";
		}
		else {
			descriptionMandatory = true;
			descriptionStyle = "width:230px; border:1px solid red;";
		}
		
		if (maxAttempt!=null&&maxAttempt.trim().length() == 0) {
			maxAttemptMandatory = true;
			maxAttemptWrongFormat = false;
		}
		else
			maxAttemptWrongFormat = !StringUtils.isNumeric(maxAttempt);
		
		if(maxAttemptMandatory || maxAttemptWrongFormat)
			maxAttemptStyle = "width:230px; border:1px solid red;";
		else
			maxAttemptStyle = "width:230px;";
		
		if (attemptDelay!=null&&attemptDelay.trim().length() == 0) {
			attemptDelayMandatory = true;
			attemptDelayWrongFormat = false;
		}
		else
			attemptDelayWrongFormat = !StringUtils.isNumeric(attemptDelay);
		
		if(attemptDelayMandatory || attemptDelayWrongFormat)
			attemptDelayStyle = "width:230px; border:1px solid red;";
		else
			attemptDelayStyle = "width:230px;";

		if (connectorType.equals("CALYPSO") || connectorType.equals("JMX") || connectorType.equals("SYSTEM_AGENT")) {
			if(connectorContext!=null&&connectorContext.trim().length() > 0) {
				connectorContextMandatory = false;
				connectorContextStyle = "width:230px;";
			}
			else {
				connectorContextMandatory = true;
				connectorContextStyle = "width:230px; border:1px solid red;";
			}
		}
		
		if (connectorType.equals("JMX") || connectorType.equals("SYSTEM_AGENT")) {
			if (port!=null&&port.trim().length() == 0)
				portMandatory = true;
			else
				portWrongFormat = !StringUtils.isNumeric(port);
			
			if(portMandatory || portWrongFormat)
				portStyle = "width:230px; border:1px solid red;";
			else
				portStyle = "width:230px;";
		}
		
		if(userName!=null&&userName.trim().length() > 0) {
			userNameMandatory = false;
			userNameStyle = "width:220px;";
		}
		else {
			userNameMandatory = true;
			userNameStyle = "width:220px; border:1px solid red;";
		}
		
		if (connectorType.equals("CALYPSO") || connectorType.equals("RDBMS")) {
			if(password!=null&&password.trim().length() > 0) {
				passwordMandatory = false;
				passwordStyle = "width:220px;";
			}
			else {
				passwordMandatory = true;
				passwordStyle = "width:220px; border:1px solid red;";
			}
		}
			
		if (connectorType.equals("JMX")) {
			if(processName!=null&&processName.trim().length() > 0) {
				processNameMandatory = false;
				processNameStyle = "width:230px;";
			}
			else {
				processNameMandatory = true;
				processNameStyle = "width:230px; border:1px solid red;";
			}
		}
		
		if (connectorType.equals("RDBMS")) {
			if(driver!=null&&driver.trim().length() > 0) {
				driverMandatory = false;
				driverStyle = "width:230px;";
			}
			else {
				driverMandatory = true;
				driverStyle = "width:230px; border:1px solid red;";
			}
			
			if(uri!=null&&uri.trim().length() > 0) {
				uriMandatory = false;
				uriStyle = "width:230px;";
			}
			else {
				uriMandatory = true;
				uriStyle = "width:230px; border:1px solid red;";
			}
		}
	}
	
	public void setJmsFactory(JmsProcessorFactory jmsFactory) {
		this.jmsFactory = jmsFactory;
	}

	public boolean getListRendered() {
		return getConnectorConfigs().size() > 0;
	}
}
