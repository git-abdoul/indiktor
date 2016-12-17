package com.fsi.monitoring.ikr.model;

import java.io.Serializable;
import java.util.Date;

public class IkrVersion implements Serializable {
	private static final long serialVersionUID = -6621322375782410123L;
	
	private int major;
	private int minor;
	private int sub;
	
	private Date versionDate;
	
	private int patchVersion;
	private Date patchDate;
	
	public IkrVersion(int major, int minor, int sub, Date versionDate,
			int patchVersion, Date patchDate) {
		super();
		this.major = major;
		this.minor = minor;
		this.sub = sub;
		this.versionDate = versionDate;
		this.patchVersion = patchVersion;
		this.patchDate = patchDate;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getSub() {
		return sub;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public int getPatchVersion() {
		return patchVersion;
	}

	public Date getPatchDate() {
		return patchDate;
	}
	
	
	public String getMajorVersion() {
		return major + "." + minor + "." + sub;
	}
	
	public String getFullVersion() {
		return major + "." + minor + "." + sub + " P" + patchVersion;
	}
	
}
