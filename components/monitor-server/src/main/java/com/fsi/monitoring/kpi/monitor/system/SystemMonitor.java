package com.fsi.monitoring.kpi.monitor.system;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.monitoring.connector.systemAgent.SystemAgentConnector;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemCPUResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemDiskResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemLoadResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemMemoryResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemNetworkInterfaceResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemNetworkStatResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemNetworkTcpResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemProcStatResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemSwapResourceData;
import com.fsi.monitoring.kpi.monitor.system.resourceData.SystemUptimeResourceData;
import com.fsi.monitoring.system.dto.HostCpuPerc;
import com.fsi.monitoring.system.dto.HostCpuPercList;
import com.fsi.monitoring.system.dto.HostFileSystem;
import com.fsi.monitoring.system.dto.HostFileSystemList;
import com.fsi.monitoring.system.dto.HostFileSystemUsage;
import com.fsi.monitoring.system.dto.HostFileSystemUsageList;
import com.fsi.monitoring.system.dto.HostMemory;
import com.fsi.monitoring.system.dto.HostNetworkInterface;
import com.fsi.monitoring.system.dto.HostNetworkInterfaceStat;
import com.fsi.monitoring.system.dto.HostNetworkInterfaceStatList;
import com.fsi.monitoring.system.dto.HostNetworkStat;
import com.fsi.monitoring.system.dto.HostProcLoads;
import com.fsi.monitoring.system.dto.HostProcStat;
import com.fsi.monitoring.system.dto.HostSwap;
import com.fsi.monitoring.system.dto.HostTcp;
import com.fsi.monitoring.system.dto.HostUptime;
import com.fsi.monitoring.system.dto.SystemInfo;

public class SystemMonitor 
extends MonitorTask {
	private HostFileSystem[] previousFileSystems = null;
	private HostFileSystemUsage[] previousFileSystemUsage = null;
	private String[] previousNetInfs;
	private HostNetworkInterfaceStat[] previousNetInfStats;	
	private HostTcp previousHostTcp;
	
	protected SystemAgentConnector systemAgentConnector;
	
	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {		
		systemAgentConnector = (SystemAgentConnector)getConnector(SystemAgentConnectorConfig.TYPE);		
	}
	
	@Override
	protected void postFetchs() throws Exception {}
	
	public SystemCPUResourceData fetchSYSTEM_CPU()
	throws ConnectorException {	
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_CPU_GLOBAL", null);
		Map<String, HostCpuPerc> infos = null;
		if (siInfo != null) {		
			infos = new HashMap<String, HostCpuPerc>();
			HostCpuPercList info = (HostCpuPercList)siInfo;
			HostCpuPerc global = info.getGlobalCpuPerc();
			infos.put("cpu[total]", global);
		}
		
		siInfo = systemAgentConnector.updateInfo("SYSTEM_CPU_CORE", null);
		if (siInfo != null) {
			HostCpuPercList info = (HostCpuPercList)siInfo;
			HostCpuPerc[] cpuPercs = info.getCpuPercs();
			for (int i=0;i<cpuPercs.length;i++) {
				HostCpuPerc cpuPerc = cpuPercs[i];				
				if (cpuPerc != null) 
					infos.put("cpu["+ i +"]", cpuPerc);
			}
		}
		
		return new SystemCPUResourceData(infos, new Date());
	}
	
	public SystemCPUResourceData fetchSYSTEM_CPU_CORE()
	throws ConnectorException {	
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_CPU_CORE", null);
		Map<String, HostCpuPerc> infos = null;
		if (siInfo != null) {
			infos = new HashMap<String, HostCpuPerc>();
			HostCpuPercList info = (HostCpuPercList)siInfo;
			HostCpuPerc[] cpuPercs = info.getCpuPercs();
			for (int i=0;i<cpuPercs.length;i++) {
				HostCpuPerc cpuPerc = cpuPercs[i];				
				if (cpuPerc != null) 
					infos.put("cpu["+ i +"]", cpuPerc);
			}
		}
		return new SystemCPUResourceData(infos, new Date());
	}
	
	public SystemUptimeResourceData fetchSYSTEM_UPTIME()
	throws ConnectorException {
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_UPTIME", null);
		return new SystemUptimeResourceData((HostUptime)siInfo, new Date());
	}
	
	public SystemProcStatResourceData fetchSYSTEM_PROC_STAT()
	throws ConnectorException {	
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_PROC_STAT", null);
		return new SystemProcStatResourceData((HostProcStat)siInfo, new Date());
	}
	
	public SystemSwapResourceData fetchSYSTEM_SWAP()
	throws ConnectorException {		
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_SWAP", null);
		return new SystemSwapResourceData((HostSwap)siInfo, new Date());
	}
	
	public SystemNetworkTcpResourceData fetchSYSTEM_NETWORK_TCP()
	throws ConnectorException {		
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_NETWORK_TCP", null);
		if (siInfo != null) {
			HostTcp info = (HostTcp)siInfo;
			this.previousHostTcp = info;
		}
		return new SystemNetworkTcpResourceData((HostTcp)siInfo, previousHostTcp, new Date());
	}
	
	public SystemNetworkStatResourceData fetchSYSTEM_NETWORK_STAT()
	throws ConnectorException {	
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_NETWORK_STAT", null);
		return new SystemNetworkStatResourceData((HostNetworkStat)siInfo, new Date());
	}
	
	public SystemDiskResourceData fetchSYSTEM_DISK()
	throws ConnectorException {	
		HostFileSystemList fileSystemList = (HostFileSystemList)systemAgentConnector.updateInfo("SYSTEM_DISK", "LIST");
		HostFileSystemUsageList fileSystemUsageList = (HostFileSystemUsageList)systemAgentConnector.updateInfo("SYSTEM_DISK", "USAGE_LIST");
		HostFileSystem[] fileSys = (fileSystemList != null) ? fileSystemList.getHostFileSystems() : new HostFileSystem[0];
		HostFileSystemUsage[] infoUsages = (fileSystemUsageList != null) ? fileSystemUsageList.getHostFileSystemUsages() : new HostFileSystemUsage[0];
		if(previousFileSystems != null && previousFileSystems.length != fileSys.length) {
			previousFileSystems =  null;
			previousFileSystemUsage = null;
		}	
		
		Map<String, HostFileSystemUsage> currentInfos = new HashMap<String, HostFileSystemUsage>();
		Map<String, HostFileSystemUsage> previousInfos = new HashMap<String, HostFileSystemUsage>();
		for (int i = 0; i < infoUsages.length; ++i) {
			if (infoUsages[i] != null) {
				String name = "disc["+ fileSys[i].getDevName() + "]";
				currentInfos.put(name, infoUsages[i]);
				previousInfos.put(name, previousFileSystemUsage != null? previousFileSystemUsage[i] : null);
			}
		}
		previousFileSystems = fileSys;
		previousFileSystemUsage = infoUsages;
		
		return new SystemDiskResourceData(currentInfos, previousInfos, new Date());
	}
	
	public SystemLoadResourceData fetchSYSTEM_LOAD()
	throws ConnectorException {	
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_LOAD", null);
		return new SystemLoadResourceData((HostProcLoads)siInfo, new Date());
	}
	

	public SystemMemoryResourceData fetchSYSTEM_MEMORY()
	throws ConnectorException {	
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_MEMORY", null);
		return new SystemMemoryResourceData((HostMemory)siInfo, new Date());
	}
	
	public SystemNetworkInterfaceResourceData fetchSYSTEM_NETWORK_INTERFACE()
	throws ConnectorException {	
		HostNetworkInterface networkInterface = (HostNetworkInterface)systemAgentConnector.updateInfo("SYSTEM_NETWORK_INTERFACE", "LIST");
		HostNetworkInterfaceStatList networkInterfaceStatList = (HostNetworkInterfaceStatList)systemAgentConnector.updateInfo("SYSTEM_NETWORK_INTERFACE", "STATS");
		String[] netInfs = (networkInterface != null) ? networkInterface.getNetworkInterfaces() : new String[0];
		HostNetworkInterfaceStat[] netInfStats = (networkInterfaceStatList != null) ? networkInterfaceStatList.getNetworkInterfaceStats() : new HostNetworkInterfaceStat[0];
		if(previousNetInfs != null && previousNetInfStats.length != netInfs.length) {
			previousNetInfs =  null;
			previousNetInfStats = null;
		}	
		
		Map<String, HostNetworkInterfaceStat> currentInfos = new HashMap<String, HostNetworkInterfaceStat>();
		Map<String, HostNetworkInterfaceStat> previousInfos = new HashMap<String, HostNetworkInterfaceStat>();
		for (int i = 0; i < netInfStats.length; ++i) {
			if (netInfStats[i] != null) {
				String name = "netinfs["+ netInfs[i] + "]";
				currentInfos.put(name, netInfStats[i]);
				previousInfos.put(name, previousNetInfStats != null? previousNetInfStats[i] : null);
			}
		}
		previousNetInfs = netInfs;
		previousNetInfStats = netInfStats;

		return new SystemNetworkInterfaceResourceData(currentInfos, previousInfos, new Date());
	}

	@Override
	protected void initConnection() throws Exception {}	
}
