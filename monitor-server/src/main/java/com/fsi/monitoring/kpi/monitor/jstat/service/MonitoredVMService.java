package com.fsi.monitoring.kpi.monitor.jstat.service;

import java.util.Date;

import sun.jvmstat.monitor.LongMonitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.StringMonitor;

public class MonitoredVMService  {

	private MonitoredVm vm;
	private Integer vmID;
	
	public Integer getVmID() {
		return vmID;
	}

	protected Date processStartDate = null;	
	
	public MonitoredVMService(MonitoredVm monitoredvm, Integer vmID) throws MonitorException {
		finalizerInitialized = false;
		vm = monitoredvm;
		this.vmID = vmID;
		initialize();
	}

	synchronized void initialize_finalizer() {
		if (finalizerInitialized)
			return;
		try {
			finalizerQLength = (LongMonitor) vm.findByName("sun.gc.finalizer.queue.length");
			if (finalizerQLength == null)
				return;
			finalizerQMaxLength = (LongMonitor) vm.findByName("sun.gc.finalizer.queue.maxLength");
			finalizerTime = (LongMonitor) vm.findByName("sun.gc.finalizer.time");
			finalizerCount = (LongMonitor) vm.findByName("sun.gc.finalizer.objects");
			finalizerInitialized = true;
		} catch (MonitorException monitorexception) {
		}
		return;
	}
	
	public String getMainClass() throws MonitorException {
		return MonitoredVmUtil.mainClass(vm, true);
	}
	
	public MonitoredVm getMonitoredVm() {
		return vm;
	}

	private void initialize_post_1_4_1() throws MonitorException {
		newGenMaxSize = (LongMonitor) vm.findByName("sun.gc.generation.0.maxCapacity");
		newGenMinSize = (LongMonitor) vm.findByName("sun.gc.generation.0.minCapacity");
		newGenCurSize = (LongMonitor) vm.findByName("sun.gc.generation.0.capacity");
		collector0name = (StringMonitor) vm.findByName("sun.gc.collector.0.name");
		collector1name = (StringMonitor) vm.findByName("sun.gc.collector.1.name");
		lastGCCause = (StringMonitor) vm.findByName("sun.gc.lastCause");
		currentGCCause = (StringMonitor) vm.findByName("sun.gc.cause");
	}

	private void initialize_common() throws MonitorException {
		survivor0Size = (LongMonitor) vm.findByName("sun.gc.generation.0.space.1.maxCapacity");
		survivor0Capacity = (LongMonitor) vm.findByName("sun.gc.generation.0.space.1.capacity");
		survivor0Used = (LongMonitor) vm.findByName("sun.gc.generation.0.space.1.used");
		survivor1Size = (LongMonitor) vm.findByName("sun.gc.generation.0.space.2.maxCapacity");
		survivor1Capacity = (LongMonitor) vm.findByName("sun.gc.generation.0.space.2.capacity");
		survivor1Used = (LongMonitor) vm.findByName("sun.gc.generation.0.space.2.used");
		edenSize = (LongMonitor) vm.findByName("sun.gc.generation.0.space.0.maxCapacity");
		edenCapacity = (LongMonitor) vm.findByName("sun.gc.generation.0.space.0.capacity");
		edenUsed = (LongMonitor) vm.findByName("sun.gc.generation.0.space.0.used");
		tenuredSize = (LongMonitor) vm.findByName("sun.gc.generation.1.space.0.maxCapacity");
		tenuredCapacity = (LongMonitor) vm.findByName("sun.gc.generation.1.space.0.capacity");
		tenuredUsed = (LongMonitor) vm.findByName("sun.gc.generation.1.space.0.used");
		permSize = (LongMonitor) vm.findByName("sun.gc.generation.2.space.0.maxCapacity");
		permCapacity = (LongMonitor) vm.findByName("sun.gc.generation.2.space.0.capacity");
		permUsed = (LongMonitor) vm.findByName("sun.gc.generation.2.space.0.used");
		edenGCEvents = (LongMonitor) vm.findByName("sun.gc.collector.0.invocations");
		edenGCTime = (LongMonitor) vm.findByName("sun.gc.collector.0.time");
		tenuredGCEvents = (LongMonitor) vm.findByName("sun.gc.collector.1.invocations");
		tenuredGCTime = (LongMonitor) vm.findByName("sun.gc.collector.1.time");
		ageTableSize = (LongMonitor) vm.findByName("sun.gc.generation.0.agetable.size");
		if (ageTableSize != null) {
			maxTenuringThreshold = (LongMonitor) vm.findByName("sun.gc.policy.maxTenuringThreshold");
			tenuringThreshold = (LongMonitor) vm.findByName("sun.gc.policy.tenuringThreshold");
			desiredSurvivorSize = (LongMonitor) vm.findByName("sun.gc.policy.desiredSurvivorSize");
			int i = (int) ageTableSize.longValue();
			ageTableSizes = new LongMonitor[i];
			String s = "sun.gc.generation.0.agetable.bytes.";
			for (int j = 0; j < i; j++)
				if (j < 10)
					ageTableSizes[j] = (LongMonitor) vm.findByName(s + "0" + j);
				else
					ageTableSizes[j] = (LongMonitor) vm.findByName(s + j);

		}
		classLoadTime = (LongMonitor) vm.findByName("sun.cls.time");
		classesLoaded = (LongMonitor) vm.findByName("java.cls.loadedClasses");
		classesUnloaded = (LongMonitor) vm.findByName("java.cls.unloadedClasses");
		classBytesLoaded = (LongMonitor) vm.findByName("sun.cls.loadedBytes");
		classBytesUnloaded = (LongMonitor) vm.findByName("sun.cls.unloadedBytes");
		totalCompileTime = (LongMonitor) vm.findByName("java.ci.totalTime");
		totalCompile = (LongMonitor) vm.findByName("sun.ci.totalCompiles");
		try {
			initialize_finalizer();
		} catch (Throwable throwable) {
		}
		osElapsedTime = (LongMonitor) vm.findByName("sun.os.hrt.ticks");
		osFrequency = (LongMonitor) vm.findByName("sun.os.hrt.frequency");
		javaCommand = (StringMonitor) vm.findByName("sun.rt.javaCommand");
		javaHome = (StringMonitor) vm.findByName("java.property.java.home");
		vmArgs = (StringMonitor) vm.findByName("java.rt.vmArgs");
		vmFlags = (StringMonitor) vm.findByName("java.rt.vmFlags");
		vmInfo = (StringMonitor) vm.findByName("java.property.java.vm.info");
		vmName = (StringMonitor) vm.findByName("java.property.java.vm.name");
		vmVersion = (StringMonitor) vm.findByName("java.property.java.vm.version");
		vmVendor = (StringMonitor) vm.findByName("java.property.java.vm.vendor");
		vmSpecName = (StringMonitor) vm.findByName("java.property.java.vm.specification.name");
		vmSpecVersion = (StringMonitor) vm.findByName("java.property.java.vm.specification.version");
		vmSpecVendor = (StringMonitor) vm.findByName("java.property.java.vm.specification.vendor");
		classPath = (StringMonitor) vm.findByName("java.property.java.class.path");
		bootClassPath = (StringMonitor) vm.findByName("sun.property.sun.boot.class.path");
		libraryPath = (StringMonitor) vm.findByName("java.property.java.library.path");
		bootLibraryPath = (StringMonitor) vm.findByName("sun.property.sun.boot.library.path");
		endorsedDirs = (StringMonitor) vm.findByName("java.property.java.endorsed.dirs");
		extDirs = (StringMonitor) vm.findByName("java.property.java.ext.dirs");
		lastModificationTime = (LongMonitor) vm.findByName("sun.perfdata.timestamp");
	}

	void initialize() throws MonitorException {
		initialize_common();
		if (!vmVersion.stringValue().startsWith("1.4.1"))
			initialize_post_1_4_1();
	}

	public long getNewGenMaxSize() {
		if (newGenMaxSize != null)
			return newGenMaxSize.longValue();
		else
			return edenSize.longValue() + survivor0Size.longValue()
					+ survivor1Size.longValue();
	}

	public long getNewGenMinSize() {
		if (newGenMinSize != null)
			return newGenMinSize.longValue();
		else
			return edenSize.longValue() + survivor0Size.longValue()
					+ survivor1Size.longValue();
	}

	public long getNewGenCurSize() {
		if (newGenCurSize != null)
			return newGenCurSize.longValue();
		else
			return edenSize.longValue() + survivor0Size.longValue()
					+ survivor1Size.longValue();
	}

	public String getLastGCCause() {
		if (lastGCCause == null)
			return null;
		else
			return lastGCCause.stringValue();
	}

	public String getCurrentGCCause() {
		if (currentGCCause == null)
			return null;
		else
			return currentGCCause.stringValue();
	}
	
//	public long getClassCount() {
//		return (getEdenGCTime() + getTenuredGCTime());
//	}
		
	public long getStartTime() {
		if (processStartDate == null) {	
			long elapsed = getOsElapsedTime();
			long frequency = getOsFrequency();
			long startTime = System.currentTimeMillis() - (elapsed * 1000 / frequency);
			processStartDate = new Date(startTime);
		}
		return processStartDate.getTime();
	}
		
	public long getTotGCTime() {
		return getMemoryGCNewTime() + getMemoryGCFullTime();
	}	
	

	public long getEdenMemoryUsed() {
		return edenUsed.longValue();
	}

	public long getTenuredSpaceMemoryUsed() {
		return tenuredUsed.longValue();
	}

	public long getSurvivor0Used() {
		return survivor0Used.longValue();
	}

	public long getSurvivor1Used() {
		return survivor1Used.longValue();
	}

	public long getPermGenMemoryUsed() {
		return permUsed.longValue();
	}

	public long getEdenMemoryMax() {
		return edenSize.longValue();
	}

	public long getSurvivor0Size() {
		return survivor0Size.longValue();
	}

	public long getSurvivor1Size() {
		return survivor1Size.longValue();
	}

	public long getTenuredSpaceMemoryMax() {
		return tenuredSize.longValue();
	}

	public long getPermGenMemoryMax() {
		return permSize.longValue();
	}

	public long getEdenMemoryCommitted() {
		return edenCapacity.longValue();
	}

	public long getSurvivor0Capacity() {
		return survivor0Capacity.longValue();
	}

	public long getSurvivor1Capacity() {
		return survivor1Capacity.longValue();
	}

	public long getTenuredSpaceMemoryCommitted() {
		return tenuredCapacity.longValue();
	}

	public long getPermGenMemoryCommitted() {
		return permCapacity.longValue();
	}

	public long getMemoryGCNewCount() {
		return edenGCEvents.longValue();
	}

	public long getMemoryGCFullCount() {
		return tenuredGCEvents.longValue();
	}

	public long getMemoryGCNewTime() {
		return edenGCTime.longValue();
	}

	public long getMemoryGCFullTime() {
		return tenuredGCTime.longValue();
	}

	public long getTenuringThreshold() {
		if (tenuringThreshold == null)
			return 0L;
		else
			return tenuringThreshold.longValue();
	}

	public long getMaxTenuringThreshold() {
		if (maxTenuringThreshold == null)
			return 0L;
		else
			return maxTenuringThreshold.longValue();
	}

	public long getDesiredSurvivorSize() {
		if (desiredSurvivorSize == null)
			return 0L;
		else
			return desiredSurvivorSize.longValue();
	}

	public long getAgeTableSize() {
		if (ageTableSize == null)
			return 0L;
		else
			return ageTableSize.longValue();
	}

	public long[] getAgeTableSizes() {
		if (ageTableSize == null)
			return null;
		long al[] = new long[ageTableSizes.length];
		for (int i = 0; i < ageTableSizes.length; i++)
			al[i] = ageTableSizes[i].longValue();

		return al;
	}

	public void getAgeTableSizes(long al[]) {
		if (ageTableSize == null)
			return;
		for (int i = 0; i < ageTableSizes.length; i++)
			al[i] = ageTableSizes[i].longValue();

	}

	public long getClassLoadTime() {
		return classLoadTime.longValue();
	}

	public long getCurrentClassesLoaded() {
		return classesLoaded.longValue();
	}

	public long getTotalClassesUnloaded() {
		return classesUnloaded.longValue();
	}

	public long getClassBytesLoaded() {
		return classBytesLoaded.longValue();
	}

	public long getClassBytesUnloaded() {
		return classBytesUnloaded.longValue();
	}

	public long getTotalCompilationTime() {
		return totalCompileTime.longValue();
	}

	public long getTotalCompile() {
		return totalCompile.longValue();
	}

	public boolean isFinalizerInitialized() {
		return finalizerInitialized;
	}

	public void initializeFinalizer() {
		initialize_finalizer();
	}

	public long getFinalizerTime() {
		if(finalizerInitialized && finalizerTime == null)
			try {
             finalizerTime = (LongMonitor)vm.findByName("sun.gc.finalizer.time");
			}
         	catch(MonitorException monitorexception) { }
         if(finalizerTime == null)
        	 return 0L;
         return finalizerTime.longValue();
	}

	public long getFinalizerCount() {
	     //MonitoredVmModel monitoredvmmodel = this;
	     //JVM INSTR monitorenter ;
	     if(finalizerInitialized && finalizerCount == null)
	         try
	         {
	             finalizerCount = (LongMonitor)vm.findByName("sun.gc.finalizer.objects");
	         }
	         catch(MonitorException monitorexception) { }
	     if(finalizerCount == null)
	         return 0L;
	     return finalizerCount.longValue();
	     //monitoredvmmodel;
	     //JVM INSTR monitorexit ;
	}

	public long getFinalizerQLength() {
		if (finalizerQLength == null)
			return 0L;
		else
			return finalizerQLength.longValue();
	}

	public long getFinalizerQMaxLength() {
		if (finalizerQMaxLength == null)
			return 0L;
		else
			return finalizerQMaxLength.longValue();
	}

	public long getOsElapsedTime() {
		return osElapsedTime.longValue();
	}

	public long getOsFrequency() {
		return osFrequency.longValue();
	}

	public String getJavaCommand() {
		if (javaCommand == null)
			return null;
		else
			return javaCommand.stringValue();
	}

	public String getJavaHome() {
		if (javaHome == null)
			return null;
		else
			return javaHome.stringValue();
	}

	public String getVmArgs() {
		if (vmArgs == null)
			return null;
		else
			return vmArgs.stringValue();
	}

	public String getVmFlags() {
		if (vmFlags == null)
			return null;
		else
			return vmFlags.stringValue();
	}

	public String getClassPath() {
		if (classPath == null)
			return null;
		else
			return classPath.stringValue();
	}

	public String getEndorsedDirs() {
		if (endorsedDirs == null)
			return null;
		else
			return endorsedDirs.stringValue();
	}

	public String getExtDirs() {
		if (extDirs == null)
			return null;
		else
			return extDirs.stringValue();
	}

	public String getLibraryPath() {
		if (libraryPath == null)
			return null;
		else
			return libraryPath.stringValue();
	}

	public String getBootClassPath() {
		if (bootClassPath == null)
			return null;
		else
			return bootClassPath.stringValue();
	}

	public String getBootLibraryPath() {
		if (bootLibraryPath == null)
			return null;
		else
			return bootLibraryPath.stringValue();
	}

	public String getVmInfo() {
		if (vmInfo == null)
			return null;
		else
			return vmInfo.stringValue();
	}

	public String getVmName() {
		if (vmName == null)
			return null;
		else
			return vmName.stringValue();
	}

	public String getVmVersion() {
		if (vmVersion == null)
			return null;
		else
			return vmVersion.stringValue();
	}

	public String getVmVendor() {
		if (vmVendor == null)
			return null;
		else
			return vmVendor.stringValue();
	}

	public String getVmSpecName() {
		if (vmSpecName == null)
			return null;
		else
			return vmSpecName.stringValue();
	}

	public String getVmSpecVersion() {
		if (vmSpecVersion == null)
			return null;
		else
			return vmSpecVersion.stringValue();
	}

	public String getVmSpecVendor() {
		if (vmSpecVendor == null)
			return null;
		else
			return vmSpecVendor.stringValue();
	}

	public long getLastModificationTime() {
		return lastModificationTime.longValue();
	}
	
	public String getEdenCollectorName() {
		if (this.collector0name != null)
			return this.collector0name.stringValue();
		else
			return null;
	}

	public String getTenuredCollectorName() {
		if (this.collector1name != null)
			return this.collector1name.stringValue();
		else
			return null;
	}
	
	public long getSurvivorSpaceMemoryCommitted() {
		return (getSurvivor0Size() + getSurvivor1Size());
	}
	
	public long getSurvivorSpaceMemoryMax() {
		return (getSurvivor0Capacity() + getSurvivor1Capacity());
	}
	
	public long getSurvivorSpaceMemoryUsed() {
		return (getSurvivor0Used() + getSurvivor1Used());
	}

	private LongMonitor newGenMinSize;
	private LongMonitor newGenMaxSize;
	private LongMonitor newGenCurSize;
	private LongMonitor edenSize;
	private LongMonitor edenCapacity;
	private LongMonitor edenUsed;
	private LongMonitor edenGCTime;
	private LongMonitor edenGCEvents;
	private LongMonitor survivor0Size;
	private LongMonitor survivor0Capacity;
	private LongMonitor survivor0Used;
	private LongMonitor survivor1Size;
	private LongMonitor survivor1Capacity;
	private LongMonitor survivor1Used;
	private LongMonitor tenuredSize;
	private LongMonitor tenuredCapacity;
	private LongMonitor tenuredUsed;
	private LongMonitor tenuredGCTime;
	private LongMonitor tenuredGCEvents;
	private LongMonitor permSize;
	private LongMonitor permCapacity;
	private LongMonitor permUsed;
	private LongMonitor tenuringThreshold;
	private LongMonitor maxTenuringThreshold;
	private LongMonitor desiredSurvivorSize;
	private LongMonitor ageTableSize;
	private LongMonitor ageTableSizes[];
	private StringMonitor lastGCCause;
	private StringMonitor currentGCCause;
	private StringMonitor collector0name;
	private StringMonitor collector1name;
	private boolean finalizerInitialized;
	private LongMonitor finalizerTime;
	private LongMonitor finalizerQLength;
	private LongMonitor finalizerQMaxLength;
	private LongMonitor finalizerCount;
	private LongMonitor classLoadTime;
	private LongMonitor classesLoaded;
	private LongMonitor classesUnloaded;
	private LongMonitor classBytesLoaded;
	private LongMonitor classBytesUnloaded;
	private LongMonitor totalCompileTime;
	private LongMonitor totalCompile;
	private LongMonitor osElapsedTime;
	private LongMonitor osFrequency;
	private StringMonitor javaCommand;
	private StringMonitor javaHome;
	private StringMonitor vmArgs;
	private StringMonitor vmFlags;
	private StringMonitor vmInfo;
	private StringMonitor vmName;
	private StringMonitor vmVersion;
	private StringMonitor vmVendor;
	private StringMonitor vmSpecName;
	private StringMonitor vmSpecVersion;
	private StringMonitor vmSpecVendor;
	private StringMonitor classPath;
	private StringMonitor bootClassPath;
	private StringMonitor libraryPath;
	private StringMonitor bootLibraryPath;
	private StringMonitor endorsedDirs;
	private StringMonitor extDirs;
	private LongMonitor lastModificationTime;
}
