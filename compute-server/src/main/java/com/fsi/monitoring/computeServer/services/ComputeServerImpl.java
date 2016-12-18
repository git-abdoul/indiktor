package com.fsi.monitoring.computeServer.services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nfunk.jep.JEP;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.Alert;
import com.fsi.monitoring.cache.StaticDataCacheEventListener;
import com.fsi.monitoring.computeServer.alert.ComputeAlert;
import com.fsi.monitoring.computeServer.statistic.AbstractIkrComputeStatistics;
import com.fsi.monitoring.computeServer.statistic.IkrComputeStatistics;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsMulticastProducer;
import com.fsi.monitoring.jms.JmsProcessorFactory;
import com.fsi.monitoring.jms.JmsProcessorType;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.CrossComputeDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.msd.StaticData;

public class ComputeServerImpl
extends UnicastRemoteObject 
implements ComputeServerService, CacheEventListener {

	private static final long serialVersionUID = -2884418430508762508L;
	private static final Logger logger = Logger.getLogger(ComputeServerImpl.class);		
	
	private static final NumberFormat nf = new DecimalFormat("#.##"); 

	private ComputeAlert computeAlert = null;
	
	private JmsProcessorFactory jmsFactory;
	private JmsMulticastProducer alertProducer;
	private JmsMulticastProducer ikrValueProducer;

	private MonitoringPM monitoringPM;
	private DataModelPM dataModelPM;

	private Map<Long,IkrValue> currentIkrValuesCache = Collections.synchronizedMap(new HashMap<Long,IkrValue>());
	private Map<Long,IkrValue> oldIkrValuesCache = Collections.synchronizedMap(new HashMap<Long,IkrValue>());
	
	private Map<Long, IkrComputeStatistics> ikrComputeStatistics;
	
	public ComputeServerImpl() throws RemoteException {
		computeAlert = new ComputeAlert();	
	}
	
	public void init() {
		alertProducer = (JmsMulticastProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.ALERT);
		ikrValueProducer = (JmsMulticastProducer)jmsFactory.getProducerJmsProcessor(JmsProcessorType.IKR_VALUE);
		
		ikrComputeStatistics = new HashMap<Long, IkrComputeStatistics>();		
		
		StaticDataCacheEventListener cacheEventListener = StaticDataCacheEventListener.getInstance();
		cacheEventListener.addObserver(this);
		
		// load The static Data for computation
		 try {
			Map<Long, AbstractIkrDefinition> staticDataDefs = monitoringPM.loadStaticDataDefinitions();
			for(AbstractIkrDefinition ikrDefinition : staticDataDefs.values()) {
				IkrValue ikrValue = new IkrValue();
				ikrValue.setCaptureTime(new Date());
				ikrValue.setIkrCategoryId(ikrDefinition.getIkrCategoryId());
				ikrValue.setIkrDefinitionId(ikrDefinition.getId());
				ikrValue.setValue(((StaticData)ikrDefinition).getValue());
				currentIkrValuesCache.put(ikrDefinition.getId(), ikrValue);
			}
		} catch (PersistenceException e) {
			logger.error(e.getMessage(), e);
		}
		
		computeAlert.setDataModelPM(dataModelPM);
	}	
	
	public void setJmsFactory(JmsProcessorFactory jmsFactory) {
		this.jmsFactory = jmsFactory;
	}

	public void setMonitoringPM(MonitoringPM monitoringPM) {
		this.monitoringPM = monitoringPM;
	}
	
	public void setDataModelPM(DataModelPM dataModelPM) {
		this.dataModelPM = dataModelPM;
	}	
	
	public void processAndSend(Collection<IkrValue> ikrValues)
	throws RemoteException {
		
		if (ikrValues == null) {
			logger.error("Received null values in saveIkrValues");
			return;
		} else if (ikrValues.isEmpty()) {
			return;
		}
		
		// The defIds for which the value has changed
		Collection<Long> defIds = new ArrayList<Long>();
		Collection<Long> crossComputeIds = new ArrayList<Long>();
		
		Map<Long,List<IkrValue>> ikrValuesTempCache = new HashMap<Long, List<IkrValue>>();
		for (IkrValue ikrValue : ikrValues) {
			long defId = ikrValue.getValueDefinitionId();
			if (!defIds.contains(defId))
				defIds.add(defId);
			List<IkrValue> values = ikrValuesTempCache.get(defId);					
			if (values == null) {
				values = new ArrayList<IkrValue>();
				ikrValuesTempCache.put(defId, values);
			}
			values.add(ikrValue);
		}
		
		try {			
			for (long defId : ikrValuesTempCache.keySet()) {	
				IkrValue ikrValue = getLastValue(ikrValuesTempCache.get(defId));
				currentIkrValuesCache.put(defId, ikrValue);
				try {
					IkrDefinition definition = (IkrDefinition)monitoringPM.getIkrDefinition(defId);
					Collection<Long> tmpCrossComputeIds = definition.getLinkedCrossComputeDefinitionIds();
					if (tmpCrossComputeIds != null && tmpCrossComputeIds.size()>0) {
						crossComputeIds.addAll(tmpCrossComputeIds);
					}
				} catch(Exception exc) {
					logger.error(exc.getMessage(), exc);
				}
			}
			
			Collection<IkrValue> crossComputedValues = computeCrossValues(crossComputeIds);
			// update the local cache
			for (IkrValue ikrValue : crossComputedValues) {
				long defId = ikrValue.getValueDefinitionId();
				defIds.add(defId);
				List<IkrValue> values = ikrValuesTempCache.get(defId);					
				if (values == null) {
					values = new ArrayList<IkrValue>();
					ikrValuesTempCache.put(defId, values);
				}
				values.add(ikrValue);
				currentIkrValuesCache.put(defId, ikrValue);
			}
			ikrValues.addAll(crossComputedValues);
			
			// compute statics
			Collection<IkrValue> statisticComputedValues = new ArrayList<IkrValue>();			
			for (IkrValue ikrValue : ikrValues) {
				Collection<IkrValue> tmpStatisticComputedValues = computeIkrStatisticsValue(ikrValue);
				if (tmpStatisticComputedValues!=null && tmpStatisticComputedValues.size()>0){
					statisticComputedValues.addAll(tmpStatisticComputedValues);				
					for(IkrValue computedValue : tmpStatisticComputedValues) {
						try {
							long defId = computedValue.getValueDefinitionId();
							IkrDefinition definition = (IkrDefinition)monitoringPM.getIkrDefinition(defId);
							Collection<Long> tmpCrossComputeIds = definition.getLinkedCrossComputeDefinitionIds();
							if (tmpCrossComputeIds != null && tmpCrossComputeIds.size()>0) {
								crossComputeIds.addAll(tmpCrossComputeIds);
							}
							Collection<IkrValue> crossComputedValues2 = computeCrossValues(crossComputeIds);
							statisticComputedValues.addAll(crossComputedValues2);
						} catch(Exception exc) {
							logger.error(exc.getMessage(), exc);
						}
					}		
				}
			}			
			
			// update the local cache
			for (IkrValue ikrValue : statisticComputedValues) {
				long defId = ikrValue.getValueDefinitionId();
				defIds.add(defId);
				List<IkrValue> values = ikrValuesTempCache.get(defId);					
				if (values == null) {
					values = new ArrayList<IkrValue>();
					ikrValuesTempCache.put(defId, values);
				}
				values.add(ikrValue);
				currentIkrValuesCache.put(defId, ikrValue);
			}
			ikrValues.addAll(statisticComputedValues);	
			
			monitoringPM.saveIkrValues(filterIkrValues(ikrValuesTempCache), false);	
			sendIkrValues(ikrValues);			
			
			Collection<Alert> alerts = computeAlert.computeAlerts(defIds, ikrValuesTempCache);
			sendAlerts(alerts);

		} catch(Exception e){
			logger.error("Process and Send Error", e);
			e.printStackTrace();
			throw new RemoteException("Process and Send Error",e);
		}
	}

//	public void addAlertComment(long alertDefinitionId, AlertCommentEvent event)
//	throws RemoteException {
//		Alert alert = computeAlert.addAlertComment(alertDefinitionId, event);
//		
//		Collection<Alert> alerts = new ArrayList<Alert>(1);
//		alerts.add(alert);
//		
//		try {
//			sendAlerts(alerts);
//		} catch(Exception e){
//			logger.error("Add alert comment Error", e);
//			throw new RemoteException("Add alert comment Error",e);
//		}
//	}
	
	private IkrValue getLastValue(List<IkrValue> ikrValues) {
		IkrValue value = null;
		Collections.sort(ikrValues, new Comparator<IkrValue>() {
			public int compare(IkrValue o1, IkrValue o2) {
				return o2.getCaptureTime().compareTo(o1.getCaptureTime());
			}
		});		
		value = ikrValues.get(0);
		return value;
	}
	
	private Collection<IkrValue> filterIkrValues(Map<Long,List<IkrValue>> ikrValuesMap) throws Exception {
		Collection<IkrValue> filteredIkrValues = new ArrayList<IkrValue>();		
		for (long ikrDefId : ikrValuesMap.keySet()) {
			List<IkrValue> ikrValues = ikrValuesMap.get(ikrDefId);
			Iterator<IkrValue> ikrValueIT = ikrValues.iterator();
			while(ikrValueIT.hasNext()) {
				IkrValue oldValue = oldIkrValuesCache.get(ikrDefId);
				IkrValue newValue = ikrValueIT.next();								
				int ikrCatId = newValue.getIkrCategoryId();
				IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(ikrCatId);
				if (ikrCategory != null) {
					if (isThresholdVerified(newValue,oldValue,ikrCategory) && ikrCategory.isPersistent()) {
						filteredIkrValues.add(newValue);
						oldIkrValuesCache.put(ikrDefId, newValue);
						currentIkrValuesCache.put(ikrDefId, newValue);
					} else {
						logger.debug("IkrValue discarded : ikrDefinitionId=" + ikrDefId + ", value=" + newValue.getValue().toString());
					}
				}						
			}	
		}
		return filteredIkrValues;
	}	
	
	private boolean isThresholdVerified(IkrValue newValue, IkrValue oldValue, IkrCategory ikrCategory) throws Exception {
		boolean res = true;	
		
		if (newValue == null || oldValue == null ||
			newValue.getValueDefinitionId() != oldValue.getValueDefinitionId() ||
			newValue.getIkrCategoryId() != oldValue.getIkrCategoryId()) {
			return res;
		}
		
		double threshold = ikrCategory.getThreshold();
		try {
			switch (ikrCategory.getIkrUnitType()) {
			
				case NUMBER :
				case RATE :
				case STORAGE :
				case CURRENCY :
				case THROUGHPUT :
				case DURATION :
					double newValDb = Double.parseDouble(newValue.getValue());
					double oldValDb = Double.parseDouble(oldValue.getValue());
					
					if (oldValDb == 0 && newValDb == 0) {
						res = false;
					} else if (oldValDb == 0 && newValDb != 0) {
						res = true;
					} else {
						double deltaDb = (newValDb - oldValDb);
						double relativeDeltaDb = deltaDb/oldValDb;
						relativeDeltaDb = Math.abs(relativeDeltaDb)*100;
						
						res = relativeDeltaDb > threshold;
					}	
				break;
				
				case STRING :
					String valStringNew = newValue.getValue();
					String valStringOld = oldValue.getValue();
				
					if (valStringOld != null && valStringNew != null) {
						res = !valStringNew.equalsIgnoreCase(valStringOld);
					}
				break;
				
				case BOOLEAN :
					Boolean valBooleanNew = Boolean.valueOf(newValue.getValue());
					Boolean valBooleanOld = Boolean.valueOf(oldValue.getValue());
					
					if (valBooleanOld != null && valBooleanNew != null) {
						res = valBooleanOld.booleanValue() != valBooleanNew.booleanValue();
					}
				break; 
				
				case DATETIME :
					String newValStr = newValue.getValue();
					String oldValStr = newValue.getValue();
					if (StringUtils.isAlphanumeric(newValStr) && StringUtils.isAlphanumeric(oldValStr)) {
						long valDateNew = Long.valueOf(newValStr);
						long valDateOld = Long.valueOf(oldValStr);
						if (valDateOld == 0 && valDateNew == 0) {
							res = false;
						} else if (valDateOld == 0 && valDateNew != 0) {
							res = true;
						} else {
							double deltaDb = (valDateNew - valDateOld);
							double relativeDeltaDb = deltaDb/valDateOld;
							relativeDeltaDb = Math.abs(relativeDeltaDb);
							
							res = relativeDeltaDb > threshold;
						}	
					}
					else {
						if (oldValStr != null && newValStr != null) {
							res = !newValStr.equalsIgnoreCase(oldValStr);
						}
					}
				break;      
			}		
		} catch(Exception exc) {
			new Exception("Error when testing threshold",exc);
		}
		
		return res;
	}
	
	private synchronized Collection<IkrValue> computeCrossValues(Collection<Long> crossComputeIds) {
		Collection<IkrValue> res = new ArrayList<IkrValue>();
		
		Date captureTime = new Date();
		
		try {
			Map<Long, AbstractIkrDefinition> crossComputeDefinitions = monitoringPM.getIkrDefinitions(crossComputeIds);
	
			compute:
			for (AbstractIkrDefinition ikrDefinition : crossComputeDefinitions.values()) {
				CrossComputeDefinition crossComputeDefinition = (CrossComputeDefinition)ikrDefinition;
				String formula =  crossComputeDefinition.getCrossComputation();
	
				JEP jep = new JEP();
				
				Pattern p = Pattern.compile("M\\d+");
				Matcher m = p.matcher(formula);
				boolean b = false;
				while(b = m.find()) {
					String formulaId = m.group();
					
					String defIdStr = formulaId.substring(1);
					Long defId = Long.valueOf(defIdStr);
					
					IkrValue ikrValue = currentIkrValuesCache.get(defId);
					
					if (ikrValue == null) {
						logger.debug("Warning, ikrValue not found for defId " + defId);
						logger.debug("Cross Computation cancelled : " + formula);
						continue compute;
					}
					
					double value = Double.parseDouble(ikrValue.getValue());
					jep.addVariable(formulaId, value);
					
				}	
				
				jep.parseExpression(formula);
				double computedValue =  jep.getValue();
				if (!Double.isNaN(computedValue) && !Double.isInfinite(computedValue)) {					
					IkrValue computedIkrValue = new IkrValue();
					computedIkrValue.setCaptureTime(captureTime);
					computedIkrValue.setIkrCategoryId(crossComputeDefinition.getIkrCategoryId());
					computedIkrValue.setIkrDefinitionId(crossComputeDefinition.getId());
					computedIkrValue.setValue(nf.format(computedValue));
					res.add(computedIkrValue);
				}
			}
		} catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
		
		return res;
	}
	
	private synchronized Collection<IkrValue> computeIkrStatisticsValue(IkrValue ikrValue) {
		Collection<IkrValue> res = new ArrayList<IkrValue>();
		
		Date captureTime = new Date();

		long ikrDefinitionId = ikrValue.getValueDefinitionId();
		
		try {
			AbstractIkrDefinition definition = monitoringPM.getIkrDefinition(ikrDefinitionId);
			
			if (definition.getId() == 6882)
				System.out.println("");
		
			Collection<Long> linkedStatisticIds = definition.getLinkedStatisticDefinitionIds();
		
			for (long linkedStatisticId : linkedStatisticIds) {
				AbstractIkrDefinition linkedDefinition = monitoringPM.getIkrDefinition(linkedStatisticId);
				
				IkrComputeStatistics metricComputeStatistic = ikrComputeStatistics.get(linkedStatisticId);				
				if (metricComputeStatistic == null) {
					metricComputeStatistic = AbstractIkrComputeStatistics.createIkrComputeStatistics(linkedStatisticId, linkedDefinition.getIkrCompute());
				}
				
				ikrComputeStatistics.put(linkedStatisticId, metricComputeStatistic);
				
				metricComputeStatistic.addValue(ikrValue);
				String computedValue = metricComputeStatistic.getComputedValue();
					
				IkrValue computedIkrValue = new IkrValue();
				computedIkrValue.setCaptureTime(captureTime);
				computedIkrValue.setIkrCategoryId(ikrValue.getIkrCategoryId());
				computedIkrValue.setIkrDefinitionId(metricComputeStatistic.getIkrDefinition());
				computedIkrValue.setValue(computedValue);
				res.add(computedIkrValue);

			}
		} catch(Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
		return res;
	}	
	
	private void sendAlerts(Collection<Alert> alerts)
	throws Exception {
		if (alerts != null && !alerts.isEmpty()) {	
			List<IkrJmsMessage> values = new ArrayList<IkrJmsMessage>();
			values.addAll(alerts);
			alertProducer.publish(values);
		}
	}
	
	private void sendIkrValues(Collection<IkrValue> ikrValues)
	throws Exception {
		if (ikrValues != null && !ikrValues.isEmpty()) {	
			List<IkrJmsMessage> values = new ArrayList<IkrJmsMessage>();
			values.addAll(ikrValues);
			ikrValueProducer.publish(values);
		}
	}

	public void dispose() {}

	public Collection<Alert> getAlerts() throws RemoteException {
		return computeAlert.getAlerts();
	}

	public void notifyElementPut(Ehcache arg0, Element arg1)
	throws CacheException {
		try {
				Element element = (Element)arg1;
				StaticData sd = (StaticData)element.getValue();
				
				IkrValue ikrValue = new IkrValue();
				ikrValue.setCaptureTime(new Date());
				ikrValue.setIkrCategoryId(sd.getIkrCategoryId());
				ikrValue.setIkrDefinitionId(sd.getId());
				ikrValue.setValue(sd.getValue());
				currentIkrValuesCache.put(sd.getId(), ikrValue);
		} catch (Exception exc) {
			logger.error("Impossible to update static data cache - creation", exc);
		}
	}

	public void notifyElementRemoved(Ehcache arg0, Element arg1)
	throws CacheException {}

	public void notifyElementUpdated(Ehcache arg0, Element arg1)
	throws CacheException {
		try {
				Element element = (Element)arg1;
				StaticData sd = (StaticData)element.getValue();
				
				currentIkrValuesCache.remove(sd.getId());
				
				IkrValue ikrValue = new IkrValue();
				ikrValue.setCaptureTime(new Date());
				ikrValue.setIkrCategoryId(sd.getIkrCategoryId());
				ikrValue.setIkrDefinitionId(sd.getId());
				ikrValue.setValue(sd.getValue());
				currentIkrValuesCache.put(sd.getId(), ikrValue);
		} catch (Exception exc) {
			logger.error("Impossible to update static data cache - update", exc);
		}
	}

	public void notifyElementExpired(Ehcache cache, Element element) {}

	public void notifyElementEvicted(Ehcache cache, Element element) {}

	public void notifyRemoveAll(Ehcache cache) {}
}
