package com.fsi.monitoring.alert.config.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;


import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.action.AlertAction;
import com.fsi.monitoring.alert.action.SnmpAlertAction;
import com.fsi.monitoring.alert.action.UserAlertAction;
import com.fsi.monitoring.alert.action.AlertAction.AlertActionType;
import com.fsi.monitoring.alert.config.SnmpConfigRowBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.snmp.SnmpConfig;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.UserItem;
import com.fsi.monitoring.user.UserPM;
import com.fsi.monitoring.util.FacesUtils;


public class AlertActionBean {
	
	private int id;	
	
	private UserAlertAction userAlertAction = null;
	private SnmpAlertAction snmpAlertAction = null;

	private boolean mail;
	private boolean sms;
	private boolean snmp;

	private String lastNameSearch = null;
	private String snmpConfigName = null;
	
	private Collection<UserItem> searchUsers = null;
	private Collection<SnmpConfigRowBean> snmpSearchResultConfigs = null;
	private Collection<UserItem> users = null;
	private Collection<SnmpConfigRowBean> snmpConfigs = null;
	
	public AlertActionBean(List<AlertAction> alertActions) {
		Collection<AlertActionType> types = new ArrayList<AlertActionType>();
		for (AlertAction action : alertActions) {
			updateActionType(action, types);
		}		
		init();
	}
	
	private void updateActionType(AlertAction action, Collection<AlertActionType> types) {
		if (action instanceof UserAlertAction) {
			this.userAlertAction = (UserAlertAction)action;
			types = userAlertAction.getTypes();
			this.snmpAlertAction = new SnmpAlertAction();
		} 
		else if(action instanceof SnmpAlertAction) {
			this.snmpAlertAction = (SnmpAlertAction)action;
			types = snmpAlertAction.getTypes();
			this.userAlertAction = new UserAlertAction();
		}
		
		if (types.contains(AlertActionType.MAIL)) {
			mail = true;
		}
		if (types.contains(AlertActionType.SMS)) {
			sms = true;
		}
		
		if (types.contains(AlertActionType.SNMP)) {
			snmp = true;
		}
	}
	
	public AlertActionBean() {
		userAlertAction = new UserAlertAction();
		snmpAlertAction = new SnmpAlertAction();
		init();
	}
	
	private void init() {
		loadUsers();		
		snmpConfigs = new ArrayList<SnmpConfigRowBean>();
	}
	
	public int getId() {
		return id;
	}
	

	public boolean isMail() {
		return mail;
	}
	
	public void setMail(boolean mail) {
		this.mail = mail;
		if (mail && !userAlertAction.getTypes().contains(AlertActionType.MAIL)) {
			userAlertAction.getTypes().add(AlertActionType.MAIL);
		} 
		if (!mail) {
			userAlertAction.getTypes().remove(AlertActionType.MAIL);
		}
	}
	
	public boolean isSms() {
		return sms;
	}
	
	public void setSms(boolean sms) {
		this.sms = sms;
		if (sms && !userAlertAction.getTypes().contains(AlertActionType.SMS)) {
			userAlertAction.getTypes().add(AlertActionType.SMS);
		}
		if (!sms) {
			userAlertAction.getTypes().remove(AlertActionType.SMS);
		}
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

	public AlertAction getUserAlertAction() {
		return userAlertAction;
	}
	
	public AlertAction getSnmpAlertAction() {
		return snmpAlertAction;
	}

	public String getLastNameSearch() {
		return lastNameSearch;
	}

	public void setLastNameSearch(String lastName) {
		this.lastNameSearch = lastName;
	}		
		
	public String getSnmpConfigName() {
		return snmpConfigName;
	}

	public void setSnmpConfigName(String snmpConfigName) {
		this.snmpConfigName = snmpConfigName;
	}

	public Collection<UserItem> getUsers() {
		return users;
	}
	
	public Collection<UserItem> getSearchUsers() {
		return searchUsers;
	}	
	
	public int getNbUsers() {
		if (users == null) {
			return 0;
		}
		return users.size();
	}	
	
	
	public void addSnmp(ActionEvent actionEvent) {
		if (snmpSearchResultConfigs != null) {
			for(SnmpConfigRowBean snmpItem : snmpSearchResultConfigs) {
				if (snmpItem.isSelected() && !snmpConfigs.contains(snmpItem)) {
					snmpConfigs.add(snmpItem);
					snmpAlertAction.addSnmpConfigId(snmpItem.getConfig().getId());
				}
			}
		}
	}

	public void addSnmpAll(ActionEvent event) {
		if (snmpSearchResultConfigs != null) {
			for(SnmpConfigRowBean snmpItem : snmpSearchResultConfigs) {
				if (!snmpConfigs.contains(snmpItem)) {
					snmpItem.setSelected(false);
					snmpConfigs.add(snmpItem);
					snmpAlertAction.addSnmpConfigId(snmpItem.getConfig().getId());
				}
			}
		}
	}	
	
	public void removeSnmp(ActionEvent event) {
		System.out.println("REM");
		Iterator<SnmpConfigRowBean> selectionIT = snmpConfigs.iterator();
		while (selectionIT.hasNext()) {
			SnmpConfigRowBean snmpItem = selectionIT.next();
			if (snmpItem.isSelected()) {
				snmpItem.setSelected(false);
				selectionIT.remove();
				snmpAlertAction.removeUserId(snmpItem.getConfig().getId());
			}
		}	
	}
	
	public void removeSnmpAll(ActionEvent event) {
		Iterator<SnmpConfigRowBean> selectionIT = snmpConfigs.iterator();
		while (selectionIT.hasNext()) {
			SnmpConfigRowBean snmpItem = selectionIT.next();
			snmpItem.setSelected(false);
			selectionIT.remove();
			snmpAlertAction.removeUserId(snmpItem.getConfig().getId());
		}
	}	

	
	
	public void addUser(ActionEvent actionEvent) {
		if (searchUsers != null) {
			for(UserItem userItem : searchUsers) {
				if (userItem.isSelected() && !users.contains(userItem)) {
					users.add(userItem);
					userAlertAction.addUserId(userItem.getUser().getId());
				}
			}
		}
	}

	public void addUserAll(ActionEvent event) {
		for(UserItem userItem : searchUsers) {
			if (!users.contains(userItem)) {
				userItem.setSelected(false);
				users.add(userItem);
				userAlertAction.addUserId(userItem.getUser().getId());
			}
		}
	}	
	
	public void removeUser(ActionEvent event) {
		System.out.println("REM");
		Iterator<UserItem> selectionIT = users.iterator();
		while (selectionIT.hasNext()) {
			UserItem userItem = selectionIT.next();
			if (userItem.isSelected()) {
				userItem.setSelected(false);
				selectionIT.remove();
				userAlertAction.removeUserId(userItem.getUser().getId());
			}
		}	
	}
	
	public void removeUserAll(ActionEvent event) {
		Iterator<UserItem> selectionIT = users.iterator();
		while (selectionIT.hasNext()) {
			UserItem userItem = selectionIT.next();
			userItem.setSelected(false);
			selectionIT.remove();
			userAlertAction.removeUserId(userItem.getUser().getId());
		}
	}	
	
	private void loadUsers() {
		users = new HashSet<UserItem>();
		
		Collection<Long> userIds = userAlertAction.getUserIds();
		if (userIds != null && !userIds.isEmpty()) {	
			Map<Long, User> userMap2 = getUserFromIds(userIds);
			
			for(User user : userMap2.values()) {
				UserItem userItem = new UserItem(user);	
				userItem.setSelected(true);
				users.add(userItem);
			}	
		}		
	}
	
	public void searchUsers(ActionEvent actionEvent) {
		searchUsers = new ArrayList<UserItem>();
		Map<Long, User> userMap = getUsersFromName(lastNameSearch);
		for(User user : userMap.values()) {
			UserItem userItem = new UserItem(user);	
			searchUsers.add(userItem);
		}
	}
	
	public void searchSnmpConfigs(ActionEvent actionEvent) {
		snmpSearchResultConfigs = new ArrayList<SnmpConfigRowBean>();
		AlertPM alertPM = (AlertPM)FacesUtils.getManagedBean(PersistencyBeanName.alertPM.name());
		Collection<SnmpConfig> configs;
		try {
			configs = alertPM.getSnmpConfigs();
			for(SnmpConfig conf : configs) {
				if(snmpConfigName!=null && snmpConfigName.length()>0) {
					if (snmpConfigName.equalsIgnoreCase(snmpConfigName))
						snmpSearchResultConfigs.add(new SnmpConfigRowBean(conf));
				}
				else {
					snmpSearchResultConfigs.add(new SnmpConfigRowBean(conf));
				}
			}
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		
	}
	
	private Map<Long, User> getUsersFromName(String lastName) {
		Map<Long, User> userMap = null;
		try {
			UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
			userMap = userPM.searchUsers(lastName);
		} catch(Exception exc) {
			System.out.println(exc);
		}
		return userMap;
	}
	
	private Map<Long, User> getUserFromIds(Collection<Long> userIds) {
		Map<Long, User> userMap = null;
		try {
			UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
			userMap = userPM.getUsers(userIds);
		} catch(Exception exc) {
			System.out.println(exc);
		}
		return userMap;
	}

	public Collection<SnmpConfigRowBean> getSnmpSearchResultConfigs() {
		return snmpSearchResultConfigs;
	}

	public Collection<SnmpConfigRowBean> getSnmpConfigs() {
		return snmpConfigs;
	}	
	
	
}
