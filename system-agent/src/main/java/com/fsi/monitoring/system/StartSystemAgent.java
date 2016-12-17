package com.fsi.monitoring.system;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

import com.fsi.monitoring.log.LogAnalyzer;
import com.fsi.monitoring.system.config.SystemAgentConfigManager;
import com.fsi.monitoring.system.config.SystemAgentContext;
import com.fsi.monitoring.system.server.SystemMonitoringServer;
import com.fsi.monitoring.system.socket.SystemAgentServerSocket;

public class StartSystemAgent {
	private static final Logger LOG = Logger.getLogger(StartSystemAgent.class);
	
	public static void main(String args []) {
		try {
			SystemAgentConfigManager config = new SystemAgentConfigManager();
			config.loadConfiguration();
		} catch (Exception e1) {
			LOG.fatal("Error while loading configuration" + e1);
		}
		
		SystemAgentContext context = SystemAgentContext.getContext();		
		boolean logConfigured=false;
		int port = context.getSystemAgentPort();	
		
		SystemMonitoringServer server = null;
		
		try {
			System.load(context.getSigarHome() + "/" + context.getSigarLibName()); 	
			server = new SystemMonitoringServer();
		}catch(RemoteException e) {
			String msg = "Error while instantiating System Agent RMI Server : " + e;
			System.out.println(msg);
			LOG.info(msg);
			System.exit(1);
		} catch (Throwable e) {
			System.err.println("Error while instantiating System Agent RMI Server : " + e);
			if(logConfigured){
				LOG.error("Error while instantiating System Agent RMI Server", e);
			}else{
				e.printStackTrace();
			}
			System.exit(1);
		}				
		
		server.startClient();		
		
		try {
			Registry registry = LocateRegistry.createRegistry(port);			
			server.bind(registry);
			System.out.println("System Agent Started on port <" + port + ">");
			LOG.info("System Agent Started on port <" + port + ">");
			
			if (context.isEnableSocket()) {
				SystemAgentServerSocket serverSocket = new SystemAgentServerSocket(server, context.getSocketPort(), context.getSocketMaxConnection());
				serverSocket.start();
			}
			
			if (context.isEnableLogAnalyzer()) {
				LogAnalyzer logAnalyzer = new LogAnalyzer(server, context.getCheckDelay(), context.getRootDirectories(), context.getFileFormat(), context.getFileFormatName(), context.getFileFormatDatetimeFormat(), context.getFileFormatExtension());
				logAnalyzer.start();
			}
			
		} catch (AccessException e) {
			System.out.println(e.getMessage());
			LOG.info(e.getMessage());
			System.exit(1);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			LOG.info(e.getMessage());
			System.exit(1);
		} catch (AlreadyBoundException e) {
			System.out.println(e.getMessage());
			LOG.info(e.getMessage());
			System.exit(1);
		}
		
	}
	
	static void usage() {
		LOG.info("Usage java ... StartSystemAgent -port 1024");
		System.out.println("Usage java ... StartSystemAgent -port 1024");
	}
	
	static public boolean isOption(String args[], String opt) {
        for (int i = 0; i < args.length; i++)
            if (args[i].equals(opt))
                return true;
        return false;
    }

    static public String getOption(String args[], String opt) {
        for (int i = 0; i < args.length; i++)
            if (args[i].equals(opt)
                    && (i < args.length - 1))
                return args[i + 1];
        return null;
    }
}
