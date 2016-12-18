package com.fsi.monitoring.dashboard.component.chart;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.primefaces.component.chart.UIChart;
import org.primefaces.component.chart.bar.BarChart;
import org.primefaces.component.chart.line.LineChart;
import org.primefaces.component.chart.pie.PieChart;
import org.primefaces.model.chart.ChartModel;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.dashboard.component.chart.model.IkrChartModel;
import com.fsi.monitoring.dashboard.component.chart.model.IkrChartModelFactory;
import com.fsi.monitoring.dashboard.component.framework.ComputableComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.RealTimeBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponent;

public class IkrChartComponent 
extends DashBoardComponent 
implements RealTimeComponent, ComputableComponent {
	private static final Logger logger = Logger.getLogger(IkrChartComponent.class);

	private static final long serialVersionUID = 554487791018440233L;		
	
	public int synchronizationDelay;
	public static final int LAST_VALUES = 50;
	
	private int width;
	private int height;
	private int maxSlot;
	
	private int xpos;
	private int ypos;
	
	private BeanPM beanPM;
	
	private Set<Long> ikrDefinitionIds;
	private Map<Long, FormattedValue> formats;

	private IkrChartModel chartModel;
	private UIChart chartUI;
	private String chartStyle;
	
	public IkrChartComponent(String componentId,
						  	 String title,
						  	 int xpos,
						  	 int ypos,
						  	 int width,
						  	 int height,
						  	 int maxSlotToSet,
						  	 String type,
						  	 BeanPM beanPM) {
		
		super(componentId, title, "left:" + xpos + "px;top:" + ypos + "px;position:absolute;", "definitionChart", true);
		
		ikrDefinitionIds = new HashSet<Long>();
		formats = new HashMap<Long, FormattedValue>();
		
		maxSlot = (maxSlotToSet==0)?60:maxSlotToSet;
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		synchronizationDelay = -1*maxSlot;
		this.maxSlot = maxSlot*60000;
		
		this.beanPM = beanPM;
		
		int cptHeight = height + 20;
		int cptWidth = width + 20;
		
		chartStyle = "height:"+height+"px;width:"+width+"px;";
		style = style + "height:"+cptHeight+"px;width:"+cptWidth+"px;";
		
		chartModel = IkrChartModelFactory.createChartModel(IkrChartType.getChartType(type));
		chartModel.setChartHeight(height);
		chartModel.setChartWidth(width);
		chartModel.init();
		chartUI = IkrChartModelFactory.getChartUI(IkrChartType.getChartType(type));
		if (chartUI!=null) {
			chartUI.setValue(chartModel.getModel());
			chartUI.setLegendPosition("e");
			chartUI.setStyle("height:"+height+"px;width:"+height+"px;");
		}
	}	
	
	public void setInfo(AbstractIkrDefinition ikrDefinition, IkrCategory ikrCat, String label, DataModelPM dataModelPM) {
		long ikrDefinitionId = ikrDefinition.getId();
		String chartLabel = ikrCat.getLabel()+ ":" +ikrDefinition.getFullIkrInstance();
		if (label!=null && label.length() != 0) {
			chartLabel = label;
		} 
		chartModel.addSeries(ikrDefinitionId, chartLabel);
		ikrDefinitionIds.add(ikrDefinition.getId());
	}
	
	@Override
	public void synchronize() {
		Calendar from = Calendar.getInstance();
		from.add(Calendar.MINUTE, synchronizationDelay);
		for (long ikrDefId : ikrDefinitionIds) {
			try {
				List<IkrValueBean> valueBeans = beanPM.getIkrValueBeansByIkrDefinition(ikrDefId, from.getTime());
				if (valueBeans.isEmpty())
					valueBeans = beanPM.getLastIkrValuesBeanByIkrDefinition(ikrDefId, LAST_VALUES);
				for (IkrValueBean bean : valueBeans) {
					push(bean);
				}
			} catch (PersistenceException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	protected boolean isAccepted(IkrValueBean ikrValueBean) {
		long ikrDefinitionId = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getId();
		return ikrDefinitionIds.contains(ikrDefinitionId);
	}
	
	public void push(RealTimeBean valueBean) {		
		IkrValueBean ikrValueBean = (IkrValueBean)valueBean;			
		if (isAccepted(ikrValueBean)) {
			long ikrDefinitionId = ikrValueBean.getIkrDefinitionBean().getIkrDefinition().getId();
			FormattedValue format = ikrValueBean.getFormattedValue();
			FormattedValue oldFormat = formats.get(ikrDefinitionId);
			if (oldFormat == null)
				formats.put(ikrDefinitionId, format);
			else {
				String oldUnit = oldFormat.getIkrUnit().getSymbol();
				double oldDivider = oldFormat.getIkrUnit().getDivider().doubleValue();
				String unit = format.getIkrUnit().getSymbol();				
				double divider = format.getIkrUnit().getDivider().doubleValue();
				if (!oldUnit.equals(unit) && divider<oldDivider) {
					formats.put(ikrDefinitionId, format);		
				}
			}
			
			FormattedValue[] formatArray = {oldFormat, format};
			chartModel.updateModel(ikrDefinitionId, format.getValue(), ikrValueBean.getIkrValue().getCaptureTime(), formatArray);
		}
	}

	public void computeComponent() {
//		Map<Long, List<String>> valuesToRemove = new HashMap<Long, List<String>>();
//		data = new HashMap<String, Map<Long,Double>>();
//		for (long ikrDefId : ikrValues.keySet()) {
//			List<IkrValueBean> valueBeans = ikrValues.get(ikrDefId);			
//			for (IkrValueBean bean : valueBeans) {				
//				IkrUnit toUnit = formats.get(ikrDefId).getIkrUnit();
//				String toUnitSymbol = formats.get(ikrDefId).getIkrUnit().getSymbol();
//				FormattedValue formattedValue = bean.getFormattedValue();
//				IkrUnit unit = formattedValue.getIkrUnit();
//				String unitSymbol = formattedValue.getIkrUnit().getSymbol();
//				String value = formattedValue.getValue();
//				if (!toUnitSymbol.equals(unitSymbol)) {
//					value = convertValue(value, unit, toUnit);				
//				}				
//				
//				long dt = (new Date()).getTime() - bean.getIkrValue().getCaptureTime().getTime();
//				String time = formatter.format(bean.getIkrValue().getCaptureTime());
//				if (dt < maxSlot || maxSlot == 0) {					
//					Map<Long,Double> values = data.get(time);
//					if (values == null) {
//						values = new HashMap<Long, Double>();
//						data.put(time, values);
//					}
//					
//					try {
//						values.put(ikrDefId, Double.parseDouble(value));
//					}
//					catch (NumberFormatException e) {
//						logger.error(e.getMessage(), e);
//						values.put(ikrDefId, 0.0);
//					}
//				}
//				else {
//					List<String> times = valuesToRemove.get(ikrDefId);
//					if (times == null) {
//						times = new ArrayList<String>();
//						valuesToRemove.put(ikrDefId, times);
//					}
//					times.add(time);
//				}
//			}	
//		}
//		
//		synchronized (ikrValues) {
//			for (long id : valuesToRemove.keySet()) {
//				List<String> times = valuesToRemove.get(id);
//				List<IkrValueBean> valueBeans = ikrValues.get(id);
//				Iterator<IkrValueBean> iterator = valueBeans.iterator();
//				synchronized (valueBeans) {
//					while (iterator.hasNext()) {
//						IkrValueBean bean = iterator.next();
//						if (times.contains(formatter.format(bean.getIkrValue().getCaptureTime())))
//							iterator.remove();
//					}
//				}
//			}
//		}			
	}


	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMaxSlot() {
		return maxSlot;
	}	
	
	public ChartModel getChartModel() {
		return chartModel.getModel();
	}
	
	public boolean isLine() {
		return (chartUI instanceof LineChart);
	}
	
	public boolean isPie() {
		return (chartUI instanceof PieChart);
	}
	
	public boolean isBar() {
		return (chartUI instanceof BarChart);
	}
	
	public boolean isFill() {
		boolean ret = false;
		if (chartUI instanceof LineChart) {
			ret = ((LineChart)chartUI).isFill();
		}
		return ret;
	}
	
	public boolean isStacked() {
		boolean ret = false;
		if (chartUI instanceof LineChart) {
			ret = ((LineChart)chartUI).isStacked();
		}
		else if (chartUI instanceof BarChart) {
			ret = ((BarChart)chartUI).isStacked();
		}
		return ret;
	}
	
	public String getOrientation() {
		String ret = "vertical";
		if (chartUI instanceof BarChart) {
			ret = ((BarChart)chartUI).getOrientation();
		}
		return ret;
	}
	
	public Set<Long> getIkrDefinitionIds() {
		return ikrDefinitionIds;
	}
	
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}	

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	
	public UIChart getChartComponent() {
		return chartUI;
	}

	public String getChartStyle() {
		return chartStyle;
	}

}
