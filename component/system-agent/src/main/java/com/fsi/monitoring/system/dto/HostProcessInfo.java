package com.fsi.monitoring.system.dto;

public class HostProcessInfo extends SystemInfo {
	private static final long serialVersionUID = -3401094418573957258L;
	
	public HostProcessInfo() {
		super("ProcessInfo");
	}
	
	/**
	 * @uml.property  name="pid"
	 */
	private long pid;
	/**
	 * @uml.property  name="hostProcessTime"
	 * @uml.associationEnd  
	 */
	private HostProcessTime hostProcessTime;
	/**
	 * @uml.property  name="hostProcessCpu"
	 * @uml.associationEnd  
	 */
	private HostProcessCpu hostProcessCpu;
	/**
	 * @uml.property  name="name"
	 */
	private String name;
	/**
	 * @uml.property  name="total"
	 */
	private long total;
	/**
	 * @uml.property  name="hostProcessMemory"
	 * @uml.associationEnd  
	 */
	private HostProcessMemory hostProcessMemory;
	/**
	 * @uml.property  name="hostProcessState"
	 * @uml.associationEnd  
	 */
	private HostProcessState hostProcessState;
	/**
	 * @uml.property  name="arguments"
	 */
	private String[] arguments;
	/**
	 * @uml.property  name="javaClassName"
	 */
	private String javaClassName;
	/**
	 * @uml.property  name="javaArgs"
	 */
	private String javaArgs;
	/**
	 * @uml.property  name="description"
	 */
	private String description;
	
	/**
	 * @return
	 * @uml.property  name="javaClassName"
	 */
	public String getJavaClassName() {
		return javaClassName;
	}

	/**
	 * @param javaClassName
	 * @uml.property  name="javaClassName"
	 */
	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}

	public HostProcessCpu getProcessCpu() {
		return hostProcessCpu;
	}

	public void setProcessCpu(HostProcessCpu hostProcessCpu) {
		this.hostProcessCpu = hostProcessCpu;
	}

	public void setProcessTime(HostProcessTime hostProcessTime) {
		this.hostProcessTime = hostProcessTime;
	}

	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param total
	 * @uml.property  name="total"
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	public void setProcessMemory(HostProcessMemory hostProcessMemory) {
		this.hostProcessMemory = hostProcessMemory;
	}

	public void setProcessState(HostProcessState hostProcessState) {
		this.hostProcessState = hostProcessState;
	}

	/**
	 * @param arguments
	 * @uml.property  name="arguments"
	 */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public HostProcessTime getProcessTime() {
		return hostProcessTime;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 * @uml.property  name="total"
	 */
	public long getTotal() {
		return total;
	}

	public HostProcessMemory getProcessMemory() {
		return hostProcessMemory;
	}

	public HostProcessState getProcessState() {
		return hostProcessState;
	}

	/**
	 * @return
	 * @uml.property  name="arguments"
	 */
	public String[] getArguments() {
		return arguments;
	}

	/**
	 * @return
	 * @uml.property  name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 * @uml.property  name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}	

	/**
	 * @return
	 * @uml.property  name="javaArgs"
	 */
	public String getJavaArgs() {
		return javaArgs;
	}

	/**
	 * @param javaArgs
	 * @uml.property  name="javaArgs"
	 */
	public void setJavaArgs(String javaArgs) {
		this.javaArgs = javaArgs;
	}

	/**
	 * @return
	 * @uml.property  name="pid"
	 */
	public long getPid() {
		return pid;
	}

	/**
	 * @param pid
	 * @uml.property  name="pid"
	 */
	public void setPid(long pid) {
		this.pid = pid;
	}
}
