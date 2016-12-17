package com.fsi.monitoring.applet;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jfree.chart.JFreeChart;

public interface IAppletChartOnDemand extends Remote {
	public JFreeChart getChart() throws RemoteException;
}
