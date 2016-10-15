/**
 * 
 */
package com.fsi.monitoring.kpi.monitor.dbms.oracle;

import java.util.Date;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.RdbmsConnectorConfig;
import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.kpi.monitor.FetchException;
import com.fsi.monitoring.kpi.monitor.MonitorTask;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleDatabaseMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleIOMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleLatchMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleLockMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleNetworkMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleProcessMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleRedoMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleRollbackSegmentMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSGAMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSGAMeasurement.OracleHitRatioResult;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSGAMeasurement.OracleSGASizeResult;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSegmentMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSessionMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSharedPoolMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStatementMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStatementMeasurement.OracleSQLGlobalStatsMemoryResult;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStorageMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleWaitEventMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleDatabaseResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleDispatcherResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleIOStatsResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleIOStorageResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleLatchPerfResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleLatchResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleLockResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleNetworkEventResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleProcessMemoryResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleRedoResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleRollbackSegmentResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSGAHitRatioResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSGASizeResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSQLGlobalStatsMemoryResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSegmentResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSessionPerfResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSessionWaitResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSharedPoolHitResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSharedPoolMemoryResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleSharedServerResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleStorageResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleVirtualCircuitResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.resourceData.OracleWaitEventResourceData;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.service.OracleMontoringClient;

/**
 * @author Maltem
 *
 */
public class OracleMonitor extends MonitorTask {
	private static final Logger LOG = Logger.getLogger(OracleMonitor.class);	
	
	private OracleMontoringClient client;
	private Date fetchDate;
	
	@Override
	protected void preStart() {}

	@Override
	protected void preFetchs() throws Exception {
		fetchDate = new Date();
		RdbmsConnector rdbmsConnector = (RdbmsConnector)getConnector(RdbmsConnectorConfig.TYPE);
		client = new OracleMontoringClient(rdbmsConnector, null);
	}	
	
	@Override
	protected void postFetchs() throws Exception {}
	

	/** 							BEGIN   --- 	DBMS_ORACLE_DATABASE                                       **/
	public OracleDatabaseResourceData fetchORACLE_DATABASE()
	throws ConnectorException, FetchException, PersistenceException {	
		OracleDatabaseMeasurement measurement = (OracleDatabaseMeasurement)client.getMeasurement("DBMS_ORACLE_DATABASE");
		return new OracleDatabaseResourceData(measurement.getDbInfoResult(), fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_DATABASE                                       **/
	
	/** 							BEGIN   --- 	DBMS_ORACLE_IO                                       **/	
	public OracleIOStorageResourceData fetchORACLE_IO_STORAGE()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleIOMeasurement measurement = (OracleIOMeasurement)client.getMeasurement("DBMS_ORACLE_IO");		
		return new OracleIOStorageResourceData(measurement.getStorageResult(), fetchDate);
	}
	
	public OracleIOStatsResourceData fetchORACLE_IO_STATS()
	throws ConnectorException, FetchException, PersistenceException {	
		OracleIOMeasurement measurement = (OracleIOMeasurement)client.getMeasurement("DBMS_ORACLE_IO");
		return new OracleIOStatsResourceData(measurement.getIOResult(), fetchDate);
	}
	
	/** 							END   --- 	DBMS_ORACLE_IO                                       **/
	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_LATCH                                       **/
	public OracleLatchResourceData fetchORACLE_LATCH()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleLatchMeasurement measurement = (OracleLatchMeasurement)client.getMeasurement("DBMS_ORACLE_LATCH");
		return new OracleLatchResourceData(measurement.getLacthResult(), fetchDate);
	}
	
	public OracleLatchPerfResourceData fetchORACLE_LATCH_PERF()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleLatchMeasurement measurement = (OracleLatchMeasurement)client.getMeasurement("DBMS_ORACLE_LATCH");
		return new OracleLatchPerfResourceData(measurement.getLatchPerfResult(), fetchDate);
	}
	
	/** 							END   --- 	DBMS_ORACLE_LATCH                                       **/
	
	/** 							BEGIN   --- 	DBMS_ORACLE_LOCK                                       **/
	public OracleLockResourceData fetchORACLE_LOCK()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleLockMeasurement measurement = (OracleLockMeasurement)client.getMeasurement("DBMS_ORACLE_LOCK");
		return new OracleLockResourceData(measurement.getLockResult(), fetchDate);
	}
	
	/** 							END   --- 	DBMS_ORACLE_LOCK                                       **/
	
	/** 							BEGIN   --- 	DBMS_ORACLE_NETWORK                                       **/	
	public OracleSharedServerResourceData fetchORACLE_NETWORK_SHARED_SERVER()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleNetworkMeasurement measurement = (OracleNetworkMeasurement)client.getMeasurement("DBMS_ORACLE_NETWORK_SHARED_SERVER");
		return new OracleSharedServerResourceData( measurement.getShareServerResult(), fetchDate);
	}	
	
	public OracleDispatcherResourceData fetchORACLE_NETWORK_DISPATCHER()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleNetworkMeasurement measurement = (OracleNetworkMeasurement)client.getMeasurement("DBMS_ORACLE_NETWORK_DISPATCHER");
		return new OracleDispatcherResourceData(measurement.getDispatcherResult(), fetchDate);
	}	
	
	public OracleVirtualCircuitResourceData fetchORACLE_NETWORK_CIRCUIT()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleNetworkMeasurement measurement = (OracleNetworkMeasurement)client.getMeasurement("DBMS_ORACLE_NETWORK_CIRCUIT");
		return new OracleVirtualCircuitResourceData(measurement.getVirtualCircuitResult(), fetchDate);
	}	
	
	public OracleNetworkEventResourceData fetchORACLE_NETWORK_EVENT()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleNetworkMeasurement measurement = (OracleNetworkMeasurement)client.getMeasurement("DBMS_ORACLE_NETWORK_EVENT");
		return new OracleNetworkEventResourceData(measurement.getNetworkEventResult(), fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_NETWORK                                       **/	
	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_PROCESS                                       **/	
	public OracleProcessMemoryResourceData fetchORACLE_PROCESS()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleProcessMeasurement measurement = (OracleProcessMeasurement)client.getMeasurement("DBMS_ORACLE_PROCESS");
		return new OracleProcessMemoryResourceData( measurement.getProcessMemoryResult(), fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_PROCESS                                       **/	
	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_REDO                                       **/	
	public OracleRedoResourceData fetchORACLE_REDO()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleRedoMeasurement measurement = (OracleRedoMeasurement)client.getMeasurement("DBMS_ORACLE_REDO");
		return new OracleRedoResourceData(measurement.getRedoResult(), fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_REDO                                       **/	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_ROLLBACK_SEGMENT                                       **/	
	public OracleRollbackSegmentResourceData fetchORACLE_ROLLBACK_SEGMENT()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleRollbackSegmentMeasurement measurement = (OracleRollbackSegmentMeasurement)client.getMeasurement("DBMS_ORACLE_ROLLBACK_SEGMENT");
		return new OracleRollbackSegmentResourceData(measurement.getRollbackSegmentResult(), fetchDate);
	}
	
	/** 							END   --- 	DBMS_ORACLE_ROLLBACK_SEGMENT                                       **/	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_SESSION                                       **/	
	public OracleSessionWaitResourceData fetchORACLE_SESSION_WAIT()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleSessionMeasurement measurement = (OracleSessionMeasurement)client.getMeasurement("DBMS_ORACLE_SESSION");
		return new OracleSessionWaitResourceData( measurement.getSessionWaitResult(), fetchDate);
	}
	
	public OracleSessionPerfResourceData fetchORACLE_SESSION_PERF()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleSessionMeasurement measurement = (OracleSessionMeasurement)client.getMeasurement("DBMS_ORACLE_SESSION");
		return new OracleSessionPerfResourceData( measurement.getSessionPerfResult(), fetchDate);
	}
	
	/** 							END   --- 	DBMS_ORACLE_SESSION                                       **/
	
	/** 							BEGIN   --- 	DBMS_ORACLE_SGA                                       **/
	public OracleSGASizeResourceData fetchORACLE_SGA_SIZE()
	throws ConnectorException, FetchException, PersistenceException {	
		OracleSGAMeasurement measurement = (OracleSGAMeasurement)client.getMeasurement("DBMS_ORACLE_SGA");	
		OracleSGASizeResult result = measurement.getSgaSizeResult();
		result.setName(client.getDatabaseInstance());
		return new OracleSGASizeResourceData(result, fetchDate);
	}
	
	public OracleSGAHitRatioResourceData fetchORACLE_SGA_HIT_RATIO()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleSGAMeasurement measurement = (OracleSGAMeasurement)client.getMeasurement("DBMS_ORACLE_SGA");
		OracleHitRatioResult result = measurement.getSgaHitRatioResult();
		result.setName(client.getDatabaseInstance());
		return new OracleSGAHitRatioResourceData(result, fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_SGA                                       **/
	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_SHARED_POOL                                       **/
	public OracleSharedPoolHitResourceData fetchORACLE_SHARED_POOL_HITS()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleSharedPoolMeasurement measurement = (OracleSharedPoolMeasurement)client.getMeasurement("DBMS_ORACLE_SHARED_POOL");
		return new OracleSharedPoolHitResourceData(measurement.getSharedPoolHitResult(), fetchDate);
	}
	
	public OracleSharedPoolMemoryResourceData fetchORACLE_SHARED_POOL_MEMORY()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleSharedPoolMeasurement measurement = (OracleSharedPoolMeasurement)client.getMeasurement("DBMS_ORACLE_SHARED_POOL");
		return new OracleSharedPoolMemoryResourceData(measurement.getSharedPoolMemoryResult(), fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_SHARED_POOL                                       **/
	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_STATEMENT                                       **/
//	public List<IkrInstanceData> fetchDBMS_ORACLE_STATEMENT()
//	throws ConnectorException, FetchException, PersistenceException {		
//		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();		
////		for(OracleSQLStatsResult info : client.getQueriesStats())
////			res.add(new OracleSQLStatsIkrInstanceData(info, System.currentTimeMillis()));		
//		return res;
//	}	
	
	public OracleSQLGlobalStatsMemoryResourceData fetchORACLE_STATEMENT_GLOBAL()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleStatementMeasurement measurement = (OracleStatementMeasurement)client.getMeasurement("DBMS_ORACLE_STATEMENT_GLOBAL");
		OracleSQLGlobalStatsMemoryResult result =  measurement.getSQLGlobalStatsMemoryResult();
		result.setName(client.getDatabaseInstance());
		return new OracleSQLGlobalStatsMemoryResourceData(result, fetchDate);
	}	
	/** 							END   --- 	DBMS_ORACLE_STATEMENT                                       **/
	
	
	/** 							BEGIN   --- 	DBMS_ORACLE_TABLE                                       **/
//	public List<IkrInstanceData> fetchDBMS_ORACLE_TABLE()
//	throws ConnectorException, FetchException, PersistenceException {		
//		List<IkrInstanceData> res = new ArrayList<IkrInstanceData>();
//		return res;
//	}
	/** 							END   --- 	DBMS_ORACLE_TABLE                                       **/
	
	/** 							BEGIN   --- 	DBMS_ORACLE_WAIT_EVENT                                       **/
	public OracleWaitEventResourceData fetchORACLE_WAIT_EVENT()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleWaitEventMeasurement measurement = (OracleWaitEventMeasurement)client.getMeasurement("DBMS_ORACLE_WAIT_EVENT");	
		return new OracleWaitEventResourceData( measurement.getWaitEventResult(), fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_WAIT_EVENT                                       **/
	
	/** 							BEGIN   --- 	DBMS_ORACLE_STORAGE                                       **/
	public OracleStorageResourceData fetchORACLE_STORAGE()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleStorageMeasurement measurement = (OracleStorageMeasurement)client.getMeasurement("DBMS_ORACLE_STORAGE");		
		return new OracleStorageResourceData(measurement.getStorageResult(), fetchDate);
	}
	/** 							END   --- 	DBMS_ORACLE_STORAGE                                       **/
	
	public OracleSegmentResourceData fetchORACLE_SEGMENT_STATS()
	throws ConnectorException, FetchException, PersistenceException {		
		OracleSegmentMeasurement measurement = (OracleSegmentMeasurement)client.getMeasurement("DBMS_ORACLE_SEGMENT");		
		return new OracleSegmentResourceData(measurement.getSegmentData(), fetchDate);
	}

	@Override
	public void initConnection() throws Exception {}
	
}
