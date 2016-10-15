package com.fsi.monitoring.connector.sysload;



import java.util.Collection;
import java.util.Date;

import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.http.HttpConnector;
import com.fsi.monitoring.connector.sysload.SysloadConnectorImpl.SysloadData;

public interface SysloadConnector 
extends HttpConnector {
	
	Collection<SysloadData> getSysloadMetric(String metric, Date fromDate, Date endDate) throws ConnectorException;
        
}
