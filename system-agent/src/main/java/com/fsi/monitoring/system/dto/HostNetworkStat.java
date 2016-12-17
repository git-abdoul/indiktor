package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.NetStat;

public class HostNetworkStat extends SystemInfo {
	private static final long serialVersionUID = -1498587583061958791L;
	
	/**
	 * @uml.property  name="netStat"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private NetStat netStat;

	protected HostNetworkStat(NetStat netStat) {
		super("NetworkStat");
		this.netStat = netStat;
	}

	public int getAllInboundTotal() {
		return netStat.getAllInboundTotal();
	}

	public int getAllOutboundTotal() {
		return netStat.getAllOutboundTotal();
	}

	public int getTcpBound() {
		return netStat.getTcpBound();
	}

	public int getTcpClose() {
		return netStat.getTcpClose();
	}

	public int getTcpCloseWait() {
		return netStat.getTcpCloseWait();
	}

	public int getTcpClosing() {
		return netStat.getTcpClosing();
	}

	public int getTcpEstablished() {
		return netStat.getTcpEstablished();
	}

	public int getTcpFinWait1() {
		return netStat.getTcpFinWait1();
	}

	public int getTcpFinWait2() {
		return netStat.getTcpFinWait2();
	}

	public int getTcpIdle() {
		return netStat.getTcpIdle();
	}

	public int getTcpInboundTotal() {
		return netStat.getTcpInboundTotal();
	}

	public int getTcpLastAck() {
		return netStat.getTcpLastAck();
	}

	public int getTcpListen() {
		return netStat.getTcpListen();
	}

	public int getTcpOutboundTotal() {
		return netStat.getTcpOutboundTotal();
	}

	public int getTcpSynRecv() {
		return netStat.getTcpSynRecv();
	}

	public int getTcpSynSent() {
		return netStat.getTcpSynSent();
	}

	public int getTcpTimeWait() {
		return netStat.getTcpTimeWait();
	}

	public int[] getTcpStates() {
		return netStat.getTcpStates();
	}

}
