package com.fsi.monitoring.user;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class UserBean 
extends SortableList {
	
	private static final String loginColumnName = "Login";
	private static final String firstnameColumnName = "First Name";
	private static final String lastnameColumnName = "Last Name";
	private static final String emailColumnName = "Email";
	
	private User user;
	
	private List<RoleItem> availableRoleItems = null;
	private List<RoleItem> userRoleItems = null;
	
	private String userRoleItemsStr;
	
	private HtmlPanelGroup updatePanel = null;

	private List<UserItem> userItems = null;
	private List<UserItem> userItemsSelected = null;
	private boolean rendererCreateNewUser = false;
	
	private int rowsByPage = 15;
	private boolean paginationVisible = false;
	
	private boolean selectAll = false;

	int numberUsersSelected = 0;
	
	private String login;
	
	//Password Management
	private String oldPassword;
	private String oldPasswordStyle = "";
	private String newPassword;
	private String verifyPassword;
	private String newPasswordStyleCMP = "";
	private String verifyPasswordStyleCMP = "";
	private String pwdMessage = "";

	private boolean loginMandatory = false;
	private String loginStyle = "width:280px;";
	private boolean newPasswordMandatory = false;
	private String newPasswordStyle = "width:280px;";
	private boolean verifyPasswordMandatory = false;
	private String verifyPasswordStyle = "width:280px;";
	private boolean firstNameMandatory = false;
	private String firstNameStyle = "width:280px;";
	private boolean lastNameMandatory = false;
	private String lastNameStyle = "width:280px;";
	private boolean emailMandatory = false;
	private boolean emailWrongFormat = false;
	private String emailStyle = "width:280px;";
	private boolean pwdNotMatch = false;
	
	private boolean loginError = false;
	private boolean emailError = false;
	private boolean noRole = false;
	private String roleStyle = "border: 1px solid #336699; text-align: center; width: 100%;";
	
	private boolean onEdit = false;
	
	private boolean roleModifyMode;
	private boolean enableResetLogin;
	
	private Map<Long, Role> roles;
	
	public UserBean () {
		super(loginColumnName);
		updatePanel = new HtmlPanelGroup();	
		userRoleItemsStr = "";
	}	
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public List<RoleItem> getAvailableRoleItems() {
		if (availableRoleItems != null && availableRoleItems.size() > 0) {
			Collections.sort(availableRoleItems, new Comparator<RoleItem>() {
				public int compare(RoleItem o1, RoleItem o2) {
					return o1.getRole().getName().compareToIgnoreCase(o2.getRole().getName());
				}			
			});
		}		
		return availableRoleItems;
	}	
	
	public List<RoleItem> getUserRoleItems() {
		if (userRoleItems != null && userRoleItems.size() > 0) {
			Collections.sort(userRoleItems, new Comparator<RoleItem>() {
				public int compare(RoleItem o1, RoleItem o2) {
					return o1.getRole().getName().compareToIgnoreCase(o2.getRole().getName());
				}			
			});			
		}
		return userRoleItems;
	}
	
	public String getUserRoleItemsStr() {
		int i = 0;
		for (RoleItem role : userRoleItems) {
			if (i == 0)
				userRoleItemsStr = role.getRole().getName();
			else {
				userRoleItemsStr = userRoleItemsStr + "," + role.getRole().getName();
			}
			i++;
		}
		return userRoleItemsStr;
	}	
	
	public boolean isEnableResetLogin() {
		return enableResetLogin;
	}

	public boolean isRoleModifyMode() {
		return roleModifyMode;
	}

	public void setUpdatePanel(HtmlPanelGroup panel) {
		this.updatePanel = panel;
	}
	
	public HtmlPanelGroup getUpdatePanel() {
		return updatePanel;
	}	
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}

	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}

	public String getPwdMessage() {
		return pwdMessage;
	}

	public void init(ActionEvent action) {
		if (!isAuthorized(115,"users")) {
			return;
		}
		userRoleItems = new ArrayList<RoleItem>();
		userItemsSelected = new ArrayList<UserItem>();
		selectAll = false;
		roles = getRoles();
		updateUsers();
	}
	
	public void pageChangeListener(ActionEvent action) {
		init(null);
	}
	
	public void changeMyPwd(ActionEvent action) {
		if (!isAuthorized(1000,"changeMyPwd")) {
			return;
		}
		
		pwdMessage = "";
		oldPassword = null;
		newPassword = null;
		verifyPassword = null;
		HttpSession session = FacesUtils.getHttpSession(false);
		user = (User) session.getAttribute("user");		
	}
	
	private void updateUsers() {
		userItems = new ArrayList<UserItem>();
		
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
		try {
			Map<Long, User> users = userPM.getUsers();
			
			for(User user : users.values()) {
				if (user.getId() != 1) {
					String userLogin = userPM.getLogin(user.getId());
					String password = userPM.getPassword(user.getId());
					List<Long> roleIds = user.getRoleIds();
					int i = 0;
					String userRoleStr = "";
					for (long id : roleIds) {
						Role role = roles.get(id);
						if (i == 0)
							userRoleStr = role.getName();
						else {
							userRoleStr = userRoleStr + "," + role.getName();
						}
						i++;
					}
					UserItem item = new UserItem(user);
					item.setLogin(userLogin);
					item.setRoles(userRoleStr);
					item.setPassword(password);
					userItems.add(item);
				}
			}
		} catch(Exception exc) {
			System.out.println(exc);
		}
	}
	
	public List<UserItem> getUserItems() {	
		if (userItems != null && userItems.size()>0)
			sort();
		return userItems;
	}
	
//	public void initNewUser() {
//		if (!isAuthorized(120,"users")) {
//			action();
//		}
//		resetUser();
//		updatePanel.setRendered(false);
////		return UPDATE_USER;
//	}	
	
	public void initUser() {
//		updatePanel.setRendered(true);
		roleModifyMode = false;
		enableResetLogin = false;
		availableRoleItems = new ArrayList<RoleItem>();
		userRoleItems = new ArrayList<RoleItem>();		
		
		List<Long> userRoleIds = user.getRoleIds();		
//		Map<Long,Role> roles = getRoles();
		for (Role role : roles.values()) {
			RoleItem roleItem = new RoleItem(role);
			if (userRoleIds.contains(roleItem.getRole().getId())) {
				userRoleItems.add(roleItem);
			} else {
				availableRoleItems.add(roleItem);
			}
		}
		
//		if (user.getId() != 0) {
//			int i = 0;
//			for (RoleItem role : userRoleItems) {
//				if (i == 0)
//					userRoleItemsStr = role.getRole().getName();
//				else {
//					userRoleItemsStr = userRoleItemsStr + "," + role.getRole().getName();
//				}
//				i++;
//			}
//			
//			UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
//			try {
//				login = userPM.getLogin(user.getId());
//				newPassword = userPM.getPassword(user.getId());
//				verifyPassword = newPassword;
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	public String getDeleteMessage() {
		numberUsersSelected = 0;
		for (UserItem userItem : userItems) {
			if (userItem.isSelected()){
				numberUsersSelected++;
			}
		}
		String message = "No components selected";
		if (numberUsersSelected == 1) {
			for (UserItem userItem : userItems) {
				if(userItem.isSelected()) {
					message = "Are you sure to delete this user : " + userItem.getUser().getFirstName() + " " + userItem.getUser().getLastName();
					break;
				}
			}
			return message;
		}
		else {
			message = "Are you sure to delete these " + numberUsersSelected + " users?";
			return message;
		}
	}
	
	public void handleRole(ActionEvent event) {
		roleModifyMode = true;
	}
	
	public void backToUserDesc(ActionEvent event) {
		roleModifyMode = false;
	}
	
	public void resetLogin(ActionEvent event) {
		if (!isAuthorized(119,"")) {
			return;
		}	
		
		if(newPassword != null && newPassword.length() > 0 && newPassword.equalsIgnoreCase(verifyPassword)) {
			try {
				UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
				userPM.updatePassword(user.getId(), newPassword);
			} catch(Exception exc) {
				System.out.println(exc);
			}		
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("Password doesn't Match. Please Retype new Password");
		}		
		enableResetLogin = false;
	}
	
	public void activateResetLogin(ActionEvent event) {
		if (!isAuthorized(119, "")) {
			setAccessDenied();
			return;
		}
		enableResetLogin = true;
		newPassword = "";
		verifyPassword = "";
	}
	
	public void deleteUser(ActionEvent event) {
		if (!isAuthorized(116,"users")) {
			setAccessDenied();
			return;
		}
		UserItem userItemSelected = (UserItem)event.getComponent().getAttributes().get("UserItem");
		user = userItemSelected.getUser();
		login = userItemSelected.getLogin();
		newPassword = userItemSelected.getPassword();
		verifyPassword = newPassword;
		userRoleItemsStr = userItemSelected.getRoles();
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
		try {
			userPM.deleteUser(user.getId());
		} catch(Exception exc) {
			System.out.println(exc);
		}	
		selectAll = false;
		userItemsSelected = new ArrayList<UserItem>();
		updateUsers();
		user = null;
	}
	
	public void deleteSelectedUsers(ActionEvent event) {
		if (!isAuthorized(116,"users")) {
			setAccessDenied();
			return;
		}	
		
		numberUsersSelected = 0;
		for (UserItem userItem : userItems) {
			if (userItem.isSelected()){
				numberUsersSelected++;
			}
		}
		if (numberUsersSelected > 0) {
			for (UserItem userItem : userItems) {
				if (userItem.isSelected()){
					user = userItem.getUser();
					UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
					try {
						userPM.deleteUser(user.getId());
					} catch(Exception exc) {
						System.out.println(exc);
					}	
					userItemsSelected = new ArrayList<UserItem>();
					selectAll = false;
					updateUsers();
					user = null;
				}
			}
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("No user has been selected");
		}
	}
	
	public void duplicate(ActionEvent event) {
		if (!isAuthorized(118,"users")) {
			setAccessDenied();
			return;
		}
		UserItem userItemSelected = (UserItem)event.getComponent().getAttributes().get("UserItem");
		user = userItemSelected.getUser();
		login = userItemSelected.getLogin();
		newPassword = userItemSelected.getPassword();
		verifyPassword = newPassword;
		userRoleItemsStr = userItemSelected.getRoles();
		initUser();
		User newUser = new User(0, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone1());
		newUser.setRoleIds(user.getRoleIds());
		newUser.setGroupsIds(user.getGroupIds());
		user = newUser;
		rendererCreateNewUser = true;
		
//		numberUsersSelected = 0;
//		for (UserItem userItem : userItems) {
//			if (userItem.isSelected()){
//				numberUsersSelected++;
//			}
//		}
//		if (numberUsersSelected < 2) {
//			if(numberUsersSelected == 1) {
//				initUser();
//				User newUser = new User(0, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone1());
//				newUser.setRoleIds(user.getRoleIds());
//				newUser.setGroupsIds(user.getGroupIds());
//				user = newUser;
//				rendererCreateNewUser = true;
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No user has been selected");
//			}
//		}
//		
////		if (user != null) {
////			User newUser = new User(0, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone1());
////			newUser.setRoleIds(user.getRoleIds());
////			newUser.setGroupsIds(user.getGroupIds());
////			user = newUser;
////			rendererCreateNewUser = true;
////		}	
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one user to edit");
//		}
	}	
	
	public void validate(ActionEvent event) {
//		if (!isAuthorized(117,"")) {
//			return ;
//		}	
		testFields();
		if(lastNameMandatory || loginMandatory || newPasswordMandatory || verifyPasswordMandatory
				|| emailMandatory || firstNameMandatory || loginError || emailError || emailWrongFormat || noRole)
			return;
		
		if(newPassword != null && newPassword.length() > 0 && newPassword.equalsIgnoreCase(verifyPassword)) {
			if (enableResetLogin) {
				resetLogin(event);
			}
			else {
				UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());		
				List<Long> userRoleIds = new ArrayList<Long>();
				
				for(RoleItem roleItem : userRoleItems) {
					userRoleIds.add(roleItem.getRole().getId());
				}		
				user.setRoleIds(userRoleIds);
				
				try {
					if (user.getId() == 0) {
						userPM.createNewUser(login, newPassword, user);
					} else {
						userPM.updateUser(user);
					}
				} catch(Exception exc) {
					System.out.println(exc);
				}	
			}
			updateUsers();
			rendererCreateNewUser = false;
			selectAll = false;
			userItemsSelected = new ArrayList<UserItem>();
			loginMandatory = false;
			loginStyle = "width:280px;";
			newPasswordMandatory = false;
			newPasswordStyle = "width:280px;";
			verifyPasswordMandatory = false;
			verifyPasswordStyle = "width:280px;";
			firstNameMandatory = false;
			firstNameStyle = "width:280px;";
			lastNameMandatory = false;
			lastNameStyle = "width:280px;";
			emailMandatory = false;
			emailWrongFormat = false;
			emailStyle = "width:280px;";
			pwdNotMatch = false;
			emailError = false;
			loginError = false;
			onEdit = false;
			noRole = false;
			roleStyle = "border: 1px solid #336699; text-align: center; width: 100%;";
		}
		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.init();
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Password doesn't Match. Please Retype new Password");
			pwdNotMatch = true;
			newPasswordStyle = "width:280px; border:1px solid red;";
			verifyPasswordStyle = "width:280px; border:1px solid red;";
			verifyPassword = "";
		}		
	}	
	
	public void testFields() {
		if(login.trim() == null || login.trim().length() == 0) {
			loginMandatory = true;
			loginStyle = "width:280px; border:1px solid red;";
		}
		else {
			loginMandatory = false;
			loginStyle = "width:280px;";
		}
		
		if(newPassword.trim() == null || newPassword.trim().length() == 0) {
			newPasswordMandatory = true;
			pwdNotMatch = false;
			newPasswordStyle = "width:280px; border:1px solid red;";
		}
		else {
			newPasswordMandatory = false;
			newPasswordStyle = "width:280px;";
		}
		
		if(verifyPassword.trim() == null || verifyPassword.trim().length() == 0) {
			verifyPasswordMandatory = true;
			pwdNotMatch = false;
			verifyPasswordStyle = "width:280px; border:1px solid red;";
		}
		else {
			verifyPasswordMandatory = false;
			verifyPasswordStyle = "width:280px;";
		}
		
		if(user.getFirstName().trim() == null || user.getFirstName().trim().length() == 0) {
			firstNameMandatory = true;
			firstNameStyle = "width:280px; border:1px solid red;";
		}
		else {
			firstNameMandatory = false;
			firstNameStyle = "width:280px;";
		}
		
		if(user.getLastName().trim() == null || user.getLastName().trim().length() == 0) {
			lastNameMandatory = true;
			lastNameStyle = "width:280px; border:1px solid red;";
		}
		else {
			lastNameMandatory = false;
			lastNameStyle = "width:280px;";
		}
		
		if(user.getEmail().trim() == null || user.getEmail().trim().length() == 0) {
			emailMandatory = true;
			emailWrongFormat = false;
			emailStyle = "width:280px; border:1px solid red;";
		}
		else if(!user.getEmail().trim().contains("@") || !user.getEmail().trim().contains(".")) {
			emailWrongFormat = true;
			emailMandatory = false;
			emailStyle = "width:280px; border:1px solid red;";
		}
		else {
			emailMandatory = false;
			emailWrongFormat = false;
			emailStyle = "width:280px;";
		}
		
		loginError = emailError = false;
		for(UserItem userItem : userItems) {
			if(userItem.getLogin().trim().equalsIgnoreCase(login.trim()) && userItem.getUser().getId() != user.getId()) {
				loginError = true;
				loginStyle = "width:280px; border:1px solid red;";
			}
			if(userItem.getUser().getEmail().trim().equalsIgnoreCase(user.getEmail().trim()) && userItem.getUser().getId() != user.getId()) {
				emailError = true;
				emailStyle = "width:280px; border:1px solid red;";
			}
		}
		if(!loginError && !loginMandatory)
			loginStyle = "width:280px;";
		if(!emailError && !emailMandatory && !emailWrongFormat)
			emailStyle = "width:280px;";
		
		if(!(userRoleItems.size() > 0)) {
			noRole = true;
			roleStyle = "border:1px solid red; text-align: center; width: 100%;";
		}
		else {
			noRole = false;
			roleStyle = "border: 1px solid #336699; text-align: center; width: 100%;";
		}
	}
	
	public void checkPassword(ActionEvent event) {
		if(newPassword != null && newPassword.length() > 0 && newPassword.equalsIgnoreCase(verifyPassword)) {
		}
		else {
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("Password doesn't Match. Please Retype new Password");
		}
	}
	
	public String validatePwd(ActionEvent event) {
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
		String currentPwd = "";
		try {
			currentPwd = userPM.getPassword(user.getId());
		} catch (RemoteException e) {
			System.out.println(e);
		}
		if (oldPassword != null && !oldPassword.equalsIgnoreCase(currentPwd)) {
			pwdMessage = "Wrong Old Password";
			oldPassword = "";
			newPassword = "";
			verifyPassword = "";
			oldPasswordStyle = "border:1px solid red;";
			newPasswordStyleCMP = "";
			verifyPasswordStyleCMP = "";
			return "changeMyPwd";
		}
		
		if(newPassword != null && newPassword.length() > 0 && newPassword.equalsIgnoreCase(verifyPassword)) {
			try {				
				userPM.updatePassword(user.getId(), newPassword);
				pwdMessage = "Password Modified";
				oldPasswordStyle = "";
				newPasswordStyleCMP = "";
				verifyPasswordStyleCMP = "";
			} catch(Exception exc) {
				System.out.println(exc);
			}		
		} else {
			oldPasswordStyle = "";
			newPasswordStyleCMP = "border:1px solid red;";
			verifyPasswordStyleCMP = "border:1px solid red;";
			pwdMessage = "Password doesn't Match. Please Retype new Password";	
			verifyPassword = "";
		}
		return "changeMyPwd";
	}	
	
//	private void resetUser() {
//		user = new User();		
//		roleItems = new ArrayList<RoleItem>();
//		
//		Map<Long,Role> roles = getRoles();
//		
//		for (Role role : roles.values()) {
//			RoleItem roleItem = new RoleItem(role);
//			roleItems.add(roleItem);
//		}
//	}	
	
	private Map<Long,Role> getRoles() {
		Map<Long,Role> res = null;
		
		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
		try {
			res = userPM.getRoles();
		} catch(Exception exc) {
			System.out.println(exc);
		}
		
		return res;
	}
	
//	private User getUser(long userId) {
//		User user = null;
//		
//		UserPM userPM = (UserPM)FacesUtils.getManagedBean(PersistencyBeanName.userPM.name());
//		try {
//			user = userPM.getUser(userId);
//		} catch(Exception exc) {
//			System.out.println(exc);
//		}
//		
//		return user;
//	}

	public boolean isRendererCreateNewUser() {
		return rendererCreateNewUser;
	}	
	
//	public void openCreateNewUserPopup(ActionEvent event) {
//		initNewUser();
//		rendererCreateNewUser = true;
//	} 
//	
//	public void openUpdateUserPopup(ActionEvent event) {
//		UserBean UserBean = (UserBean)event.getComponent().getAttributes().get("UserSelected");
//		rendererCreateNewUser = true;
//	} 
	
	public void openCreateNewUserPopup(ActionEvent event) {
		if (!isAuthorized(120,"users")) {
			setAccessDenied();
			return;
		}		
		rendererCreateNewUser = true;
		user = new User();
		login = "";
		newPassword = "";
		verifyPassword = "";
		userRoleItemsStr = "";
		initUser();
	} 
	
	public void openUpdateUserPopup(ActionEvent event) {
		if (!isAuthorized(117,"users")) {
			setAccessDenied();
			return;
		}
		UserItem userItemSelected = (UserItem)event.getComponent().getAttributes().get("UserItem");
		user = userItemSelected.getUser();
		login = userItemSelected.getLogin();
		newPassword = userItemSelected.getPassword();
		verifyPassword = newPassword;
		userRoleItemsStr = userItemSelected.getRoles();
		rendererCreateNewUser = true;
		initUser();
		
//		numberUsersSelected = 0;
//		for (UserItem userItem : userItems) {
//			if (userItem.isSelected()){
//				numberUsersSelected++;
//			}
//		}
//		if (numberUsersSelected < 2) {
//			if(numberUsersSelected == 1) {	
//				rendererCreateNewUser = true;
//				initUser();
//			}
//			else {
//				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//				error.setRendered(true);
//				error.setModal(true);
//				error.setType(ErrorMessageBean.WARNING);
//				error.addMessage("No user has been selected");
//			}
//		}		
////		if (user != null) {		
////			rendererCreateNewUser = true;
////			initUser();			
////		}	
//		else {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Please, select only one user to edit");
//		}
	} 
	
	public void closeCreateNewUserPopup(ActionEvent event) {
		rendererCreateNewUser = false;
		updateUsers();
		user = null;
		for(UserItem userItem : userItems) {
			for(UserItem userItemSelected : userItemsSelected) {
				if(userItem.getLogin().equals(userItemSelected.getLogin())
						&& userItem.getPassword().equals(userItemSelected.getPassword())) {
					userItem.setSelected(true);
				}
			}
		}
		loginMandatory = false;
		loginStyle = "width:280px;";
		newPasswordMandatory = false;
		newPasswordStyle = "width:280px;";
		verifyPasswordMandatory = false;
		verifyPasswordStyle = "width:280px;";
		firstNameMandatory = false;
		firstNameStyle = "width:280px;";
		lastNameMandatory = false;
		lastNameStyle = "width:280px;";
		emailMandatory = false;
		emailWrongFormat = false;
		emailStyle = "width:280px;";
		pwdNotMatch = false;
		emailError = false;
		loginError = false;
		onEdit = false;
		noRole = false;
		roleStyle = "border: 1px solid #336699; text-align: center; width: 100%;";
	} 
	
	public void rowSelectionListener(RowSelectorEvent event) {
		if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			event.queue();
			return;
		}
		
		int rowId = event.getRow();
		UserItem selectedUserItem = userItems.get(rowId);
		user = selectedUserItem.getUser();
		login = selectedUserItem.getLogin();
		newPassword = selectedUserItem.getPassword();
		verifyPassword = newPassword;
		userRoleItemsStr = selectedUserItem.getRoles();
	}

//	public UserItem getSelectedUserItem() {
//		return selectedUserItem;
//	}

	public int getRowsByPage() {
		return rowsByPage;
	}

	public void setRowsByPage(int rowsByPage) {
		this.rowsByPage = rowsByPage;
	}

	public boolean isPaginationVisible() {
		if (userItems.size() > rowsByPage)
			return paginationVisible = true;
		else
			return paginationVisible = false;
	}
	
	public void addRoleItem(ActionEvent event) {
		RoleItem roleItem = (RoleItem)event.getComponent().getAttributes().get("roleItem");
		userRoleItems.add(roleItem);
		availableRoleItems.remove(roleItem);
		
//		for(RoleItem item : availableRoleItems) {
//			if (item.getSelected()) {
//				item.setSelected(false);
//				userRoleItems.add(item);
//				availableRoleItems.remove(item);
//				break;
//			}			
//		}
	}

	public void removeRoleItem(ActionEvent event) {
		RoleItem roleItem = (RoleItem)event.getComponent().getAttributes().get("roleItem");
		availableRoleItems.add(roleItem);
		userRoleItems.remove(roleItem);
		
//		for(RoleItem item : userRoleItems) {
//			if (item.getSelected()) {
//				item.setSelected(false);
//				userRoleItems.remove(item);
//				availableRoleItems.add(item);
//				break;
//			}			
//		}
	}
	
	private void setAccessDenied() {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}

	public String getLoginColumnName() {
		return loginColumnName;
	}

	public String getFirstnameColumnName() {
		return firstnameColumnName;
	}

	public String getLastnameColumnName() {
		return lastnameColumnName;
	}

	public String getEmailColumnName() {
		return emailColumnName;
	}
	
	public void handleSelectedUser(ValueChangeEvent event) {
		UserItem userItemSelected = (UserItem)event.getComponent().getAttributes().get("UserItem");
		if(userItemSelected != null) {
			for(UserItem userItem : userItems) {
				if(userItem.equals(userItemSelected)) {
					userItem.setSelected((Boolean)event.getNewValue());
					if((Boolean)event.getNewValue())
						userItemsSelected.add(userItemSelected);
					else
						userItemsSelected.remove(userItemSelected);
				}
			}
		}
	}
	
	public void handleSelectAllUsers(ValueChangeEvent evt) {
		if (!evt.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
			evt.setPhaseId(PhaseId.INVOKE_APPLICATION);
			evt.queue();
			return;
		} 
		userItemsSelected.clear();
		for(UserItem userItem : userItems) {
			userItem.setSelected((Boolean)evt.getNewValue());
			if((Boolean)evt.getNewValue())
				userItemsSelected.add(userItem);
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public int getUserItemsSelected() {
		int size = userItemsSelected.size();
		return size;
	}

	public void setUserItemsSelected(List<UserItem> userItemsSelected) {
		this.userItemsSelected = userItemsSelected;
	}

	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}

	@Override
	protected void sort() {
		Collections.sort(userItems, new Comparator<UserItem>() {
			public int compare(UserItem o1, UserItem o2) {
				int res = 0;
				try {
					if (getLoginColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getLogin().toLowerCase().compareTo(o2.getLogin().toLowerCase()) :  o2.getLogin().toLowerCase().compareTo(o1.getLogin().toLowerCase());
					}
					else if (getFirstnameColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getUser().getFirstName().toLowerCase().compareTo(o2.getUser().getFirstName().toLowerCase()) : o2.getUser().getFirstName().toLowerCase().compareTo(o1.getUser().getFirstName().toLowerCase());
					}
					else if (getLastnameColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getUser().getLastName().toLowerCase().compareTo(o2.getUser().getLastName().toLowerCase()) : o2.getUser().getLastName().toLowerCase().compareTo(o1.getUser().getLastName().toLowerCase());
					}
					else if (getEmailColumnName().equals(getSortColumnName())) {
						res = ascending ? o1.getUser().getEmail().toLowerCase().compareTo(o2.getUser().getEmail().toLowerCase()) : o2.getUser().getEmail().toLowerCase().compareTo(o1.getUser().getEmail().toLowerCase());
					}
				}
				catch (Exception e) {}
				return res;
			}
		});		
		
	}

	//-------------Control and style---------------//
	
	public boolean isLoginMandatory() {
		return loginMandatory;
	}

	public String getLoginStyle() {
		return loginStyle;
	}

	public boolean isNewPasswordMandatory() {
		return newPasswordMandatory;
	}

	public String getNewPasswordStyle() {
		return newPasswordStyle;
	}

	public boolean isVerifyPasswordMandatory() {
		return verifyPasswordMandatory;
	}

	public String getVerifyPasswordStyle() {
		return verifyPasswordStyle;
	}

	public boolean isFirstNameMandatory() {
		return firstNameMandatory;
	}

	public String getFirstNameStyle() {
		return firstNameStyle;
	}

	public boolean isLastNameMandatory() {
		return lastNameMandatory;
	}

	public String getLastNameStyle() {
		return lastNameStyle;
	}

	public boolean isEmailMandatory() {
		return emailMandatory;
	}

	public String getEmailStyle() {
		return emailStyle;
	}

	public boolean isPwdNotMatch() {
		return pwdNotMatch;
	}

	public String getOldPasswordStyle() {
		return oldPasswordStyle;
	}

	public String getNewPasswordStyleCMP() {
		return newPasswordStyleCMP;
	}

	public String getVerifyPasswordStyleCMP() {
		return verifyPasswordStyleCMP;
	}

	public boolean isLoginError() {
		return loginError;
	}

	public boolean isEmailError() {
		return emailError;
	}

	public boolean isNoRole() {
		return noRole;
	}

	public String getRoleStyle() {
		return roleStyle;
	}

	public boolean isEmailWrongFormat() {
		return emailWrongFormat;
	}
	
	public boolean getListRendered() {
		return getUserItems().size() > 0;
	}
}
