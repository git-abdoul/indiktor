package com.fsi.monitoring.dashboard.config;


import generated.dashboard.config.schema.DashBoard;
import generated.dashboard.config.schema.DashBoardType.ThreadType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.DetailType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.MethodType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.StackTraceType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.StatusType;
import generated.dashboard.config.schema.DashBoardType.ThreadType.TypeType;
import generated.dashboard.config.schema.ObjectFactory;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.fsi.monitoring.realTime.subscriber.RealTimeComponentFactory;
import com.fsi.monitoring.util.FacesUtils;

public class ThreadConfigBean
extends DashBoardConfigBean {

	private static final long serialVersionUID = 2767684910418822131L;

	private static final Logger logger = Logger.getLogger(ThreadConfigBean.class);	
	
	private String env;
	private String type;
	private String title;
	
	private ThreadType threadType;
	
	private boolean errorThreadTitleVisible = false;
	
	private SelectItem[] renderedItems;
	
	{
		renderedItems = new SelectItem[2];
		renderedItems[0] = new SelectItem(Boolean.valueOf("true"), "YES");
		renderedItems[1] = new SelectItem(Boolean.valueOf("false"), "NO");
	}
	

	public void init(String env,
					 String type,
					 String title) {
		try {
			try {
				this.env = env;
				this.type = type;
				this.title = title;
				
				ComponentXmlManager componentXmlManager = (ComponentXmlManager)FacesUtils.getManagedBean("xmlComponentManager");
				
				DashBoard dashBoard = componentXmlManager.getDashBoardSchema(env, type, title);
				
				threadType = dashBoard.getThread();
				
				if (threadType == null) {			
					ObjectFactory objFactory = new ObjectFactory();
					threadType = objFactory.createDashBoardTypeThreadType();
					
					DetailType detailType = objFactory.createDashBoardTypeThreadTypeDetailType();
					threadType.setDetail(detailType);
					
					MethodType methodType = objFactory.createDashBoardTypeThreadTypeMethodType();
					threadType.setMethod(methodType);
					
					StatusType statusType = objFactory.createDashBoardTypeThreadTypeStatusType();
					threadType.setStatus(statusType);
					
					TypeType typeType = objFactory.createDashBoardTypeThreadTypeTypeType();
					threadType.setType(typeType);
					
					StackTraceType stType = objFactory.createDashBoardTypeThreadTypeStackTraceType();
					threadType.setStackTrace(stType);
				}
			} catch (Exception exc) {
				logger.error(exc);
			}

		} catch (Exception exc) {
			logger.error(exc);
		}			
	}

	public ThreadType getThreadType() {
		return threadType;
	}
	
	public boolean isErrorThreadTitleVisible() {
		return errorThreadTitleVisible;
	}

	public void setErrorThreadTitleVisible(boolean errorThreadTitleVisible) {
		this.errorThreadTitleVisible = errorThreadTitleVisible;
	}

	public SelectItem[] getRenderedItems() {
		return renderedItems;
	}
		
	public void save() {
		if (threadType.getTitle() == null || threadType.getTitle().length() == 0) {
			errorThreadTitleVisible = true;
			return;	
		}			
		errorThreadTitleVisible = false;		
		RealTimeComponentFactory factory = RealTimeComponentFactory.getFactory();	
		factory.updateThreadComponent(env, type, title, threadType);
		
		DashboardMainConfigBean bean = (DashboardMainConfigBean)FacesUtils.getManagedBean("dashboardMainConfigBean");
		bean.resetSelectedDashBoard();
	}	

}
