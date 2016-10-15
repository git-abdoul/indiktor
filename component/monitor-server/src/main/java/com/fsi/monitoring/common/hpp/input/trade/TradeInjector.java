package com.fsi.monitoring.common.hpp.input.trade;

import com.calypso.tk.core.Action;
import com.calypso.tk.core.JDatetime;
import com.calypso.tk.core.Status;
import com.calypso.tk.core.Trade;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectionUtil;

public class TradeInjector {

	public static final int INJECTION_PERIOD=2000;
	public static final int TRADE_COUNT = 100;
	public static final int REPLICATED_TRADEID=1128;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			DSConnection ds = ConnectionUtil.connect(args, "TradeInput");
			Trade trade = ds.getRemoteTrade().getTrade(REPLICATED_TRADEID);
			Trade clone = null;
			for (int i = 0; i < TRADE_COUNT; ++i) {
				try {Thread.sleep(INJECTION_PERIOD);} catch(Throwable e) {e.printStackTrace();}
				clone = (Trade)trade.clone();
//				clone.setMutable(true);

				//clone.setInternalReference(String.valueOf(clone.getId()) + System.currentTimeMillis());
				//clone.setExternalReference(String.valueOf(clone.getId()) + System.currentTimeMillis());
				clone.setInternalReference(null);
				clone.setExternalReference(null);

				clone.setStatus(Status.S_NONE);
				clone.setAction(Action.NEW);
				clone.setId(0);
				clone.getProduct().setId(0);

				JDatetime tradeDt = new JDatetime(System.currentTimeMillis());
				JDatetime settleDt = new JDatetime(System.currentTimeMillis());
				settleDt.add(1,0,0,0,0);

				clone.setTradeDate(tradeDt);
				clone.setSettleDate(settleDt.getJDate());

				ds.getRemoteTrade().save(clone);
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
}