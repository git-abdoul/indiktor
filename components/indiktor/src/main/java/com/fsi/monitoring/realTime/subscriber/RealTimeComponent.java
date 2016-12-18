package com.fsi.monitoring.realTime.subscriber;

import com.fsi.monitoring.datamodel.bean.RealTimeBean;

public interface RealTimeComponent {

	void push(RealTimeBean valueBean);	
}
