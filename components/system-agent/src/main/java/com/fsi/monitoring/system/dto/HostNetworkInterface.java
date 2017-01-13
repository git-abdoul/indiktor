package com.fsi.monitoring.system.dto;

public class HostNetworkInterface extends SystemInfo {
	private static final long serialVersionUID = -5861807369126292183L;
	
	/**
	 * @uml.property  name="networkInterfaces"
	 */
	private String[] networkInterfaces;
	
	protected HostNetworkInterface(String[] networkInterfaces) {
		super("NetworkInterface");
		this.networkInterfaces = networkInterfaces;
	}
	
	public int getSize() {
		return networkInterfaces.length;
	}
	
	/**
	 * @return
	 * @uml.property  name="networkInterfaces"
	 */
	public String[] getNetworkInterfaces(){
		return networkInterfaces;
	}
}
