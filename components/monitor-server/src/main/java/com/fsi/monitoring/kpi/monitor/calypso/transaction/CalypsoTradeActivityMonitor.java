package com.fsi.monitoring.kpi.monitor.calypso.transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.calypso.tk.core.JDate;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.Trade;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventTrade;
import com.calypso.tk.mo.TradeFilter;
import com.calypso.tk.util.TradeArray;
import com.fsi.fwk.util.DateUtil;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.connector.calypso.CalypsoListener;
import com.fsi.monitoring.kpi.monitor.calypso.flow.AbstractCalypsoDataFlowMonitor;
import com.fsi.monitoring.kpi.monitor.calypso.flow.resourceData.DataFlowResourceData;


public class CalypsoTradeActivityMonitor extends AbstractCalypsoDataFlowMonitor implements CalypsoListener{
	private static final Logger LOG = Logger.getLogger(CalypsoTradeActivityMonitor.class);	
	
	private List<PSEvent> eventBuffer;	
	private List<PSEvent> eventsToUse;
	
	@Override
	protected void synchronizeData(String id, Date synchronizeDate) throws Exception {
		Map<String, Long> stats = new HashMap<String, Long>();
		List<Trade> trades = getDSTrades(id, synchronizeDate);
		for (Trade trade : trades) {
			if (trade==null) continue;
			if (!acceptBusinessComponent(trade))
				continue;
			String instance = getInstance(trade);
			updateCurrentStat(instance, stats, null);
			
			Date newObjectDate = new Date(trade.getUpdatedTime().getTime());
			updateLastEvtDate(newObjectDate);
		}
	}
	
//	private List<Trade> getSQLTrades(String id, Date enteredDate) throws Exception {
//		String query = "SELECT * FROM TRADE WHERE ENTERED_DATE > TO_DATE(?,?)";
//		Connection con = null;
//		PreparedStatement pStmt = null;
//		ResultSet rs = null;
//		List<Trade> trades = new ArrayList<Trade>();
//		try {
//			con = calypsoConnector.getDatabaseConnector().getConnection();
//			pStmt = con.prepareStatement(query.toUpperCase());
//			pStmt.setString(1, DateUtil.getDate(enteredDate));
//			pStmt.setString(2,  DateUtil.getOracleSQLDateDefaultPattern());
//			rs = pStmt.executeQuery();
//			while (rs.next()) {
//				
//			}
//		} catch(Exception e) {
//			throw new Exception(e);
//		} finally {
//			closeResultSet(rs);
//			closeStatement(pStmt);
//			closeConnection(con);
//		}	
//		return trades; 	
//	}
	
	private List<Trade> getDSTrades(String id, Date updateDate) throws Exception {
		List<Trade> trades = new ArrayList<Trade>();
		TradeFilter filter = new TradeFilter();
		filter.setSQLWhereClause("update_date_time > to_date('"+ DateUtil.getDate(updateDate) + "','"+ DateUtil.getOracleSQLDateDefaultPattern() +"')");
		try {
			TradeArray array = calypsoConnector.getRemoteTrade().getTrades(filter, null);
			if (array!=null)
				trades = Arrays.asList(array.getTrades());
		} catch (RemoteException e) {
			throw new Exception(e);
		} catch (ConnectorException e) {
			throw new Exception(e);
		}		
		return trades;
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
		events.add(PSEventTrade.class.getName());				
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
	
	public DataFlowResourceData fetchTRADE()
	throws ConnectorException {			
		return fetchWorkflowBasedObjectStats(eventsToUse);
	}	
	
	protected String getBusinessComponentValue(String componentType,Object businessObject) {
		String result = "";
		try {
			Trade trade = (Trade)businessObject;
			Object resObj = null;
			if ("ProcessingOrg".equalsIgnoreCase(componentType)) {
				resObj = trade.getBook().getLegalEntity();
			} else {
				String methName = "get" + componentType.substring(0, 1).toUpperCase() + componentType.substring(1);
				Method method = Trade.class.getMethod(methName, null);
				resObj = method.invoke(trade, null); 
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
		}		
		
		return result;
	}	
	
	@Override
	protected String flowName() {
		return "Trade";
	}	
}
