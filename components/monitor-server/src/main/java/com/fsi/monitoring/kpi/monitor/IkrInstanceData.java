package com.fsi.monitoring.kpi.monitor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public class IkrInstanceData {
	private static final Logger logger = Logger.getLogger(IkrInstanceData.class);	
	
	private Map<String, Method> methods;
	private String ikrInstance;	
	private Date captureTime;
	
	public IkrInstanceData(String ikrInstance,
						   Date captureTime) {
		this.ikrInstance = ikrInstance;
		this.captureTime = captureTime;
		methods = new HashMap<String, Method>();
	}
	
	public String getIkrInstance() {
		return ikrInstance;
	}
	
	public void getIkrValues(Collection<IkrDefinition> ikrDefinitions, List<IkrValue> ikrValues) 
	throws Exception {			
		// for each activated ikrDefinition, create a corresponding ikrValue
		for (IkrDefinition ikrDefinition : ikrDefinitions) {
			if (ikrDefinition.isActivated() && ikrDefinition.getIkrCompute() == null) {
				try {
					IkrCategory category = (IkrCategory)PMFactory.getDataModelPM().getIkrStaticDomain(ikrDefinition.getIkrCategoryId());
					String value = null;
					String methodName = null;
	
					Method method = methods.get(methodName);
					if (method == null) {
						method = this.getClass().getMethod(methodName, null);
						methods.put(methodName, method);
					}
					value = (String)method.invoke(this, null);
							
					if (value == null)
						continue;
		
					IkrValue ikrValue = new IkrValue();
					ikrValue.setValue(value);
					ikrValue.setIkrDefinitionId(ikrDefinition.getId());
					ikrValue.setIkrCategoryId(category.getId());
					ikrValue.setCaptureTime(captureTime);					
					ikrValues.add(ikrValue);
				} catch (Exception exc) {
					logger.error("Error while fetching data for IkrCategoryId " + ikrDefinition.getIkrCategoryId() + "," + ikrInstance,exc);
				}
			}
		}
	}
}
