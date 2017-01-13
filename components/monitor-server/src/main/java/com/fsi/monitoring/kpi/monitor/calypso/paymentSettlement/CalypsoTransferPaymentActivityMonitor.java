package com.fsi.monitoring.kpi.monitor.calypso.paymentSettlement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.calypso.tk.bo.BOTransfer;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.Trade;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventTransfer;
import com.calypso.tk.util.TransferArray;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.calypso.flow.AbstractCalypsoDataFlowMonitor;
import com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData.DataFlowResourceData;


public class CalypsoTransferPaymentActivityMonitor extends AbstractCalypsoDataFlowMonitor implements CalypsoListener{
	private static final Logger LOG = Logger.getLogger(CalypsoTransferPaymentActivityMonitor.class);
	
	private String flowName;	
	
	private List<PSEventTransfer> eventBuffer;	
	private List<PSEventTransfer> eventsToUse;
	
	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		Map<String, Long> stats = new HashMap<String, Long>();
		Date now = calypsoConnector.getDsCurrentTime();
		String tableName = "trade";
		String filterClause = "trade.update_date_time > " + ioSQL.datetime2String(synchronizeDate);		
		try {
			TransferArray array = calypsoConnector.getRemoteBackOffice().getBOTransfers(tableName, filterClause, null, 0);
			BOTransfer[] transfers = (array!=null)?array.getTransfers():null;
			if (transfers==null) return;
			for (BOTransfer transfer : transfers) {
				if (transfer==null) continue;
				JDatetime enteredDate = transfer.getEnteredDate();
				if (enteredDate != null) {
					if (enteredDate.before(synchronizeDate))
						continue;
				}					
				if (transfer.isPayment())
					flowName = "Payment";
				else
					flowName = "Transfer";
				if (!acceptBusinessComponent(transfer))
					continue;
				String instance = getInstance(transfer);
				updateCurrentStat(instance, stats, null);
				
				Date newObjectDate = now;
				if (transfer.getEnteredDate()!=null)
					newObjectDate = new Date(transfer.getEnteredDate().getTime());
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
			eventBuffer.add((PSEventTransfer)event);
		} finally {
			statsLock.unlock();
		}		
	}	

	@Override
	protected void preStart() {
		eventBuffer = new ArrayList<PSEventTransfer>();
		super.preStart();
		List<String> events = new ArrayList<String>();
		events.add(PSEventTransfer.class.getName());				
		calypsoConnector.subscribeToCalypsoEvents(this, events);
	}

	@Override
	protected void preFetchs() throws Exception {
		eventsToUse = new ArrayList<PSEventTransfer>();
		statsLock.lock();
		try {			
			eventsToUse.addAll(eventBuffer);				
			eventBuffer.clear();
		} finally {
			statsLock.unlock();
		}
		super.preFetchs();
		
	}
	
	public DataFlowResourceData fetchTRANSFER()
	throws ConnectorException {
		return fetchTransferPaymentStats(false, "Transfer");
	}	
	
	public DataFlowResourceData fetchPAYMENT()
	throws ConnectorException {	
		return fetchTransferPaymentStats(true, "Payment");
	}
	
	@Override
	protected String flowName() {
		return flowName;
	}	
	
	protected DataFlowResourceData fetchTransferPaymentStats (boolean isPayment, String flowName)
	throws ConnectorException {
//		List<PSEvent> events = Collections.synchronizedList(calypsoConnector.getEventListener().getEvents(PSEventTransfer.class.getName()));		
		Map<String, Long> stats = new HashMap<String, Long>();		
		Iterator<PSEventTransfer> iter = eventsToUse.iterator();		
		while (iter.hasNext()) {				
			PSEventTransfer xferEvt = iter.next();
			BOTransfer xfer =  xferEvt.getBoTransfer();
			
			if (isPayment) {
				if (xfer.isPayment() != isPayment)
					continue;
			}
			else {
				if (xfer.isTransfer() != (!isPayment))
					continue;
			}
			
			if (!acceptBusinessComponent(xfer))
				continue;
			
			JDatetime enteredDate = xfer.getEnteredDate();
			if (enteredDate == null) {
				Trade trade = xferEvt.getTrade();
				enteredDate = trade.getEnteredDate();
			}
//			if (enteredDate.before(lastEvtDate))
//				continue;
			
			Date newObjectDate = new Date(enteredDate.getTime());
			String newObjectId = String.valueOf(xfer.getId());
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
			
			String oldStatus = "";
			String currentStatus = "";
			String instance = "";
			oldStatus = (xferEvt.getOldStatus() != null) ? xferEvt.getOldStatus().getStatus() : null;			
			currentStatus = xferEvt.getStatus().getStatus();
			instance = getInstance(xfer, flowName);			
			computeWorkflowBasedObjectStats(stats, oldStatus, currentStatus, instance);
		}
		return new DataFlowResourceData(stats, new Date());
	}
	
	private synchronized String getInstance(Object object, String flowName) {
		this.flowName = flowName;
		return getInstance(object);
	}	

	@Override
	protected String getBusinessComponentValue(String componentType, Object businessObject) {
		String result = "";
		try {
			BOTransfer transfer = (BOTransfer)businessObject;
			Object resObj = null;
			if ("Payer".equalsIgnoreCase(componentType)) {
				int id = transfer.getInternalLegalEntityId();
				resObj = calypsoConnector.getEntityName(id);
			} else if ("ProcessingOrg".equalsIgnoreCase(componentType)) {
				int id = transfer.getProcessingOrg();
				resObj = calypsoConnector.getEntityName(id);
			} else if ("Receiver".equalsIgnoreCase(componentType)) {
				int id = transfer.getExternalLegalEntityId();
				resObj = calypsoConnector.getEntityName(id);
			} else if ("Status".equalsIgnoreCase(componentType)) {
				boolean failed = transfer.isFailed(new JDate(), true);
				if (failed)
					resObj = "FAILED";
				else
					resObj = transfer.getStatus().getStatus();
			} else if ("DeliveryInstruction".equalsIgnoreCase(componentType)) {
				String method = transfer.getSettlementMethod();
                String le = calypsoConnector.getEntityName( transfer.getExternalAgentId());
                if (le != null && le.length()>0) {
                    resObj = method+" "+le;
                }
                else {
                	resObj =  method;
                }				
			} else {
				String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
				Method method = BOTransfer.class.getMethod(methName, null);
				resObj = method.invoke(transfer, null); 
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
