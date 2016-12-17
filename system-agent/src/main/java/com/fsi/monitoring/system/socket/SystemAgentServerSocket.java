package com.fsi.monitoring.system.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.fsi.monitoring.system.server.SystemMonitoringServer;

public class SystemAgentServerSocket {
	private static final Logger LOG = Logger.getLogger(SystemAgentServerSocket.class);
	
	private int port;
	private int maxConnections;
	private SystemMonitoringServer server;
	
	private boolean running = true;
	
	public SystemAgentServerSocket(SystemMonitoringServer server, int port, int maxConnections) {
		super();
		this.server = server;
		this.port = port;
		this.maxConnections = maxConnections;
	}
	
	public void start() {
		int i = 0;
		try {
			final ServerSocket listener = new ServerSocket(port);
			LOG.info("System Agent Socket Server Listening on port " + port);
			System.out.println("System Agent Socket Server Listening on port " + port);
			
			Thread runner = new Thread(new Runnable() {				
				public void run() {
					while(running){
						Socket clientSocket;
						try {
							clientSocket = listener.accept();						
							LOG.debug("Receive a connection from " + clientSocket.getInetAddress().getHostName());
							System.out.println("Receive a connection from " + clientSocket.getInetAddress().getHostName());
							ClientSocketHandler connection = new ClientSocketHandler(server, clientSocket);
							Thread handler = new Thread(connection);
							handler.start();
						} catch (IOException e) {
							LOG.error(e.getMessage(), e);
							System.err.println(e.getMessage());
						}
					}
					
				}
			});
			
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void stop() {
		running = false;
	}
	
	
}
