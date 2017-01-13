package com.fsi.monitoring.kpi.monitor.jstat;

import org.apache.log4j.Logger;

import com.fsi.monitoring.kpi.monitor.Monitor;
import com.fsi.monitoring.kpi.monitor.MonitorTask;

import sun.jvmstat.monitor.MonitoredHost;

public class JStatMonitor extends MonitorTask implements Monitor {
	private static final Logger LOG = Logger.getLogger(JStatMonitor.class);
//	
//	private static final String ALL_PROCESS_WILDCARD = "*";
//	
	private MonitoredHost monitoredHost;
	
	@Override
	protected void preFetchs() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postFetchs() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
//	private List<Integer> vmIds = new ArrayList<Integer>();
//	private List<String> nonMonitoredProcesses = new ArrayList<String>();
//	private String[] classNameFilters;
//	private List<MonitoredVMService> vmServices = new ArrayList<MonitoredVMService>();
//	private String hostname;
//
//	@Override
//	protected List<IkrInstanceData> fetch(IkrCategoryGroup ikrCategoryGroup) {
//		List<IkrInstanceData> res = null;
//		
//		try {
//			updateVMs();
//			
//			switch(ikrCategoryGroup) {
//				case JSTAT_PROCESS_MEMORY :
//					res = fetchProcessMemory();
//				break;
//				
//				case JSTAT_PROCESS_GC :
//					res = fetchProcessGC();
//				break;
//				
//				case JSTAT_PROCESS_CLASS :
//					res = fetchProcessClass();
//				break;
//				
//				case JSTAT_PROCESS_JVM :
//					res = fetchProcessJVM();
//				break;	
//			}
//		} catch(Exception exc) {
//			LOG.error("GetProcessMetrics error", exc);
//		}
//
//		return res;
//	}
//	
//	private List<IkrInstanceData> fetchProcessMemory() 
//	throws Exception {
//		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
//		for (MonitoredVMService service : vmServices) {
//			JStatProcessMemoryIkrInstanceData ikrInstanceData =
//				new JStatProcessMemoryIkrInstanceData(getProcessName(service) + "@" + hostname,
//													System.currentTimeMillis(),
//													service);	
//			// Only on ikrInstance for this ikrCategory
//			res.add(ikrInstanceData);
//		}	
//		return res;
//	}
//	
//	private List<IkrInstanceData> fetchProcessGC() 
//	throws Exception {
//		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
//		for (MonitoredVMService service : vmServices) {
//			JStatProcessGCIkrInstanceData ikrInstanceData =
//				new JStatProcessGCIkrInstanceData(getProcessName(service) + "@" + hostname,
//													System.currentTimeMillis(),
//													service);	
//			// Only on ikrInstance for this ikrCategory
//			res.add(ikrInstanceData);
//		}	
//		return res;
//	}
//	
//	private List<IkrInstanceData> fetchProcessClass() 
//	throws Exception {
//		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
//		for (MonitoredVMService service : vmServices) {
//			JStatProcessClassIkrInstanceData ikrInstanceData =
//				new JStatProcessClassIkrInstanceData(getProcessName(service) + "@" + hostname,
//													System.currentTimeMillis(),
//													service);	
//			// Only on ikrInstance for this ikrCategory
//			res.add(ikrInstanceData);
//		}	
//		return res;
//	}
//	
//	private List<IkrInstanceData> fetchProcessJVM() 
//	throws Exception {
//		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
//		for (MonitoredVMService service : vmServices) {
//			JStatProcessJVMIkrInstanceData ikrInstanceData =
//				new JStatProcessJVMIkrInstanceData(getProcessName(service) + "@" + hostname,
//													System.currentTimeMillis(),
//													service);	
//			// Only on ikrInstance for this ikrCategory
//			res.add(ikrInstanceData);
//		}	
//		return res;
//	}	
//	
//	// =========  INNER CLASSES ========= //
//	private class JStatProcessJVMIkrInstanceData extends IkrInstanceData {
//		private MonitoredVMService service;
//			
//		public JStatProcessJVMIkrInstanceData(String ikrInstance, long captureTime, MonitoredVMService service) {
//			super(ikrInstance,captureTime);
//			this.service = service;
//		}
//			
//		@Override
//		protected String getValue(int ikrCategoryId) 
//		throws Exception {
//			String value = null; 
//			
//			switch (ikrCategoryId) {
//				case 179: value = String.valueOf(service.getTotalCompilationTime()); break;
//				case 180: value = String.valueOf(service.getStartTime()); break;
//				case 181: value = String.valueOf(service.getClassLoadTime()); break;
//			}
//			return value;
//		}
//	}
//	
//	private class JStatProcessMemoryIkrInstanceData extends IkrInstanceData {		
//		private MonitoredVMService service;
//		
//		public JStatProcessMemoryIkrInstanceData(String ikrInstance, long captureTime, MonitoredVMService service) {
//			super(ikrInstance,captureTime);
//			this.service = service;
//		}
//		
//		@Override
//		protected String getValue(int ikrCategoryId) {
//			String value = null;
//			
//			switch (ikrCategoryId) {
////				case 150: value = String.valueOf(service.getInit()); break;
////				case 151: value = String.valueOf(service.getUsed()); break;
////				case 152: value = String.valueOf(service.getCommitted()); break;
////				case 153: value = String.valueOf(service.getMax()); break;
////				case 154: value = String.valueOf(service.getInit()); break;
////				case 155: value = String.valueOf(service.getUsed()); break;
//				case 156: value = String.valueOf(service.getEdenMemoryUsed()); break;
//				case 157: value = String.valueOf(service.getEdenMemoryCommitted()); break;			
//				case 158: value = String.valueOf(service.getEdenMemoryMax()); break;
//				case 159: value = String.valueOf(service.getTenuredSpaceMemoryUsed()); break;
//				case 160: value = String.valueOf(service.getTenuredSpaceMemoryCommitted()); break;
//				case 161: value = String.valueOf(service.getTenuredSpaceMemoryMax()); break;
//				case 162: value = String.valueOf(service.getSurvivorSpaceMemoryUsed()); break;
//				case 163: value = String.valueOf(service.getSurvivorSpaceMemoryCommitted()); break;
//				case 164: value = String.valueOf(service.getSurvivorSpaceMemoryMax()); break;
//				case 165: value = String.valueOf(service.getSurvivor1Used()); break;
//				case 166: value = String.valueOf(service.getSurvivor1Size()); break;
//				case 167: value = String.valueOf(service.getSurvivor1Capacity()); break;
//				case 168: value = String.valueOf(service.getSurvivor1Used()); break;
//				case 169: value = String.valueOf(service.getSurvivor1Size()); break;
//				case 170: value = String.valueOf(service.getSurvivor1Capacity()); break;
//				case 171: value = String.valueOf(service.getPermGenMemoryUsed()); break;
//				case 172: value = String.valueOf(service.getPermGenMemoryCommitted()); break;
//				case 173: value = String.valueOf(service.getPermGenMemoryMax()); break;
//			}
//			return value;
//		}
//	}
//	
//	private class JStatProcessClassIkrInstanceData extends IkrInstanceData {
//		private MonitoredVMService service;
//		
//		public JStatProcessClassIkrInstanceData(String ikrInstance, long captureTime, MonitoredVMService service) {
//			super(ikrInstance,captureTime);
//			this.service = service;
//		}
//		
//		@Override
//		protected String getValue(int ikrCategoryId) {
//			String value = null;
//			
//			switch (ikrCategoryId) {
//				case 182: value = String.valueOf(service.getCurrentClassesLoaded()); break;
//				case 183: value = String.valueOf(service.getTotalClassesUnloaded()); break;
//			}
//			return value;
//		}
//	}	
//	
//	private class JStatProcessGCIkrInstanceData extends IkrInstanceData {
//		private MonitoredVMService service;
//			
//		public JStatProcessGCIkrInstanceData(String ikrInstance, long captureTime, MonitoredVMService service) 
//		throws Exception {
//			super(ikrInstance,captureTime);
//			this.service = service;
//		}
//			
//		@Override
//		protected String getValue(int ikrCategoryId) {
//			String value = null;
//				
//			switch (ikrCategoryId) {
//			case 174: value = String.valueOf(service.getMemoryGCFullTime()); break;
//			case 175: value = String.valueOf(service.getMemoryGCFullCount()); break;
//			case 176: value = String.valueOf(service.getMemoryGCNewTime()); break;
//			case 177: value = String.valueOf(service.getMemoryGCNewCount()); break;
//			case 178: value = String.valueOf(service.getTotGCTime()); break;	
//					
//			}
//			return value;
//		}		
//	}
//	
//	private void updateVMs() throws CaptureException {
//		if (monitoredHost == null) {			
//			throw new CaptureException("Unable to update VMs, Monitored Host not found", BaseException.ERROR);
//		}
//		
//		try {
//			Set<Integer> jvms = (Set<Integer>)monitoredHost.activeVms();			
//			for (Integer vmID : jvms) {
//				if (!vmIds.contains(vmID)) {
//					String vmidString = "//" + vmID.intValue() + "?mode=r";	
//					
//					if (nonMonitoredProcesses.contains(vmidString))
//						continue;
//						
//					VmIdentifier id = new VmIdentifier(vmidString);
//					MonitoredVm vm = monitoredHost.getMonitoredVm(id, 30000);
//					MonitoredVMService vmService = new MonitoredVMService(vm, vmID);
//					String mainClass = vmService.getMainClass();
//					if (accepts(mainClass, classNameFilters)) {										
//						vmIds.add(vmID);
//						vmServices.add(vmService);
//					} else {
//						nonMonitoredProcesses.add(vmidString); // Non monitored processes
//					}
//				}
//			}
//		} catch (MonitorException e) {
//			throw new CaptureException(e.getMessage(), e, BaseException.EXCEPTION);
//		} catch (URISyntaxException e) {
//			throw new CaptureException(e.getMessage(), e, BaseException.EXCEPTION);
//		} catch (Exception e) {
//			throw new CaptureException(e.getMessage(), e, BaseException.EXCEPTION);
//		}
//	}
//	
//	private String getProcessName(MonitoredVMService vmService) {
//		try {
//			String mainClass = vmService.getMainClass();
//			String mainArgs =  MonitoredVmUtil.mainArgs(vmService.getMonitoredVm());
//			if (mainClass.toLowerCase().contains("calypso") && mainArgs != null && mainArgs.contains("RO")) {
//				mainClass += "RO";
//			}		
//			if (mainClass.toLowerCase().contains("mainentry")) {
//				mainClass += ":"+vmService.getVmID().toString();
//			}	
//			return mainClass;
//		} catch (MonitorException e) {
//			LOG.error(e.getMessage(), e);
//		}	
//		return null;
//	}
//	
//	private boolean accepts(String name, String[] filter){
//		if(filter==null || filter.length==0)
//			return false;
//		String filterComponent;
//		boolean accepted = false;
//		int filterInd = 0;
//		while(!accepted && filterInd<filter.length){
//			filterComponent = filter[filterInd];
//			accepted = name.contains(filterComponent) || filterComponent.equals(ALL_PROCESS_WILDCARD);
//			filterInd++;
//		}		
//		return accepted;
//	}
//	
//	public void init() throws Exception {
//		this.hostname = monitorConfig.getHostname();
//		String info1 = monitorConfig.getInfo1();
//		classNameFilters = (info1==null || info1.length()==0) ? new String[0] : info1.split(":");		
//	}

	@Override
	protected void initConnection() throws Exception {
//		Arguments arguments = new Arguments(new String[] {monitorConfig.getHostname()+":"+monitorConfig.getPort()});
//		try {
//			monitoredHost = MonitoredHost.getMonitoredHost(arguments.hostId());
//		} catch (MonitorException e) {
//			LOG.error("Unable to connect to JStad : " + e.getMessage(), e);
//		}
	}

	@Override
	protected void preStart() {
		// TODO Auto-generated method stub
		
	}
}
