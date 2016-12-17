package com.fsi.monitoring.util.chart;

import java.awt.BasicStroke;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


public class IkrHistoChartFactory {
	
	public static JFreeChart getTimeSeriesChart(Map<Long, String> chartUnitsMap, Map<Long, String> seriesLabels, Map<String, TimeSeriesCollection> sources, ChartTheme theme) throws CloneNotSupportedException {
		JFreeChart chart = null;
		Set<String> units = new HashSet<String>(chartUnitsMap.values());
		if (units != null && units.size()>0) {
			int i = 0;
			XYPlot plot = null;
			XYLineAndShapeRenderer renderer = null;
			for (Iterator<String> it=units.iterator(); it.hasNext(); ) {
				String unit = it.next();
				TimeSeriesCollection source = sources.get(unit);
				if (source == null) {
					source = new TimeSeriesCollection();
					 
					if (chart == null) {
						chart = ChartFactory.createTimeSeriesChart("", "Time", unit , source, true, true, false);
						plot = chart.getXYPlot();	
						plot.setNoDataMessage("No data available");
					    DateAxis domain = (DateAxis) plot.getDomainAxis();	        
					    domain.setLowerMargin(0.0);
					    domain.setUpperMargin(0.0);
					    domain.setDateFormatOverride(new SimpleDateFormat("dd/MM HH:mm"));
					    domain.setTickLabelsVisible(true);	    
					    
					    NumberAxis axis = (NumberAxis)plot.getRangeAxis();
				        axis.setAutoRange(true);
				        axis.setAutoRangeIncludesZero(false);
				        axis.setNumberFormatOverride(new DecimalFormat("#.##"));
				        axis.setTickLabelsVisible(true);
					        
					    LegendTitle legend = chart.getLegend();;
					    legend.setItemFont(new Font("", Font.PLAIN, 6));
					        
					    renderer = (XYLineAndShapeRenderer) plot.getRenderer();
					    renderer.setAutoPopulateSeriesStroke(false);
					    renderer.setBaseStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					    renderer.setDrawSeriesLineAsPath(true);	
//					    StandardXYToolTipGenerator g = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("dd/MM HH:mm"), new DecimalFormat("0.00"));
//				        renderer.setBaseToolTipGenerator(g);
					        
					 }
					 else {
						 NumberAxis axis = new NumberAxis(unit);
						 axis.setAutoRange(true);
						 axis.setAutoRangeIncludesZero(false);
						 axis.setNumberFormatOverride(new DecimalFormat("#.##"));
						 axis.setTickLabelsVisible(true);
						 plot.setRangeAxis(i, axis);
						 plot.setDataset(i, source);			
						 plot.setRenderer(i, (XYItemRenderer) renderer.clone());
						 plot.mapDatasetToRangeAxis(i, i);	
					 }
					sources.put(unit, source);
				}								      	
				i=i+1;
			} 
			
		    ChartFactory.setChartTheme(theme);
		    ChartUtilities.applyCurrentTheme(chart);
//		    
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
	
	public static void build(String chartData, Map<Long, String> chartUnitsMap, Map<Long, String> seriesLabels, Map<String, TimeSeriesCollection> sources) {
		if (chartData != null) {
			String[] series = chartData.split("#FINDTS#");
			for (String serie:series) {
				String[] datas = serie.split("#FIN#");	
				String data = datas[0];					
				String [] idx = data.split(";");
				long id = Long.parseLong(idx[0]);
				String unit = chartUnitsMap.get(id);
				TimeSeriesCollection source = sources.get(unit);
				processChartData(source, seriesLabels, serie);
			}			
		}
	}

	private static void processChartData(TimeSeriesCollection source, Map<Long, String> seriesLabels, String datasetStr) {		
		TimeSeries serie = null;
		String[] datas = datasetStr.split("#FIN#");
		for(String data : datas) {	
			String [] idx = data.split(";");
			Minute time = new Minute(new Date(Long.parseLong(idx[1])));
			double value = Double.parseDouble(idx[2]);
			if(serie == null) {
				long ikrDefId = Long.parseLong(idx[0]);
				serie = new TimeSeries(seriesLabels.get(ikrDefId));
				source.addSeries(serie);
			}
			serie.addOrUpdate(time, value);	
		}	
	}
}
