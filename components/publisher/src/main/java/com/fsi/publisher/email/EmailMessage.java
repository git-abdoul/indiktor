package com.fsi.publisher.email;

import java.util.Set;

import com.fsi.monitoring.user.User;
import com.fsi.publisher.common.UserMessage;

public class EmailMessage 
extends UserMessage {
	
	private static final long serialVersionUID = 144397773285577738L;
	
	private String subject;
	
	
	public EmailMessage(Set<User> users, 
						String subject, 
						String content) {
		super(users, content);
		this.subject = subject;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String[] getRecipients() {		
		String[] res = new String[users.size()];
		
		int i=0;
		for (User user : users) {
			res[i] = user.getEmail();
			i = i + 1;
		}
		
		return res;
	}

}
