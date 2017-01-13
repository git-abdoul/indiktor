package com.fsi.monitoring.system.dto;


public class HostCpu extends SystemInfo {
	private static final long serialVersionUID = 3202953168400027110L;
	
	/**
	 * @uml.property  name="cpu"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private org.hyperic.sigar.Cpu cpu;

	protected HostCpu(org.hyperic.sigar.Cpu cpu) {
		super("Cpu");
		this.cpu = cpu;
	}

	public long getIdle() {
		return cpu.getIdle();
	}

	public long getNice() {
		return cpu.getNice();
	}

	public long getSystem() {
		return cpu.getSys();
	}

	public long getTotal() {
		return cpu.getTotal();
	}

	public long getUser() {
		return cpu.getUser();
	}

	public long getWait() {
		return cpu.getWait();
	}

}
