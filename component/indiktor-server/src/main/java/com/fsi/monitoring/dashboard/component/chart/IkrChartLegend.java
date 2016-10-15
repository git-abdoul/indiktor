package com.fsi.monitoring.dashboard.component.chart;

import java.io.Serializable;

public class IkrChartLegend implements Serializable {
	private static final long serialVersionUID = -5100624958362644444L;
	
	private String label;
	private long colorId;
	
	public IkrChartLegend(String label, long colorId) {
		super();
		this.label = label;
		this.colorId = colorId;
	}

	public String getLabel() {
		return label;
	}

	public String getColorId() {
		return "color_"+colorId;
	}
}
