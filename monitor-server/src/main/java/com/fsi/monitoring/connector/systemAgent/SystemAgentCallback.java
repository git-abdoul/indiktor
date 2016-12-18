package com.fsi.monitoring.connector.systemAgent;

import com.fsi.monitoring.system.dto.SystemInfo;

public interface SystemAgentCallback {
	public void onMessage(SystemInfo info);
}
