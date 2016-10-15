package com.fsi.monitoring.system.dto;


public class HostTcp extends  SystemInfo {
	private static final long serialVersionUID = -3254597724563296133L;
	
	/**
	 * @uml.property  name="tcp"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private org.hyperic.sigar.Tcp tcp;
	
	protected HostTcp(org.hyperic.sigar.Tcp tcp) {
		super("Tcp");
		this.tcp = tcp;
	}

	public long getActiveOpens() {
		return tcp.getActiveOpens();
	}

	public long getAttemptFails() {
		return tcp.getAttemptFails();
	}

	public long getCurrEstab() {
		return tcp.getCurrEstab();
	}

	public long getEstabResets() {
		return tcp.getEstabResets();
	}

	public long getInErrs() {
		return tcp.getInErrs();
	}

	public long getInSegs() {
		return tcp.getInSegs();
	}

	public long getOutRsts() {
		return tcp.getOutRsts();
	}

	public long getOutSegs() {
		return tcp.getOutSegs();
	}

	public long getPassiveOpens() {
		return tcp.getPassiveOpens();
	}

	public long getRetransSegs() {
		return tcp.getRetransSegs();
	}
}
