package com.fsi.monitoring.admin.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fsi.monitoring.admin.AdminComponent;
import com.fsi.monitoring.admin.AdminRequest;
import com.fsi.monitoring.admin.AdminRequestCommand;
import com.fsi.monitoring.admin.ComponentStatus;
import com.fsi.monitoring.admin.console.bean.IkrAdminLoggingBean;
import com.fsi.monitoring.admin.console.bean.IkrEventLogBean;
import com.fsi.monitoring.admin.console.bean.StatusFilterBean;
import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.jms.IkrJmsMessage;
import com.fsi.monitoring.jms.JmsP2PProducer;
import com.fsi.monitoring.jms.JmsMulticastProducer;
import com.fsi.monitoring.realTime.listener.AdminEventListener;
import com.fsi.monitoring.util.FacesUtils;

public class AdminConsole extends SortableList {
	private static final Logger logger = Logger.getLogger(AdminConsole.class);
	
	public static final String typeColumnName = "Type";
	public static final String envColumnName = "Environment";
	public static final String nameColumnName = "Name";
	public static final String startTimeColumnName = "Start Time";
	public static final String statusColumnName = "Status";
	
	private Map<Long, IkrEventLogBean> eventLogs;
	private List<IkrEventLogBean> listForDisplay;
	private AdminComponent componentType;
	private JmsMulticastProducer adminRequestProducer;
	private JmsP2PProducer heartBeatProducer;
	
	private boolean alive;
	
	private Map<String, Integer> statistics;
	private String statisticStr = "";
	
	private Set<ComponentStatus> filters; 
	private Map<Integer, StatusFilterBean> statusFilters;
	
	public AdminConsole(AdminComponent componentType, JmsMulticastProducer adminRequestProducer, JmsP2PProducer heartBeatProducer) {
		super(statusColumnName);
		this.componentType = componentType;
		this.adminRequestProducer = adminRequestProducer;
		this.heartBeatProducer = heartBeatProducer;
		
		eventLogs = new HashMap<Long, IkrEventLogBean>();	
		
		statusFilters = new HashMap<Integer, StatusFilterBean>();
		filters = new HashSet<ComponentStatus>();
		for (ComponentStatus status : ComponentStatus.values()) {
			filters.add(status);
			StatusFilterBean filterBean = new StatusFilterBean(status);
			filterBean.setSelected(true);
			statusFilters.put(status.getStatusLevel(), filterBean);
		}
	}
	
	@Override
	protected boolean isDefaultAscending(String sortColumn) {
		return true;
	}
	
	public List<StatusFilterBean> getStatusFilters() {
		List<StatusFilterBean> filterBeansToSort = new ArrayList<StatusFilterBean>(statusFilters.values());
		Collections.sort(filterBeansToSort, new Comparator<StatusFilterBean>() {
			public int compare(StatusFilterBean o1, StatusFilterBean o2) {
				int res = 0;
				try {
					int status1 = o1.getStatus().getStatusLevel();
		        	int status2 = o2.getStatus().getStatusLevel();
		            return status2-status1;
				}
				catch (Exception e) {}
				return res;
			}
		});			
		return filterBeansToSort;
	}	
	
	public void initConsole(List<Long> ids) throws Exception{
		try {	
//			alive = heartBeatProducer.isComponentAlive(componentType);
			alive = true;
			if (!alive) {
				AdminEventListener listener = (AdminEventListener)FacesUtils.getManagedBean("adminEventListener");
				listener.resetEventLogBeans(componentType);
			}
			else if (ids!=null&&ids.size()>0){
				List<IkrJmsMessage> requests = new ArrayList<IkrJmsMessage>();
				for (long id : ids) {
					requests.add(new AdminRequest(AdminRequestCommand.GLOBAL_STATUS, id, componentType));
				}
				adminRequestProducer.publish(requests);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
	
	public void removeFilter(ComponentStatus filter) {
		filters.remove(filter);
		StatusFilterBean bean = statusFilters.get(filter.getStatusLevel());
		bean.setSelected(false);
	}
	
	public void addFilter(ComponentStatus filter) {
		filters.add(filter);
		StatusFilterBean bean = statusFilters.get(filter.getStatusLevel());
		bean.setSelected(true);
	}
	
	@Override
	protected void sort() {
		filter();
		Collections.sort(listForDisplay, new Comparator<IkrEventLogBean>() {
			public int compare(IkrEventLogBean o1, IkrEventLogBean o2) {
				int res = 0;
				try {
					if (typeColumnName.equals(getSortColumnName())) {
						res = ascending ? o1.getCategory().toLowerCase().compareTo(o2.getCategory().toLowerCase()) :  o2.getCategory().toLowerCase().compareTo(o1.getCategory().toLowerCase());
					}
					else if (nameColumnName.equals(getSortColumnName())) {
						res = ascending ? o1.getComponentName().toLowerCase().compareTo(o2.getComponentName().toLowerCase()) :  o2.getComponentName().toLowerCase().compareTo(o1.getComponentName().toLowerCase());
					}
					else if (envColumnName.equals(getSortColumnName())) {
						res = ascending ? o1.getLogicalEnv().toLowerCase().compareTo(o2.getLogicalEnv().toLowerCase()) :  o2.getLogicalEnv().toLowerCase().compareTo(o1.getLogicalEnv().toLowerCase());
					}
					else if (startTimeColumnName.equals(getSortColumnName())) {
						res = ascending ? o1.getEventLog().getStartTime().compareTo(o2.getEventLog().getStartTime()) :  o2.getEventLog().getStartTime().compareTo(o1.getEventLog().getStartTime());
					}
					else if (statusColumnName.equals(getSortColumnName())) {
						int status1 = o1.getEventLog().getStatus().getStatusLevel();
			        	int status2 = o2.getEventLog().getStatus().getStatusLevel();
			            return ascending ? status1-status2 : status2-status1;
					}
				}
				catch (Exception e) {}
				return res;
			}
		});			
	}
	
	private void reload() {
		statistics = new HashMap<String, Integer>();
		AdminEventListener listener = (AdminEventListener)FacesUtils.getManagedBean("adminEventListener");
		Collection<IkrEventLogBean> beans = listener.getEventLogBeans(componentType);
		for (IkrEventLogBean eventLogBean : beans) {
			eventLogs.put(eventLogBean.getEventLog().getValueDefinitionId(), eventLogBean);
			Integer statValue = statistics.get(eventLogBean.getStatus());
			statistics.put(eventLogBean.getStatus(), (statValue!=null)?statValue+1:1);
		}	
		
		statisticStr = "";
		int total = eventLogs.size();
		int sz = statistics.size();
		int i = 0;
		for (String attr : statistics.keySet()) {
			statisticStr = statisticStr+attr+":"+statistics.get(attr)+"/"+total;
			if (i<sz-1)
				statisticStr = statisticStr + " | ";
			i++;
		}
		
		sort();
	}
	
	private void filter() {
		listForDisplay = new ArrayList<IkrEventLogBean>();
		for (IkrEventLogBean eventLogBean : eventLogs.values()) {
			if(filters.contains(eventLogBean.getEventLog().getStatus()))
				listForDisplay.add(eventLogBean);
		}	
	}
	
	public List<IkrEventLogBean> getEventBeans() {
		reload();			
		return listForDisplay;
	}	
	
	public IkrEventLogBean getEventLogBean(long id) {
		return eventLogs.get(id);
	}
	
	public List<IkrAdminLoggingBean> getLogBuffer() {
		List<IkrAdminLoggingBean> buffer = new ArrayList<IkrAdminLoggingBean>();
		if (eventLogs!=null&eventLogs.size()>0) {
			for (IkrEventLogBean bean : eventLogs.values()) {
				buffer.addAll(bean.getLogBeans());
			}
		}			
		return buffer;
	}
	
	public String getStatistic() {
		return statisticStr;
	}
	
	public String getStyle() {
		String style = "text-align: left; background-color: #990000; color: white; font-weight:bold;";
		if (alive)
			style = "text-align: left; background-color: green;";
	   return style;
	 }
	
	
}
