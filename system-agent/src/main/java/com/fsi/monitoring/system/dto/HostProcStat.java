package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.ProcStat;

public class HostProcStat extends SystemInfo {
	private static final long serialVersionUID = -5456335056939777135L;
	
	/**
	 * @uml.property  name="procStat"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private org.hyperic.sigar.ProcStat procStat;
	
	protected HostProcStat(ProcStat procStat) {
		super("ProcStat");
		this.procStat = procStat;
	}

	public long getIdle() {
		return procStat.getIdle();
	}

	public long getRunning() {
		return procStat.getRunning();
	}

	public long getSleeping() {
		return procStat.getSleeping();
	}

	public long getStopped() {
		return procStat.getStopped();
	}

	public long getThreads() {
		return procStat.getThreads();
	}

	public long getTotal() {
		return procStat.getTotal();
	}

	public long getZombie() {
		return procStat.getZombie();
	}

}
