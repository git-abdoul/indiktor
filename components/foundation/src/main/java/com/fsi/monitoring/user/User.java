package com.fsi.monitoring.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User 
implements Serializable {
	private static final long serialVersionUID = -2693567742913421679L;

	private long id;
	
	private String firstName;
	private String lastName;
	private String email;
	private String phone1;
	
	private List<Long> groupIds;
	private List<Long> roleIds;
	
	public User() {
		this.id = 0;
		this.firstName = "";
		this.lastName = "";
		this.email = "";
		this.phone1 = "";
		groupIds = new ArrayList<Long>();
		roleIds = new ArrayList<Long>();
	}
	
	public User(long id,
				String firstname,
				String lastname,
				String email,
				String phone1) {
		this.id = id;
		this.firstName = firstname;
		this.lastName = lastname;
		this.email = email;
		this.phone1 = phone1;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPhone1() {
		return phone1;
	}
	
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	
	public List<Long> getRoleIds() {
		return roleIds;
	}
	
	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}
	
	public List<Long> getGroupIds() {
		return groupIds;
	}
	
	public void setGroupsIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}	
	
	public boolean isAdmin() {
		return id==0;
	}
}
