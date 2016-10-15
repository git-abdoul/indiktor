package com.fsi.monitoring.user;


public class RoleItem {

	private Role role;
	
	private boolean selected;
	
	public RoleItem(Role role) {
		this.role = role;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
//	public String select() {
//		RoleBean roleBean = (RoleBean)FacesUtils.getManagedBean("roleBean");
//		roleBean.initRole(role);
//		
//		return RoleBean.UPDATE_ROLE;
//	}
	
}
