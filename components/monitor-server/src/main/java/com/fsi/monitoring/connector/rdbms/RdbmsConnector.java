package com.fsi.monitoring.connector.rdbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.connector.Connector;
import com.fsi.monitoring.connector.ConnectorException;

public interface RdbmsConnector
extends Connector {	
	Connection getConnection() throws ConnectorException;	
	void rollbackConnection(Connection con) throws PersistenceException;
	void closeResultSet(ResultSet rs) throws PersistenceException;
	void closeStatement(Statement stmt) throws PersistenceException;
	void closeConnection(Connection con) throws PersistenceException;
}
