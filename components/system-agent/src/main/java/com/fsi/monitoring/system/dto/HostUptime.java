package com.fsi.monitoring.system.dto;

public class HostUptime extends SystemInfo {
	private static final long serialVersionUID = 1071598912497435048L;
	
	/**
	 * @uml.property  name="uptime"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private org.hyperic.sigar.Uptime uptime;

	public HostUptime(org.hyperic.sigar.Uptime uptime) {
		super("Uptime");
		this.uptime = uptime;
	}

	public double getUptime() {
		return uptime.getUptime();
	}
}
