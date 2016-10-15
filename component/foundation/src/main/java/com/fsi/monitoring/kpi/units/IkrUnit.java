package com.fsi.monitoring.kpi.units;


public interface IkrUnit {
	String name();
	String getSymbol();
	Number getDivider();
	boolean isChartSupported();
}
