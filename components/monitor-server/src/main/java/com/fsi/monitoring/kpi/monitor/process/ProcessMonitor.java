/**
 * 
 */
package com.fsi.monitoring.kpi.monitor.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.SystemAgentConnectorConfig;
import com.fsi.monitoring.connector.systemAgent.SystemAgentConnector;
import com.fsi.monitoring.ikr.model.IkrCategoryResource;
import com.fsi.monitoring.ikr.model.MetricDomainConfigResource;
import com.fsi.monitoring.ikr.model.MetricDomainResource;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.process.resourceData.SystemProcessAvailabilityResourceData;
import com.fsi.monitoring.kpi.monitor.process.resourceData.SystemProcessResourceData;
import com.fsi.monitoring.system.dto.HostProcessInfo;
import com.fsi.monitoring.system.dto.HostProcessInfoList;
import com.fsi.monitoring.system.dto.SystemInfo;

/**
 * TODO : describe the class more extensively
 * 
 * Filtering : 
 * 3 parameters can be used to specify which process should be selected
 * We now call a filter a string that should be contained in another string
 * 
 * - processFilters : colon separated list of the process filters
 * - jarFilters : colon separated list of jar filters 
 * - classFilters : colon separated list of classname filters (package names also work)
 */
public class ProcessMonitor extends MonitorTask {
	private static final Logger LOG = Logger.getLogger(ProcessMonitor.class);
	
	public static final String JAVA_PROCESS = "java";	
	
	protected Set<String> existingProcesses;
	
	protected String[] classNameFilters;
	protected String[] jarNameFilters;
	protected String[] processNameFilters;
	protected String[] argumentFilters;
	
	protected String[] defaultFilter;
	
	protected SystemAgentConnector systemAgentConnector;
	
	private List<HostProcessInfo> processes;

	/**
	 * @param id
	 * @param name
	 * @param type
	 * @throws PersistenceException
	 */
	public ProcessMonitor() throws PersistenceException {
		defaultFilter = new String[1];
		defaultFilter[0] = ALL_WILDCARD;
		classNameFilters = defaultFilter;
		jarNameFilters = defaultFilter;
		processNameFilters = defaultFilter;	
		argumentFilters = defaultFilter;		
		existingProcesses = new HashSet<String>();
	}
	
	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {
		systemAgentConnector = (SystemAgentConnector)getConnector(SystemAgentConnectorConfig.TYPE);		
		
		// fetch processes which has been already alive. 
		try {
			List<MetricDomainConfigResource> domainResources = monitorConfig.getMetricDomainConfig().getResources();
			MetricDomainResource domainResource = null;
			for (MetricDomainConfigResource resource : domainResources) {
				if ("SYSTEM_PROCESS_AVAILABILITY".equals(resource.getResource().getResourceName())) {
					domainResource = resource.getResource();
					break;
				}
			}
			if (domainResource != null) {
				Map<String, IkrCategoryResource> categoryResources = PMFactory.getDataModelPM().getIkrCategoryResources(domainResource.getId());
				int availabilityCategoryId = categoryResources.get("up").getIkrStaticDomainId();
				Map<Long, AbstractIkrDefinition> ikrDefs = PMFactory.getMonitoringPM().getIkrDefinitions(getId(), availabilityCategoryId);
				for(AbstractIkrDefinition def : ikrDefs.values()) {
					existingProcesses.add(def.getIkrInstance());
				}
			}
		} catch (PersistenceException e) {
			LOG.error("Process Monitor :: Unable to get existing processes");
			LOG.error(e.getMessage(), e);
		}			
		processes = fetchProcesses();
	}
	
	@Override
	protected void postFetchs() throws Exception {}
		
	protected List<HostProcessInfo> fetchProcesses() 
	throws ConnectorException {
		List<HostProcessInfo> processes = new ArrayList<HostProcessInfo>();
		
		SystemInfo siInfo = systemAgentConnector.updateInfo("SYSTEM_PROCESS", null);
		
		if (siInfo != null) {
			HostProcessInfoList info = (HostProcessInfoList)siInfo;
	
			List<HostProcessInfo> processList = (info != null) ? info.getList() : new ArrayList<HostProcessInfo>();
			for (HostProcessInfo process : processList) {
				String processName = process.getName();
				if (process.getName() == null || process.getName().length()==0)
					continue;
				processName = processName.toLowerCase();
				if(!accepts(processName, processNameFilters))
					continue;			
				if(isJavaProcess(processName)){
					String[] processArgs = process.getArguments();
					//first try to get the jar name 
					String classname = process.getJavaClassName();
					String jarName = findJarName(processArgs);
					if(jarName==null){
						//no jar found, we will try to find a class							
						if (classname != null) {
							if (!accepts(classname, classNameFilters))
								continue;
							else
								processName = classname;
						} 
						else {
							continue;
						}
					}else {
						// jar found, we need to determine whether we accept it
						if(!accepts(jarName, jarNameFilters)){
							//jar not accepted so we discard that process
							continue;
						}else{
							//jar accepted, the process name becomes the jar name
							processName = (classname != null) ? classname : jarName;
						}
					}	
					
					if(!accepts(process.getJavaArgs(), argumentFilters))
						continue;
					
					int idx = processName.lastIndexOf("");
					if(idx > 0)
						processName = processName.substring(idx+1);
				}
				else {
					if(!accepts(process.getArguments(), argumentFilters))
						continue;
					
					int idx = processName.lastIndexOf("\\");
					if(idx > 0) {
						processName = processName.substring(idx+1);
						idx = processName.lastIndexOf("");
						if(idx > 0)
							processName = processName.substring(0,idx);
						
						if (processName.contains("oracle"))
							processName = processName + "_" + process.getArguments()[1];
					}				
				}
				process.setName(processName);
				processes.add(process);
			}		
		}
		return processes;
	}	
	
	private boolean isJavaProcess(String processName){
		return processName.contains(JAVA_PROCESS);
	}	
	
	/**
	 * 
	 * @return null if not found otherwise returns the jar name
	 */
	private String findJarName(String[] processArgs){
		String jarName=null;
		boolean jarArgFound = false;
		int argInd =0;
		// try to find param jar
		while (!jarArgFound && argInd < processArgs.length) {
			String arg = processArgs[argInd];
			if (arg.equalsIgnoreCase("-jar") && ((argInd+1)<processArgs.length) && processArgs[argInd+1].endsWith(".jar")) {
				jarArgFound=true;
				jarName = processArgs[argInd+1];								
			}
			argInd++;
		}
		return jarName;
	}
	
	public SystemProcessResourceData fetchSYSTEM_PROCESS_STATS()
	throws ConnectorException {
		return new SystemProcessResourceData(processes, new Date());
	}
	
	public SystemProcessAvailabilityResourceData fetchSYSTEM_PROCESS_AVAILABILITY()
	throws ConnectorException {		
		Set<String> lives = new HashSet<String>();		
		Map<String, Integer> avails = new HashMap<String, Integer>();
		
		// Processes UP
		List<HostProcessInfo> processesAlive = fetchProcesses();		
		for(HostProcessInfo info : processesAlive) {
			String instanceRA = info.getName();
			lives.add(instanceRA);
			existingProcesses.add(instanceRA);
			avails.put(instanceRA, 1);
		}
		
		// Processes DOWN
		for(String instanceRA : existingProcesses) {
			if (!lives.contains(instanceRA)) {
				avails.put(instanceRA, 0);
			}
		}
		
		return new SystemProcessAvailabilityResourceData(avails, new Date());
	}
	
	public void init() throws Exception {
		super.init();
		String procFilter = monitorConfig.getAttributes().get("PROCESS_NAME_FILTERS");
		processNameFilters = (procFilter!=null&&procFilter.length()>0)?procFilter.split(":"):defaultFilter;
		
		String jarFilter = monitorConfig.getAttributes().get("JAR_NAME_FILTERS");
		jarNameFilters = (jarFilter!=null&&jarFilter.length()>0)?jarFilter.split(":"):defaultFilter;
		
		String javaFilter = monitorConfig.getAttributes().get("CLASS_NAME_FILTERS");
		classNameFilters = (javaFilter!=null&&javaFilter.length()>0)?javaFilter.split(":"):defaultFilter;
		
		String argFilter = monitorConfig.getAttributes().get("ARGUMENT_FILTERS");
		argumentFilters = (argFilter!=null&&argFilter.length()>0)?argFilter.split(":"):defaultFilter;
	}
	
	@Override
	protected void initConnection() throws Exception {}	
}
