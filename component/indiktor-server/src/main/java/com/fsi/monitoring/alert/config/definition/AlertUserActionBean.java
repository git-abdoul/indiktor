package com.fsi.monitoring.alert.config.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import com.fsi.monitoring.alert.action.AlertAction.AlertActionType;
import com.fsi.monitoring.alert.action.UserAlertAction;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.user.User;
import com.fsi.monitoring.user.UserItem;
import com.fsi.monitoring.user.UserPM;
import com.fsi.monitoring.util.FacesUtils;


public class AlertUserActionBean {
	
	private boolean allSelected;
	private boolean mail;
	private boolean sms;

	private String lastNameSearch = "";

	private Collection<UserItem> searchUsers = null;
	private Collection<UserItem> selectedUsers = null;
	
	private UserAlertAction userAlertAction = null;	
	private UserPM userPM = null;
	
	public AlertUserActionBean(UserAlertAction userAlertAction,
							   UserPM userPM) {
		this.userAlertAction = userAlertAction;
		this.userPM = userPM;
		
		if (userAlertAction.getTypes().contains(AlertActionType.MAIL)) {
			mail = true;
		}
		
		if (userAlertAction.getTypes().contains(AlertActionType.SMS)) {
			sms = true;
		}

		loadUsers();
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

	public String getLastNameSearch() {
		return lastNameSearch;
	}

	public void setLastNameSearch(String lastName) {
		this.lastNameSearch = lastName;
	}
	
	public Collection<UserItem> getSelectedUsers() {
		return selectedUsers;
	}
	
	public Collection<UserItem> getSearchUsers() {
		if(searchUsers == null)
			searchUsers = new ArrayList<UserItem>();
		
		if(searchUsers != null && searchUsers.size() == 0 && selectedUsers != null && selectedUsers.size() == 0) {
			UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
			try {
				Map<Long, User> users = userPM.getUsers();
				
				for(User user : users.values()) {
					if (user.getId() != 1) {
						UserItem userItem = new UserItem(user);	
						boolean alreadyInList = false;
						for(UserItem userItemInList : selectedUsers) {
							if(userItem.getUser().getId() != userItemInList.getUser().getId()) {
								alreadyInList = true;
								break;
							}
						}
						if(!alreadyInList) {
							searchUsers.add(userItem);
						}
					}
				}
			} catch(Exception exc) {
				System.out.println(exc);
			}
			return searchUsers;
		}
		else {
			return searchUsers;
		}
	}
	
	public void addUser(ActionEvent actionEvent) {
		UserItem userItemToAdd = (UserItem)actionEvent.getComponent().getAttributes().get("userItem");
		boolean alreadyInList = false;
		for(UserItem userItem : selectedUsers) {
			if(userItemToAdd.getUser().getId() == userItem.getUser().getId()) {
				alreadyInList = true;
				break;
			}
		}
		if(!alreadyInList) {
			searchUsers.remove(userItemToAdd);
			selectedUsers.add(userItemToAdd);
			userAlertAction.addUserId(userItemToAdd.getUser().getId());
		}
//		if (searchUsers != null) {
//			for(UserItem userItem : searchUsers) {
//				if (userItem.isSelected() && !selectedUsers.contains(userItem)) {
//					selectedUsers.add(userItem);
//					userItem.setSelected(false);
//					userAlertAction.addUserId(userItem.getUser().getId());
//				}
//			}
//		}
	}

	public void addUserAll(ActionEvent event) {		
		for(UserItem userItem : searchUsers) {
			boolean alreadyInList = false;
			for(UserItem userItemInList : selectedUsers) {
				if(userItem.getUser().getId() == userItemInList.getUser().getId()) {
					alreadyInList = true;
					break;
				}
			}
			if(!alreadyInList) {
				selectedUsers.add(userItem);
				userAlertAction.addUserId(userItem.getUser().getId());
			}
//			if (!selectedUsers.contains(userItem)) {
//				userItem.setSelected(false);
//				selectedUsers.add(userItem);
//				userItem.setSelected(false);
//				userAlertAction.addUserId(userItem.getUser().getId());
//			}
		}
		searchUsers.clear();
	}	
	
	public void removeUser(ActionEvent event) {
		UserItem userItemToRemove = (UserItem)event.getComponent().getAttributes().get("userItem");
		Iterator<UserItem> selectionIT = selectedUsers.iterator();
		while (selectionIT.hasNext()) {
			UserItem userItem = selectionIT.next();
			if (userItem.getUser().getId() == userItemToRemove.getUser().getId()) {
				userItemToRemove.setSelected(false);
				selectionIT.remove();
				searchUsers.add(userItemToRemove);
				userAlertAction.removeUserId(userItem.getUser().getId());
			}
		}	
	}
	
	public void removeUserAll(ActionEvent event) {
		Iterator<UserItem> selectionIT = selectedUsers.iterator();
		while (selectionIT.hasNext()) {
			UserItem userItem = selectionIT.next();
			userItem.setSelected(false);
			selectionIT.remove();
			searchUsers.add(userItem);
			userAlertAction.removeUserId(userItem.getUser().getId());
		}
	}	
	
	public void onChangeAllSelected(ValueChangeEvent evnt) {	
		this.allSelected = (Boolean)evnt.getNewValue();
	
		for (UserItem item : selectedUsers) {
			item.setSelected(allSelected);
		}
	}
	
	private void loadUsers() {
		selectedUsers = new ArrayList<UserItem>();
		
		Collection<Long> userIds = userAlertAction.getUserIds();
		if (userIds != null && !userIds.isEmpty()) {	
			Map<Long, User> userMap2 = getUserFromIds(userIds);
			
			for(User user : userMap2.values()) {
				UserItem userItem = new UserItem(user);	
				selectedUsers.add(userItem);
			}	
		}		
	}

	public void searchUsers(ValueChangeEvent event) {
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		if(lastNameSearch.trim().length() > 0) {
			searchUsers = new ArrayList<UserItem>();
			Map<Long, User> userMap = getUsersFromName(lastNameSearch);
			for(User user : userMap.values()) {
				UserItem userItem = new UserItem(user);	
				boolean alreadyInList = false;
				for(UserItem userItemInList : selectedUsers) {
					if(userItem.getUser().getId() == userItemInList.getUser().getId()) {
						alreadyInList = true;
						break;
					}
				}
				if(!alreadyInList) {
					searchUsers.add(userItem);
				}
			}
		}
		else {
			searchUsers = new ArrayList<UserItem>();
			UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
			try {
				Map<Long, User> users = userPM.getUsers();
				
				for(User user : users.values()) {
					if (user.getId() != 1) {
						UserItem userItem = new UserItem(user);	
						boolean alreadyInList = false;
						for(UserItem userItemInList : selectedUsers) {
							if(userItem.getUser().getId() == userItemInList.getUser().getId()) {
								alreadyInList = true;
								break;
							}
						}
						if(!alreadyInList) {
							searchUsers.add(userItem);
						}
					}
				}
			} catch(Exception exc) {
				System.out.println(exc);
			}
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
			userMap = userPM.getUsers(userIds);
		} catch(Exception exc) {
			System.out.println(exc);
		}
		return userMap;
	}

	public boolean isAllSelected() {
		return allSelected;
	}

	public void setAllSelected(boolean allSelected) {
		this.allSelected = allSelected;
	}	
}
