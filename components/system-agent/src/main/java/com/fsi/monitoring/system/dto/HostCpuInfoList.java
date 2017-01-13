package com.fsi.monitoring.system.dto;

public class HostCpuInfoList extends SystemInfo {
	private static final long serialVersionUID = 8509716255067337850L;
	
	/**
	 * @uml.property  name="hostCpuInfos"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private HostCpuInfo[] hostCpuInfos;

	protected HostCpuInfoList() {
		super("CpuInfoList");
	}
	
	public void setList(HostCpuInfo[] hostCpuInfos) {
		this.hostCpuInfos = hostCpuInfos;
	}
	
	public HostCpuInfo[] getList() {
		return hostCpuInfos;
	}
}
