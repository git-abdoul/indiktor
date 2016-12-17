package com.fsi.monitoring.kpi.monitor.process.resourceData;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.HostProcessInfo;

public class SystemProcessResourceData extends IkrResourceData {	
	private List<HostProcessInfo> infos;
	
	public SystemProcessResourceData(List<HostProcessInfo> infos,
			   				  		Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}
	
	public Map<String, String> getResident() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessMemory().getResident()));
			}
		}
		return values;
	}
	
	public Map<String, String> getShare() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessMemory().getShare()));
			}
		}
		return values;
	}
	
	public Map<String, String> getVirtual() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessMemory().getSize()));
			}
		}
		return values;
	}	
	
	public Map<String, String> getCpuPercent() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				double cpu = (info.getProcessCpu().getCpuPercent()>1)?1:info.getProcessCpu().getCpuPercent();
				values.put(info.getName(),String.valueOf((cpu>0)?cpu:0));
			}
		}
		return values;
	}
	
	public Map<String, String> getStartTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessTime().getStartTime()));
			}
		}
		return values;
	}
	
	public Map<String, String> getSystem() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessTime().getSys()*1000));
			}
		}
		return values;
	}
	
	public Map<String, String> getUser() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessTime().getUser()*1000));
			}
		}
		return values;
	}
	
	public Map<String, String> getTotalCpuTime() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessTime().getTotal()*1000));
			}
		}
		return values;
	}
	
	public Map<String, String> getTotalFileDesc() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getTotal()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMajor() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessMemory().getMajorFaults()));
			}
		}
		return values;
	}
	
	public Map<String, String> getMinor() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessMemory().getMinorFaults()));
			}
		}
		return values;
	}
	
	public Map<String, String> getTotalPage() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessMemory().getPageFaults()));
			}
		}
		return values;
	}
	
	public Map<String, String> getNice() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessState().getNice()));
			}
		}
		return values;
	}
	
	public Map<String, String> getPriority() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessState().getPriority()));
			}
		}
		return values;
	}
	
	public Map<String, String> getProcessor() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessState().getProcessor()));
			}
		}
		return values;
	}
	
	public Map<String, String> getThreads() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessState().getThreads()));
			}
		}
		return values;
	}
	
	public Map<String, String> getState() {
		Map<String, String> values = new HashMap<String, String>();
		if (infos!=null) {
			for (HostProcessInfo info : infos) {
				values.put(info.getName(),String.valueOf(info.getProcessState().getState()));
			}
		}
		return values;
	}
}
