package com.fsi.monitoring.datamodel.bean.factory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.config.definition.AlertDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrStaticDomainBean;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.connector.ConnectorConfigSelectionBean;
import com.fsi.monitoring.datamodel.monitor.LogicalEnvBean;
import com.fsi.monitoring.datamodel.monitor.MonitorConfigBean;

public interface BeanPM 
extends Serializable {
	
	public static final String BeanPM_ID = "beanPM";

	IkrValueBean getIkrValueBean(long ikrValueId);
	List<IkrValueBean> getIkrValueBeans(Collection<Long> ikrValueIds);
	
	IkrDefinitionBean getIkrDefinitionBean(long ikrDefinitionId);
	List<IkrDefinitionBean> getIkrDefinitionBeans(Collection<Long> ikrDefinitionIds);
	void flushIkrDefinitionBean(long ikrDefinitionId);
	void flushIkrDefinitionBeans(Collection<Long> ikrDefinitionIds);	
	void flushIkrDefinitionBeans();	
	
	AlertDefinitionBean getAlertDefinitionBean(long alertDefinitionId);
	List<AlertDefinitionBean> getAlertDefinitionBeans(Collection<Long> alertDefinitionIds);
	void flushAlertDefinitionBean(long alertDefinitionBeanId);	
	
	List<IkrValueBean> getIkrValueBeansByIkrDefinition(long ikrDefinitionId, Date fromDate) throws PersistenceException;
	List<IkrValueBean> getLastIkrValuesBeanByIkrDefinition (long ikrDefinitionId, int nbOfValues) throws PersistenceException;
	
	LogicalEnvBean getLogicalEnvBean(int logicalEnvId);
	List<LogicalEnvBean> getLogicalEnvBeans(Collection<Integer> logicalenvIds);
	void flushLogicalEnvBean(long logicalEnvId);
	
	MonitorConfigBean getMonitorConfigBean(long monitorCongifId);
	List<MonitorConfigBean> getMonitorConfigBeans(Collection<Long> monitorCongifIds);
	void flushMonitorConfigBean(long monitorCongifId);
	
	ConnectorConfigSelectionBean getConnectorConfigBean(int connectorCongifId);
	List<ConnectorConfigSelectionBean> getConnectorConfigBeans(Collection<Integer> connectorCongifIds);
	void flushConnectorConfigBean(long connectorCongifId);
	
	IkrStaticDomainBean getIkrStaticDomainBean(int domainId);
	List<IkrStaticDomainBean> getIkrStaticDomainBeans(Collection<Integer> domainIdIds);
	void flushIkrStaticDomainBean(int domainId);
}
