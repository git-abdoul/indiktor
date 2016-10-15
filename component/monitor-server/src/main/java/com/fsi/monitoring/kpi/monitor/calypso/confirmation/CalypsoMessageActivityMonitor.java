package com.fsi.monitoring.kpi.monitor.calypso.confirmation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.calypso.tk.bo.BOMessage;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventMessage;
import com.calypso.tk.util.MessageArray;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.calypso.flow.AbstractCalypsoDataFlowMonitor;
import com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData.DataFlowResourceData;


public class CalypsoMessageActivityMonitor extends AbstractCalypsoDataFlowMonitor implements CalypsoListener{
	private static final Logger LOG = Logger.getLogger(CalypsoMessageActivityMonitor.class);
	
	private List<PSEvent> eventBuffer;	
	private List<PSEvent> eventsToUse;
	
	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		Map<String, Long> stats = new HashMap<String, Long>();
		String filter = "creation_date > " + ioSQL.datetime2String(synchronizeDate);		
		try {
			MessageArray array = calypsoConnector.getRemoteBackOffice().getMessages(filter);
			BOMessage[] messages = (array!=null)?array.getMessages():null;
			if (messages==null)return;
			for (BOMessage message : messages) {
				if (message==null) continue;
				if (!acceptBusinessComponent(message))
					continue;
				String instance = getInstance(message);
				updateCurrentStat(instance, stats, null);
				
				Date newObjectDate = new Date(message.getCreationDate().getTime());
				updateLastEvtDate(newObjectDate);
			}		
		} catch (RemoteException e) {
			LOG.error(e);
		} catch (ConnectorException e) {
			LOG.error(e);
		}	
	}
	
	public void onEventReceived(PSEvent event) {
		statsLock.lock();
		try {
			eventBuffer.add(event);
		} finally {
			statsLock.unlock();
		}		
	}	

	@Override
	protected void preStart() {
		eventBuffer = new ArrayList<PSEvent>();
		super.preStart();
		List<String> events = new ArrayList<String>();
		events.add(PSEventMessage.class.getName());				
		calypsoConnector.subscribeToCalypsoEvents(this, events);
	}

	@Override
	protected void preFetchs() throws Exception {
		eventsToUse = new ArrayList<PSEvent>();
		statsLock.lock();
		try {			
			eventsToUse.addAll(eventBuffer);				
			eventBuffer.clear();
		} finally {
			statsLock.unlock();
		}
		super.preFetchs();
		
	}
	
	public DataFlowResourceData fetchMESSAGE()
	throws ConnectorException {			
		return fetchWorkflowBasedObjectStats(eventsToUse);
	}
	
	@Override
	protected String flowName() {
		return "Message";
	}	

	@Override
	protected String getBusinessComponentValue(String componentType, Object businessObject) {
		String result = "";
		try {
			Object resObj = null;
			BOMessage message = (BOMessage)businessObject;
			if ("Receiver".equalsIgnoreCase(componentType)) {
				int id = message.getReceiverId();
				resObj = calypsoConnector.getEntityName(id);
			} else if ("Sender".equalsIgnoreCase(componentType)) {
				int id = message.getSenderId();
				resObj = calypsoConnector.getEntityName(id);
			} else {
				String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
				Method method = BOMessage.class.getMethod(methName, null);
				resObj = method.invoke(message, null); 
			}	
			if (resObj != null) {
				if (resObj instanceof JDate || resObj instanceof JDatetime)
					result = dateToString(resObj);
				else
					result = resObj.toString();
			}
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			LOG.error(e.getMessage(), e);
		} catch (ConnectorException e) {
			LOG.error(e.getMessage(), e);
		}		
		
		return result;
	}	
}
