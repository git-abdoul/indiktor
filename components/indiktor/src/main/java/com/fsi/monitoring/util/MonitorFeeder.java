package com.fsi.monitoring.util;

import java.util.List;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.ikr.agent.config.AgentConfig;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.dao.DataModelDAO;

public class MonitorFeeder {
	
	private DataModelPM dataModelPM = null;
	private DataModelDAO dataModelDAO = null;
	private String monitorEnv;
	
	public MonitorFeeder(String monitorEnv) {
		this.monitorEnv = monitorEnv;
		dataModelPM = (DataModelPM) FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
		dataModelDAO = (DataModelDAO) FacesUtils.getManagedBean("dataModelDAO");
	}
	
	public void createMonitorEnv(List<MonitorConfig> configs, String agentType, String agentLabel) throws PersistenceException {
//		AgentConfig agentCfg = new AgentConfig(0, monitorEnv+" - "+agentLabel, agentType);
//		long id = dataModelDAO.createAgent(agentCfg);
//		for (MonitorConfig monCfg : configs){
////			MonitorConfig current = copy(monCfg);
//			monCfg.setEnv(monitorEnv);
//			monCfg.setAgentId(id);
//			long monitorId = dataModelDAO.createMonitor(monCfg);					
//			List<IkrStaticDomain> ikrCategoryGroups = dataModelPM.getSupportedIkrCategoryGroups(monCfg.getMonitorType());					
//			for (IkrStaticDomain ikrCategoryGroup : ikrCategoryGroups) {
//				dataModelPM.createActivatedCategory(monitorId,ikrCategoryGroup.getDomainValue());
//			}		
//		}
	}
	
//	private MonitorConfig copy (MonitorConfig monCfg) {
//		return new MonitorConfig(monCfg.getId(),
//								 monCfg.getAgentId(),
//								 monCfg.getName(),
//								 monCfg.getLabel(),
//								 monCfg.getEnv(),
//								 monCfg.getMonitorType(),
//								 monCfg.getCaptureDelay(),
////								 monCfg.getHostname(), 
////								 monCfg.getPort(), 
////								 monCfg.getUserName(), 
////								 monCfg.getPassword(),
//								 monCfg.getStarted());
//	}
}
