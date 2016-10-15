package com.fsi.monitoring.util;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.commons.lang.time.StopWatch;
import org.icefaces.application.PortableRenderer;
import org.icefaces.application.PushRenderer;

import com.fsi.monitoring.dashboard.component.framework.ComputableComponent;

public class IkrProgressBarController implements ComputableComponent {
	private static final int PAUSE_AMOUNT_S = 150;
	
	private IkrProgressBarBean progressBar;
	private PortableRenderer renderer;
	private StopWatch watch;
	private Updater updater;
	
	private boolean renderProgressBar = false;
	
//	private RenderManager renderManager;
//	private String sessionId;
	
	public IkrProgressBarController() {
//		this.renderManager = renderManager;
//		this.sessionId = sessionId;
	}
	
	public void initProgressBar() {
		this.progressBar = new IkrProgressBarBean();
		progressBar.setLabelPosition("embed");
		progressBar.setProgressMessageEnabled(true);
		progressBar.setCompletedMessageEnabled(true);
		progressBar.setCompletedMessage("Finished");
		progressBar.setIntermediatMode(true);		
	}
	
	public void computeComponent() {}	
	
	private class Updater extends Thread {
		public void run() {
			int i = 1;
			int progress = 0;
			progressBar.setPercentComplete(progress);
			while(progressBar.isPogressStarted()) {
				System.out.println("IkrProgressBarController -- getTime -- " + progress);
				watch.split();
				String time = watch.toSplitString();
				progressBar.setProgressMessage("Processing Data :  " + time);
				System.out.println("IkrProgressBarController -- Processing Data :  " + time);
				if (!progressBar.isIntermediatMode()) {
					progress = progress + 1;	
					progressBar.setPercentComplete(progress);
				}
				else {
					progressBar.setPercentComplete(1);
				}
				
				try {
	                Thread.sleep(PAUSE_AMOUNT_S);
	            }catch (InterruptedException failedSleep) {}
	            
//	            System.out.println("IkrProgressBarController -- Before State Render -- " + progress);
//				
				try {
					renderer.render("all");
//					renderManager.getOnDemandRenderer(sessionId).requestRender();
	           }catch (Exception failedRender) {
	        	   failedRender.printStackTrace();
	        	   progressBar.setPogressStarted(false);
	           }
//	           
//	           System.out.println("ProgressThread -- After State Render -- " + progress);
	           if (!progressBar.isIntermediatMode()) { 
		           i++;
		           
		           if(progress > 100) {
		        	   progress = 0;
		        	   progressBar.setPercentComplete(progress);
		           }
	           }
			}		
//			renderManager.getOnDemandRenderer("all").requestRender();
		}
	}
	
	public void startProgress() {
		PushRenderer.addCurrentSession("all");   
   		renderer = PushRenderer.getPortableRenderer();
		renderProgressBar = true;
		watch = null;
		watch = new StopWatch();
		watch.start();
		progressBar.setPogressStarted(true);
		
		updater = null;
		updater = new Updater();
		updater.start();
//		start = true;
	}
	
	public void stopProgress() {
//		start = false;
		progressBar.setPogressStarted(false);
		progressBar.setPercentComplete(100);
		if (watch != null) {
			watch.stop();
			watch = null;
		}
		
		updater = null;
	}

	public IkrProgressBarBean getProgressBar() {
		return progressBar;
	}

	public boolean isRenderProgressBar() {
		return renderProgressBar;
	}

	public void setRenderProgressBar(boolean renderProgressBar) {
		this.renderProgressBar = renderProgressBar;
	}	
	
}
