package com.fsi.monitoring.admin;

import java.io.Serializable;
import java.util.Date;

public class IkrAdminLogging implements Serializable {
	private static final long serialVersionUID = -455504546247519345L;
	
	private Date logDatetime;
	private IkrAdminLoggingCategory category;
	private String content;
	
	public IkrAdminLogging(Date logDatetime, IkrAdminLoggingCategory category,
			String content) {
		super();
		this.logDatetime = logDatetime;
		this.category = category;
		this.content = content;
	}

	public Date getLogDatetime() {
		return logDatetime;
	}

	public IkrAdminLoggingCategory getCategory() {
		return category;
	}

	public String getContent() {
		return content;
	}
}
