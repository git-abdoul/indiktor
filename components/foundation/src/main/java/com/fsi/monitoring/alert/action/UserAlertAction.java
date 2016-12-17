package com.fsi.monitoring.alert.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserAlertAction 
extends AbstractAlertAction 
implements AlertAction,Serializable {

	private static final long serialVersionUID = 7617290793991878942L;
	public enum RecipientType {USER, GROUP, ROLE};
	
	private Set<Long> userIds;
	
	public UserAlertAction(int id, 
						   long alertDefinitionId,
						   Collection<AlertActionType> types) {
		super(id, alertDefinitionId, types);
		userIds = new HashSet<Long>();
	}
	
	public UserAlertAction() {
		super(0, 0, new ArrayList<AlertActionType>());
		userIds = new HashSet<Long>();
	}
	
	public void addUserId(long userId) {
		userIds.add(userId);
	}
	
	public void removeUserId(long userId) {
		userIds.remove(userId);
	}
	
	public Collection<Long> getUserIds() {
		return userIds;
	}
	
	public void launch() {
		
	}
}
