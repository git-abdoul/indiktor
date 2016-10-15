package com.fsi.fwk.histo;

import java.io.Serializable;
import java.util.Date;

public class Historizable 
implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9025214229236152521L;

	private long id;
	
	private long version;
	private Date versionDate;
	private long userId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getVersion() {
		return version;
	}
	
	public void setVersion(long version) {
		this.version = version;
	}
	
	public Date getVersionDate() {
		return versionDate;
	}
	
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}

}
