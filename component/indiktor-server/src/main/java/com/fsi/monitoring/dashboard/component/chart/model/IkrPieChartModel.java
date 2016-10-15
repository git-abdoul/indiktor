package com.fsi.monitoring.dashboard.component.chart.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.primefaces.model.chart.PieChartModel;

import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.kpi.units.IkrUnit;

public class IkrPieChartModel extends IkrChartModel {
	private static final long serialVersionUID = 5846948502153756036L;
	
	public IkrPieChartModel() {
		super();
	}

	@Override
	protected void initModel() {
		model = new PieChartModel();
	}	

	@Override
	public void addSeries(long ikrDefinitionId, String label) {
		labels.put(ikrDefinitionId, label);
	}

	@Override
	public void updateModel(long ikrDefinitionId, String value, Date time, FormattedValue[] formats) {
		String label = labels.get(ikrDefinitionId);
		if (label!=null) {
			String unit = formats[1].getIkrUnit().getSymbol();
			String serie = labels.get(ikrDefinitionId) + ((unit!=null&&unit.length()>0)?"("+unit+")":"");
			((PieChartModel)model).set(serie, Double.parseDouble(value));
			if (formats[0]!=null && formats[0].getIkrUnit().getSymbol().equals(formats[1].getIkrUnit().getSymbol())) {
				IkrUnit currentUnit = formats[0].getIkrUnit();
				IkrUnit toUnit = formats[1].getIkrUnit();
				if (!currentUnit.getSymbol().equals(toUnit.getSymbol())) {
					rebuildSeries(ikrDefinitionId, currentUnit, toUnit);
				}				
			}
		}
	}

	@Override
	protected void rebuildSeries(long ikrDefinitionId, IkrUnit currentUnit, IkrUnit toUnit) {
		Map<String, Number> oldData = ((PieChartModel)model).getData();
		Map<String, Number> newData = new HashMap<String, Number>();
		String unit = toUnit.getSymbol();
		for (String category : oldData.keySet()) {
			Number oldValue = oldData.get(category);
			String newValue = convertValue(String.valueOf(oldValue), currentUnit, toUnit);
			category = category.replace(currentUnit.getSymbol(), unit);
			newData.put(category, Double.parseDouble(newValue));
		}		
		((PieChartModel)model).setData(newData);
	}

}
