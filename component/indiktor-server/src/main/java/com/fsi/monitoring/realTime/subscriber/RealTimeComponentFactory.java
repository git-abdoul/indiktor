package com.fsi.monitoring.realTime.subscriber;

import generated.dashboard.config.schema.AlertBoardGridType;
import generated.dashboard.config.schema.AlertBoardGridType.AlertGridItemType;
import generated.dashboard.config.schema.AlertBoardType;
import generated.dashboard.config.schema.AlertBoardType.AlertItemType;
import generated.dashboard.config.schema.AlertBoardType.AlertItemType.AlertType;
import generated.dashboard.config.schema.BatchBoardType;
import generated.dashboard.config.schema.BatchBoardType.BatchItemType;
import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.DashBoardType.AlertBoardGridsType;
import generated.dashboard.config.schema.DashBoardType.AlertBoardsType;
import generated.dashboard.config.schema.DashBoardType.BatchBoardsType;
import generated.dashboard.config.schema.DashBoardType.ChartsType;
import generated.dashboard.config.schema.DashBoardType.InfoBoardsType;
import generated.dashboard.config.schema.DashBoardType.NavigationBoardsType;
import generated.dashboard.config.schema.DashBoardType.ThreadType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.DetailType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.MethodType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.StackTraceType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.StatusType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.TypeType;
import generated.dashboard.config.schema.DefinitionChartType;
import generated.dashboard.config.schema.DefinitionChartType.DefinitionType;
import generated.dashboard.config.schema.InfoBoardType;
import generated.dashboard.config.schema.InfoBoardType.InfoItemType;
import generated.dashboard.config.schema.NavigationBoardType;
import generated.dashboard.config.schema.NavigationBoardType.NavigationItemType;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.AlertPM;
import com.fsi.monitoring.alert.bean.AlertBean;
import com.fsi.monitoring.alert.composite.AlertComposite;
import com.fsi.monitoring.alert.composite.AlertLeaf;
import com.fsi.monitoring.applet.RTChartAppletProxy;
import com.fsi.monitoring.dashboard.component.DashBoardSummaryComponent;
import com.fsi.monitoring.dashboard.component.NavigationBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardComponent;
import com.fsi.monitoring.dashboard.component.alert.AlertBoardGridComponent;
import com.fsi.monitoring.dashboard.component.batch.BatchBoardComponent;
import com.fsi.monitoring.dashboard.component.chart.IkrChartComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponents;
import com.fsi.monitoring.dashboard.component.info.InfoComponent;
import com.fsi.monitoring.dashboard.config.ComponentXmlManager;
import com.fsi.monitoring.dashboard.thread.ThreadAnalysisBean;
import com.fsi.monitoring.dashboard.thread.component.ThreadComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadDetailComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadMethodComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadStatusComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadTypeComponent;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.monitor.config.MonitorConfig;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.realTime.RealTimeRender;
import com.fsi.monitoring.realTime.listener.AlertRealTimeListener;
import com.fsi.monitoring.realTime.listener.IkrValueRealTimeListener;
import com.fsi.monitoring.util.FacesUtils;



public class RealTimeComponentFactory {		
	private static final Logger logger = Logger.getLogger(RealTimeComponentFactory.class);
	
	public static final int Y_MIN = 130;
		
	private ComponentXmlManager xmlComponentManager;
	
	private String globalEnv = null;
	private String globalType = null;
	private String globalTitle = null;
	
	// key = boardFdn
	private Map<String, DashBoardComponents> dashBoardComponentMap;
	
	
	private AlertRealTimeListener alertRTListener;
	private IkrValueRealTimeListener ikrValueRTListener;	
	private RealTimeRender realTimeRender;
	
	private MonitoringPM monitoringPM;
	private AlertPM alertPM;
	private DataModelPM dataModelPM;
	private BeanPM beanPM;
	
	private RTChartAppletProxy appletProxy;

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    
    public void setGlobalEnv(String env) {
    	this.globalEnv = env;
    }
    
    public void setGlobalType(String type) {
    	this.globalType = type;
    }
    
    public void setGlobalTitle(String title) {
    	this.globalTitle = title;
    }
    
    public void setXmlComponentManager(ComponentXmlManager manager) {
    	this.xmlComponentManager = manager;
    }
    
	public RealTimeComponentFactory() {
		dashBoardComponentMap = new HashMap<String, DashBoardComponents>();
	}
	
	public void setAppletProxy(RTChartAppletProxy appletProxy) {
		this.appletProxy = appletProxy;
	}

	public void setMonitoringPM(MonitoringPM monitoringPM) {
		this.monitoringPM = monitoringPM;
	}
	
	public void setAlertPM(AlertPM alertPM) {
		this.alertPM = alertPM;
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}	
	
	public void setBeanPM(BeanPM beanPM) {
		this.beanPM = beanPM;
	}

	public static RealTimeComponentFactory getFactory() {
		return (RealTimeComponentFactory)FacesUtils.getManagedBean("realTimeComponentFactory");
	}
	
	public void setAlertRTListener(AlertRealTimeListener alertRealTimeListener) {
		this.alertRTListener = alertRealTimeListener;
	}
	
	public void setIkrValueRTListener(IkrValueRealTimeListener ikrValueRealTimeListener) {
		this.ikrValueRTListener = ikrValueRealTimeListener;
	}
	
	public void setRealTimeRender(RealTimeRender realTimeRender) {
		this.realTimeRender = realTimeRender;
	}
	
	public DashBoardComponents getDashBoardComponents(String env,
													  String type,
													  String title) {
		DashBoardComponents res = null;
		
		r.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			res = dashBoardComponentMap.get(dashBoardKey);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			r.unlock();
		}
			
		return res;
	}
	
	public DashBoardComponents deleteDashBoard(String env,
			  								   String type,
			  								   String title) {
		DashBoardComponents res = null;
		
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			Collection<NavigationBoardComponent> navigationBoardComponents = components.getNavigationBoardComponents();
			if (navigationBoardComponents != null) {
				for (NavigationBoardComponent navigationBoardComponent : navigationBoardComponents) {
					realTimeRender.unSubscribeComputableComponent(navigationBoardComponent);
				}
			}
			
			Collection<AlertBoardComponent> alertBoardComponents = components.getAlertBoardComponents();
			if (alertBoardComponents != null) {
				for (AlertBoardComponent alertBoardComponent : alertBoardComponents) {
					realTimeRender.unSubscribeComputableComponent(alertBoardComponent);
				}
			}
			
			Collection<AlertBoardGridComponent> alertBoardGridComponents = components.getAlertBoardGridComponents();
			if (alertBoardGridComponents != null) {
//				for (AlertBoardGridComponent alertBoardGridComponent : alertBoardGridComponents) {
//					realTimeRender.unSubscribeComputableComponent(alertBoardGridComponent);
//				}
			}
			
			Collection<InfoComponent> infoComponents = components.getInfoBoardComponents();
			if (infoComponents != null) {
				for (InfoComponent infoComponent : infoComponents) {
					if (infoComponent != null) {
						Collection<Long> ikrDefinitionIds = infoComponent.getIkrDefintionIds();
						for (Long ikrDefinitionId : ikrDefinitionIds) {
							ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, infoComponent);
						}
					}
				}
			}
			
//			Collection<BatchBoardComponent> batchBoardComponents = components.getBatchBoardComponents();
//			if (batchBoardComponents != null) {
//				for (BatchBoardComponent batchBoardComponent : batchBoardComponents) {
//					if (batchBoardComponent != null) {
//						Collection<Long> ikrDefinitionIds = batchBoardComponent.getIkrDefintionIds();
//						for (Long ikrDefinitionId : ikrDefinitionIds) {
//							ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, batchBoardComponent);
//						}
//					}
//				}
//			}
			
//			Collection<IkrCategoryChartComponent> ikrCategoryChartComponents = components.getIkrCategoryChartComponents();
//			if (ikrCategoryChartComponents != null) {
//				for (IkrCategoryChartComponent ikrCategoryChartComponent : ikrCategoryChartComponents) {
//					realTimeRender.unSubscribeComputableComponent(ikrCategoryChartComponent);
//					
//					Collection<Long> ikrIds = ikrCategoryChartComponent.getIkrDefinitionIds();
//					
//					for (Long id : ikrIds) {
//						ikrValueRTListener.unSubscribeIkrCategoryComponent(id.intValue(), ikrCategoryChartComponent);
//					}	
//				}
//			}
			
			Collection<IkrChartComponent> ikrChartComponents = components.getIkrChartComponents();
			if (ikrChartComponents != null) {
				for (IkrChartComponent ikrChartComponent : ikrChartComponents) {
					realTimeRender.unSubscribeComputableComponent(ikrChartComponent);
					
					Collection<Long> ikrIds = ikrChartComponent.getIkrDefinitionIds();
					
					for (Long id : ikrIds) {
						ikrValueRTListener.unSubscribeIkrDefinitionComponent(id.intValue(), ikrChartComponent);
					}	
				}
			}			
			
			ThreadAnalysisBean threadAnalysisBean = components.getThreadAnalysisComponent();
			if (threadAnalysisBean != null) {
					ThreadDetailComponent detailComponent = threadAnalysisBean.getThreadDetailComponent();
					realTimeRender.unSubscribeComputableComponent(detailComponent);		
					for (int ikrCategoryId : detailComponent.getIkrCategoryIds()) {
						ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, detailComponent);
					}	
					
					ThreadMethodComponent methodComponent = threadAnalysisBean.getThreadMethodComponent();
					realTimeRender.unSubscribeComputableComponent(methodComponent);		
					for (int ikrCategoryId : methodComponent.getIkrCategoryIds()) {
						ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, methodComponent);
					}	
					
					ThreadStatusComponent statusComponent = threadAnalysisBean.getThreadStatusComponent();
					realTimeRender.unSubscribeComputableComponent(statusComponent);		
					for (int ikrCategoryId : statusComponent.getIkrCategoryIds()) {
						ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, statusComponent);
					}
					
					ThreadTypeComponent typeComponent = threadAnalysisBean.getThreadTypeComponent();
					realTimeRender.unSubscribeComputableComponent(typeComponent);		
					for (int ikrCategoryId : typeComponent.getIkrCategoryIds()) {
						ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, typeComponent);
					}
			}
			
			// Remove link to this Dashboard
			Collection<DashBoardComponents> allComponents = dashBoardComponentMap.values();
			for (DashBoardComponents dashBoardComponents : allComponents) {
				DashBoardSummaryComponent fromSummary = dashBoardComponents.getSummary();
				
				DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(fromSummary.getEnv(),
																		     fromSummary.getType(), 
																		     fromSummary.getTitle());
				
				NavigationBoardsType navigationBoardsType = dashBoard.getNavigationBoards();
				
				if (navigationBoardsType != null) {
					List<NavigationBoardType> navigationBoards = navigationBoardsType.getNavigationBoard();
					
					if (navigationBoards != null) {
						for (NavigationBoardType navigationBoard : navigationBoards) {
							if (navigationBoard != null) {	
								boolean updated = false;
								boolean deleteNavigationBoard = false;
								
								List<NavigationItemType> items = navigationBoard.getNavigationItem();			
								Iterator<NavigationItemType> itemIT = items.iterator();
								
								while(itemIT.hasNext()) {
									NavigationItemType item = itemIT.next();
									if (item.getEnv().equals(env) &&
										item.getTitle().equals(title) &&
										item.getType().equals(type)) {
										if(items.size() == 1)
											deleteNavigationBoard = true;
										else {
											itemIT.remove();
											updated = true;
										}
									}
								}
								if(deleteNavigationBoard) {
									removeNavigationBoardComponent(fromSummary.getEnv(), fromSummary.getType(), fromSummary.getTitle(), navigationBoard.getId());
								}
								if (updated && !deleteNavigationBoard) {
									updateNavigationBoardComponent(fromSummary.getEnv(),
										     					   fromSummary.getType(), 
										     					   fromSummary.getTitle(), 
										     					   navigationBoard);
								}
							}
						}
					}
				}
			}
						
			dashBoardComponentMap.remove(dashBoardKey);
			
			xmlComponentManager.removeDashBoarsFilePath(env, type, title);
			
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
		
		return res;
	}	
	
	public Collection<DashBoardComponents> getDashBoardComponents() {
		Collection<DashBoardComponents> res = null;
		
		r.lock();
		try {
			res = dashBoardComponentMap.values();
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			r.unlock();
		}
		return res;
	}
	 
	public void init() {
		createDashBoardComponents(globalEnv, globalType, globalTitle);
	}
	
	public void createNewDashBoard(String env,
								   String type,
								   String title) {
		w.lock();
		try {		
			ObjectFactory objFactory = new ObjectFactory();
			
			DashBoard dashBoard = objFactory.createDashBoard();
			dashBoard.setEnv(env);
			dashBoard.setType(type);
			dashBoard.setTitle(title);
			
			
			xmlComponentManager.saveDashBoardSchema(dashBoard);
			
			createDashBoardComponents(env, type, title);
			
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}			
	}
	
	public void addNewAlertBoardComponent(String env,
										  String type,
										  String title,
										  AlertBoardType alertBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			Collection<AlertBoardComponent> alertBoardComponents = components.getAlertBoardComponents();
			
			int id = 0;
			if (alertBoardComponents != null) {
				for (AlertBoardComponent cpt : alertBoardComponents) {
					String idStr = cpt.getComponentId().replaceAll("alertBoard", "");
					int tmpId = Integer.parseInt(idStr);
					id = Math.max(id, tmpId);
				}
			}			
	
			String componentId = "alertBoard" + (++id);
			
			alertBoardType.setId(componentId);
			
			// Add the new component
			AlertBoardComponent alertBoardComponent = createAlertBoardComponent(alertBoardType);
			components.addAlertBoardComponent(alertBoardComponent);
			realTimeRender.subscribeComputableComponent(alertBoardComponent);
			
			// Update the summary
			DashBoardSummaryComponent summary = components.getSummary();
			summary.updateAlertBoardComponents(components.getAlertBoardComponents());
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			AlertBoardsType alertBoardsType = dashBoard.getAlertBoards();
			
			ObjectFactory objFactory = new ObjectFactory();
			if (alertBoardsType == null) {	
				alertBoardsType = objFactory.createDashBoardTypeAlertBoardsType();
				dashBoard.setAlertBoards(alertBoardsType);
			}
			
			List<AlertBoardType> alertBoardTypes = alertBoardsType.getAlertBoard();			
			alertBoardTypes.add(alertBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void addNewAlertBoardGridComponent(String env,
											  String type,
											  String title,
											  AlertBoardGridType alertBoardGridType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			Collection<AlertBoardGridComponent> alertBoardGridComponents = components.getAlertBoardGridComponents();
			
			int id = 0;
			if (alertBoardGridComponents != null) {
				for (AlertBoardGridComponent cpt : alertBoardGridComponents) {
					String idStr = cpt.getComponentId().replaceAll("alertBoardGrid", "");
					int tmpId = Integer.parseInt(idStr);
					id = Math.max(id, tmpId);
				}
			}			
	
			String componentId = "alertBoardGrid" + (++id);
			
			alertBoardGridType.setId(componentId);
			
			// Add the new component
			AlertBoardGridComponent alertBoardGridComponent = createAlertBoardGridComponent(alertBoardGridType);
			components.addAlertBoardGridComponent(alertBoardGridComponent);
//			realTimeRender.subscribeComputableComponent(alertBoardComponent);
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			AlertBoardGridsType alertBoardGridsType = dashBoard.getAlertBoardGrids();
			
			ObjectFactory objFactory = new ObjectFactory();
			if (alertBoardGridsType == null) {	
				alertBoardGridsType = objFactory.createDashBoardTypeAlertBoardGridsType();
				dashBoard.setAlertBoardGrids(alertBoardGridsType);
			}
			
			List<AlertBoardGridType> alertBoardGridTypes = alertBoardGridsType.getAlertBoardGrid();			
			alertBoardGridTypes.add(alertBoardGridType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void addNewNavigationBoardComponent(String env,
											   String type,
											   String title,
											   NavigationBoardType navigationBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			Collection<NavigationBoardComponent> navigationBoardComponents = components.getNavigationBoardComponents();
			
			int id = 0;
			if (navigationBoardComponents != null) {
				for (NavigationBoardComponent cpt : navigationBoardComponents) {
					String idStr = cpt.getComponentId().replaceAll("navigationBoard", "");
					int tmpId = Integer.parseInt(idStr);
					id = Math.max(id, tmpId);
				}
			}
			
			String componentId = "navigationBoard" + (++id);
			navigationBoardType.setId(componentId);
			
			// Add the new component
			NavigationBoardComponent navigationBoardComponent = createNavigationBoardComponent(navigationBoardType);
			components.addNavigationBoardComponent(navigationBoardComponent);
			realTimeRender.subscribeComputableComponent(navigationBoardComponent);
			
			// Update the summary
			DashBoardSummaryComponent summary = components.getSummary();
			summary.updateNavigationBoardComponents(components.getNavigationBoardComponents());			
			
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			NavigationBoardsType navigationBoardsType = dashBoard.getNavigationBoards();
			
			ObjectFactory objFactory = new ObjectFactory();
			if (navigationBoardsType == null) {	
				navigationBoardsType = objFactory.createDashBoardTypeNavigationBoardsType();
				dashBoard.setNavigationBoards(navigationBoardsType);
			}
			
			List<NavigationBoardType> navigationBoardTypes = navigationBoardsType.getNavigationBoard();	
			navigationBoardTypes.add(navigationBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void addNewChartComponent(String env,
			   								   String type,
			   								   String title,
			   								   DefinitionChartType definitionChartType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			Collection<IkrChartComponent> chartComponents = components.getIkrChartComponents();
			
			int id = 0;
			if (chartComponents != null) {
				for (IkrChartComponent cpt : chartComponents) {
					String idStr = cpt.getComponentId().replaceAll("definitionChart", "");
					int tmpId = Integer.parseInt(idStr);
					id = Math.max(id, tmpId);
				}
			}
			
			String componentId = "definitionChart" + (++id);
			definitionChartType.setId(componentId);
			
			// Add the new component
			IkrChartComponent chartComponent = createChartComponent(definitionChartType);
			components.addIkrChartComponent(chartComponent);
			realTimeRender.subscribeComputableComponent(chartComponent);
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			ChartsType chartsType = dashBoard.getCharts();
			
			ObjectFactory objFactory = new ObjectFactory();
			if (chartsType == null) {	
				chartsType = objFactory.createDashBoardTypeChartsType();
				dashBoard.setCharts(chartsType);
			}
			
			List<DefinitionChartType> definitionChartTypes = chartsType.getDefinitionChart();
			definitionChartTypes.add(definitionChartType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void addNewInfoBoardComponent(String env,
			   							 String type,
			   							 String title,
			   							 InfoBoardType infoBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			Collection<InfoComponent> infoBoardComponents = components.getInfoBoardComponents();
			
			int id = 0;
			if (infoBoardComponents != null) {
				for (InfoComponent cpt : infoBoardComponents) {
					String idStr = cpt.getComponentId().replaceAll("infoBoard", "");
					int tmpId = Integer.parseInt(idStr);
					id = Math.max(id, tmpId);
				}
			}
			
			String componentId = "infoBoard" + (++id);
			infoBoardType.setId(componentId);
			
			// Add the new component
			InfoComponent infoComponent = createInfoComponent(infoBoardType);
			components.addInfoComponent(infoComponent);
			
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			InfoBoardsType infoBoardsType = dashBoard.getInfoBoards();
			
			ObjectFactory objFactory = new ObjectFactory();
			if (infoBoardsType == null) {	
				infoBoardsType = objFactory.createDashBoardTypeInfoBoardsType();
				dashBoard.setInfoBoards(infoBoardsType);
			}
			
			List<InfoBoardType> infoBoardTypes = infoBoardsType.getInfoBoard();
			infoBoardTypes.add(infoBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}	
	
	public void addNewBatchBoardComponent(String env,
				 String type,
				 String title,
				 BatchBoardType batchBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			Collection<BatchBoardComponent> batchBoardComponents = components.getBatchBoardComponents();
			
			int id = 0;
			if (batchBoardComponents != null) {
				for (BatchBoardComponent cpt : batchBoardComponents) {
					String idStr = cpt.getComponentId().replaceAll("batchBoard", "");
					int tmpId = Integer.parseInt(idStr);
					id = Math.max(id, tmpId);
				}
			}
			
			String componentId = "batchBoard" + (++id);
			batchBoardType.setId(componentId);
			
			// Add the new component
			BatchBoardComponent batchBoardComponent = createBatchBoardComponent(batchBoardType);
			components.addBatchBoardComponent(batchBoardComponent);
			
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			BatchBoardsType batchBoardsType = dashBoard.getBatchBoards();
			
			ObjectFactory objFactory = new ObjectFactory();
			if (batchBoardsType == null) {	
				batchBoardsType = objFactory.createDashBoardTypeBatchBoardsType();
				dashBoard.setBatchBoards(batchBoardsType);
			}
			
			List<BatchBoardType> batchBoardTypes = batchBoardsType.getBatchBoard();
			batchBoardTypes.add(batchBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	
//	public void addNewCategoryChartComponent(String env,
//			   								 String type,
//			   								 String title,
//			   								 CategoryChartType categoryChartType) {
//		w.lock();
//		try {
//			String dashBoardKey = getDashBoardKey(env, type, title);
//			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
//			
//			Collection<IkrCategoryChartComponent> categoryChartComponents = components.getIkrCategoryChartComponents();
//			
//			int id = 0;
//			if (categoryChartComponents != null) {
//				for (IkrCategoryChartComponent cpt : categoryChartComponents) {
//					String idStr = cpt.getComponentId().replaceAll("categoryChart", "");
//					int tmpId = Integer.parseInt(idStr);
//					id = Math.max(id, tmpId);
//				}
//			}
//			
//			String componentId = "categoryChart" + (++id);
//			categoryChartType.setId(componentId);
//			
//			// Add the new component
//			IkrCategoryChartComponent categoryChartComponent = createCategoryChartComponent(categoryChartType);
//			components.addIkrCategoryChartComponent(categoryChartComponent);
//			realTimeRender.subscribeComputableComponent(categoryChartComponent);
//			
//			// XML save
//			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
//			ChartsType chartsType = dashBoard.getCharts();
//			
//			ObjectFactory objFactory = new ObjectFactory();
//			if (chartsType == null) {	
//				chartsType = objFactory.createDashBoardTypeChartsType();
//				dashBoard.setCharts(chartsType);
//			}
//			
//			List<CategoryChartType> categoryChartTypes = chartsType.getCategoryChart();
//			categoryChartTypes.add(categoryChartType);	
//			xmlComponentManager.saveDashBoardSchema(dashBoard);
//		} catch (Exception exc) {
//			logger.error(exc);
//		} finally {
//			w.unlock();
//		}
//	}		
	
	public void updateAlertBoardComponent(String env,
			  							  String type,
			  							  String title,
			  							  AlertBoardType alertBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			// replace the component
			AlertBoardComponent alertBoardComponent = createAlertBoardComponent(alertBoardType);
			
			Collection<AlertBoardComponent> alertBoardComponents = components.getAlertBoardComponents();
			AlertBoardComponent oldCpt = null;
			for(AlertBoardComponent cpt : alertBoardComponents) {
				if (cpt.getComponentId().equals(alertBoardType.getId())) {
					oldCpt = cpt;
				}
			}
			
			components.replaceAlertBoardComponent(oldCpt,alertBoardComponent);
			realTimeRender.unSubscribeComputableComponent(oldCpt);
			realTimeRender.subscribeComputableComponent(alertBoardComponent);
			
			// Update the summary
			DashBoardSummaryComponent summary = components.getSummary();
			summary.updateAlertBoardComponents(components.getAlertBoardComponents());
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<AlertBoardType> alertBoardTypes = dashBoard.getAlertBoards().getAlertBoard();
			
			Iterator<AlertBoardType> alertBoardTypesIT = alertBoardTypes.iterator();
			
			while (alertBoardTypesIT.hasNext()) {
				AlertBoardType tmpAlertBoardType = alertBoardTypesIT.next();
				if (tmpAlertBoardType.getId().equals(alertBoardType.getId())) {
					alertBoardTypesIT.remove();
					break;
				}
			}
						
			alertBoardTypes.add(alertBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}	
	
	public void updateAlertBoardGridComponent(String env,
											  String type,
											  String title,
											  AlertBoardGridType alertBoardGridType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			// replace the component
			AlertBoardGridComponent alertBoardGridComponent = createAlertBoardGridComponent(alertBoardGridType);
			
			Collection<AlertBoardGridComponent> alertBoardGridComponents = components.getAlertBoardGridComponents();
			AlertBoardGridComponent oldCpt = null;
			for(AlertBoardGridComponent cpt : alertBoardGridComponents) {
				if (cpt.getComponentId().equals(alertBoardGridType.getId())) {
					oldCpt = cpt;
				}
			}
			
			components.replaceAlertBoardGridComponent(oldCpt,alertBoardGridComponent);
//			realTimeRender.unSubscribeComputableComponent(oldCpt);
//			realTimeRender.subscribeComputableComponent(alertBoardComponent);
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<AlertBoardGridType> alertBoardGridTypes = dashBoard.getAlertBoardGrids().getAlertBoardGrid();
			
			Iterator<AlertBoardGridType> alertBoardGridTypesIT = alertBoardGridTypes.iterator();
			
			while (alertBoardGridTypesIT.hasNext()) {
				AlertBoardGridType tmpAlertBoardGridType = alertBoardGridTypesIT.next();
				if (tmpAlertBoardGridType.getId().equals(alertBoardGridType.getId())) {
					alertBoardGridTypesIT.remove();
					break;
				}
			}
						
			alertBoardGridTypes.add(alertBoardGridType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void updateNavigationBoardComponent(String env,
			  								   String type,
			  								   String title,
			  								   NavigationBoardType navigationBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			// replace the component
			NavigationBoardComponent navigationBoardComponent = createNavigationBoardComponent(navigationBoardType);
			
			Collection<NavigationBoardComponent> navigationBoardComponents = components.getNavigationBoardComponents();
			NavigationBoardComponent oldCpt = null;
			for(NavigationBoardComponent cpt : navigationBoardComponents) {
				if (cpt.getComponentId().equals(navigationBoardType.getId())) {
					oldCpt = cpt;
				}
			}
			
			components.replaceNavigationBoardComponent(oldCpt,navigationBoardComponent);
			realTimeRender.unSubscribeComputableComponent(oldCpt);
			realTimeRender.subscribeComputableComponent(navigationBoardComponent);
			
			// Update the summary
			DashBoardSummaryComponent summary = components.getSummary();
			summary.updateNavigationBoardComponents(navigationBoardComponents);
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<NavigationBoardType> navigationBoardTypes = dashBoard.getNavigationBoards().getNavigationBoard();
			
			Iterator<NavigationBoardType> navigationBoardTypesIT = navigationBoardTypes.iterator();
			
			while (navigationBoardTypesIT.hasNext()) {
				NavigationBoardType tmpNavigationBoardType = navigationBoardTypesIT.next();
				if (tmpNavigationBoardType.getId().equals(navigationBoardType.getId())) {
					navigationBoardTypesIT.remove();
					break;
				}
			}
			
			navigationBoardTypes.add(navigationBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}	
	
	public void updateThreadComponent(String env,
			   						  String type,
			   						  String title,
			   						  ThreadType threadType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			ThreadAnalysisBean oldThreadComponent = components.getThreadAnalysisComponent();
			if (oldThreadComponent != null) {
				ThreadDetailComponent detailComponent = oldThreadComponent.getThreadDetailComponent();
				realTimeRender.unSubscribeComputableComponent(detailComponent);		
				for (int ikrCategoryId : detailComponent.getIkrCategoryIds()) {
					ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, detailComponent);
				}	
				
				ThreadMethodComponent methodComponent = oldThreadComponent.getThreadMethodComponent();
				realTimeRender.unSubscribeComputableComponent(methodComponent);		
				for (int ikrCategoryId : methodComponent.getIkrCategoryIds()) {
					ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, methodComponent);
				}	
				
				ThreadStatusComponent statusComponent = oldThreadComponent.getThreadStatusComponent();
				realTimeRender.unSubscribeComputableComponent(statusComponent);		
				for (int ikrCategoryId : statusComponent.getIkrCategoryIds()) {
					ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, statusComponent);
				}
				
				ThreadTypeComponent typeComponent = oldThreadComponent.getThreadTypeComponent();
				realTimeRender.unSubscribeComputableComponent(typeComponent);		
				for (int ikrCategoryId : typeComponent.getIkrCategoryIds()) {
					ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, typeComponent);
				}
			}
			
			// replace the component
			ThreadAnalysisBean newThreadComponent = createThreadComponent(threadType);
			components.setThreadAnalysisComponent(newThreadComponent);
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			dashBoard.setThread(threadType);
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}	
	
//	public void updateCategoryChartComponent(String env,
//			   								 String type,
//			   								 String title,
//			   								 CategoryChartType categoryChartType) {
//		w.lock();
//		try {
//			String dashBoardKey = getDashBoardKey(env, type, title);
//			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
//			
//			// replace the component
//			IkrCategoryChartComponent categoryChartComponent = createCategoryChartComponent(categoryChartType);
//			
//			Collection<IkrCategoryChartComponent> categoryChartComponents = components.getIkrCategoryChartComponents();
//			IkrCategoryChartComponent oldCpt = null;
//			for(IkrCategoryChartComponent cpt : categoryChartComponents) {
//				if (cpt.getComponentId().equals(categoryChartType.getId())) {
//					oldCpt = cpt;
//				}
//			}
//			
//			components.replaceCategoryChartComponent(oldCpt, categoryChartComponent);
//			realTimeRender.unSubscribeComputableComponent(oldCpt);
//			realTimeRender.subscribeComputableComponent(categoryChartComponent);
//			
//			Collection<Long> ikrIds = oldCpt.getIkrDefinitionIds();
//			
//			for (Long id : ikrIds) {
//				ikrValueRTListener.unSubscribeIkrCategoryComponent(id.intValue(), oldCpt);
//			}
//			
//			// XML save
//			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
//			
//			List<CategoryChartType> categoryChartTypes = dashBoard.getCharts().getCategoryChart();
//			
//			Iterator<CategoryChartType> categoryChartTypesIT = categoryChartTypes.iterator();
//			
//			while (categoryChartTypesIT.hasNext()) {
//				CategoryChartType tmpCategoryChartType = categoryChartTypesIT.next();
//				if (tmpCategoryChartType.getId().equals(categoryChartType.getId())) {
//					categoryChartTypesIT.remove();
//					break;
//				}
//			}
//			
//			categoryChartTypes.add(categoryChartType);	
//			xmlComponentManager.saveDashBoardSchema(dashBoard);
//		} catch (Exception exc) {
//			logger.error(exc);
//		} finally {
//			w.unlock();
//		}
//	}	
	
	public void updateChartComponent(String env,
			   								   String type,
			   								   String title,
			   								   DefinitionChartType definitionChartType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			// replace the component
			IkrChartComponent definitionChartComponent = createChartComponent(definitionChartType);
			
			Collection<IkrChartComponent> chartComponents = components.getIkrChartComponents();
			IkrChartComponent oldCpt = null;
			for(IkrChartComponent cpt : chartComponents) {
				if (cpt.getComponentId().equals(definitionChartType.getId())) {
					oldCpt = cpt;
				}
			}
			
			components.replaceChartComponent(oldCpt, definitionChartComponent);
			realTimeRender.unSubscribeComputableComponent(oldCpt);
			realTimeRender.subscribeComputableComponent(definitionChartComponent);
			
			Collection<Long> ikrDefinitionIds = oldCpt.getIkrDefinitionIds();
			for (Long ikrDefinitionId : ikrDefinitionIds) {
				ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, oldCpt);
			}
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<DefinitionChartType> definitionChartTypes = dashBoard.getCharts().getDefinitionChart();
			
			Iterator<DefinitionChartType> definitionChartTypesIT = definitionChartTypes.iterator();
			
			while (definitionChartTypesIT.hasNext()) {
				DefinitionChartType tmpDefinitionChartType = definitionChartTypesIT.next();
				if (tmpDefinitionChartType.getId().equals(definitionChartType.getId())) {
					definitionChartTypesIT.remove();
					break;
				}
			}
			
			definitionChartTypes.add(definitionChartType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void updateInfoBoardComponent(String env,
			   							 String type,
			   							 String title,
			   							 InfoBoardType infoBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			// replace the component
			InfoComponent infoComponent = createInfoComponent(infoBoardType);
			
			Collection<InfoComponent> infoComponents = components.getInfoBoardComponents();
			InfoComponent oldCpt = null;
			for(InfoComponent cpt : infoComponents) {
				if (cpt.getComponentId().equals(infoBoardType.getId())) {
					oldCpt = cpt;
				}
			}
			
			components.replaceInfoBoardComponent(oldCpt, infoComponent);
			
			Collection<Long> ikrDefinitionIds = oldCpt.getIkrDefintionIds();
			for (Long ikrDefinitionId : ikrDefinitionIds) {
				ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, oldCpt);
			}
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<InfoBoardType> infoBoardTypes = dashBoard.getInfoBoards().getInfoBoard();
			
			Iterator<InfoBoardType> infoBoardTypesIT = infoBoardTypes.iterator();
			
			while (infoBoardTypesIT.hasNext()) {
				InfoBoardType tmpInfoBoardType = infoBoardTypesIT.next();
				if (tmpInfoBoardType.getId().equals(infoBoardType.getId())) {
					infoBoardTypesIT.remove();
					break;
				}
			}
	
			infoBoardTypes.add(infoBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}	
	
	public void updateBatchBoardComponent(String env,
				 								  String type,
				 								  String title,
				 								  BatchBoardType batchBoardType) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			// replace the component
			BatchBoardComponent batchBoardComponent = createBatchBoardComponent(batchBoardType);
			
			Collection<BatchBoardComponent> batchBoardComponents = components.getBatchBoardComponents();
			BatchBoardComponent oldCpt = null;
			for(BatchBoardComponent cpt : batchBoardComponents) {
				if (cpt.getComponentId().equals(batchBoardType.getId())) {
					oldCpt = cpt;
				}
			}
			
			components.replaceBatchBoardComponent(oldCpt, batchBoardComponent);
			
//			Collection<Long> ikrDefinitionIds = oldCpt.getIkrDefintionIds();
//			for (Long ikrDefinitionId : ikrDefinitionIds) {
//				ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, oldCpt);
//			}
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<BatchBoardType> batchBoardTypes = dashBoard.getBatchBoards().getBatchBoard();
			
			Iterator<BatchBoardType> batchBoardTypesIT = batchBoardTypes.iterator();
			
			while (batchBoardTypesIT.hasNext()) {
				BatchBoardType tmpBatchBoardType = batchBoardTypesIT.next();
				if (tmpBatchBoardType.getId().equals(batchBoardType.getId())) {
					batchBoardTypesIT.remove();
					break;
				}
			}
	
			batchBoardTypes.add(batchBoardType);	
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}	
	
	public void removeAlertBoardComponent(String env,
			  							  String type,
			  							  String title,
			  							  String componentId) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
		
			// replace the component
	
			Collection<AlertBoardComponent> alertBoardComponents = components.getAlertBoardComponents();
			AlertBoardComponent oldCpt = null;
			for(AlertBoardComponent cpt : alertBoardComponents) {
				if (cpt.getComponentId().equals(componentId)) {
					oldCpt = cpt;
				}
			}
			
			components.removeAlertBoardComponent(oldCpt);
			realTimeRender.unSubscribeComputableComponent(oldCpt);
			
			// Update the summary
			DashBoardSummaryComponent summary = components.getSummary();
			summary.updateAlertBoardComponents(components.getAlertBoardComponents());
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<AlertBoardType> alertBoardTypes = dashBoard.getAlertBoards().getAlertBoard();
			
			Iterator<AlertBoardType> alertBoardTypesIT = alertBoardTypes.iterator();
			
			while (alertBoardTypesIT.hasNext()) {
				AlertBoardType tmpAlertBoardType = alertBoardTypesIT.next();
				if (tmpAlertBoardType.getId().equals(componentId)) {
					alertBoardTypesIT.remove();
					break;
				}
			}
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void removeAlertBoardGridComponent(String env,
											  String type,
											  String title,
											  String componentId) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
		
			// replace the component
	
			Collection<AlertBoardGridComponent> alertBoardGridComponents = components.getAlertBoardGridComponents();
			AlertBoardGridComponent oldCpt = null;
			for(AlertBoardGridComponent cpt : alertBoardGridComponents) {
				if (cpt.getComponentId().equals(componentId)) {
					oldCpt = cpt;
				}
			}
			
			components.removeAlertBoardGridComponent(oldCpt);
//			realTimeRender.unSubscribeComputableComponent(oldCpt);
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<AlertBoardGridType> alertBoardGridTypes = dashBoard.getAlertBoardGrids().getAlertBoardGrid();
			
			Iterator<AlertBoardGridType> alertBoardGridTypesIT = alertBoardGridTypes.iterator();
			
			while (alertBoardGridTypesIT.hasNext()) {
				AlertBoardGridType tmpAlertBoardGridType = alertBoardGridTypesIT.next();
				if (tmpAlertBoardGridType.getId().equals(componentId)) {
					alertBoardGridTypesIT.remove();
					break;
				}
			}
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void removeNavigationBoardComponent(String env,
			  								   String type,
			  								   String title,
			  								   String componentId) {
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			// remove the component
			Collection<NavigationBoardComponent> navigationBoardComponents = components.getNavigationBoardComponents();
			NavigationBoardComponent oldCpt = null;
			for(NavigationBoardComponent cpt : navigationBoardComponents) {
				if (cpt.getComponentId().equals(componentId)) {
					oldCpt = cpt;
				}
			}
			
			components.removeNavigationBoardComponent(oldCpt);
			realTimeRender.unSubscribeComputableComponent(oldCpt);
			
			// Update the summary
			DashBoardSummaryComponent summary = components.getSummary();
			summary.updateNavigationBoardComponents(components.getNavigationBoardComponents());	
			
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<NavigationBoardType> navigationtBoardTypes = dashBoard.getNavigationBoards().getNavigationBoard();
			
			Iterator<NavigationBoardType> navigationBoardTypesIT = navigationtBoardTypes.iterator();
			
			while (navigationBoardTypesIT.hasNext()) {
				NavigationBoardType tmpNavigationBoardType = navigationBoardTypesIT.next();
				if (tmpNavigationBoardType.getId().equals(componentId)) {
					navigationBoardTypesIT.remove();
					break;
				}
			}
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}	
	
	public void removeInfoBoardComponent(String env,
			  							 String type,
			  							 String title,
			  							 String componentId) {	
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
		
			// replace the component
			Collection<InfoComponent> infoComponents = components.getInfoBoardComponents();
			InfoComponent oldCpt = null;
			for(InfoComponent cpt : infoComponents) {
				if (cpt.getComponentId().equals(componentId)) {
					oldCpt = cpt;
				}
			}
			
			components.removeInfoBoardComponent(oldCpt);
			Collection<Long> ikrDefinitionIds = oldCpt.getIkrDefintionIds();
			for (Long ikrDefinitionId : ikrDefinitionIds) {
				ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, oldCpt);
			}
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<InfoBoardType> infoBoardTypes = dashBoard.getInfoBoards().getInfoBoard();
			
			Iterator<InfoBoardType> infoBoardTypesIT = infoBoardTypes.iterator();
			
			while (infoBoardTypesIT.hasNext()) {
				InfoBoardType tmpInfoBoardType = infoBoardTypesIT.next();
				if (tmpInfoBoardType.getId().equals(componentId)) {
					infoBoardTypesIT.remove();
					break;
				}
			}
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void removeBatchBoardComponent(String env,
		 								  String type,
		 								  String title,
		 								  String componentId) {	
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
		
			// replace the component
			Collection<BatchBoardComponent> batchBoardComponents = components.getBatchBoardComponents();
			BatchBoardComponent oldCpt = null;
			for(BatchBoardComponent cpt : batchBoardComponents) {
				if (cpt.getComponentId().equals(componentId)) {
					oldCpt = cpt;
				}
			}
			
			components.removeBatchBoardComponent(oldCpt);
			Collection<Long> ikrDefinitionIds = oldCpt.getIkrDefintionIds();
			for (Long ikrDefinitionId : ikrDefinitionIds) {
				ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, oldCpt);
			}
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<BatchBoardType> batchBoardTypes = dashBoard.getBatchBoards().getBatchBoard();
			
			Iterator<BatchBoardType> batchBoardTypesIT = batchBoardTypes.iterator();
			
			while (batchBoardTypesIT.hasNext()) {
				BatchBoardType tmpBatchBoardType = batchBoardTypesIT.next();
				if (tmpBatchBoardType.getId().equals(componentId)) {
					batchBoardTypesIT.remove();
					break;
				}
			}
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
	
	public void removeChartComponent(String env,
			  								   String type,
			  								   String title,
			  								   String componentId) {	
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
		
			// replace the component			
			Collection<IkrChartComponent> chartComponents = components.getIkrChartComponents();
			IkrChartComponent oldCpt = null;
			for(IkrChartComponent cpt : chartComponents) {
				if (cpt.getComponentId().equals(componentId)) {
					oldCpt = cpt;
				}
			}
			
			components.removeChartComponent(oldCpt);
			Collection<Long> ikrDefinitionIds = oldCpt.getIkrDefinitionIds();
			for (Long ikrDefinitionId : ikrDefinitionIds) {
				ikrValueRTListener.unSubscribeIkrDefinitionComponent(ikrDefinitionId, oldCpt);
			}
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			List<DefinitionChartType> definitionChartTypes = dashBoard.getCharts().getDefinitionChart();
			
			Iterator<DefinitionChartType> definitionChartTypesIT = definitionChartTypes.iterator();
			
			while (definitionChartTypesIT.hasNext()) {
				DefinitionChartType tmpDefinitionChartType = definitionChartTypesIT.next();
				if (tmpDefinitionChartType.getId().equals(componentId)) {
					definitionChartTypesIT.remove();
					break;
				}
			}	
			
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}		
		
	}
	
	public void removeThreadComponent(String env,
			   						  String type,
			   						  String title) {	

		
		w.lock();
		try {
			String dashBoardKey = getDashBoardKey(env, type, title);
			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
			
			ThreadAnalysisBean oldThreadComponent = components.getThreadAnalysisComponent();
			
			ThreadDetailComponent detailComponent = oldThreadComponent.getThreadDetailComponent();
			realTimeRender.unSubscribeComputableComponent(detailComponent);		
			for (int ikrCategoryId : detailComponent.getIkrCategoryIds()) {
				ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, detailComponent);
			}	
			
			ThreadMethodComponent methodComponent = oldThreadComponent.getThreadMethodComponent();
			realTimeRender.unSubscribeComputableComponent(methodComponent);		
			for (int ikrCategoryId : methodComponent.getIkrCategoryIds()) {
				ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, methodComponent);
			}	
			
			ThreadStatusComponent statusComponent = oldThreadComponent.getThreadStatusComponent();
			realTimeRender.unSubscribeComputableComponent(statusComponent);		
			for (int ikrCategoryId : statusComponent.getIkrCategoryIds()) {
				ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, statusComponent);
			}
			
			ThreadTypeComponent typeComponent = oldThreadComponent.getThreadTypeComponent();
			realTimeRender.unSubscribeComputableComponent(typeComponent);		
			for (int ikrCategoryId : typeComponent.getIkrCategoryIds()) {
				ikrValueRTListener.unSubscribeIkrCategoryComponent(ikrCategoryId, typeComponent);
			}			
			
			components.removeThreadComponent();
			
			// XML save
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			dashBoard.setThread(null);
			xmlComponentManager.saveDashBoardSchema(dashBoard);
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}		
	
//	public void removeCategoryChartComponent(String env,
//			  							     String type,
//			  							     String title,
//			  							     String componentId) {	
//		w.lock();
//		try {
//			String dashBoardKey = getDashBoardKey(env, type, title);
//			DashBoardComponents components = dashBoardComponentMap.get(dashBoardKey);
//			
//			// replace the component			
//			Collection<IkrCategoryChartComponent> categoryChartComponents = components.getIkrCategoryChartComponents();
//			IkrCategoryChartComponent oldCpt = null;
//			for(IkrCategoryChartComponent cpt : categoryChartComponents) {
//				if (cpt.getComponentId().equals(componentId)) {
//					oldCpt = cpt;
//				}
//			}
//			
//			components.removeCategoryChartComponent(oldCpt);
//			
//			Collection<Long> ikrIds = oldCpt.getIkrDefinitionIds();
//			for (Long id : ikrIds) {
//				ikrValueRTListener.unSubscribeIkrCategoryComponent(id.intValue(), oldCpt);
//			}
//			
//			// XML save
//			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
//			
//			List<CategoryChartType> categoryChartTypes = dashBoard.getCharts().getCategoryChart();
//			
//			Iterator<CategoryChartType> categoryChartTypesIT = categoryChartTypes.iterator();
//			
//			while (categoryChartTypesIT.hasNext()) {
//				CategoryChartType tmpCategoryChartType = categoryChartTypesIT.next();
//				if (tmpCategoryChartType.getId().equals(componentId)) {
//					categoryChartTypesIT.remove();
//					break;
//				}
//			}
//
//			xmlComponentManager.saveDashBoardSchema(dashBoard);
//		} catch (Exception exc) {
//			logger.error(exc);
//		} finally {
//			w.unlock();
//		}		
//		
//	}		
	
	public void modifyComponentsDisplay(String env,
										String type,
										String title,
										Map<DashBoardComponent, String> newStyles) {
		
		w.lock();
		try {
			DashBoard dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			
			for(DashBoardComponent component : newStyles.keySet()) {
				String newStyle = newStyles.get(component);
				//System.out.println("new style:" + newStyle);
				
	        	Pattern p = Pattern.compile("[pt]:\\d*");
	        	Matcher m = p.matcher(newStyle);
	        	
	        	boolean b = false;
	        	String leftStr = null;
	        	String topStr = null;
	        	while (b = m.find()) {
	        		       		
	        		if (m.group().startsWith("p")) {
	        			topStr = m.group().substring(2);
	        			//System.out.println("top : " + topStr);
	        		}	
	        		
	        		if (m.group().startsWith("t")) {
	        			leftStr = m.group().substring(2);
	        			//System.out.println("left : " + leftStr);
	        		}
	        	}
	        	
	        	String oldStyle = component.getStyle();
	        	//System.out.println("old style:" + component.getStyle());
	        	//System.out.println("new style:" + newStyle);
	        	newStyle = oldStyle.replaceFirst("top:\\d*", "top:"+topStr).replaceFirst("left:\\d*", "left:"+leftStr);
	        	//System.out.println("new style:" + newStyle);
	        	
	        	component.setStyle(newStyle);
	        	
	        	int x = Integer.parseInt(leftStr);
	        	int y = Integer.parseInt(topStr);
	        	
	        	if (component instanceof AlertBoardComponent) {
	        		List<AlertBoardType> alertBoards = dashBoard.getAlertBoards().getAlertBoard();
	    			for (AlertBoardType alertBoardType : alertBoards) {
	    				if (alertBoardType.getId().equals(component.getComponentId())) {
	    					alertBoardType.setX(x);
	    					alertBoardType.setY(y);
	    					break;
	    				}
	    			}
	        	} else if (component instanceof AlertBoardGridComponent) {
	        		List<AlertBoardGridType> alertBoardGridTypes = dashBoard.getAlertBoardGrids().getAlertBoardGrid();
	        		for (AlertBoardGridType alertBoardGridType : alertBoardGridTypes) {
	        			if (alertBoardGridType.getId().equals(component.getComponentId())) {
	        				alertBoardGridType.setX(x);
	        				alertBoardGridType.setY(y);
	        			}
	        		}
	        	} else if (component instanceof NavigationBoardComponent) {
	        		List<NavigationBoardType> navigationBoardTypes = dashBoard.getNavigationBoards().getNavigationBoard();
	        		for (NavigationBoardType navigationBoardType : navigationBoardTypes) {
	        			if (navigationBoardType.getId().equals(component.getComponentId())) {
	        				navigationBoardType.setX(x);
	        				navigationBoardType.setY(y);
	        			}
	        		}
	        	} else if (component instanceof InfoComponent) {
	        		List<InfoBoardType> infoBoardTypes = dashBoard.getInfoBoards().getInfoBoard();
	        		for (InfoBoardType infoBoardType : infoBoardTypes) {
	        			if (infoBoardType.getId().equals(component.getComponentId())) {
	        				infoBoardType.setX(x);
	        				infoBoardType.setY(y);
	        			}
	        		}
//	        	} else if (component instanceof IkrCategoryChartComponent) {
//	        		List<CategoryChartType> chartTypes = dashBoard.getCharts().getCategoryChart();
//	        		for (CategoryChartType chartType : chartTypes) {
//	        			if (chartType.getId().equals(component.getComponentId())) {
//	        				chartType.setX(x);
//	        				chartType.setY(y);
//	        			}
//	        		}
	        	} else if (component instanceof BatchBoardComponent) {
	        		List<BatchBoardType> batchBoardTypes = dashBoard.getBatchBoards().getBatchBoard();
	        		for (BatchBoardType batchBoardType : batchBoardTypes) {
	        			if (batchBoardType.getId().equals(component.getComponentId())) {
	        				batchBoardType.setX(x);
	        				batchBoardType.setY(y);
	        			}
	        		}
	        	} else if (component instanceof IkrChartComponent) {
	        		((IkrChartComponent)component).setXpos(x);
	        		((IkrChartComponent)component).setYpos(y);
	        		List<DefinitionChartType> chartTypes = dashBoard.getCharts().getDefinitionChart();
	        		for (DefinitionChartType chartType : chartTypes) {
	        			if (chartType.getId().equals(component.getComponentId())) {
	        				chartType.setX(x);
	        				chartType.setY(y);
	        			}
	        		}
	        	} else if (component instanceof ThreadAnalysisBean ||
	        			   component instanceof ThreadComponent) {
	        		ThreadType threadType = dashBoard.getThread();
	        		
	        		String componentId = component.getComponentId();
	        		
	        		if (componentId.equals("thread-stackTrace")) {
	        			StackTraceType stackTraceType = threadType.getStackTrace();
	        			stackTraceType.setX(x);
	        			stackTraceType.setY(y);
	        		} else if (componentId.equals("thread-status")) {
	        			StatusType statusType = threadType.getStatus();
	        			statusType.setX(x);
	        			statusType.setY(y);
	        		} else if (componentId.equals("thread-method")) {
	        			MethodType methodType = threadType.getMethod();
	        			methodType.setX(x);
	        			methodType.setY(y);
	        		} else if (componentId.equals("thread-type")) {
	        			TypeType typeType = threadType.getType();
	        			typeType.setX(x);
	        			typeType.setY(y);
	        		} else if (componentId.equals("thread-detail")) {
	        			DetailType detailType = threadType.getDetail();
	        			detailType.setX(x);
	        			detailType.setY(y);
	        		}
	        	}
	        	xmlComponentManager.saveDashBoardSchema(dashBoard);
			} 
		} catch (Exception exc) {
			logger.error(exc);
		} finally {
			w.unlock();
		}
	}
//			if (component instanceof NavigationBoardComponent) {
//				nodeLabel = ComponentType.navigationBoard.name();
//			} else if (component instanceof AlertBoardComponent) {
//				nodeLabel = ComponentType.alertBoard.name();
//			} else if (component instanceof InfoComponent) {
//				nodeLabel = ComponentType.infoBoard.name();
//			} else if (component instanceof IkrCategoryChartComponent) {
//				nodeLabel = ComponentType.categoryGraph.name();
//			} else if (component instanceof IkrDefinitionChartComponent) {
//				nodeLabel = ComponentType.definitionGraph.name();
//			} else if (component instanceof ThreadComponent || 
//					   component instanceof ThreadAnalysisBean) {
//				nodeLabel = ComponentType.thread.name();
//			}
			
			
//			NodeList nodes = document.getElementsByTagName(nodeLabel);
//			for (int j=0; j<nodes.getLength();j++) {
//				Element element = (Element)nodes.item(j);
//				String elementId = element.getAttribute("id");
//				if (elementId == null || elementId.length() == 0) {
//					elementId = nodeLabel + j;
//				}
//				
//				if (component.getComponentId().equals(elementId)) {
//					element.setAttribute("x", leftStr);
//		        	element.setAttribute("y", topStr);
//					break;
//				}
//				
//				// Thread is a special case
//				if (nodeLabel == ComponentType.thread.name() &&
//					component.getComponentId().contains(elementId)) {
//					
//					String threadComponentLabel = component.getComponentId().split("-")[1];
//					element.setAttribute(threadComponentLabel+"x", leftStr);
//		        	element.setAttribute(threadComponentLabel+"y", topStr);
//					break;
//					
//				}
//			}
		
		
//	    try {       	
//	    	Source src = new DOMSource(document);
//				
//			String docPath = document.getDocumentURI().replace("file:", "").replace("%20", " ");
//		    Result result = new StreamResult(docPath);
//	
//		    Transformer xformer = TransformerFactory.newInstance().newTransformer();
//		    xformer.transform(src,result);
//		        
//	   } catch(Exception exc) {
//	        System.out.println(exc);
//	   }	

	private String getDashBoardKey(String env,
								   String type,
								   String title) {
		return env + '.' + type + '.' + title;
	}

	private void createDashBoardComponents(String env,
										   String type,
										   String title) {
		
		if (getDashBoardComponents(env,type,title) != null) {
			// This DashBoard has already been created 
			return;
		}
		
		try {
			
			DashBoard dashBoard = null;
			
			try {
				dashBoard = xmlComponentManager.getDashBoardSchema(env, type, title);
			} catch (Exception exc) {
				logger.fatal("Impossible to get Dashboard schema for env="+env+",type="+type+",title="+title,exc);
			}				
				
			DashBoardComponents dashBoardComponents = new DashBoardComponents(); 
			
			// create navigationBoards
			NavigationBoardsType navigationBoardsType = dashBoard.getNavigationBoards();
			if (navigationBoardsType != null) {
				List<NavigationBoardType> navigationBoards = navigationBoardsType.getNavigationBoard();
				for(NavigationBoardType navigationBoard : navigationBoards) {
					NavigationBoardComponent navigationBoardComponent = createNavigationBoardComponent(navigationBoard);
					dashBoardComponents.addNavigationBoardComponent(navigationBoardComponent);
					realTimeRender.subscribeComputableComponent(navigationBoardComponent);
				}
			}

			// create alertBoards
			AlertBoardsType alertBoardsType = dashBoard.getAlertBoards();
			if (alertBoardsType != null) {
				List<AlertBoardType> alertBoards = alertBoardsType.getAlertBoard();
				for (AlertBoardType alertBoardType : alertBoards) {
					AlertBoardComponent alertBoardComponent = createAlertBoardComponent(alertBoardType);
					dashBoardComponents.addAlertBoardComponent(alertBoardComponent);
					realTimeRender.subscribeComputableComponent(alertBoardComponent);
				}
			}
			
			// create alertBoardGrids
			AlertBoardGridsType alertBoardGridsType = dashBoard.getAlertBoardGrids();
			if (alertBoardGridsType != null) {
				List<AlertBoardGridType> alertBoardGrids = alertBoardGridsType.getAlertBoardGrid();
				for (AlertBoardGridType alertBoardGridType : alertBoardGrids) {
					AlertBoardGridComponent alertBoardGridComponent = createAlertBoardGridComponent(alertBoardGridType);
					dashBoardComponents.addAlertBoardGridComponent(alertBoardGridComponent);
				}
			}
			
			// create infoBoards
			InfoBoardsType infoBoardsType = dashBoard.getInfoBoards();
			if (infoBoardsType != null) {
				List<InfoBoardType> infoBoardTypes = infoBoardsType.getInfoBoard();
				for (InfoBoardType infoBoardType : infoBoardTypes) {
					InfoComponent infoBoardComponent = createInfoComponent(infoBoardType);
					dashBoardComponents.addInfoComponent(infoBoardComponent);
				}
			}
			
			// create batchBoards
			BatchBoardsType batchBoardsType = dashBoard.getBatchBoards();
			if (batchBoardsType != null) {
				List<BatchBoardType> batchBoardTypes = batchBoardsType.getBatchBoard();
				for (BatchBoardType batchBoardType : batchBoardTypes) {
					BatchBoardComponent batchBoardComponent = createBatchBoardComponent(batchBoardType);
					dashBoardComponents.addBatchBoardComponent(batchBoardComponent);
				}
			}

			ChartsType chartsType = dashBoard.getCharts();
			if (chartsType != null) {
				// create categoryCharts
//				List<CategoryChartType> categoryChartTypes = chartsType.getCategoryChart();
//				if (categoryChartTypes != null) {
//					for (CategoryChartType categoryChartType : categoryChartTypes) {
//						IkrCategoryChartComponent categoryChartComponent = 
//							createCategoryChartComponent(categoryChartType);
//						dashBoardComponents.addIkrCategoryChartComponent(categoryChartComponent);
//						realTimeRender.subscribeComputableComponent(categoryChartComponent);
//					}
//					
//				}
				
				// create definitionCharts
				List<DefinitionChartType> definitionChartTypes = chartsType.getDefinitionChart();
				if (definitionChartTypes != null) {
					for (DefinitionChartType definitionChartType : definitionChartTypes) {
						IkrChartComponent definitionChartComponent = 
							createChartComponent(definitionChartType);
						dashBoardComponents.addIkrChartComponent(definitionChartComponent);
						realTimeRender.subscribeComputableComponent(definitionChartComponent);
					}
				}
			}	
			
			// create threads
			ThreadType threadType = dashBoard.getThread();
			if (threadType != null) {
				ThreadAnalysisBean threadAnalysisComponent = createThreadComponent(threadType);
				dashBoardComponents.setThreadAnalysisComponent(threadAnalysisComponent);
			}

			DashBoardSummaryComponent summary = 
					new DashBoardSummaryComponent("summary",
												  title,
												  "n/a",
												  env,
												  type,
												  dashBoardComponents.getAlertBoardComponents(),
												  dashBoardComponents.getNavigationBoardComponents());
			
			dashBoardComponents.setSummary(summary);
			realTimeRender.subscribeComputableComponent(summary);

			String dashBoardKey = getDashBoardKey(env, type, title);
			dashBoardComponentMap.put(dashBoardKey, dashBoardComponents);
			
		} catch(Exception exc) {
			logger.error("Error while creating DashBoardComponents : " + env + " " + type + " " + title, exc);
		}
	}
	
//	private DashBoardSummaryComponent createDashBoardSummary(String boardEnv,
//															 String boardType,
//															 String boardTitle,
//												    		 Collection<AlertBoardComponent> alertBoardComponents) {
//		String style = "n/a";
//		
//		DashBoardSummaryComponent summary = new DashBoardSummaryComponent("summary",
//																		  boardTitle,
//																		  style,
//																		  boardEnv,
//																		  boardType,
//																		  alertBoardComponents);		
//		return summary;
//	}
	
	private NavigationBoardComponent createNavigationBoardComponent(NavigationBoardType navigationBoardType) {
		
		NavigationBoardComponent res = null;
		
		try {
			
				String navigationBoardId = navigationBoardType.getId();
				String navigationBoardTitle = navigationBoardType.getTitle();
				
				int x = navigationBoardType.getX();
				int y = Math.max(navigationBoardType.getY(),RealTimeComponentFactory.Y_MIN);
			
				String style = "left:" + x + "px;top:" + y + "px;position:absolute;";
	
				res = new NavigationBoardComponent(navigationBoardId,
												   navigationBoardTitle,
												   style);
		
				List<NavigationItemType> navigationItems = navigationBoardType.getNavigationItem();
				
				for(NavigationItemType navigationItem : navigationItems) {
					String env = navigationItem.getEnv();
					String type = navigationItem.getType();
					String title = navigationItem.getTitle();
					
					DashBoardComponents componentList = getDashBoardComponents(env, type, title);
					
					if (componentList == null) {
						// this board has not been created yet
						createDashBoardComponents(env, type, title);
						componentList = getDashBoardComponents(env, type, title);
					} 
					
					DashBoardSummaryComponent summary = componentList.getSummary();
					res.addDashBoardSummary(summary);
				}
	
		} catch (Exception exc) {
			logger.error("Error in NavigationBoard configuration",exc);
		}
			
		return res;
	}
	
	private AlertBoardComponent createAlertBoardComponent(AlertBoardType alertBoardType) {
		AlertBoardComponent res = null;
		
		List<AlertItemType> alertItems = alertBoardType.getAlertItem();
		List<AlertLeaf> alertLeafs = new ArrayList<AlertLeaf>(alertItems.size());
		
		for(AlertItemType alertItem : alertItems) {
				
			String itemTitle = alertItem.getTitle();
			String itemType =  alertItem.getType();
			
			List<AlertType> alerts = alertItem.getAlert();
			List<AlertBean> alertBeans = new ArrayList<AlertBean>();			
			
			for (AlertType alert : alerts) {
				String env = alert.getEnv();
				String labelFilter = alert.getLabel();				

				Collection<Long> alertDefinitionIds = alertPM.getAlertDefinitionIdsByLabelAndEnv(labelFilter, env);
				
				if  (alertDefinitionIds != null) {	
					for (long alertDefinitionId : alertDefinitionIds) {
						AlertBean alertBean = alertRTListener.getAlertBean(alertDefinitionId);
						alertBeans.add(alertBean);
					}
				}
			}

			AlertLeaf leaf = new AlertLeaf(itemTitle,itemType,alertBeans);
			alertLeafs.add(leaf);
		}
			
		
		String alertCompositeTitle = "n/a";
		String type = alertBoardType.getType();
		AlertComposite alertComposite = new AlertComposite(alertCompositeTitle,
														   type,
														   alertLeafs);

		try {
			String alertBoardId = alertBoardType.getId();
			String alertBoardTitle = alertBoardType.getTitle();
				
			int x = alertBoardType.getX();
			int y = Math.max(alertBoardType.getY(),RealTimeComponentFactory.Y_MIN);
				
			String alertBoardStyle = "left:" + x + "px;top:" + y + "px;position:absolute;";

			res = new AlertBoardComponent(alertBoardId,
										  alertBoardTitle,
										  alertBoardStyle,
										  alertComposite);
		} catch (Exception exc) {
				logger.error(exc);
		}
		
		return res;
	}
	
	private AlertBoardGridComponent createAlertBoardGridComponent(AlertBoardGridType alertBoardGridType) {
		AlertBoardGridComponent res = null;
		String alertBoardGridId = alertBoardGridType.getId();
		String alertBoardGridTitle = alertBoardGridType.getTitle();
		List<String> AlertBoardGridLogicalEnvs = new ArrayList<String>();
		boolean upOn = alertBoardGridType.isUp();
		boolean downOn = alertBoardGridType.isDown();
		boolean ackOn = alertBoardGridType.isAck();
		boolean lowOn = alertBoardGridType.isLow();
		boolean mediumOn = alertBoardGridType.isMedium();
		boolean highOn = alertBoardGridType.isHigh();
		boolean notRunningOn = alertBoardGridType.isNotRunning();
				
		int x = alertBoardGridType.getX();
		int y = Math.max(alertBoardGridType.getY(),RealTimeComponentFactory.Y_MIN);
		
		String style = "left:" + x + "px;top:" + y + "px;position:absolute;";
		
		List<AlertGridItemType> alertGridItems = alertBoardGridType.getAlertGridItem();
		for(AlertGridItemType alertGridItem : alertGridItems) {
			AlertBoardGridLogicalEnvs.add(alertGridItem.getEnv());
		}
		
		res = new AlertBoardGridComponent(alertBoardGridId,alertBoardGridTitle,style);
		res.setInfo(AlertBoardGridLogicalEnvs, upOn, downOn, ackOn, lowOn, mediumOn, highOn, notRunningOn);
				
		return res;
	}
	
	private InfoComponent createInfoComponent(InfoBoardType infoBoardType) {
		InfoComponent res = null;
		
		try { 
		
			String infoBoardId = infoBoardType.getId();
			String infoBoardTitle = infoBoardType.getTitle();
			
			int x = infoBoardType.getX();
			int y = Math.max(infoBoardType.getY(),RealTimeComponentFactory.Y_MIN);
			
			String style = "left:" + x + "px;top:" + y + "px;position:absolute;";
	
			res = new InfoComponent(infoBoardId,
									infoBoardTitle,
									style,
									beanPM);
			List<InfoItemType> infoItemTypes = infoBoardType.getInfoItem();
			
			for(InfoItemType infoItemType : infoItemTypes) {
				String ikrCategoryName = infoItemType.getIkrCategoryValue();
				String ikrInstance = infoItemType.getIkrInstance();
				String context = infoItemType.getContext();
				String logicalEnvName = infoItemType.getLogicalEnv();
				try {
					IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomainByValue(ikrCategoryName);
					if (ikrCategory != null) {
						LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(logicalEnvName);
						if (logicalEnv == null) {
							logger.error("LogicalEnv name not recognized: " + logicalEnvName);
							return null;
						}
						Map<Long, AbstractIkrDefinition> definitions = monitoringPM.getIkrDefinitions(logicalEnv.getId(),context,ikrCategory.getId());
						AbstractIkrDefinition ikrDefinition = null;
						
						for (AbstractIkrDefinition definition : definitions.values()) {
							if (definition.getFullIkrInstance().equals(ikrInstance)) {
								ikrDefinition = definition;
								break;
							}
						}
						if (ikrDefinition != null) {
							long ikrDefinitionId = ikrDefinition.getId();							
							res.setInfo(ikrDefinitionId, infoItemType.getLabel(), monitoringPM, dataModelPM);
							ikrValueRTListener.subscribeIkrDefinitionComponent(ikrDefinitionId, res);
						} else {
							logger.error("IkrDefinition name not recognized: " + ikrInstance);
						}
					} else {
						logger.error("MetricCategory name not recognized: " + ikrCategoryName);
					}
				} catch (PersistenceException exc) {
					logger.error(exc);
				}
				
			}
		} catch(Exception exc) {
			logger.error(exc);
		}
		
		return res;
	}
	
	private BatchBoardComponent createBatchBoardComponent(BatchBoardType batchBoardType) {
		BatchBoardComponent res = null;
		String batchBoardId = batchBoardType.getId();
		String batchBoardTitle = batchBoardType.getTitle();
		String batchBoardLogicalEnv = batchBoardType.getLogicalEnv();
		String batchBoardContext = batchBoardType.getContext();
		Boolean batchBoardAutoDiscovery = batchBoardType.isAutoDiscovery();
		
		int x = batchBoardType.getX();
		int y = Math.max(batchBoardType.getY(),RealTimeComponentFactory.Y_MIN);
		
		String style = "left:" + x + "px;top:" + y + "px;position:absolute;";
		
		boolean error = false;
		LogicalEnv logicalEnv = null;
		Map<Long, AbstractIkrDefinition> ikrDefinitions = null;
		try {
			logicalEnv = dataModelPM.getLogicalEnv(batchBoardLogicalEnv);
			if (logicalEnv == null) {
				logger.error("LogicalEnv name not recognized: " + batchBoardLogicalEnv);
				return null;
			}
			long monitorConfigId = 0;
			Map<Long, MonitorConfig> monitorConfigs = dataModelPM.getMonitorConfigs(logicalEnv.getId());
			for (MonitorConfig monitorConfig : monitorConfigs.values()) {
				if(monitorConfig.getContext().equals(batchBoardContext)) {
					monitorConfigId = monitorConfig.getId();
					break;
				}
			}				
			ikrDefinitions = monitoringPM.getIkrDefinitions(monitorConfigId);
		}
		catch (PersistenceException exc) {
			logger.error(exc);
			error = true;
			return null;
		}			

		res = new BatchBoardComponent(batchBoardId,batchBoardTitle,style);
			
		if(batchBoardAutoDiscovery) {
			Set<String> insts = new HashSet<String>();
			for (AbstractIkrDefinition ikrDefinition : ikrDefinitions.values()) {
				String ikrInstance = ikrDefinition.getIkrInstance();
				if (!insts.contains(ikrInstance)) {
					insts.add(ikrInstance);
					res.setInfo(logicalEnv.getId(), batchBoardContext, ikrInstance, null);					
				}
				ikrValueRTListener.subscribeIkrDefinitionComponent(ikrDefinition.getId(), res);
				res.addIkrdefinitionId(ikrDefinition.getId());
			}
			insts = null;
		}
		else {
			Map<String, List<AbstractIkrDefinition>> instanceDefinitions = new HashMap<String, List<AbstractIkrDefinition>>();
			for (AbstractIkrDefinition ikrDefinition : ikrDefinitions.values()) {
				String ikrInstance = ikrDefinition.getIkrInstance();
				List<AbstractIkrDefinition> defs = instanceDefinitions.get(ikrInstance);
				if (defs == null) {
					defs = new ArrayList<AbstractIkrDefinition>();
					instanceDefinitions.put(ikrInstance, defs);
				}
				defs.add(ikrDefinition);
			}
			List<BatchItemType> batchItemTypes = batchBoardType.getBatchItem();				
			for(BatchItemType batchItemType : batchItemTypes) {					
				String ikrInstance = batchItemType.getIkrInstance();
				String label = batchItemType.getLabel();				
				List<AbstractIkrDefinition> defs = instanceDefinitions.get(ikrInstance);
				if (defs!=null&&defs.size()>0) {
					res.setInfo(logicalEnv.getId(), batchBoardContext, ikrInstance, label);	
					for (AbstractIkrDefinition def : defs) {
						ikrValueRTListener.subscribeIkrDefinitionComponent(def.getId(), res);
						res.addIkrdefinitionId(def.getId());
					}
				}
			}
		} 
		return res;
	}
	
//	private IkrCategoryChartComponent createCategoryChartComponent(CategoryChartType categoryChartType) {
//		
//		IkrCategoryChartComponent res = null;
//		
//		try {
//			String chartId = categoryChartType.getId();			
//			String chartTitle = categoryChartType.getTitle();
//			String chartType = categoryChartType.getType();
//			int maxSlot = categoryChartType.getMaxSlot();
//			
//			int x = categoryChartType.getX();
//			int y = Math.max(categoryChartType.getY(),RealTimeComponentFactory.Y_MIN);
//
//			int width = categoryChartType.getWidth();
//			int height = categoryChartType.getHeight();
//
//			String style = "left:" + x + "px;top:" + y + "px;position:absolute";			
//			
//			Collection<MetricGroupBean> metricBeans = new ArrayList<MetricGroupBean>();
//						
//			List<MetricType> metricTypes = categoryChartType.getMetric();
//			for (MetricType metricType : metricTypes) {
//				String ikrCategoryName = metricType.getIkrCategoryValue();
//				String logicalEnvName = metricType.getLogicalEnv();
//				String context = metricType.getContext();
//				String domainView = metricType.getDomainView();
//				try {
//					IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomainByValue(ikrCategoryName);
//					if (ikrCategory == null) {
//						logger.error("IkrCategory value not recognized: " + ikrCategoryName);
//						continue;
//					}
//					LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(logicalEnvName);
//					if (logicalEnv == null) {
//						logger.error("LogicalEnv name not recognized: " + logicalEnvName);
//						continue;
//					}
//					
//					MetricGroupBean metricBean = new MetricGroupBean(ikrCategory, context, logicalEnv,domainView);
//					metricBeans.add(metricBean);
//				} catch(Exception exc) {
//					logger.error(exc);
//				}
//			}
//			
//			res = new IkrCategoryChartComponent(chartId, chartTitle, metricBeans, style, width, height, maxSlot, chartType, dataModelPM);
//			res.setProxy(appletProxy);
//			res.setRmiPort(appletProxy.getPort());
//			res.setRmiHostname(appletProxy.getHostname());
//			for (MetricGroupBean metricBean : metricBeans) {
//				ikrValueRTListener.subscribeIkrCategoryComponent(metricBean.getIkrCategory().getId(), res);
//			}		
//		} catch (Exception exc) {
//			logger.error(exc);
//		}
//		return res;
//	}
	
	private IkrChartComponent createChartComponent(DefinitionChartType definitionChartType) {

		IkrChartComponent res = null;
		
		try {
			String chartId = definitionChartType.getId();			
			String chartTitle = definitionChartType.getTitle();
			int maxSlot = definitionChartType.getMaxSlot();
			
			int x = definitionChartType.getX();
			int y = Math.max(definitionChartType.getY(),RealTimeComponentFactory.Y_MIN);

			int width = definitionChartType.getWidth();
			int height = definitionChartType.getHeight();	
//			int width = 390;
//			int height = 300;
			
			String style = "left:" + x + "px;top:" + y + "px;position:absolute;";
			
			res = new IkrChartComponent(chartId , chartTitle, x, y, width, height, maxSlot, definitionChartType.getType(), beanPM);
//			res.setProxy(appletProxy);
//			res.setRmiPort(appletProxy.getPort());
//			res.setRmiHostname(appletProxy.getHostname());
			
			List<DefinitionType> definitionTypes = definitionChartType.getDefinition();
			
			for (DefinitionType definitionType : definitionTypes) {
				String ikrCategoryName = definitionType.getIkrCategoryValue();
				String ikrInstance = definitionType.getIkrInstance();
				String context = definitionType.getContext();
				String logicalEnvName = definitionType.getLogicalEnv();
				
				try {
					IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomainByValue(ikrCategoryName);
					if (ikrCategory != null) {
						LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(logicalEnvName);
						if (logicalEnv == null) {
							logger.error("LogicalEnv name not recognized: " + logicalEnvName);
							continue;
						}
						Map<Long, AbstractIkrDefinition> definitions = monitoringPM.getIkrDefinitions(logicalEnv.getId(),context,ikrCategory.getId());
						AbstractIkrDefinition ikrDefinition = null; 
						
						for (AbstractIkrDefinition definition : definitions.values()) {
							if (definition.getFullIkrInstance().equals(ikrInstance)) {
								ikrDefinition = definition;
								break;
							}
						}
						if (ikrDefinition != null) {
							long ikrDefinitionId = ikrDefinition.getId();
							res.setInfo(ikrDefinition, ikrCategory, definitionType.getLabel(), dataModelPM);
							ikrValueRTListener.subscribeIkrDefinitionComponent(ikrDefinitionId, res);
						} else {
							logger.error("IkrDefinition name not recognized: " + ikrInstance);
						}
					} else {
						logger.error("MetricCategory name not recognized: " + ikrCategoryName);
					}
				} catch (PersistenceException exc) {
					logger.error(exc);
				}
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
		
		return res;
	}
	
	private ThreadAnalysisBean createThreadComponent(ThreadType threadType) {
		ThreadAnalysisBean res = null;
		
		try {
			String processName = threadType.getProcess();
			String hostname = threadType.getHostname();
			
			
			// DETAIL
			DetailType detailType = threadType.getDetail();
			
			int x = detailType.getX();
			int y = Math.max(detailType.getY(),RealTimeComponentFactory.Y_MIN);
			
			String style = "left:" + x + "px;top:" + y + "px;position:absolute;";
			
			String id = "thread-detail";
			
			String title = detailType.getTitle();
			boolean rendered = detailType.isRendered();
			
			ThreadDetailComponent detailComponent = new ThreadDetailComponent(id,
																			  title,
																			  style,
																			  rendered,
																			  processName,
																			  hostname,
																			  dataModelPM);
			detailComponent.setMonitoringPM(monitoringPM);
			
			for (int ikrCategoryId : detailComponent.getIkrCategoryIds()) {
				ikrValueRTListener.subscribeIkrCategoryComponent(ikrCategoryId, detailComponent);
			}			
			realTimeRender.subscribeComputableComponent(detailComponent);
			
			// METHOD
			MethodType methodType = threadType.getMethod();
			
			x = methodType.getX();
			y = Math.max(methodType.getY(),RealTimeComponentFactory.Y_MIN);
			
			style = "left:" + x + "px;top:" + y + "px;position:absolute;";
			
			id = "thread-method";

			title = methodType.getTitle();
			rendered = methodType.isRendered();

			ThreadMethodComponent methodComponent = new ThreadMethodComponent(id,
																			  title,
																			  style,
																			  rendered,
																			  processName,
																			  hostname,
																			  dataModelPM);
			methodComponent.setMonitoringPM(monitoringPM);

			for (int ikrCategoryId : methodComponent.getIkrCategoryIds()) {
				ikrValueRTListener.subscribeIkrCategoryComponent(ikrCategoryId, methodComponent);
			}			
			realTimeRender.subscribeComputableComponent(methodComponent);
			
			// STATUS
			StatusType statusType = threadType.getStatus();
			
			x = statusType.getX();
			y = Math.max(statusType.getY(),RealTimeComponentFactory.Y_MIN);
			
			style = "left:" + x + "px;top:" + y + "px;position:absolute;";
			
			id = "thread-status";

			title = statusType.getTitle();
			rendered = statusType.isRendered();			

			ThreadStatusComponent statusComponent = new ThreadStatusComponent(id, 
																			  title,
																			  style,
																			  rendered,
																			  processName,
																			  hostname,
																			  dataModelPM);
			statusComponent.setMonitoringPM(monitoringPM);

			for (int ikrCategoryId : statusComponent.getIkrCategoryIds()) {
				ikrValueRTListener.subscribeIkrCategoryComponent(ikrCategoryId, statusComponent);
			}			
			realTimeRender.subscribeComputableComponent(statusComponent);
			
			// TYPE
			TypeType typeType = threadType.getType();
			
			x = typeType.getX();
			y = Math.max(typeType.getY(),RealTimeComponentFactory.Y_MIN);
			
			style = "left:" + x + "px;top:" + y + "px;position:absolute;";
			
			id = "thread-type";
		
			title = typeType.getTitle();
			rendered = typeType.isRendered();				

			ThreadTypeComponent typeComponent = new ThreadTypeComponent(id, 
																		title,
																		style,
																		rendered,
																		processName,
																		hostname,
																		dataModelPM);
			typeComponent.setMonitoringPM(monitoringPM);

			for (int ikrCategoryId : typeComponent.getIkrCategoryIds()) {
				ikrValueRTListener.subscribeIkrCategoryComponent(ikrCategoryId, typeComponent);
			}			
			realTimeRender.subscribeComputableComponent(typeComponent);

			// STACK TRACE
			StackTraceType stackTraceType = threadType.getStackTrace();
			
			x = stackTraceType.getX();
			y = Math.max(stackTraceType.getY(),RealTimeComponentFactory.Y_MIN);
			
			style = "left:" + x + "px;top:" + y + "px;position:absolute;";
			
			id = "thread-stackTrace";
			
			title = stackTraceType.getTitle();
			rendered = stackTraceType.isRendered();	
			
			String threadComponentTitle = threadType.getTitle();
			
			res = new ThreadAnalysisBean(id, 
										 title,
										 style,
										 rendered,
										 threadComponentTitle,
										 detailComponent,
										 methodComponent,
										 statusComponent,
										 typeComponent);
		} catch (Exception exc) {
			logger.error("Error when creating thread component",exc);
		}
		
		return res;
	}	
}
