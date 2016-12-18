package com.fsi.monitoring.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.icesoft.faces.component.ext.taglib.MethodBindingString;
import com.icesoft.faces.component.menubar.MenuItem;
import com.icesoft.faces.component.menubar.MenuItemSeparator;

public class MenuBean {
	private String calypsoResourceDir = "calypsoboard/";
	private String techResourceDir = "techboard/";
	private List<MenuItem> menuModel;

	public List<MenuItem> getMenuModel() {
		return menuModel;
	}

	public MenuBean() {

		menuModel = new ArrayList<MenuItem>();

		// IKR Workspace Menu
		MenuItem ikrWorkspace = new MenuItem();
		ikrWorkspace.setValue("IKR Workspace");
		ikrWorkspace.setAction(new MethodBindingString(""));
		menuModel.add(ikrWorkspace);

		// IKR Console Menu
		MenuItem ikrconsole = new MenuItem();
		ikrconsole.setValue("IKR Console");
		ikrconsole.setAction(new MethodBindingString(""));

		MenuItem calypsoConsole = new MenuItem();
		calypsoConsole.setValue("Calypso");
		ikrconsole.getChildren().add(calypsoConsole);

		MenuItem calypsoConsole_Server = new MenuItem();
		calypsoConsole_Server.setValue("Data Backbone");
		calypsoConsole.getChildren().add(calypsoConsole_Server);

		MenuItem calypsoConsole_Engines = new MenuItem();
		calypsoConsole_Engines.setValue("Engines");
		calypsoConsole.getChildren().add(calypsoConsole_Engines);

		MenuItem calypsoConsole_Event = new MenuItem();
		calypsoConsole_Event.setValue("Events");
		calypsoConsole.getChildren().add(calypsoConsole_Event);

		MenuItem calypsoConsole_Tasks = new MenuItem();
		calypsoConsole_Tasks.setValue("Event Tasks");
		calypsoConsole.getChildren().add(calypsoConsole_Tasks);

		MenuItem calypsoConsole_SchedTasks = new MenuItem();
		calypsoConsole_SchedTasks.setValue("Scheduled Tasks");
		calypsoConsole.getChildren().add(calypsoConsole_SchedTasks);

		MenuItem calypsoConsole_Exceptions = new MenuItem();
		calypsoConsole_Exceptions.setValue("Exceptions");
		calypsoConsole.getChildren().add(calypsoConsole_Exceptions);

		MenuItem processConsole = new MenuItem();
		processConsole.setValue("Process");
		ikrconsole.getChildren().add(processConsole);

		MenuItem dbmsConsole = new MenuItem();
		dbmsConsole.setValue("DBMS");
		ikrconsole.getChildren().add(dbmsConsole);

		MenuItem systemConsole = new MenuItem();
		systemConsole.setValue("System");
		ikrconsole.getChildren().add(systemConsole);

		menuModel.add(ikrconsole);

		// Dashboaerd Center Menu
		MenuItem dashboardCenter = new MenuItem();
		dashboardCenter.setValue("Dashboard Center");

		// Dashboard - My Boards subMenu
		MenuItem myBoards = new MenuItem();
		myBoards.setValue("My Boards");
		myBoards.setDisabled(true);
		dashboardCenter.getChildren().add(myBoards);

		// Dashboard - Weather subMenu
		MenuItem weatherBoard = new MenuItem();
		weatherBoard.setValue("Weather Board");
		weatherBoard.setDisabled(true);
		dashboardCenter.getChildren().add(weatherBoard);

		// Dashboard - Tech subMenu
		MenuItem techBoard = new MenuItem();
		techBoard.setValue("Tech Board");
		dashboardCenter.getChildren().add(techBoard);
		Map<String, String> techGlobalBoards = getTechGlobalConfs();
		for (String key : techGlobalBoards.keySet()) {
			String value = techGlobalBoards.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(TechnicalComponentBoardBean.BASE_URL + "?config="+ key);
			item.setAction(new MethodBindingString("techGlobalBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{technicalGlobalBoardBean.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			techBoard.getChildren().add(item);		
		}

		Map<String, String> techBoards = getTechBoardsMenu();
		if (techBoards.size() > 0) {
			MenuItemSeparator separator = new MenuItemSeparator();
			techBoard.getChildren().add(separator);
		}
		for (String key : techBoards.keySet()) {
			String value = techBoards.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(TechnicalComponentBoardBean.BASE_URL + "?config="+ key);
			item.setAction(new MethodBindingString("techComponentBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{technicalComponentBoardBean.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			techBoard.getChildren().add(item);			
		}

		// Dashboard - Calypso subMenu
		MenuItem calypsoBoard = new MenuItem();
		calypsoBoard.setValue("Calypso Board");

		Map<String, String> managers = null;// getConfigs(calypsoResourceDir + CalypsoGlobalBoardManager.RESOURCE);
		for (String key : managers.keySet()) {
			String value = managers.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(CalypsoGlobalBoardManager.BASE_URL + "?config=" + key);
			item.setAction(new MethodBindingString("calypsoGlobalBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{calypsoGlobalBoardManager.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			calypsoBoard.getChildren().add(item);
		}

		if (managers.size() > 0) {
			MenuItemSeparator separator = new MenuItemSeparator();
			calypsoBoard.getChildren().add(separator);
		}

		MenuItem mainEntry = new MenuItem();
		mainEntry.setValue("Main Entry");
		mainEntry.setAction(new MethodBindingString("mainEntryBoard"));
		mainEntry.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{mainEntryTableRecordsManager.init}", new Class[]{ ActionEvent.class }));

		MenuItem dataBackbone = new MenuItem();
		dataBackbone.setValue("Data Backbone");
		Map<String, String> dsServers = null;//getConfigs(calypsoResourceDir + DataserverComponentBoardBean.RESOURCE);
		for (String key : dsServers.keySet()) {
			String value = dsServers.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(DataserverComponentBoardBean.BASE_URL + "?config="+ key);			
			item.setAction(new MethodBindingString("dataserverComponentBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{dataserverComponentBoardBean.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			dataBackbone.getChildren().add(item);
		}

		if (dsServers.size() > 0) {
			MenuItemSeparator separator = new MenuItemSeparator();
			dataBackbone.getChildren().add(separator);
		}

		Map<String, String> esServers = null;// getConfigs(calypsoResourceDir + EventServerComponentBoardBean.RESOURCE);
		for (String key : esServers.keySet()) {
			String value = esServers.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(EventServerComponentBoardBean.BASE_URL + "?config="+ key);			
			item.setAction(new MethodBindingString("eventServerComponentBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{eventServerComponentBoardBean.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			dataBackbone.getChildren().add(item);
		}

		MenuItem engines = new MenuItem();
		engines.setValue("Engines");
		Map<String, String> engineConfs = null;//getCalypsoGlobalConfs(EngineGlobalBoardBean.RESOURCE);
		for (String key : engineConfs.keySet()) {
			String value = engineConfs.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(EngineGlobalBoardBean.BASE_URL + "?config=" + key);			
			item.setAction(new MethodBindingString("engineGlobalBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{engineGlobalBoardBean.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			engines.getChildren().add(item);
		}

		MenuItem scheduledTasks = new MenuItem();
		scheduledTasks.setValue("Scheduled Tasks");
		Map<String, String> tasks = getScheduledTasks();
		for (String key : tasks.keySet()) {
			String value = tasks.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(ScheduledTaskBoardBean.BASE_URL + "?config=" + key);
			item.setAction(new MethodBindingString("scheduledTaskBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{scheduledTaskBoardBean.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			scheduledTasks.getChildren().add(item);
		}

		MenuItem externalApp = new MenuItem();
		externalApp.setValue("External Apps");
		Map<String, String> externalAppConfs = null;//getCalypsoGlobalConfs(ExternalApplicationGlobalBoardBean.RESOURCE);
		for (String key : externalAppConfs.keySet()) {
			String value = externalAppConfs.get(key);
			MenuItem item = new MenuItem();
			item.setValue(value);
//			item.setLink(ExternalApplicationGlobalBoardBean.BASE_URL + "?config=" + key);
			item.setAction(new MethodBindingString("externalApplicationGlobalBoard"));
			item.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{externalApplicationGlobalBoardBean.init}", new Class[]{ ActionEvent.class }));		
			item.getAttributes().put("config", key);
			externalApp.getChildren().add(item);
		}		

		calypsoBoard.getChildren().add(mainEntry);
		calypsoBoard.getChildren().add(dataBackbone);
		calypsoBoard.getChildren().add(engines);
		calypsoBoard.getChildren().add(scheduledTasks);
		calypsoBoard.getChildren().add(externalApp);
		dashboardCenter.getChildren().add(calypsoBoard);

		menuModel.add(dashboardCenter);

		// Report center Menu
		MenuItem reportCenter = new MenuItem();
		reportCenter.setValue("Report Center");
		reportCenter.setAction(new MethodBindingString(""));
		menuModel.add(reportCenter);

		// Alert Center Menu
		MenuItem alertCenter = new MenuItem();
		alertCenter.setValue("Alert Center");

		// Alert manager - Tech subMenu
		MenuItem alertManager = new MenuItem();
		alertManager.setValue("Alert Manager");
		alertManager.setAction(new MethodBindingString("alertManager"));
        alertManager.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{alertManagerBean.initManager}", new Class[]{ ActionEvent.class }));		
		alertCenter.getChildren().add(alertManager);

		// Alert Board - Tech subMenu
		MenuItem alertBoard = new MenuItem();
		alertBoard.setValue("Alert Board");

		MenuItem serverBoard = new MenuItem();
		serverBoard.setValue("Server Board");
		serverBoard.setAction(new MethodBindingString("alertBoard"));
		serverBoard.setActionListener(FacesContext.getCurrentInstance().getApplication().createMethodBinding("#{logicalAlertBoardBean.initTree}",new Class[] { ActionEvent.class }));
		serverBoard.getAttributes().put("config", "ServerBoard");
		alertBoard.getChildren().add(serverBoard);

		MenuItem MapBoard = new MenuItem();
		MapBoard.setValue("Map Board");
		MapBoard.setAction(new MethodBindingString("alertBoard"));
		alertBoard.getChildren().add(MapBoard);

		alertCenter.getChildren().add(alertBoard);
		menuModel.add(alertCenter);

		// Tools Menu
		MenuItem tools = new MenuItem();
		tools.setValue("Tools");

		MenuItem threadDumpAnalyzer = new MenuItem();
		threadDumpAnalyzer.setValue("Thread Dump Analyzer");
		threadDumpAnalyzer.setDisabled(true);
		tools.getChildren().add(threadDumpAnalyzer);

		MenuItem logAnalyzer = new MenuItem();
		logAnalyzer.setValue("Log Analyzer");
		logAnalyzer.setDisabled(true);
		tools.getChildren().add(logAnalyzer);

		menuModel.add(tools);

		// IKR Console Menu
		MenuItem administration = new MenuItem();
		administration.setValue("Administration");

		MenuItem config = new MenuItem();
		config.setValue("Configuration");
		config.setAction(new MethodBindingString("alertConfig"));
		config.setActionListener(FacesContext.getCurrentInstance()
				.getApplication().createMethodBinding(
						"#{allAlertDefinitionBean.initDefinitionBeans}",
						new Class[] { ActionEvent.class }));
		administration.getChildren().add(config);

		MenuItem monitorConfig = new MenuItem();
		monitorConfig.setValue("Monitor");
		monitorConfig.setAction(new MethodBindingString("monitoringConfig"));

		MenuItem dashboardConfig = new MenuItem();
		dashboardConfig.setValue("Dashboard");
		dashboardConfig.setDisabled(true);

		MenuItem alertConfig = new MenuItem();
		alertConfig.setValue("Alert");
		alertConfig.setAction(new MethodBindingString("alertConfig"));
		alertConfig.setActionListener(FacesContext.getCurrentInstance()
				.getApplication().createMethodBinding(
						"#{allAlertDefinitionBean.initDefinitionBeans}",
						new Class[] { ActionEvent.class }));

		config.getChildren().add(monitorConfig);
		config.getChildren().add(dashboardConfig);
		config.getChildren().add(alertConfig);

		MenuItem security = new MenuItem();
		security.setValue("Security");
		administration.getChildren().add(security);

		MenuItem user = new MenuItem();
		user.setValue("User");
		user.setAction(new MethodBindingString("users"));

		MenuItem role = new MenuItem();
		role.setValue("Role");
		role.setAction(new MethodBindingString("roles"));

		security.getChildren().add(user);
		security.getChildren().add(role);

		menuModel.add(administration);
	}

	private Map<String, String> getScheduledTasks() {
		ResourceBundle bundle = null;//ResourceBundle.getBundle(calypsoResourceDir	+ ScheduledTaskBoardBean.RESOURCE);
		Map<String, String> tasks = null;// getConfigs(calypsoResourceDir + ScheduledTaskBoardBean.RESOURCE);
		for (String key : tasks.keySet()) {
			String subtitle = bundle.getString(key + ".sub_title");
			String title = (subtitle != null && subtitle.length() > 0) ? subtitle
					+ ":" + tasks.get(key)
					: tasks.get(key);
			tasks.put(key, title);
		}
		return tasks;
	}

	private Map<String, String> getTechBoardsMenu() {
		ResourceBundle bundle = null;//ResourceBundle.getBundle(techResourceDir + TechnicalComponentBoardBean.RESOURCE);
		Map<String, String> configs = null;//getConfigs(techResourceDir + TechnicalComponentBoardBean.RESOURCE);
		for (String key : configs.keySet()) {
			String subtitle = bundle.getString(key + ".sub_title");
			String title = (subtitle != null && subtitle.length() > 0) ? configs
					.get(key)
					+ " - " + subtitle
					: configs.get(key);
			configs.put(key, title);
		}
		return configs;
	}
	
	private Map<String, String> getTechGlobalConfs() {
		ResourceBundle bundle = null;//ResourceBundle.getBundle(techResourceDir + TechnicalGlobalBoardBean.RESOURCE);
		Map<String, String> confs = new HashMap<String, String>();
		for (Enumeration<String> keys = bundle.getKeys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			if (key.contains(".TITLE")) {
				String value = bundle.getString(key);
				String tmp = key.substring(0, key.lastIndexOf('.'));
				confs.put(tmp, value);
			}
		}
		return confs;
	}

	private Map<String, String> getCalypsoGlobalConfs(String resource) {
		ResourceBundle bundle = ResourceBundle.getBundle(calypsoResourceDir + resource);
		Map<String, String> confs = new HashMap<String, String>();
		for (Enumeration<String> keys = bundle.getKeys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			if (key.contains(".TITLE")) {
				String value = bundle.getString(key);
				String tmp = key.substring(0, key.lastIndexOf('.'));
				String param = bundle.getString(tmp + ".ENV");
				confs.put(param, value);
			}
		}
		return confs;
	}

	private Map<String, String> getConfigs(String resource) {
		ResourceBundle bundle = ResourceBundle.getBundle(resource);
		Map<String, String> confs = new HashMap<String, String>();
		for (Enumeration<String> keys = bundle.getKeys(); keys
				.hasMoreElements();) {
			String key = keys.nextElement();
			if (key.contains(".title")) {
				String value = bundle.getString(key);
				String tmp = key.substring(0, key.indexOf('.'));
				confs.put(tmp, value);
			}
		}
		return confs;
	}
}
