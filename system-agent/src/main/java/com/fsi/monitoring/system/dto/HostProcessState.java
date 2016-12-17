package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.ProcState;

public class HostProcessState extends SystemInfo {
	private static final long serialVersionUID = -5826295346461945936L;
	
	/**
	 * @uml.property  name="procState"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private ProcState procState;
	
	protected HostProcessState(ProcState procState) {
		super("ProcessState");
		this.procState = procState;
	}
	
	public String getName() {
		return procState.getName();
	}

	public int getNice() {
		return procState.getNice();
	}

	public long getPpid() {
		return procState.getPpid();
	}

	public int getPriority() {
		return procState.getPriority();
	}

	public int getProcessor() {
		return procState.getProcessor();
	}

	public char getState() {
		return procState.getState();
	}

	public long getThreads() {
		return procState.getThreads();
	}

	public int getTty() {
		return procState.getTty();
	}
}
