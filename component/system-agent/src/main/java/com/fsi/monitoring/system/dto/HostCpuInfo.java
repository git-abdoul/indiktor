package com.fsi.monitoring.system.dto;


public class HostCpuInfo extends SystemInfo {
	private static final long serialVersionUID = -499948386324663370L;
	
	/**
	 * @uml.property  name="cpuInfo"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private org.hyperic.sigar.CpuInfo cpuInfo;
	
	protected HostCpuInfo(org.hyperic.sigar.CpuInfo cpuInfo) {
		super("CpuInfo");
		this.cpuInfo = cpuInfo;
	}

	public long getCacheSize() {
		return cpuInfo.getCacheSize();
	}

	public String getModel() {
		return cpuInfo.getModel();
	}

	public String getVendor() {
		return cpuInfo.getVendor();
	}

	public int getMhz() {
		return cpuInfo.getMhz();
	}
}
