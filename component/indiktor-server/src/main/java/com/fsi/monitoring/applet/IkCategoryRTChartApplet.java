package com.fsi.monitoring.applet;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.fsi.monitoring.dashboard.component.chart.IkrChartComponent;
import com.fsi.monitoring.dashboard.component.chart.IkrChartType;
import com.fsi.monitoring.util.chart.IkrRealtimeChartFactory;

public class IkCategoryRTChartApplet extends JApplet {
	private static final long serialVersionUID = -1298475339807127239L;	

	private Map<Long, TimeSeries> series;
	private int maxSlot;
	private Dataset source;
	private String chartType;
//	private List<String> ikrCatkeys;
	
	@Override
	public void init() {
		System.out.println("IkCategoryRTChartApplet - INIT  ::  Thread ID " + Thread.currentThread().getId());
		
		series = new HashMap<Long, TimeSeries>();
		
		chartType = getParameter("chartType");
		System.out.println("IkCategoryRTChartApplet - chartType = " + chartType);
		
		maxSlot = Integer.parseInt(getParameter("maxSlot"));
		System.out.println("IkCategoryRTChartApplet - maxSlot = " + maxSlot);
		
//		ikrCatkeys = new ArrayList<String>();
//		String ikrCategoriesKey = getParameter("ikrCategorieKeys");
//		System.out.println("IkCategoryRTChartApplet - ikrCategoriesKey  = " + ikrCategoriesKey);
//		if (ikrCategoriesKey!=null && ikrCategoriesKey.length()>0){
//			String idx[] = ikrCategoriesKey.split(":");
//			for (String id : idx) {
//				ikrCatkeys.add(id);
//			}			
//		}
		
		String unit = getParameter("chartUnits");
		System.out.println("IkCategoryRTChartApplet - unit  = " + unit);
		
		setLayout(new GridLayout(1,1));			
		
		JFreeChart chart = null;
		if (IkrChartType.CHART_PIE2D.getLabel().equalsIgnoreCase(chartType)) {
			source = new DefaultPieDataset();
			chart = IkrRealtimeChartFactory.getPieChart((DefaultPieDataset) source);
		}
		else {
			source = new TimeSeriesCollection();
			chart = IkrRealtimeChartFactory.getTimeSeriesChart(unit, maxSlot, (TimeSeriesCollection)source);
		}
		
		ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPopupMenu(null);        
        getContentPane().add(chartPanel);
		System.out.println("IkCategoryRTChartApplet - Chart created : " + chart);
		
		final String chartData = getParameter("chartData");	
		System.out.println("IkCategoryRTChartApplet - chartData : " + chartData);
		if(chartData!=null && chartData.length()>0) {
			Runnable worker = new Runnable() {
				public void run() {
					build(chartData);					
				}				
			};
			(new Thread(worker)).start();
		}
	}
	
	public void build(String chartData) {
		if (chartData != null) {
			System.out.println("IkCategoryRTChartApplet - Building Chart : " + chartData);
			String[] series = chartData.split("#FINDTS#");
			for (String serie:series) {
				if (IkrChartType.CHART_PIE2D.getLabel().equalsIgnoreCase(chartType)) {
					addPieData(getDataset(serie));
				}
				else {
					addTimeSeriesData(getDataset(serie));
				}
			}			
		}
	}

	private List<String[]> getDataset(String datasetStr) {	
		List<String[]> res = new ArrayList<String[]>();
		String[] datas = datasetStr.split("#FIN#");
		for(String data : datas) {	
			String[] idx = data.split(";");			
			String label = idx[1];
			label = label.substring(label.indexOf(":") + 1);
			String instance = label.substring(0, label.indexOf("@"));
			String[] value = new String[4];
			value[0]= idx[3];
			value[1]= idx[2];
			value[2]=instance;
			value[3]=idx[0];
			res.add(value);
		}			
		return res;
	}
	
	private void addPieData(List<String[]> values) {
		for (String[] value : values){
			double valueDB = Double.parseDouble(value[0]);
			((DefaultPieDataset)source).setValue(value[2], valueDB);
		}
	}
	
	private void addTimeSeriesData(List<String[]> values) {
		for (String[] value : values) {
			long id = Long.valueOf(value[3]);
			TimeSeries serie = series.get(id);
			if (serie == null) {
				serie = new TimeSeries(value[2], Millisecond.class);
				serie.setMaximumItemAge(maxSlot*1000);
				((TimeSeriesCollection)source).addSeries(serie);
				series.put(id, serie);
			}
			double valueDB = Double.parseDouble(value[0]);
			Second time = new Second(new Date(Long.parseLong(value[1])));
			serie.addOrUpdate(time, valueDB);
		}
	}

	@Override
	public void start() {
		System.out.println("IkCategoryRTChartApplet - START");
	}	
	
	@Override
	public void stop() {
		System.out.println("IkCategoryRTChartApplet - STOP");
	}	
	
	@Override
	public void destroy() {
		System.out.println("IkCategoryRTChartApplet - DESTROY");			
//		try {
//			feeder.unsubscribeMe();
			getContentPane().removeAll();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
	}	
}
