package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.FileSystem;

public class HostFileSystem extends SystemInfo {
	private static final long serialVersionUID = -6966415193338054335L;
	
	/**
	 * @uml.property  name="fileSystem"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private FileSystem fileSystem;
	
	protected HostFileSystem(FileSystem fileSystem) {
		super("FileSystem");
		this.fileSystem = fileSystem;
	}
	
	public String getDevName() {
		return fileSystem.getDevName();
	}

	public String getDirName() {
		return fileSystem.getDirName();
	}

	public long getFlags() {
		return fileSystem.getFlags();
	}

	public String getSysTypeName() {
		return fileSystem.getSysTypeName();
	}

	public int getFileSystemType() {
		return fileSystem.getType();
	}

	public String getTypeName() {
		return fileSystem.getTypeName();
	}
}
