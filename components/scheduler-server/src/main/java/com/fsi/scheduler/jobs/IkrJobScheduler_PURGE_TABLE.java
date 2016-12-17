package com.fsi.scheduler.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import com.fsi.fwk.exception.persistence.PersistenceException;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrAdminLoggingCategory;
import com.fsi.monitoring.config.PMFactory;
import com.fsi.monitoring.histo.HistoPM;
import com.fsi.monitoring.ikr.model.IkrStaticDomain;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.indiktor.MonitoringPM;
import com.fsi.monitoring.kpi.metrics.AbstractIkrDefinition;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.msd.StaticData;

public class IkrJobScheduler_PURGE_TABLE extends IkrJobScheduler {
	private static final Logger LOG = Logger.getLogger(IkrJobScheduler_PURGE_TABLE.class);	
	
	private String purgeFrequency;
	private int valuesPerDay;
	private boolean archiveNeeded;
	
	private DataModelPM dataModelPM;
	private MonitoringPM monitoringPM;
	
	private Map<Integer, IkrCategory> ikrCategoryCache;
	
	public IkrJobScheduler_PURGE_TABLE() {}

	@Override
	public void process() throws Exception{		
		LOG.info("start PURGE_TABLE");
		System.out.println("start PURGE_TABLE");
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "start PURGE_TABLE"));
		notifyEventLog();
		if (archiveNeeded) {
			LOG.info("PURGE_TABLE - Archiving ...");
			System.out.println("PURGE_TABLE - Archiving ...");
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "PURGE_TABLE - Archiving ..."));
			notifyEventLog();
			List<IkrValue> valuesToArchive = new ArrayList<IkrValue>();
			Map<String, List<Long>> eligibleDefIds = getEligibleIkrDefinitionIds();
			 
			List<Long> ikrdefIds = eligibleDefIds.get("NUMBER");
			try {
				List<IkrValue> avgValues = getAverageValues(ikrdefIds, true);
				if(avgValues.size()>0)
					valuesToArchive.addAll(avgValues);
			} catch (PersistenceException e) {
				LOG.error(e.getMessage(), e);
				status = ComponentStatus.ERROR_OCCURED;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
				notifyEventLog();
			}
			 
			ikrdefIds = eligibleDefIds.get("BOOLEAN");
			try {
				List<IkrValue> avgValues = getAverageValues(ikrdefIds, false);
				if(avgValues.size()>0)
					valuesToArchive.addAll(avgValues);
			} catch (PersistenceException e) {
				LOG.error(e.getMessage(), e);
				status = ComponentStatus.ERROR_OCCURED;
				addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
				notifyEventLog();
			}
			 
			ikrdefIds = eligibleDefIds.get("DATETIME");			 
			 
			ikrdefIds = eligibleDefIds.get("STRING");			
			
			try {
				monitoringPM.saveIkrValues(valuesToArchive, true);
			} catch (Exception e) {
				throw new Exception("Error occured while saving Archive datas", e);
			}
			
			LOG.info("PURGE_TABLE - Archiving finished");
			System.out.println("PURGE_TABLE - Archiving finished");
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "PURGE_TABLE - Archiving finished"));
			notifyEventLog();
		}
		
		LOG.info("PURGE_TABLE - Purging ...");
		System.out.println("PURGE_TABLE - Purging ...");
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "PURGE_TABLE - Purging ..."));
		notifyEventLog();
		Date[] purgeDate = getPurgeStartDate();
		try {
			monitoringPM.cleanIkrValues(purgeDate[1]);
		} catch (Exception e) {
			throw new Exception("Error occured while cleaning datas", e);
		}
		
		LOG.info("PURGE_TABLE - Purge finished");
		System.out.println("PURGE_TABLE - Purge finished");
		addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.INFO, "PURGE_TABLE - Purge finished"));
		notifyEventLog();
	}
	
	private Map<String, List<Long>> getEligibleIkrDefinitionIds() {
		Map<String, List<Long>> eligibleDefIds = new HashMap<String, List<Long>>();
		try {
			Map<Long, AbstractIkrDefinition> ikrDefinitions = monitoringPM.getIkrDefinitions();
			for (AbstractIkrDefinition ikrDefinition : ikrDefinitions.values()) {
				if (ikrDefinition instanceof StaticData)
					continue;
				IkrCategory category = ikrCategoryCache.get(ikrDefinition.getIkrCategoryId());
				if (category.isArchive()) {
					String type = "NUMBER";
					switch (category.getIkrUnitType()) {		
						case STRING :
							type = "STRING";
						break;
						
						case BOOLEAN :
							type = "BOOLEAN";
						break;
						
						case DATETIME :
							type = "DATETIME";
						break;
					}	
					List<Long> ids = eligibleDefIds.get(type);
					if (ids == null)
						ids = new ArrayList<Long>();
					ids.add(ikrDefinition.getId());
					eligibleDefIds.put(type, ids);					
				}
			}
		} catch (PersistenceException e) {
			LOG.error(e.getMessage(), e);
			status = ComponentStatus.ERROR_OCCURED;
			addLog(new IkrAdminLogging(new Date(), IkrAdminLoggingCategory.ERROR, e.getMessage()));
			notifyEventLog();
		}
		return eligibleDefIds;
	}
	
	private RangeDate[] getNumberDateRange(Date startDate) {
		RangeDate[] ranges = new RangeDate[valuesPerDay];
		int interval = 24/valuesPerDay;
		RangeDate tmp = null;
		for (int i=0; i<valuesPerDay; i++) {
			Calendar calEnd = Calendar.getInstance();
			Date start = null;
			if (tmp == null)
				start = startDate;
			else 
				start = tmp.getEnd();
			calEnd.setTime(start);
			calEnd.add(Calendar.MINUTE, interval*60);
			Calendar calMedium = Calendar.getInstance();
			calMedium.setTime(start);
			double ratio = (double)interval/2;
			double medium = (double)(ratio*60);
			calMedium.add(Calendar.MINUTE, (int)medium);
			RangeDate current = new RangeDate(start, calEnd.getTime(), calMedium.getTime());
			tmp = current;
			ranges[i] = current;
		}
		return ranges;
	}
	
	private Date[] getDatesOfPurge() {
		Date[] datesOfPurge = null;
		Date[] purgeRange = getPurgeStartDate();
		if ("Day-1".equals(purgeFrequency)) {
			datesOfPurge = getDatesOfPurge(1, purgeRange[0]);
		}
		else if ("Week-1".equals(purgeFrequency)) {
			datesOfPurge = getDatesOfPurge(7, purgeRange[0]);
		}
		else {
			long nb = DateUtils.getFragmentInDays(purgeRange[1], Calendar.MONTH);
			datesOfPurge = getDatesOfPurge((int)nb, purgeRange[0]);
		}
		return datesOfPurge;
	}
	
	private Date[] getDatesOfPurge(int nbOfDays, Date startPurgeDate) {
		Date start = null;
		Date[] datesOfPurge = new Date[nbOfDays];			
		for (int i=0; i<nbOfDays; i++){
			if (start == null)
				datesOfPurge[i] = startPurgeDate;
			else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				cal.add(Calendar.DATE, 1);
				datesOfPurge[i] = cal.getTime();
			}
			start = datesOfPurge[i];
		}
		return datesOfPurge;
	}
	
	private List<IkrValue> getAverageValues(List<Long> ikrDefinitionIds, boolean isNumeric) throws PersistenceException {
		List<IkrValue> avgValues = new ArrayList<IkrValue>();
		if (ikrDefinitionIds==null || ikrDefinitionIds.isEmpty())
			return avgValues;
		HistoPM histoPM = PMFactory.getHistoPM();
		for (Date startDate : getDatesOfPurge()) {
			RangeDate[] ranges = getNumberDateRange(startDate);
			for (RangeDate range : ranges) {
				Map<Long, List<IkrValue>> histoValues = histoPM.getIkrValues(ikrDefinitionIds, range.getStart(), range.getEnd());
				for (List<IkrValue> values : histoValues.values()) {
					IkrValue value = getAverageValue(range, values, isNumeric);
					avgValues.add(value);
				}
			}	
		}
		Collections.sort(avgValues, new Comparator<IkrValue>() {
			public int compare(IkrValue o1, IkrValue o2) {
				return o1.getCaptureTime().compareTo(o2.getCaptureTime());
			}
		});		
		return avgValues;
	}	
	
	private IkrValue getAverageValue(RangeDate range, List<IkrValue> values, boolean isNumeric) {
		IkrValue ikrAvgValue = new IkrValue();
		IkrValue sample = values.get(0);
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (IkrValue value : values) {
			if (isNumeric) {
				double dbVal = Double.parseDouble(value.getValue());
				stats.addValue(dbVal);
			}
			else {
				boolean boolVal = Boolean.parseBoolean(value.getValue());
				stats.addValue((boolVal)?1:0);
			}
		}		
		ikrAvgValue.setCaptureTime(range.getMedium());
		ikrAvgValue.setIkrCategoryId(sample.getIkrCategoryId());
		ikrAvgValue.setIkrDefinitionId(sample.getValueDefinitionId());
		double avgValue = stats.getMean();
		String value = String.valueOf(avgValue);
		if (!isNumeric) {
			value = String.valueOf((avgValue>=0.5)?true:false);
		}
		ikrAvgValue.setValue(value);
		return ikrAvgValue;
	}
		
	private Date[] getPurgeStartDate() {
		Date[] date = new Date[2];				
		if ("Day-1".equals(purgeFrequency)) {
			Calendar cal0 = Calendar.getInstance();
			cal0.add(Calendar.DATE, -1);
			cal0.set(Calendar.HOUR_OF_DAY, 0);
			cal0.set(Calendar.MINUTE, 0);
			cal0.set(Calendar.SECOND, 0);
			cal0.set(Calendar.MILLISECOND, 0);		
			date[0] = cal0.getTime();
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date[0]);
			cal1.set(Calendar.HOUR_OF_DAY, 23);
			cal1.set(Calendar.MINUTE, 59);
			cal1.set(Calendar.SECOND, 59);
			cal1.set(Calendar.MILLISECOND, 999);
			date[1] = cal1.getTime();
		}
		else if ("Week-1".equals(purgeFrequency)) {
			Calendar cal0 = Calendar.getInstance();
			cal0.add(Calendar.WEEK_OF_YEAR, -1);
			cal0.set(Calendar.DAY_OF_WEEK, cal0.getFirstDayOfWeek());
			cal0.set(Calendar.HOUR_OF_DAY, 0);
			cal0.set(Calendar.MINUTE, 0);
			cal0.set(Calendar.SECOND, 0);
			cal0.set(Calendar.MILLISECOND, 0);
			date[0] = cal0.getTime();
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date[0]);	
			cal1.add(Calendar.DATE, 6);
			cal1.set(Calendar.HOUR_OF_DAY, 23);
			cal1.set(Calendar.MINUTE, 59);
			cal1.set(Calendar.SECOND, 59);
			cal1.set(Calendar.MILLISECOND, 999);
			date[1] = cal1.getTime();
		}
		else {
			Calendar cal0 = Calendar.getInstance();
			cal0.add(Calendar.MONTH, -1);
			cal0.set(Calendar.DAY_OF_MONTH, 1);
			cal0.set(Calendar.HOUR_OF_DAY, 0);
			cal0.set(Calendar.MINUTE, 0);
			cal0.set(Calendar.SECOND, 0);
			cal0.set(Calendar.MILLISECOND, 0);
			date[0] = cal0.getTime();
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date[0]);
			cal1.add(Calendar.MONTH, 1);
			cal1.add(Calendar.DATE, -1);
			cal1.set(Calendar.HOUR_OF_DAY, 23);
			cal1.set(Calendar.MINUTE, 59);
			cal1.set(Calendar.SECOND, 59);
			cal1.set(Calendar.MILLISECOND, 999);
			date[1] = cal1.getTime();
		}
		return date;
	}
	

	@Override
	public void init() {
		ikrCategoryCache = new HashMap<Integer, IkrCategory>();
		dataModelPM = PMFactory.getDataModelPM();
		monitoringPM = PMFactory.getMonitoringPM();
		
		Map<Integer, IkrStaticDomain> staticDomains = dataModelPM.loadIkrStaticDomains();
		for (IkrStaticDomain domain : staticDomains.values()) {
			if (domain instanceof IkrCategory) {
				ikrCategoryCache.put(domain.getId(), (IkrCategory)domain);
			}
		}
		
		purgeFrequency = config.getAttributes().get("PURGE_FREQUENCY");
		archiveNeeded = Boolean.valueOf(config.getAttributes().get("ARCHIVE_NEEDED"));
		if (archiveNeeded)
			valuesPerDay = Integer.valueOf(config.getAttributes().get("VALUES_PER_DAY"));
		
	}
	
	class RangeDate {
		private Date start;
		private Date end;
		private Date medium;
		
		public RangeDate(Date start, Date end, Date medium) {
			super();
			this.start = start;
			this.end = end;
			this.medium = medium;
		}

		public Date getStart() {
			return start;
		}

		public Date getEnd() {
			return end;
		}

		public Date getMedium() {
			return medium;
		}		
	}

}
