package com.fsi.monitoring.util.chart;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import org.krysalis.jcharts.properties.PointChartProperties;

public class ChartSeriesShape {
private static final List<Shape> shapes = new ArrayList<Shape>();
	
	static {		
		shapes.add(0, PointChartProperties.SHAPE_CIRCLE);
		shapes.add(1, PointChartProperties.SHAPE_DIAMOND);
		shapes.add(2, PointChartProperties.SHAPE_TRIANGLE);
		shapes.add(3, PointChartProperties.SHAPE_SQUARE);
	}
	
	public static Shape get(int index) {
		return shapes.get(index);
	}
}
