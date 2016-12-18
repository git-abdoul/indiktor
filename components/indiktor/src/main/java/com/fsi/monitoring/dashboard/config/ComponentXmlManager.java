package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.DashBoard;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


import org.apache.log4j.Logger;

public class ComponentXmlManager {

	private static final Logger logger = Logger.getLogger(ComponentXmlManager.class);
	
	private String boardRoot;
	
	public void setBoardRoot(String boardRoot) {
		this.boardRoot = boardRoot;
	}
	
	public DashBoard getDashBoardSchema(String env,
										String type,
										String title) {
		DashBoard dashBoard = null;
		
		try {
			JAXBContext jc = JAXBContext.newInstance("generated.dashboard.config.schema");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			
			String boardPath = getDashBoardFilePath(env, type, title);
			System.out.println(boardPath + " dashBoard requested");
			
			dashBoard = (DashBoard) unmarshaller.unmarshal(new File(boardPath));
		} catch(Exception exc) {
			System.out.println(exc);
			logger.error(exc);
		}
		
		return dashBoard;
	}
	
	public void saveDashBoardSchema(DashBoard dashBoard) {
		try {
			JAXBContext jc = JAXBContext.newInstance("generated.dashboard.config.schema");
			Marshaller marshaller = jc.createMarshaller();
		
			String boardPath = createDashBoardFilePath(dashBoard.getEnv(),
													   dashBoard.getType(),
													   dashBoard.getTitle());

			
			marshaller.marshal(dashBoard,
					   		   new FileOutputStream(boardPath));
			
		} catch(Exception exc) {
			logger.error(exc);
		}
	}
	
	public void removeDashBoarsFilePath(String env,
			   						    String type,
			   						    String title) {
		try {
			String path =  getDashBoardFilePath(env,type,title);
			
			File file = new File(path);
			File parentTypeFile = file.getParentFile();
			File parentEnvFile = parentTypeFile.getParentFile();
			
			file.delete();
			
			int parentTypeListFiles = parentTypeFile.listFiles().length;
			if(parentTypeListFiles == 0) {
				parentTypeFile.delete();
			}
			
			int parentEnvListFiles = parentEnvFile.listFiles().length;
			if(parentEnvListFiles == 0) {
				parentEnvFile.delete();
			}
		} catch(Exception exc) {
			logger.error(exc);
		}
				
		
	}
	
	private String createDashBoardFilePath(String env,
										   String type,
										   String title) {
		String res = null;
		
		try {
			String path =  getDashBoardFilePath(env,type,title);
			
			File file = new File(path.substring(0,path.lastIndexOf('/')));
			file.mkdirs();
			
			res = getDashBoardFilePath(env, type, title);

		} catch(Exception exc) {
			logger.error(exc);
		}
		
		return res;
	}
	
	private String getDashBoardFilePath(String env,
										String type,
										String title) {
		env = env.replaceAll("/", "");
		type = type.replaceAll("/", "");
		title = title.replaceAll("/", "");
		
		String res = boardRoot + '/' + env + '/' + type + '/' + title +  ".xml";
		
		res = res.replaceAll(" ", "");
			
		return res;
	}
	
	
	
}
