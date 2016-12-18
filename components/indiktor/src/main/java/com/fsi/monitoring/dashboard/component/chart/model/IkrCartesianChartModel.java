package com.fsi.monitoring.dashboard.component.chart.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.kpi.units.IkrUnit;

public class IkrCartesianChartModel extends IkrChartModel {
	private static final long serialVersionUID = -8125854231132710796L;
	
	protected Map<Long, ChartSeries> series;
	
	public IkrCartesianChartModel() {
		super();
		series = new HashMap<Long, ChartSeries>();
	}

	@Override
	protected void initModel() {
		model = new CartesianChartModel();
	}

	//TODO Remove comments otherwise Chrts won't be displayed
	public void init() {
//		int nbOfCategory = chartWidth/50;
//		((CartesianChartModel)model).setNbOfCategory(nbOfCategory);
	}


	@Override
	public void addSeries(long ikrDefinitionId, String label) {
		ChartSeries serie = new ChartSeries(label);
		series.put(ikrDefinitionId, serie);
		labels.put(ikrDefinitionId, label);
		((CartesianChartModel)model).addSeries(serie);
	}

	@Override
	public void updateModel(long ikrDefinitionId, String value, Date time, FormattedValue[] formats) {
		ChartSeries serie = series.get(ikrDefinitionId);		
		if (serie!=null) {
			serie.set(formatter.format(time), Double.parseDouble(value));
			if (formats[0]!=null && formats[0].getIkrUnit().getSymbol().equals(formats[1].getIkrUnit().getSymbol())) {
				IkrUnit currentUnit = formats[0].getIkrUnit();
				IkrUnit toUnit = formats[1].getIkrUnit();
				if (!currentUnit.getSymbol().equals(toUnit.getSymbol())) {
					rebuildSeries(ikrDefinitionId, currentUnit, toUnit);
				}				
			}
			String unit = formats[1].getIkrUnit().getSymbol();
			String newLabel = labels.get(ikrDefinitionId) + ((unit!=null&&unit.length()>0)?"("+unit+")":"");
			serie.setLabel(newLabel);
		}
	}

	@Override
	protected void rebuildSeries(long ikrDefinitionId, IkrUnit currentUnit, IkrUnit toUnit) {
		ChartSeries serie = series.get(ikrDefinitionId);
		Map<Object, Number> oldData = serie.getData();
		Map<Object, Number> newData = new HashMap<Object, Number>();
		for (Object time : oldData.keySet()) {
			double oldValue = (Double)oldData.get(time);
			String newValue = convertValue(String.valueOf(oldValue), currentUnit, toUnit);
			newData.put((String)time, Double.parseDouble(newValue));
		}		
		serie.setData(newData);
	}

}
