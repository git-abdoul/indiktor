package com.fsi.monitoring.kpi.monitor.system.sysload;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.SysloadConnectorConfig;
import com.fsi.monitoring.connector.sysload.SysloadConnector;
import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.MonitorTask;

public class SystemMonitor 
extends MonitorTask {
	private static final Logger LOG = Logger.getLogger(SystemMonitor.class);	

	
	protected SysloadConnector sysloadConnector;
	Date fromDate;
	Date endDate;
	
	@Override
	protected void preFetchs() throws Exception {
		sysloadConnector = (SysloadConnector)getConnector(SysloadConnectorConfig.TYPE);
		Date now = new Date();
		endDate = new Date(now.getTime() - 5*60*1000);	
		fromDate = new Date(now.getTime() - 10*60*1000);		
	}	
		
	@Override
	protected void postFetchs() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public List<IkrInstanceData> fetchSYSTEM_CPU_GLOBAL()
	throws ConnectorException {	
		return fetchSystemCPU(true);
	}
	
	public List<IkrInstanceData> fetchSYSTEM_CPU_CORE()
	throws ConnectorException {	
		return fetchSystemCPU(false);
	}
	
	public List<IkrInstanceData> fetchSYSTEM_UPTIME()
	throws ConnectorException {
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		

		return res;
	}
	
	public List<IkrInstanceData> fetchSYSTEM_PROC_STAT()
	throws ConnectorException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();

		return res;
	}
	
	public List<IkrInstanceData> fetchSYSTEM_SWAP()
	throws ConnectorException {		
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		return res;
	}
	
	public List<IkrInstanceData> fetchSYSTEM_NETWORK_TCP()
	throws ConnectorException {		
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		return res;
	}
	
	public List<IkrInstanceData> fetchSYSTEM_NETWORK_STAT()
	throws ConnectorException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		return res;
	}
	
	private List<IkrInstanceData> fetchSystemCPU(boolean isGlobal)
	throws ConnectorException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
//		if (isGlobal){
//			SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_CPU_GLOBAL", null);
//
//			if (siInfo != null) {		
//				HostCpuPercList info = (HostCpuPercList)siInfo;
//	
//				HostCpuPerc global = info.getGlobalCpuPerc();
//				String ikrInstance = "cpu@" + systemAgentConnector.getHostname();
//				res.add(new SystemCPUIkrInstanceData(ikrInstance,fetchDate,global));
//			}
//		} else {
//			SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_CPU_CORE", null);
//
//			if (siInfo != null) {
//				HostCpuPercList info = (HostCpuPercList)siInfo;
//	
//				HostCpuPerc[] cpuPercs = info.getCpuPercs();
//				for (int i=0;i<cpuPercs.length;i++) {
//					String ikrInstance = "cpu#" + i + "@" + systemAgentConnector.getHostname();
//					HostCpuPerc cpuPerc = cpuPercs[i];				
//					if (cpuPerc != null) 
//						res.add(new SystemCPUIkrInstanceData(ikrInstance,fetchDate,cpuPerc));
//				}
//			}
//		}
		return res;
	}
	
	public List<IkrInstanceData> fetchSYSTEM_DISK()
	throws ConnectorException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();		

//		HostFileSystemList fileSystemList = (HostFileSystemList)systemAgentConnector.updateInfo("SYSTEM_DISK", "LIST");
//		HostFileSystemUsageList fileSystemUsageList = (HostFileSystemUsageList)systemAgentConnector.updateInfo("SYSTEM_DISK", "USAGE_LIST");
//		HostFileSystem[] fileSys = (fileSystemList != null) ? fileSystemList.getHostFileSystems() : new HostFileSystem[0];
//		HostFileSystemUsage[] infoUsages = (fileSystemUsageList != null) ? fileSystemUsageList.getHostFileSystemUsages() : new HostFileSystemUsage[0];
//		if(previousFileSystems != null && previousFileSystems.length != fileSys.length) {
//			previousFileSystems =  null;
//			previousFileSystemUsage = null;
//		}				
//		for (int i = 0; i < infoUsages.length; ++i) {
//			String ikrInstance = fileSys[i].getDevName() + "@" + systemAgentConnector.getHostname();
//			if (infoUsages[i] != null) {
//				SystemDiskIkrInstanceData ikrInstanceData = new SystemDiskIkrInstanceData(ikrInstance,
//																						  new Date(),
//																						  infoUsages[i],
//																						  previousFileSystemUsage != null? previousFileSystemUsage[i] : null);		
//				res.add(ikrInstanceData);				
//			}
//		}
//		previousFileSystems = fileSys;
//		previousFileSystemUsage = infoUsages;
		
		return res;
	}
	
	public List<IkrInstanceData> fetchSYSTEM_LOAD()
	throws ConnectorException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();	
		
//		SystemInfo info = systemAgentConnector.updateInfo("SYSTEM_LOAD", null);
//
//		if (info != null) {	
//			String ikrInstance = "load@" + systemAgentConnector.getHostname();
//			SystemLoadIkrInstanceData ikrInstanceData = 
//						new SystemLoadIkrInstanceData(ikrInstance,
//													  new Date(),
//													  (HostProcLoads)info);
//			res.add(ikrInstanceData);
//		}
		return res;
	}
	

	public List<IkrInstanceData> fetchSYSTEM_MEMORY()
	throws ConnectorException {	
		List<IkrInstanceData> res = null;			
		
//		long conv = 1000000;
//		
//		String ikrInstance = "memory@" + sysloadConnector.getConnectorContext();
//		
//		Map<Date,SystemMemoryResourceData> resMap = new HashMap<Date,SystemMemoryResourceData>();
//		
//		Collection<SysloadData> data = null;
//		
//		//used memory:	sUTILMEM     8B
//		data = sysloadConnector.getSysloadMetric("sUTILMEM     8B",fromDate,endDate);
//		
//		for (SysloadData fetch : data) {
//			Date fetchDate = fetch.getDate();
//			
//			SystemMemoryResourceData ikrInstanceData = resMap.get(fetchDate);
//			if (ikrInstanceData == null) {
//				ikrInstanceData = new SystemMemoryResourceData(ikrInstance,fetchDate);
//				resMap.put(fetchDate, ikrInstanceData);
//			}
//			ikrInstanceData.setUsed(Long.parseLong(fetch.getValue())*conv);
//		}
//		
//		//available memory:	sUTILMEM     8C
//		data = sysloadConnector.getSysloadMetric("sUTILMEM     8C",fromDate,endDate);
//		
//		for (SysloadData fetch : data) {
//			Date fetchDate = fetch.getDate();
//			
//			SystemMemoryResourceData ikrInstanceData = resMap.get(fetchDate);
//			if (ikrInstanceData == null) {
//				ikrInstanceData = new SystemMemoryResourceData(ikrInstance,fetchDate);
//				resMap.put(fetchDate, ikrInstanceData);
//			}
//			ikrInstanceData.setFree(Long.parseLong(fetch.getValue())*conv);
//		}
//		
//		//total memory size:	sUTILMEM     88
//		data = sysloadConnector.getSysloadMetric("sUTILMEM     88",fromDate,endDate);
//		
//		for (SysloadData fetch : data) {
//			Date fetchDate = fetch.getDate();
//			
//			SystemMemoryResourceData ikrInstanceData = resMap.get(fetchDate);
//			if (ikrInstanceData == null) {
//				ikrInstanceData = new SystemMemoryResourceData(ikrInstance,fetchDate);
//				resMap.put(fetchDate, ikrInstanceData);
//			}
//			ikrInstanceData.setTotal(Long.parseLong(fetch.getValue())*conv);
//		}			
//
//		res = new ArrayList<IkrInstanceData>(resMap.values());
		
		return res;
	}
	
	public List<IkrInstanceData> fetchSYSTEM_NETWORK_INTERFACE()
	throws ConnectorException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();

//		HostNetworkInterface networkInterface = (HostNetworkInterface)systemAgentConnector.updateInfo("SYSTEM_NETWORK_INTERFACE", "LIST");
//		HostNetworkInterfaceStatList networkInterfaceStatList = (HostNetworkInterfaceStatList)systemAgentConnector.updateInfo("SYSTEM_NETWORK_INTERFACE", "STATS");
//		String[] netInfs = (networkInterface != null) ? networkInterface.getNetworkInterfaces() : new String[0];
//		HostNetworkInterfaceStat[] netInfStats = (networkInterfaceStatList != null) ? networkInterfaceStatList.getNetworkInterfaceStats() : new HostNetworkInterfaceStat[0];
//		if(previousNetInfs != null && previousNetInfStats.length != netInfs.length) {
//			previousNetInfs =  null;
//			previousNetInfStats = null;
//		}	
//			
//		for (int i = 0; i < netInfStats.length; ++i) {
//			String ikrInstance = netInfs[i] + "@" + systemAgentConnector.getHostname();
//			if (netInfStats[i] != null) {
//				SystemNetworkInterfaceIkrInstanceData ikrInstanceData = 
//					new SystemNetworkInterfaceIkrInstanceData(ikrInstance,
//														      new Date(),
//															  netInfStats[i],
//															  previousNetInfStats != null? previousNetInfStats[i] : null);
//				res.add(ikrInstanceData);	
//			}
//		}
//		previousNetInfs = netInfs;
//		previousNetInfStats = netInfStats;

		return res;
	}		
	
	public void init() throws Exception {super.init();}	

	@Override
	protected void initConnection() throws Exception {}

	@Override
	protected void preStart() {
		// TODO Auto-generated method stub
		
	}
}
