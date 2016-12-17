package com.fsi.publisher.sms;

import java.util.Set;

import com.fsi.monitoring.user.User;
import com.fsi.publisher.common.UserMessage;

public class SMSMessage
extends UserMessage {
	
	
	public SMSMessage(Set<User> users, 
					  String content) {
		super(users, content);
	}
	
	public String[] getRecipients() {		
		String[] res = new String[users.size()];
		
		int i=0;
		for (User user : users) {
			res[i] = user.getPhone1();
		}
		
		return res;
	}	
	
	public String[] getRecipients(SMSMessage message) {
		
		String[] res = new String[users.size()];
		
		int i=0;
		for (User user : users) {
			res[i] = user.getPhone1();
		}
		
		return res;
	}		
	
	
}
