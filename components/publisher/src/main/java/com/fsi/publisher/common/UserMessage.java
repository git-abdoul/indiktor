package com.fsi.publisher.common;

import java.util.Set;

import com.fsi.monitoring.user.User;

public class UserMessage {

	private String content;
	
	protected Set<User> users;
	
	
	public UserMessage(Set<User> users,
					   String content) {
		this.users = users;
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
	
}
