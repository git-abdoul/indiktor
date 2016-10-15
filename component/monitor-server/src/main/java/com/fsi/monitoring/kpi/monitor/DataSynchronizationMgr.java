package com.fsi.monitoring.kpi.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

public class DataSynchronizationMgr {
	private static final Logger logger = Logger.getLogger(DataSynchronizationMgr.class);	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss");
	
	public final ReentrantLock statsLock = new ReentrantLock();
	
	private String synchronizationFile;	
	private URL syncUrl;
	private Properties dataProperties;
	private Map<String, DataSynchronization> datas;
	
	public void init() {
		datas = new HashMap<String, DataSynchronization>();
		dataProperties = new Properties();
		try {    		
    		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(synchronizationFile);
    		syncUrl = ResourceUtils.getURL(resolvedLocation);
    		
    		File file = new File(syncUrl.getFile());
    		if (!file.exists()) {
    			file.createNewFile();
    		} 
    	} catch (IOException ex) {
    		logger.error(ex.getMessage(), ex);
        }
	}
	
	private void reloadData() throws FileNotFoundException, IOException {
		List<String> keys = new ArrayList<String>();
		dataProperties.load(new FileInputStream(syncUrl.getFile()));        		
        for (Object objKey : dataProperties.keySet()) {
        	String recKey = (String)objKey;
        	int idx = recKey.indexOf(".");
        	String key = recKey.substring(0, idx);
        	if (keys.contains(key))
        		continue;  
        	keys.add(key);
        	String dtKey=key+".dt";
        	String stKey=key+".st";                	
        	String values = dataProperties.getProperty(dtKey);
        	String statsStr = dataProperties.getProperty(stKey);
        	if (values!=null && values.length()>0) {
        		String[] elts = values.split(":"); 
        		Date lastEvtDate;
				try {
					lastEvtDate = dateFormat.parse(elts[1]);
					Map<String, Long> stats = new HashMap<String, Long>();
					if (statsStr!=null && statsStr.length()>0) {
						String[] tmp = statsStr.split(":");
						for (String str : tmp) {
							String[] def = str.split("=");
							String instance = def[0];
							long val = Long.valueOf(def[1]);
							stats.put(instance, val);
						}
					}   						
					datas.put(key, new DataSynchronization(elts[0], lastEvtDate,stats));
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
				}
        	}
        }          
	}
	
	public DataSynchronization getDataSynchronization(String monitorId) {
		DataSynchronization dataSynchronization = null;
		statsLock.lock();
		try {
			reloadData();
			dataSynchronization = datas.get(monitorId);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			statsLock.unlock();
		}		
		return dataSynchronization;
	}
	
	public synchronized void writeDataSynchronization(String monitorId, DataSynchronization data) {
		statsLock.lock();
		try {
			String value = data.getId() + ":" + dateFormat.format(data.getLastEvtDate());
    		dataProperties.setProperty(monitorId+".dt", value); 
    		String instanceValues = "";
    		Map<String, Long> stats = data.getStats();
    		int sz = stats.size();
    		int i = 0;
    		for (String inst : stats.keySet()) {
    			instanceValues = instanceValues + inst+"="+stats.get(inst);
    			if (i < sz-1) {
    				instanceValues = instanceValues + ":";    				
    			}
    			i++;
    		}		
    		dataProperties.setProperty(monitorId+".st", instanceValues);     		
    		dataProperties.store(new FileOutputStream(syncUrl.getFile()), null); 
    	} catch (IOException ex) {
    		logger.error(ex.getMessage(), ex);
        }  finally {
			statsLock.unlock();
		}		
	}

	public void setSynchronizationFile(String synchronizationFile) {
		this.synchronizationFile = synchronizationFile;
	}
}
