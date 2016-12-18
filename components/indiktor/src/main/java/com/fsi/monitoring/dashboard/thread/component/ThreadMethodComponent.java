package com.fsi.monitoring.dashboard.thread.component;

import org.apache.log4j.Logger;

import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;


public class ThreadMethodComponent 
extends ThreadStatsComponent {

	private static final long serialVersionUID = -8900723790371103063L;

	private static final Logger logger = Logger.getLogger(ThreadMethodComponent.class);	
	
	public ThreadMethodComponent(String componentId,
								 String title,
								 String style,
								 boolean rendered,
								 String processName, 
								 String hostname,
								 DataModelPM dataModelPM) {
		super(componentId, title, style, rendered, processName, hostname, dataModelPM);
	}	
	
	@Override
	protected void initIkrCategories(DataModelPM dataModelPM) {
		
		try {
//			IkrCategory ikrCategory = dataModelPM.getIkrCategory("process.thread.method.count");
//			this.ikrCategoryId = ikrCategory.getId();
//			
//			ikrCategory = dataModelPM.getIkrCategory("process.thread.method.ratio");
//			this.ikrCategoryRatioId = ikrCategory.getId();
			
			initIkrCategoriesIds();
		
		} catch (Exception exc) {
			logger.error(exc);
		}
	}	
}
