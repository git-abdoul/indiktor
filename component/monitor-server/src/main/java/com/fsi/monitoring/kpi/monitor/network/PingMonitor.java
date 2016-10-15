/**
 * 
 */
package com.fsi.monitoring.kpi.monitor.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.network.resourceData.PingResourceData;
import com.fsi.monitoring.kpi.monitor.network.service.PingService;
import com.fsi.monitoring.kpi.monitor.network.service.PingService.PingResult;

/**
 * @author Maltem
 *
 */
public class PingMonitor extends MonitorTask {
	private String[] hostnames;	
	
	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {}

	@Override
	protected void postFetchs() throws Exception {}

	public PingResourceData fetchPING_20() {
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		List<PingResult> results = new PingService().ping(hostnames, 5, 16384);
		return new PingResourceData(results, "ping20", new Date());
	}
	
	public PingResourceData fetchPING_64() {
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		List<PingResult> results = new PingService().ping(hostnames, 5, 64);
		return new PingResourceData(results, "ping64", new Date());
	}
	
	public void init() throws Exception {
		super.init();
		String info1 = monitorConfig.getAttributes().get("HOSTNAMES");
		hostnames = (info1==null || info1.length()==0) ? new String[0] : info1.split(":");
	}

	@Override
	protected void initConnection() throws Exception {}
}
