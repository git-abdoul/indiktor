package com.fsi.monitoring.system.dto;

public class HostProcLoads extends SystemInfo {
	private static final long serialVersionUID = -5877038085243711117L;
	
	/**
	 * @uml.property  name="procLoads" multiplicity="(0 -1)" dimension="1"
	 */
	private double[] procLoads;

	protected HostProcLoads(double[] procLoads) {
		super("ProcLoads");
		this.procLoads = procLoads;
	}

	public double getLoad1Min() {
		return ((procLoads != null && procLoads.length > 0) ? procLoads[0] : 0.D);
	}

	public double getLoad5Min() {
		return ((procLoads != null && procLoads.length > 0) ? procLoads[1] : 0.D);
	}

	public double getLoad15Min() {
		return ((procLoads != null && procLoads.length > 0) ? procLoads[2] : 0.D);
	}
	
}
