package com.fsi.monitoring.alert.config.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.action.AlertAction;
import com.fsi.monitoring.alert.action.AlertAction.AlertActionType;
import com.fsi.monitoring.alert.action.SnmpAlertAction;
import com.fsi.monitoring.alert.config.SnmpConfigRowBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.snmp.SnmpConfig;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.UserItem;
import com.fsi.monitoring.user.UserPM;
import com.fsi.monitoring.util.FacesUtils;


public class AlertSnmpActionBean implements Serializable{
	private static final long serialVersionUID = 1281835683573810742L;

	protected final static Logger logger = Logger.getLogger(AlertSnmpActionBean.class);	
	
	private boolean allSelected;
	private boolean snmp;

	private String snmpConfigNameSearch = null;	
	
	private Collection<SnmpConfigRowBean> snmpSearchResultConfigs = null;
	private Collection<SnmpConfigRowBean> snmpConfigs = null;
	
	private SnmpAlertAction snmpAlertAction;
	
	private AlertPM alertPM;
	
	public AlertSnmpActionBean(SnmpAlertAction snmpAlertAction,
							   AlertPM alertPM) {
		this.snmpAlertAction = snmpAlertAction;
		this.alertPM = alertPM;
		
		if (snmpAlertAction.getTypes().contains(AlertActionType.SNMP)) {
			snmp = true;
		}
		
		loadConfigs();
	}
	
	public boolean isSnmp() {
		return snmp;
	}
	
	public void setSnmp(boolean snmp) {
		this.snmp = snmp;
		
		if (snmp && !snmpAlertAction.getTypes().contains(AlertActionType.SNMP)) {
			snmpAlertAction.getTypes().add(AlertActionType.SNMP);
		} 
		
		if (!snmp) { 
			snmpAlertAction.getTypes().remove(AlertActionType.SNMP);
		}	
	}
	
	private void loadConfigs() {
		snmpConfigs = new ArrayList<SnmpConfigRowBean>();
		
		Collection<SnmpConfig> configs;
		try {
			configs = alertPM.getSnmpConfigs();
			
			for(SnmpConfig config : configs) {
				if (snmpAlertAction.getSnmpConfigIds().contains(config.getId())) {
					snmpConfigs.add(new SnmpConfigRowBean(config));
				}
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public AlertAction getSnmpAlertAction() {
		return snmpAlertAction;
	}
		
	public String getSnmpConfigNameSearch() {
		return snmpConfigNameSearch;
	}

	public void setSnmpConfigNameSearch(String snmpConfigNameSearch) {
		this.snmpConfigNameSearch = snmpConfigNameSearch;
	}
	
	public void addSnmp(ActionEvent actionEvent) {
		SnmpConfigRowBean snmpItemToAdd = (SnmpConfigRowBean)actionEvent.getComponent().getAttributes().get("snmpItem");
		boolean alreadyInList = false;
		for(SnmpConfigRowBean snmpItem : snmpConfigs) {
			if(snmpItemToAdd.getConfig().getId() == snmpItem.getConfig().getId()) {
				alreadyInList = true;
				break;
			}
		}
		if(!alreadyInList) {
			snmpSearchResultConfigs.remove(snmpItemToAdd);
			snmpConfigs.add(snmpItemToAdd);
			snmpAlertAction.addSnmpConfigId(snmpItemToAdd.getConfig().getId());
		}
//		if (snmpSearchResultConfigs != null) {
//			for(SnmpConfigRowBean snmpItem : snmpSearchResultConfigs) {
//				if (snmpItem.isSelected() && !snmpConfigs.contains(snmpItem)) {
//					snmpConfigs.add(snmpItem);
//					snmpItem.setSelected(false);
//					snmpAlertAction.addSnmpConfigId(snmpItem.getConfig().getId());
//				}
//			}
//		}
	}

	public void addSnmpAll(ActionEvent event) {
		for(SnmpConfigRowBean snmpItem : snmpSearchResultConfigs) {
			boolean alreadyInList = false;
			for(SnmpConfigRowBean snmpItemInList : snmpConfigs) {
				if(snmpItem.getConfig().getId() == snmpItemInList.getConfig().getId()) {
					alreadyInList = true;
					break;
				}
			}
			if(!alreadyInList) {
				snmpConfigs.add(snmpItem);
				snmpAlertAction.addSnmpConfigId(snmpItem.getConfig().getId());
			}
		}
		snmpSearchResultConfigs.clear();
//		if (snmpSearchResultConfigs != null) {
//			for(SnmpConfigRowBean snmpItem : snmpSearchResultConfigs) {
//				if (!snmpConfigs.contains(snmpItem)) {
//					snmpConfigs.add(snmpItem);
//					snmpItem.setSelected(false);
//					snmpAlertAction.addSnmpConfigId(snmpItem.getConfig().getId());
//				}
//			}
//		}
	}	
	
	public void removeSnmp(ActionEvent event) {
		SnmpConfigRowBean snmpItemToRemove = (SnmpConfigRowBean)event.getComponent().getAttributes().get("snmpItem");
		Iterator<SnmpConfigRowBean> selectionIT = snmpConfigs.iterator();
		while (selectionIT.hasNext()) {
			SnmpConfigRowBean snmpItem = selectionIT.next();
			if (snmpItem.getConfig().getId() == snmpItemToRemove.getConfig().getId()) {
				snmpItemToRemove.setSelected(false);
				selectionIT.remove();
				snmpSearchResultConfigs.add(snmpItemToRemove);
				snmpAlertAction.removeUserId(snmpItem.getConfig().getId());
			}
//			if (snmpItem.isSelected()) {
//				snmpItem.setSelected(false);
//				selectionIT.remove();
//				snmpAlertAction.removeUserId(snmpItem.getConfig().getId());
//			}
		}	
	}
	
	public void removeSnmpAll(ActionEvent event) {
		Iterator<SnmpConfigRowBean> selectionIT = snmpConfigs.iterator();
		while (selectionIT.hasNext()) {
			SnmpConfigRowBean snmpItem = selectionIT.next();
			snmpItem.setSelected(false);
			selectionIT.remove();
			snmpSearchResultConfigs.add(snmpItem);
			snmpAlertAction.removeUserId(snmpItem.getConfig().getId());
		}
	}
	
	public void searchSnmpConfigs(ValueChangeEvent event) {
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		if(snmpConfigNameSearch.trim().length() > 0) {
			snmpSearchResultConfigs = new ArrayList<SnmpConfigRowBean>();
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			Collection<SnmpConfig> configs;
			try {
				configs = alertPM.getSnmpConfigs();
				for(SnmpConfig conf : configs) {
					boolean alreadyInList = false;
					for(SnmpConfigRowBean snmpItemInList : snmpConfigs) {
						if(conf.getId() == snmpItemInList.getConfig().getId()) {
							alreadyInList = true;
							break;
						}
					}
					if(!alreadyInList) {
						if (snmpConfigNameSearch.equalsIgnoreCase(conf.getName()))
							snmpSearchResultConfigs.add(new SnmpConfigRowBean(conf));
					}
				}
			} catch (Exception exc) {
				logger.error(exc);
			}
		}
		else {
			snmpSearchResultConfigs = new ArrayList<SnmpConfigRowBean>();
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			Collection<SnmpConfig> configs;
			try {
				configs = alertPM.getSnmpConfigs();
				for(SnmpConfig conf : configs) {
					boolean alreadyInList = false;
					for(SnmpConfigRowBean snmpItemInList : snmpConfigs) {
						if(conf.getId() == snmpItemInList.getConfig().getId()) {
							alreadyInList = true;
							break;
						}
					}
					if(!alreadyInList) {
						snmpSearchResultConfigs.add(new SnmpConfigRowBean(conf));
					}
				}
			} catch (Exception exc) {
				logger.error(exc);
			}
		}
//		snmpSearchResultConfigs = new ArrayList<SnmpConfigRowBean>();
//		AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
//		Collection<SnmpConfig> configs;
//		try {
//			configs = alertPM.getSnmpConfigs();
//			for(SnmpConfig conf : configs) {
//				if(snmpConfigNameSearch != null && snmpConfigNameSearch.length() >0) {
//					if (snmpConfigNameSearch.equalsIgnoreCase(conf.getName()))
//						snmpSearchResultConfigs.add(new SnmpConfigRowBean(conf));
//				}
//				else {
//					snmpSearchResultConfigs.add(new SnmpConfigRowBean(conf));
//				}
//			}
//		} catch (Exception exc) {
//			logger.error(exc);
//		}
	}
	
	public void onChangeAllSelected(ValueChangeEvent evnt) {	
		this.allSelected = (Boolean)evnt.getNewValue();
	
		for (SnmpConfigRowBean item : snmpConfigs) {
			item.setSelected(allSelected);
		}
	}
	
	public Collection<SnmpConfigRowBean> getSnmpSearchResultConfigs() {
		if(snmpSearchResultConfigs == null)
			snmpSearchResultConfigs = new ArrayList<SnmpConfigRowBean>();
		
		if(snmpSearchResultConfigs != null && snmpSearchResultConfigs.size() == 0 && snmpConfigs != null && snmpConfigs.size() == 0) {
			AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
			Collection<SnmpConfig> configs;
			try {
				configs = alertPM.getSnmpConfigs();
				for(SnmpConfig conf : configs) {
					boolean alreadyInList = false;
					for(SnmpConfigRowBean snmpItemInList : snmpConfigs) {
						if(conf.getId() == snmpItemInList.getConfig().getId()) {
							alreadyInList = true;
							break;
						}
					}
					if(!alreadyInList) {
						snmpSearchResultConfigs.add(new SnmpConfigRowBean(conf));
					}
				}
			} catch (Exception exc) {
				logger.error(exc);
			}
			return snmpSearchResultConfigs;
		}
		else {
			return snmpSearchResultConfigs;
		}
	}

	public Collection<SnmpConfigRowBean> getSnmpConfigs() {
		return snmpConfigs;
	}

	public boolean isAllSelected() {
		return allSelected;
	}

	public void setAllSelected(boolean allSelected) {
		this.allSelected = allSelected;
	}	
	
	
	
	
}
