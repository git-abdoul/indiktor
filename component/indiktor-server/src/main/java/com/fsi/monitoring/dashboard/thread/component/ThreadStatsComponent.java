package com.fsi.monitoring.dashboard.thread.component;

import com.fsi.monitoring.dashboard.thread.ThreadStatsBean;
import com.fsi.monitoring.indiktor.DataModelPM;
import com.fsi.monitoring.kpi.metrics.IkrValue;
import com.fsi.monitoring.kpi.units.FormattedValue;
import com.fsi.monitoring.kpi.units.RateUnit;

public abstract class ThreadStatsComponent
extends ThreadComponent<ThreadStatsBean> {
	
	protected int ikrCategoryId;
	protected int ikrCategoryRatioId;
	
	public ThreadStatsComponent(String componentId,
			 					String title,
			 					String style,
			 					boolean rendered,
			 					String processName, 
			 					String hostname,
			 					DataModelPM dataModelPM) {
		super(componentId, title, style, rendered, processName, hostname, dataModelPM);
	}	
	
	@Override
	protected ThreadStatsBean getBean(String label) {
		return new ThreadStatsBean(label);
	}
	
	@Override
	protected void updateBean(ThreadStatsBean statsBean, IkrValue ikrValue) {
		int ikrCatId = ikrValue.getIkrCategoryId();
		
		if (ikrCatId == ikrCategoryId) {
			statsBean.setValue(ikrValue.getValue());
			statsBean.setCaptureTime(ikrValue.getCaptureTime());
		} else if (ikrCatId == ikrCategoryRatioId){
			FormattedValue format = RateUnit.format(ikrValue.getValue(), null);
			statsBean.setPercentage(format.getValue());
			statsBean.setCaptureTime(ikrValue.getCaptureTime());
		}
	}
	
	protected void initIkrCategoriesIds() {
		ikrCategoryIds.add(ikrCategoryId);
		ikrCategoryIds.add(ikrCategoryRatioId);
	}	
}
