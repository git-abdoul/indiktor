package com.fsi.monitoring.dashboard.thread.component;



import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fsi.monitoring.dashboard.component.framework.ComputableComponent;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.RealTimeBean;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponent;

public abstract class ThreadComponent<T>
extends DashBoardComponent
implements RealTimeComponent, ComputableComponent {
	
	private static final Logger logger = Logger.getLogger(ThreadComponent.class);	
	
	private String processName;
	private String hostname;

	private Map<String,T> beans;
	
	protected Collection<Integer> ikrCategoryIds;
	private Map<String,IkrValue> ikrValues;
	 
	private MonitoringPM monitoringPM;
	
	protected boolean computationNeeded = true;

	public ThreadComponent(String componentId,
						   String title,
						   String style,
						   boolean rendered,
						   String processName, 
						   String hostname,
						   DataModelPM dataModelPM) {
		super(componentId, title, style, "thread", rendered);
		this.processName = processName.toLowerCase();
		this.hostname = hostname.toLowerCase();
		beans = new HashMap<String,T>();
		ikrValues = new HashMap<String,IkrValue>();
		ikrCategoryIds = new HashSet<Integer>();
		initIkrCategories(dataModelPM);
	}
	
	@Override
	public void synchronize() {
		// TODO Auto-generated method stub
		
	}
	
	protected String getLabel(String instance) {
		int index = instance.indexOf("#");
		return instance.substring(index + 1);
	}

	public void setMonitoringPM(MonitoringPM monitoringPM) {
		this.monitoringPM = monitoringPM;
	}
	
	public Collection<T> getBeans() {
		return beans.values();
	}

	public void computeComponent() {
		if (computationNeeded) {
			for(Map.Entry<String, IkrValue> entry : ikrValues.entrySet()) {
				String label = entry.getKey().split(":")[0];
				IkrValue ikrValue = entry.getValue();
				T detailBean = beans.get(label);
				if (detailBean == null) {
					// No bean yet for this def
					detailBean = getBean(label);
					beans.put(label, detailBean);
				}
	
				updateBean(detailBean, ikrValue);	
			}
			computationNeeded = false;
		}
	}
	
	protected abstract T getBean(String label);
	protected abstract void updateBean(T bean,IkrValue ikrValue);
	protected abstract void initIkrCategories(DataModelPM dataModelPM);
	
	public Collection<Integer> getIkrCategoryIds() {
		return ikrCategoryIds;
	}
	
	public void push(RealTimeBean valueBean) {
		try {	
			IkrValueBean ikrValueBean = (IkrValueBean)valueBean;
			IkrValue ikrValue = ikrValueBean.getIkrValue();
					
			if (ikrCategoryIds.contains(ikrValue.getIkrCategoryId())) {
				AbstractIkrDefinition ikrDefinition = monitoringPM.getIkrDefinition(ikrValue.getValueDefinitionId());
		
//				if (ikrDefinition.getIkrInstance().toLowerCase().contains(processName)
//					&& ikrDefinition.getIkrContext().toLowerCase().contains(hostname)) {					
//						String label = getLabel(ikrDefinition.getIkrInstance());		
//						ikrValues.put(label + ":" + ikrValue.getIkrCategoryId(),ikrValue);	
//						computationNeeded = true;
//				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
