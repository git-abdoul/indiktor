package com.fsi.monitoring.dashboard.component.chart.model;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;


public class IkrTimeSeriesChartModel extends IkrCartesianChartModel {
	private static final long serialVersionUID = -8666350679268963749L;

	public IkrTimeSeriesChartModel() {
		super();
	}

	@Override
	public void addSeries(long ikrDefinitionId, String label) {
		LineChartSeries serie = new LineChartSeries(label);
		serie.setMarkerStyle("");
		series.put(ikrDefinitionId, serie);
		labels.put(ikrDefinitionId, label);
		((CartesianChartModel)model).addSeries(serie);
	}
}
