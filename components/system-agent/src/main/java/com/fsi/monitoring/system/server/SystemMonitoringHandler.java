package com.fsi.monitoring.system.server;

import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.NetStat;
import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.ProcUtil;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.Tcp;
import org.hyperic.sigar.Uptime;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import com.fsi.fwk.exception.SystemException;
import com.fsi.monitoring.system.dto.HostCpu;
import com.fsi.monitoring.system.dto.HostCpuInfo;
import com.fsi.monitoring.system.dto.HostCpuInfoList;
import com.fsi.monitoring.system.dto.HostCpuList;
import com.fsi.monitoring.system.dto.HostCpuPerc;
import com.fsi.monitoring.system.dto.HostCpuPercList;
import com.fsi.monitoring.system.dto.HostFileSystem;
import com.fsi.monitoring.system.dto.HostFileSystemList;
import com.fsi.monitoring.system.dto.HostFileSystemUsage;
import com.fsi.monitoring.system.dto.HostFileSystemUsageList;
import com.fsi.monitoring.system.dto.HostMemory;
import com.fsi.monitoring.system.dto.HostNetworkInfo;
import com.fsi.monitoring.system.dto.HostNetworkInterface;
import com.fsi.monitoring.system.dto.HostNetworkInterfaceStat;
import com.fsi.monitoring.system.dto.HostNetworkInterfaceStatList;
import com.fsi.monitoring.system.dto.HostNetworkStat;
import com.fsi.monitoring.system.dto.HostProcLoads;
import com.fsi.monitoring.system.dto.HostProcStat;
import com.fsi.monitoring.system.dto.HostProcessCpu;
import com.fsi.monitoring.system.dto.HostProcessInfo;
import com.fsi.monitoring.system.dto.HostProcessInfoList;
import com.fsi.monitoring.system.dto.HostProcessMemory;
import com.fsi.monitoring.system.dto.HostProcessState;
import com.fsi.monitoring.system.dto.HostProcessTime;
import com.fsi.monitoring.system.dto.HostSwap;
import com.fsi.monitoring.system.dto.HostTcp;
import com.fsi.monitoring.system.dto.HostUptime;
import com.fsi.monitoring.system.dto.SystemInfoBuilder;
import com.fsi.monitoring.system.util.LogErrorHandler;

public class SystemMonitoringHandler {
	private static final Logger LOG = Logger.getLogger(SystemMonitoringHandler.class);
	/**
	 * @uml.property  name="sigar"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Sigar sigar ;
	/**
	 * @uml.property  name="monitoredHost"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.Integer"
	 */
	private MonitoredHost monitoredHost;
	/**
	 * @uml.property  name="vmsTable"
	 * @uml.associationEnd  qualifier="vmID:java.lang.Integer java.lang.String"
	 */
	Hashtable<Integer, String[]> vmsTable;
	
	public SystemMonitoringHandler(MonitoredHost monitoredHost, Sigar sigar) {
		this.sigar = sigar;
		this.monitoredHost = monitoredHost;
		this.vmsTable = new Hashtable<Integer, String[]>();
	}

	public synchronized HostProcStat getProcStat() {
		try {
			ProcStat procStat = sigar.getProcStat();
			return (HostProcStat)SystemInfoBuilder.get(SystemInfoBuilder.PROC_STAT, procStat);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Process stats Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	public synchronized HostProcLoads getProcLoads() {
		try {
			double[] procLoads = sigar.getLoadAverage();
			return (HostProcLoads)SystemInfoBuilder.get(SystemInfoBuilder.PROC_LOADS, procLoads);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Proc Loads Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostCpu getCpu(){
		try {
			Cpu cpu = sigar.getCpu();
			return (HostCpu)SystemInfoBuilder.get(SystemInfoBuilder.CPU, cpu);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Cpu Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	public synchronized HostCpuPerc getCpuPerc() {
		try {
			CpuPerc cpuPerc = sigar.getCpuPerc();
			return (HostCpuPerc)SystemInfoBuilder.get(SystemInfoBuilder.CPU_PERC, cpuPerc);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Cpu Percentage Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostCpuList getCpuList() {
		try {
			Cpu[] cpus = sigar.getCpuList();
			HostCpuList hostCpuList = (HostCpuList)SystemInfoBuilder.get(SystemInfoBuilder.CPU_LIST, null);
			hostCpuList.setGlobalCpu(getCpu());
			HostCpu[] array = new HostCpu[cpus.length];
			int i = 0;
			for (Cpu cpu : cpus){
				array[i] = (HostCpu)SystemInfoBuilder.get(SystemInfoBuilder.CPU, cpu);
				i++;
			}			
			hostCpuList.setCpus(array);
			return hostCpuList;
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Cpu List : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostCpuPercList getCpuPercList() {
		try {
			CpuPerc[] cpuPercs = sigar.getCpuPercList();
			HostCpuPercList hostCpuPercList = (HostCpuPercList)SystemInfoBuilder.get(SystemInfoBuilder.CPU_PERC_LIST, null);
			hostCpuPercList.setGlobalCpuPerc(getCpuPerc());
			HostCpuPerc[] array = new HostCpuPerc[cpuPercs.length];
			int i = 0;
			for (CpuPerc cpuPerc : cpuPercs){
				array[i] = (HostCpuPerc)SystemInfoBuilder.get(SystemInfoBuilder.CPU_PERC, cpuPerc);
				i++;
			}			
			hostCpuPercList.setCpuPercs(array);
			return hostCpuPercList;
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Cpu Percentage Information List : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}


	public synchronized HostMemory getMemory() {
		try {
			Mem mem = sigar.getMem();
			return (HostMemory)SystemInfoBuilder.get(SystemInfoBuilder.MEMORY, mem);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Memory Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostCpuInfoList getCpuInfoList() {
		try {
			CpuInfo[] cpInfos = sigar.getCpuInfoList();
			HostCpuInfoList hostCpuInfoList = (HostCpuInfoList)SystemInfoBuilder.get(SystemInfoBuilder.CPU_INFO_LIST, null);
			HostCpuInfo[] array = new HostCpuInfo[cpInfos.length];
			int i = 0;
			for (CpuInfo cpuInfo : cpInfos){
				array[i] = (HostCpuInfo)SystemInfoBuilder.get(SystemInfoBuilder.CPU_INFO, cpuInfo);
				i++;
			}			
			hostCpuInfoList.setList(array);
			return hostCpuInfoList;
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Cpu Information List : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostNetworkInfo getNetworkInfo() {
		try {
			NetInfo netInfo = sigar.getNetInfo();
			return (HostNetworkInfo)SystemInfoBuilder.get(SystemInfoBuilder.NETWORK_INFO, netInfo);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Network Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}	

	public synchronized HostSwap getSwap() {
		try {
			Swap swap = sigar.getSwap();
			return (HostSwap)SystemInfoBuilder.get(SystemInfoBuilder.SWAP, swap);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Swap Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostUptime getUptime() {
		try {
			Uptime uptime = sigar.getUptime();
			return (HostUptime)SystemInfoBuilder.get(SystemInfoBuilder.UPTIME, uptime);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Uptime Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}	

	public synchronized HostNetworkStat getNetworkStat() {
		try {
			NetStat netstat = sigar.getNetStat();
			return (HostNetworkStat)SystemInfoBuilder.get(SystemInfoBuilder.NETWORK_STAT, netstat);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Network Statistics Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostTcp getTcp() {
		try {
			Tcp tcp = sigar.getTcp();
			return (HostTcp)SystemInfoBuilder.get(SystemInfoBuilder.TCP, tcp);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Network TCP Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostNetworkInterface getNetworkInterface() {		
		try {
			String netIntfs[] = sigar.getNetInterfaceList();
			return (HostNetworkInterface)SystemInfoBuilder.get(SystemInfoBuilder.NETWORK_INTERFACE, netIntfs);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Network Interface Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	public synchronized HostNetworkInterfaceStatList getNetworkInterfaceStatList() {
		try {
			String netIntfs[] = getNetworkInterface().getNetworkInterfaces();
			NetInterfaceStat netStats[] = ((netIntfs == null) || (netIntfs.length == 0)) ? null
					: new NetInterfaceStat[netIntfs.length];	
			HostNetworkInterfaceStat[] hostNetworkInterfaceStats = null;
			if (netIntfs != null && netStats != null) {
				hostNetworkInterfaceStats = new HostNetworkInterfaceStat[netIntfs.length];
				for (int k = 0; k < netIntfs.length; ++k) {
					try {
						netStats[k] = sigar.getNetInterfaceStat(netIntfs[k]);
						hostNetworkInterfaceStats[k] = (HostNetworkInterfaceStat)SystemInfoBuilder.get(SystemInfoBuilder.NETWORK_INTERFACE_STAT, netStats[k]);
					} catch (Throwable e) {
						LogErrorHandler.displayError("Error when retrieving stats from device <" + netIntfs[k] + "> : " + e.getMessage(), LOG);
					}
				}
			}
			HostNetworkInterfaceStatList hostNetworkInterfaceStatList = (HostNetworkInterfaceStatList)SystemInfoBuilder.get(SystemInfoBuilder.NETWORK_INTERFACE_STAT_LIST, null);
			hostNetworkInterfaceStatList.setNetworkInterfaceStats(hostNetworkInterfaceStats);
			return hostNetworkInterfaceStatList;
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	public synchronized HostFileSystemList getFileSystemList() {		
		try {
			FileSystem fsys[] = sigar.getFileSystemList();
			HostFileSystemList hostFileSystemList = (HostFileSystemList)SystemInfoBuilder.get(SystemInfoBuilder.FILE_SYSTEM_LIST, null);
			HostFileSystem[] array = new HostFileSystem[fsys.length];
			int i = 0;
			for (FileSystem fileSystem : fsys){
				array[i] = (HostFileSystem)SystemInfoBuilder.get(SystemInfoBuilder.FILE_SYSTEM, fileSystem);
				i++;
			}			
			hostFileSystemList.setHostFileSystems(array);
			return hostFileSystemList;
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving File System Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	public synchronized HostFileSystemUsageList getFileSystemUsageList() {
		try {
			HostFileSystem fsys[] = getFileSystemList().getHostFileSystems();
			FileSystemUsage fsysUsage[] = ((fsys == null) || (fsys.length == 0)) ? null
					: new FileSystemUsage[fsys.length];
			HostFileSystemUsage[] hostFileSystemUsages = null;
			if (fsys != null && fsysUsage != null) {
				hostFileSystemUsages = new HostFileSystemUsage[fsys.length];
				for (int i = 0; i < fsys.length; ++i) {
					if (fsys[i] != null && fsys[i].getDevName() != null) {
						try {
							fsysUsage[i] = sigar.getFileSystemUsage(fsys[i].getDevName());
							hostFileSystemUsages[i] = (HostFileSystemUsage)SystemInfoBuilder.get(SystemInfoBuilder.FILE_SYSTEM_USAGE, fsysUsage[i]);
						} catch (Throwable e) {
							LogErrorHandler.displayError("Error when retrieving stats from device <" + fsys[i].getDevName() + "> : "  + e.getMessage(), LOG);
						}
					}
				}
			}	
			HostFileSystemUsageList hostFileSystemUsageList = (HostFileSystemUsageList)SystemInfoBuilder.get(SystemInfoBuilder.FILE_SYSTEM_USAGE_LIST, null);
			hostFileSystemUsageList.setHostFileSystemUsages(hostFileSystemUsages);
			return hostFileSystemUsageList;
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}	


	public synchronized HostProcessInfoList getProcessInfoList() {		
		try {
			HostProcessInfoList hostProcessInfoList = (HostProcessInfoList)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_INFO_LIST, null);
			long pids[] = sigar.getProcList();			
			if(monitoredHost != null)
				return getProcessInfoListUsingJStatd(pids, hostProcessInfoList);
			else
				return getProcessInfoListUsingSigarOnly(pids, hostProcessInfoList);
		} catch (SigarException e) {
			LogErrorHandler.displayError("Error when retrieving Process Information : " + e.getMessage(), LOG);
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	private HostProcessInfoList getProcessInfoListUsingSigarOnly(long pids[], HostProcessInfoList hostProcessInfoList) {		
		try {
			for (int i = 0; i < pids.length; i++) {
				try {
					HostProcessInfo procinfo = (HostProcessInfo)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_INFO, null);
					procinfo.setPid(pids[i]);
					procinfo.setProcessTime((HostProcessTime)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_TIME, sigar.getProcTime(pids[i])));
					procinfo.setProcessCpu((HostProcessCpu)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_CPU, sigar.getProcCpu(pids[i])));
					procinfo.setName(sigar.getProcExe(pids[i]).getName());
					procinfo.setTotal(sigar.getProcFd(pids[i]).getTotal());
					procinfo.setProcessMemory((HostProcessMemory)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_MEMORY, sigar.getProcMem(pids[i])));
					procinfo.setProcessState((HostProcessState)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_STATE, sigar.getProcState(pids[i])));
					procinfo.setArguments(sigar.getProcArgs(pids[i]));
					if (procinfo.getName().contains("java")) {
						String javaMainClass = getJavaClassName(procinfo.getArguments(), pids[i]);		
						procinfo.setJavaClassName(javaMainClass);
						StringBuffer javaArgs = new StringBuffer();
						int j = 1;
						int len = procinfo.getArguments().length;
						for(String arg : procinfo.getArguments()) {
							javaArgs.append(arg);
							if (j < len)
								javaArgs.append(" ");
							j = j + 1;
						}						
						procinfo.setJavaArgs(javaArgs.toString());
					}
					procinfo.setDescription(ProcUtil.getDescription(sigar, pids[i]));
					hostProcessInfoList.add(procinfo);	
				} catch (SigarException e) {
					LogErrorHandler.displayError("Error when retrieving Process Information : " + e.getMessage(), LOG);
				}
			}
			return hostProcessInfoList;
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	private HostProcessInfoList getProcessInfoListUsingJStatd(long pids[], HostProcessInfoList hostProcessInfoList) {		
		try {
			updateVmsTable();
			
			if (vmsTable.size() == 0)
				return getProcessInfoListUsingSigarOnly(pids, hostProcessInfoList);
			
			for (int i = 0; i < pids.length; i++) {
				try {
					HostProcessInfo procinfo = (HostProcessInfo)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_INFO, null);
					procinfo.setPid(pids[i]);
					procinfo.setProcessTime((HostProcessTime)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_TIME, sigar.getProcTime(pids[i])));
					procinfo.setProcessCpu((HostProcessCpu)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_CPU, sigar.getProcCpu(pids[i])));
					procinfo.setName(sigar.getProcExe(pids[i]).getName());
					procinfo.setTotal(sigar.getProcFd(pids[i]).getTotal());
					procinfo.setProcessMemory((HostProcessMemory)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_MEMORY, sigar.getProcMem(pids[i])));
					procinfo.setProcessState((HostProcessState)SystemInfoBuilder.get(SystemInfoBuilder.PROCESS_STATE, sigar.getProcState(pids[i])));
					procinfo.setArguments(sigar.getProcArgs(pids[i]));
					if (procinfo.getName().contains("java")) {
						String javaMainClass = getJavaClassName(procinfo.getArguments(), pids[i]);					
						String[] javaIds = vmsTable.get((int)pids[i]);
						if (javaIds != null) {
							procinfo.setJavaClassName((javaMainClass != null) ? javaMainClass : javaIds[0]);
							procinfo.setJavaArgs(javaIds[1]);
						}
					}
					procinfo.setDescription(ProcUtil.getDescription(sigar, pids[i]));
					hostProcessInfoList.add(procinfo);	
				} catch (SigarException e) {
					LogErrorHandler.displayError("Error when retrieving Process Information : " + e.getMessage(), LOG);
				}
			}
			return hostProcessInfoList;
		} catch (SecurityException e) {
			LOG.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	private String getJavaClassName(String[] args, long pid) throws SigarException {
		String name = ProcUtil.getJavaMainClass(sigar, pid);
		String idx = "-Dappname";
		if (name == null) {
			for (String arg : args) {
				if (arg.contains(idx)){
					name = arg.substring(idx.length()+1, arg.length());
				}
			}
		}
		return name;
	}
	
	private void updateVmsTable() {
		Set<Integer> jvms = null;
		try {
			jvms = (Set<Integer>)monitoredHost.activeVms();
		} catch (MonitorException e1) {
			LOG.error(e1.getMessage(), e1);
		}
		
		if (jvms!= null && jvms.size()>0) {
			for (Integer vmID : jvms) {
				if (vmsTable.containsKey(vmID)) 
					continue;
				try {
					String vmidString = "//" + vmID.intValue() + "?mode=r";
					MonitoredVm vm = monitoredHost.getMonitoredVm(new VmIdentifier(vmidString), 30000);
					String[] javaIds = new String[2];
					javaIds[0] = MonitoredVmUtil.mainClass(vm, true);
					javaIds[1] = MonitoredVmUtil.mainArgs(vm);				
					vmsTable.put(vmID, javaIds);
				} catch (URISyntaxException e) {
					LOG.error(e.getMessage(), e);
				} catch (MonitorException e) {
					LOG.error(e.getMessage(), e);
				}				
			}
		}
	}
	
}
