package com.fsi.monitoring.workSpace.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.time.TimeSeriesCollection;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.component.bean.ModifiableMetricBean;
import com.fsi.monitoring.component.ikrSelector.MetricSelectorBean;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.dashboard.bean.GlobalBoardBean;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.histo.HistoPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.msd.StaticData;
import com.fsi.monitoring.util.ErrorMessageBean;
import com.fsi.monitoring.util.FacesUtils;
import com.fsi.monitoring.util.IkrProgressBarController;
import com.fsi.monitoring.util.ResourceDownload;
import com.fsi.monitoring.util.chart.IkrHistoChartFactory;
import com.fsi.monitoring.util.export.ChartPdfExport;
import com.fsi.monitoring.util.export.IkrExcelExport;
import com.fsi.monitoring.workSpace.DataCenterBean;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import com.lowagie.text.pdf.DefaultFontMapper;


public class WorkSpaceBean extends GlobalBoardBean implements Serializable{	
	private static final long serialVersionUID = -7895374859874826854L;
	private static final Logger logger = Logger.getLogger(WorkSpaceBean.class);	
	private static final SimpleDateFormat exportFileDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
	
	private Date fromDate;
	private Date toDate;
	
	private String title;
	private String wsId;
	private boolean gridDisplay;
	private boolean graphDisplay;
	
	private MetricSelectorBean ikrSelectorBean;
	private HistoPM histoPM;
	private BeanPM beanPM;
//	private ExportConfigBean exportConfig;
	
	private Effect valueChangeEffect2;	
	private List<WorkSpaceComponentBean> graphs;	
	private SimpleDateFormat dateFormat;	
	private int tour = 0;
	
	private boolean pdf;
	private boolean excel;
	
	private boolean exportVisible = false;
	private String exportType = "";	
	
	private WorkSpaceComponentBean graphToExtend = null;
	private boolean extendedGraphPopup = false;
	
	private ResourceDownload resource;
	
	private boolean rendererHistoricalDataAnalysis = false;
	
//	private PersistentFacesState state;
//	private RenderManager renderManager;
	private String sessionId;
	
	private IkrProgressBarController progressController;
	
	private DataCenterBean temp = (DataCenterBean)FacesUtils.getManagedBean("dataCenterBean");
	
	private WorkspaceValueBean getValueBeans() {			
		Map<Long,String> chartLabels = new HashMap<Long, String>();
		Map<Long, FormattedValue> formats = new HashMap<Long, FormattedValue>();
		Map<Long, List<IkrValueBean>> valueBeans = new HashMap<Long, List<IkrValueBean>>();
		try {
			List<IkrValueBean> ikrValueBeans = new ArrayList<IkrValueBean>();
			// Dynamic Values
			long dt0 = System.currentTimeMillis();
			System.out.println("Workspace  -- SQL Start data");
//			Collection<Long> ikrValueIds = histoPM.getChartIkrValueIds(getSelectedIkrDefinitions(), fromDate, toDate);	
//			Collection<IkrValue> ikrValues = histoPM.getIkrValues(getSelectedIkrDefinitions(), fromDate, toDate, 350);
			Collection<Long> ikrValueIds = histoPM.getIkrValueIds(getSelectedIkrDefinitions(), fromDate, toDate);
			ikrValueBeans = beanPM.getIkrValueBeans(ikrValueIds);
			long dt1 = System.currentTimeMillis();
			System.out.println("Workspace  -- SQL End     Time = " + (dt1-dt0) + "     points = " + ikrValueIds.size());
			
			long dt2 = System.currentTimeMillis();
			System.out.println("Workspace  -- GET Cache Start data");
//			if (ikrValueIds!=null && ikrValueIds.size()>0) {
//				BeanPM beanPM = (BeanPMFactory)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
//				ikrValueBeans.addAll(beanPM.getIkrValueBeans(ikrValueIds));
//			}
//			for(IkrValue ikrVal : ikrValues) {
//				IkrValueBean ikrValueBean = new IkrValueBean(getIkrDefinitionBean(ikrVal.getValueDefinitionId()), ikrVal);
//				ikrValueBeans.add(ikrValueBean);
//			}
//			long dt3 = System.currentTimeMillis();
//			System.out.println("Workspace  -- GET Cache End     Time = " + (dt3-dt2));
			
			
//			 Static Values
			ikrValueBeans.addAll(getIkrStaticDataValues());			
			
			for (IkrValueBean ikrValueBean : ikrValueBeans) {
				IkrCategory ikrCat = ikrValueBean.getIkrDefinitionBean().getIkrCategory();
				AbstractIkrDefinition ikrDef = ikrValueBean.getIkrDefinitionBean().getIkrDefinition();
				
				long id = (ikrDef!=null)?ikrDef.getId():ikrCat.getId(); 				
				//Labels				
				String label = chartLabels.get(id);
				if (label == null) {
					label = ikrCat.getLabel();
					if (ikrDef!=null)
						label = label + ":" +ikrDef.getFullIkrInstance();
					chartLabels.put(id, label);
				}
				
				// Units
				FormattedValue format = ikrValueBean.getFormattedValue();
				FormattedValue oldFormat = formats.get(id);
				if (oldFormat == null)
					formats.put(id, format);
				else {
					String oldUnit = oldFormat.getIkrUnit().getSymbol();
					double oldDivider = oldFormat.getIkrUnit().getDivider().doubleValue();
					String unit = format.getIkrUnit().getSymbol();				
					double divider = format.getIkrUnit().getDivider().doubleValue();
					if (!oldUnit.equals(unit) && divider<oldDivider) {							
						formats.put(id, format);		
					}
				}
				
				// ValueBeans
				List<IkrValueBean> values = valueBeans.get(id);
				if(values == null) {
					values = new ArrayList<IkrValueBean>();
					valueBeans.put(id, values);
				}
				values.add(ikrValueBean);
			}	
		} catch (PersistenceException e) {
			logger.error(e);
		}
		return new WorkspaceValueBean(chartLabels, formats, valueBeans);
	}
	
	private IkrDefinitionBean getIkrDefinitionBean(long id) {
		IkrDefinitionBean bean = null;
		Collection<ModifiableMetricBean> ikrDefinitionBeans = ikrSelectorBean.getSelectedBeans();
		for (ModifiableMetricBean mIkrDefinitionBean : ikrDefinitionBeans) {	
			IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)mIkrDefinitionBean.getMetricGroupBean();	
//			IkrCategory ikrCat = ikrDefinitionBean.getIkrCategory();
			AbstractIkrDefinition ikrDef = ikrDefinitionBean.getIkrDefinition();
			if(ikrDef.getId() == id) {
				bean = ikrDefinitionBean;
				break;
			}
//			if (ikrDef!= null) {
//				if(ikrDef.getId() == id) {
//					bean = ikrDefinitionBean;
//					break;
//				}
//			}
//			else {
//				if(ikrCat.getId() == id) {
//					bean = ikrDefinitionBean;
//					break;
//				}
//			}
		}
		return bean;
	}
		
	private String getFlux(List<IkrValueBean> values, Map<Long, FormattedValue> formats) {	
		StringBuffer flux = new StringBuffer();	
		if (values != null && !values.isEmpty()) {					
			for (IkrValueBean bean : values) {
//				IkrCategory ikrCat = bean.getIkrDefinitionBean().getIkrCategory();
				AbstractIkrDefinition ikrDef = bean.getIkrDefinitionBean().getIkrDefinition();
				long id = ikrDef.getId();
//				long id = ikrCat.getId();
//				if (ikrDef!= null)
//					id = ikrDef.getId();
				long captureTime = bean.getIkrValue().getCaptureTime().getTime();
				IkrUnit toUnit = formats.get(id).getIkrUnit();
				String toUnitSymbol = formats.get(id).getIkrUnit().getSymbol();
				FormattedValue formattedValue = bean.getFormattedValue();
				IkrUnit unit = formattedValue.getIkrUnit();
				String unitSymbol = formattedValue.getIkrUnit().getSymbol();
				String value = formattedValue.getValue();
				if (!toUnitSymbol.equals(unitSymbol)) {
					value = convertValue(value, unit, toUnit);				
				}			
				String data = id + ";" + captureTime + ";" + value;		
				flux.append(data);
				flux.append("#FIN#");
			}
		}		
		return flux.toString();
	}
	
	protected String convertValue(String value, IkrUnit fromUnit, IkrUnit toUnit) {
		String res = null;
		try {
		    Method method = fromUnit.getClass().getMethod("convertTo", new Class[] {String.class, toUnit.getClass()});
		    Object o = method.invoke(fromUnit, new Object[] {value, toUnit});
		    res = (String)o;
		} catch (NoSuchMethodException exc1) {
			logger.error(exc1);
		} catch (IllegalAccessException exc2) {
			logger.error(exc2);
		} catch (InvocationTargetException exc3) {
			Throwable exc = exc3.getTargetException();
			if (exc instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)exc;
			} else {
				logger.error(exc);
			}
		}
		return (res!=null)?res:value;
	}
	
	public WorkSpaceBean() {
		wsId = "WS";
		graphs = new ArrayList<WorkSpaceComponentBean>();
        valueChangeEffect2 = new Highlight("#fda505");
        valueChangeEffect2.setFired(true);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");       
       
	}
	
	public void init(ActionEvent action) {
		if (isAuthorized(41,"ikrWorkSpace")) {
			if (ikrSelectorBean == null) {
				ikrSelectorBean = new MetricSelectorBean();
				ikrSelectorBean.setReInitSelected(true);
				ikrSelectorBean.setRendered(false);
				Calendar toCal = Calendar.getInstance();
				Calendar fromCal = Calendar.getInstance();
				fromCal.add(Calendar.HOUR, -1);
				fromDate = fromCal.getTime();
				toDate = toCal.getTime();
				graphDisplay = true;
				histoPM = (HistoPM)FacesUtils.getManagedBean(PersistencyBeanName.histoPM.name());
				beanPM = (BeanPM)FacesUtils.getManagedBean(BeanPM.BeanPM_ID);
			}
			ikrSelectorBean.init();	
			
//			state = PersistentFacesState.getInstance();
			progressController = new IkrProgressBarController();		
			progressController.initProgressBar();
		}
	}	
	
	public Map<String,WorkSpaceComponentBean> getGraphs() {
		Map<String,WorkSpaceComponentBean> map = new HashMap<String,WorkSpaceComponentBean>();
		
		for(int i=0; i<graphs.size(); i++) {
			map.put("graph" + i, graphs.get(i));
		}
		
		return map;
	}
	
//	public void setRenderManager(RenderManager renderManager) {
//		this.renderManager = renderManager;
//		
//		 sessionId = FacesContext.getCurrentInstance().getExternalContext().getSession(false).toString();
//		 renderManager.getOnDemandRenderer(sessionId).add(this);
//	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean getGridDisplay() {
		return gridDisplay;
	}
	
	public void setGridDisplay(boolean gridDisplay) {
		this.gridDisplay = gridDisplay;
	}
	
	public boolean getGraphDisplay() {
		return graphDisplay;
	}
	
	public void setGraphDisplay(boolean graphDisplay) {
		this.graphDisplay = graphDisplay;
	}	
		
	public Date getFromDate() {
		return fromDate;
	}
	
	public String getFromDateStr() {		
		return dateFormat.format(fromDate);
	}
	
	public String getToDateStr() {
		return dateFormat.format(toDate);
	}
	
	public void setFromDate(Date date) {
		this.fromDate = date;
	}
	
	public Date getToDate() {
		return toDate;
	}
	
	public void setToDate(Date date) {
		this.toDate = date;
	}
	
	private List<Long> getSelectedIkrDefinitions() {
		List<Long> selectedIkrDefinitions = new ArrayList<Long>();
		Collection<ModifiableMetricBean> ikrDefinitionBeans = ikrSelectorBean.getSelectedBeans();
		for(ModifiableMetricBean bean : ikrDefinitionBeans) {
			if (bean.isSelected()) {
				IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)bean.getMetricGroupBean();
				if (!StaticData.STATIC_DATA_CONTEXT.equals(ikrDefinitionBean.getContext()))
					selectedIkrDefinitions.add(ikrDefinitionBean.getIkrDefinition().getId());
			}
		}
		return selectedIkrDefinitions;
	}
	
	private List<Long> getSelectedStaticDataDefinitions() {
		List<Long> selectedIkrDefinitions = new ArrayList<Long>();
		Collection<ModifiableMetricBean> ikrDefinitionBeans = ikrSelectorBean.getSelectedBeans();
		for(ModifiableMetricBean bean : ikrDefinitionBeans) {
			if (bean.isSelected()) {
				IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)bean.getMetricGroupBean();
				if (StaticData.STATIC_DATA_CONTEXT.equals(ikrDefinitionBean.getContext())) 
					selectedIkrDefinitions.add(ikrDefinitionBean.getIkrDefinition().getId());
			}
		}
		return selectedIkrDefinitions;
	}
	
	private List<IkrValueBean> getIkrStaticDataValues() {
		List<IkrValueBean> res = new ArrayList<IkrValueBean>();
		Collection<ModifiableMetricBean> ikrDefinitionBeans = ikrSelectorBean.getSelectedBeans();
		for(ModifiableMetricBean bean : ikrDefinitionBeans) {
			if (bean.isSelected()) {
				IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)bean.getMetricGroupBean();
				if (StaticData.STATIC_DATA_CONTEXT.equals(ikrDefinitionBean.getContext())) {
					IkrValue fromIkrValue = new IkrValue();
					fromIkrValue.setCaptureTime(fromDate);
					fromIkrValue.setIkrCategoryId(ikrDefinitionBean.getIkrCategory().getId());
					fromIkrValue.setIkrDefinitionId(ikrDefinitionBean.getIkrDefinition().getId());
					fromIkrValue.setValue(((StaticData)ikrDefinitionBean.getIkrDefinition()).getValue());
					
					IkrValue endIkrValue = new IkrValue();
					endIkrValue.setCaptureTime(toDate);
					endIkrValue.setIkrCategoryId(ikrDefinitionBean.getIkrCategory().getId());
					endIkrValue.setIkrDefinitionId(ikrDefinitionBean.getIkrDefinition().getId());
					endIkrValue.setValue(((StaticData)ikrDefinitionBean.getIkrDefinition()).getValue());
					
					res.add(new IkrValueBean(ikrDefinitionBean, fromIkrValue));
					res.add(new IkrValueBean(ikrDefinitionBean, endIkrValue));					
				}
			}
		}
		return res;
	}
	
//	private List<Integer> getSelectedIkrStaticDatas() {
//		List<Integer> res = new ArrayList<Integer>();
//		Collection<ModifiableMetricBean> ikrDefinitionBeans = ikrSelectorBean.getSelectedBeans();
//		for(ModifiableMetricBean bean : ikrDefinitionBeans) {
//			if (bean.isSelected()) {
//				IkrDefinitionBean ikrDefinitionBean = (IkrDefinitionBean)bean.getMetricGroupBean();
//				if (ikrDefinitionBean.getIkrDefinition() == null) {
//					StaticData data = (StaticData)ikrDefinitionBean.getIkrCategory();					
//					res.add(data.getId());
//					
//				}
//			}
//		}
//		return res;
//	}
	
	public String getIkrDefIds() {
		List<Long> ikrDefinitionBeans = getSelectedIkrDefinitions();
		String ids = "";
		if (ikrDefinitionBeans != null && ikrDefinitionBeans.size()>0) {
			long id = ikrDefinitionBeans.remove(0);
			IkrDefinitionBean ikrDefinitionBean = getIkrDefinitionBean(id);
			ids = ikrDefinitionBean.getIkrDefinition().getId() + "=" + "unit" + ";" + ikrDefinitionBean.getIkrCategory().getLabel();
		}
		for (long id : ikrDefinitionBeans) {
			IkrDefinitionBean ikrDefinitionBean = getIkrDefinitionBean(id);
			ids = ids + ":" + ikrDefinitionBean.getIkrDefinition().getId() + "=" + "unit" + ";" + ikrDefinitionBean.getIkrCategory().getLabel();
		}
		return ids;   //   --------------------  getUnitValue(ikrDefinitionBean.getUnit())
	}

	
    public TimeZone getTimeZone() {
        return java.util.TimeZone.getDefault();
    }	

    public void effect2ChangeListener(ValueChangeEvent event){
        valueChangeEffect2.setFired(false);
    }    
    
	public void generate(ActionEvent action) {		
		if(getSelectedIkrDefinitions().size()==0 && getSelectedStaticDataDefinitions().size()==0){
			ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
			error.init();
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
			error.addMessage("You must select at least 1 metric to generate a chart or a grid");
		}
		else {				
			if (fromDate.after(toDate)) {
				ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
				error.addMessage("Date Inconsistency: From Date must be before To Date");
			} 
			else {				
				progressController.startProgress();				
				final ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
				error.init();
				error.setRendered(false);
				(new Thread (new Runnable() {					
					public void run() {
						processHistoricalDataAnalysis(error);						
					}
				})).start();
			}		
		}		
	}
	
	private void processHistoricalDataAnalysis(ErrorMessageBean error) {
		long dt0 = System.currentTimeMillis();
		System.out.println("Workspace  -- start loading data");
		WorkspaceValueBean wsValueBean = getValueBeans();	
		long dt1 = System.currentTimeMillis();
		System.out.println("Workspace  -- data loaded     Time = " + (dt1-dt0) + "     points = " + wsValueBean.getValueBeans().values().size());
		Map<Long, List<IkrValueBean>> valueBeans = wsValueBean.getValueBeans();
		for(long id : getSelectedIkrDefinitions()) {
			List<IkrValueBean> beans = valueBeans.get(id);
			if(beans==null || beans.isEmpty()) {
				IkrDefinitionBean def = getIkrDefinitionBean(id);
				String label = def.getIkrDefinition().getFullIkrInstance() + " : " + def.getIkrCategory().getLabel();
				error.addMessage("No data found for " + label);
			}
		}
		
		if(error.getMessages().size()>0) {
			error.setRendered(true);
			error.setModal(true);
			error.setType(ErrorMessageBean.WARNING);
		}				
		
		if (graphDisplay) {
			List<String> messages = new ArrayList<String>();
			boolean chartNotSupported = false;			
			for (long id : getSelectedIkrDefinitions()) {
				IkrDefinitionBean ikrDef = getIkrDefinitionBean(id);
				if(!ikrDef.getIkrCategory().getIkrUnit().isChartSupported()) {
					messages.add(ikrDef.getIkrCategory().getLabel() + "(" + ikrDef.getInstance() + ")");
					valueBeans.remove(id);
					chartNotSupported = true;
				}
			}
			
			if (chartNotSupported) {
				error.addMessage("Chart Not Supported for :");
				error.addMessage("  ");
				for (String msg : messages) {
					error.addMessage(msg);
				}
				error.setRendered(true);
				error.setModal(true);
				error.setType(ErrorMessageBean.WARNING);
			}
			
			wsId = wsId + "_" + tour + "_" + graphs.size();	
			int i = 1;					
			int len = valueBeans.size();
			String chartData = "";
			for(long id : valueBeans.keySet()){
				List<IkrValueBean> values = valueBeans.get(id);
				if(values!=null && values.size()>0) {
					chartData = chartData + getFlux(values, wsValueBean.getFormats());
					if(i < len)
						chartData = chartData + "#FINDTS#";
					i = i + 1;							
				}
			}		
			
			if (chartData.length()>0) {
				WorkSpaceGraphBean component = new WorkSpaceGraphBean(wsId,chartData, wsValueBean.getChartLabels(), wsValueBean.getFormats());
				component.setIkrDefIds(getIkrDefIds());
				component.setTitle((title!=null && title.length()>0)?title:"");
				component.setValueBeans(valueBeans);
				graphs.add(component);
			}
		}
		
		if (gridDisplay) {
			wsId = wsId + "_" + tour;
			for(long id : valueBeans.keySet()) {
				List<IkrValueBean> values = valueBeans.get(id);
				if (values.size()>0) {
					String finalWsId = wsId + "_" + graphs.size();
					WorkSpaceGridBean component = new WorkSpaceGridBean(finalWsId, values);
					component.setIkrDefinitionBean(getIkrDefinitionBean(id));
					component.setTitle((title!=null && title.length()>0)?title:"");
					graphs.add(component);
				}
			}
		}
		wsId = "WS";
		tour = tour + 1;
		ikrSelectorBean.setRendered(false);
		
		long st1 = System.currentTimeMillis();
		System.out.println("Workspace  -- generation finished     Time = " + (st1-dt0));
		progressController.stopProgress();
		progressController.setRenderProgressBar(false);
		rendererHistoricalDataAnalysis = false;
	}
	
	public void delete(ActionEvent action) {
		WorkSpaceComponentBean graphToDelete = (WorkSpaceComponentBean)action.getComponent().getAttributes().get("obj");
		
		Iterator<WorkSpaceComponentBean> graphIT = graphs.iterator();
		while (graphIT.hasNext()) {
			WorkSpaceComponentBean graph = graphIT.next();
			if (graphToDelete == graph) {
				graphIT.remove();
				break;
			}
		}
	}
	
	public void openExtendedGraphPopup(ActionEvent action) {
		graphToExtend = (WorkSpaceComponentBean)action.getComponent().getAttributes().get("obj");
		extendedGraphPopup = true;
	}
	
	public void closeExtendedGraphPopup(ActionEvent action) {
		graphToExtend = null;
		extendedGraphPopup = false;
	}
	
	public boolean isExtendedGraphPopup() {
		return extendedGraphPopup;
	}

	public WorkSpaceComponentBean getGraphToExtend() {
		return graphToExtend;
	}

	public void pdfExport(ActionEvent action) {
		if (!isAuthorized(42,"ikrWorkSpace")) {
			String currentMenu = temp.getCurrentMenu();
			setAccessDenied(currentMenu);
			return;
		}
		excel = false;
		pdf = true;
		exportType = "PDF";
		WorkSpaceGraphBean bean = (WorkSpaceGraphBean)action.getComponent().getAttributes().get("obj");
		Map<String, TimeSeriesCollection> sources = new Hashtable<String, TimeSeriesCollection>();
		try {	
			String exportDir = FacesUtils.getServletContext().getRealPath("/") + "export";
			File directory = new File(exportDir);
			if (!directory.exists())
				directory.mkdir();
			
			String filename = bean.getWsId() + "_" + exportFileDateFormat.format(new Date()) + ".pdf"; 
			JFreeChart chart = IkrHistoChartFactory.getTimeSeriesChart(bean.getFormats(), bean.getChartLabels(), sources, StandardChartTheme.createJFreeTheme());
			IkrHistoChartFactory.build(bean.getChartData(), bean.getFormats(), bean.getChartLabels(), sources);
			
			File file = new File(exportDir + "/" + filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			ChartPdfExport.saveChartAsPDF(file, chart, 400, 300, new DefaultFontMapper(), bean.getTitle());
			resource = new ResourceDownload(null, exportDir, filename, ResourceDownload.PDF);
			exportVisible = true;
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (CloneNotSupportedException e) {
			logger.error(e.getMessage(), e);
		} 
	}	
		
	public void excelExport(ActionEvent action) {
		if (!isAuthorized(42,"ikrWorkSpace")) {
			String currentMenu = temp.getCurrentMenu();
			setAccessDenied(currentMenu);
			return;
		}
		
		excel = true;
		pdf = false;
		exportType = "Excel";
		String exportDir = FacesUtils.getServletContext().getRealPath("/") + "export";
		File directory = new File(exportDir);
		if (!directory.exists())
			directory.mkdir();
		
		WorkSpaceComponentBean component = (WorkSpaceComponentBean)action.getComponent().getAttributes().get("obj");
		String filename = component.getWsId() + "_" + exportFileDateFormat.format(new Date()) + ".xls";
		Map<Long, List<IkrValueBean>> valueBeans = null;
		if (component.isGraphComponent()) {
			WorkSpaceGraphBean graphComponent = (WorkSpaceGraphBean)component;
			valueBeans = graphComponent.getValueBeans();
		} else {
			WorkSpaceGridBean gridComponent = (WorkSpaceGridBean)component;
			valueBeans = new HashMap<Long, List<IkrValueBean>>();
			valueBeans.put(gridComponent.getIkrDefinition().getId(), new ArrayList<IkrValueBean>(gridComponent.getIkrValueBeans()));
		}				
		File file = new File(exportDir + "/" + filename);
		IkrExcelExport.writeGridAsExcel(file, valueBeans);
		resource = new ResourceDownload(null, exportDir, filename, ResourceDownload.EXCEL);
		exportVisible = true;
	}
	
	public boolean isPdf() {
		return pdf;
	}

	public boolean isExcel() {
		return excel;
	}
	
	public boolean isExportVisible() {
		return exportVisible;
	}
	
	public void toggleExportView(ActionEvent event) {
		exportVisible = false;
	}	
	
	public String getExportType() {
		return exportType;
	}

	public ResourceDownload getResource() {
		return resource;
	}
	
	private void setAccessDenied(String currentMenu) {
		ErrorMessageBean error = (ErrorMessageBean)FacesUtils.getManagedBean("errorMessageBean");
		error.init();
		error.setRendered(true);
		error.setModal(true);
		error.setType(ErrorMessageBean.WARNING);
		error.addMessage("Access Denied");
	}
	
	public IkrProgressBarController getProgressBarController() {
		return progressController;
	}


	class WorkspaceValueBean {
		private Map<Long,String> chartLabels;
		private Map<Long, FormattedValue> formats;
		private Map<Long, List<IkrValueBean>> valueBeans;
		
		public WorkspaceValueBean(Map<Long, String> chartLabels,
				Map<Long, FormattedValue> formats, Map<Long, List<IkrValueBean>> valueBeans) {
			super();
			this.chartLabels = chartLabels;
			this.formats = formats;
			this.valueBeans = valueBeans;
		}

		public Map<Long, String> getChartLabels() {
			return chartLabels;
		}

		public Map<Long, FormattedValue> getFormats() {
			return formats;
		}

		public Map<Long, List<IkrValueBean>> getValueBeans() {
			return valueBeans;
		}		
	}
	
	public MetricSelectorBean getMetricSelectorBean() {
		return ikrSelectorBean;
	}


	public boolean isRendererHistoricalDataAnalysis() {
		return rendererHistoricalDataAnalysis;
	}

	public void setRendererHistoricalDataAnalysis(
			boolean rendererHistoricalDataAnalysis) {
		this.rendererHistoricalDataAnalysis = rendererHistoricalDataAnalysis;
	}

	public IkrProgressBarController getProgressController() {
		return progressController;
	}

//	public PersistentFacesState getState() {
//		return state;
//	}
//
//	public void renderingException(RenderingException renderingException) {
//		if (renderingException instanceof FatalRenderingException) {
//			logger.error("OutputProgressController Fatal rendering exception: ", renderingException);
//            renderManager.getOnDemandRenderer(sessionId).remove(this);
//            renderManager.getOnDemandRenderer(sessionId).dispose();
//        }
//		
//	}
//
//	public void dispose() throws Exception {
//		renderManager.getOnDemandRenderer(sessionId).remove(this);
//        renderManager.getOnDemandRenderer(sessionId).dispose();		
//	}
	
	public boolean isRendererMyData() {
		return ikrSelectorBean.getSelectedBeans().size()>0;
	}
}
