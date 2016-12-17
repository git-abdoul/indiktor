package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.Mem;

public class HostMemory extends SystemInfo {
	private static final long serialVersionUID = -1485844675229594556L;
	
	/**
	 * @uml.property  name="memory"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Mem memory;
	
	protected HostMemory(Mem memory) {
		super("Memory");
		this.memory = memory;
	}

	public long getActualFree() {
		return memory.getActualFree();
	}

	public long getActualUsed() {
		return memory.getActualUsed();
	}

	public long getFree() {
		return memory.getFree();
	}

	public long getRam() {
		return memory.getRam();
	}

	public long getTotal() {
		return memory.getTotal();
	}

	public long getUsed() {
		return memory.getUsed();
	}

}
