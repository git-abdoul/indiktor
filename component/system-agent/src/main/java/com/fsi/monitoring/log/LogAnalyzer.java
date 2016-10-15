package com.fsi.monitoring.log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.fsi.fwk.scheduling.Scheduler;
import com.fsi.fwk.scheduling.SchedulerTask;
import com.fsi.fwk.scheduling.iterators.TimeIterator;
import com.fsi.monitoring.log.tail.LogTailer;
import com.fsi.monitoring.system.server.SystemMonitoringServer;

public class LogAnalyzer {
	private static final Logger LOG = Logger.getLogger(LogAnalyzer.class);
	
	private static final long DELAY = 3600;
	
	private String[] directories;
	private String nameFormat;
	private String datetimeFormat;
	private String extension;
	private long checkDelay;
	
	private SystemMonitoringServer server;
	
	private Scheduler scheduler;
	
	private Map<String, LogTailer> logTailers;
	
	public LogAnalyzer(SystemMonitoringServer server, long checkDelay, String[] directories, String fileFormat,
			String nameFormat, String datetimeFormat, String extension) {
		super();
		
		this.server = server;
		if (checkDelay == 0)
			this.checkDelay = DELAY;
		else
			this.checkDelay = checkDelay;
		this.directories = directories;
		this.nameFormat = nameFormat;
		if (this.nameFormat != null)
			this.nameFormat = this.nameFormat.trim();
		else
			this.nameFormat = "";
		
		if (fileFormat.contains("%DATE-FORMAT%")) {
			if (datetimeFormat!=null && datetimeFormat.length()>0) {
				this.datetimeFormat = datetimeFormat;
			}
		}
		else
			this.datetimeFormat = "";
		
		this.extension = extension;
		
		logTailers = new HashMap<String, LogTailer>();
	}
	
	private String getLogFileName(File file) {
		int dtFormatLg = datetimeFormat.length();
		String name = "";
		if (dtFormatLg>0 && nameFormat.startsWith(datetimeFormat)) {
			int lg = file.getName().length();
			int extlg = extension.length() + 1;
			name = file.getName().substring(dtFormatLg-1, lg);
			name = name.substring(0, extlg-1);
		} 
		else {
			int extlength = datetimeFormat.length() + extension.length() + 1;
			int lg = file.getName().length() - extlength;
			name = file.getName().substring(0, lg-1);
		}
		return name;
	}
	
	private List<File> getEligibleLogs() {
		List<File> logFiles = new ArrayList<File>();		
		Map<String, List<File>> fileMap = new HashMap<String, List<File>>();
		FileFilter fileFilter = new FileFilter() {
		    public boolean accept(File file) {
		    	String name = file.getName();
		    	boolean ret = false;
		    	if (file.isDirectory())
		    		ret = true;
		    	else  {
		    		if (name.endsWith("."+extension) && (nameFormat.length()==0 || "*".equals(nameFormat) || name.contains(nameFormat))) {
		    			ret = true;
		    		}
		    	}
		        return ret;
		    }
		};		
		
		for(String dirName : directories) {
			File dir = new File(dirName);	
			Map<String, List<File>> results = getLogs(dir, fileFilter);
			for (String key : results.keySet()) {
				List<File> tmp = fileMap.get(key);
				if (tmp == null) {
					fileMap.put(key, results.get(key));
				}
				else {
					tmp.addAll(results.get(key));
				}
			}
		}
		
		for (String key : fileMap.keySet()) {
			List<File> files = fileMap.get(key);
			File lastRecentFile = null;
			for (File file : files) {
				if (lastRecentFile == null)
					lastRecentFile = file;
				else {
					if (FileUtils.isFileNewer(file, lastRecentFile))
						lastRecentFile = file;		
				}
			}
			logFiles.add(lastRecentFile);
		}
		
		return logFiles;
	}
	
	private Map<String, List<File>> getLogs(File dir, FileFilter fileFilter) {
		Map<String, List<File>> fileMap = new HashMap<String, List<File>>();
		File[] files = dir.listFiles(fileFilter);
		for (File file : files) {
			if (file.isDirectory()) {
				Map<String, List<File>> deeperList = getLogs(file, fileFilter);
				for (String key : deeperList.keySet()) {
					List<File> tmp = fileMap.get(key);
					if (tmp == null) {
						fileMap.put(key, deeperList.get(key));
					}
					else {
						tmp.addAll(deeperList.get(key));
					}
				}
			}
			else {		
				String name = getLogFileName(file);
				List<File> tmp = fileMap.get(name);
				if (tmp == null) {
					tmp = new ArrayList<File>();
					fileMap.put(name, tmp);
				}
				tmp.add(file);
			}
		}
		return fileMap;
	}
	
	public void start() {
		LOG.info("Starting System Agent Log Analyzer ...");
		System.out.println("Starting System Agent Log Analyzer ...");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());	
		
		LogTailerTask task = new LogTailerTask();
		task.run();
		
		scheduler = new Scheduler();
		scheduler.schedule(task, new TimeIterator(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), checkDelay));	
	}
	
	public void stop() {
		scheduler.cancel();
	}
	
	class LogTailerTask extends SchedulerTask {
		@Override
		public void run() {
			List<File> currentLogFiles = getEligibleLogs();
        	for (File current : currentLogFiles) {
        		String name = getLogFileName(current);
        		try {
            		LogTailer logTailer = logTailers.get(name);
            		if (logTailer == null) {           			
						logTailer = new LogTailer(name, server, current);	
						logTailer.start();
						logTailers.put(name, logTailer);
						LOG.info("Start Monitoring log File - " + current.getName());
						System.out.println("Start Monitoring log File - " + current.getName());
						long size = current.length();
						System.out.println("Log Size - " + current.getName() + " : " + FileUtils.byteCountToDisplaySize(size));
            		}
            		else {
            			if (!logTailer.getFile().getName().equals(current.getName())) {
            				logTailer.stopTailer();
            				LOG.info("Monitoring of log File - " + logTailer.getFile().getName() + " Stopped");
            				logTailer = new LogTailer(name, server, current);
            				logTailer.start();
            				logTailers.put(name, logTailer);
            				LOG.info("Start Monitoring log File - " + current.getName());
            			}
            		}
        		} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
        	}   
		}		
	}
	
}
