package com.fsi.monitoring.dashboard.component.chart;

import com.icesoft.faces.component.outputchart.OutputChart;

public enum IkrChartType {	
	CHART_TIMESERIES(OutputChart.LINE_CHART_TYPE),
	CHART_PIE2D(OutputChart.PIE2D_CHART_TYPE),
	CHART_AREA(OutputChart.AREA_CHART_TYPE),
	CHART_AREA_STACKED(OutputChart.AREA_STACKED_CHART_TYPE),
	CHART_BAR_VERTICAL("Vertical Bar"),
	CHART_BAR_HORIZONTAL("Horizontal Bar"),
	CHART_BAR_STACKED(OutputChart.BAR_STACKED_CHART_TYPE);
	
	private String label;
	
	private IkrChartType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	public static IkrChartType getChartType (String name) {
		IkrChartType type = CHART_TIMESERIES;
		if (OutputChart.PIE2D_CHART_TYPE.equals(name)) {
			type = CHART_PIE2D;
		}
		else if (OutputChart.AREA_CHART_TYPE.equals(name)) {
			type = CHART_AREA;
		}
		else if (OutputChart.AREA_STACKED_CHART_TYPE.equals(name)) {
			type = CHART_AREA_STACKED;
		}
		else if ("Vertical Bar".equals(name)) {
			type = CHART_BAR_VERTICAL;
		}
		else if ("Horizontal Bar".equals(name)) {
			type = CHART_BAR_HORIZONTAL;
		}
		else if (OutputChart.BAR_STACKED_CHART_TYPE.equals(name)) {
			type = CHART_BAR_STACKED;
		}		
		return type;
	}
}
