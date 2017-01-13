package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.NetInterfaceStat;

public class HostNetworkInterfaceStat extends SystemInfo {
	private static final long serialVersionUID = 8038755225668679071L;
	
	/**
	 * @uml.property  name="netInterfaceStat"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private NetInterfaceStat netInterfaceStat;
	
	protected HostNetworkInterfaceStat(NetInterfaceStat netInterfaceStat) {
		super("NetworkInterfaceStat");
		this.netInterfaceStat = netInterfaceStat;
	}
	
	public long getRxBytes() {
		return netInterfaceStat.getRxBytes();
	}

	public long getRxDropped() {
		return netInterfaceStat.getRxDropped();
	}

	public long getRxErrors() {
		return netInterfaceStat.getRxErrors();
	}

	public long getRxFrame() {
		return netInterfaceStat.getRxFrame();
	}

	public long getRxOverruns() {
		return netInterfaceStat.getRxOverruns();
	}

	public long getRxPackets() {
		return netInterfaceStat.getRxPackets();
	}

	public long getSpeed() {
		return netInterfaceStat.getSpeed();
	}

	public long getTxBytes() {
		return netInterfaceStat.getTxBytes();
	}

	public long getTxCarrier() {
		return netInterfaceStat.getTxCarrier();
	}

	public long getTxCollisions() {
		return netInterfaceStat.getTxCollisions();
	}

	public long getTxDropped() {
		return netInterfaceStat.getTxDropped();
	}

	public long getTxErrors() {
		return netInterfaceStat.getTxErrors();
	}

	public long getTxOverruns() {
		return netInterfaceStat.getTxOverruns();
	}

	public long getTxPackets() {
		return netInterfaceStat.getTxPackets();
	}
}
