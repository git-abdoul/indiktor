package com.fsi.monitoring.kpi.monitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.kpi.compute.MetricCompute;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public class IkrResourceData {
	private static final Logger LOG = Logger.getLogger(IkrResourceData.class);
	
	private Date captureTime;
	
	public IkrResourceData(Date captureTime) {
		this.captureTime = captureTime;
	}
	
	public void getIkrValues(Map<String, Object> fetchedValues, long monitorId, int ikrStaticDataId, List<IkrValue> ikrValues) 
	throws Exception {			
		for (String instance : fetchedValues.keySet()) {
//			AbstractIkrDefinition ikrDefinition = PMFactory.getMonitoringPM().getIkrDefinition(monitorId, ikrStaticDataId, instance, MetricCompute.RT);	
			
			long ikrDefinitionId = PMFactory.getMonitoringPM().getIkrDefinitionId(monitorId, ikrStaticDataId, instance, MetricCompute.RT);
			if (ikrDefinitionId == 0) {
				// This is a new instance								
				// Create new ikrDefinition for this metric label and ikrCategory
				ikrDefinitionId = PMFactory.getMonitoringPM().createIkrDefinition(new IkrDefinition(0, monitorId, ikrStaticDataId, instance, MetricCompute.RT, true));
			}
			
			AbstractIkrDefinition ikrDefinition = PMFactory.getMonitoringPM().getIkrDefinition(ikrDefinitionId);
			
			if (ikrDefinition == null)
				continue;
			
			Object obj = fetchedValues.get(instance);
			if (obj==null)
				continue;			
			
			List<String> values = new ArrayList<String>();
			
			if (obj instanceof String)
				values.add((String)obj);
			else if (obj instanceof List)
				values.addAll((List)obj);
			
			if (ikrDefinition.isActivated() && MetricCompute.RT.equals(ikrDefinition.getIkrCompute())) {
				for (String value : values) {
					IkrValue ikrValue = new IkrValue();
					ikrValue.setValue(value);
					ikrValue.setIkrDefinitionId(ikrDefinition.getId());
					ikrValue.setIkrCategoryId(ikrStaticDataId);
					ikrValue.setCaptureTime(captureTime);					
					ikrValues.add(ikrValue);
				}
			}
		}
	}
}
