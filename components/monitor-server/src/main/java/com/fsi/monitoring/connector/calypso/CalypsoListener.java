package com.fsi.monitoring.connector.calypso;

import java.util.concurrent.locks.ReentrantLock;

import com.calypso.tk.event.PSEvent;

public interface CalypsoListener {
	public final ReentrantLock statsLock = new ReentrantLock();
	
	public void onEventReceived(PSEvent event);
	
}
