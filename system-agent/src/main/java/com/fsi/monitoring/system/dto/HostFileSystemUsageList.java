package com.fsi.monitoring.system.dto;


public class HostFileSystemUsageList extends SystemInfo {
	private static final long serialVersionUID = -7812613906965922262L;
	
	/**
	 * @uml.property  name="hostFileSystemUsages"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private HostFileSystemUsage[] hostFileSystemUsages;
	
	protected HostFileSystemUsageList() {
		super("FileSystemUsageList");
	}
	
	public int getSize() {
		return hostFileSystemUsages.length;
	}
	
	/**
	 * @param hostFileSystemUsages
	 * @uml.property  name="hostFileSystemUsages"
	 */
	public void setHostFileSystemUsages(HostFileSystemUsage[] hostFileSystemUsages) {
		this.hostFileSystemUsages = hostFileSystemUsages;
	}
	
	/**
	 * @return
	 * @uml.property  name="hostFileSystemUsages"
	 */
	public HostFileSystemUsage[] getHostFileSystemUsages(){
		return hostFileSystemUsages;
	}
}
