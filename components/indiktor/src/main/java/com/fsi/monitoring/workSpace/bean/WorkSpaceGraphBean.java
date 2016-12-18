package com.fsi.monitoring.workSpace.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.kpi.units.FormattedValue;


public class WorkSpaceGraphBean
extends WorkSpaceComponentBean {

	private static final long serialVersionUID = -731895393976355236L;

	private String chartData;
	private String ikrDefIds;
	private Map<Long,String> chartLabels;
	private Map<Long, FormattedValue> formats;
	private Map<Long, List<IkrValueBean>> valueBeans;
	
	public WorkSpaceGraphBean(String wsId, String chartData, Map<Long,String> chartLabels, Map<Long, FormattedValue> formats) {
		super(wsId,Type.graph);
		this.chartData = chartData;
		this.chartLabels =chartLabels;
		this.formats = formats;
	}
	
	public String getChartData() {
		return chartData;
	}
	
	public void setIkrDefIds(String ikrDefIds) {
		this.ikrDefIds = ikrDefIds;
	}
	
	public String getIkrDefIds() {
		return ikrDefIds;
	}	
	
	public Map<Long, String> getChartLabels() {
		return chartLabels;
	}

	public Map<Long, String> getFormats() {
		Map<Long, String> units = new HashMap<Long, String>();
		for(long id : formats.keySet()) {
			units.put(id, formats.get(id).getIkrUnit().getSymbol());
		}
		return units;
	}

	public String getChartSeriesLabels() {
		String str = "";
		int i = 0;
		for(long id : chartLabels.keySet()) {
			String tmp = id + "=" + chartLabels.get(id);
			if (i==0)
				str = tmp;
			else
				str = str + ">>>" + tmp;
			i = i + 1;
		}
		return str;
	}
	
	public String getChartUnits() {
		String str = "";
		int i = 0;
		for(long id : formats.keySet()) {
			String tmp = id + "=" + formats.get(id).getIkrUnit().getSymbol();
			if (i==0)
				str = tmp;
			else
				str = str + ":" + tmp;
			i = i + 1;
		}
		return str;
	}

	public Map<Long, List<IkrValueBean>> getValueBeans() {
		return valueBeans;
	}

	public void setValueBeans(Map<Long, List<IkrValueBean>> valueBeans) {
		this.valueBeans = valueBeans;
	}	
}
