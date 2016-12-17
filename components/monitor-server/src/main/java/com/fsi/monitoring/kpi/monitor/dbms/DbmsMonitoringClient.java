package com.fsi.monitoring.kpi.monitor.dbms;

import java.util.HashMap;
import java.util.Observable;

import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;

public abstract class DbmsMonitoringClient extends Observable{
	protected HashMap<String, RdbmsConnectorDAO> measurements;
	
	private String databaseInstance;
	
	public DbmsMonitoringClient(RdbmsConnector rdbmsConnector,
								String databaseInstance) {
	    this.init(rdbmsConnector);
	}	
	
	private void init(RdbmsConnector rdbmsConnector) {
		this.measurements = new HashMap<String, RdbmsConnectorDAO>();
	    initMeasurements(rdbmsConnector);
	    databaseInstance = rdbmsConnector.getConnectorContext();
	}
	
	public String getDatabaseInstance(){
		return databaseInstance;
	}
	
	public abstract void initMeasurements(RdbmsConnector rdbmsConnector);
}
