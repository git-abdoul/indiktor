package com.fsi.monitoring.kpi.monitor.murex;

import java.util.Date;
import java.util.List;

import com.fsi.monitoring.kpi.monitor.IkrResourceData;
import com.fsi.monitoring.system.dto.murex.MurexInfo;

public class MurexInfoResourceData extends IkrResourceData {
	protected List<MurexInfo> infos;

	public MurexInfoResourceData(List<MurexInfo> infos, Date captureTime) {
		super(captureTime);
		this.infos = infos;
	}

}
