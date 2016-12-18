package com.fsi.monitoring.user;

import com.fsi.monitoring.util.FacesUtils;

public class UserItem {

	private User user;
	private boolean selected;
	
	private String login;
	private String password;
	private String roles;
	
	public UserItem(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
//	public String select() {
//		UserBean userBean = (UserBean)FacesUtils.getManagedBean("userBean");
//		userBean.initUser(user.getId());
//		
//		return UserBean.UPDATE_USER;
//	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isSelected() {
		return selected;
	}	
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		return (int)user.getId();
	}
	
	@Override
	public boolean equals(Object obj) {
		UserItem other = (UserItem)obj;
		return user.getId() == other.getUser().getId();
	}


}
