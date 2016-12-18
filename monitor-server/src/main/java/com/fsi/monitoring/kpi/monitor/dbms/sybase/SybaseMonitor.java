/**
 * 
 */
package com.fsi.monitoring.kpi.monitor.dbms.sybase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.IkrInstanceData;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseCachePoolIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseCachedObjectIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseContentionIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseDataCacheIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseDeviceIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseEngineCpuTimeIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseEngineDiskIOIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseEngineHkGcIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseEngineIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseLogsErrorsIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseLogsSeverityIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseMonitorConfIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseNetworkIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybasePartitionIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcedureCacheIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcessContentionIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcessIOIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcessIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcessNetworkIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcessThreadIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcessTransactionIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseProcessULCIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseStatementIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.instanceData.SybaseTableIkrInstanceData;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement.SybaseCachePoolResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement.SybaseCachedObjectResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseCacheMeasurement.SybaseDataCacheResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseContentionMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseDeviceMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseDeviceMeasurement.SybaseDeviceIOResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineCpuTimeResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineDiskIOResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineHkGcResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseEngineMeasurement.SybaseEngineResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseLogMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseMonitorConfMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseMonitorConfMeasurement.SybaseMonitorConfResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseNetworkMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybasePartitionMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybasePartitionMeasurement.SybasePartitionResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessContentionResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessIOResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessNetworkResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessThreadResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessTransactionResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseProcessMeasurement.SybaseProcessULCResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseStatementMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseStatementMeasurement.SybaseStatementResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseTableMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.measurement.SybaseTableMeasurement.SybaseTableResult;
import com.fsi.monitoring.kpi.monitor.dbms.sybase.service.SybaseMonitoringClient;

/**
 * @author Maltem
 *
 */
public class SybaseMonitor extends MonitorTask {
	
	private SybaseMonitoringClient client;
	
	private Date fetchDate;
	
	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {
		fetchDate = new Date();				
	}
	
	@Override
	protected void postFetchs() throws Exception {}


/** 							BEGIN   --- 	DBMS_SYBASE_CACHE                                       **/
	public List<IkrInstanceData> fetchDBMS_SYBASE_CACHE_PROCEDURE()
	throws ConnectorException, FetchException, PersistenceException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();	
		
		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)client.getMeasurement("DBMS_SYBASE_CACHE_PROCEDURE");
	
		res.add(new SybaseProcedureCacheIkrInstanceData(client.getDatabaseInstance(), 
														measurement.getProcedureCacheResult(), 
														fetchDate));
		return res;
	}
	
	public List<IkrInstanceData> fetchDBMS_SYBASE_CACHE_DATA()
	throws ConnectorException, FetchException, PersistenceException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)client.getMeasurement("DBMS_SYBASE_CACHE_DATA");	

		for(SybaseDataCacheResult info : measurement.getDataCacheResult())
			res.add(new SybaseDataCacheIkrInstanceData(client.getDatabaseInstance(), 
													   info, 
													   fetchDate));
		
		return res;
	}
	
	public List<IkrInstanceData> fetchDBMS_SYBASE_CACHE_OBJECT()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)client.getMeasurement("DBMS_SYBASE_CACHE_OBJECT");

		for(SybaseCachedObjectResult info : measurement.getCachedObjectResult())
			res.add(new SybaseCachedObjectIkrInstanceData(client.getDatabaseInstance(), info, fetchDate));
		
		return res;
	}
	
	public List<IkrInstanceData> fetchDBMS_SYBASE_CACHE_POOL()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseCacheMeasurement measurement = (SybaseCacheMeasurement)client.getMeasurement("DBMS_SYBASE_CACHE_POOL");

		for(SybaseCachePoolResult info : measurement.getCachePoolResult())
			res.add(new SybaseCachePoolIkrInstanceData(client.getDatabaseInstance(), info, fetchDate));
		
		return res;
	}
	
/** 							END   --- 	DBMS_SYBASE_CACHE                                       **/	


/** 							BEGIN   --- 	DBMS_SYBASE_CONTENTION                                       **/
	public List<IkrInstanceData> fetchDBMS_SYBASE_CONTENTION()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseContentionMeasurement measurement = (SybaseContentionMeasurement)client.getMeasurement("DBMS_SYBASE_CONTENTION");

		res.add(new SybaseContentionIkrInstanceData(client.getDatabaseInstance(), 
													measurement.getDeadLockNumber(), 
													fetchDate));
		return res;
	}

/** 							END   --- 	DBMS_SYBASE_CONTENTION                                       **/
	
/** 							BEGIN   --- 	DBMS_SYBASE_DEVICE                                       **/	
	public List<IkrInstanceData> fetchDBMS_SYBASE_DEVICE()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseDeviceMeasurement measurement = (SybaseDeviceMeasurement)client.getMeasurement("DBMS_SYBASE_DEVICE");

		for(SybaseDeviceIOResult info : measurement.getDeviceIOResult())
			res.add(new SybaseDeviceIkrInstanceData(client.getDatabaseInstance(), 
												    info, 
												    fetchDate));
		
		return res;
	}

/** 							END   --- 	DBMS_SYBASE_DEVICE                                       **/

/** 							BEGIN   --- 	DBMS_SYBASE_ENGINE                                   **/
	public List<IkrInstanceData> fetchDBMS_SYBASE_ENGINE()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)client.getMeasurement("DBMS_SYBASE_ENGINE");
		
		for(SybaseEngineResult info : measurement.getEngineResult())
			res.add(new SybaseEngineIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));	
		return res;
	}
		
	public List<IkrInstanceData> fetchDBMS_SYBASE_ENGINE_CPU_TIME()
	throws ConnectorException, FetchException, PersistenceException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)client.getMeasurement("DBMS_SYBASE_ENGINE");

		for(SybaseEngineCpuTimeResult info : measurement.getEngineCpuTimeResult())
			res.add(new SybaseEngineCpuTimeIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));	
		return res;
	}
	
	public List<IkrInstanceData> fetchDBMS_SYBASE_ENGINE_DISK()
	throws ConnectorException, FetchException, PersistenceException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)client.getMeasurement("DBMS_SYBASE_ENGINE");

		for(SybaseEngineDiskIOResult info : measurement.getEngineDiskIOResult())
			res.add(new SybaseEngineDiskIOIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));	
		return res;
	}
	
	public List<IkrInstanceData> fetchDBMS_SYBASE_ENGINE_HKGC()
	throws ConnectorException, FetchException, PersistenceException {	
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseEngineMeasurement measurement = (SybaseEngineMeasurement)client.getMeasurement("DBMS_SYBASE_ENGINE");	
		
		for(SybaseEngineHkGcResult info : measurement.getEngineHkGcResult())
			res.add(new SybaseEngineHkGcIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;
	}
/** 							END   --- 	DBMS_SYBASE_ENGINE                                       **/
	
/** 							BEGIN   --- 	DBMS_SYBASE_LOGS                                     **/	
	public List<IkrInstanceData> fetchDBMS_SYBASE_LOG_SEVERITY()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseLogMeasurement measurement = (SybaseLogMeasurement)client.getMeasurement("DBMS_SYBASE_LOG_SEVERITY");

		Map<String, Integer> severities = measurement.getLogSeverityResult();		
		for(String key : severities.keySet())
			res.add(new SybaseLogsSeverityIkrInstanceData(key + "@" + client.getDatabaseInstance(), severities.get(key), fetchDate));
		
		return res;
	}
	
	public List<IkrInstanceData> fetchDBMS_SYBASE_LOG_MESSAGE()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseLogMeasurement measurement = (SybaseLogMeasurement)client.getMeasurement("DBMS_SYBASE_LOG_MESSAGE");

		Map<String, String> errors = measurement.getLogMessageResult();		
		for(String key : errors.keySet())
			res.add(new SybaseLogsErrorsIkrInstanceData(key + "@" + client.getDatabaseInstance(), errors.get(key), fetchDate));	
		return res;
	}

/** 							END   --- 	DBMS_SYBASE_LOGS                                       **/
	
/** 							BEGIN   --- 	DBMS_SYBASE_MONITORCONF                            **/	
	public List<IkrInstanceData> fetchDBMS_SYBASE_MONITORCONF()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseMonitorConfMeasurement measurement = (SybaseMonitorConfMeasurement)client.getMeasurement("DBMS_SYBASE_MONITORCONF");	
		
		for(SybaseMonitorConfResult info : measurement.getMonitorConfResult())
			res.add(new SybaseMonitorConfIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));
		
		return res;
	}
	
/** 							END   --- 	DBMS_SYBASE_MONITORCONF                                       **/

/** 							BEGIN   --- 	DBMS_SYBASE_PARTITION                                     **/
	public List<IkrInstanceData> fetchDBMS_SYBASE_PARTITION()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybasePartitionMeasurement measurement = (SybasePartitionMeasurement)client.getMeasurement("DBMS_SYBASE_PARTITION");
	
		for(SybasePartitionResult info : measurement.getPartitionResult())
			res.add(new SybasePartitionIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));
		
		return res;
	}
	 
/** 							END   --- 	DBMS_SYBASE_PARTITION                                     **/
	
/** 							BEGIN   --- 	DBMS_SYBASE_NETWORK                                     **/	
	public List<IkrInstanceData> fetchDBMS_SYBASE_NETWORK()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();	
		
		SybaseNetworkMeasurement measurement = (SybaseNetworkMeasurement)client.getMeasurement("DBMS_SYBASE_NETWORK");

		res.add(new SybaseNetworkIkrInstanceData(client.getDatabaseInstance(), 
												measurement.getNetworkResult(), 
												fetchDate));		
		return res;
	}
/** 							END   --- 	DBMS_SYBASE_NETWORK                                     **/
	
/** 							BEGIN   --- 	DBMS_SYBASE_PROCESS                                     **/	
	public List<IkrInstanceData> fetchDBMS_SYBASE_PROCESS()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();	
		
		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)client.getMeasurement("DBMS_SYBASE_PROCESS");

		for(SybaseProcessResult info : measurement.getProcessResult())
			res.add(new SybaseProcessIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;	
	}

	public List<IkrInstanceData> fetchDBMS_SYBASE_PROCESS_NETWORK()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
			
		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)client.getMeasurement("DBMS_SYBASE_PROCESS");

		for(SybaseProcessNetworkResult info : measurement.getProcessNetworkResult())
			res.add(new SybaseProcessNetworkIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;	
	}
		
	public List<IkrInstanceData> fetchDBMS_SYBASE_PROCESS_CONTENTION()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
			
		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)client.getMeasurement("DBMS_SYBASE_PROCESS");

		for(SybaseProcessContentionResult info : measurement.getProcessContentionResult())
			res.add(new SybaseProcessContentionIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;	
	}
		
	public List<IkrInstanceData> fetchDBMS_SYBASE_PROCESS_TRANSACTION()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)client.getMeasurement("DBMS_SYBASE_PROCESS");
	
		for(SybaseProcessTransactionResult info : measurement.getProcessTransactionResult())
			res.add(new SybaseProcessTransactionIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;	
	}
		
	public List<IkrInstanceData> fetchDBMS_SYBASE_PROCESS_ULC()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)client.getMeasurement("DBMS_SYBASE_PROCESS");	
		
		for(SybaseProcessULCResult info : measurement.getProcessULCResult())
			res.add(new SybaseProcessULCIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;	
	}
		
	public List<IkrInstanceData> fetchDBMS_SYBASE_PROCESS_THREAD()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)client.getMeasurement("DBMS_SYBASE_PROCESS");
		
		for(SybaseProcessThreadResult info : measurement.getProcessThreadResult())
			res.add(new SybaseProcessThreadIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;	
	}
		
	public List<IkrInstanceData> fetchDBMS_SYBASE_PROCESS_IO()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
			
		SybaseProcessMeasurement measurement = (SybaseProcessMeasurement)client.getMeasurement("DBMS_SYBASE_PROCESS");	
			
		for(SybaseProcessIOResult info : measurement.getProcessIOResult())
			res.add(new SybaseProcessIOIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));		
		return res;	
	}

/** 							END   --- 	DBMS_SYBASE_PROCESS                                     **/

	
/** 							BEGIN   --- 	DBMS_SYBASE_STATEMENT                                     **/
	public List<IkrInstanceData> fetchDBMS_SYBASE_STATEMENT()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseStatementMeasurement measurement = (SybaseStatementMeasurement)client.getMeasurement("DBMS_SYBASE_STATEMENT");

		List<SybaseStatementResult> infos = measurement.getStatementResult();
		for(SybaseStatementResult info : infos)
			res.add(new SybaseStatementIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));
		return res;
	}

/** 							END   --- 	DBMS_SYBASE_STATEMENT                                     **/
	
/** 							END   --- 	DBMS_SYBASE_TABLE                                     **/	
	public List<IkrInstanceData> fetchDBMS_SYBASE_TABLE()
	throws ConnectorException, FetchException, PersistenceException {	 
		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
		
		SybaseTableMeasurement measurement = (SybaseTableMeasurement)client.getMeasurement("DBMS_SYBASE_TABLE");

		for(SybaseTableResult info : measurement.getTableResult())
			res.add(new SybaseTableIkrInstanceData(client.getDatabaseInstance(),info, fetchDate));
		return res;
	}

/** 							END   --- 	DBMS_SYBASE_TABLE                                     **/

	
	@Override
	public void initConnection() throws Exception {
		RdbmsConnector rdbmsConnector = (RdbmsConnector)getConnector(RdbmsConnectorConfig.TYPE);
		String instance = monitorConfig.getAttributes().get("INSTANCE");
		
		client = new SybaseMonitoringClient(rdbmsConnector, instance);
	}
}
