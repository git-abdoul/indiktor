package com.fsi.monitoring.realTime.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fsi.monitoring.RealTimeValue;
import com.fsi.monitoring.datamodel.bean.IkrDefinitionBean;
import com.fsi.monitoring.datamodel.bean.IkrValueBean;
import com.fsi.monitoring.datamodel.bean.factory.BeanPM;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.realTime.subscriber.RealTimeComponent;

public class IkrValueRealTimeListener extends RealtimeValueListener {
	
	private Map<Long,Collection<RealTimeComponent>> ikrDefinitionSubscribers;
	private Map<Integer,Collection<RealTimeComponent>> ikrCategorySubscribers;
	
	private BeanPM beanPM;
	
	public IkrValueRealTimeListener() {
		ikrDefinitionSubscribers = new HashMap<Long,Collection<RealTimeComponent>>();
		ikrCategorySubscribers = new HashMap<Integer,Collection<RealTimeComponent>>();
	}
	
	public void subscribeIkrDefinitionComponent(long ikrDefinitionId, RealTimeComponent subscriber) {
		Collection<RealTimeComponent> subscribers = ikrDefinitionSubscribers.get(ikrDefinitionId);
		if (subscribers == null) {
			subscribers = new ArrayList<RealTimeComponent>();
			ikrDefinitionSubscribers.put(ikrDefinitionId, subscribers);
		}
		
		subscribers.add(subscriber);
	}
	
	public void unSubscribeIkrDefinitionComponent(long ikrDefinitionId,
												  RealTimeComponent subscriber) {
		Collection<RealTimeComponent> subscribers = ikrDefinitionSubscribers.get(ikrDefinitionId);
		if (subscribers != null)
			subscribers.remove(subscriber);
	}
	
	public void unSubscribeIkrCategoryComponent(int ikrCategoryId,
			  									RealTimeComponent subscriber) {
		Collection<RealTimeComponent> subscribers = ikrCategorySubscribers.get(ikrCategoryId);
		if (subscribers != null)
			subscribers.remove(subscriber);
	}	
	
	public void subscribeIkrCategoryComponent(int ikrCategoryId,
											  RealTimeComponent subscriber) {
		Collection<RealTimeComponent> subscribers = ikrCategorySubscribers.get(ikrCategoryId);
		if (subscribers == null) {
			subscribers = new ArrayList<RealTimeComponent>();
			ikrCategorySubscribers.put(ikrCategoryId, subscribers);
		}
		
		subscribers.add(subscriber);
	}

	@Override
	protected void notifRealTimeSubscribers(Collection<? extends RealTimeValue> realtimeValues) {
		for (RealTimeValue rtValue : realtimeValues) { 
			IkrValue ikrValue = (IkrValue)rtValue;
			long ikrDefinitionId = ikrValue.getValueDefinitionId();
			int ikrCategoryId = ikrValue.getIkrCategoryId();

			IkrDefinitionBean ikrDefinitionBean = beanPM.getIkrDefinitionBean(ikrValue.getValueDefinitionId());
			IkrValueBean valueBean = new IkrValueBean(ikrDefinitionBean,ikrValue);
			
			Collection<RealTimeComponent> ikrDefSubscribers = ikrDefinitionSubscribers.get(ikrDefinitionId);
			
			if (ikrDefSubscribers!= null && !ikrDefSubscribers.isEmpty()) {
				for (RealTimeComponent realTimeComponent : ikrDefSubscribers) {
					realTimeComponent.push(valueBean);
				}
			}
			
			Collection<RealTimeComponent> ikrCatSubscribers = ikrCategorySubscribers.get(ikrCategoryId);
			if (ikrCatSubscribers!= null && !ikrCatSubscribers.isEmpty()) {
				for (RealTimeComponent realTimeComponent : ikrCatSubscribers) {
					realTimeComponent.push(valueBean);
				}
			}
		}
	}

	@Override
	protected void initRealTimeValues() {
		// TODO Auto-generated method stub		
	}
	
	public void setBeanPM(BeanPM beanPM) {
		this.beanPM = beanPM;
	}	
}
