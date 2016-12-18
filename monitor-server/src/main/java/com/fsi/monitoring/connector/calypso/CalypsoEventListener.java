package com.fsi.monitoring.connector.calypso;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.calypso.tk.core.SerializationException;
import com.calypso.tk.event.ESStarter;
import com.calypso.tk.event.PSConnection;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventEngineRequest;
import com.calypso.tk.event.PSEventException;
import com.calypso.tk.event.PSEventLog;
import com.calypso.tk.event.PSEventMonitor;
import com.calypso.tk.event.PSEventStats;
import com.calypso.tk.event.PSException;
import com.calypso.tk.event.PSSubscriber;
import com.calypso.tk.service.ConnectionListener;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.service.RemoteReferenceData;
import com.calypso.tk.util.InstantiateUtil;
import com.calypso.tk.util.Timer;
import com.calypso.tk.util.TimerRunnable;
import com.fsi.monitoring.connector.ConnectorException;
import com.fsi.monitoring.kpi.monitor.calypso.CalypsoException;

public class CalypsoEventListener  
extends Observable 
implements PSSubscriber, ConnectionListener {

	private static final Logger LOG = Logger.getLogger(CalypsoEventListener.class);
	
	private DSConnection dsConnection;
	private PSConnection psConnection;
	private PSConnection psConnectionAppRunnings;
	private Timer _psTimer;
	private String applicationName;
	private Vector<String> subscribedEvents = new Vector<String>();
	
	private Map<String, PSEventMonitor> engineStats = new HashMap<String, PSEventMonitor>();
	private List<CalypsoException> calypsoExceptions = new ArrayList<CalypsoException>();		
	
	private final ReentrantLock lock = new ReentrantLock();
	private int globalStat = 0;
	private Map<String, Integer> exceptionTypeStats = new HashMap<String, Integer>();
	private Map<String, Integer> objectStats = new HashMap<String, Integer>();
	
	private final ReentrantLock statsLock = new ReentrantLock();
	
	protected Map<String, List<PSEvent>> eventFlows = Collections.synchronizedMap(new HashMap<String, List<PSEvent>>());
	
	
	public CalypsoEventListener(DSConnection dsConnection, String applicationName) {
		super();
		this.dsConnection = dsConnection;
		this.applicationName = applicationName;
		if (this.dsConnection != null)
			this.dsConnection.setAutoReconnect(true);
	}
	
	public boolean checkStatus() {
		if ((psConnection == null || !psConnection.isStarted()) ||
			(psConnectionAppRunnings == null || !psConnectionAppRunnings.isStarted())) {
			stop();
			return false;
		}
		
		return true;
	
	}

	public void start() {
		try {
    		psConnection = ESStarter.startConnection(dsConnection, this);
    		psConnection.start(false);
    		psConnection.setApplicationName(applicationName);
    		psConnectionAppRunnings = ESStarter.startConnection(dsConnection);
    		psConnectionAppRunnings.start(true);
    		
    		subscribeToEvents();
            
        } catch(Exception e) { 
        	LOG.error("ERROR: Unable to create connection to event server...", e);
        }	
	}
	
	public void stop() {
		try {
			unsubscribeEvents();
			psConnectionAppRunnings.stop();
			psConnectionAppRunnings = null;
			psConnection.stop();
			psConnection = null;
		} catch (PSException e) {
			LOG.error(e.getMessage(), e);
		}		
	}
	
	public Map<String, Integer> getExceptionTypeStats() {
		lock.lock();	
		Map<String, Integer> res = new HashMap<String, Integer>(exceptionTypeStats);
		exceptionTypeStats.clear();		
		lock.unlock();		 
		return res;		
		
	}
	
	public Map<String, Integer> getObjectStats() {
		lock.lock();	
		Map<String, Integer> res = new HashMap<String, Integer>(objectStats);
		objectStats.clear();		
		lock.unlock();		 
		return res;			
		
	}
	
	public int getGlobalStat() {
		lock.lock();
		int res = globalStat;
		globalStat = 0;
		lock.unlock();		
		return res;
	}
	
	private void subscribeToEvents() throws PSException {
		if (subscribedEvents.size() > 0) {
			psConnection.unsubscribe();
			subscribedEvents.clear();
		}
		
		RemoteReferenceData remote = dsConnection.getRemoteReferenceData();
		try {
			Vector<String> eventClassNames = remote.getDomainValues("eventClass");
			for (String name : eventClassNames) {
				PSEvent inst = getPSEventInstance(name);
				if (inst != null) {
					subscribedEvents.add(inst.getClass().getName());
					LOG.info("Event " + name + " subscribed");
				}
			}
			subscribedEvents.addElement(PSEventException.class.getName());
			subscribedEvents.addElement(PSEventLog.class.getName());
			subscribedEvents.addElement(PSEventMonitor.class.getName());
			subscribedEvents.addElement(PSEventStats.class.getName());
		} catch (RemoteException e) {
			LOG.error("ERROR: Unable to get PSEvent list from domain values", e);
		}
        psConnection.subscribe(subscribedEvents);
	}
	
	private void unsubscribeEvents() throws PSException {
		psConnection.unsubscribe(subscribedEvents);
	}
	
	private PSEvent getPSEventInstance(String cname) {
        String className = "tk.event." + cname;
        try {
        	return (PSEvent) InstantiateUtil.getInstance(className);
        }
        catch (Exception e) {
        	LOG.error("Error while instantiating " + cname , e);
        }
        return null;
      }


	
	   /**
     * From PSSubscriber interface
     */
    public void newEvent(PSEvent event) {
    	LOG.debug(event.getClassName() + " received");
		if (event instanceof PSEventMonitor) {			
			PSEventMonitor psEventMonitor = (PSEventMonitor) event;
			engineStats.put(psEventMonitor.getSource(), psEventMonitor);
		}    	
		else if (event instanceof PSEventException) {
			processException((PSEventException) event);
		}		
		else if (event instanceof PSEventLog) {
//			processException((PSEventException) event);
		}
		
		statsLock.lock();
		try {
			String eventName = event.getClass().getName();
			List<PSEvent> events =  eventFlows.get(eventName);
			if (events == null) {
				events = new ArrayList<PSEvent>();
				eventFlows.put(eventName, events);
			}
			events.add(event);
		} finally {
			statsLock.unlock();
		}		
    }
    
    public List<PSEvent> getEvents(String eventClass) {
		statsLock.lock();
		List<PSEvent> res = new ArrayList<PSEvent>();
		List<PSEvent> tmp = eventFlows.get(eventClass);
		if (tmp!= null && tmp.size()>0)
			res.addAll(tmp);
		statsLock.unlock();
		return res;
	}
    
    public List<PSEvent> getEvents(List<String> eventClasses) {
		statsLock.lock();
		List<PSEvent> res = new ArrayList<PSEvent>();
		for (String eventClass : eventClasses) {
			List<PSEvent> tmp = eventFlows.get(eventClass);
			if (tmp!= null && tmp.size()>0)
				res.addAll(tmp);
		}
		statsLock.unlock();
		return res;
	}
    
	private void processException(PSEventException event) {
		synchronized(calypsoExceptions) {	
			String exceptionstr = event.getException();
			String[] tmp = StringUtils.splitPreserveAllTokens(exceptionstr, '\n');
			String appName = tmp[0];
			String content = tmp[2]+ '\n' + getExceptionContent(tmp);
			CalypsoException calypsoException = 
					new CalypsoException(new Date(System.currentTimeMillis()), appName, content);
			
			calypsoExceptions.add(calypsoException);
		}
	}
	
	public List<CalypsoException> getCalypsoExceptions() {
		List<CalypsoException> res = null;
	
		synchronized(calypsoExceptions) {
			res = new ArrayList<CalypsoException>(calypsoExceptions);
			calypsoExceptions.clear();
		}
		
		return res;
	}
	
	private String getExceptionContent(String[] exceptions) {
		int i = 0;
		while(exceptions[i].indexOf("END") < 0) 
			i++;
		return exceptions[++i] + '\n' + exceptions[++i];
	}	
    
    public long[] getEventServerStats() {
    	try {
			return psConnectionAppRunnings.getEventServerStats();
		} catch (PSException e) {
			LOG.error(this, e);
		}
		return null;
    }
    
    public int getEventServerMaxQueueSize() {
    	return psConnectionAppRunnings.getMaxQueueSize();
    }
    
    public PSEventMonitor requestEngineStats(String engineName)
    throws ConnectorException {    
 //       sendEngineRequest(engineName, PSEventEngineRequest.REQUEST_CACHE_STATS);
        
        PSEventMonitor result = engineStats.get(engineName);
        return result;
    }
    
    private void sendEngineRequest(String engineName, int request) 
    throws ConnectorException {
    	if (psConnection == null) {
            throw new ConnectorException();   
    	}    
    	try {
    		PSEventEngineRequest ad = new PSEventEngineRequest();
            ad.setType(request);
            ad.setMessage(engineName);
            psConnection.publish(ad);
        } catch (SerializationException e) {
        	LOG.error(this, e);
        } catch (PSException e) {
        	LOG.error(this, e);
		}
    }
    
    public List<String> getActiveEngines() {
    	Vector<String> apps;
		try {
			apps = psConnectionAppRunnings != null?psConnectionAppRunnings.getApplicationNames():new Vector<String>();
		} catch (PSException e) {
			LOG.error(this, e);
			return new ArrayList<String>();
		}
		
		Vector<String> actives = new Vector<String>();
		for (int i = 0; i < apps.size(); i++) {
			String app = (String) apps.elementAt(i);
			if (app.indexOf("Engine") >= 0)
				actives.addElement(app);
		}
		return actives;
    }

    public void onDisconnect() {
        LOG.error("Disconnected from event server.");
        System.err.println("Disconnected from event server.");

        if(_psTimer != null) {
            return;
        }

        LOG.info("Starting Timer to reconnect to EventServer");
        System.out.println("Starting Timer to reconnect to EventServer");

        TimerRunnable r = new TimerRunnable() {
            public void timerRun() {
                if((DSConnection.getDefault() == null)
                        || DSConnection.getDefault().isClosed()) {
                	LOG.error("PSTimer Not connected to DS skipping try");
                    return;
                }

                LOG.info("Timer Trying to reconnect to EventServer");
                System.out.println("Timer Trying to reconnect to EventServer");

                final Runnable r2 = new Runnable() {
                    public void run() {
                        if(PSConnection.getCurrent() != null) {
                            try {
                                PSConnection.getCurrent().stop();
                            } catch(Exception e) {}
                        }
                        boolean v = internalPSStart();
                        if(v) {
                            stopPSTimer();
                        }
                    }
                };
                r2.run();
            }
        };
        _psTimer = new com.calypso.tk.util.Timer(r, DSConnection.getTimeoutReconnect());
        _psTimer.start();
    }

    void stopPSTimer() {
        if(_psTimer == null) {
            return;
        }
        LOG.info("Stopping Timer to reconnect to EventServer");
        com.calypso.tk.util.Timer timer = _psTimer;
        _psTimer = null;
        timer.stop();
    }

    boolean internalPSStart() {
        try {
            PSConnection ps = ESStarter.startConnection(DSConnection.getDefault(), this);
            if(ps == null) {
                return false;
            }
            PSConnection.setCurrent(ps);
            ps.setApplicationName(applicationName);
            ps.start();
            LOG.info("Reconnected to EventServer");
            System.out.println("Reconnected to EventServer");
            return true;
        } catch(Exception e) {
            return false;
        }
    }

	public void connectionClosed(DSConnection arg0) {
		System.out.println("Disconnected from DATA SERVER");		
	}

	public void reconnected(DSConnection arg0) {}

	public void tryingBackup(DSConnection arg0) {}

	public void usingBackup(DSConnection arg0) {}
 
}