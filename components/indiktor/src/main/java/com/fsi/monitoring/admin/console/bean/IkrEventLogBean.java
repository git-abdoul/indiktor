package com.fsi.monitoring.admin.console.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.IkrAdminLogging;
import com.fsi.monitoring.admin.IkrEventLog;
import com.fsi.monitoring.datamodel.bean.RealTimeBean;

public class IkrEventLogBean implements RealTimeBean {	
	private IkrEventLog eventLog;
	
	private List<IkrAdminLoggingBean> logBeans;
	private List<IkrAdminStatsBean> statsBean;

	public IkrEventLogBean() {
		super();		
	}
	
	public void setEventLog(IkrEventLog eventLog) {
		this.eventLog = eventLog;
		
		logBeans = new ArrayList<IkrAdminLoggingBean>();
		statsBean = new ArrayList<IkrAdminStatsBean>();
		
		for (IkrAdminLogging log : eventLog.getLogs()) {
			logBeans.add(new IkrAdminLoggingBean(log, eventLog.getComponentType().name(), eventLog.getName()));
		}
		
		Map<String, String> stats = eventLog.getStats();
		for (String key : stats.keySet()) {
			statsBean.add(new IkrAdminStatsBean(key, stats.get(key)));
		}
	}
	
	public IkrEventLog getEventLog() {
		return eventLog;
	}
	
	public String getLogicalEnv() {
		return eventLog.getLogicalEnv();
	}
	
	public String getComponentName() {
		return eventLog.getName();
	}
	
	public String getStartTime() {
		if (eventLog.getStartTime()==null)
			return "";
		return (new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss")).format(eventLog.getStartTime());
	}
	
	public String getStatus() {
		String status = eventLog.getStatus().getLabel();
		if ((eventLog.getComponentType()==AdminComponent.CONNECTOR)&&(eventLog.getStatus()==ComponentStatus.STARTING))
			status = "CONNECTING...";
		return status;
	}
	
	public boolean isUp() {
		boolean ret = true;
		if (eventLog.getStatus().getStatusLevel()<0)
			ret = false;
		return ret;
	}
	
	public boolean isRenderCommand() {
		return !(eventLog.getStatus()==ComponentStatus.REMOVE);
	}
	
	public String getStyle() {
		String style = "text-align: left; background-color: #990000; color: white; font-weight:bold;";
		if (eventLog.getStatus()==ComponentStatus.NOTHING_TO_REPORT)
			style = "text-align: left; background-color: green;";
		else if (eventLog.getStatus()==ComponentStatus.ERROR_OCCURED || eventLog.getStatus()==ComponentStatus.INVALID)
			style = "text-align: left; background-color: #F06161;";			
		else if (eventLog.getStatus()==ComponentStatus.STARTING) 
			style = "text-align: left; background-color: #f39402;";
		else if (eventLog.getStatus()==ComponentStatus.REMOVE)
			style = "text-align: left; background-color: #554f4f;";
	   return style;
	 }
	
	public String getCategory() {
		return eventLog.getCategory();
	}
	
	public AdminComponent getComponentType() {
		return eventLog.getComponentType();
	}

	public List<IkrAdminLoggingBean> getLogBeans() {
		if (logBeans!=null&&logBeans.size()>0) {
			Collections.sort(logBeans, new Comparator<IkrAdminLoggingBean>(){
				public int compare(IkrAdminLoggingBean o1,IkrAdminLoggingBean o2) {
					Date date1 = o1.getLog().getLogDatetime();
					Date date2 = o2.getLog().getLogDatetime();
		            return date2.compareTo(date1);
				}
			}			
			);
		}
		return logBeans;
	}

	public List<IkrAdminStatsBean> getStatsBean() {
		return (statsBean!=null)?statsBean:new ArrayList<IkrAdminStatsBean>();
	}
	
	public boolean isRenderLogDetails() {
		return (logBeans!=null)?logBeans.size()>0:true;
	}
	
	public boolean isRenderStatsDetails() {
		return (statsBean!=null)?statsBean.size()>0:true;
	}	
	
}
