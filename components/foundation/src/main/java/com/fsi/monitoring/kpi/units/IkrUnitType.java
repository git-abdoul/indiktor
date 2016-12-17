package com.fsi.monitoring.kpi.units;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.fsi.monitoring.kpi.compute.MetricCompute;



public enum IkrUnitType {
	NA(),
	DURATION(DurationUnit.class),
	STORAGE(StorageUnit.class),
	RATE(RateUnit.class),
	THROUGHPUT(ThroughputUnit.class),
	DATETIME(DatetimeUnit.class),
	BOOLEAN(BooleanUnit.class),
	STRING(StringUnit.class),
	NUMBER(NumberUnit.class),
	CURRENCY(CurrencyUnit.class);
	
	private static final Logger logger = Logger.getLogger(IkrUnitType.class);
	
	private List<IkrUnit> ikrUnits;
	private Class<? extends Enum<?>> unitClass;
	
	IkrUnitType(String... u) {}
	
	<E extends Enum<E>> IkrUnitType(Class<E> enumClass) {
		this.unitClass = enumClass;
		ikrUnits = new ArrayList<IkrUnit>();
		for (E su : EnumSet.allOf(enumClass)) {
			ikrUnits.add((IkrUnit)su);
		}
	}
	
	public IkrUnit getIkrUnit(String name) {
		IkrUnit res = null;
		try {			
		    Method method = unitClass.getMethod("valueOf", String.class);
		    Object o = method.invoke(null, name);
		    res = (IkrUnit)o;
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
		return res;
	}
	
	public List<IkrUnit> getIkrUnits() {
		return ikrUnits;
	}
	
	public 	Collection<MetricCompute> getSupportedComputes() {
		Collection<MetricCompute> res = null;
		try {		
		    Method method = unitClass.getMethod("getSupportedComputes");
		    Object o = method.invoke(null);
		    res = (Collection<MetricCompute>)o;
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
		
		return res;
	}
	
	public FormattedValue format(String value, IkrUnit origUnit) {
		FormattedValue res = null;

		try {		
		    Method method = unitClass.getMethod("format", String.class, unitClass);
		    Object o = method.invoke(null, value, origUnit);
		    res = (FormattedValue)o;
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
		return res;
	}
	
	public FormattedValue formatTo(String value, IkrUnit toUnit) {
		FormattedValue res = null;
		
		try {
		    Method method = unitClass.getMethod("formatTo", String.class, unitClass);
		    Object o = method.invoke(null, value, toUnit);
		    res = (FormattedValue)o;
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
		return res;
	}	
	
	public String convert(String value, IkrUnit fromUnit) 
	throws IllegalArgumentException {
		String res = null;

		try {		
		    Method method = unitClass.getMethod("convert", String.class, unitClass);
		    Object o = method.invoke(null, value, fromUnit);
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
		return res;
	}

}
