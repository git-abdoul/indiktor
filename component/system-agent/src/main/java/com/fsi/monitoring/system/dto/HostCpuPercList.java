package com.fsi.monitoring.system.dto;

public class HostCpuPercList extends SystemInfo {
	private static final long serialVersionUID = 2346753077688860056L;
	
	/**
	 * @uml.property  name="globalCpuPerc"
	 * @uml.associationEnd  
	 */
	private HostCpuPerc globalCpuPerc;
	/**
	 * @uml.property  name="hostCpuPercs"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private HostCpuPerc[] hostCpuPercs;
	
	protected HostCpuPercList() {
		super("CpuPercList");
	}
	
	public int size() {
		return hostCpuPercs.length;
	}

	/**
	 * @return
	 * @uml.property  name="globalCpuPerc"
	 */
	public HostCpuPerc getGlobalCpuPerc() {
		return globalCpuPerc;
	}

	/**
	 * @param globalCpuPerc
	 * @uml.property  name="globalCpuPerc"
	 */
	public void setGlobalCpuPerc(HostCpuPerc globalCpuPerc) {
		this.globalCpuPerc = globalCpuPerc;
	}
	
	public void setCpuPercs(HostCpuPerc[] cpuPercs) {
		this.hostCpuPercs = cpuPercs;
	}
	
	public HostCpuPerc[] getCpuPercs() {
		return hostCpuPercs;
	}
}
