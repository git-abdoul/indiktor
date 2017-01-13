package com.fsi.monitoring.system.dto;

public class HostSwap extends SystemInfo {
	private static final long serialVersionUID = 305963221735794321L;
	
	/**
	 * @uml.property  name="swap"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private org.hyperic.sigar.Swap swap;
	
	protected HostSwap(org.hyperic.sigar.Swap swap) {
		super("Swap");
		this.swap = swap;
	}

	public long getFree() {
		return swap.getFree();
	}
	
	public long getPageIn() {
		return swap.getPageIn();
	}

	public long getPageOut() {
		return swap.getPageOut();
	}

	public long getTotal() {
		return swap.getTotal();
	}

	public long getUsed() {
		return swap.getUsed();
	}
}
