package com.fsi.monitoring.system.dto;


public class HostFileSystemList extends SystemInfo {
	private static final long serialVersionUID = 3358565701907720816L;

	/**
	 * @uml.property  name="hostFileSystems"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private HostFileSystem[] hostFileSystems;
	
	protected HostFileSystemList() {
		super("FileSystemList");
	}
	
	public int getSize() {
		return hostFileSystems.length;
	}
	
	/**
	 * @param hostFileSystems
	 * @uml.property  name="hostFileSystems"
	 */
	public void setHostFileSystems(HostFileSystem[] hostFileSystems) {
		this.hostFileSystems = hostFileSystems;
	}
	
	/**
	 * @return
	 * @uml.property  name="hostFileSystems"
	 */
	public HostFileSystem[] getHostFileSystems(){
		return hostFileSystems;
	}
}
