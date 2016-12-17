package com.fsi.monitoring.dashboard.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import com.fsi.monitoring.component.table.SortableList;
import com.fsi.monitoring.dashboard.component.framework.DashBoardComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadDetailComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadMethodComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadStatusComponent;
import com.fsi.monitoring.dashboard.thread.component.ThreadTypeComponent;

public class ThreadAnalysisBean 
extends DashBoardComponent {
	
	private static final long serialVersionUID = 1446262541934553805L;

	private static final Logger logger = Logger.getLogger(ThreadAnalysisBean.class);
		
	private ThreadDetailStorded details;
	private ThreadStatsStorded statuses;
	private ThreadStatsStorded methods;
	private ThreadStatsStorded types;
	
	private ThreadDetailBean selectedThreadDetail;
	
	private String threadComponentTitle;
	
	public ThreadAnalysisBean(String componentId,
							  String stackTraceTitle,
							  String stackTraceStyle,
							  boolean rendered,
							  String threadComponentTitle,
							  ThreadDetailComponent threadDetailComponent,
							  ThreadMethodComponent threadMethodComponent,
							  ThreadStatusComponent threadStatusComponent,
							  ThreadTypeComponent threadTypeComponent) {	
		super(componentId, stackTraceTitle, stackTraceStyle, "thread", rendered);
		
		details = new ThreadDetailStorded(threadDetailComponent);
		statuses = new ThreadStatsStorded(threadStatusComponent);
		methods = new ThreadStatsStorded(threadMethodComponent);
		types = new ThreadStatsStorded(threadTypeComponent);
		
		this.threadComponentTitle = threadComponentTitle;
	}	
	
	@Override
	public void synchronize() {
		// TODO Auto-generated method stub
		
	}
	
	public String getThreadComponentTitle() {
		return threadComponentTitle;
	}
	
	public ThreadDetailComponent getThreadDetailComponent() {
		return (ThreadDetailComponent)details.getThreadComponent();
	}
	
	public ThreadMethodComponent getThreadMethodComponent() {
		return (ThreadMethodComponent)methods.getThreadComponent();
	}
	
	public ThreadStatusComponent getThreadStatusComponent() {
		return (ThreadStatusComponent)statuses.getThreadComponent();
	}

	public ThreadTypeComponent getThreadTypeComponent() {
		return (ThreadTypeComponent)types.getThreadComponent();
	}

	public ThreadDetailBean getSelectedThreadDetails() {
		return selectedThreadDetail;
	}
	
	public void rowSelectionListener(ActionEvent event) {
		selectedThreadDetail = (ThreadDetailBean)event.getComponent().getAttributes().get("details");
    }

	public ThreadDetailStorded getDetails() {
		return details;
	}

	public ThreadStatsStorded getStatuses() {
		return statuses;
	}

	public ThreadStatsStorded getMethods() {
		return methods;
	}

	public ThreadStatsStorded getTypes() {
		return types;
	}
	
	public class ThreadStatsStorded 
	extends ThreadSorted<ThreadStatsBean> {

		private static final String nameColumnName = "Name";
	    private static final String nbColumnName = "NB";
	    private static final String percentColumnName = "%";		

	    public ThreadStatsStorded(ThreadComponent<ThreadStatsBean> threadComponent) {
			super(nameColumnName, threadComponent);			
			comparator = new StatsComparator();
		}	    
	    
		private class StatsComparator implements Comparator<ThreadStatsBean> {
		    public int compare(ThreadStatsBean o1, ThreadStatsBean o2) {
		        if (sortColumnName == null) {
		            return 0;
		        }
		        if (sortColumnName.equals(nameColumnName)) {
		        	String name1 = o1.getStatus();
		        	String name2 = o2.getStatus();
		            return ascending ? name1.compareTo(name2) : name2.compareTo(name1);
		        } else if (sortColumnName.equals(nbColumnName)) {
		        	Double nb1 = Double.parseDouble(o1.getValue());
		        	Double nb2 = Double.parseDouble(o2.getValue());
		            return ascending ? nb1.compareTo(nb2) : nb2.compareTo(nb1);
		        } else if (sortColumnName.equals(percentColumnName)) {			        	
		        	Double d1 = Double.parseDouble(o1.getPercentage());
		        	Double d2 = Double.parseDouble(o2.getPercentage());
		        	return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
		        } else return 0;
		    }
		};
		
		public String getNameColumnName() {
			return nameColumnName;
		}

		public String getNbColumnName() {
			return nbColumnName;
		}

		public String getPercentColumnName() {
			return percentColumnName;
		}
	}	

	public class ThreadDetailStorded 
	extends ThreadSorted<ThreadDetailBean> {

		private static final String nameColumnName = "Name";
	    private static final String stateColumnName = "State";
	    private static final String blockedTimeColumnName = "Blocked Time";
	    private static final String waitedTimeColumnName = "Waited Time";
		
	    public ThreadDetailStorded(ThreadComponent<ThreadDetailBean> threadComponent) {
			super(nameColumnName, threadComponent);			
			comparator = new DetailComparator();
		}	    
	    
		private class DetailComparator implements Comparator<ThreadDetailBean> {
		    public int compare(ThreadDetailBean o1, ThreadDetailBean o2) {
		        if (sortColumnName == null) {
		            return 0;
		        }
		        if (sortColumnName.equals(nameColumnName)) {
		        	String name1 = o1.getThreadName();
		        	String name2 = o2.getThreadName();
		            return ascending ? name1.compareTo(name2) : name2.compareTo(name1);
		        } else if (sortColumnName.equals(stateColumnName)) {
		        	String state1 = o1.getState();
		        	String state2 = o2.getState();
		            return ascending ? state1.compareTo(state2) : state2.compareTo(state1);
		        } else if (sortColumnName.equals(blockedTimeColumnName)) {			        	
		        	Long d1 = Long.parseLong(o1.getBlockedTime());
		        	Long d2 = Long.parseLong(o2.getBlockedTime());
		        	return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
		        } else if (sortColumnName.equals(waitedTimeColumnName)) {
		        	Long d1 = Long.parseLong(o1.getWaitedTime());
		        	Long d2 = Long.parseLong(o2.getWaitedTime());
		        	return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
		        } else return 0;
		    }
		};		
		
		public String getNameColumnName() {
			return nameColumnName;
		}

		public String getStateColumnName() {
			return stateColumnName;
		}

		public String getBlockedTimeColumnName() {
			return blockedTimeColumnName;
		}

		public String getWaitedTimeColumnName() {
			return waitedTimeColumnName;
		}
	}
	
	public class ThreadSorted<T> extends SortableList {

	    private ThreadComponent<T> threadComponent = null;
	    
		public ThreadSorted(String nameColumnName,
							ThreadComponent<T> threadComponent) {
			super(nameColumnName);
			this.threadComponent = threadComponent;
		}

		@Override
		protected boolean isDefaultAscending(String sortColumn) {
			return true;
		}

		@Override
		protected void sort() {
			System.out.println("SORT");
		}
		
		public ThreadComponent<T> getThreadComponent() {
			return threadComponent;
		}
		
		public List<T> getBeans() {
			Collection<T> beans = threadComponent.getBeans();
			
			List<T> displayBeans = null;
			
			if (beans.size() > 0) {
				displayBeans = new ArrayList<T>(beans);
				Collections.sort(displayBeans, comparator);
			}
			
			return displayBeans;
		}
	}
	
//	public class ThreadStatsSorted extends SortableList {
//		private String nameColumnName;
//	    private static final String nbColumnName = "NB";
//	    private static final String percentColumnName = "%";
//	    
//	    private List<ThreadStatsBean> stats;
//	    
//	    public ThreadStatsSorted(String type) {			
//			super(nbColumnName);
//			nameColumnName = type;
//		}
//
//		@Override
//		protected boolean isDefaultAscending(String sortColumn) {
//			return false;
//		}
//
//	
//		protected void sort() {
//			Comparator<ThreadStatsBean> comparator = new Comparator<ThreadStatsBean>() {
//			    public int compare(ThreadStatsBean o1, ThreadStatsBean o2) {
//			        if (sortColumnName == null) {
//			            return 0;
//			        }
//			        if (sortColumnName.equals(nameColumnName)) {
//			        	String name1 = o1.getStatus();
//			        	String name2 = o2.getStatus();
//			            return ascending ? name1.compareTo(name2) : name2.compareTo(name1);
//			        } else if (sortColumnName.equals(nbColumnName)) {
//			        	Double nb1 = Double.parseDouble(o1.getValue());
//			        	Double nb2 = Double.parseDouble(o2.getValue());
//			            return ascending ? nb1.compareTo(nb2) : nb2.compareTo(nb1);
//			        } else if (sortColumnName.equals(percentColumnName)) {			        	
//			        	Double d1 = Double.parseDouble(o1.getPercentage());
//			        	Double d2 = Double.parseDouble(o2.getPercentage());
//			        	return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
//			        } else return 0;
//			    }
//			};
//			
//			if (stats != null && stats.size() > 1)
//				Collections.sort(stats, comparator);			
//		}
//		
//		public void sortByName(ActionEvent action) {			
//			this.setSortColumnName(FacesUtils.getRequestParameter("columnName"));
//			if (!oldSort.equals(sortColumnName))
//				setAscending(true);
//			else {
//				setAscending(!ascending);
//			}
//		}
//		
//		protected List<ThreadStatsBean> getSorted(List<ThreadStatsBean> list) {
//			this.stats = list;
//			sort();
//			return stats;
//		}
//
//		public String getNameColumnName() {
//			return nameColumnName;
//		}
//
//		public String getNbColumnName() {
//			return nbColumnName;
//		}
//
//		public String getPercentColumnName() {
//			return percentColumnName;
//		}		
//	}	
	
	public void threadSelectionListener(ValueChangeEvent event) {}
	
	public void buildChart(ActionEvent event) {		
//		String type = (String) event.getComponent().getAttributes().get("chart_type");
//		if (THREAD_METHOD.equalsIgnoreCase(type)) {
//			ThreadMethodBeanSubscriber chartMethodSubscriber = (ThreadMethodBeanSubscriber) charts.get(THREAD_METHOD);
//			for(ThreadStatsBean bean : threadMethods) {
//				if (bean.isSelected())
//					chartMethodSubscriber.addChartSubscriber(bean.getStatus());
//			}	
//			chartMethodSubscriber.renewChartSource();
////			registerRealTimeSubscriber(chartMethodSubscriber);
////			updateRealTimeSubscriber(chartMethodSubscriber);
//		} else if (THREAD_STATUS.equalsIgnoreCase(type)) {
//			ThreadStatusBeanSubscriber chartStatusSubscriber = (ThreadStatusBeanSubscriber) charts.get(THREAD_STATUS);
//			for(ThreadStatsBean bean : threadStatus) {
//				if (bean.isSelected())
//					chartStatusSubscriber.addChartSubscriber(bean.getStatus());
//			}	
//			chartStatusSubscriber.renewChartSource();
////			registerRealTimeSubscriber(chartStatusSubscriber);
////			updateRealTimeSubscriber(chartStatusSubscriber);
//		} else if (THREAD_TYPE.equalsIgnoreCase(type)) {
//			ThreadTypeBeanSubscriber chartTypeSubscriber = (ThreadTypeBeanSubscriber) charts.get(THREAD_TYPE);
//			for(ThreadStatsBean bean : threadTypes) {
//				if (bean.isSelected())
//					chartTypeSubscriber.addChartSubscriber(bean.getStatus());
//			}	
//			chartTypeSubscriber.renewChartSource();
////			registerRealTimeSubscriber(chartTypeSubscriber);
////			updateRealTimeSubscriber(chartTypeSubscriber);
//		}
	}
	
	public byte[] getStatusChart() {
		return null;
//		return ((ThreadStatusBeanSubscriber)charts.get(THREAD_STATUS)).getChart();
	}
	
	public byte[] getMethodChart() {
		return null;
		//return ((ThreadMethodBeanSubscriber)charts.get(THREAD_METHOD)).getChart();
	}
	
	public byte[] getTypeChart() {
		return null;
		//return ((ThreadTypeBeanSubscriber)charts.get(THREAD_TYPE)).getChart();
	}		
}
