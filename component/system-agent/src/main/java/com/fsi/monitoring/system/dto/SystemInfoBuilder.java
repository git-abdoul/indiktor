package com.fsi.monitoring.system.dto;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.NetStat;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcTime;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.Tcp;
import org.hyperic.sigar.Uptime;

import com.fsi.fwk.exception.BaseException;
import com.fsi.fwk.exception.SystemException;

public class SystemInfoBuilder {
	public static final String PROC_STAT = "ProcStat";
	public static final String PROC_LOADS = "ProcLoads";
	public static final String CPU = "Cpu";
	public static final String CPU_INFO = "CpuInfo";
	public static final String CPU_PERC = "CpuPerc";
	public static final String CPU_LIST = "CpuList";
	public static final String CPU_PERC_LIST = "CpuPercList";
	public static final String MEMORY = "Memory";
	public static final String CPU_INFO_LIST = "CpuInfoList";
	public static final String NETWORK_INFO = "NetworkInfo";
	public static final String SWAP = "Swap";
	public static final String UPTIME = "Uptime";
	public static final String NETWORK_STAT = "NetworkStat";
	public static final String TCP = "Tcp";
	public static final String NETWORK_INTERFACE = "NetworkInterface";
	public static final String NETWORK_INTERFACE_STAT_LIST = "NetworkInterfaceStatList";
	public static final String NETWORK_INTERFACE_STAT = "NetworkInterfaceStat";
	public static final String FILE_SYSTEM_LIST = "FileSystemList";
	public static final String FILE_SYSTEM = "FileSystem";
	public static final String FILE_SYSTEM_USAGE_LIST = "FileSystemUsageList";
	public static final String FILE_SYSTEM_USAGE = "FileSystemUsage";
	public static final String PROCESS_INFO_LIST = "ProcessInfoList";
	public static final String PROCESS_TIME = "ProcessTime";
	public static final String PROCESS_INFO = "ProcessInfo";
	public static final String PROCESS_MEMORY = "ProcessMemory";
	public static final String PROCESS_STATE = "ProcessState";
	public static final String PROCESS_CPU = "ProcessCpu";
	
	public static SystemInfo get(String type, Object sigarObject) throws SystemException{
		SystemInfo info = null;		
		if (PROC_STAT.equals(type))
			info = new HostProcStat((ProcStat)sigarObject);
		else if (PROC_LOADS.equals(type))
			info = new HostProcLoads((double[])sigarObject);
		else if (CPU.equals(type))
			info = new HostCpu((Cpu)sigarObject);
		else if (CPU_INFO.equals(type))
			info = new HostCpuInfo((CpuInfo)sigarObject);
		else if (CPU_PERC.equals(type))
			info = new HostCpuPerc((CpuPerc)sigarObject);
		else if (CPU_LIST.equals(type))
			info = new HostCpuList();
		else if (CPU_PERC_LIST.equals(type))
			info = new HostCpuPercList();
		else if (MEMORY.equals(type))
			info = new HostMemory((Mem)sigarObject);
		else if (NETWORK_INFO.equals(type))
			info = new HostNetworkInfo((NetInfo)sigarObject);
		else if (SWAP.equals(type))
			info = new HostSwap((Swap)sigarObject);
		else if (UPTIME.equals(type))
			info = new HostUptime((Uptime)sigarObject);
		else if (NETWORK_STAT.equals(type))
			info = new HostNetworkStat((NetStat)sigarObject);
		else if (TCP.equals(type))
			info = new HostTcp((Tcp)sigarObject);
		else if (NETWORK_INTERFACE.equals(type))
			info = new HostNetworkInterface((String[])sigarObject);
		else if (NETWORK_INTERFACE_STAT.equals(type))
			info = new HostNetworkInterfaceStat((NetInterfaceStat)sigarObject);
		else if (NETWORK_INTERFACE_STAT_LIST.equals(type))
			info = new HostNetworkInterfaceStatList();
		else if (FILE_SYSTEM_LIST.equals(type))
			info = new HostFileSystemList();
		else if (FILE_SYSTEM.equals(type))
			info = new HostFileSystem((FileSystem)sigarObject);
		else if (FILE_SYSTEM_USAGE_LIST.equals(type))
			info = new HostFileSystemUsageList();
		else if (FILE_SYSTEM_USAGE.equals(type))
			info = new HostFileSystemUsage((FileSystemUsage)sigarObject);
		else if (PROCESS_INFO_LIST.equals(type))
			info = new HostProcessInfoList();
		else if (PROCESS_TIME.equals(type))
			info = new HostProcessTime((ProcTime)sigarObject);
		else if (PROCESS_INFO.equals(type))
			info = new HostProcessInfo();
		else if (PROCESS_MEMORY.equals(type))
			info = new HostProcessMemory((ProcMem)sigarObject);
		else if (PROCESS_STATE.equals(type))
			info = new HostProcessState((ProcState)sigarObject);
		else if (PROCESS_CPU.equals(type))
			info = new HostProcessCpu((ProcCpu)sigarObject);
		else
			throw new SystemException(type + " Not Implemented", BaseException.EXCEPTION);		
		return info;		
	}
}
