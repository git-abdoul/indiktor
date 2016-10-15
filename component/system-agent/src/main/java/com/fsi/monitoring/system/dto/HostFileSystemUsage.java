package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.FileSystemUsage;

public class HostFileSystemUsage extends SystemInfo {
	private static final long serialVersionUID = 8118973518553901889L;
	
	/**
	 * @uml.property  name="fileSystemUsage"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private FileSystemUsage fileSystemUsage;
	
	protected HostFileSystemUsage(FileSystemUsage fileSystemUsage) {
		super("FileSystemUsage");
		this.fileSystemUsage = fileSystemUsage;
	}
	
	public long getAvail() {
		return fileSystemUsage.getAvail();
	}

	public long getDiskQueue() {
		return (long)fileSystemUsage.getDiskQueue();
	}

	public long getDiskReadBytes() {
		return fileSystemUsage.getDiskReadBytes();
	}

	public long getDiskReads() {
		return fileSystemUsage.getDiskReads();
	}

	public double getDiskServiceTime() {
		return fileSystemUsage.getDiskServiceTime();
	}

	public long getDiskWriteBytes() {
		return fileSystemUsage.getDiskWriteBytes();
	}

	public long getDiskWrites() {
		return fileSystemUsage.getDiskWrites();
	}

	public long getFiles() {
		return fileSystemUsage.getFiles();
	}

	public long getFree() {
		return fileSystemUsage.getFree();
	}

	public long getFreeFiles() {
		return fileSystemUsage.getFreeFiles();
	}

	public long getTotal() {
		return fileSystemUsage.getTotal();
	}

	public long getUsed() {
		return fileSystemUsage.getUsed();
	}

	public double getUsePercent() {
		return fileSystemUsage.getUsePercent();
	}
}
