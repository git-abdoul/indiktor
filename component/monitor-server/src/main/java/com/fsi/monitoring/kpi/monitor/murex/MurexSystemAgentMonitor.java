package com.fsi.monitoring.kpi.monitor.murex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fsi.monitoring.connector.systemAgent.SystemAgentCallback;
import com.fsi.monitoring.kpi.monitor.AbstractSystemAgentMonitor;
import com.fsi.monitoring.system.dto.SystemInfo;
import com.fsi.monitoring.system.dto.murex.MurexInfo;

public abstract class MurexSystemAgentMonitor extends AbstractSystemAgentMonitor implements SystemAgentCallback, Serializable {
	private static final long serialVersionUID = 960026552421641620L;

	protected List<MurexInfo> infos;
	
	@Override
	protected void preStart() {
		infos = Collections.synchronizedList(new ArrayList<MurexInfo>());		
		super.preStart();
	}
	
	public void onMessage(SystemInfo info) {
		if (info instanceof MurexInfo)
			infos.add((MurexInfo)info);					
	}
	
	@Override
	protected void postFetchs() throws Exception {
		infos.clear();
	}	

}
