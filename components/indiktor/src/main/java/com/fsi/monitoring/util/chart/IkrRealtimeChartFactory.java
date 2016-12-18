package com.fsi.monitoring.util.chart;

import java.awt.BasicStroke;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


public class IkrRealtimeChartFactory {	
	private static final Font defaultFont = new Font("", Font.PLAIN, 10);
	
	public static final String NO_UNIT = "NO_UNIT";
	
	public static JFreeChart getMultipleAxesTimeSeriesChart(Map<Long, String> chartUnitsMap, Map<Long, String> seriesLabels, int maxSlot, Map<Long, TimeSeries> series) throws CloneNotSupportedException {
		JFreeChart chart = null;
		Map<String, TimeSeriesCollection> sources = new HashMap<String, TimeSeriesCollection>();
		Set<String> units = new HashSet<String>(chartUnitsMap.values());
		if (units != null && units.size()>0) {
			System.out.println("IkrRealtimeChartFactory -- units size = " + units.size());
			int i = 0;
			XYPlot plot = null;
			XYLineAndShapeRenderer renderer = null;			
			for (Iterator<String> it=units.iterator(); it.hasNext(); ) {				
				String unitTmp = it.next();	
				String unit = (NO_UNIT.equals(unitTmp))?"":unitTmp;
				System.out.println("IkrRealtimeChartFactory -- unit = " + unit);
				TimeSeriesCollection source = sources.get(unit);
				if (source == null) {
					 source = new TimeSeriesCollection();
				
					if (chart == null) {
						chart = ChartFactory.createTimeSeriesChart("", "Time", unit , source, true, true, false);
						plot = chart.getXYPlot();						
						plot.setNoDataMessage("No data available");
						plot.setDomainZeroBaselineVisible(true);
				        plot.setRangeZeroBaselineVisible(true);
				        
						DateAxis domain = (DateAxis) plot.getDomainAxis();							
				        domain.setAutoRange(true);
				        domain.setLowerMargin(0.0);
				        domain.setUpperMargin(0.0);
				        domain.setTickLabelsVisible(true);
				        domain.setLabelFont(defaultFont);
				        domain.setTickLabelFont(defaultFont);
				        domain.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
				        
				        renderer = (XYLineAndShapeRenderer) plot.getRenderer();
				        renderer.setAutoPopulateSeriesStroke(false);
				        renderer.setBaseStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				        renderer.setDrawSeriesLineAsPath(true);
//				        StandardXYToolTipGenerator g = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("HH:mm"),new DecimalFormat("0.00"));
//				        renderer.setBaseToolTipGenerator(g);
				        
				        NumberAxis axis = (NumberAxis)plot.getRangeAxis();
				        axis.setAutoRange(true);
				        axis.setAutoRangeIncludesZero(false);
//				        axis.setTickUnit(new NumberTickUnit(0.5, new DecimalFormat("#.##")));
				        axis.setNumberFormatOverride(new DecimalFormat("#.##"));
				        axis.setTickLabelsVisible(true);
				        axis.setLabelFont(defaultFont);
				        axis.setTickLabelFont(defaultFont);
				        
				        LegendTitle legend = chart.getLegend();;
				        legend.setItemFont(new Font("", Font.PLAIN, 6));
					}
					else {
						NumberAxis axis = new NumberAxis(unit);
						axis.setAutoRange(true);
						axis.setAutoRangeIncludesZero(false);
						axis.setLabelFont(defaultFont);
						axis.setTickLabelFont(defaultFont);
//						axis.setTickUnit(new NumberTickUnit(0.5, new DecimalFormat("#.##")));
						axis.setNumberFormatOverride(new DecimalFormat("#.##"));
						plot.setRangeAxis(i, axis);
						plot.setDataset(i, source);			
			        	plot.setRenderer(i, (XYItemRenderer) renderer.clone());
			        	plot.mapDatasetToRangeAxis(i, i);	
					}
					sources.put(unit, source);
				}			
  	
				i=i+1;
			} 
			
			for(long id : seriesLabels.keySet()) {
				String unit = chartUnitsMap.get(id);
				String label = seriesLabels.get(id);
				if (unit != null) {
					TimeSeriesCollection source = sources.get(unit);
					TimeSeries serie = new TimeSeries(label, Millisecond.class);					
					if(maxSlot==0)
						maxSlot = 3600000;
					serie.setMaximumItemAge(maxSlot*1000);
					source.addSeries(serie);
					series.put(id, serie);
				}
			}
			
		    ChartFactory.setChartTheme(StandardChartTheme.createJFreeTheme());
		    ChartUtilities.applyCurrentTheme(chart);
		    
//		   if (units.size()>1 && plot.getRangeAxisCount()==chartUnitsMap.size()) {
//			    for (int j=0; j<units.size(); j++) {
//			    	XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer(j);
//					r.setSeriesPaint(0, ChartSeriesColor.get(j));	  
//					NumberAxis axis = (NumberAxis)plot.getRangeAxis(j);
//					axis.setLabelPaint(ChartSeriesColor.get(j));
//		        	axis.setTickLabelPaint(ChartSeriesColor.get(j));
//				}
//			}
		}
		return chart;
	}
	
	public static JFreeChart getTimeSeriesChart(String unit, int maxSlot, TimeSeriesCollection source) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("", "Time", unit, source, true, true, false);		
		XYPlot plot = chart.getXYPlot();	
		plot.setNoDataMessage("No data available");
		
		DateAxis domain = (DateAxis) plot.getDomainAxis();	  
        domain.setAutoRange(true);
        domain.setLowerMargin(0.0);
        domain.setUpperMargin(0.0);
        domain.setTickLabelsVisible(true);	   
        domain.setLabelFont(defaultFont);
        domain.setTickLabelFont(defaultFont);
        domain.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        
        NumberAxis axis = (NumberAxis)plot.getRangeAxis();
        axis.setAutoRange(true);
        axis.setAutoRangeIncludesZero(false);
//        axis.setTickUnit(new NumberTickUnit(0.5, new DecimalFormat("#.##")));
        axis.setNumberFormatOverride(new DecimalFormat("#.##"));
        axis.setTickLabelsVisible(true);
        axis.setLabelFont(defaultFont);
        axis.setTickLabelFont(defaultFont);
        
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setAutoPopulateSeriesStroke(false);
        renderer.setBaseStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setDrawSeriesLineAsPath(true);	
//        StandardXYToolTipGenerator g = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("HH:mm"), new DecimalFormat("0.00"));
//        renderer.setBaseToolTipGenerator(g);
		
	    ChartFactory.setChartTheme(StandardChartTheme.createJFreeTheme());
	    ChartUtilities.applyCurrentTheme(chart);
	    
		return chart;
	}
	
	public static JFreeChart getPieChart(DefaultPieDataset source) {
		JFreeChart chart = ChartFactory.createPieChart("", source, true, true, false);
		
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setNoDataMessage("No data available");
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})")); 
        plot.setSimpleLabels(true);
        plot.setInteriorGap(0.0);
		
	    ChartFactory.setChartTheme(StandardChartTheme.createJFreeTheme());
	    ChartUtilities.applyCurrentTheme(chart);
	    
		return chart;
	}
	
}
