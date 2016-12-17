package com.fsi.monitoring.dashboard.component.chart.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.primefaces.model.chart.ChartModel;

import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.kpi.units.IkrUnit;

public abstract class IkrChartModel implements Serializable {
	private static final Logger logger = Logger.getLogger(IkrChartModel.class);
	private static final long serialVersionUID = 1522928642593541004L;
	
	protected static final Format formatter = new SimpleDateFormat("HH:mm:ss");
	
	protected Map<Long, String> labels;
	
	protected int chartHeight;
	protected int chartWidth;
	
	protected ChartModel model;

	public IkrChartModel() {
		labels = new HashMap<Long, String>();
		initModel();
	}
	
	public void init() {}
	
	protected abstract void initModel();
	public abstract void addSeries(long ikrDefinitionId, String label);	
	public abstract void updateModel(long ikrDefinitionId, String value, Date time, FormattedValue[] formats);	
	protected abstract void rebuildSeries(long ikrDefinitionId, IkrUnit currentUnit, IkrUnit toUnit);
	
	protected String convertValue(String value, IkrUnit fromUnit, IkrUnit toUnit) {
		String res = null;
		try {
		    Method method = fromUnit.getClass().getMethod("convertTo", new Class[] {String.class, toUnit.getClass()});
		    Object o = method.invoke(fromUnit, new Object[] {value, toUnit});		    
		    res = (String)o;
		} catch (NoSuchMethodException exc1) {
			logger.error(exc1.getMessage(), exc1);
		} catch (IllegalAccessException exc2) {
			logger.error(exc2.getMessage(), exc2);
		} catch (InvocationTargetException exc3) {
			Throwable exc = exc3.getTargetException();
			if (exc instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)exc;
			} else {
				logger.error(exc.getMessage(), exc);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return (res!=null)?res:value;
	}
	
	public ChartModel getModel() {
		return model;
	}


	public void setChartHeight(int chartHeight) {
		this.chartHeight = chartHeight;
	}


	public void setChartWidth(int chartWidth) {
		this.chartWidth = chartWidth;
	}
}
