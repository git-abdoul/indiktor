package com.fsi.monitoring.log.tail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import org.apache.log4j.Logger;

import com.fsi.monitoring.system.dto.logAnalysis.LogInfo;
import com.fsi.monitoring.system.server.SystemMonitoringServer;

public class LogTailer extends Thread {
	private static final Logger LOG = Logger.getLogger(LogTailer.class);
	
	private SystemMonitoringServer server;
	
	private boolean running = true;

	private int updateInterval = 1000;
	private File logFile;
	private long logFilePointer; 	
	private String logInstance;

	public LogTailer(String logInstance, SystemMonitoringServer server, File logFile) throws IOException {
		this.logInstance = logInstance;
		this.server = server;
		this.logFile = logFile;
				
		// Do not allow tail logging of non-existant files. (Is this a good idea?)
        if (!logFile.exists() || logFile.isDirectory() || !logFile.canRead()) {
            throw new IOException("Can't read this file.");
        }
        
        logFilePointer = logFile.length();
	}

	 public void run() {
        try {
            while (running) {
                Thread.sleep(updateInterval);
                long len = logFile.length();
                if (len < logFilePointer) {
                    // Log must have been jibbled or deleted.
                    LOG.warn("Log file was reset. Restarting logging from start of file.");
                    logFilePointer = len;
                }
                else if (len > logFilePointer) {
                    // File must have had something added to it!
                    RandomAccessFile raf = new RandomAccessFile(logFile, "r");
                    raf.seek(logFilePointer);
                    String line = null;
                    while ((line = raf.readLine()) != null) {
                    	server.notifyInfo(new LogInfo(logInstance, line, new Date()));
                    	System.out.println("Log Received : " + logFile.getName() + " | " + line);
                    }
                    logFilePointer = raf.getFilePointer();
                    raf.close();
                }
            }
        }
        catch (Exception e) {
            LOG.error("Fatal error reading log file, log tailing has stopped.", e);
        }
    }
	 
	 public void stopTailer() {
		 running = false;
	 }
	
	public String getFilename() {
        return logFile.toString();
    }
    
    public File getFile() {
        return logFile;
    }

}
