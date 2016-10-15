package com.fsi.monitoring.kpi.monitor.dbms.oracle.service;


import org.apache.log4j.Logger;

import com.fsi.monitoring.connector.rdbms.RdbmsConnector;
import com.fsi.monitoring.connector.rdbms.RdbmsConnectorDAO;
import com.fsi.monitoring.kpi.monitor.dbms.DbmsMonitoringClient;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleDatabaseMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleIOMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleLatchMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleLockMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleNetworkMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleProcessMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleRedoMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleRollbackSegmentMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSGAMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSegmentMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSessionMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleSharedPoolMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStatementMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleStorageMeasurement;
import com.fsi.monitoring.kpi.monitor.dbms.oracle.measurement.OracleWaitEventMeasurement;

public class OracleMontoringClient extends DbmsMonitoringClient {
	private static final Logger LOG = Logger.getLogger(OracleMontoringClient.class);

	public OracleMontoringClient(RdbmsConnector rdbmsConnector, String databaseInstance) {
		super(rdbmsConnector, databaseInstance);
	}
	
	public RdbmsConnectorDAO getMeasurement(String measurementsName) {
		return measurements.get(measurementsName);
	}

	@Override
	public void initMeasurements(RdbmsConnector rdbmsConnector) {
		measurements.put("DBMS_ORACLE_DATABASE", new OracleDatabaseMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_IO", new OracleIOMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_LATCH", new OracleLatchMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_LOCK", new OracleLockMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_NETWORK_CIRCUIT", new OracleNetworkMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_NETWORK_DISPATCHER", new OracleNetworkMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_NETWORK_EVENT", new OracleNetworkMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_NETWORK_SHARED_SERVER", new OracleNetworkMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_PROCESS", new OracleProcessMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_REDO", new OracleRedoMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_ROLLBACK_SEGMENT", new OracleRollbackSegmentMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_SESSION", new OracleSessionMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_SGA", new OracleSGAMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_SHARED_POOL", new OracleSharedPoolMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_STATEMENT", new OracleStatementMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_STATEMENT_GLOBAL", new OracleStatementMeasurement(rdbmsConnector));
//		measurements.put("DBMS_ORACLE_TABLE", new OracleTableMeasurement());
		measurements.put("DBMS_ORACLE_WAIT_EVENT", new OracleWaitEventMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_STORAGE", new OracleStorageMeasurement(rdbmsConnector));
		measurements.put("DBMS_ORACLE_SEGMENT", new OracleSegmentMeasurement(rdbmsConnector));
	}

}
