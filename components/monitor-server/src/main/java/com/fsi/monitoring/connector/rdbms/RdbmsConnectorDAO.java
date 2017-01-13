package com.fsi.monitoring.connector.rdbms;

import com.fsi.monitoring.dao.AbstractDAO;

public abstract class RdbmsConnectorDAO
extends AbstractDAO {
	
	protected RdbmsConnector rdbmsConnector;
	
	public RdbmsConnectorDAO(RdbmsConnector rdbmsConnector) {
		this.rdbmsConnector = rdbmsConnector;
	}

}
