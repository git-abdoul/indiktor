package com.fsi.monitoring.computeServer.alert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.alert.AlertComputeResolution.ComputeStatus;
import com.fsi.monitoring.alert.condition.AlertConditionResolver;
import com.fsi.monitoring.alert.condition.ValueAlertCondition;
import com.fsi.monitoring.computeServer.config.ComputeServerContext;
import com.fsi.monitoring.config.PersistencyBeanName;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.units.IkrUnit;
import com.fsi.monitoring.kpi.units.IkrUnitType;

public class ComputeServerAlertConditionResolver 
implements AlertConditionResolver {

	private static final Logger logger = Logger.getLogger(ComputeServerAlertConditionResolver.class);	
	
	private IkrValue ikrValue;
	
	private AlertConditionResolverStrategy alertConditionResolverStrategyDouble;
	private AlertConditionResolverStrategy alertConditionResolverStrategyString;
	private AlertConditionResolverStrategy alertConditionResolverStrategyBoolean;	
	private AlertConditionResolverStrategy alertConditionResolverStrategyDatetime;	
	
	private MonitoringPM monitoringPM = null;
	private DataModelPM dataModelPM = null;
	
	public ComputeServerAlertConditionResolver() {
		alertConditionResolverStrategyDouble = new AlertConditionResolverStrategyDouble();
		alertConditionResolverStrategyString = new AlertConditionResolverStrategyString();
		alertConditionResolverStrategyBoolean = new AlertConditionResolverStrategyBoolean();
		alertConditionResolverStrategyDatetime = new AlertConditionResolverStrategyDatetime();
		
		monitoringPM =(MonitoringPM)ComputeServerContext.getBean(PersistencyBeanName.monitoringPM);
		dataModelPM = (DataModelPM)ComputeServerContext.getBean(PersistencyBeanName.dataModelPM); 
	}
	
	public ComputeStatus resolveCondition(ValueAlertCondition alertCondition) throws Exception{
		
		AlertConditionResolverStrategy resolverStrategy = findResolverStrategy();
		
		String value = ikrValue.getValue();
		ComputeStatus res = null;
		try {
			IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(ikrValue.getIkrCategoryId());
			String convertedValue = convertValue(alertCondition.getValue(), alertCondition.getUnit(), ikrCategory.getIkrUnit());
			res = resolverStrategy.getConditionStatus(value,
	 					alertCondition.getOperator(), 
	 					convertedValue);
		} catch (PersistenceException e) {
			throw new Exception("Impossible to resolve alert condition");
		}
		
		return res;
	}
	
	private String convertValue(String value, IkrUnit fromUnit, IkrUnit toUnit) {
		String res = null;
		try {
		    Method method = fromUnit.getClass().getMethod("convertTo", new Class[] {String.class, toUnit.getClass()});
		    Object o = method.invoke(fromUnit, new Object[] {value, toUnit});
		    res = (String)o;
		} catch (NoSuchMethodException exc1) {
			logger.error(exc1);
		} catch (IllegalAccessException exc2) {
			logger.error(exc2);
		} catch (InvocationTargetException exc3) {
			Throwable exc = exc3.getTargetException();
			if (exc instanceof IllegalArgumentException) {
				throw (IllegalArgumentException)exc;
			} else {
				logger.error(exc);
			}
		}
		return (res!=null)?res:value;
	}
	
	public void setIkrValue(IkrValue ikrValue) {
		this.ikrValue = ikrValue;
	}
	
	private AlertConditionResolverStrategy findResolverStrategy() {
		AlertConditionResolverStrategy resolverStrategy = null;
		try {
			IkrCategory ikrCategory = (IkrCategory)dataModelPM.getIkrStaticDomain(ikrValue.getIkrCategoryId());
			IkrUnitType ikrUnitType = ikrCategory.getIkrUnitType();
			
			
			switch (ikrUnitType) {
				case BOOLEAN :
					resolverStrategy = alertConditionResolverStrategyBoolean;
				break;
				
				case DURATION :
				case RATE :
				case STORAGE :
				case THROUGHPUT :
				case NUMBER :
					resolverStrategy = alertConditionResolverStrategyDouble;
				break;
				
				case STRING :
					resolverStrategy = alertConditionResolverStrategyString;
				break;
				
				case DATETIME :
					resolverStrategy = alertConditionResolverStrategyDatetime;
				break;
				
			}
		} catch (Exception exc) {
			logger.error(exc);
		}
		return resolverStrategy;
	}
}
