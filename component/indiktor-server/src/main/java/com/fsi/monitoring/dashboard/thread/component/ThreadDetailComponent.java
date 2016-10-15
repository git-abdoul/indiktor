package com.fsi.monitoring.dashboard.thread.component;


import org.apache.log4j.Logger;

import com.fsi.monitoring.dashboard.thread.ThreadDetailBean;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrCategory;
import com.fsi.monitoring.kpi.metrics.IkrValue;

public class ThreadDetailComponent 
extends ThreadComponent<ThreadDetailBean> {

	private static final Logger logger = Logger.getLogger(ThreadDetailComponent.class);	
	
	private int stateCategoryId;
	private int waitedTimeCategoryId;
	private int blockedTimeCategoryId;
	private int stackTraceCategoryId;

	
	public ThreadDetailComponent(String componentId,
								 String title,
								 String style,
								 boolean rendered,
								 String processName, 
								 String hostname,
								 DataModelPM dataModelPM) {
		super(componentId,title, style, rendered, processName, hostname, dataModelPM);
	}

	@Override
	protected ThreadDetailBean getBean(String label) {
		return new ThreadDetailBean(label);
	}

	@Override
	protected void updateBean(ThreadDetailBean bean, 
							  IkrValue ikrValue) {
		int categoryId = ikrValue.getIkrCategoryId();
		String value = ikrValue.getValue();
		
		if (categoryId == stateCategoryId) {
			bean.setState(value);
		} else if (categoryId == waitedTimeCategoryId) {
			bean.setWaitedTime(value);
		} else if (categoryId == blockedTimeCategoryId) {
			bean.setBlockedTime(value);
		} else if (categoryId == stackTraceCategoryId) {
			bean.setStackTrace(value);
		}
	}
	
	@Override
	protected void initIkrCategories(DataModelPM dataModelPM) {
		
		try {
//			IkrCategory ikrCategory = dataModelPM.getIkrCategory("process.thread.info.state");
//			this.stateCategoryId = ikrCategory.getId();
//			ikrCategoryIds.add(stateCategoryId);
//			
//			ikrCategory = dataModelPM.getIkrCategory("process.thread.info.waited_time");
//			this.waitedTimeCategoryId = ikrCategory.getId();
//			ikrCategoryIds.add(waitedTimeCategoryId);
//			
//			ikrCategory = dataModelPM.getIkrCategory("process.thread.info.blocked_time");
//			this.blockedTimeCategoryId = ikrCategory.getId();
//			ikrCategoryIds.add(blockedTimeCategoryId);
//			
//			ikrCategory = dataModelPM.getIkrCategory("process.thread.info.stacktrace");
//			this.stackTraceCategoryId = ikrCategory.getId();
//			ikrCategoryIds.add(stackTraceCategoryId);
		} catch (Exception exc) {
			logger.error(exc);
		}
	}	
}
