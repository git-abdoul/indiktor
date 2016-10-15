package com.fsi.monitoring.user.dao;


import java.util.Collection;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.user.AccessPerm;
import com.fsi.monitoring.user.Role;
import com.fsi.monitoring.user.User;

public interface UserDAO {

	// ========== USER SERVICE ==========//
	User getUser(String login, String password) 
	throws PersistenceException;
	
	User getUser(long userId) 
	throws PersistenceException;
	
	String getPassword(long userId) 
	throws PersistenceException;
	
	public String getLogin(long userId) 
	throws PersistenceException;
	
	Map<Long,User> getUsers()
	throws PersistenceException;
	
	Map<Long,User> getUsers(Collection<Long> userIds)
	throws PersistenceException;	
	
	Map<Long,User> searchUsers(String querySearch)
	throws PersistenceException;	
	
	void createUser(User user, long userId)
	throws PersistenceException;
	
	void createLogin(long userId, String login, String password)
	throws PersistenceException;
	
	void deleteUserLogin(long userId)
	throws PersistenceException;
	
	void updatePassword(long userId, String password)
	throws PersistenceException;
	
	void updateUser(User user)
	throws PersistenceException;
	
	void deleteUser(long userId)
	throws PersistenceException;
	
	void deleteRole(long roleId)
	throws PersistenceException;
	
	Map<Long,AccessPerm> getAccessPerms()
	throws PersistenceException;
	
	Map<Long,Role> getRoles()
	throws PersistenceException;
	
	long createRole(Role role)
	throws PersistenceException;
	
	void updateRole(Role role)
	throws PersistenceException;
	
	void resetLogin(long userId, String login, String password)
	throws PersistenceException;
}
