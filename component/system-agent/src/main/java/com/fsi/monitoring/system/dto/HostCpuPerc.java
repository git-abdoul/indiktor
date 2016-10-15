package com.fsi.monitoring.system.dto;

public class HostCpuPerc extends SystemInfo {
	private static final long serialVersionUID = 3443408377402822540L;
	
	/**
	 * @uml.property  name="cpuPerc"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private org.hyperic.sigar.CpuPerc cpuPerc;

	protected HostCpuPerc(org.hyperic.sigar.CpuPerc cpuPerc) {
		super("CpuPerc");
		this.cpuPerc = cpuPerc;
	}

	public double getIdle() {
		return cpuPerc.getIdle();
	}

	public double getUser() {
		return cpuPerc.getUser();
	}

	public double getSystem() {
		return cpuPerc.getSys();
	}

	public double getWait() {
		return cpuPerc.getWait();
	}

	public double getCombined() {
		return cpuPerc.getCombined();
	}

}
