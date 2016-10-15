package com.fsi.monitoring.dashboard.component.chart.model;

import org.primefaces.component.chart.UIChart;
import org.primefaces.component.chart.bar.BarChart;
import org.primefaces.component.chart.line.LineChart;
import org.primefaces.component.chart.pie.PieChart;

import com.fsi.monitoring.dashboard.component.chart.IkrChartType;

public class IkrChartModelFactory {
	
	public static IkrChartModel createChartModel(IkrChartType chartType) {
		IkrChartModel chartModel = null;
		switch (chartType) {
			case CHART_PIE2D:
				chartModel = new IkrPieChartModel();
				break;
				
			case CHART_TIMESERIES:
				chartModel = new IkrTimeSeriesChartModel();
				break;
				
			case CHART_AREA:
			case CHART_AREA_STACKED:
			case CHART_BAR_HORIZONTAL:
			case CHART_BAR_VERTICAL:
			case CHART_BAR_STACKED:
				chartModel = new IkrCartesianChartModel();
				break;
	
			default:
				break;
		}
		return chartModel;
	}
	
	public static UIChart getChartUI(IkrChartType chartType) {
		UIChart chart = null;
		switch (chartType) {
			case CHART_PIE2D:
				chart = new PieChart();
				break;
				
			case CHART_TIMESERIES:
				chart = new LineChart();
				((LineChart)chart).setShowMarkers(false);
				((LineChart)chart).setBreakOnNull(true);
				break;
				
			case CHART_AREA:
				chart = new LineChart();
				((LineChart)chart).setFill(true);
				break;
				
			case CHART_AREA_STACKED:
				chart = new LineChart();
				((LineChart)chart).setFill(true);
				((LineChart)chart).setStacked(true);
				break;
				
			case CHART_BAR_VERTICAL:
				chart = new BarChart();
				((BarChart)chart).setOrientation("vertical");
				break;
			
			case CHART_BAR_HORIZONTAL:
				chart = new BarChart();
				((BarChart)chart).setOrientation("horizontal");
				break;
				
			case CHART_BAR_STACKED:
				chart = new BarChart();
				((BarChart)chart).setStacked(true);
				break;
	
			default:
				break;
		}
		return chart;
	}
}
