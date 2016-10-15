package com.fsi.monitoring.kpi.monitor.log;

import java.util.Date;
import java.util.List;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.logAnalysis.LogInfo;

public abstract class AbstractGenericLogInfoResourceData extends IkrResourceData {
	protected List<LogInfo> infos;

	public AbstractGenericLogInfoResourceData(List<LogInfo> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}

}
