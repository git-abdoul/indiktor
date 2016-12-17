package com.fsi.monitoring.user;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import com.fsi.monitoring.user.exception.LoginFailedException;

public interface UserPM {

	// ------ USER ------ //	
	User login(String login, String password) 
	throws RemoteException, LoginFailedException;

	void resetLogin(User user)
	throws RemoteException;
	
	User getUser(long userId)
	throws RemoteException;
	
	String getPassword(long userId)
	throws RemoteException;
	
	String getLogin(long userId)
	throws RemoteException;
	
	Map<Long,User> getUsers()
	throws RemoteException;
	
	boolean createNewUser(String login, 
						  String password,
						  User user);

	void updateUser(User user);	
	
	void updatePassword(long userId, String newPassword); 
	
	boolean deleteUser(long userId);
	
	Map<Long,User> searchUsers(String querySearch);
	
	Map<Long,User> getUsers(Collection<Long> userIds);	
	
	// ------ ROLE ------ //
	Map<Long,AccessPerm> getAccessPerms();
	
	Map<Long, Role> getRoles();	
	
	void updateRole(Role role);
	
	boolean deleteRole(long roleId);
	
	// ------ GROUP ------ //
	Map<Long,UserGroup> getUserGroups();
	
	void updateUserGroup(UserGroup userGroup);	
	
	void deleteUserGroup(long groupId);
}
