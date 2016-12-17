package com.fsi.monitoring.alert.composite;


import java.util.Collection;
import org.apache.log4j.Logger;

public class AlertComposite
extends AlertItem {

	private static final long serialVersionUID = 8503965340049032748L;

	private static final Logger logger = Logger.getLogger(AlertComposite.class);	
	
	private Collection<AlertLeaf> alertLeafs = null;

	public AlertComposite(String title,
			 			  String type,
			 			  Collection<AlertLeaf> alertLeafs) {
		super(title, type);
		
		this.alertLeafs = alertLeafs;
		updateLevel();
	}	
	
	public Collection<AlertLeaf> getAlertLeafs() {
		return alertLeafs;
	}
	
	private void resetLevel() {
		level = NO_ALERT_DEFINITION;		
	}	
	
//	public void push(AlertBean alertBean) {
//		for(AlertLeaf alertLeaf : alertLeafs) {
//			alertLeaf.push(alertBean);
//		}
//	}
	
	public void updateLevel() {
		resetLevel();
		for(AlertLeaf alertLeaf : alertLeafs) {
			level = Math.max(level, alertLeaf.update());
		}
	}
	
	
//	public AlertCompositeBean(AlertCompositeBean parent,
//							  File file) {
//		boardId = file.getAbsolutePath();
//		
//		computeHierarchy(parent);	
//		alertComponentBeans = new HashMap<String,AlertComponentBean>();
//		
//		if (file.isDirectory()) {
//			if (!file.getAbsolutePath().contains(".svn")) {
//				label = file.getName();
//				File[] children = file.listFiles();
//			
//				for(int i=0; i<children.length; i++) {
//					if (!children[i].getAbsolutePath().contains(".svn")) {
//						AlertCompositeBean childBean = new AlertCompositeBean(this,children[i]);
//						alertComponentBeans.put(childBean.getBoardId(),childBean);
//					}
//				}
//			}
//		} else {
//			loadFileConfiguration(file);
//		}
//		
//		loadAlertFilters();
//	}
	
//	private AlertComposite(AlertComposite parent,
//							   Element element,
//							   RealTimeComponentFactory factory,
//							   AlertPM alertPM) {
//		try {
//		
//		title = element.getAttribute("title");
//		type = element.getAttribute("type");
//		boardId = parent.boardId;
//		this.parent = parent;
//		
//		String x = element.getAttribute("x");
//		if (x == null || x.length() == 0) {
//			x = "300";
//		}
//		
//		String y = element.getAttribute("y");
//		if (y == null || y.length() == 0) {
//			y = "300";
//		}
//		int yInt = Integer.parseInt(y);
//		if (yInt < RealTimeComponentFactory.Y_MIN) {
//			y = String.valueOf(RealTimeComponentFactory.Y_MIN);
//		}
//					
//		String width = element.getAttribute("width");
//		if (width == null || width.length() == 0) {
//			width = "500";
//		}
//		
//		style = "width: " + width + "px; left: " + x + "px; top: " + y + "px; position: absolute";
//		} catch (Exception exc) {
//			logger.error("Error in configuration board : " + boardId);
//		}
//		
//		//computeHierarchy(parent);
//		alertComponentBeans = new HashMap<String,AlertBoardComponent>();
//		
//		NodeList nodes = element.getElementsByTagName("alertItem");
//		for (int i=0; i<nodes.getLength();i ++) {
//			Element alertItemNode = (Element)nodes.item(i);
//			AlertBoardComponent alertComponentBean = null;
//			
//			String boardId = alertItemNode.getAttribute("boardId");
//			if (boardId != null && boardId.length() != 0) {
//				GlobalDashboardBean dashboardBean = factory.createDashboardBean(boardId);
//				alertComponentBean = dashboardBean.getAlertComposite();
//				alertComponentBean.setParent(this);
//				//alertComponentBean.type = "nav-" + dashboardBean.getType();
//				//	alertComponentBean.computeHierarchy(this);
//			} else {
//				alertComponentBean = new AlertLeaf(this,alertItemNode,alertPM);
//			}
//			alertComponentBeans.put(alertComponentBean.getTitle(),alertComponentBean);
//		}
//		
//		loadAlertFilters();
//	}
	
//	public String getStyle() {
//		return style;
//	}
//	
//	public AlertComposite(String boardId,
//							  String type,
//							  String title,
//							  NodeList alertBoardNodes,
//							  RealTimeComponentFactory factory,
//							  AlertPM alertPM) {  
//		try {
//
//			this.title = title;
//			this.boardId = boardId;
//			this.type = type;
//			
//			alertComponentBeans = new HashMap<String,AlertBoardComponent>();
//			
//			for (int i=0; i<alertBoardNodes.getLength();i++) {
//				Element alertBoardElement = (Element)alertBoardNodes.item(i);
//
//				AlertComposite childBean = new AlertComposite(this,alertBoardElement,factory,alertPM);
//				childBean.setId("alertBoard-" + i);
//				alertComponentBeans.put(childBean.getTitle(),childBean);
//			}
//		} catch(Exception exc) {
//			logger.error(exc);
//		}
//		
//	}
//		
//	@Override
//	public Collection<AlertBoardComponent> getSubTree() {
//		Collection<AlertBoardComponent> res = new ArrayList<AlertBoardComponent>();
//		res.add(this);
//		for(AlertBoardComponent alertComponentBean : alertComponentBeans.values()) {
//			res.addAll(alertComponentBean.getSubTree());
//		}		
//		return res;
//	}
		
		
	
//	public AlertCompositeBean(String boardId,
//							  String rootId,
//							  Properties bundle,
//							  AlertPM alertPM) {
//		super(rootId);
//		children = new ArrayList<AlertComponentBean>();
//		alerts = new HashMap<Long,Alert>();
//		hierarchy = new ArrayList<AlertComponentBean>();
//		
//		String[] rootResource = null;
//		try {
//			rootResource = bundle.getProperty(rootId).split(";");			
//		} catch (MissingResourceException exc) {
//			logger.error(rootId + " not found in " + boardId);
//		}
//		init(rootResource,alertPM);
//		initChildrenHierachy();		
//	}
	
//	public void setAlertPM(AlertPM alertPM) {
//		super.setAlertPM(alertPM);
//		for (AlertComponentBean child : children) {
//			child.setAlertPM(alertPM);
//		}
//	}
	
//	private void initChildrenHierachy() {
//		for (AlertComponentBean child : children) {
//			child.setHierarchy(hierarchy);
//		}
//	}
//	
//	protected void setHierarchy(Collection<AlertComponentBean> parentHierarchy) {
//		hierarchy = new ArrayList<AlertComponentBean>(parentHierarchy);
//		hierarchy.add(parent);
//		for (AlertComponentBean child : children) {
//			child.setHierarchy(hierarchy);
//		}
//	}	
//	
//	private void init(String[] resource,AlertPM alertPM) {
//		this.label = resource[0];
//		this.resourcePath = resource[1];
//		this.alertPM = alertPM;
//		if (!"NO_ALERT".equalsIgnoreCase(resource[2])) { 
//			loadConfiguration(resource,alertPM);	
//			loadAlertDefinitionIds();		
//			loadAlertFilters();
//		}		
//	}	
	
//	protected void updateLevel() {
//		computeLevel();
//		for (AlertComponentBean child : children) {
//			child.updateLevel();
//		}
//	}
	
//	public AlertBoardComponent getChildren(String componentId) {
//		return alertComponentBeans.get(componentId);
//	}	
//	
//	public Collection<AlertBoardComponent> getChildren() {
//		return new ArrayList<AlertBoardComponent>(alertComponentBeans.values());
//	}
//	
//	public Map<String,AlertBoardComponent> getChildrenMap() {
//		return alertComponentBeans;	
//	}	
//
//	@Override
//	protected void computeComponent() {
//		computeLevel();
//	}
//
//	@Override
//	protected void push(Collection<RealTimeValue> values) {}
//
//	@Override
//	public int computeLevel() {
//		level = AlertBoardBean.AlertBoardComponent;
//		
//		for(AlertBoardComponent child : alertComponentBeans.values()) {
//			level = Math.max(level, child.computeLevel()); 	
//		}
//		return level;
//	}	
	
//	private void loadAlertDefinitionIds() {
//		for (AlertComponentBean child : children) {
//			alertDefinitionIds.addAll(child.getAlertDefinitionIds());
//		}
//	}


	
//	private void addChild(AlertComponentBean child) {
//		children.add(child);
//		child.setParent(this);
//	}

//	private void loadAlertFilters() {
//		alertFilters = new HashMap<Long,AlertFilter>();
//		
//		for (AlertBoardComponent child : alertComponentBeans.values()) {
//			Map<Long, AlertFilter> filters = child.getAlertFilters();
//			if (filters != null) {
//				Collection<AlertFilter> childFilters = filters.values();			
//				for(AlertFilter childFilter: childFilters) {
//					long alertDefinitionId = childFilter.getAlertDefinitionId();
//					AlertFilter currentFilter = alertFilters.get(childFilter.getAlertDefinitionId());
//					if (currentFilter == null) {
//						currentFilter = new AlertFilter(alertDefinitionId);
//						alertFilters.put(alertDefinitionId, currentFilter);
//					}
//					currentFilter.addDomains(childFilter.getDomains());
//				}
//			}
//		}
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

//	protected void loadConfiguration(String[] resource,AlertPM alertPM) {
//		String childrensResourcePath = resource[1];
//		List<String> childrenIds = Arrays.asList(resource[2].split(","));
//		
//		Properties bundle = new Properties();
//		
//		try {
//			bundle.load(new FileInputStream(GlobalBoardBean.CONFIG_PATH + childrensResourcePath + ".properties"));
//		} catch (Exception exc) {
//			logger.error(exc);
//		}		
//		
//		//ResourceBundle bundle = ResourceBundle.getBundle(childrensResourcePath);
//
//		for (String childId : childrenIds) {
//			AlertComponentBean child = null;
//			
//			String[] childResource = null;
//			try {
//				childResource = bundle.getProperty("AC_" + childId).split(";");
//				if (logger.isDebugEnabled()) {
//					logger.debug("===" + bundle.getProperty("AC_" + childId));
//				}
//			} catch (MissingResourceException exc) {
//				logger.error("AC_" + childId + " not found in " + childrensResourcePath);
//			}
//			
//			if (childResource.length == 2) {
//				// This children is a leaf
//				String env = null;
//				
//				try {
//					env = bundle.getProperty("ENV");
//				} catch (MissingResourceException e) {
//					logger.debug("RealTimeComponentFactory, ENV not found in " + resource);
//				}
//				child = new AlertLeafBean(childId,childResource,alertPM,env);
//			} else {
//				child = new AlertCompositeBean(childId,childResource,alertPM);
//			}
//			addChild(child);				
//		}
//	}
	
//	protected synchronized void push(RealTimeValue value) {	
//		if (value instanceof Alert && alertDefinitionIds.contains(value.getValueDefinitionId())) {
//			alerts.put(value.getValueDefinitionId(), (Alert)value);
//			changed = true;
//		}	
//	}
//	
//	protected synchronized void computeComponent() {
//		if (changed) {
//			updateLevel();
//			changed = false;
//		}
//	}
//
//	@Override
//	protected Map<Long, Alert> getAlerts() {
//		if (alerts == null) {
//			return parent.getAlerts();
//		}
//		return alerts; 
//	}
}
