package com.fsi.monitoring.realTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.icefaces.application.PortableRenderer;
import org.icefaces.application.PushRenderer;

import com.fsi.monitoring.dashboard.component.framework.ComputableComponent;
import com.fsi.monitoring.realTime.listener.RealtimeValueListener;

public final class RealTimeRender 
extends Observable {
	
	private final static Logger logger = Logger.getLogger(RealTimeRender.class);

	private PortableRenderer renderer = null;
//	private RenderManager renderManager;	
	private ScheduledThreadPoolExecutor schedulerPool;
	private int interval;
	
	private Collection<ComputableComponent> computableComponents;
	
	public RealTimeRender() {
		computableComponents = new ArrayList<ComputableComponent>();
	}
	
	public void init() {
		IntervalSessionRender sessionRender = new IntervalSessionRender();
		schedulerPool = new ScheduledThreadPoolExecutor(5);
		schedulerPool.scheduleWithFixedDelay(sessionRender, 0, interval, TimeUnit.SECONDS);
	}
	
//	public void setRenderManager(RenderManager renderManager) {
//		this.renderManager = renderManager;
//	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void subscribeComputableComponent(ComputableComponent component) {
		synchronized (computableComponents) {
			computableComponents.add(component);
		}
	}
	
	public void unSubscribeComputableComponent(ComputableComponent component) {
		synchronized (computableComponents) {
			computableComponents.remove(component);
		}
	}
	
	public void setListeners(List<RealtimeValueListener> listeners) {
		for (RealtimeValueListener listener : listeners) {
			addObserver(listener);
		}
	}
	
	public void dispose() {}
	
	private void notifyComputableComponent() {		
		synchronized (computableComponents) {
			for (Iterator<ComputableComponent> it = computableComponents.iterator(); it.hasNext(); ) {
				ComputableComponent computableComponent = it.next();
				if (computableComponent != null) {
					computableComponent.computeComponent();
				}
			}
		}		
	}
	
	private class IntervalSessionRender 
	implements Runnable {
		public void run() {
			try {			
				PortableRenderer renderer = getRenderer();
				if (renderer != null) {
					setChanged();
					notifyObservers();
					notifyComputableComponent();
					renderer.render("all");
				}
			} catch (Throwable t) {
				logger.error("Error while rendering request",t);
			}
		}
		
		private PortableRenderer getRenderer() {
			if (renderer == null) {
				try {
					renderer = PushRenderer.getPortableRenderer();	
				} catch (Throwable t) {
					t.printStackTrace();
					logger.error("Renderer not initialized");
				}
			}
			return renderer;
		}		
//		public void run() {
//			try {			
//				OnDemandRenderer renderer = getRenderer();
//				if (renderer != null) {
//					setChanged();
//					notifyObservers();
//					notifyComputableComponent();
//					renderer.requestRender();
//				}
//			} catch (Throwable t) {
//				logger.error("Error while rendering request",t);
//			}
//		}
		
//		private OnDemandRenderer getRenderer() {
//			if (renderer == null) {
//				try {
//				 renderer = renderManager.getOnDemandRenderer("all");		
//				} catch (Throwable t) {
//					logger.error("Renderer not initialized");
//				}
//			}
//			return renderer;
//		}		
	}
}
