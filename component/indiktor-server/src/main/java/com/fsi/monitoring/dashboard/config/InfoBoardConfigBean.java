package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.InfoBoardType;
import generated.dashboard.config.schema.InfoBoardType.InfoItemType;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.MetricGroupBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.FacesUtils;

public class InfoBoardConfigBean 
extends DashBoardConfigBean {

	private static final long serialVersionUID = 2767684910418822131L;

	private static final Logger logger = Logger.getLogger(InfoBoardConfigBean.class);
	
	private InfoBoardType infoBoard;
	
	private MetricSelectorBean ikrSelectorBean;
	
	private boolean errorInfoListVisible = false;
	private boolean errorInfoBoardTitleVisible = false;
	
	private boolean titleError;
	
	public void init(String env,
					 String type,
					 String title,
					 String componentId) {
		super.init(env, type, title);
		
		try {
			init();			
			
			ComponentXmlManager componentXmlManager = (ComponentXmlManager)FacesUtils.getManagedBean("xmlComponentManager");
			
			DashBoard dashBoard = componentXmlManager.getDashBoardSchema(env, type, title);
			
			List<InfoBoardType> infoBoards = dashBoard.getInfoBoards().getInfoBoard();
			for (InfoBoardType infoBoard : infoBoards) {
				if (infoBoard.getId().equals(componentId)) {
					this.infoBoard = infoBoard;
					break;
				}			
			}
			
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			
			if (infoBoard != null) {
				Collection<InfoItemType> itemTypes = infoBoard.getInfoItem();
				
				for (InfoItemType infoItemType : itemTypes) {

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
								return;
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
								MetricGroupBean metricGroupBean = beanPM.getIkrDefinitionBean(ikrDefinition.getId());
								ModifiableMetricBean bean = new ModifiableMetricBean(metricGroupBean);
					    		bean.setLabel(infoItemType.getLabel());
								bean.updateSelected(false);
								ikrSelectorBean.getSelectedBeans().add(bean);
							} else {
								logger.error("IkrDefinition name not recognized: " + ikrInstance);
								return;
							}
						} else {
							logger.error("MetricCategory name not recognized: " + ikrCategoryName);
							return;
						}
					} catch (PersistenceException exc) {
						logger.error(exc);
					}
				}
				
			} else {
				logger.error("InfoBoard not found when initializing configuration, env=" + env + ",type=" + type + ",title=" + title + ",componentId=" + componentId);
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public void init(String env,
					 String type,
					 String title) {
		try {
			super.init(env, type, title);
			init();
			ikrSelectorBean.setRendered(true);
			
			ObjectFactory objFactory = new ObjectFactory();

			InfoItemType infoItem = objFactory.createInfoBoardTypeInfoItemType();		
			infoBoard = objFactory.createInfoBoardType();
				
			List<InfoItemType> infoItems = infoBoard.getInfoItem();
			infoItems.add(infoItem);			
		} catch (Exception exc) {
			logger.error(exc);
		}			
	}
	
	private void init() {
		ikrSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
		ikrSelectorBean.init();
		ikrSelectorBean.setDashboardMode(true);
		ikrSelectorBean.setSelectedTableScrollWitdh(475);
		ikrSelectorBean.setSelectedTableWitdh(600);
		ikrSelectorBean.setSelectedTableScrollHeight(495);
		ikrSelectorBean.setRendered(true);
	}

	public InfoBoardType getInfoBoard() {
		return infoBoard;
	}
		
	public boolean isErrorInfoListVisible() {
		return errorInfoListVisible;
	}

	public void setErrorInfoListVisible(boolean errorInfoListVisible) {
		this.errorInfoListVisible = errorInfoListVisible;
	}

	public boolean isErrorInfoBoardTitleVisible() {
		return errorInfoBoardTitleVisible;
	}

	public void setErrorInfoBoardTitleVisible(boolean errorInfoBoardTitleVisible) {
		this.errorInfoBoardTitleVisible = errorInfoBoardTitleVisible;
	}

	public void save() {
		Collection<ModifiableMetricBean> beans = ikrSelectorBean.getSelectedBeans();
		
		if (beans.isEmpty()) {
			errorInfoListVisible = true;
//			error = true;
//			message = "Info list cannot be empty";
//			setAction("infoBoardConfig");
			return;
		}
		
		if (infoBoard.getTitle().trim().length() == 0) {
			errorInfoBoardTitleVisible = true;
			errorInfoListVisible = false;
//			error = true;
//			message = "InfoBoard Title cannot be empty";
//			setAction("infoBoardConfig");
			return;	
		}		
		
		errorInfoListVisible = false;
		errorInfoBoardTitleVisible = false;
		
		try {
			ObjectFactory objFactory = new ObjectFactory();
			infoBoard.getInfoItem().clear();
			
			for (ModifiableMetricBean bean : beans) {
				InfoItemType infoItemType = objFactory.createInfoBoardTypeInfoItemType();
				IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)bean.getMetricGroupBean();
				infoItemType.setIkrCategoryValue(ikrDefinitionBean.getIkrCategory().getDomainValue());
				infoItemType.setContext(ikrDefinitionBean.getContext());
				infoItemType.setIkrInstance(ikrDefinitionBean.getIkrDefinition().getFullIkrInstance());
				infoItemType.setLogicalEnv(ikrDefinitionBean.getLogicalEnv().getName());
				infoItemType.setLabel(bean.getLabel());
				infoBoard.getInfoItem().add(infoItemType);
			}

			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
			
			if (infoBoard.getId() == null || infoBoard.getId().length() == 0) {
				factory.addNewInfoBoardComponent(env, type, title, infoBoard);
			} else {
				factory.updateInfoBoardComponent(env, type, title, infoBoard);
			}
			
			DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			bean.resetSelectedDashBoard();
		
		} catch (Exception exc) {
			logger.error("Error while saving Info Board details", exc);
		}
	}

	public String getInfoPanelStyle() {
		if(errorInfoListVisible && !getListRenderer())
			return "border: 1px solid red; text-align: center; width: 475px; height: 495px;";
		else
			return "border: 1px solid #336699; text-align: center; width: 475px; height: 495px;";
	}

	public String getInfoTitleStyle() {
		if(errorInfoBoardTitleVisible || titleError)
			return "border: 1px solid red;";
		else
			return "";
	}
	
	public boolean getListRenderer() {
		if(ikrSelectorBean != null && ikrSelectorBean.getSelectedBeans().size() != 0)
			return true;
		else
			return false;
	}

	public void setTitleError(boolean titleError) {
		this.titleError = titleError;
	}
}
