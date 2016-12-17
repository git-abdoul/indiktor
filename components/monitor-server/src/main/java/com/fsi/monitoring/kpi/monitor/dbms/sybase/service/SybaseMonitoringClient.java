package com.fsi.monitoring.kpi.monitor.dbms.sybase.service;


import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;

import com.fsi.monitoring.kpi.monitor.dbms.DbmsMonitoringClient;

import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseContentionMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseDeviceMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseLogMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseMonitorConfMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseNetworkMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybasePartitionMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseStatementMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseTableMeasurement;

public class SybaseMonitoringClient extends DbmsMonitoringClient {
//	private static final Logger LOG = Logger.getLogger(SybaseMonitoringClient.class);
//	private static final String SQL_CONNECTION_ISSUE = "JZ006";

	public SybaseMonitoringClient(RdbmsConnector rdbmsConnector, String databaseInstance) {
		super(rdbmsConnector, databaseInstance);
	}
	
	public RdbmsConnectorDAO getMeasurement(String measurementsName) {
		return measurements.get(measurementsName);
	}	
	
//	private void handleError(SQLException e, AbstractDataSourceDAO measurement) {
//		if (measurement.getDataSource() != null && SQL_CONNECTION_ISSUE.equalsIgnoreCase(e.getSQLState())) {
//			System.err.println(e.getMessage());
//			LOG.error(e.getMessage(), e);
//			measurement.setDataSource(null);
//			this.setChanged();
//			this.notifyObservers(this);
//		}
//	}
	
//	public List<SybaseMonitorConfResult> getMonitorConf() {
//		SybaseMonitorConfMeasurement measurement = (SybaseMonitorConfMeasurement)measurements.get("DBMS_SYBASE_MONITORCONF");
//		try {
//			if (measurement.getDataSource() != null)
//				return measurement.getMonitorConfResult();
//		}
//		catch (SQLException e) {			
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseDataCacheResult> getDataCache() {
//		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)measurements.get("DBMS_SYBASE_CACHE_DATA");
//		try {
//			return measurement.getDataCacheResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseCachedObjectResult> getCachedObject() {
//		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)measurements.get("DBMS_SYBASE_CACHE_OBJECT");
//		try {
//			return measurement.getCachedObjectResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseCachePoolResult> getCachePool() {
//		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)measurements.get("DBMS_SYBASE_CACHE_POOL");
//		try {
//			return measurement.getCachePoolResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public SybaseProcedureCacheResult getProcedureCache() {
//		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)measurements.get("DBMS_SYBASE_CACHE_PROCEDURE");
//		try {
//			return measurement.getProcedureCacheResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public int getDeadLock() {
//		SybaseContentionMeasurement measurement = (SybaseContentionMeasurement)measurements.get("DBMS_SYBASE_CONTENTION");
//		try {
//			return measurement.getDeadLockNumber();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return -1;
//	}
	
//	public List<SybaseDeviceIOResult> getDeviceIO() {
//		SybaseDeviceMeasurement measurement = (SybaseDeviceMeasurement)measurements.get("DBMS_SYBASE_DEVICE");
//		try {
//			return measurement.getDeviceIOResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseEngineResult> getEngine() {
//		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)measurements.get("DBMS_SYBASE_ENGINE");
//		try {
//			return measurement.getEngineResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseEngineCpuTimeResult> getEngineCpuTime() {
//		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)measurements.get("DBMS_SYBASE_ENGINE");
//		try {
//			return measurement.getEngineCpuTimeResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseEngineDiskIOResult> getEngineDiskIO() {
//		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)measurements.get("DBMS_SYBASE_ENGINE");
//		try {
//			return measurement.getEngineDiskIOResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseEngineHkGcResult> getEngineHkGc() {
//		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)measurements.get("DBMS_SYBASE_ENGINE");
//		try {
//			return measurement.getEngineHkGcResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public Map<String, String> getLogMessage() {
//		SybaseLogMeasurement measurement = (SybaseLogMeasurement)measurements.get("DBMS_SYBASE_LOG_MESSAGE");
//		try {
//			return measurement.getLogMessageResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public Map<String, Integer> getLogSeverity() {
//		SybaseLogMeasurement measurement = (SybaseLogMeasurement)measurements.get("DBMS_SYBASE_LOG_SEVERITY");
//		try {
//			return measurement.getLogSeverityResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public SybaseNetworkResult getNetwork() {
//		SybaseNetworkMeasurement measurement = (SybaseNetworkMeasurement)measurements.get("DBMS_SYBASE_NETWORK");
//		try {
//			return measurement.getNetworkResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybasePartitionResult> getPartition() {
//		SybasePartitionMeasurement measurement = (SybasePartitionMeasurement)measurements.get("DBMS_SYBASE_PARTITION");
//		try {
//			return measurement.getPartitionResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseTableResult> getTable() {
//		SybaseTableMeasurement measurement = (SybaseTableMeasurement)measurements.get("DBMS_SYBASE_TABLE");
//		try {
//			return measurement.getTableResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseProcessResult> getProcess() {
//		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)measurements.get("DBMS_SYBASE_PROCESS");
//		try {
//			return measurement.getProcessResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseProcessNetworkResult> getProcessNetwork(){
//		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)measurements.get("DBMS_SYBASE_PROCESS");
//		try {
//			return measurement.getProcessNetworkResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseProcessContentionResult> getProcessContention(){
//		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)measurements.get("DBMS_SYBASE_PROCESS");
//		try {
//			return measurement.getProcessContentionResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseProcessTransactionResult> getProcessTransaction() {
//		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)measurements.get("DBMS_SYBASE_PROCESS");
//		try {
//			return measurement.getProcessTransactionResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseProcessULCResult> getProcessULC() {
//		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)measurements.get("DBMS_SYBASE_PROCESS");
//		try {
//			return measurement.getProcessULCResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseProcessThreadResult> getProcessThread() {
//		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)measurements.get("DBMS_SYBASE_PROCESS");
//		try {
//			return measurement.getProcessThreadResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseProcessIOResult> getProcessIO() {
//		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)measurements.get("DBMS_SYBASE_PROCESS");
//		try {
//			return measurement.getProcessIOResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}
	
//	public List<SybaseStatementResult> getStatement() {
//		SybaseStatementMeasurement measurement = (SybaseStatementMeasurement)measurements.get("DBMS_SYBASE_STATEMENT");
//		try {
//			return measurement.getStatementResult();
//		} catch (SQLException e) {
//			handleError(e, measurement);
//		}
//		return null;
//	}

	@Override
	public void initMeasurements(RdbmsConnector rdbmsConnector) {
		measurements.put("DBMS_SYBASE_CACHE_DATA", new SybaseCacheMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_CACHE_POOL", new SybaseCacheMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_CACHE_PROCEDURE", new SybaseCacheMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_CACHE_OBJECT", new SybaseCacheMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_CONTENTION", new SybaseContentionMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_DEVICE", new SybaseDeviceMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_ENGINE", new SybaseEngineMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_LOG_MESSAGE", new SybaseLogMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_LOG_SEVERITY", new SybaseLogMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_MONITORCONF", new SybaseMonitorConfMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_NETWORK", new SybaseNetworkMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_PARTITION", new SybasePartitionMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_PROCESS", new SybaseProcessMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_STATEMENT", new SybaseStatementMeasurement(rdbmsConnector));
		measurements.put("DBMS_SYBASE_TABLE", new SybaseTableMeasurement(rdbmsConnector));		
	}
	
}
