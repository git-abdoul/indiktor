package com.fsi.monitoring.dashboard.thread.component;

import org.apache.log4j.Logger;

import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;


public class ThreadStatusComponent 
extends ThreadStatsComponent {


	private static final long serialVersionUID = -3071839853611683781L;
	
	private static final Logger logger = Logger.getLogger(ThreadStatusComponent.class);	

	
	public ThreadStatusComponent(String componentId,
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
//			IkrCategory ikrCategory = dataModelPM.getIkrCategory("process.thread.status.count");
//			this.ikrCategoryId = ikrCategory.getId();
//			
//			ikrCategory = dataModelPM.getIkrCategory("process.thread.status.ratio");
//			this.ikrCategoryRatioId = ikrCategory.getId();
			
			initIkrCategoriesIds();
		
		} catch (Exception exc) {
			logger.error(exc);
		}
	}
}
