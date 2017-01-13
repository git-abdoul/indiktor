package com.fsi.monitoring.system.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.fsi.monitoring.system.config.SystemAgentContext;
import com.fsi.monitoring.system.dto.murex.MurexInfo;
import com.fsi.monitoring.system.server.SystemMonitoringServer;

public class ClientSocketHandler implements Runnable {
	private static final Logger LOG = Logger.getLogger(ClientSocketHandler.class);
	
	/**
	 * @uml.property  name="blockPattern"
	 */
	private Pattern blockPattern;
	/**
	 * @uml.property  name="tagPattern"
	 * @uml.associationEnd  qualifier="key:java.lang.Object java.lang.String"
	 */
	private Pattern tagPattern;
	/**
	 * @uml.property  name="mandatoryTags" multiplicity="(0 -1)" dimension="1"
	 */
	private String[] mandatoryTags;
	
	/**
	 * @uml.property  name="clientSocket"
	 */
	private Socket clientSocket;
	/**
	 * @uml.property  name="server"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private SystemMonitoringServer server;
	
	ClientSocketHandler(SystemMonitoringServer server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
		
		SystemAgentContext context = SystemAgentContext.getContext();
		blockPattern = Pattern.compile(context.getBlockPattern()+"+");
		tagPattern = Pattern.compile(context.getTagPattern()+"+");
		mandatoryTags = context.getMandatoryTags();
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
			
			String data = "";
			while ((data = in.readLine()) != null) {
				processData(data);
			}
			
			clientSocket.close();
			
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

	}
	
	private void processData(String data) {
		LOG.debug("Processing data : " + data);		
		System.out.println("Processing data : " + data);	
		
		Map<String, String> tags = getTags(data);
		
		List<String> errors = checkMessage(tags);
				
		if (errors.isEmpty()) {
			server.notifyInfo(new MurexInfo(tags, tags.get("CATEGORY"), new Date()));
		}
		else {
			for (String errorMsg : errors) {
				LOG.error(errorMsg);
				System.out.println(errorMsg);
			}
		}
	}
	
	private Map<String, String> getTags(String messages) {
		Map<String, String> values = new HashMap<String, String>();
		String[] splitMessages = blockPattern.split(messages);
		for (String unitMsg : splitMessages) {			
			String[] tokens = tagPattern.split(unitMsg);
			values.put(tokens[0].trim(), tokens[1].trim());
			LOG.debug(tokens[0].trim() + " = " + tokens[1].trim());
			System.out.println(tokens[0].trim() + " = " + tokens[1].trim());
		}
		return values;
	}
	
	private List<String> checkMessage(Map<String, String> values){
		List<String> errors = new ArrayList<String>();
		
		if (mandatoryTags == null)  {
			errors.add("MSG_LENGTH tag is missing");
			return errors;
		}
		
		String lengthCheck = values.get("MSG_LENGTH");
		if (lengthCheck != null) {
			try {
				int lgth = Integer.parseInt(lengthCheck);
				if (lgth != values.size()) {
					errors.add("Message length is incorrect");
				}
			}
			catch (NumberFormatException e) {
				errors.add("Tag MSG_LENGTH is not correctly formatted");
			}
		}
		else {
			errors.add("MSG_LENGTH tag is missing");
		}
		
		for (String tag : mandatoryTags) {
			if (!values.containsKey(tag)) {
				errors.add(tag + " tag is missing");
			}
		}
		
		return errors;		
	}

}
