package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.NetInfo;

public class HostNetworkInfo extends SystemInfo {
	private static final long serialVersionUID = 3302909791842393885L;
	
	/**
	 * @uml.property  name="netInfo"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private NetInfo netInfo;
	
	protected HostNetworkInfo(NetInfo netInfo) {
		super("NetworkInfo");
		this.netInfo = netInfo;
	}

	public String getDefaultGateway() {
		return netInfo.getDefaultGateway();
	}

	public String getDomainName() {
		return netInfo.getDomainName();
	}

	public String getHostName() {
		return netInfo.getHostName();
	}

	public String getPrimaryDns() {
		return netInfo.getPrimaryDns();
	}

	public String getSecondaryDns() {
		return netInfo.getSecondaryDns();
	}
}
