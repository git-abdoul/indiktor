package com.fsi.monitoring.system.dto;


public class HostNetworkInterfaceStatList extends SystemInfo {
	private static final long serialVersionUID = 7563361735686600005L;
	
	/**
	 * @uml.property  name="networkInterfaceStats"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private HostNetworkInterfaceStat[] networkInterfaceStats;
	
	protected HostNetworkInterfaceStatList() {
		super("NetworkInterfaceStatList");		
	}
	
	public int getSize() {
		return networkInterfaceStats.length;
	}
	
	/**
	 * @return
	 * @uml.property  name="networkInterfaceStats"
	 */
	public HostNetworkInterfaceStat[] getNetworkInterfaceStats(){
		return networkInterfaceStats;
	}
	
	/**
	 * @param networkInterfaceStats
	 * @uml.property  name="networkInterfaceStats"
	 */
	public void setNetworkInterfaceStats(HostNetworkInterfaceStat[] networkInterfaceStats) {
		this.networkInterfaceStats = networkInterfaceStats;
	}
}
