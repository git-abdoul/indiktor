package com.fsi.monitoring.test;

import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;


public class ChartDemoBean implements Serializable {
	private static final long serialVersionUID = -574273352031596690L;
	
	private static final Format formatter = new SimpleDateFormat("HH:mm:ss");
	
	private CartesianChartModel categoryModel;
    private CartesianChartModel linearModel;
    private CartesianChartModel liveModel;
    
    LineChartSeries liveSeries;
    
    

	public ChartDemoBean() {
        createCategoryModel();
        createLinearModel();
        createLiveModel();
	}

    public CartesianChartModel getCategoryModel() {
        return categoryModel;
    }

    public CartesianChartModel getLinearModel() {
        return linearModel;
    }
    
    public CartesianChartModel getLiveModel() {    	
        return liveModel;
    }
    
    private void createLiveModel() {
    	liveModel = new CartesianChartModel();
        liveSeries = new LineChartSeries();
        liveSeries.setLabel("memory(kb)");
        liveModel.addSeries(liveSeries);
    }
    
    public void updateValue() {
    	 Calendar cal = Calendar.getInstance();
         //cal.setTime(new Date());         
         String time = formatter.format(cal.getTime());
         liveSeries.set(time, Math.random()*27+100);
    }

    private void createCategoryModel() {
        categoryModel = new CartesianChartModel();

        LineChartSeries boys = new LineChartSeries();
        boys.setLabel("Boys");
        LineChartSeries girls = new LineChartSeries();
        girls.setLabel("Girls");
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        
        
        for (int i=0; i<60; i++) {
        	 cal.add(Calendar.MINUTE, 1);
        	 String time = formatter.format(cal.getTime());
        	 boys.set(time, Math.random()*12+10);
        	 girls.set(time, Math.random()*8+15);
        }

        categoryModel.addSeries(boys);
        categoryModel.addSeries(girls);
    }

    private void createLinearModel() {
        linearModel = new CartesianChartModel();

        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series 1");

        series1.set(1, 2);
        series1.set(2, 1);
        series1.set(3, 3);
        series1.set(4, 6);
        series1.set(5, 8);

        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Series 2");
        series2.setMarkerStyle("diamond");

        series2.set(1, 6);
        series2.set(2, 3);
        series2.set(3, 2);
        series2.set(4, 7);
        series2.set(5, 9);

        linearModel.addSeries(series1);
        linearModel.addSeries(series2);
    }
}
