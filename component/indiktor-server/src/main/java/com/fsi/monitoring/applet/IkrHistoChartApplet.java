package com.fsi.monitoring.applet;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JApplet;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.time.TimeSeriesCollection;

import com.fsi.monitoring.util.chart.IkrHistoChartFactory;

public class IkrHistoChartApplet extends JApplet {
	private static final long serialVersionUID = -1298475339807127239L;	
	
	private JFreeChart chart;
	private Map<String, TimeSeriesCollection> sources;

	@Override
	public void init() {
		final Map<Long, String> seriesLabels = new HashMap<Long, String>();
		String labelsStr = getParameter("chartSeriesLabels");
		System.out.println("IkrHistoChartApplet - labelsStr  = " + labelsStr);
		if (labelsStr!=null && labelsStr.length()>0){
			String idx[] = labelsStr.split(">>>");
			for (int i=0; i<idx.length; i++) {
				String[] tmp = idx[i].split("=");
				seriesLabels.put(Long.parseLong(tmp[0]), tmp[1]);
			}			
		}
		
		final Map<Long, String> chartUnitsMap = new HashMap<Long, String>();
		String chartUnitsStr = getParameter("chartUnits");
		System.out.println("IkrHistoChartApplet - chartUnits  = " + chartUnitsStr);
		if (chartUnitsStr!=null && chartUnitsStr.length()>0){
			String idx[] = chartUnitsStr.split(":");
			for (int i=0; i<idx.length; i++) {
				String[] tmp = idx[i].split("=");
				String unit = "";
				if (tmp.length==2)
					unit = tmp[1];
				chartUnitsMap.put(Long.parseLong(tmp[0]), unit);
			}			
		}
		
		setLayout(new GridLayout(1,1));
		
		if (sources == null)
			sources = new Hashtable<String, TimeSeriesCollection>();
		
		try {
			chart = IkrHistoChartFactory.getTimeSeriesChart(chartUnitsMap, seriesLabels, sources, StandardChartTheme.createDarknessTheme());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		if (chart != null) {		
	        ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPopupMenu(null);        
	        getContentPane().add(chartPanel);
		}
		
		final String chartData = getParameter("chartData");		
		if(chartData!=null && chartData.length()>0) {
			Runnable worker = new Runnable() {
				public void run() {
					IkrHistoChartFactory.build(chartData, chartUnitsMap, seriesLabels, sources);					
				}				
			};
			(new Thread(worker)).start();
		}
	}

	@Override
	public void start() {
		System.out.println("IkrHistoChartApplet - START");
	}

	@Override
	public void stop() {
		System.out.println("IkrHistoChartApplet - STOP");
	}	
	
	@Override
	public void destroy() {
		System.out.println("IkrHistoChartApplet - DESTROY");		
		getContentPane().removeAll();
	}	
}
