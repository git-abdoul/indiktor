package com.fsi.monitoring.dashboard.config;

import generated.dashboard.config.schema.CategoryChartType;
import generated.dashboard.config.schema.CategoryChartType.MetricType;
import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.DashBoardType.ChartsType;
import generated.dashboard.config.schema.ObjectFactory;

import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.datamodel.bean.MetricGroupBean;
import com.fsi.monitoring.ikr.LogicalEnv;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.FacesUtils;

public class CategoryChartConfigBean
extends DashBoardConfigBean {

	private static final long serialVersionUID = 2767684910418822131L;

	private static final Logger logger = Logger.getLogger(DefinitionChartConfigBean.class);	
	
	private CategoryChartType categoryChart;
	
    private static SelectItem[] typeItems;
    
	private MetricSelectorBean ikrSelectorBean;
	
	private String chartType;
    
    static {
    	typeItems = new SelectItem[2];
    	typeItems[0] = new SelectItem("timeseries", "timeseries");
    	typeItems[1] = new SelectItem("pie", "pie");
    }
    	
	public void init(String env,
					 String type,
					 String title,
					 String componentId) {
		super.init(env, type, title);
		
		try {
			init();
			
			ComponentXmlManager componentXmlManager = (ComponentXmlManager)FacesUtils.getManagedBean("xmlComponentManager");
			
			DashBoard dashBoard = componentXmlManager.getDashBoardSchema(env, type, title);
			
			ChartsType chartsType = dashBoard.getCharts();
			List<CategoryChartType> categoryCharts = chartsType.getCategoryChart();
			
			for (CategoryChartType categoryChart : categoryCharts) {
				if (categoryChart.getId().equals(componentId)) {
					this.categoryChart = categoryChart;
					break;
				}
			}
			
			DataModelPM dataModelPM = (DataModelPM)FacesUtils.getManagedBean(PersistencyBeanName.dataModelPM.name());			
			
			if (categoryChart != null) {
				Collection<MetricType> metricTypes = categoryChart.getMetric();
				
				for (MetricType metricType : metricTypes) {
					String ikrCategoryName = metricType.getIkrCategoryValue();
					String context = metricType.getContext();
					String logicalEnvName = metricType.getLogicalEnv();
					String domainView = metricType.getDomainView();
					
					IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomainByValue(ikrCategoryName);
					if (ikrCategory != null) {
						LogicalEnv logicalEnv = dataModelPM.getLogicalEnv(logicalEnvName);
						if (logicalEnv == null) {
							logger.error("LogicalEnv name not recognized: " + logicalEnvName);
							return;
						}
						
						IkrStaticDomain metricDomain = dataModelPM.getIkrStaticDomain(ikrCategory.getParentDomainId());
						IkrStaticDomain domainType = dataModelPM.getIkrStaticDomain(metricDomain.getParentDomainId());
						
						MetricGroupBean metricGroupBean = new MetricGroupBean(ikrCategory, context, logicalEnv,domainView);
						metricGroupBean.setDomainType(domainType);
						metricGroupBean.setMetricDomain(metricDomain);
			    		ModifiableMetricBean bean = new ModifiableMetricBean(metricGroupBean);
						bean.updateSelected(false);
						ikrSelectorBean.getSelectedBeans().add(bean);
					} else {
						logger.error("MetricCategory name not recognized: " + ikrCategoryName);
						return;
					}
				}
				
			} else {
				logger.error("CategoryChart not found when initializing configuration, env=" + env + ",type=" + type + ",title=" + title + ",componentId=" + componentId);
			}		
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
	
	public boolean isShowMaxSlot() {
		boolean ret = true;
		if (chartType != null && chartType.length()>0)
			ret = "timeseries".equals(categoryChart.getType());;
		return ret;
	}
	
	public void onChangeChartTypeSelected(ValueChangeEvent evnt) {	
		this.chartType = (String)evnt.getNewValue();	
	}	
	
	public void init(String env,
					 String type,
					 String title) {
		super.init(env, type, title);
		try {
			init();
			
			ObjectFactory objFactory = new ObjectFactory();
			categoryChart = objFactory.createCategoryChartType();			
		} catch (Exception exc) {
			logger.error(exc);
		}		
	}
	
	private void init() {
		ikrSelectorBean = (MetricSelectorBean)FacesUtils.getManagedBean("metricSelectorBean");
		ikrSelectorBean.init();
		ikrSelectorBean.setRendered(true);
		ikrSelectorBean.setSelectedTableScrollWitdh(590);
		ikrSelectorBean.setSelectedTableWitdh(575);
		ikrSelectorBean.setSelectedTableScrollHeight(400);
		ikrSelectorBean.setMetricGroupSelectorMode();
	}	
	
	public SelectItem[] getTypeItems() {
		return typeItems;
	}

	public CategoryChartType getCategoryChart() {
		return categoryChart;
	}

		
	public void save(ActionEvent action) {
		if (!isAuthorized(83,"dashboardMainConfig")) {
			return;
		}
		
		Collection<ModifiableMetricBean> beans = ikrSelectorBean.getSelectedBeans();
		
		if (beans == null || beans.isEmpty()) {
			error = true;
			message = "Metric list cannot be empty";
			setAction("categoryChartConfig");
			return;
		}
		
		if (categoryChart.getTitle() == null || categoryChart.getTitle().length() == 0) {
			error = true;
			message = "CategoryChart Title cannot be empty";
			setAction("categoryChartConfig");
			return;	
		}
		
		try {
			ObjectFactory objFactory = new ObjectFactory();
			categoryChart.getMetric().clear();
			
			for (ModifiableMetricBean mbean : beans) {
				MetricGroupBean bean = mbean.getMetricGroupBean();
				MetricType metricType = objFactory.createCategoryChartTypeMetricType();
				metricType.setIkrCategoryValue(bean.getIkrCategory().getDomainValue());
				metricType.setContext(bean.getContext());
				metricType.setLogicalEnv(bean.getLogicalEnv().getName());
				categoryChart.getMetric().add(metricType);
			}
		
			RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();
//			
//			if (categoryChart.getId() == null || categoryChart.getId().length() == 0) {
//				factory.addNewCategoryChartComponent(env, type, title, categoryChart);
//			} else {
//				factory.updateCategoryChartComponent(env, type, title, categoryChart);
//			}
			
			DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
			bean.resetSelectedDashBoard();
		
		} catch (Exception exc) {
			logger.error("Error while saving Category Chart details", exc);
		}
		
	}
}
