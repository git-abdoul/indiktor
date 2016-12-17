package com.fsi.monitoring.util.menu;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.fsi.monitoring.util.menu.menuItem.MenuItem;
import com.fsi.monitoring.util.menu.menuItem.MenuItemSeparator;
import com.fsi.monitoring.util.menu.menuItem.MenuItems;


public class MenuBean {
	private String calypsoResourceDir = "resources/calypsoboard/";
	private String techResourceDir = "resources/techboard/";
	
	private List<MenuItem> menuModel;

	public String getMenuModel() {
		StringBuffer menuStr = new StringBuffer();
		
		menuStr.append("<ul id=\"mainMenu\" class=\"lvl0\">");
		
		for(MenuItem menuItem : menuModel) {
			menuStr.append(menuItem.edit());
		}
		
		menuStr.append("</ul>");
		
		String res =  menuStr.toString();
		System.out.println(res);
		return res;
	}
	
	private void addMenuItem(MenuItem menuItem) {
		menuModel.add(menuItem);
		byte level = 1;
		menuItem.setLevel(level);
		menuItem.setId("subMenu" + menuModel.size());
	}

	
	public MenuBean() {

		menuModel = new ArrayList<MenuItem>();

		// IKR Workspace Menu
		MenuItem ikrWorkspace = new MenuItem();
		ikrWorkspace.setValue("IKR Workspace");
		addMenuItem(ikrWorkspace);

		// IKR Console Menu
		MenuItems ikrconsole = new MenuItems();
		ikrconsole.setValue("IKR Console");
		addMenuItem(ikrconsole);
		
			MenuItems calypsoConsole = new MenuItems();
			calypsoConsole.setValue("Calypso");
			ikrconsole.add(calypsoConsole);

				MenuItem calypsoConsole_Server = new MenuItem();
				calypsoConsole_Server.setValue("Data Backbone");
				calypsoConsole.add(calypsoConsole_Server);

				MenuItem calypsoConsole_Engines = new MenuItem();
				calypsoConsole_Engines.setValue("Engines");
				calypsoConsole.add(calypsoConsole_Engines);

				MenuItem calypsoConsole_Event = new MenuItem();
				calypsoConsole_Event.setValue("Events");
				calypsoConsole.add(calypsoConsole_Event);

				MenuItem calypsoConsole_Tasks = new MenuItem();
				calypsoConsole_Tasks.setValue("Event Tasks");
				calypsoConsole.add(calypsoConsole_Tasks);

				MenuItem calypsoConsole_SchedTasks = new MenuItem();
				calypsoConsole_SchedTasks.setValue("Scheduled Tasks");
				calypsoConsole.add(calypsoConsole_SchedTasks);

				MenuItem calypsoConsole_Exceptions = new MenuItem();
				calypsoConsole_Exceptions.setValue("Exceptions");
				calypsoConsole.add(calypsoConsole_Exceptions);

			MenuItem processConsole = new MenuItem();
			processConsole.setValue("Process");
			ikrconsole.add(processConsole);

			MenuItem dbmsConsole = new MenuItem();
			dbmsConsole.setValue("DBMS");
			ikrconsole.add(dbmsConsole);

			MenuItem systemConsole = new MenuItem();
			systemConsole.setValue("System");
			ikrconsole.add(systemConsole);

		// Dashboard Center Menu
		MenuItems dashboardCenter = new MenuItems();
		dashboardCenter.setValue("Dashboard Center");
		addMenuItem(dashboardCenter);
		
			// Dashboard - My Boards subMenu
			MenuItem myBoards = new MenuItem();
			myBoards.setValue("My Boards");
			dashboardCenter.add(myBoards);

			// Dashboard - Weather subMenu
			MenuItem weatherBoard = new MenuItem();
			weatherBoard.setValue("Weather Board");
			dashboardCenter.add(weatherBoard);

			// Dashboard - Tech subMenu
			MenuItems techBoard = new MenuItems();
			techBoard.setValue("Tech Board");
			dashboardCenter.add(techBoard);
			
				Map<String, String> techGlobalBoards = getTechGlobalConfs();
				for (String key : techGlobalBoards.keySet()) {
					String value = techGlobalBoards.get(key);
					MenuItem item = new MenuItem();
					item.setValue(value);
					item.setAction("techGlobalBoard");
					item.setActionListener("#{technicalGlobalBoardBean.init}");		
					item.setAttribute("config", key);
					techBoard.add(item);		
				}

				Map<String, String> techBoards = getTechBoardsMenu();
				if (techBoards.size() > 0) {
					MenuItemSeparator separator = new MenuItemSeparator();
					techBoard.add(separator);
				}
				for (String key : techBoards.keySet()) {
					String value = techBoards.get(key);
					MenuItem item = new MenuItem();
					item.setValue(value);
					item.setAction("techComponentBoard");
					item.setActionListener("#{technicalComponentBoardBean.init}");		
					item.setAttribute("config", key);
					techBoard.add(item);			
				}

			// Dashboard - Calypso subMenu
			MenuItems calypsoBoard = new MenuItems();
			calypsoBoard.setValue("Calypso Board");
			dashboardCenter.add(calypsoBoard);
			
				Map<String, String> managers = null;//getConfigs(calypsoResourceDir + CalypsoGlobalBoardManager.RESOURCE);
				for (String key : managers.keySet()) {
					String value = managers.get(key);
					MenuItem item = new MenuItem();
					item.setValue(value);
//					item.setLink(CalypsoGlobalBoardBuilder.BASE_URL + "?config=" + key);
					item.setAction("calypsoGlobalBoard");
					item.setActionListener("#{calypsoGlobalBoardManager.init}");		
					item.setAttribute("config", key);
					calypsoBoard.add(item);
				}

				if (managers.size() > 0) {
					MenuItemSeparator separator = new MenuItemSeparator();
					calypsoBoard.add(separator);
				}

				MenuItem mainEntry = new MenuItem();
				mainEntry.setValue("Main Entry");
				mainEntry.setAction("mainEntryBoard");
				calypsoBoard.add(mainEntry);

				MenuItems dataBackbone = new MenuItems();
				dataBackbone.setValue("Data Backbone");
				calypsoBoard.add(dataBackbone);
		
					Map<String, String> dsServers = null;//getConfigs(calypsoResourceDir + DataserverComponentBoardBean.RESOURCE);
					for (String key : dsServers.keySet()) {
						String value = dsServers.get(key);
						MenuItem item = new MenuItem();
						item.setValue(value);
//						item.setLink(DataserverComponentBoardBuilder.BASE_URL + "?config=" + key);
						item.setAction("dataserverComponentBoard");
						item.setActionListener("#{dataserverComponentBoardBean.init}");		
						item.setAttribute("config", key);					
						dataBackbone.add(item);
					}

					if (dsServers.size() > 0) {
						MenuItemSeparator separator = new MenuItemSeparator();
						dataBackbone.add(separator);
					}

					Map<String, String> esServers = null;//getConfigs(calypsoResourceDir + EventServerComponentBoardBean.RESOURCE);
					for (String key : esServers.keySet()) {
						String value = esServers.get(key);
						MenuItem item = new MenuItem();
						item.setValue(value);
//						item.setLink(EventserverComponentBoardBuilder.BASE_URL + "?config=" + key);
						item.setAction("eventServerComponentBoard");
						item.setActionListener("#{eventServerComponentBoardBean.init}");		
						item.setAttribute("config", key);
						dataBackbone.add(item);
					}

				MenuItems engines = new MenuItems();
				engines.setValue("Engines");
				calypsoBoard.add(engines);
		
					Map<String, String> engineConfs = null;//getCalypsoGlobalConfs(EngineGlobalBoardBean.RESOURCE);
					for (String key : engineConfs.keySet()) {
						String value = engineConfs.get(key);
						MenuItem item = new MenuItem();
						item.setValue(value);
//						item.setLink(EngineGlobalBoardBuilder.BASE_URL + "?config=" + key);
						item.setAction("engineGlobalBoard");
						item.setActionListener("#{engineGlobalBoardBean.init}");		
						item.setAttribute("config", key);
						engines.add(item);
					}

					MenuItems scheduledTasks = new MenuItems();
					scheduledTasks.setValue("Scheduled Tasks");
					calypsoBoard.add(scheduledTasks);
			
						Map<String, String> tasks = getScheduledTasks();
						for (String key : tasks.keySet()) {
							String value = tasks.get(key);
							MenuItem item = new MenuItem();
							item.setValue(value);
//							item.setLink(ScheduledTaskBoardBuilder.BASE_URL + "?config=" + key);
							item.setAction("scheduledTaskBoard");
							item.setActionListener("#{scheduledTaskBoardBean.init}");		
							item.setAttribute("config", key);
							scheduledTasks.add(item);
						}

					MenuItems externalApp = new MenuItems();
					externalApp.setValue("External Apps");
					calypsoBoard.add(externalApp);
			
					Map<String, String> externalAppConfs = null;//getCalypsoGlobalConfs(ExternalApplicationGlobalBoardBean.RESOURCE);
					for (String key : externalAppConfs.keySet()) {
						String value = externalAppConfs.get(key);
						MenuItem item = new MenuItem();
						item.setValue(value);
//						item.setLink(ExternalApplicationGlobalBoardBuilder.BASE_URL + "?config=" + key);
						item.setAction("externalApplicationGlobalBoard");
						item.setActionListener("#{externalApplicationGlobalBoardBean.init}");		
						item.setAttribute("config", key);
						externalApp.add(item);
					}

		// Report center Menu
		MenuItem reportCenter = new MenuItem();
		reportCenter.setValue("Report Center");
		addMenuItem(reportCenter);

		// Alert Center Menu
		MenuItems alertCenter = new MenuItems();
		alertCenter.setValue("Alert Center");
		addMenuItem(alertCenter);
		
			// Alert manager - Tech subMenu
			MenuItem alertManager = new MenuItem();
			alertManager.setValue("Alert Manager");
			alertManager.setAction("alertManager");
			alertManager.setActionListener("#{alertManagerBean.initManager}");		
			alertCenter.add(alertManager);

			// Alert Board - Tech subMenu
			MenuItems alertBoard = new MenuItems();
			alertBoard.setValue("Alert Board");
			alertCenter.add(alertBoard);
			
				MenuItem serverBoard = new MenuItem();
				serverBoard.setValue("Server Board");
				serverBoard.setAction("alertBoard");
				serverBoard.setActionListener("#{logicalAlertBoardBean.initTree}");
				serverBoard.setAttribute("config", "ServerBoard");
				alertBoard.add(serverBoard);

				MenuItem mapBoard = new MenuItem();
				mapBoard.setValue("Map Board");
				mapBoard.setAction("alertBoard");
				mapBoard.setActionListener("#{logicalAlertBoardBean.initTree}");
				mapBoard.setAttribute("config", "MapBoard");
				alertBoard.add(mapBoard);


		// Tools Menu
		MenuItems tools = new MenuItems();
		tools.setValue("Tools");
		addMenuItem(tools);
		
			MenuItem threadDumpAnalyzer = new MenuItem();
			threadDumpAnalyzer.setValue("Thread Dump Analyzer");
			tools.add(threadDumpAnalyzer);

			MenuItem logAnalyzer = new MenuItem();
			logAnalyzer.setValue("Log Analyzer");
			tools.add(logAnalyzer);

		// IKR Console Menu
		MenuItems administration = new MenuItems();
		administration.setValue("Administration");
		addMenuItem(administration);
		
			MenuItems config = new MenuItems();
			config.setValue("Configuration");
			config.setAction("alertConfig");
			config.setActionListener("#{allAlertDefinitionBean.initDefinitionBeans}");
			administration.add(config);

				MenuItem monitorConfig = new MenuItem();
				monitorConfig.setValue("Monitor");
				monitorConfig.setAction("monitoringConfig");
				config.add(monitorConfig);
				
				MenuItem dashboardConfig = new MenuItem();
				dashboardConfig.setValue("Dashboard");
				config.add(dashboardConfig);
				
				MenuItem alertConfig = new MenuItem();
				alertConfig.setValue("Alert");
				alertConfig.setAction("alertConfig");
				alertConfig.setActionListener("#{allAlertDefinitionBean.initDefinitionBeans}");
				config.add(alertConfig);

			MenuItems security = new MenuItems();
			security.setValue("Security");
			administration.add(security);

				MenuItem user = new MenuItem();
				user.setValue("User");
				user.setAction("users");
				security.add(user);
			
				MenuItem role = new MenuItem();
				role.setValue("Role");
				role.setAction("roles");
				security.add(role);
	}

	private Map<String, String> getScheduledTasks() {
		ResourceBundle bundle = null;//ResourceBundle.getBundle(calypsoResourceDir	+ ScheduledTaskBoardBean.RESOURCE);
		Map<String, String> tasks = null;//getConfigs(calypsoResourceDir + ScheduledTaskBoardBean.RESOURCE);
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

