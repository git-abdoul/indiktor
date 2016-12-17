package com.fsi.monitoring.util.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ChartSeriesColor {
	private static final List<Color> colors = new ArrayList<Color>();
	
	static {		
		colors.add(0, new Color(255, 0, 0));
		colors.add(1, new Color(204, 102, 51));
		colors.add(2, new Color(153, 153, 0));
		colors.add(3, new Color(153, 204, 0));		
		colors.add(4, new Color(51, 153, 0));
		colors.add(5, new Color(255, 153, 153));		
		colors.add(6, new Color(51, 153, 255));		
		colors.add(7, new Color(153, 153, 255));
		colors.add(8, new Color(51, 51, 255));		
		colors.add(9, new Color(0, 102, 102));
		colors.add(10, new Color(0, 51, 255));
		colors.add(11, new Color(0, 51, 0));
	}
	
	public static Color get(int index) {
		return colors.get(index);
	}
}
