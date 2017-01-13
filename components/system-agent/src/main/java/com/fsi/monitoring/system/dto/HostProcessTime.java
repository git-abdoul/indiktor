package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.ProcTime;

public class HostProcessTime extends SystemInfo {
	private static final long serialVersionUID = 653793900373480047L;
	
	/**
	 * @uml.property  name="procTime"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ProcTime procTime;
	
	protected HostProcessTime(ProcTime procTime) {
		super("ProcessTime");
		this.procTime = procTime;
	}
	
	public long getStartTime() {
		return procTime.getStartTime();
	}

	public long getSys() {
		return procTime.getSys();
	}

	public long getTotal() {
		return procTime.getTotal();
	}

	public long getUser() {
		return procTime.getUser();
	}
}
