package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.ProcMem;

public class HostProcessMemory extends SystemInfo {
	private static final long serialVersionUID = 5269771787485533041L;

	/**
	 * @uml.property  name="procMem"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ProcMem procMem;
	
	protected HostProcessMemory(ProcMem procMem) {
		super("ProcessMemory");
		this.procMem = procMem;
	}
	
	public long getMajorFaults() {
		return procMem.getMajorFaults();
	}

	public long getMinorFaults() {
		return procMem.getMinorFaults();
	}

	public long getPageFaults() {
		return procMem.getPageFaults();
	}

	public long getResident() {
		return procMem.getResident();
	}

	public long getRss() {
		return procMem.getRss();
	}

	public long getShare() {
		return procMem.getShare();
	}

	public long getSize() {
		return procMem.getSize();
	}

	public long getVsize() {
		return procMem.getVsize();
	}
}
