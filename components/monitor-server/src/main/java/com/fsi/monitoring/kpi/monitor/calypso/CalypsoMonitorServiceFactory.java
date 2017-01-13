package com.fsi.monitoring.kpi.monitor.calypso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.calypso.tk.core.Holiday;
import com.calypso.tk.core.JDate;
import com.calypso.tk.util.ConnectException;
import com.calypso.tk.util.ConnectionUtil;
import com.calypso.tk.util.ScheduledTask;
import com.fsi.fwk.apps.config.AbstractApplicationContext;
import com.fsi.fwk.exception.SystemException;
import com.fsi.monitoring.util.MonitorReconnectionAttempt;

public class CalypsoMonitorServiceFactory implements Observer {
	private static final Logger LOG = Logger.getLogger(CalypsoMonitorServiceFactory.class);	

	private static CalypsoMonitorServiceFactory instance = null;
	private Hashtable<String, CalypsoMonitorService> clients = null;
	private MonitorReconnectionAttempt attempt = null;

	public CalypsoMonitorServiceFactory() {
		clients = new Hashtable<String, CalypsoMonitorService>();
		attempt = (MonitorReconnectionAttempt)AbstractApplicationContext.getBean("monitorReconnectionAttempt");
	}

	public synchronized CalypsoMonitorService getClient(CalypsoConnectionConfig config)
			throws SystemException {
		CalypsoMonitorService client = null;
		String key = config.getCalypsoEnv();
		if (clients.containsKey(key))
			client = clients.get(key);
		else {
			client = new CalypsoMonitorService(config);
			client.addObserver(this);
			clients.put(key, client);
			try {
				client.initConnection(ConnectionUtil.connect(config.getUser(),
						config.getPassword(), config.getApplicationName(),
						config.getCalypsoEnv()));
				LOG.info("Calypso connection <" + key
						+ "> + successfully initialized");
				System.out.println("Calypso connection <" + key
						+ "> + successfully initialized");

			} catch (ConnectException e) {
				String msg = "Error when trying to get Calypso Connection for "
						+ key + " : " + e.getMessage();
				System.err.println(msg);
				LOG.error(msg, e);
				initClientConnection(client);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			} 
		}
		return client;
	}

	private void initClientConnection(CalypsoMonitorService client) {
		CalypsoConnectionConfig config = client.getConfig();
		String key = client.getId();
		LOG.error("Calypso connection <" + key
				+ "> lost, Trying to reconnect...");
		System.err.println("Calypso connection <" + key
				+ "> lost, Trying to reconnect...");		
		int maxReconn = (attempt!=null)?attempt.getMaxAttemptNumber("calypsoMonitor"):MonitorReconnectionAttempt.DEFAULT_MONITOR_MAX_RECONNECTION;
		int reconDelay = (attempt!=null)?attempt.getMaxAttemptDelay("calypsoMonitor"):MonitorReconnectionAttempt.DEFAULT_MONITOR_RECONNECTION_DELAY;
		boolean successFullyconected = false;
		int i = 0;
		while (i < maxReconn) {
			try {
				LOG.info("Calypso Connection <" + key
						+ "> : trying to reconnect. " + i);
				System.out.println("Calypso Connection <" + key
						+ "> : trying to reconnect. " + i);
				client.initConnection(ConnectionUtil.connect(config.getUser(),
						config.getPassword(), config.getApplicationName(),
						config.getCalypsoEnv()));
				LOG.info("Calypso Connection <" + key
						+ "> : reconnect successfully");
				System.out.println("Calypso Connection <" + key
						+ "> : reconnect successfully");
				successFullyconected = true;
				break;
			} catch (ConnectException e1) {
				LOG.error(e1.getMessage());
				try {
					LOG.error("Calypso Connection <" + key
							+ "> : Can't reconnect. Sleeping ....");
					System.err.println("Calypso Connection <" + key
							+ "> : Can't reconnect. Sleeping ....");
					Thread.sleep(reconDelay);
				} catch (InterruptedException e2) {
					LOG.error(e2.getMessage(), e2);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			} 
			i++;
		}

		if (!successFullyconected) {
			client.deleteObserver(this);
			clients.remove(key);
			LOG.info("Calypso Connection <" + key + "> : Connection failed");
			System.out.println("Calypso Connection <" + key
					+ "> : Connection failed");
		}
	}

	public void update(Observable o, Object arg) {
		if (arg instanceof CalypsoMonitorService) {
			CalypsoMonitorService client = (CalypsoMonitorService) arg;
			initClientConnection(client);
		}
	}

	public static synchronized CalypsoMonitorServiceFactory getInstance() {
		if (instance == null)
			instance = new CalypsoMonitorServiceFactory();
		return instance;
	}

	public void remove(CalypsoMonitorService monitor) {
		if (monitor != null) {
			monitor.stop();
			clients.remove(monitor.getId()); 
			monitor = null;
		}
	}

	public static boolean isHoliday(ScheduledTask task, JDate date) {
		if (task.getExecuteOnHolidays() == false && !Holiday.getCurrent().isBusinessDay(date, task.getHolidays())) {
			if (task.getDateRule() != null) {
				return false;			
			}
			return true;
		}
		return false;
	}

	public static String dateToString(Date date) {
		Format format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(date);
	}

	public static Date getFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		Calendar newMonthCal = Calendar.getInstance();
		newMonthCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);

		return newMonthCal.getTime();
	}
	
}
