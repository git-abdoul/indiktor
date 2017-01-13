/**
 * 
 */
package com.fsi.monitoring.kpi.monitor.jmx;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.jmx.resourceData.JmxThreadInfoResourceData;
import com.fsi.monitoring.kpi.monitor.jmx.resourceData.JmxThreadStatusResourceData;
import com.fsi.monitoring.kpi.monitor.jmx.resourceData.JmxThreadTypeStatsResourceData;

/**
 * @author Maltem
 * 
 */
public class JmxThreadMonitor extends JmxMonitorTask {
	private ThreadMXBean threadMXBean = null;	

	@Override
	protected void preFetchs() throws Exception {
		super.preFetchs();
		threadMXBean = jmxConnector.getThreadMXBean();
	}

	public JmxThreadTypeStatsResourceData fetchTHREAD_STATS() 
	throws ConnectorException {
		return new JmxThreadTypeStatsResourceData(threadMXBean, jmxConnector.getProcessName(), new Date());
	}


	public JmxThreadStatusResourceData fetchTHREAD_STATUS()
	throws ConnectorException {		
		List<ThreadInfo> infos = getAllThreads();
		ThreadMonitorInfo globalInfo = new ThreadMonitorInfo();
		for (ThreadInfo info : infos) {
			String state = info.getThreadState().name();
			// Update global Info
			updateThreadMonitorInfo(state, globalInfo);
			if (info.isSuspended()) {
				updateThreadMonitorInfo("SUSPENDED", globalInfo);
			}
		}
		
		// DEADLOCK Info
		long[] tmp = threadMXBean.findMonitorDeadlockedThreads();
		long[] deadlocks = (tmp == null || tmp.length == 0) ? new long[0] : tmp;
		globalInfo.deadlock = deadlocks.length;
		
		return new JmxThreadStatusResourceData(getStats(globalInfo, null), jmxConnector.getProcessName(), new Date());
	}

	public JmxThreadStatusResourceData fetchTHREAD_TYPE()
	throws ConnectorException {		
		Map<String, ThreadMonitorInfo> stateMap = new HashMap<String, ThreadMonitorInfo>();
		long[] tmp = threadMXBean.findMonitorDeadlockedThreads();
		long[] deadlocks = (tmp == null || tmp.length == 0) ? new long[0] : tmp;
		List<ThreadInfo> infos = getAllThreads();
		for (ThreadInfo info : infos) {
			String state = info.getThreadState().name();

			// Update info for each thread type
			String threadName = info.getThreadName();
			int pos = threadName.indexOf("-");
			String threadType = (pos > 0) ? threadName.substring(0, pos)
					: threadName;
			pos = threadType.lastIndexOf("(");
			threadType = (pos > 0) ? threadName.substring(0, pos) : threadType;

			// check Calypso Specificity
			threadType = updateForCalypso(threadType);
			if (stateMap.containsKey(threadType))
				updateThreadMonitorInfo(state, stateMap.get(threadType));
			else
				stateMap.put(threadType, updateThreadMonitorInfo(state,new ThreadMonitorInfo()));

			if (info.isSuspended())
				updateThreadMonitorInfo("SUSPENDED", stateMap.get(threadType));
			
			if (isInvolvedInDeadlock(info, deadlocks))
				updateThreadMonitorInfo("DEADLOCK", stateMap.get(threadType));
		}
		
		Map<String, Double> threadStats = new HashMap<String, Double>();		
		for (String key : stateMap.keySet()) {
			threadStats.putAll(getStats(stateMap.get(key), key));
		}
		
		return new JmxThreadStatusResourceData(threadStats, jmxConnector.getProcessName(), new Date());
	}
	
	private Map<String, Double> getStats(ThreadMonitorInfo threadInfo, String type) {
		Map<String, Double> threadStats = new HashMap<String, Double>();
		
		// DEADLOCK
		if (threadInfo.getDeadlock() > 0)  {
			String name = (type!=null&&type.length()>0)?type+",DEADLOCK":"DEADLOCK";
			threadStats.put(name, threadInfo.getDeadlock());
		}
			
		// RUNNABLE Info
		if (threadInfo.getRunnable() > 0){
			String name = (type!=null&&type.length()>0)?type+",RUNNABLE":"RUNNABLE";
			threadStats.put(name, threadInfo.getRunnable());
		}
		
		// NEW Info
		if (threadInfo.getNewThread() > 0) {
			String name = (type!=null&&type.length()>0)?type+",NEW":"NEW";
			threadStats.put(name, threadInfo.getNewThread());
		}
		
		// TERMINATED Info
		if (threadInfo.getTerminated() > 0) {
			String name = (type!=null&&type.length()>0)?type+",TERMINATED":"TERMINATED";
			threadStats.put(name, threadInfo.getTerminated());
		}
		
		// WAITING Info
		if (threadInfo.getWaiting() > 0) {
			String name = (type!=null&&type.length()>0)?type+",WAITING":"WAITING";
			threadStats.put(name, threadInfo.getWaiting());
		}
		
		// TIMED_WAITING Info
		if (threadInfo.getTimeWaited() > 0){
			String name = (type!=null&&type.length()>0)?type+",TIMED_WAITING":"TIMED_WAITING";
			threadStats.put(name, threadInfo.getTimeWaited());
		}
		
		// SUSPENDED Info
		if (threadInfo.getSuspended() > 0){
			String name = (type!=null&&type.length()>0)?type+",SUSPENDED":"SUSPENDED";
			threadStats.put(name, threadInfo.getSuspended());
		}
		
		// BLOCKED Info
		if (threadInfo.getBlocked() > 0){
			String name = (type!=null&&type.length()>0)?type+",BLOCKED":"BLOCKED";
			threadStats.put(name, threadInfo.getBlocked());
		}
		
		return threadStats;
	}

	private String updateForCalypso(String threadType) {
		String type = threadType;
		if (threadType.contains("EngineThread"))
			type = "EngineThread";
		return type;
	}

	private ThreadMonitorInfo updateThreadMonitorInfo(String state,
			ThreadMonitorInfo info) {
		if (Thread.State.BLOCKED.name().equalsIgnoreCase(state)) {
			info.blocked++;
		} else if (Thread.State.RUNNABLE.name().equalsIgnoreCase(state)) {
			info.runnable++;
		} else if (Thread.State.WAITING.name().equalsIgnoreCase(state)) {
			info.waiting++;
		} else if (Thread.State.TIMED_WAITING.name().equalsIgnoreCase(state)) {
			info.timeWaited++;
		} else if (Thread.State.TERMINATED.name().equalsIgnoreCase(state)) {
			info.terminated++;
		} else if (Thread.State.NEW.name().equalsIgnoreCase(state)) {
			info.newThread++;
		} else if ("SUSPENDED".equalsIgnoreCase(state)) {
			info.suspended++;
		} else if ("DEADLOCK".equalsIgnoreCase(state)) {
			info.deadlock++;
		}
		return info;
	}

	public JmxThreadStatusResourceData fetchTHREAD_METHOD()
	throws ConnectorException {		
		Map<String, Double> methodMap = new HashMap<String, Double>();
		List<ThreadInfo> infos = getAllThreads();
		for (ThreadInfo info : infos) {
			StackTraceElement[] traces = info.getStackTrace();
			String method = "NO JAVA STACK";
			if (traces != null && traces.length > 0) {
				int line = traces[0].getLineNumber();
				String filename = (line > 0) ? (traces[0].getFileName() + ":" + line)
						: "Native Method";
				method = traces[0].getClassName() + ""
						+ traces[0].getMethodName() + "(" + filename + ")";
			}
			double current = 1;
			if (methodMap.containsKey(method))
				current = methodMap.get(method) + 1;
			methodMap.put(method, current);
		}
		
		return new JmxThreadStatusResourceData(methodMap, jmxConnector.getProcessName(), new Date());
	}

//	public JmxThreadInfoResourceData fetchTHREAD_INFO()
//	throws ConnectorException {		
//		long[] tmp = threadMXBean.findMonitorDeadlockedThreads();
//		long[] deadlocks = (tmp == null || tmp.length == 0) ? new long[0] : tmp;
//		Map<String, List<ThreadInfo>> threadMap = new HashMap<String, List<ThreadInfo>>();
//		List<ThreadInfo> infos = getAllThreads();
//		for (ThreadInfo info : infos) {
//			String threadName = info.getThreadName();
//			String[] nameItems = threadName.split("-");
//			if (nameItems.length > 1) {
//				int pos = nameItems[0].lastIndexOf("(");				
//				if(pos > 0) 
//					threadName = nameItems[0].substring(0, pos) + "-" + nameItems[1];
//				else if (nameItems[1].contains("[")) {
//					threadName = nameItems[0] + "-";
//					String[] items = nameItems[1].split(",");
//					for(String item : items) {
//						pos = item.indexOf("@");
//						threadName = threadName + ((pos>0)?item.substring(0, pos):item) + ",";
//					}					
//					threadName = threadName.substring(0, threadName.lastIndexOf(",")) + ((threadName.contains("]"))?"":"]");
//				}
//			} 
//			
//			threadName = fixServiceName(threadName);
//			
//			if (!threadMap.containsKey(threadName))
//				threadMap.put(threadName, new ArrayList<ThreadInfo>());
//			threadMap.get(threadName).add(info);
//		}
//		
//		Map<String, ThreadInfo> threadInfos = new HashMap<String, ThreadInfo>();
//		for (String threadName : threadMap.keySet()) {
//			List<ThreadInfo> threads = threadMap.get(threadName);
//			if (threads.size() > 1) {
//				int i = 0;
//				for (ThreadInfo info : threads) {
//					threadInfos.put(threadName + "(" + i + ")", info);
//					i++;
//				}
//			} else {
//				ThreadInfo info = threads.get(0);
//				threadInfos.put(threadName, info);
//			}
//		}
//		return new JmxThreadInfoResourceData(threadInfos, deadlocks, jmxConnector.getProcessName(), new Date());
//	}
	
	public JmxThreadInfoResourceData fetchTHREAD_INFO()
	throws ConnectorException {		
		return new JmxThreadInfoResourceData(getAllThreads(), jmxConnector.getProcessName(), new Date());
	}
	
	private String fixServiceName(String name) {
		String res = name;
		if (name!= null && name.length()>0) {
			String elts[] = name.split(" ");
			if (elts.length>5) {
				if (elts[1].contains("com.calypso.tk.service"))
					res = elts[0] + " " + elts[1];
			}
		}
		return res;
	}
	
	private List<ThreadInfo> getAllThreads()
	throws ConnectorException {		
		ThreadMXBean threadMXBean = jmxConnector.getThreadMXBean();		
		
		List<ThreadInfo> threads = new ArrayList<ThreadInfo>();
		long[] ids = threadMXBean.getAllThreadIds();
		ThreadInfo[] infos = threadMXBean.getThreadInfo(ids, Integer.MAX_VALUE);
		for (ThreadInfo info : infos) {
			if (info != null)
				threads.add(info);
		}
		return threads;
	}	
	
	private boolean isInvolvedInDeadlock(ThreadInfo info, long[] deadlocks) {
		boolean isInvolvedInDeadlock = false;
		for (long id : deadlocks) {
			if (id == info.getThreadId()) {
				isInvolvedInDeadlock = true;
				break;
			}
		}
		return isInvolvedInDeadlock;
	}	
}
