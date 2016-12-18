package com.fsi.monitoring.user;

public class AccessPermBean 
implements Comparable<AccessPermBean> {

	private AccessPerm accessPerm;
	private boolean    selected;
	
	public AccessPermBean(AccessPerm accessPerm) {
		super();
		this.accessPerm = accessPerm;
	}
	
	public AccessPerm getAccessPerm() {
		return accessPerm;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int compareTo(AccessPermBean o) {
		if (accessPerm.getId() > o.getAccessPerm().getId()) {
			return 1;
		} else if (accessPerm.getId() < o.getAccessPerm().getId()) {
			return -1;
		}
		return 0;
	}
	
}
