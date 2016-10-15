package com.fsi.monitoring.user;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.encryption.DES;
import com.fsi.monitoring.global.IdGenerator;
import com.fsi.monitoring.user.dao.UserDAO;
import com.fsi.monitoring.user.exception.LoginFailedException;

public class UserPMFactory
implements UserPM {

	private static final Logger LOG = Logger.getLogger(UserPMFactory.class);	

	private UserDAO dao = null;
	private IdGenerator userIdGenerator;
	
	private DES pwdManager;
	
	public void setUserDAO(UserDAO userDAO) {
		this.dao = userDAO;
	}
	
	public void setUserIdGenerator (IdGenerator userIdGenerator) {
		this.userIdGenerator = userIdGenerator;
	}	
	
	public UserPMFactory() {
		pwdManager = new DES("USER PASSWORDS ENCRYPTION");
	}
	
	public User login(String login, String password) 
	throws  LoginFailedException {
		User user = null;

		try {			
			
			password = pwdManager.encrypt(password);
			
			user = dao.getUser(login, password);

			if (user == null) {
				throw new LoginFailedException();
			}
		} catch (Exception e) {
			LOG.error("Error occured while calling login", e);
		}
		return user;
	}

	public boolean createNewUser(String login, String password, User user) {
		try {
			long userId = userIdGenerator.getNextId(1);
				
			dao.createUser(user, userId);
			
			password = pwdManager.encrypt(password.toLowerCase());
			
			dao.createLogin(userId,login,password);
		} catch (Exception e) {
			LOG.error("Error occured while calling createNewUser", e);
			return false;
		}
		return true;
	}

	public boolean deleteUser(long userId) {
		try {
			dao.deleteUser(userId);
			dao.deleteUserLogin(userId);
		} catch (Exception e) {
			LOG.error("Error occured while calling deleteUser", e);
			return false;
		}
		return true;		
	}
	
	public boolean deleteRole(long roleId) {
		try {
			dao.deleteRole(roleId);
		} catch (Exception e) {
			LOG.error("Error occured while calling deleteRole", e);
			return false;
		}
		return true;	
	}

	public void deleteUserGroup(long groupId) {
		// TODO Auto-generated method stub
		
	}

	public Map<Long, AccessPerm> getAccessPerms() {
		Map<Long, AccessPerm> res = null;

		try {
			res = dao.getAccessPerms();
		} catch (Exception e) {
			LOG.error("Error occured while calling getAccessPerms", e);
		}
		return res;
	}

	public User getUser(long userId) {
		User user = null;

		try {
			user = dao.getUser(userId);

			if (user == null) {
				throw new LoginFailedException();
			}
		} catch (Exception e) {
			LOG.error("Error occured while calling getUser", e);
		}
		return user;
	}
	
	public String getPassword(long userId) {
		String pwd = null;

		try {
			pwd = dao.getPassword(userId);
			pwd = pwdManager.decrypt(pwd);
		} catch (Exception e) {
			LOG.error("Error occured while calling getPassword", e);
		}

		return pwd;
	}
	
	public String getLogin(long userId) {
		String login = null;

		try {
			login = dao.getLogin(userId);
		} catch (Exception e) {
			LOG.error("Error occured while calling getLogin", e);
		}

		return login;
	}

	public Map<Long, UserGroup> getUserGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updatePassword(long userId, String newPassword) {
		try {
			
			newPassword = pwdManager.encrypt(newPassword);
			
			dao.updatePassword(userId, newPassword);
		} catch (Exception e) {
			LOG.error("Error occured while calling updatePassword", e);	
		}			
	}

	public void updateRole(Role role) {
		try {
			if (role.getId() == 0) {
				// creation
				dao.createRole(role);
			} else {
				dao.updateRole(role);
			}
		} catch (Exception e) {
			LOG.error("Error occured while calling updateRole", e);
		}				
	}

	public void updateUser(User user)  {
		try {
			dao.updateUser(user);
		} catch (Exception e) {
			LOG.error("Error occured while calling updateUser", e);	
		}	
	}

	public void updateUserGroup(UserGroup userGroup) {
		// TODO Auto-generated method stub
		
	}

	public Map<Long, Role> getRoles() {
		Map<Long, Role> res = null;

		try {
			res = dao.getRoles();
		} catch (Exception e) {
			LOG.error("Error occured while calling getRoles", e);
		}
		return res;
	}

	public Map<Long, User> getUsers() throws RemoteException {
		Map<Long, User> res = null;

		try {
			res = dao.getUsers();
		} catch (Exception e) {
			LOG.error("Error occured while calling getUsers", e);	
		}
		return res;
	}
	
	public Map<Long, User> getUsers(Collection<Long> userIds) {
		Map<Long, User> res = null;
		
		try {
			res = dao.getUsers(userIds);
		} catch (Exception e) {
			LOG.error("Error occured while calling getUsers", e);
		}
		return res;
	}

	public Map<Long, User> searchUsers(String querySearch) {
		Map<Long, User> res = null;
		
		try {
			res = dao.searchUsers(querySearch);
		} catch (Exception e) {
			LOG.error("Error occured while calling searchUsers", e);
		}
		return res;
	}	
	
	public void resetLogin(User user) throws RemoteException {
		try {
			dao.updateUser(user);
			
			String password = pwdManager.encrypt(user.getLastName().toLowerCase());
			
			dao.resetLogin(user.getId(), user.getEmail(), password);
		} catch (Exception e) {
			LOG.error("Error occured while calling getUsers", e);
			throw new RemoteException("Error occured while calling getUsers",e);	
		}
	}
}
