package com.fsi.monitoring.query.cacheLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.query.QueryName;
import com.fsi.monitoring.query.dao.QueryDAO;

public class QueryCacheLoader 
implements CacheLoader {
	

	private static final Logger logger = Logger.getLogger(QueryCacheLoader.class);
	
	private QueryDAO queryDAO = null;
	
	public QueryCacheLoader() {}
	
	public CacheLoader clone(Ehcache arg0) throws CloneNotSupportedException {
		return null;
	}

	public void dispose() throws CacheException {
	}

	public String getName() {
		return null;
	}

	public Status getStatus() {
		return null;
	}
	
	public void setQueryDAO(QueryDAO queryDAO) {
		this.queryDAO = queryDAO;
	}
	
	public Object load(Object arg0, Object arg1)
	throws CacheException {
		return null;
	}

	public Map loadAll(Collection arg0, Object arg1)
	throws CacheException {
		return null;
	}

	public Object load(Object arg0) 
	throws CacheException {
		
		Object res = null;
		
		if (arg0 == null) {
			return new ArrayList<Long>();
		}
		
		String[] tmp = ((String)arg0).split(";");
		
		QueryName.queries queryName = QueryName.queries.valueOf(tmp[0]);
		
		try {
			switch (queryName) {
				
				case MonitorsByEnv :
					String env = tmp[1];
					res = queryDAO.getMonitorIdsByLogicalEnv(Integer.parseInt(env));
				break;
			
				case IkrCategories :
					res = queryDAO.getIkrCategoryIds();
				break;
				
				case IkrCategoriesByGroup :
					res = queryDAO.getIkrCategoryIdsByGroup(tmp[1]);
				break;	
			
				case IkrDefinitionsByIkrIntance :
					long monitorId = Long.valueOf(tmp[1]);
					//long start = System.currentTimeMillis();
					String ikrInstance = tmp[3];
					String ikrEnv = tmp[4];
					//System.out.println("monitor: " + monitorId + " inst: " + ikrInstance + " env: " + ikrEnv + " cat:" + tmp[2]);
					res = queryDAO.getIkrDefinitionIdsByIkrInstance(monitorId, tmp[2], ikrInstance, ikrEnv);
					//System.out.println("**dao defIds delay : " + (System.currentTimeMillis()-start));
				break;
				
				case IkrDefinitions :
					monitorId = Long.valueOf(tmp[1]);
					// start = System.currentTimeMillis();
					res = queryDAO.getIkrDefinitionIds(monitorId, tmp[2]);
					//System.out.println("**dao defIds delay : " + (System.currentTimeMillis()-start));
				break;
				
				case IkrDefinitionSelector :
					String ikrCategory = tmp[1];
					ikrInstance = tmp.length == 4 ? tmp[2] : "";
					ikrEnv = tmp.length == 4 ? tmp[3] : "";
					res = queryDAO.getIkrDefinitionIds(ikrCategory, ikrInstance, ikrEnv);
				break;				
				
				case IkrDefinitionsALL :
					res = queryDAO.getIkrDefinitionIds();
				break;
				
				case AlertDefinitionsByIkrDefinitionId :
					long ikrDefinitionId = Long.valueOf(tmp[1]);
					res = queryDAO.getAlertDefinitions(ikrDefinitionId);
				break;
				
				case AlertDefinitionIdsALL :
					res = queryDAO.getAlertDefinitionIds();
				break;				
			}
		
		} catch(PersistenceException exc) {
			exc.printStackTrace();
			logger.fatal(exc);
		}
		return res;
	}

	public Map loadAll(Collection arg0)
	throws net.sf.jsr107cache.CacheException {
		System.out.println("---loadAll no arg for QueryCacheLoader---");
		return null;
	}

}
