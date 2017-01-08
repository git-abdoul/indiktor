package com.fsi.monitoring.kpi.monitor.calypso.accounting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.calypso.tk.bo.BOCre;
import com.calypso.tk.bo.BOPosting;
import com.calypso.tk.core.Book;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventAccounting;
import com.calypso.tk.event.PSEventCre;
import com.calypso.tk.event.PSEventException;
import com.calypso.tk.event.PSEventTask;
import com.calypso.tk.util.CreArray;
import com.calypso.tk.util.PostingArray;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.calypso.CalypsoException;
import com.fsi.monitoring.kpi.monitor.calypso.flow.AbstractCalypsoDataFlowMonitor;
import com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData.DataFlowResourceData;

public class CalypsoAccountingEntryMonitor extends AbstractCalypsoDataFlowMonitor implements CalypsoListener{
	private static final Logger LOG = Logger.getLogger(CalypsoAccountingEntryMonitor.class);

	private String flowName;
	private Set<String> creIds;	
	
	private List<PSEvent> eventAccountings;	
	private List<PSEvent> eventAccountingsToUse;

	@Override
	protected void initConnector() {
		super.initConnector();		
		creIds = new HashSet<String>();
	}

	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {	
		String filter = "creation_date > " + ioSQL.datetime2String(synchronizeDate);
		Map<String, Long> currentStats = new HashMap<String, Long>();
		if ("ACCOUTING_ENTRY_POSTING".equals(getType())) 
			synchronizePosting(filter, currentStats);		
		else 
			synchronizeCre(filter, currentStats);			
	}
	
	private void synchronizePosting(String filter, Map<String, Long> stats) {
		flowName = "Posting";
		if (filter != null) {
			try {
				PostingArray array = calypsoConnector.getRemoteBackOffice().getBOPostings(filter);
				BOPosting[] postings = (array!=null)?array.getPostings():null;
				if (postings==null)return;
				for (BOPosting posting : postings) {
					if (posting==null)continue;
					if (!acceptBusinessComponent(posting))
						continue;
					String instance = getInstance(posting);
					updateCurrentStat(instance, stats, null);
					
					Date newObjectDate = new Date(posting.getUpdateTime().getTime());
					updateLastEvtDate(newObjectDate);
				}		
			} catch (RemoteException e) {
				LOG.error(e);
			} catch (ConnectorException e) {
				LOG.error(e);
			}
		}
	}
	
	private void synchronizeCre(String filter, Map<String, Long> stats) {
		flowName = "Cre";		
		if (filter != null) {
			try {
				CreArray array = calypsoConnector.getRemoteBackOffice().getBOCres(null, filter);
				BOCre[] cres = array.getCres();
				for (BOCre cre : cres) {
					if (!acceptBusinessComponent(cre))
						continue;
					String instance = getInstance(cre);
					updateCurrentStat(instance, stats, null);
					
					Date newObjectDate = new Date(cre.getUpdateTime().getTime());
					updateLastEvtDate(newObjectDate);
				}		
			} catch (RemoteException e) {
				LOG.error(e);
			} catch (ConnectorException e) {
				LOG.error(e);
			}
		}
	}	
	
	public void onEventReceived(PSEvent event) {
		statsLock.lock();
		try {
			eventAccountings.add(event);
		} finally {
			statsLock.unlock();
		}		
	}	

	@Override
	protected void preStart() {
		eventAccountings = new ArrayList<PSEvent>();
		super.preStart();
		List<String> events = new ArrayList<String>();
		if ("ACCOUTING_ENTRY_POSTING".equals(getType())) 
			events.add(PSEventAccounting.class.getName());		
		else 
			events.add(PSEventCre.class.getName());		
		calypsoConnector.subscribeToCalypsoEvents(this, events);
	}

	@Override
	protected void preFetchs() throws Exception {
		eventAccountingsToUse = new ArrayList<PSEvent>();
		statsLock.lock();
		try {			
			eventAccountingsToUse.addAll(eventAccountings);				
			eventAccountings.clear();
		} finally {
			statsLock.unlock();
		}
		super.preFetchs();
		
	}

	public DataFlowResourceData fetchACCOUTING_POSTING()
	throws ConnectorException {		
		flowName = "Posting";
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(PSEventAccounting.class.getName()));		
		Map<String, Long> stats = new HashMap<String, Long>();		
		Iterator<PSEvent> iter = eventAccountingsToUse.iterator();		
		while (iter.hasNext()) {	
			PSEvent event = iter.next();
			PSEventAccounting accEvent = (PSEventAccounting)event;
			BOPosting posting =  accEvent.getBoPosting();
			
			if (!acceptBusinessComponent(posting))
				continue;
			
//			if (posting.getUpdateTime().before(lastEvtDate))
//				continue;
			
			Date newObjectDate = new Date(posting.getUpdateTime().getTime());
			String newObjectId = String.valueOf(posting.getId());
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
			
			String instance = getInstance(posting);	
			updateCurrentStat(instance, stats, null);
		}		
		
		return new DataFlowResourceData(stats, new Date());
	}
	
	public DataFlowResourceData fetchACCOUTING_CRE()
	throws ConnectorException {		
		flowName = "Cre";		
//		List<String> eventClasses = new ArrayList<String>();
//		eventClasses.add(PSEventCre.class.getName());
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(eventClasses));		
		Map<String, List<String>> dataAttributes = new HashMap<String, List<String>>();
		Map<String, Long> stats = new HashMap<String, Long>();		
		Iterator<PSEvent> iter = eventAccountingsToUse.iterator();		
		while (iter.hasNext()) {	
			PSEvent event = iter.next();
			PSEventCre accEvent = (PSEventCre)event;
			BOCre cre =  accEvent.getBOCre();
			
			if (!acceptBusinessComponent(cre))
				continue;
			
//			if (cre.getCreationDate().before(lastEvtDate))
//				continue;	
//			
//			if (cre.getUpdateTime().before(lastEvtDate))
//				continue;
			
			Date newObjectDate = new Date(cre.getUpdateTime().getTime());
			String newObjectId = String.valueOf(cre.getId());
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
			
			String instance = getInstance(cre);	
			updateCurrentStat(instance, stats, null);
			
			int lg = instance.length();
			String instanceKey = instance.substring(instance.indexOf("[")+1, lg-1);
			
			String creId = String.valueOf(cre.getId());
			if (!creIds.contains(creId)) {
				Map<String, String> creAttrs = cre.getAttributes();
				if (creAttrs!=null && creAttrs.size()>0) {
					for (String key : creAttrs.keySet()) {
						String dataAttrKey = flowName + "[" + instanceKey + "," + key + "]";
						List<String> values = dataAttributes.get(dataAttrKey);
						if (values == null) {
							values = new ArrayList<String>();
							dataAttributes.put(dataAttrKey,values);
						}
						values.add(creAttrs.get(key));
					}
				}
				creIds.add(creId);
			}
		}
		
		DataFlowResourceData resource = new DataFlowResourceData(stats, new Date());
		resource.setDataAttributes(dataAttributes);		
		return resource;
	}
	
	@Override
	protected String getBusinessComponentValue(String componentType,Object businessObject) {
		String result = "";
		if ("ACCOUTING_ENTRY_POSTING".equals(getType()))
			result = getPostingBusinessComponentValue(componentType, (BOPosting)businessObject);
		else
			result = getCreBusinessComponentValue(componentType, (BOCre)businessObject);
		
		return result;
	}
	
	
	private String getPostingBusinessComponentValue(String componentType, BOPosting posting) {
		String result = "";
		try {
			Object resObj = null;
			if ("Currency".equalsIgnoreCase(componentType)) {
				resObj = posting.getCurrency();
			} else if ("AccountBook".equalsIgnoreCase(componentType)) {
				int bid = posting.getBookId();	
				Book book = calypsoConnector.getBook(bid);
				resObj = book.getAccountingBook().getName();
			} else {
				String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
				Method method = BOPosting.class.getMethod(methName, null);
				resObj = method.invoke(posting, null); 
			}	
			if (resObj != null) {
				if (resObj instanceof JDate || resObj instanceof JDatetime)
					result = dateToString(resObj);
				else
					result = resObj.toString();
			}
			else {
				result = "EMPTY_VALUE";
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
	
	private String getCreBusinessComponentValue(String componentType, BOCre cre) {
		String result = "";
		try {
			Object resObj = null;
			if ("Currency".equalsIgnoreCase(componentType)) {
				resObj = cre.getCurrency(0);
			} else if ("AccountBook".equalsIgnoreCase(componentType)) {
				int bid = cre.getBookId();
				Book book = calypsoConnector.getBook(bid);
				resObj = book.getAccountingBook().getName();
			} else {
				String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
				Method method = BOCre.class.getMethod(methName, null);
				resObj = method.invoke(cre, null); 
			}	
			if (resObj != null) {
				if (resObj instanceof JDate || resObj instanceof JDatetime)
					result = dateToString(resObj);
				else
					result = resObj.toString();
			}
			else {
				result = "EMPTY_VALUE";
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
	

	@Override
	protected String flowName() {
		return flowName;
	}
}
