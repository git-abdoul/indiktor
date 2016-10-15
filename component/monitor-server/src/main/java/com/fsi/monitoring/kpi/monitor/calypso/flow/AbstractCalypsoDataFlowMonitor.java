package com.fsi.monitoring.kpi.monitor.calypso.flow;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventMessage;
import com.calypso.tk.event.PSEventTrade;
import com.calypso.tk.event.PSEventTransfer;
import com.fsi.monitoring.connector.CalypsoConnectorConfig;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoConnector;
import com.fsi.monitoring.kpi.monitor.BusinessMonitorTask;
import com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData.DataFlowResourceData;

public abstract class AbstractCalypsoDataFlowMonitor extends BusinessMonitorTask {
	private static final Logger LOG = Logger.getLogger(AbstractCalypsoDataFlowMonitor.class);
	
	protected long monitorStartTime = 0;
	
	protected CalypsoConnector calypsoConnector;
	
	@Override
	protected void initConnector() {
		calypsoConnector = (CalypsoConnector)getConnector(CalypsoConnectorConfig.TYPE);		
	}
	
	@Override
	protected Date getMonitoredEnvCurrentTime() {
		return calypsoConnector.getDsCurrentTime();
	}

	protected DataFlowResourceData fetchWorkflowBasedObjectStats (List<PSEvent> events)
	throws ConnectorException {
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(eventClassName));	
		Map<String, Long> currentStats = new HashMap<String, Long>();		
		Iterator<PSEvent> iter = events.iterator();		
		while (iter.hasNext()) {	
			PSEvent event = iter.next();
			String oldStatus = "";
			String currentStatus = "";
			String instance = "";
			if (event instanceof PSEventTrade) {
				PSEventTrade tradeEvt = (PSEventTrade)event;				
				
				if (!acceptBusinessComponent(tradeEvt.getTrade()))
					continue;
//				if (tradeEvt.getTrade().getUpdatedTime().before(lastEvtDate))
//					continue;
				
				Date newObjectDate = new Date(tradeEvt.getTrade().getUpdatedTime().getTime());
				String newObjectId = String.valueOf(tradeEvt.getTradeId());
				if (lastObjectDate != null) {					
					if (newObjectDate.after(lastObjectDate)) {
						lastObjectDate = newObjectDate;
						lastObjectId = newObjectId;
					}
				}
				else {
					lastObjectDate = newObjectDate;
					lastObjectId = newObjectId;
				}					
				
				oldStatus = (tradeEvt.getOldStatus() != null) ? tradeEvt.getOldStatus().getStatus() : null;
				currentStatus = tradeEvt.getStatus().getStatus();				
				instance = getInstance(tradeEvt.getTrade());
			}
			else if (event instanceof PSEventMessage) {
				PSEventMessage msgEvt = (PSEventMessage)event;
				if (!acceptBusinessComponent(msgEvt.getBoMessage()))
					continue;
//				if (msgEvt.getBoMessage().getCreationDate().before(lastEvtDate))
//					continue;

				Date newObjectDate = new Date(msgEvt.getBoMessage().getCreationDate().getTime());
				String newObjectId = String.valueOf(msgEvt.getBoMessage().getId());
				if (lastObjectDate != null) {					
					if (newObjectDate.after(lastObjectDate)) {
						lastObjectDate = newObjectDate;
						lastObjectId = newObjectId;
					}
				}
				else {
					lastObjectDate = newObjectDate;
					lastObjectId = newObjectId;
				}					
				
				oldStatus = (msgEvt.getOldStatus() != null) ? msgEvt.getOldStatus().getStatus() : null;
				currentStatus = msgEvt.getMessageStatus().getStatus();			
				instance = getInstance(msgEvt.getBoMessage());
			}
			else if (event instanceof PSEventTransfer) {
				PSEventTransfer xferEvt = (PSEventTransfer)event;
				if (!acceptBusinessComponent(xferEvt.getBoTransfer()))
					continue;
//				if (xferEvt.getBoTransfer().getEnteredDate().before(lastEvtDate))
//					continue;
				
				Date newObjectDate = new Date(xferEvt.getBoTransfer().getEnteredDate().getTime());
				String newObjectId = String.valueOf(xferEvt.getBoTransfer().getId());
				if (lastObjectDate != null) {					
					if (newObjectDate.after(lastObjectDate)) {
						lastObjectDate = newObjectDate;
						lastObjectId = newObjectId;
					}
				}
				else {
					lastObjectDate = newObjectDate;
					lastObjectId = newObjectId;
				}				
				
				oldStatus = (xferEvt.getOldStatus() != null) ? xferEvt.getOldStatus().getStatus() : null;
				currentStatus = xferEvt.getStatus().getStatus();
				instance = getInstance(xferEvt.getBoTransfer());
			}		
			
			computeWorkflowBasedObjectStats(currentStats, oldStatus, currentStatus, instance);
		}
		
		return new DataFlowResourceData(currentStats, new Date());
	}
	
	protected void computeWorkflowBasedObjectStats(Map<String, Long> stats, String oldStatus, String currentStatus, String instance) {
		if (oldStatus == null) {				
			updateCurrentStat(instance, stats, null);
		}
		else {
			if (oldStatus.equals(currentStatus)) {
				Long size = globalStats.get(instance);
				if (size == null) 
					updateCurrentStat(instance, stats, new Long(1));
			} else {
				if (instance.contains(currentStatus)) {
					String oldInstance = instance.replace(currentStatus, oldStatus);
					// previous status
					Long oldSize = globalStats.get(oldInstance);
					if (oldSize == null) 
						LOG.debug("Error occured : " + oldStatus + " stat not found");
					else 
						updateCurrentStat(oldInstance, stats, oldSize-1);
					
					//current status
					updateCurrentStat(instance, stats, null);
				}
			}
		}
	}
	
	protected String dateToString(Object date) {
		Format format = new SimpleDateFormat("ddMMyyyy");
		String myDateFormat = "";
		if (date instanceof Date) {
			Date myDate = (Date)date;
			myDateFormat = format.format(myDate);
		}
		else if (date instanceof JDatetime) {
			JDatetime myDate = (JDatetime)date;
			myDateFormat = format.format(myDate);
		}
		else if (date instanceof JDate) {
			JDate myDate = (JDate)date;
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, myDate.getDayOfMonth());
			calendar.set(Calendar.MONTH, myDate.getMonth());
			calendar.set(Calendar.YEAR, myDate.getYear());
			myDateFormat = format.format(calendar.getTime());
		}		
		return myDateFormat;
	}	
}
