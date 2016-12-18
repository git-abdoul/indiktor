package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.DashBoardType.ChartsType;
import generated.dashboard.config.schema.DefinitionChartType;
import generated.dashboard.config.schema.DefinitionChartType.DefinitionType;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorVisitor;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.dashboard.component.chart.IkrChartComponent;
import com.fsi.monitoring.dashboard.component.chart.IkrChartType;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.MetricGroupBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;

public class DefinitionChartConfigBean 
extends DashBoardConfigBean implements MetricSelectorVisitor{

	private static final long serialVersionUID = 2767684910418822131L;

	private static final Logger logger = Logger.getLogger(DefinitionChartConfigBean.class);	
	
	private String env;
	private String type;
	private String title;
	
//	private int pMaxSlot;
//	private String strMaxSlot;
	
	private static SelectItem[] typeItems;
	
	private DefinitionChartType definitionChart;
	
	private MetricSelectorBean ikrSelectorBean;	
	
	private boolean errorDefinitionlistVisible = false;
	private boolean errorDefinitionChartTitleVisible = false;
	
	private boolean titleError;
		
	Set<ModifiableMetricBean> selectedMetrics;
	
	static {
    	typeItems = new SelectItem[IkrChartType.values().length];
    	int i = 0;
    	for (IkrChartType chartType : IkrChartType.values()) {
    		typeItems[i] = new SelectItem(chartType.getLabel(), chartType.getLabel());
    		i++;
    	}
    }
	
	public void init(String env,
					 String type,
					 String title,
					 String componentId) {
		this.env = env;
		this.type = type;
		this.title = title;
		
		try{ 
			init();			
			ComponentXmlManager componentXmlManager = (ComponentXmlManager)FacesUtils.getManagedBean("xmlComponentManager");			
			DashBoard dashBoard = componentXmlManager.getDashBoardSchema(env, type, title);			
			ChartsType chartsType = dashBoard.getCharts();
			List<DefinitionChartType> definitionCharts = chartsType.getDefinitionChart();		
			
			for (DefinitionChartType definitionChart : definitionCharts) {
				if (definitionChart.getId().equals(componentId)) {
					this.definitionChart = definitionChart;
					break;
				}
			}
			
			MonitoringPM monitoringPM = (MonitoringPM)FacesUtils.getManagedBean(PersistencyBeanName.monitoringPM.name());
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());
			BeanPM beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			
			if (definitionChart != null) {
				Collection<DefinitionType> definitionTypes = definitionChart.getDefinition();				
				
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
					    		bean.setLabel(definitionType.getLabel());
								bean.updateSelected(false);
								ikrSelectorBean.getSelectedBeans().add(bean);
								selectedMetrics.add(new ModifiableMetricBean(metricGroupBean));
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
				logger.error("DefinitionChart not found when initializing configuration, env=" + env + ",type=" + type + ",title=" + title + ",componentId=" + componentId);
			}		
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public void init(String env,
					 String type,
					 String title) {
		try {
			try {
				this.env = env;
				this.type = type;
				this.title = title;
				
				init();
				
				ObjectFactory objFactory = new ObjectFactory();

				DefinitionType defType = objFactory.createDefinitionChartTypeDefinitionType();
				definitionChart = objFactory.createDefinitionChartType();
				definitionChart.setType(IkrChartType.CHART_TIMESERIES.getLabel());
				definitionChart.setHeight(300);
				definitionChart.setWidth(390);
				definitionChart.setMaxSlot(60);
				definitionChart.getDefinition().add(defType);	
				
			} catch (Exception exc) {
				logger.error(exc);
			}

		} catch (Exception exc) {
			logger.error(exc);
		}	
	}	
	
	public void select(Collection<ModifiableMetricBean> ikrDefinitionBeans) {				
		boolean error = false;
		ErrorMessageBean errorMsg = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		errorMsg.init();
		errorMsg.addMessage("Chart Not Supported for :");
		errorMsg.addMessage("========================================================");
		errorMsg.addMessage("  ");
		for (ModifiableMetricBean bean : ikrDefinitionBeans) {
			IkrDefinitionBean ikrDef = (IkrDefinitionBean)bean.getMetricGroupBean();
			if(!ikrDef.getIkrCategory().getIkrUnit().isChartSupported()) {
				errorMsg.addMessage("- " + ikrDef.getIkrCategory().getLabel() + "(" + ikrDef.getInstance() + ")");
				error = true;
			}
			else {
				selectedMetrics.add(bean);
			}
		}		

		ArrayList<ModifiableMetricBean> metrics = new ArrayList<ModifiableMetricBean>();
		metrics.addAll(selectedMetrics);
		ikrSelectorBean.setSelectedBeans(metrics);
		
		if (error) {
			errorMsg.setRendered(true);
			errorMsg.setModal(true);
			errorMsg.setType(ErrorMessageBean.WARNING);
		}
	}
	
	public void deselect(Collection<ModifiableMetricBean> ikrDefinitionBeans) {
		for (ModifiableMetricBean bean : ikrDefinitionBeans) {
			if (selectedMetrics.contains(bean))
				selectedMetrics.remove(bean);
		}
	}
	

	private void init() {
		selectedMetrics = new HashSet<ModifiableMetricBean>();
		ikrSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
		ikrSelectorBean.init();
		ikrSelectorBean.setDashboardMode(true);
		ikrSelectorBean.setSelectedTableScrollWitdh(475);
		ikrSelectorBean.setSelectedTableWitdh(600);
		ikrSelectorBean.setSelectedTableScrollHeight(384);
		ikrSelectorBean.setRendered(true);
		ikrSelectorBean.accept(this);
	}	

	public boolean isErrorDefinitionlistVisible() {
		return errorDefinitionlistVisible;
	}

	public void setErrorDefinitionlistVisible(boolean errorDefinitionlistVisible) {
		this.errorDefinitionlistVisible = errorDefinitionlistVisible;
	}

	public boolean isErrorDefinitionChartTitleVisible() {
		return errorDefinitionChartTitleVisible;
	}

	public void setErrorDefinitionChartTitleVisible(
			boolean errorDefinitionChartTitleVisible) {
		this.errorDefinitionChartTitleVisible = errorDefinitionChartTitleVisible;
	}

	public DefinitionChartType getDefinitionChart() {
		return definitionChart;
	}
	
	public void onChangeChartTypeSelected(ValueChangeEvent evnt) {	
		String chartType = (String)evnt.getNewValue();	
		definitionChart.setType(chartType);
		if (!isRenderedMaxTimeSlot()) {
			definitionChart.setMaxSlot(0);
		}
	}	
		
	public void save() {
		Collection<ModifiableMetricBean> beans = ikrSelectorBean.getSelectedBeans();
		
		if (beans.isEmpty()) {
			errorDefinitionlistVisible = true;
			return;
		}
		
		if (definitionChart.getTitle().trim().length() == 0) {
			errorDefinitionChartTitleVisible = true;
			errorDefinitionlistVisible = false;
			return;	
		}	
		
		errorDefinitionChartTitleVisible = false;
		errorDefinitionlistVisible = false;
		
		try {
			ObjectFactory objFactory = new ObjectFactory();
			definitionChart.getDefinition().clear();
			
			for (ModifiableMetricBean bean : beans) {
				DefinitionType definitionType = objFactory.createDefinitionChartTypeDefinitionType();
				IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)bean.getMetricGroupBean();
				definitionType.setIkrCategoryValue(ikrDefinitionBean.getIkrCategory().getDomainValue());
				definitionType.setContext(ikrDefinitionBean.getContext());
				definitionType.setIkrInstance(ikrDefinitionBean.getIkrDefinition().getFullIkrInstance());
				definitionType.setLogicalEnv(ikrDefinitionBean.getLogicalEnv().getName());
				definitionType.setLabel(bean.getLabel());
				definitionChart.getDefinition().add(definitionType);
			}	
		
			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
			
//			definitionChart.setMaxSlot(pMaxSlot);
			
			if (definitionChart.getId() == null || definitionChart.getId().length() == 0) {
				factory.addNewChartComponent(env, type, title, definitionChart);
			} else {
				factory.updateChartComponent(env, type, title, definitionChart);
			}
			
			DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			bean.resetSelectedDashBoard();
			
		} catch (Exception exc) {
			logger.error("Error while saving DefinitionChart", exc);
		}
	}	
	
//	public void maxSlotValueChanged(ValueChangeEvent event) {
//		strMaxSlot = (String)event.getNewValue();
//		try {
//			pMaxSlot = Integer.parseInt(strMaxSlot);
//		}
//		catch(NumberFormatException nbe) {
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("Wrong format for MaxSlot: must be a positive integer");
//		}
//		
//		if (pMaxSlot < 10) {
//			pMaxSlot = 60;
//			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
//			error.setRendered(true);
//			error.setModal(true);
//			error.setType(ErrorMessageBean.WARNING);
//			error.addMessage("MaxSlot : minimum value is 10 minutes and default value is 60 minutes");
//		}
//	}
//		
//	public String getMaxSlot() {
//		return String.valueOf(pMaxSlot);
//	}
//
//	public void setMaxSlot(String strMaxSlot) {
//		this.strMaxSlot = strMaxSlot;
//	}

	public boolean isRenderedMaxTimeSlot() {
		return !(definitionChart.getType().equalsIgnoreCase(IkrChartType.CHART_PIE2D.getLabel()));
	}
	
	public SelectItem[] getTypeItems() {
		return typeItems;
	}

	public String getChartPanelStyle() {
		if(errorDefinitionlistVisible && !getListRenderer())
			return "border: 1px solid red; text-align: center; width: 475px; height: 384px;";
		else
			return "border: 1px solid #336699; text-align: center; width: 475px; height: 384px;";
	}

	public String getChartTitleStyle() {
		if(errorDefinitionChartTitleVisible || titleError)
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
