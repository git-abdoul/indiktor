package com.fsi.monitoring.dao;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * This class is used to instantiate pooling data sources initialized once. 
 * 
 * 
 * @author aurelien.gonnay
 *
 */
public class PooledDataSourceFactory {

	public PooledDataSourceFactory() {
		super();
	}

	
	/**
	 * Instantiate a pooling datasource according to the parameters
	 * @param maxWaitTimeMs: maximum time to wait when block because the pool is exhausted
	 * @param maxActiveConnections: maximum number of active connections in the pool
	 * @param connectURI: Database connection uri
	 * @param user: User name
	 * @param password: Password
	 */
	public DataSource createDataSource(int maxWaitTimeMs, int maxActiveConnections,
			String connectURI, String user, String password) {
		//
		// First, we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		ObjectPool connectionPool = new GenericObjectPool(null,maxActiveConnections,GenericObjectPool.WHEN_EXHAUSTED_BLOCK,maxWaitTimeMs);

		//
		// Next, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//

		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,user,password);

		//
		// Now we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);

		//
		// Finally, we create the PoolingDriver itself,
		// passing in the object pool we created.
		//
		return new PoolingDataSource(poolableConnectionFactory.getPool());
    }

	
	
	
	
}
