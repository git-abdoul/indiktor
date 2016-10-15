package com.fsi.monitoring.applet;

import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import com.fsi.monitoring.util.chart.IkrRealtimeChartFactory;

public class IkrDefinitionRTChartApplet extends JApplet {
	private static final long serialVersionUID = -1298475339807127239L;	

	private Map<Long, TimeSeries> series;
	
	@Override
	public void init() {
		System.out.println("IkDefinitionrRTChartApplet - INIT  ::  Thread ID " + Thread.currentThread().getId());
		
		series = new HashMap<Long, TimeSeries>();
		
		int maxSlot = Integer.parseInt(getParameter("maxSlot"));
		System.out.println("IkrDefinitionRTChartApplet - maxSlot = " + maxSlot);
		
		Map<Long, String> seriesLabels = new HashMap<Long, String>();
		String labelsStr = getParameter("chartSeriesLabels");
		System.out.println("IkrDefinitionRTChartApplet - labelsStr  = " + labelsStr);
		if (labelsStr!=null && labelsStr.length()>0){
			String idx[] = labelsStr.split(">>>");
			for (int i=0; i<idx.length; i++) {
				String[] tmp = idx[i].split("=");
				seriesLabels.put(Long.parseLong(tmp[0]), tmp[1]);
			}			
		}
		
		Map<Long, String> chartUnitsMap = new HashMap<Long, String>();
		String chartUnitsStr = getParameter("chartUnits");
		System.out.println("IkrDefinitionRTChartApplet - chartUnits  = " + chartUnitsStr);
		if (chartUnitsStr!=null && chartUnitsStr.length()>0){
			String idx[] = chartUnitsStr.split(":");
			for (int i=0; i<idx.length; i++) {
				String[] tmp = idx[i].split("=");
				String unit = "";
				if (tmp.length==2)
					unit = (tmp[1]!=null && tmp[1].length()>0)?tmp[1]:IkrRealtimeChartFactory.NO_UNIT;
				chartUnitsMap.put(Long.parseLong(tmp[0]), unit);
			}			
		}
		
		setLayout(new GridLayout(1,1));			
		
		System.out.println("IkrDefinitionRTChartApplet - COMPONENT COUNT  Before Add = " + getContentPane().getComponentCount());
		
		JFreeChart chart = null;
		try {			
			chart = IkrRealtimeChartFactory.getMultipleAxesTimeSeriesChart(chartUnitsMap, seriesLabels, maxSlot, series);
			ChartPanel chartPanel = new ChartPanel(chart);
	        chartPanel.setPopupMenu(null);        
	        getContentPane().add(chartPanel);
			System.out.println("IkrDefinitionRTChartApplet - Chart created : " + chart);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}	
		
		System.out.println("IkrDefinitionRTChartApplet - COMPONENT COUNT  After Add = " + getContentPane().getComponentCount());
	}
	
	public void build(String chartData) {
		if (chartData != null) {
			System.out.println("IkrDefinitionRTChartApplet - Building Chart : " + chartData);
			String[] series = chartData.split("#FINDTS#");
			for (String serie:series) {
				String[] datas = serie.split("#FIN#");	
				String data = datas[0];					
				String [] idx = data.split(";");
				long id = Long.valueOf(idx[0]);
				addData(id, getDataset(serie));
			}			
		}
	}

	private List<String[]> getDataset(String datasetStr) {	
		List<String[]> res = new ArrayList<String[]>();
		String[] datas = datasetStr.split("#FIN#");
		for(String data : datas) {	
			String[] idx = data.split(";");
			String[] value = new String[2];
			value[0]= idx[2];
			value[1]= idx[1];
			res.add(value);
		}			
		return res;
	}
	
	private void addData(long id, List<String[]> values) {
		TimeSeries serie = series.get(id);
		if (serie != null && values != null) {
			for (String[] value : values) {
				double valueDB = Double.parseDouble(value[0]);
				Second time = new Second(new Date(Long.parseLong(value[1])));
				serie.addOrUpdate(time, valueDB);
			}
		}
	}

	@Override
	public void start() {
		System.out.println("IkrDefinitionRTChartApplet - START");
		final String chartData = getParameter("chartData");	
		System.out.println("IkrDefinitionRTChartApplet - chartData : " + chartData);
		if(chartData!=null && chartData.length()>0) {
			Runnable worker = new Runnable() {
				public void run() {
					build(chartData);					
				}				
			};
			(new Thread(worker)).start();
		}
	}	
	
	@Override
	public void stop() {
		System.out.println("IkrDefinitionRTChartApplet - STOP");
	}	
	
	@Override
	public void destroy() {
		System.out.println("IkrDefinitionRTChartApplet - DESTROY");
	}	
}
