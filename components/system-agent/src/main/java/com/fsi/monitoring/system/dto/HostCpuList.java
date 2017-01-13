package com.fsi.monitoring.system.dto;

public class HostCpuList extends SystemInfo {
	private static final long serialVersionUID = -1242528340727935645L;
	
	/**
	 * @uml.property  name="globalCpu"
	 * @uml.associationEnd  
	 */
	private HostCpu globalCpu;
	/**
	 * @uml.property  name="hostCpus"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private HostCpu[] hostCpus;

	protected HostCpuList() {
		super("CpuList");
	}
	
	public int size() {
		return hostCpus.length;
	}

	/**
	 * @return
	 * @uml.property  name="globalCpu"
	 */
	public HostCpu getGlobalCpu() {
		return globalCpu;
	}

	/**
	 * @param globalCpu
	 * @uml.property  name="globalCpu"
	 */
	public void setGlobalCpu(HostCpu globalCpu) {
		this.globalCpu = globalCpu;
	}
	
	public void setCpus(HostCpu[] hostCpus) {
		this.hostCpus = hostCpus;
	}
	
	public HostCpu[] getCpus() {
		return hostCpus;
	}
}
