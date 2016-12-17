package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.ProcCpu;


public class HostProcessCpu extends SystemInfo {	
	private static final long serialVersionUID = -3742351775706664344L;
	
	/**
	 * @uml.property  name="procCpu"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ProcCpu procCpu;
	
	protected HostProcessCpu(ProcCpu procCpu) {
		super("ProcessCpu");
		this.procCpu = procCpu;
	}
	
	
	public long getLastTime() {
		return procCpu.getLastTime();
	}
	
	public double getCpuPercent() {
		return procCpu.getPercent();
	}
	
	public long getStartTime() {
		return procCpu.getStartTime();
	}	
	
	public long getCpuSys() {
		return procCpu.getSys();
	}	
	
	public long getCpuTotal() {
		return procCpu.getTotal();
	}
	
	public long getCpuUser() {
		return procCpu.getUser();
	}

}
